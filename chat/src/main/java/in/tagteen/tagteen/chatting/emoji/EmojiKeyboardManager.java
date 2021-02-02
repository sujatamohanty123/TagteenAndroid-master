package in.tagteen.tagteen.chatting.emoji;

import android.content.Context;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import in.tagteen.tagteen.chatting.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by tony00 on 3/23/2019.
 */
public class EmojiKeyboardManager implements SoftKeyboardChangedListener {

    private ViewGroup mainView;
    private ImageView emojiIcon;
    private EditText editText;
    private Context context;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private GridView emojiGrid;
    private FrameLayout emojiContainer;
    private SoftKeyboardManager softKeyboardManager;
    private boolean shouldOpenEmojiKeyboard;
    private boolean isEmojiPoppedUp;
    private EmojiPlayer emojiPlayer;
    private EmojiSelectListener emojiSelectListener;
    private EmojiKeyboardManagerEventListener eventListener;
    private SoftKeyboardChangedListener softKeyboardChangedListener;

    private EmojiKeyboardManager(@NonNull Context context,
                                 @NonNull ViewGroup mainView,
                                 @NonNull ImageView emojiIcon,
                                 @NonNull EditText editText) {
        this.context = context;
        this.mainView = mainView;
        this.emojiIcon = emojiIcon;
        this.editText = editText;

        initialize();
    }

    public static EmojiKeyboardManager from(@NonNull Context context,
                                            @NonNull ViewGroup mainView,
                                            @NonNull ImageView emojiIcon,
                                            @NonNull EditText editText) {
        if (context == null || mainView == null || emojiIcon == null || editText == null)
            throw new NullPointerException("Activity context or views must not be null");

        return new EmojiKeyboardManager(context, mainView, emojiIcon, editText);
    }

    private void initialize() {
        slidingUpPanelLayout = (SlidingUpPanelLayout) LayoutInflater.from(context)
                .inflate(R.layout.emoji_frame, null);

        FrameLayout mainContainer = slidingUpPanelLayout.findViewById(R.id.mainViewContainer);
        emojiGrid =  slidingUpPanelLayout.findViewById(R.id.emojiGrid);
        emojiContainer =  slidingUpPanelLayout.findViewById(R.id.emojiContainer);

        mainContainer.addView(mainView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        softKeyboardManager = SoftKeyboardManager.from(context, slidingUpPanelLayout);
        softKeyboardManager.setSoftKeyboardChangedListener(this);

        emojiIcon.setOnClickListener(this::handleIconClick);
        editText.setOnClickListener(this::handleEditClick);

        emojiPlayer = EmojiPlayer.createPlayer(context);

        configAdapter();
    }

    private void configAdapter() {
        final EmojiAdapter adapter = new EmojiAdapter();
        emojiGrid.setAdapter(adapter);

        emojiGrid.setOnItemClickListener((parent, view, position, id) -> {
            int emojiId = adapter.getItem(position);
            emojiPlayer.play(emojiId);
            if (emojiSelectListener != null)
                emojiSelectListener.onEmojiSelect(emojiId);
        });
    }

    @Override
    public void onKeyboardOpen(int keyboardHeight) {
        if(isEmojiPoppedUp){
            hideEmojiKeyboard();
        }
        if(softKeyboardChangedListener!=null)
            softKeyboardChangedListener.onKeyboardOpen(keyboardHeight);
    }

    @Override
    public void onKeyboardClose() {
        if (shouldOpenEmojiKeyboard) {
            showEmojiKeyboard();
            shouldOpenEmojiKeyboard = false;
        }
        if(softKeyboardChangedListener!=null)
            softKeyboardChangedListener.onKeyboardClose();
    }

    private void showEmojiKeyboard(){
        isEmojiPoppedUp = true;
        emojiContainer.setVisibility(View.VISIBLE);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        emojiIcon.setImageResource(R.drawable.close);
    }

    public void hideEmojiKeyboard(){
        if(isEmojiPoppedUp) {
            isEmojiPoppedUp = false;
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            emojiContainer.setVisibility(View.GONE);
            emojiIcon.setImageResource(R.drawable.chat_smiley);
        }
    }

    public void setEmojiSelectListener(EmojiSelectListener emojiSelectListener) {
        this.emojiSelectListener = emojiSelectListener;
    }

    public void setEmojiKeyboardManagerEventListener(EmojiKeyboardManagerEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setSoftKeyboardChangedListener(SoftKeyboardChangedListener softKeyboardChangedListener) {
        this.softKeyboardChangedListener = softKeyboardChangedListener;
    }

    public boolean isEmojiPoppedUp() {
        return isEmojiPoppedUp;
    }

    public SlidingUpPanelLayout getRootView() {
        return slidingUpPanelLayout;
    }

    private void handleIconClick(View view){
        if(isEmojiPoppedUp){
            hideEmojiKeyboard();
        } else {
            if (softKeyboardManager.getKeyBoardHeight() > 0) {
                shouldOpenEmojiKeyboard = true;
                MyUtils.hideSoftKeyboard(context);
            } else {
                showEmojiKeyboard();
            }
        }
        if(eventListener!=null)
            eventListener.onEmojiIconClick(view);
    }

    private void handleEditClick(View view){
        hideEmojiKeyboard();
        if(eventListener!=null)
            eventListener.onEditTextClick(view);
    }

}
