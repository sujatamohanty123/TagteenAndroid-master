package in.tagteen.tagteen;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.PlayerControlView;

import java.util.ArrayList;

import im.ene.toro.widget.Container;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.LikeJsonInputModel;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/**
 * Created by Mathivanan on 02-02-2018.
 */

class BasicAdapter extends RecyclerView.Adapter<SimplePlayerViewHolder> {
    private final OnShareItemCallback mShareItemCallback;
    private ArrayList<GetPostResponseModel.PostDetails> youthList;
    private Context context;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private OnCallbackListener onCallbackListener;
    private OnFullScreenExpandListener onFullScreenExpandListener;
    private OnBindItemListener onBindItemListener;
    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickedListener onItemClickedListener;
    private SimplePlayerViewHolder selectedVideo = null;

    private final boolean isTaggedUser;
    private final String loggedInUserId;
    private static final int FLAG_COMMENT = 0;
    private static final int FLAG_REACTS = 1;
    private boolean user_like = false;

    public BasicAdapter(
            Context context,
            ArrayList<GetPostResponseModel.PostDetails> youthList,
            Container videoContainter,
            OnShareItemCallback shareItemCallback) {
        this.youthList = youthList;
        this.context = context;
        mShareItemCallback = shareItemCallback;
        loggedInUserId =
                SharedPreferenceSingleton.getInstance()
                        .getStringPreference(context, RegistrationConstants.USER_ID);
        isTaggedUser = SharedPreferenceSingleton.getInstance()
                .getBoolPreference(context, RegistrationConstants.IS_TAGGED_USER);

        final LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) videoContainter.getLayoutManager();
        videoContainter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager != null) {
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onLoadMoreListener.onLoadMore();
                                }
                            }, 1000);
                        }
                        isLoading = true;
                    }
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (onBindItemListener != null) {
                        int itemIndex = linearLayoutManager.findFirstVisibleItemPosition();
                        if (youthList == null || youthList.isEmpty() || itemIndex >= youthList.size()) {
                            return;
                        }
                        GetPostResponseModel.PostDetails data =
                                youthList.get(linearLayoutManager.findFirstVisibleItemPosition());
                        onBindItemListener.onBindItem(data);
                    }
                }
            }
        });
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setOnCallbackListener(OnCallbackListener onCallbackListener) {
        this.onCallbackListener = onCallbackListener;
    }

    public void setOnFullScreenExpandListener(OnFullScreenExpandListener onFullScreenExpandListener) {
        this.onFullScreenExpandListener = onFullScreenExpandListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setOnBindItemListener(OnBindItemListener onBindItemListener) {
        this.onBindItemListener = onBindItemListener;
    }

    public OnLoadMoreListener getOnLoadMoreListener() {
        return onLoadMoreListener;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    @NonNull
    @Override
    public SimplePlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context)
                .inflate(R.layout.view_holder_exoplayer_basic, parent, false);
        SimplePlayerViewHolder holder = new SimplePlayerViewHolder(view, this.context);
        holder.setOnCallbackListener(onCallbackListener);
        holder.setOnPostDeleteListener(new OnPostDeleteListener() {
            @Override
            public void onDelete(String postId) {
                if (youthList != null) {
                    for (int i = 0; i < youthList.size(); i++) {
                        GetPostResponseModel.PostDetails postData = youthList.get(i);
                        if (postData.getId().equals(postId)) {
                            youthList.remove(i);
                            break;
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final SimplePlayerViewHolder holder, int position) {
        final GetPostResponseModel.PostDetails data = this.youthList.get(position);

        Uri videoUri = Uri.parse(UrlUtils.getUpdatedVideoUrl(data.getVideo().getUrl()));
        if (videoUri != null) {
            holder.youthListbind(data, videoUri, holder.getAdapterPosition());
        }

        if (data.getVideoThumbnails() != null && !data.getVideoThumbnails().isEmpty()) {
            Utils.loadImageUsingGlide(
                    this.context, holder.posterView,
                    UrlUtils.getUpdatedImageUrl(data.getVideoThumbnails().get(0).getUrl(), "large"));
        }

        holder.playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                }
                return false;
            }
        });
        holder.playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (onItemClickedListener != null) {
                    onItemClickedListener.onItemClicked(visibility == View.VISIBLE);
                }
            }
        });

        if (this.onBindItemListener != null) {
            if (position == 0) {
                this.onBindItemListener.onBindItem(data);
            }
            return;
        }

        holder.moreoption.setTag(true);
        user_like = data.getUserLike();

        holder.userName.setText(data.getFirst_name() + " " + data.getLast_name());
        holder.userTag.setText(data.getTagged_number());
        Utils.loadProfilePic(context, holder.profileImage, data.getProfile_url());
        holder.feedTime.setText(Utils.getRelativeTime(data.getDateCreated()));
        holder.likestatus();

        if (data.getConversationCount() == 1) {
            holder.textCommentCount.setText(data.getConversationCount() + " Comment");
        }
        if (data.getConversationCount() > 1) {
            holder.textCommentCount.setText(data.getConversationCount() + " Comments");
        }

        if (data.getView_count() == 1) {
            holder.viewCount.setText(data.getView_count() + " View");
        }
        if (data.getView_count() > 1) {
            holder.viewCount.setText(data.getView_count() + " Views");
        }

        if (data.getContent().equalsIgnoreCase("")
                || data.getContent().equalsIgnoreCase(" ")
                || data.getContent() == null) {
            holder.feedDesc.setVisibility(View.GONE);
        } else {
            //holder.feedDesc.setVisibility(View.VISIBLE);
            holder.feedDesc.setText(data.getContent());
            //Utils.makeTextViewResizable(this.feedDesc, 3, "More", true);
        }

        if (user_like) {
            holder.rokImage.setImageResource(R.drawable.ic_bottom_youthube);
            holder.rokImage.setColorFilter(ContextCompat.getColor(context, R.color.red_600),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.rokImage.setImageResource(R.drawable.ic_bottom_youthube);
            holder.rokImage.setColorFilter(ContextCompat.getColor(context, R.color.green_50),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        // events
        holder.toggleFullScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFullScreenExpandListener != null && holder.getCurrentPlaybackInfo() != null) {
                    selectedVideo = holder;
                    onFullScreenExpandListener.moveToFullScreenMode(
                            data.getVideo().getUrl(), holder.getCurrentPlaybackInfo().getResumePosition());
                }
            }
        });
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoProfile(context, data.getPostCreatorId());
            }
        });
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.gotoProfile(context, data.getPostCreatorId());
            }
        });
        holder.userTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.gotoProfile(context, data.getPostCreatorId());
            }
        });
        final MediaPlayer comment_sound = MediaPlayer.create(context, R.raw.comment);
        holder.layoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment_sound != null) comment_sound.start();
                if (isTaggedUser) {
                    holder.moveToComments(FLAG_COMMENT, 1);
                } else {
                    Utils.showUnverifiedUserDialog(context);
                }
            }
        });

        holder.likecount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moveToComments(FLAG_REACTS, 0);
            }
        });
        holder.textCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTaggedUser) {
                    holder.moveToComments(FLAG_COMMENT, 0);
                } else {
                    Utils.showUnverifiedUserDialog(context);
                }
            }
        });
        final MediaPlayer rock_sound = MediaPlayer.create(context, R.raw.like_teenfeed);
        holder.rock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTaggedUser) {
                    Utils.showUnverifiedUserDialog(context);
                    return;
                }
                if (user_like) {
                    holder.rokImage.setImageResource(R.drawable.ic_bottom_youthube);
                    holder.rokImage.setColorFilter(ContextCompat.getColor(context, R.color.green_50),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    data.setLikeCount(data.getLikeCount() - 1);
                    holder.removeReaction();
                } else {
                    if (rock_sound != null) rock_sound.start();
                    holder.rokImage.setImageResource(R.drawable.ic_bottom_youthube);
                    holder.rokImage.setColorFilter(ContextCompat.getColor(context, R.color.red_600),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    data.setLikeCount(data.getLikeCount() + 1);

                    LikeJsonInputModel likeJsonInputModel = new LikeJsonInputModel();
                    String token = SharedPreferenceSingleton.getInstance()
                            .getStringPreference(context, RegistrationConstants.TOKEN);
                    likeJsonInputModel.setUser_id(loggedInUserId);
                    likeJsonInputModel.setPost_id(data.getId());
                    // remove reaction before inserting
                    //removeReaction();
                    CommonApicallModule.callApiForLike(likeJsonInputModel, token, context);
                }
                user_like = !user_like;
                data.setUserLike(user_like);
                holder.likestatus();
                if (onCallbackListener != null) {
                    onCallbackListener.OnComplete();
                }
            }
        });
        holder.moreoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.showPopupMenu(holder.moreoption, data.getPostCreatorId());
            }
        });
        final MediaPlayer share_sound = MediaPlayer.create(context, R.raw.share);
        holder.layoutshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (share_sound != null) share_sound.start();
                if (isTaggedUser) {
                    //new ShareDialog(context, data, "showroom", new OnCallbackListener() {
                    //  @Override
                    //  public void OnComplete() {
                    //
                    //  }
                    //});
                    mShareItemCallback.onClickShareButton(data);
                } else {
                    Utils.showUnverifiedUserDialog(context);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return youthList.size();
    }

    public void resumeVideoPosition(long resumePosition) {
        if (this.selectedVideo != null) {
            this.selectedVideo.setResumePosition(resumePosition);
        }
    }

    public interface OnFullScreenExpandListener {
        void moveToFullScreenMode(String videoUrl, long resumePosition);
    }

    public interface OnShareItemCallback {
        void onClickShareButton(GetPostResponseModel.PostDetails data);
    }

    public interface OnBindItemListener {
        void onBindItem(GetPostResponseModel.PostDetails data);
    }

    public interface OnItemClickedListener {
        void onItemClicked(boolean isControlsVisible);
    }
}