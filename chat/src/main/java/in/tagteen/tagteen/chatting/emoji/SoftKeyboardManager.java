package in.tagteen.tagteen.chatting.emoji;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

/**
 * Created by tony00 on 3/11/2019.
 */
class SoftKeyboardManager implements ViewTreeObserver.OnGlobalLayoutListener {

    private Context context;
    private View root;
    private int keyBoardHeight;
    private boolean isOpened;
    private SoftKeyboardChangedListener softKeyboardChangedListener;

    private SoftKeyboardManager(@NonNull Context context,
                                @NonNull ViewGroup root){
        this.context = context;
        this.root = root;
        this.root.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public static SoftKeyboardManager from(@NonNull Context context,
                                           @NonNull ViewGroup root){
        return new SoftKeyboardManager(context, root);
    }

    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        root.getWindowVisibleDisplayFrame(r);

        int screenHeight = getUsableScreenHeight();
        int heightDifference = screenHeight
                - (r.bottom - r.top);
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height",
                        "dimen", "android");
        if (resourceId > 0) {
            heightDifference -= context.getResources()
                    .getDimensionPixelSize(resourceId);
        }
        if (heightDifference > 100) {
            keyBoardHeight = heightDifference;
            if(!isOpened){
                if(softKeyboardChangedListener!=null)
                    softKeyboardChangedListener.onKeyboardOpen(keyBoardHeight);
            }
            isOpened = true;
        }
        else{
            keyBoardHeight = 0;
            isOpened = false;
            if(softKeyboardChangedListener!=null)
                softKeyboardChangedListener.onKeyboardClose();
        }
    }

    private int getUsableScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            return metrics.heightPixels;

        } else {
            return root.getRootView().getHeight();
        }
    }

    public void setSoftKeyboardChangedListener(SoftKeyboardChangedListener softKeyboardChangedListener) {
        this.softKeyboardChangedListener = softKeyboardChangedListener;
    }

    public int getKeyBoardHeight() {
        return keyBoardHeight;
    }
}
