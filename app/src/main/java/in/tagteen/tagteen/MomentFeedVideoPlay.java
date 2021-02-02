package in.tagteen.tagteen;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.ModifiedAsyncWorker;

public class MomentFeedVideoPlay extends AppCompatActivity implements AsyncResponse {
    private SimpleExoPlayerView exoPlayerView;
    private SimpleExoPlayer exoPlayer;
    private ProgressBar progressBar;
    private ImageView toggleFullScreenView;

    private String postid, post_creater_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_feed_video_play);
        this.exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.VISIBLE);
        this.toggleFullScreenView = findViewById(R.id.toggleFullScreen);
        this.toggleFullScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFullScreen();
            }
        });

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        post_creater_id = intent.getStringExtra("post_creater_id");

        this.getData(postid,post_creater_id);
    }

    private void toggleFullScreen() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void getData(String postid, String post_creater_id) {
        ModifiedAsyncWorker mWorker = new ModifiedAsyncWorker(this);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        mWorker.execute(
                ServerConnector.REQUEST_GET_ARTICLE_DETAIL + postid + "/" + post_creater_id,
                BroadcastObject.toString(),
                RequestConstants.GET_REQUEST,
                RequestConstants.HEADER_YES,
                RequestConstants.GET_ARTICLE_DETAIL);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        if (RequestConstants.GET_ARTICLE_DETAIL == REQUEST_NUMBER) {
            this.progressBar.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(output);
                JSONObject data = jsonObject.getJSONObject("data");

                JSONArray images = data.getJSONArray("image");
                JSONObject videos = data.getJSONObject("video");
                String url=videos.getString("url");

                try {
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

                    Uri videoURI = Uri.parse(url);

                    DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

                    exoPlayerView.setPlayer(exoPlayer);
                    exoPlayer.prepare(mediaSource);
                    exoPlayer.setPlayWhenReady(true);
                } catch (Exception e){
                    Log.e("MainAcvtivity"," exoplayer error "+ e.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        long currentPosition = this.exoPlayer.getCurrentPosition();
        super.onConfigurationChanged(newConfig);
        this.exoPlayer.seekTo(currentPosition);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (this.toggleFullScreenView != null) {
                this.toggleFullScreenView.setImageResource(R.drawable.make_fullscreen);
            }
        } else {
            if (this.toggleFullScreenView != null) {
                this.toggleFullScreenView.setImageResource(R.drawable.make_smallscreen);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) exoPlayer.release();
    }
}
