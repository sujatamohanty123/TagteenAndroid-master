package in.tagteen.tagteen.chatting.emoji;

/**
 * Created by tony00 on 3/23/2019.
 */
public interface SoftKeyboardChangedListener {
    void onKeyboardOpen(int keyboardHeight);

    void onKeyboardClose();
}
