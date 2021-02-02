package in.tagteen.tagteen;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.VideoView;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
public class DetailActivity extends Activity implements SurfaceHolder.Callback{

    MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean pausing = false;;
    VideoView  mVideoView;
    ImageView buttonPlayVideo;
    WebView webView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(in.tagteen.tagteen.R.layout.tt_detail_view);

        buttonPlayVideo = (ImageView) findViewById(in.tagteen.tagteen.R.id.article_detail_play);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        mVideoView = (VideoView) findViewById(in.tagteen.tagteen.R.id.article_detail_video);

        Uri uri = Uri.parse("http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70/v1481795681/2_rp0zyy.mp4");
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();

        buttonPlayVideo.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("http://res.cloudinary.com/krupen/video/upload/w_300,h_150,c_crop,q_70/v1481795681/2_rp0zyy.mp4");
                mVideoView.setVideoURI(uri);
                mVideoView.requestFocus();
                mVideoView.start();
                buttonPlayVideo.setVisibility(View.GONE);

            }});

    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
}