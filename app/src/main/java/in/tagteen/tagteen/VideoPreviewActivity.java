package in.tagteen.tagteen;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswer;
import in.tagteen.tagteen.util.Constants;

public class VideoPreviewActivity extends AppCompatActivity {
    private SimpleExoPlayerView exoPlayerView;
    private SimpleExoPlayer exoPlayer;
    private ProgressBar progressBar;
    private ImageView toggleFullScreenView;

    private String videoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_feed_video_play);

        this.initWidgets();
        this.bindEvents();

        this.playVideo();
    }

    private void initWidgets() {
        // get arguments
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString(Constants.VIDEO_URL) != null) {
            this.videoUrl = bundle.getString(Constants.VIDEO_URL);
        }

        this.exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.VISIBLE);
        this.toggleFullScreenView = findViewById(R.id.toggleFullScreen);
    }

    private void bindEvents() {
        this.toggleFullScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFullScreen();
            }
        });
    }

    private void toggleFullScreen() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void playVideo() {
        if (this.videoUrl == null || this.videoUrl.trim().length() == 0) {
            return;
        }

        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            this.exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            Uri videoURI = Uri.parse(this.videoUrl);

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            this.exoPlayerView.setPlayer(this.exoPlayer);
            this.exoPlayer.prepare(mediaSource);
            this.exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            // do nothing
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
        if (this.exoPlayer != null) this.exoPlayer.release();
    }
}