package maxirozay.com.swisskey;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Set;


/**
 * Created by Maxime Rossier on 14.08.2015.
 */
public class CustomKeyboardView extends LinearLayout{
    private KeyboardView keyboardView;
    private Button[] buttons = new Button[6];
    private ImageButton delButton;
    private GridLayout[] emojiGrid = new GridLayout[5];
    private int[] lastEmojis = new int[36];
    private int[] humanEmojis = {0x270C, 0x270B, 0x270A, 0x1F440, 0x1F442, 0x1F443, 0x1F444,
            0x1F445, 0x1F446, 0x1F447, 0x1F448, 0x1F449, 0x1F44A, 0x1F44B};
    private int[] natureEmojis = {0x1F30F, 0x1F311, 0x1F313, 0x1F314, 0x1F315, 0x1F319, 0x1F31B,
            0x1F31F, 0x1F320, 0x1F334, 0x1F335, 0x1F339, 0x1F33B, 0x1F33D};
    private int widthEmoji;
    private OnClickListener onClickListener;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomKeyboardView(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public void onFinishInflate(){
        super.onFinishInflate();
        for (int i = 0; i < lastEmojis.length; i++)
            lastEmojis[i] = 0;
        keyboardView = (KeyboardView) findViewById(R.id.keyboard);
        buttons[0] = (Button) findViewById(R.id.last_emoji);
        buttons[0].setText(String.valueOf(Character.toChars(0x1F550)));
        buttons[1] = (Button) findViewById(R.id.emoticons);
        buttons[1].setText(String.valueOf(Character.toChars(0x1F60A)));
        buttons[2] = (Button) findViewById(R.id.human);
        buttons[2].setText(String.valueOf(Character.toChars(0x270C)));
        buttons[3] = (Button) findViewById(R.id.nature);
        buttons[3].setText(String.valueOf(Character.toChars(0x1F30F)));
        buttons[4] = (Button) findViewById(R.id.transport);
        buttons[4].setText(String.valueOf(Character.toChars(0x1F697)));
        buttons[5] = (Button) findViewById(R.id.back_to_alphabet);
        delButton = (ImageButton) findViewById(R.id.del);
    }

    public KeyboardView getKeyboardView() {
        return keyboardView;
    }

    public Button[] getButtons() {
        return buttons;
    }

    public ImageButton getDelButton() {
        return delButton;
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public void showEmoji(){
        findViewById(R.id.emojiView).setVisibility(View.VISIBLE);
        findViewById(R.id.keyboardScrollView).setVisibility(View.GONE);
    }

    public void showKeyboard(){
        findViewById(R.id.emojiView).setVisibility(View.GONE);
        findViewById(R.id.keyboardScrollView).setVisibility(View.VISIBLE);
    }

    public void scrollUp(){
        findViewById(R.id.keyboardScrollView).scrollTo(0, 0);
    }

    protected void initEmojiGrid(){
        Display display = ((WindowManager) getContext().getSystemService(
                getContext().WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        int col;
        if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
            widthEmoji = (int) ((getContext().getResources().getDisplayMetrics().
                    widthPixels - 8 * getContext().getResources().
                    getDisplayMetrics().density) / 12);
            col = 12;
        }
        else {
            widthEmoji = (int) ((getContext().getResources().getDisplayMetrics().
                    widthPixels - 4 * getContext().getResources().
                    getDisplayMetrics().density) / 6);
            col = 6;
        }
        for (int i = 0; i < 5; i++) {
            emojiGrid[i] = new GridLayout(getContext());
            emojiGrid[i].setColumnCount(col);
        }
        lastEmojis[0] = 0x1F600;
        addEmojiList(emojiGrid[0], lastEmojis);
        addEmojiButton(emojiGrid[1], 0x263A);
        addEmojiList(emojiGrid[1], 0x1F600, 0x1F640);
        addEmojiList(emojiGrid[1], 0x1F645, 0x1F64F);
        addEmojiList(emojiGrid[2], humanEmojis);
        addEmojiList(emojiGrid[3], natureEmojis);
        addEmojiList(emojiGrid[4], 0x1F680, 0x1F6C0);
    }

    //add a sequence of emoji/unicode between two unicode value
    private void addEmojiList(GridLayout gridLayout, int from, int to){
        for (int i=from; i < to; i++) {
            addEmojiButton(gridLayout, i);
        }
    }

    //add a list of emoji to a GridLayout
    private void addEmojiList(GridLayout gridLayout, int[] unicodes){
        for (int i=0; i < unicodes.length; i++) {
            addEmojiButton(gridLayout, unicodes[i]);
        }
    }

    //add an emojiButton to a GridLayout
    private void addEmojiButton(GridLayout gridLayout, int unicode){
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(1,1,1,1);
        params.width = widthEmoji;
        Button emoji = new Button(getContext());
        emoji.setTextSize(22f);
        emoji.setText(String.valueOf(Character.toChars(unicode)));
        emoji.setBackground(getContext().getDrawable(R.drawable.blackkey));
        emoji.setOnClickListener(onClickListener);
        gridLayout.addView(emoji, params);
    }

    protected void updateRecentEmojis(Set<String> recentEmojis) {
        int i = 0;
        for (String emoji : recentEmojis) {
            ((TextView) emojiGrid[0].getChildAt(i)).setText(emoji);
            i++;
        }
    }

    //set the emoji gridlayout
    protected void setEmojiGrid(int id) {
        ScrollView scrollView = (ScrollView)findViewById(R.id.emojiGrid);
        scrollView.removeAllViews();
        scrollView.scrollTo(0,0);
        switch (id) {
            case R.id.emoticons:
                scrollView.addView(emojiGrid[1]);
                break;
            case R.id.human:
                scrollView.addView(emojiGrid[2]);
                break;
            case R.id.nature:
                scrollView.addView(emojiGrid[3]);
                break;
            case R.id.transport:
                scrollView.addView(emojiGrid[4]);
                break;
            default:
                scrollView.addView(emojiGrid[0]);
        }
    }
}
