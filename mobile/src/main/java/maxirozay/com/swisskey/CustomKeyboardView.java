package maxirozay.com.swisskey;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;


/**
 * Created by Maxime Rossier on 14.08.2015.
 */
public class CustomKeyboardView extends LinearLayout{
    private KeyboardView keyboardView;
    private Button backToAlphabet;

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
        keyboardView = (KeyboardView) findViewById(R.id.keyboard);
        backToAlphabet = (Button) findViewById(R.id.back_to_alphabet);
    }

    public KeyboardView getKeyboardView(){
        return keyboardView;
    }

    public Button getBackToAlphabet(){
        return backToAlphabet;
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
        findViewById(R.id.scrollView).scrollTo(0, 0);
    }

    public void buildEmojiLayout(){
        GridLayout gridLayout = (GridLayout) findViewById(R.id.emojiGrid);
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(6);
        gridLayout.setRowCount(6);
        for (int i=0x1F601; i < 0x1F650; i++) {
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(1,1,1,1);
            params.width = (int) ((getContext().getResources().getDisplayMetrics().
                    widthPixels-4*getContext().getResources().getDisplayMetrics().density) / 6);

            Button button = new Button(getContext());
            button.setText(String.valueOf(Character.toChars(i)));
            button.setBackground(getContext().getDrawable(R.drawable.key));
            gridLayout.addView(button, params);
        }
    }


}
