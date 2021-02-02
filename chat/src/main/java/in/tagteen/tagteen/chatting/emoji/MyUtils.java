package in.tagteen.tagteen.chatting.emoji;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by lovekushvishwakarma on 12/09/17.
 */

class MyUtils {

    public static void hideSoftKeyboard(Context mContext) {
        if (((Activity) mContext).getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) ((Activity) mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static float toDp(Context context, int value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.getResources().getDisplayMetrics());
    }
}
