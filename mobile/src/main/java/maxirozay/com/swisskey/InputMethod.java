package maxirozay.com.swisskey;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.text.InputType;
import android.util.*;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Maxime Rossier on 13.08.2015.
 */
public class InputMethod extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;
    private String currentWord = "";
    private final int PREDICTION1 = -101,PREDICTION2 = -102,PREDICTION3 = -103,
                        CAPSLOCK = -104, SWITCHKEYBOARD = -105;
    private int keyboardType = 0;
    private String kikoo="kikoo !!";
    private boolean caps = false, capsLock = false;

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        LoadDict dict= new LoadDict( getApplicationContext());
        //dict.LoadFromFile("listeFR.txt");
        //List<String> lstSms = new ArrayList<String>(dict.getAllSmsFromProvider());
        List<SMSObject> lstSms = new ArrayList<SMSObject>(dict.readSMS());
       // for(String element : lstSms)
           // Log.d("contenuSMS",element);
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
            kv.setKeyboard(keyboard);
    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);

        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }



    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch(primaryCode) {

            case Keyboard.KEYCODE_SHIFT:
                capsLock = false;
                keyboard.getKeys().get(33).icon = getDrawable(R.drawable.caps);
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case CAPSLOCK:
                this.keyboard.getKeys().get(33).icon = getDrawable(R.drawable.capslock);
                capsLock = true;
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
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
            case SWITCHKEYBOARD:
                if (keyboardType == 0) {
                    keyboard = new Keyboard(this, R.xml.alt_keyboard);
                    keyboardType = 1;
                }
                else {
                    keyboard = new Keyboard(this, R.xml.qwertz);
                    keyboardType = 0;
                }
                kv.setKeyboard(keyboard);
                break;
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
                if (!capsLock) {
                    caps = false;
                    keyboard.setShifted(caps);
                }
                if (keyboardType == 0) {
                    if (primaryCode == 32) {
                        keyboard.getKeys().get(2).label = " ";
                    } else {
                        String[] last30Char;
                        last30Char = ic.getTextBeforeCursor(30, InputConnection.GET_TEXT_WITH_STYLES).toString().split(" ");
                        if (last30Char.length == 0)
                            keyboard.getKeys().get(2).label = " ";
                        else
                            keyboard.getKeys().get(2).label = last30Char[last30Char.length - 1];
                    }
                    kv.invalidateKey(0);
                    kv.invalidateKey(1);
                    kv.invalidateKey(2);
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
}
