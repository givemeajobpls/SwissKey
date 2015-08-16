package maxirozay.com.swisskey;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maxime Rossier on 13.08.2015.
 */
public class InputMethod extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, View.OnClickListener {

    private CustomKeyboardView kv;
    private Keyboard keyboard;
    private final int PREDICTION1 = -101,PREDICTION2 = -102,PREDICTION3 = -103,
            CAPSLOCK = -104,
            ALPHABETKEYBOARD = -105, ALTKEYBOARD = -106, EMOJIKEYBOARD = -107;
    private int keyboardType = ALPHABETKEYBOARD;

    private boolean caps = false, capsLock = false;
    private Set<String> recentEmojis = new HashSet<>();

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
            case InputType.TYPE_CLASS_PHONE:
                keyboard = new Keyboard(this, R.xml.number);
                break;
            case InputType.TYPE_TEXT_VARIATION_URI:
            case InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT:
            case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
            case InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT:
            case InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS:
            case InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS:
            case InputType.TYPE_CLASS_TEXT:
            default:
                keyboard = new Keyboard(this, R.xml.qwertz);
        }
        if (kv != null)
            kv.getKeyboardView().setKeyboard(keyboard);
    }

    @Override
    public View onCreateInputView() {
        kv = (CustomKeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        kv.getKeyboardView().setKeyboard(keyboard);
        kv.getKeyboardView().setOnKeyboardActionListener(this);
        kv.setOnClickListener(this);
        for (int i = 0; i < kv.getButtons().length; i++)
            kv.getButtons()[i].setOnClickListener(this);
        kv.getDelButton().setOnClickListener(this);
        kv.initEmojiGrid();
        getSharedPref();
        return kv;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setSharedPref();
    }

    public void setSharedPref() {
        SharedPreferences.Editor editor = getSharedPreferences("SwissKey", MODE_PRIVATE).edit();
        editor.putStringSet("recentEmoji", recentEmojis);
        editor.commit();
    }
    public void getSharedPref() {
        SharedPreferences preferences = getSharedPreferences("SwissKey", MODE_PRIVATE);
        recentEmojis = preferences.getStringSet("recentEmoji", new HashSet<String>());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        String last30Char;
        String[] lastWords;
        switch(primaryCode) {
            case Keyboard.KEYCODE_SHIFT:
                capsLock = false;
                keyboard.getKeys().get(33).icon = getDrawable(R.drawable.sym_keyboard_shift);
                caps = !caps;
                keyboard.setShifted(caps);
                kv.getKeyboardView().invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case CAPSLOCK:
                keyboard.getKeys().get(33).icon = getDrawable(R.drawable.sym_keyboard_shift_locked);
                capsLock = true;
                break;
            case PREDICTION1:
                ic.commitText(this.keyboard.getKeys().get(0).label, 1);
                break;
            case PREDICTION2:
                ic.commitText(this.keyboard.getKeys().get(1).label, 1);
                break;
            case PREDICTION3:
                ic.commitText(this.keyboard.getKeys().get(2).label, 1);
                break;
            case ALPHABETKEYBOARD:
                changeKeyboard(ALPHABETKEYBOARD);
                break;
            case ALTKEYBOARD:
                changeKeyboard(ALTKEYBOARD);
                break;
            case EMOJIKEYBOARD:
                changeKeyboard(EMOJIKEYBOARD);
                break;
            default:
                if (keyboardType != EMOJIKEYBOARD) {
                    if (primaryCode == Keyboard.KEYCODE_DELETE){
                        char last2Char = ic.getTextBeforeCursor(2,
                                InputConnection.GET_TEXT_WITH_STYLES).charAt(0);
                        if ((int)last2Char < 0xD83D)    //0xD83D is for 0x1F (beginning of an unicode for emoji)
                            ic.deleteSurroundingText(1, 0);
                        else
                            ic.deleteSurroundingText(2, 0);
                    }
                    else {
                        char code = (char) primaryCode;
                        if (Character.isLetter(code) && caps) {
                            code = Character.toUpperCase(code);
                        }
                        ic.commitText(String.valueOf(code), 1);
                    }
                    if (!capsLock) {
                        caps = false;
                        keyboard.setShifted(caps);
                    }

                    //update predictions
                    if (keyboardType == ALPHABETKEYBOARD) {
                        last30Char = ic.getTextBeforeCursor(30,
                                InputConnection.GET_TEXT_WITH_STYLES).toString();
                        lastWords = last30Char.split(" ");
                        if (lastWords.length == 0)
                            setPrediction(" ", false);
                        else {
                            Boolean isFocused = !last30Char.endsWith(" ");  //is focused on the word?
                            setPrediction(lastWords[lastWords.length - 1], isFocused);
                        }
                    }

                }
                else {
                    ic.commitText(keyboard.getKeys().get(0).label.toString(), 1);
                }
        }

    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }

    private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_to_alphabet)
            changeKeyboard(ALPHABETKEYBOARD);
        else if (view.getId() == R.id.del) {
            InputConnection ic = getCurrentInputConnection();
            char last2Char = ic.getTextBeforeCursor(2,
                    InputConnection.GET_TEXT_WITH_STYLES).charAt(0);
            if ((int)last2Char < 0xD83D)    //0xD83D is for 0x1F (beginning of an unicode for emoji)
                ic.deleteSurroundingText(1, 0);
            else
                ic.deleteSurroundingText(2, 0);
        }
        else if (view.getTag() == null && !((Button) view).getText().equals(null)){
            InputConnection ic = getCurrentInputConnection();
            String emoji = ((Button) view).getText().toString();
            ic.commitText(emoji, 1);

            //if (!recentEmojis.contains(emoji)) {
                recentEmojis.add(emoji);
                if (recentEmojis.size() > 36)
                    recentEmojis.remove(36);
                kv.updateRecentEmojis(recentEmojis);
            //}

        }
        else if (view.getTag().equals("menu_emoji")){
            kv.setEmojiGrid(view.getId());
        }

    }

    public void changeKeyboard(int type) {
        if (keyboardType == EMOJIKEYBOARD) {
            kv.showKeyboard();
        }
        switch (type) {
            case ALTKEYBOARD:
                keyboard = new Keyboard(this, R.xml.alt_keyboard);
                kv.scrollUp();
                break;
            case EMOJIKEYBOARD:
                kv.setEmojiGrid(R.id.last_emoji);
                kv.showEmoji();
                break;
            case ALPHABETKEYBOARD:
            default:
                keyboard = new Keyboard(this, R.xml.qwertz);
                kv.scrollUp();
        }
        keyboardType = type;
        kv.getKeyboardView().setKeyboard(keyboard);
    }

    private void setPrediction(String word, Boolean isFocused) {
        if (isFocused) {
            keyboard.getKeys().get(2).label = word;
        }
        else {
            keyboard.getKeys().get(1).label = word;
            keyboard.getKeys().get(2).label = " ";
        }
        kv.getKeyboardView().invalidateKey(0);
        kv.getKeyboardView().invalidateKey(1);
        kv.getKeyboardView().invalidateKey(2);
    }
}
