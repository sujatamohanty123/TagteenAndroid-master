package in.tagteen.tagteen.chatting.widget;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class TypingEditText extends AppCompatEditText implements TextWatcher, Runnable {

    private static final int TypingInterval = 2000;

    private OnTypingStatusAnalyser typingChangedListener;
    private boolean currentTypingState = false;
    private Handler handler = new Handler();

    public TypingEditText(@NonNull Context context) {
        super(context);
        this.addTextChangedListener(this);
    }

    public TypingEditText(@NonNull Context context,
                          @NonNull AttributeSet attrs) {
        super(context, attrs);
        this.addTextChangedListener(this);
    }

    public TypingEditText(
            @NonNull Context context,
            @NonNull AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.addTextChangedListener(this);
    }

    public void setOnTypingAnalyser(@NonNull OnTypingStatusAnalyser typingChangedListener) {
        this.typingChangedListener = typingChangedListener;
    }

    @Override
    public void afterTextChanged(Editable s) {
       if(s.length() == 0) return;

        if (typingChangedListener != null) {
            if (!currentTypingState) {
                currentTypingState = true;
                typingChangedListener.onTypingStatusChanged(true);
            }
            handler.removeCallbacks(this);
            handler.postDelayed(this, TypingInterval);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }


    @Override
    public void onTextChanged(CharSequence text, int start, int before, int after) {
    }

    @Override
    public void run() {
        if (typingChangedListener != null) {
            currentTypingState = false;
            typingChangedListener.onTypingStatusChanged(false);
        }
        handler.removeCallbacks(this);
    }

    public interface OnTypingStatusAnalyser {
        void onTypingStatusChanged(boolean isTyping);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return super.onCreateInputConnection(outAttrs);
    }
}