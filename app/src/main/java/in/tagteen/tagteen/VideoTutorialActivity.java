package in.tagteen.tagteen;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.Nullable;

/**
 * Created by lovekushvishwakarma on 14/10/17.
 */

public class VideoTutorialActivity extends Activity implements View.OnClickListener {
  VideoView videoview;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.overridePendingTransition(R.anim.enter, R.anim.exit);
    setContentView(R.layout.video_tutorail_page);
    videoview = (VideoView) findViewById(R.id.videoview);
    LinearLayout linearterms = (LinearLayout) findViewById(R.id.linearterms);
    linearterms.setOnClickListener(this);

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    android.widget.RelativeLayout.LayoutParams params =
        (android.widget.RelativeLayout.LayoutParams) videoview.getLayoutParams();

    float ht_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, metrics.heightPixels,
        getResources().getDisplayMetrics());
    float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, metrics.widthPixels,
        getResources().getDisplayMetrics());
    videoview.getLayoutParams().height = (int) ht_px;
    videoview.getLayoutParams().width = (int) wt_px;
    videoview.requestLayout();

    String path = "android.resource://" + getPackageName() + "/" + R.raw.videotutorial;
    videoview.setVideoURI(Uri.parse(path));
    //videoview.start();
    videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
      }
    });

    TextView sign_in_button = (TextView) findViewById(R.id.sign_in_button);
    Button buttonGetTag = (Button) findViewById(R.id.buttonGetTag);

    sign_in_button.setOnClickListener(this);
    buttonGetTag.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (R.id.sign_in_button == view.getId()) {
      Intent callLoginActivity = new Intent(VideoTutorialActivity.this, LoginActivity.class);
      startActivity(callLoginActivity);
    } else if (R.id.buttonGetTag == view.getId()) {
      Intent callLoginActivity = new Intent(VideoTutorialActivity.this, PhotoUpload.class);
      //Intent callLoginActivity = new Intent(VideoTutorialActivity.this, VerifyPhoneNumberActivity.class);
      startActivity(callLoginActivity);
    } else if (R.id.linearterms == view.getId()) {
      Intent intent = new Intent(VideoTutorialActivity.this, TermsAndCond.class);
      startActivity(intent);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    try {
      videoview.seekTo(0);
      videoview.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
  }
}
