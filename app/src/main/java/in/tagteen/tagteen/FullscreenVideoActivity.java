package in.tagteen.tagteen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

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

import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;

public class FullscreenVideoActivity extends AppCompatActivity {
    private SimpleExoPlayerView exoPlayerView;
    private SimpleExoPlayer exoPlayer;
    private ImageView toggleFullScreenView;

    private String videoUrl;
    private Long currentPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_feed_video_play);

        this.initWidgets();
    }

    private void initWidgets() {
        this.exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);
        this.toggleFullScreenView = findViewById(R.id.toggleFullScreen);
        this.toggleFullScreenView.setImageResource(R.drawable.make_smallscreen);
        this.toggleFullScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDataResult();
                finish();
            }
        });

        Intent intent = getIntent();
        this.videoUrl = intent.getStringExtra(Constants.VIDEO_URL);
        this.currentPosition = intent.getLongExtra(Constants.VIDEO_CURRENT_POSITION, 0);
        if (this.videoUrl != null && this.videoUrl.trim().length() > 0) {
            this.playVideo();
        } else {
            Utils.showShortToast(this, "No video to play");
        }
    }

    private void playVideo() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        this.exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        Uri videoURI = Uri.parse(this.videoUrl);

        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

        this.exoPlayer.prepare(mediaSource);
        this.exoPlayer.seekTo(this.currentPosition);
        this.exoPlayer.setPlayWhenReady(true);
        this.exoPlayerView.setPlayer(this.exoPlayer);
    }

    private void setDataResult() {
        Intent data = new Intent();
        if (this.exoPlayer != null) {
            data.putExtra(Constants.VIDEO_CURRENT_POSITION, this.exoPlayer.getCurrentPosition());
            this.exoPlayer.release();
        }
        setResult(RESULT_OK, data);
    }

    @Override
    public void onBackPressed() {
        this.setDataResult();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
