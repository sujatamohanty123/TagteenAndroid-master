package in.tagteen.tagteen.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import in.tagteen.tagteen.R;


public class DialogListStyle  extends Style {

    private boolean dialogMessageAvatarEnabled;
    private int dialogMessageAvatarWidth;
    private int dialogMessageAvatarHeight;

    static DialogListStyle parse(Context context, AttributeSet attrs) {
        DialogListStyle style = new DialogListStyle(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DialogsList);

        //Last message avatar
        style.dialogMessageAvatarEnabled = typedArray.getBoolean(R.styleable.DialogsList_dialogMessageAvatarEnabled, true);
        style.dialogMessageAvatarWidth = typedArray.getDimensionPixelSize(R.styleable.DialogsList_dialogMessageAvatarWidth,
                context.getResources().getDimensionPixelSize(R.dimen.dialog_last_message_avatar_width));
        style.dialogMessageAvatarHeight = typedArray.getDimensionPixelSize(R.styleable.DialogsList_dialogMessageAvatarHeight,
                context.getResources().getDimensionPixelSize(R.dimen.dialog_last_message_avatar_height));

        typedArray.recycle();
        return style;
    }

    private DialogListStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected boolean isDialogMessageAvatarEnabled() {
        return dialogMessageAvatarEnabled;
    }

    protected int getDialogMessageAvatarWidth() {
        return dialogMessageAvatarWidth;
    }

    protected int getDialogMessageAvatarHeight() {
        return dialogMessageAvatarHeight;
    }

}
