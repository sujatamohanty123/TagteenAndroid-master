package in.tagteen.tagteen.Camera.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import android.util.AttributeSet;

import in.tagteen.tagteen.R;


/*
 * Created by memfis on 7/6/16.
 */
public class FlashSwitchView extends AppCompatImageButton {

    private Drawable flashOnDrawable;
    private Drawable flashOffDrawable;
    private Drawable flashAutoDrawable;

    public FlashSwitchView(@NonNull Context context) {
        this(context, null);
    }

    public FlashSwitchView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        flashOnDrawable = ContextCompat.getDrawable(context, R.drawable.flash_on);
        flashOffDrawable = ContextCompat.getDrawable(context, R.drawable.flash_off);
        flashAutoDrawable = ContextCompat.getDrawable(context, R.drawable.flash_auto);
        init();
    }
    public void hideFlashSwitchView(boolean isNeedHide) {
       setImageDrawable(null);
    }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        setScaleType(ScaleType.CENTER_CROP);
        displayFlashAuto();
    }

    public void displayFlashOff() {
        setImageDrawable(flashOffDrawable);
    }

    public void displayFlashOn() {
        setImageDrawable(flashOnDrawable);
    }

    public void displayFlashAuto() {
        setImageDrawable(flashAutoDrawable);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (Build.VERSION.SDK_INT > 10) {
            if (enabled) {
                setAlpha(1f);
            } else {
                setAlpha(0.5f);
            }
        }
    }

}
