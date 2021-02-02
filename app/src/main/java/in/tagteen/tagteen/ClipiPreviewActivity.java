package in.tagteen.tagteen;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import im.ene.lab.toro.ToroVideoView;
import in.tagteen.tagteen.Fragments.youthtube.adapter.YouthTubeVideoAdapter;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.util.VideoTimerUtils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;



public class ClipiPreviewActivity extends Activity {

    public static int seekTo;
    public static Uri setVideoURI;
    private TextView textTimeupdate, textTotaltime;
    private ImageView makeVideofullscren, imagelogo;
    SeekBar seekabrview;
    Runnable onEverySecond;
    LinearLayout videoConrtollr;
    String sele = "selectedPath";
   ImageView SendImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.clipi_preview_activity);
       // imagelogo = (ImageView) findViewById(R.id.imagelogo_preview);
        videoConrtollr = (LinearLayout) findViewById(R.id.videoConrtollr_preview);
        SharedPreferenceSingleton.getInstance().init(this);
        setVideoURI= Uri.parse(SharedPreferenceSingleton.getInstance().getStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH));
        final ToroVideoView mvideoPlayer = (ToroVideoView) findViewById(R.id.videoPlayer_preview);
        mvideoPlayer.setVideoURI(setVideoURI);
        mvideoPlayer.seekTo(seekTo);
        mvideoPlayer.start();

        textTimeupdate = (TextView) findViewById(R.id.textTimeupdate_preview);
        textTotaltime = (TextView) findViewById(R.id.textTotaltime_preview);
        SendImage = (ImageView) findViewById(R.id.ImageOperationPostButton);
      //  makeVideofullscren = (ImageView) findViewById(R.id.makeVideofullscren_preview);
       // makeVideofullscren.setImageResource(R.drawable.make_smallscreen);
        SendImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_share_tagteen));
        SendImage.setBackground(getResources().getDrawable(R.drawable.di_grayround));

        seekabrview = (SeekBar) findViewById(R.id.seekabrview_preview);

        onEverySecond = new Runnable() {

            @Override
            public void run() {

                if (seekabrview != null) {
                    seekabrview.setProgress(mvideoPlayer.getCurrentPosition());

                    String totalDuration = VideoTimerUtils.milliSecondsToTimer(mvideoPlayer.getDuration());
                    String currentPosition = VideoTimerUtils.milliSecondsToTimer(mvideoPlayer.getCurrentPosition());
                    textTotaltime.setText("" + totalDuration);
                    textTimeupdate.setText("" + currentPosition);
                }

                if (mvideoPlayer.isPlaying()) {
                    seekabrview.postDelayed(onEverySecond, 1000);
                }

            }
        };
        mvideoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                seekabrview.setMax(mvideoPlayer.getDuration());
                if (onEverySecond != null)
                    seekabrview.postDelayed(onEverySecond, 1000);

                showControll();


            }
        });

       /* makeVideofullscren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        mvideoPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showControll();
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YouthTubeVideoAdapter.playWhenFullScrnClose();
    }

    private void hideControll() {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                videoConrtollr.setVisibility(View.GONE);
              //  imagelogo.setVisibility(View.GONE);
            }
        }, 5 * 1000);
    }

    private void showControll() {
        videoConrtollr.setVisibility(View.VISIBLE);
      //  imagelogo.setVisibility(View.VISIBLE);
        hideControll();
    }
}
