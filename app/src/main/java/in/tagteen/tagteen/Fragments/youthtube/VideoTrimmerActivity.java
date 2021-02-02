package in.tagteen.tagteen.Fragments.youthtube;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import in.tagteen.tagteen.CreatePostActivity_Keypadheight;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;
import in.tagteen.tagteen.util.Constants;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;


public class VideoTrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener {
    private K4LVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    private final int maxVideoLength = 10 * 60;
    private String path = "";
    private String flag = "";
    private String fromScreen;
    public GalleryClickedPosition delegate=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            path = extraIntent.getStringExtra(VideoGallery.EXTRA_VIDEO_PATH);
            flag = extraIntent.getStringExtra("Video_pick");
            this.fromScreen = extraIntent.getStringExtra(Constants.FROM_SCREEN);
        }

        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));

        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {
            mVideoTrimmer.setMaxDuration(maxVideoLength);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnK4LVideoListener(this);
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }


        findViewById(R.id.imageback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               goback();
            }
        });
    }

    private void goback() {
        if (this.fromScreen != null && this.fromScreen.equals(Constants.ADD_CAMPUS_LIVE_POST_SCREEN)) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else if(flag.equalsIgnoreCase("0")) {
            Intent goback = new Intent(VideoTrimmerActivity.this, VideoGallery.class);
            startActivity(goback);
            finish();
        } else {
            Intent goback = new Intent(VideoTrimmerActivity.this, CreatePostActivity_Keypadheight.class);
            startActivity(goback);
            finish();
        }
    }

    @Override
    public void onTrimStarted() {
        try{
            //mProgressDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void getResult(final Uri uri) {
        mProgressDialog.cancel();
        if (this.fromScreen != null && this.fromScreen.equals(Constants.ADD_CAMPUS_LIVE_POST_SCREEN)) {
            Intent intent = new Intent();
            intent.putExtra(Constants.TRIMMED_VIDEO_PATH, uri.getPath());
            intent.putExtra("actual_path", path);
            setResult(RESULT_OK, intent);
            finish();
        } else if(flag.equalsIgnoreCase("0")) {
            Intent gotoPostVideo = new Intent(this, VideoPostActivity.class);
            gotoPostVideo.putExtra(Constants.TRIMMED_VIDEO_PATH, uri.getPath());
            gotoPostVideo.putExtra("actual_path", path);
            startActivity(gotoPostVideo);
            finish();
        }else{
            Intent gotoPostVideo = new Intent(VideoTrimmerActivity.this, CreatePostActivity_Keypadheight.class);
            gotoPostVideo.putExtra(Constants.TRIMMED_VIDEO_PATH, uri.getPath());
            gotoPostVideo.putExtra("actual_path", path);
            startActivity(gotoPostVideo);
            finish();
        }
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VideoTrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(VideoTrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        goback();
    }
}
