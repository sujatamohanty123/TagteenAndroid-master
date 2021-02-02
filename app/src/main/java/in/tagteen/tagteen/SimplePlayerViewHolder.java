package in.tagteen.tagteen;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import org.json.JSONException;
import org.json.JSONObject;

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
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.InsertCoolModel;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.helpers.CacheDataSourceFactory;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_DELETE_POST;
import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_REPORT_POST;

/**
 * Created by Mathivanan on 02-02-2018.
 */

public class SimplePlayerViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, AsyncResponse {
    public PlayerView playerView;
    private ExoPlayerViewHelper helper;
    private SimpleExoPlayer simpleExoPlayer;

    private ProgressBar progressBarBuffering;
    public ImageView posterView;
    private GetPostResponseModel.PostDetails data;
    private Uri videoUri;
    public RelativeLayout rock;
    public RelativeLayout layoutComment,layoutshare;
    public ImageView profileImage,rokImage,moreoption;
    public TextView userName,userTag,textBeAFnn,feedTime,likecount,textCommentCount,textShareCount,viewCount,feedDesc,textReport;
    private Context context;
    private FrameLayout framTrans;

    public ImageView toggleFullScreenView;
    public ImageButton playButton;
    public ImageButton pauseButton;

    private int position;
    private String loggedInUserId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    private int like_count;

    private long resumePosition = -1;
    private Container container = null;

    private OnCallbackListener onCallbackListener;
    private OnPostDeleteListener onPostDeleteListener;
    private String deletedPostId;

