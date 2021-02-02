package in.tagteen.tagteen.Camera.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import in.tagteen.tagteen.Camera.internal.utils.Utils;
import in.tagteen.tagteen.R;

public class CameraSwitchView extends androidx.appcompat.widget.AppCompatImageButton {

  private Drawable frontCameraDrawable;
  private Drawable rearCameraDrawable;
  private int padding = 5;
  Context context;

  public CameraSwitchView(Context context) {
    this(context, null);
    this.context = context;
  }

  public CameraSwitchView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initializeView();
    this.context = context;
  }

  public CameraSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs);
    this.context = context;
  }

  @SuppressLint("ResourceType") private void initializeView() {
    Context context = getContext();
    frontCameraDrawable = ContextCompat.getDrawable(context, R.drawable.ic_camera_front_white_24dp);
    frontCameraDrawable = DrawableCompat.wrap(frontCameraDrawable);
    DrawableCompat.setTintList(frontCameraDrawable.mutate(),
        ContextCompat.getColorStateList(context, R.drawable.switch_camera_mode_selector));

    rearCameraDrawable = ContextCompat.getDrawable(context, R.drawable.ic_camera_front_white_24dp);
    rearCameraDrawable = DrawableCompat.wrap(rearCameraDrawable);
    DrawableCompat.setTintList(rearCameraDrawable.mutate(),
        ContextCompat.getColorStateList(context, R.drawable.switch_camera_mode_selector));

    setBackgroundResource(R.drawable.circle_frame_background_dark);
    displayBackCamera();

    padding = Utils.convertDipToPixels(context, padding);
    setPadding(padding, padding, padding, padding);

    displayBackCamera();
  }

  public void displayFrontCamera() {
    setImageDrawable(frontCameraDrawable);
  }

  public void displayBackCamera() {

    setImageDrawable(rearCameraDrawable);
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
