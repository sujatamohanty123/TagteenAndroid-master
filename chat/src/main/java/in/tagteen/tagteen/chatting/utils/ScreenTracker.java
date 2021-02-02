package in.tagteen.tagteen.chatting.utils;

import androidx.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by tony00 on 6/1/2019.
 */
public class ScreenTracker {

    private String screen;
    private static ScreenTracker instance;

    private ScreenTracker() {
    }

    public static ScreenTracker getInstance() {
        if (instance == null)
            instance = new ScreenTracker();
        return instance;
    }

    public void registerScreen(@Nullable String screen) {
        this.screen = screen;
    }

    public void unRegisterScreen(@Nullable String screen) {
        if (!TextUtils.isEmpty(this.screen) && this.screen.equals(screen))
            this.screen = null;
    }

    public String getCurrentScreen() {
        return screen;
    }
}
