package in.tagteen.tagteen.viewholder;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;

import im.ene.toro.CacheManager;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.Config;
import im.ene.toro.exoplayer.DefaultExoCreator;
import im.ene.toro.exoplayer.ExoCreator;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.exoplayer.ToroExo;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswer;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;

public class AnswerViewHolder extends RecyclerView.ViewHolder implements ToroPlayer {
    private PlayerView playerView;
    private ExoPlayerViewHelper helper;
    private ProgressBar progressBarBuffering;
    private ImageView posterView;
    private Container container = null;
    private ImageView imgToggleScreen;

    private KnowledgeAnswer answer;
    private Uri videoUri;
    private Context context;

    public AnswerViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        this.playerView = itemView.findViewById(R.id.player);
        this.progressBarBuffering = itemView.findViewById(R.id.progressBarBuffering);
        this.posterView = itemView.findViewById(R.id.posterView);
        this.imgToggleScreen = itemView.findViewById(R.id.toggleFullScreen);
        this.imgToggleScreen.setVisibility(View.GONE);

        this.playerView.removeView(this.posterView);
        this.playerView.getOverlayFrameLayout().addView(this.posterView);
    }

    @Override
    public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
        if (this.helper == null) {
            this.posterView.setVisibility(View.VISIBLE);
            DefaultLoadControl loadControl =
                    new DefaultLoadControl(
                            new DefaultAllocator(true, 16),
                            3000,
                            5000,
                            500,
                            3000,
                            1024,
                            true);

            Config.Builder configBuilder = new Config.Builder();
            configBuilder.setLoadControl(loadControl);
            ExoCreator exoCreator = new DefaultExoCreator(ToroExo.with(this.context), configBuilder.build());
            this.helper = new ExoPlayerViewHelper(this, this.videoUri, null, exoCreator);
            this.helper.addPlayerEventListener(new EventListener() {

                @Override
                public void onFirstFrameRendered() {
                    // update the view count
                    if (answer != null && answer.getAnswerId() != null) {
                        Utils.updateAnswerViewCount(context, answer.getAnswerId());
                    }
                }

                @Override
                public void onBuffering() {
                    showBuffering();
                    playerView.hideController();
                }

                @Override
                public void onPlaying() {
                    hideBuffering();
                }

                @Override
                public void onPaused() {
                    hideBuffering();
                }

                @Override
                public void onCompleted() {
                    playerView.showController();
                }
            });
        }
        container.setCacheManager(CacheManager.DEFAULT);
        if (playbackInfo == null) {
            playbackInfo = new PlaybackInfo();
        }
        this.container = container;
        if (this.helper != null) {
            this.helper.initialize(container, playbackInfo);
        }
    }

    public void bind(KnowledgeAnswer answer, Uri videoUri) {
        this.answer = answer;
        this.videoUri = videoUri;
    }

    public ImageView getPosterView() {
        return posterView;
    }

    @NonNull
    @Override
    public View getPlayerView() {
        return this.playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return this.helper != null ? this.helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void play() {
        if (this.helper != null) {
            this.helper.play();
        }
    }

    @Override
    public void pause() {
        if (this.helper != null) {
            this.helper.pause();
        }
    }

    @Override
    public boolean isPlaying() {
        return this.helper != null && this.helper.isPlaying();
    }

    @Override
    public void release() {

    }

    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.65;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    private void showBuffering() {
        this.progressBarBuffering.setVisibility(View.VISIBLE);
    }

    private void hideBuffering() {
        if (this.posterView.getVisibility() == View.VISIBLE) {
            this.posterView.setVisibility(View.GONE);
        }
        if (this.progressBarBuffering.getVisibility() == View.VISIBLE) {
            this.progressBarBuffering.setVisibility(View.GONE);
        }
    }
}