    public SimplePlayerViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        this.playerView = itemView.findViewById(R.id.player);
        this.progressBarBuffering = itemView.findViewById(R.id.progressBarBuffering);
        this.posterView = itemView.findViewById(R.id.posterView);
        this.playerView.removeView(this.posterView);
        this.playerView.getOverlayFrameLayout().addView(this.posterView);
        this.userName = (TextView)itemView.findViewById(R.id.userName);
        this.userTag = (TextView)itemView.findViewById(R.id.userTag);
        this.textBeAFnn = (TextView)itemView.findViewById(R.id.textBeAFnn);
        this.feedTime = (TextView)itemView.findViewById(R.id.feedTime);
        this.likecount = (TextView)itemView.findViewById(R.id.likecount);
        this.textCommentCount = (TextView)itemView.findViewById(R.id.textCommentCount);
        this.textShareCount = (TextView)itemView.findViewById(R.id.textShareCount);
        this.viewCount = (TextView)itemView.findViewById(R.id.viewCount);
        this.feedDesc = (TextView)itemView.findViewById(R.id.feedDesc);
        this.profileImage = (ImageView)itemView.findViewById(R.id.profileImage);
        this.rokImage = (ImageView)itemView.findViewById(R.id.rokImage);
        this.layoutComment = (RelativeLayout)itemView.findViewById(R.id.layoutComment);
        this.layoutshare = (RelativeLayout)itemView.findViewById(R.id.layoutshare);
        this.rock = (RelativeLayout) itemView.findViewById(R.id.rock);
        this.textReport = (TextView) itemView.findViewById(R.id.textReport);
        this.moreoption = (ImageView) itemView.findViewById(R.id.moreOptions);
        this.framTrans = (FrameLayout) itemView.findViewById(R.id.framTrans);
        this.toggleFullScreenView = itemView.findViewById(R.id.toggleFullScreen);
        this.toggleFullScreenView.setVisibility(View.GONE);
        this.playButton = itemView.findViewById(R.id.exo_play);
        this.pauseButton = itemView.findViewById(R.id.exo_pause);
    }

    @Override public View getPlayerView() {
        return playerView;
    }

    @Override public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    public void setOnCallbackListener(OnCallbackListener onCallbackListener) {
        this.onCallbackListener = onCallbackListener;
    }

    public void setOnPostDeleteListener(OnPostDeleteListener onPostDeleteListener) {
        this.onPostDeleteListener = onPostDeleteListener;
    }

    public void setResumePosition(long resumePosition) {
        this.resumePosition = resumePosition;
    }

    public Player getPlayer() {
        return this.playerView.getPlayer();
    }

    private void showBuffering() {
        this.progressBarBuffering.setVisibility(View.VISIBLE);
        //this.pauseButton.setVisibility(View.GONE);
    }

    private void hideBuffering() {
        if (this.posterView.getVisibility() == View.VISIBLE) {
            this.posterView.setVisibility(View.GONE);
        }
        if (this.progressBarBuffering.getVisibility() == View.VISIBLE) {
            this.progressBarBuffering.setVisibility(View.GONE);
        }
        //this.pauseButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void initialize(@NonNull Container container, PlaybackInfo playbackInfo) {
        if (this.helper == null) {
            this.posterView.setVisibility(View.VISIBLE);

            ExoCreator exoCreator = TagteenApplication.getExoCreator();
            this.helper = new ExoPlayerViewHelper(this, this.videoUri, null, exoCreator);
            this.helper.addPlayerEventListener(new EventListener() {

                @Override
                public void onFirstFrameRendered() {
                    // update the view count
                    Utils.updateShowroomViewCount(context, data.getId());
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
                    restartVideo();
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
        if (wantsToPlay()){
            this.framTrans.setVisibility(View.GONE);
        } else {
            this.framTrans.setVisibility(View.VISIBLE);
        }
    }

    private void restartVideo() {
        this.getPlayer().seekTo(0);
        this.getPlayer().setPlayWhenReady(true);
    }

    public void removeReaction() {
        if (data == null) {
            return;
        }
        InsertCoolModel json = new InsertCoolModel();
        json.setFlag(5);
        json.setPost_id(data.getId());
        json.setFriend_user_id(loggedInUserId);
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        CommonApicallModule.deleteCoolSwagDebNerd(json, token, context);
    }

    public void showPopupMenu(View view, String postUserId) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();

        if (this.loggedInUserId.equalsIgnoreCase(postUserId)) {
            inflater.inflate(R.menu.popup_menu_delete, menu);
        } else {
            inflater.inflate(R.menu.popup_menu_report, menu);
            //inflater.inflate(R.menu.popup_menu_delete, menu);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        deletePost();
                        break;

                    case R.id.action_report:
                        reportPost();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void deletePost() {
        final Dialog d = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.confirmationpopup);
        final TextView msg = (TextView) d.findViewById(R.id.message);
        TextView name = (TextView) d.findViewById(R.id.yourname);
        TextView ok = (TextView) d.findViewById(R.id.confirm_ok_btn);
        TextView continueorder = (TextView) d.findViewById(R.id.confirm);
        TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
        name.setVisibility(View.GONE);
        ok.setVisibility(View.GONE);
        msg.setText("Are you sure you want to delete this post ?");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        continueorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                if (loggedInUserId.equalsIgnoreCase(data.getPostCreatorId())) {
                    JSONObject BroadcastObject = new JSONObject();
                    try {
                        BroadcastObject.put("post_id", data.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    deletedPostId = data.getId();
                    String Url = REQUEST_DELETE_POST;
                    reportDelete("LoginUser", Url, BroadcastObject, RequestConstants.REQUEST_DELETE_POST);
                } else {
                    JSONObject BroadcastObject = new JSONObject();
                    try {
                        BroadcastObject.put("post_id", data.getId());
                        BroadcastObject.put("user_id", loggedInUserId);
                        BroadcastObject.put("message", "-");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String Url = REQUEST_REPORT_POST;
                    reportDelete("OtherUser", Url, BroadcastObject, RequestConstants.REQUEST_REPORT_POST);
                }
            }
        });
        d.show();
    }

    private void reportPost() {
        final Dialog onLongPressdialog = new Dialog(context);
        onLongPressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        onLongPressdialog.setContentView(R.layout.privacy_dialog);
        RelativeLayout public_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative1);
        RelativeLayout private_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative2);
        RelativeLayout frnds_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative3);
        RelativeLayout bff_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative4);
        RelativeLayout fan_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative5);
        final ImageView profile_image = (ImageView) onLongPressdialog.findViewById(R.id.imageProfilePic);
        final ImageView img1 = (ImageView) onLongPressdialog.findViewById(R.id.img1);
        final ImageView img2 = (ImageView) onLongPressdialog.findViewById(R.id.img2);
        final ImageView img3 = (ImageView) onLongPressdialog.findViewById(R.id.img3);
        final ImageView img4 = (ImageView) onLongPressdialog.findViewById(R.id.img4);
        final ImageView img5 = (ImageView) onLongPressdialog.findViewById(R.id.img5);
        TextView done = (TextView) onLongPressdialog.findViewById(R.id.done);
        TextView cancel = (TextView) onLongPressdialog.findViewById(R.id.cancel);
        TextView text = (TextView) onLongPressdialog.findViewById(R.id.text);
        TextView text1 = (TextView) onLongPressdialog.findViewById(R.id.text1);
        TextView text2 = (TextView) onLongPressdialog.findViewById(R.id.text2);
        TextView text3 = (TextView) onLongPressdialog.findViewById(R.id.text3);
        TextView text4 = (TextView) onLongPressdialog.findViewById(R.id.text4);
        TextView text5 = (TextView) onLongPressdialog.findViewById(R.id.text5);
        TextView text6 = (TextView) onLongPressdialog.findViewById(R.id.text6);
        text2.setText("Spam");
        text3.setText("Violent");
        text4.setText("Inappropriate");
        text5.setText("Self Harm");
        text6.setText("Offensive");
        done.setText("Report");
        text1.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
        profile_image.setVisibility(View.VISIBLE);

        Utils.loadImageUsingGlideCenterCrop(
                this.context, profile_image, this.data.getProfile_url());

        text.setText(data.getFirst_name() + " " + data.getLast_name());
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLongPressdialog.dismiss();
            }
        });

        public_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.GONE);
                img5.setVisibility(View.GONE);
            }
        });
        private_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setVisibility(View.GONE);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.GONE);
                img5.setVisibility(View.GONE);
            }
        });
        frnds_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setVisibility(View.GONE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.VISIBLE);
                img4.setVisibility(View.GONE);
                img5.setVisibility(View.GONE);
            }
        });
        bff_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setVisibility(View.GONE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.VISIBLE);
                img5.setVisibility(View.GONE);
            }
        });
        fan_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setVisibility(View.GONE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.GONE);
                img5.setVisibility(View.VISIBLE);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "";
                if (img1.getVisibility() == View.VISIBLE) {
                    msg = "Spam";
                } else if (img2.getVisibility() == View.VISIBLE) {
                    msg = "Violent";
                } else if (img3.getVisibility() == View.VISIBLE) {
                    msg = "Inappropriate";
                } else if (img4.getVisibility() == View.VISIBLE) {
                    msg = "Self Harm";
                } else if (img5.getVisibility() == View.VISIBLE) {
                    msg = "Offensive";
                }
                JSONObject BroadcastObject = new JSONObject();
                try {
                    BroadcastObject.put("post_id", data.getId());
                    BroadcastObject.put("user_id", loggedInUserId);
                    BroadcastObject.put("message", msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String Url = REQUEST_REPORT_POST;
                reportDelete("OtherUser", Url, BroadcastObject, RequestConstants.REQUEST_REPORT_POST);
                onLongPressdialog.dismiss();
            }
        });

        onLongPressdialog.show();
    }

    public void moveToComments(int flag, int showKeypad) {
        Bundle bundle = new Bundle();
        Intent in = new Intent(context, CommentLikeActivity_new.class);
        bundle.putInt("comment_select_flag", flag);
        bundle.putInt("keypadshow", showKeypad);
        bundle.putString("postid", data.getId());
        bundle.putString("type", "showroom");
        bundle.putString("react_type", "");
        bundle.putInt(Constants.COMMENTS_COUNT, data.getConversationCount());
        bundle.putBoolean(Constants.BLOCK_REACTS, true);
        bundle.putInt(Constants.REACTS_COUNT, like_count);
        in.putExtras(bundle);
        context.startActivity(in);
    }

    public void likestatus() {
        if (this.data != null) {
            like_count = this.data.getLikeCount();
        }
        if (like_count == 1) {
            likecount.setVisibility(View.VISIBLE);
            likecount.setText(like_count + " U rocked");
        } else if (like_count > 1) {
            likecount.setVisibility(View.VISIBLE);
            likecount.setText(like_count + " U rocked");
        } else {
            likecount.setVisibility(View.GONE);
        }
    }

    private void reportDelete(String Tag, String Url, JSONObject jsonObject, String code) {
        AsyncWorker mWorker = new AsyncWorker(context);
        mWorker.delegate = this;
        mWorker.delegate = this;
        mWorker.execute(Url, jsonObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_NO, code);
    }

    @Override
    public void play() {
        if (this.helper != null) {
            if (this.resumePosition != -1 && this.container != null) {
                PlaybackInfo playbackInfo = getCurrentPlaybackInfo();
                playbackInfo.setResumePosition(this.resumePosition);
                this.container.savePlaybackInfo(getPlayerOrder(), playbackInfo);
                //this.container.getPlaybackInfo(this.getPlayerOrder()).setResumePosition(this.resumePosition);
            }
            this.helper.play();
            /*if (this.player != null) {
                this.player.setPlayWhenReady(true);
            } else {
                Utils.showShortToast(this.context, "Player is not initialized");
            }*/
            this.resumePosition = -1;
        }
    }

    @Override
    public void pause() {
        if (this.helper != null) this.helper.pause();
    }

    @Override
    public boolean isPlaying() {
        return this.helper != null && this.helper.isPlaying();
    }

    @Override
    public void release() {
        if (this.helper != null) {
            this.helper.release();
            this.helper = null;
        }
    }

    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.65;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    public void youthListbind(
            GetPostResponseModel.PostDetails data,
            Uri videoUri,
            int position) {
        this.data = data;
        this.videoUri = videoUri;
        this.position = position;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        try {
            if (RequestConstants.REQUEST_REPORT_POST == REQUEST_NUMBER) {
                Utils.showShortToast(context,"Your post has been successfully submitted.");
            } else if (RequestConstants.REQUEST_DELETE_POST == REQUEST_NUMBER) {
                if (onCallbackListener != null) {
                    onCallbackListener.OnComplete();
                }
                if (onPostDeleteListener != null) {
                    onPostDeleteListener.onDelete(deletedPostId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}