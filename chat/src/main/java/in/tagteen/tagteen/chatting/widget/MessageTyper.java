package in.tagteen.tagteen.chatting.widget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class MessageTyper extends AppCompatEditText implements TextWatcher{

    public MessageTyper(Context context) {
        this(context, null, -1);
    }

    public MessageTyper(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MessageTyper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
