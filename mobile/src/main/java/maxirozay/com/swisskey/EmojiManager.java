package maxirozay.com.swisskey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maxime Rossier on 15.08.2015.
 */
public class EmojiManager {
    private List<String> smiley = new ArrayList<>();

    public EmojiManager() {
        initEmoji();
    }

    private void initEmoji() {
        for (int i=0x1F01; i < 0x1F650; i++)
            smiley.add(String.valueOf(Character.toChars(i)));
    }

    public List<String> getEmojiList(int emojiType){
        switch (emojiType) {
            case 0:
                return smiley;
            case 1:
                return smiley;
            default:
                return smiley;

        }

    }
}
