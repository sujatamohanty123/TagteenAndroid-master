package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.FontStyles;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_DELETE_YOUTHFEED;
import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_REPORT_POST;

public class LanguageVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements AsyncResponse {

  private List<GetPostResponseModel.PostDetails> postList;
  private Context mContext;

  private OnCallbackListener onCallbackListener;
  private OnScrollListener onScrollListener;

  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  private OnLoadMoreListener onLoadMoreListener;
  private boolean isLoading;
  private int visibleThreshold = 5;
  private int lastVisibleItem, totalItemCount;
  private final String loggedInUserId =
      SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

  private int scrollDist = 0;
  private boolean isVisible = true;
  private final int MINIMUM = 25;
  private int toolbarOffset = 0;
  private int toolbarHeight;

  public LanguageVideoAdapter(
      final Context mContext,
      List<GetPostResponseModel.PostDetails> postList,
      RecyclerView recyclerView) {
    this.postList = postList;
    this.mContext = mContext;
    this.toolbarHeight = Utils.getPxFromDp(mContext, 45);

    final LinearLayoutManager linearLayoutManager =
        (LinearLayoutManager) recyclerView.getLayoutManager();
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        moved(toolbarOffset);
        if ((toolbarOffset < toolbarHeight && dy > 0) || (toolbarOffset > 0 && dy < 0)) {
          toolbarOffset += dy;
        }

        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
          if (onLoadMoreListener != null) {
            new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                onLoadMoreListener.onLoadMore();
              }
            }, 200);
          }
          isLoading = true;
        }

        if (isVisible && scrollDist > MINIMUM) {
          hide();
          scrollDist = 0;
          isVisible = false;
        } else if (!isVisible && scrollDist < -MINIMUM) {
          show();
          scrollDist = 0;
          isVisible = true;
        }
        if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
          scrollDist += dy;
        }
      }
    });
  }

  private void hide() {
    if (this.onScrollListener != null) {
      this.onScrollListener.hide();
    }
  }

  private void show() {
    if (this.onScrollListener != null) {
      this.onScrollListener.show();
    }
  }

  private void clipToolbarOffset() {
    if (toolbarOffset > toolbarHeight) {
      toolbarOffset = toolbarHeight;
    } else if (toolbarOffset < 0) {
      toolbarOffset = 0;
    }
  }

  private void moved(int distance) {
    if (this.onScrollListener != null) {
      this.onScrollListener.onMoved(distance);
    }
  }

  public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
    this.onLoadMoreListener = mOnLoadMoreListener;
  }

  public void setOnCallbackListener(OnCallbackListener onCallbackListener) {
    this.onCallbackListener = onCallbackListener;
  }

  public void setOnScrollListener(OnScrollListener onScrollListener) {
    this.onScrollListener = onScrollListener;
  }

  public void setLoaded() {
    isLoading = false;
  }

  @Override
  public int getItemViewType(int position) {
    return this.postList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
      return new ImageViewHolder(
          LayoutInflater.from(parent.getContext()).inflate(R.layout.dex_activity, parent, false));
    } else if (viewType == VIEW_TYPE_LOADING) {
      View view = LayoutInflater.from(mContext).inflate(R.layout.load_more, parent, false);
      return new LoadingViewHolder(view);
    }
    return null;
  }

  @Override
  public int getItemCount() {
    return postList == null ? 0 : postList.size();
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holderView, int position) {
    if (holderView instanceof ImageViewHolder) {
      final ImageViewHolder holder = (ImageViewHolder) holderView;
      final GetPostResponseModel.PostDetails data = postList.get(position);
      final String postUserid = data.getPostCreatorId();
      final String vid_id = data.getId();
      holder.moreOptions.setTag(true);
      holder.userName.setText(data.getFirst_name() + " " + data.getLast_name());
      holder.userTag.setText(data.getTagged_number());
      if (data.getContent() == null || data.getContent().trim().length() == 0) {
        holder.feedDesc.setVisibility(View.GONE);
      } else {
        holder.feedDesc.setVisibility(View.VISIBLE);
        holder.feedDesc.setText(data.getContent());
      }

      holder.likeCount.setText("" + data.getLikeCount());
      holder.commentsCount.setText("" + data.getConversationCount() + " Comments");
      holder.viewsCount.setText("" + data.getView_count() + " Views");
      holder.feedTime.setText("" + Utils.getRelativeTime(data.getDateCreated()));

      if (data.getVideoThumbnails() != null && data.getVideoThumbnails().isEmpty() == false) {
        Utils.loadImageUsingGlide(
            this.mContext, holder.videoThumb,
            UrlUtils.getUpdatedImageUrl(data.getVideoThumbnails().get(0).getUrl(), "large"));
      }
      String millisecStr = data.getVideo_duration();
      long milisec = Long.valueOf(millisecStr);
      holder.videoDuration.setText(Utils.timeConversion(milisec));
      Utils.loadProfilePic(mContext, holder.profileImage, data.getProfile_url());

      final int index = holderView.getAdapterPosition();
      holder.videoThumb.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          updateViewCount(data.getId());
          Utils.moveToVideoDetails(mContext, data);
        }
      });

      holder.layotMain.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          report_visibility(event, holder);
          return false;
        }
      });
      holder.videoThumb.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          report_visibility(event, holder);
          return false;
        }
      });
      holder.profileImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(mContext, postUserid);
        }
      });
      holder.userName.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(mContext, postUserid);
        }
      });
      holder.userTag.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(mContext, postUserid);
        }
      });

      holder.moreOptions.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          showPopupMenu(holder.moreOptions, index, data.getId(), postUserid);
        }
      });
    } else if (holderView instanceof LoadingViewHolder) {
      LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holderView;
      loadingViewHolder.progressBar.setIndeterminate(true);
    }
  }

  private void report_visibility(MotionEvent event, ImageViewHolder holder) {
    if (event.getAction() == MotionEvent.ACTION_DOWN
        || event.getAction() == MotionEvent.ACTION_UP
        ||
        event.getAction() == MotionEvent.ACTION_SCROLL
        || event.getAction() == MotionEvent.ACTION_CANCEL) {
    }
  }

  private void showPopupMenu(View view, final int position, final String postId,
      String postUserId) {
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
            deletePost(postId, position);
            break;

          case R.id.action_report:
            reportPost(postId, position);
            break;
        }
        return false;
      }
    });
    popupMenu.show();
  }

  private void reportPost(final String postId, final int position) {
    final Dialog d = new Dialog(mContext);
    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    d.setContentView(R.layout.confirmationpopup);
    final TextView msg = (TextView) d.findViewById(R.id.message);
    TextView name = (TextView) d.findViewById(R.id.yourname);
    TextView continueorder = (TextView) d.findViewById(R.id.confirm);
    TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
    TextView ok = (TextView) d.findViewById(R.id.confirm_ok_btn);
    LinearLayout buttonLayout = (LinearLayout) d.findViewById(R.id.button_layout);
    name.setVisibility(View.GONE);
    buttonLayout.setVisibility(View.VISIBLE);
    continueorder.setVisibility(View.VISIBLE);
    dismiss.setVisibility(View.VISIBLE);
    ok.setVisibility(View.GONE);
    msg.setText("Are you sure you want to report this post ?");
    dismiss.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        d.dismiss();
      }
    });
    continueorder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        JSONObject BroadcastObject = new JSONObject();
        try {
          BroadcastObject.put("post_id", postId);
          BroadcastObject.put("user_id", loggedInUserId);
          BroadcastObject.put("message", "");
        } catch (JSONException e) {
          e.printStackTrace();
        }
        String Url = REQUEST_REPORT_POST;
        reportDelete("OtherUser", Url, BroadcastObject, RequestConstants.REQUEST_REPORT_POST);
        postList.remove(position);
        notifyDataSetChanged();
        d.dismiss();
      }
    });
    d.show();
  }

  private void deletePost(final String postId, final int position) {
    final Dialog d = new Dialog(mContext);
    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    d.setContentView(R.layout.confirmationpopup);
    final TextView msg = (TextView) d.findViewById(R.id.message);
    TextView name = (TextView) d.findViewById(R.id.yourname);
    TextView continueorder = (TextView) d.findViewById(R.id.confirm);
    TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
    TextView ok = (TextView) d.findViewById(R.id.confirm_ok_btn);
    LinearLayout buttonLayout = (LinearLayout) d.findViewById(R.id.button_layout);
    name.setVisibility(View.GONE);
    buttonLayout.setVisibility(View.VISIBLE);
    continueorder.setVisibility(View.VISIBLE);
    dismiss.setVisibility(View.VISIBLE);
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
        JSONObject BroadcastObject = new JSONObject();
        try {
          BroadcastObject.put("post_id", postId);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        String Url = REQUEST_DELETE_YOUTHFEED;
        reportDelete("LoginUser", Url, BroadcastObject,
            RequestConstants.REQUEST_DELETE_YOUTHTUBE_POST);
        postList.remove(position);
        notifyDataSetChanged();
        d.dismiss();
      }
    });
    d.show();
  }

  private void reportDelete(String Tag, String Url, JSONObject jsonObject, String code) {
    AsyncWorker mWorker = new AsyncWorker(mContext);
    mWorker.delegate = this;
    mWorker.delegate = this;
    mWorker.execute(Url, jsonObject.toString(), RequestConstants.POST_REQUEST,
        RequestConstants.HEADER_NO, code);
  }

  private void updateViewCount(String post_id) {
    AsyncWorker mWorker = new AsyncWorker(mContext);
    mWorker.delegate = this;
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    String usr_id =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    try {
      BroadcastObject.put("post_id", post_id);
      BroadcastObject.put("user_id", usr_id);
    } catch (Exception e) {
      e.printStackTrace();
    }
    String url;
    url = ServerConnector.REQUEST_UPDATE_YOUTHTUBE_VIEWCOUNT;
    mWorker.execute(url, BroadcastObject.toString(), RequestConstants.POST_REQUEST,
        RequestConstants.HEADER_YES, RequestConstants.UPDATE_VIDEO_VIEW_COUNT);
  }

  @Override
  public void onRefresh() {

  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
  }

  private class ImageViewHolder extends RecyclerView.ViewHolder {
    private ImageView likeImage, moreOptions;
    private TextView userName, userTag, feedDesc, textReport, likeCount;
    private TextView commentsCount, viewsCount, feedTime, videoDuration;
    private ImageView profileImage, videoThumb;
    private RelativeLayout layotMain;

    public ImageViewHolder(View itemView) {
      super(itemView);
      profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
      likeImage = (ImageView) itemView.findViewById(R.id.likeImage);
      moreOptions = (ImageView) itemView.findViewById(R.id.moreOptions);
      videoThumb = (ImageView) itemView.findViewById(R.id.videoThumb);
      videoDuration = (TextView) itemView.findViewById(R.id.video_duration);
      userName = (TextView) itemView.findViewById(R.id.userName);
      userTag = (TextView) itemView.findViewById(R.id.userTag);
      feedDesc = (TextView) itemView.findViewById(R.id.feedDesc);
      textReport = (TextView) itemView.findViewById(R.id.textReport);
      likeCount = (TextView) itemView.findViewById(R.id.likeCount);
      commentsCount = (TextView) itemView.findViewById(R.id.commentsCount);
      viewsCount = (TextView) itemView.findViewById(R.id.viewsCount);
      feedTime = (TextView) itemView.findViewById(R.id.feedTime);
      layotMain = (RelativeLayout) itemView.findViewById(R.id.layotMain);

      //setFonts
      // userName.setTypeface(FontStyles.font4Profile(mContext));
      userTag.setTypeface(FontStyles.font4Profile(mContext));
      feedDesc.setTypeface(FontStyles.OpenSansLight(mContext));
    }
  }

  private class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(View view) {
      super(view);
      progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }
  }

  public interface OnScrollListener {
    void show();

    void hide();

    void onMoved(int distance);
  }
}