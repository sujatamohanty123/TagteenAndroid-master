package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import in.tagteen.tagteen.DataCache;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.FontStyles;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_DELETE_YOUTHFEED;
import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_REPORT_POST;

public class LatestShowroomVideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements AsyncResponse {

  private ArrayList<GetPostResponseModel.PostDetails> youthList;
  private Context mContext;
  private int select_caregory_id = 0;

  private int selectedIndex = -1;
  private OnCallbackListener onCallbackListener;
  private OnScrollListener onScrollListener;

  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  private OnLoadMoreListener onLoadMoreListener;
  private boolean isLoading;
  private int loadedPageNumber;
  private int visibleThreshold = 5;
  private int lastVisibleItem, totalItemCount;
  private final String loggedInUserId =
      SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

  private int scrollDist = 0;
  private boolean isVisible = true;
  private final int MINIMUM = 25;
  private int toolbarOffset = 0;
  private int toolbarHeight;

  public LatestShowroomVideosAdapter(
      ArrayList<GetPostResponseModel.PostDetails> youthList, final Context mContext,
      int select_caregory_id, RecyclerView recyclerView) {
    this.youthList = youthList;
    this.mContext = mContext;
    this.select_caregory_id = select_caregory_id;
    this.toolbarHeight = Utils.getPxFromDp(mContext, 85);

    final StaggeredGridLayoutManager layoutManager =
        (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        clipToolbarOffset();
        moved(toolbarOffset);
        if ((toolbarOffset < toolbarHeight && dy > 0) || (toolbarOffset > 0 && dy < 0)) {
          toolbarOffset += dy;
        }

        if (layoutManager != null) {
          totalItemCount = layoutManager.getItemCount();
        }
        int[] lastVisibleItems = layoutManager.findLastVisibleItemPositions(null);
        if (lastVisibleItems != null && lastVisibleItems.length > 0) {
          lastVisibleItem = lastVisibleItems[lastVisibleItems.length - 1];
        }
        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
          isLoading = true;
          if (onLoadMoreListener != null) {
            new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                onLoadMoreListener.onLoadMore();
              }
            }, 200);
          }
        }
      }
    });
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

  public void setLoadedPageNumber(int loadedPageNumber) {
    this.loadedPageNumber = loadedPageNumber;
  }

  @Override
  public int getItemViewType(int position) {
    return this.youthList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  @NotNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
      return new ImageViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.latest_video_row, parent, false));
    } else if (viewType == VIEW_TYPE_LOADING) {
      View view = LayoutInflater.from(mContext).inflate(R.layout.load_more, parent, false);
      return new LoadingViewHolder(view);
    }
    return null;
  }

  @Override
  public int getItemCount() {
    return youthList == null ? 0 : youthList.size();
  }

  @Override
  public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holderView, final int position) {
    if (holderView instanceof ImageViewHolder) {
      final ImageViewHolder holder = (ImageViewHolder) holderView;
      final GetPostResponseModel.PostDetails data = youthList.get(position);
      final String postUserid = data.getPostCreatorId();
      holder.userName.setText(data.getFirst_name() + " " + data.getLast_name());
      String viewCount = data.getView_count() + " Views";
      holder.userTag.setText(viewCount);

      //holder.feedTime.setText("" + Utils.getRelativeTime(data.getDateCreated()));
      if (data.getVideoThumbnails() != null && !data.getVideoThumbnails().isEmpty()) {
        Utils.loadImageUsingGlide(this.mContext, holder.videoThumb,
            UrlUtils.getUpdatedImageUrl(data.getVideoThumbnails().get(0).getUrl(), "large"));
      }
      String millisecStr = data.getVideo_duration();
      long milisec = Long.parseLong(millisecStr);
      holder.videoDuration.setText(Utils.timeConversion(milisec));
      Utils.loadProfilePic(mContext, holder.profileImage, data.getProfile_url());

      if (data.getContent() != null) {
        holder.lblVideoDesc.setText(Html.fromHtml(data.getContent()));
      }

      //Uncomment below lines to set video title

      //if (!GeneralApiUtils.isStringEmpty(data.getTitle())) {
      //  holder.txtTitle.setText(data.getTitle());
      //  holder.txtTitle.setVisibility(View.VISIBLE);
      //} else {
      //  holder.txtTitle.setVisibility(View.GONE);
      //}

      final int index = holderView.getAdapterPosition();
      holder.videoThumb.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          selectedIndex = index;
          List<GetPostResponseModel.PostDetails> subList =
                  youthList.subList(position + 1, youthList.size());
          DataCache.getInstance().setPostlist(subList, true);
          DataCache.getInstance().setPageLoaded(loadedPageNumber);

          Utils.moveToVideoDetails(mContext, data);
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
    } else if (holderView instanceof LoadingViewHolder) {
      LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holderView;
      loadingViewHolder.progressBar.setIndeterminate(true);
    }
  }

  private void report_visibility(MotionEvent event, ImageViewHolder holder) {
    if (event.getAction() == MotionEvent.ACTION_DOWN
        || event.getAction() == MotionEvent.ACTION_UP
        || event.getAction() == MotionEvent.ACTION_SCROLL
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
    Objects.requireNonNull(d.getWindow())
        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        youthList.remove(position);
        notifyDataSetChanged();
        d.dismiss();
      }
    });
    d.show();
  }

  private void deletePost(final String postId, final int position) {
    final Dialog d = new Dialog(mContext);
    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
    Objects.requireNonNull(d.getWindow())
        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        youthList.remove(position);
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

  @Override
  public void onRefresh() {

  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
  }

  private class ImageViewHolder extends RecyclerView.ViewHolder {
    private TextView userName, userTag, videoDuration;
    private ImageView profileImage, videoThumb;
    private TextView lblVideoDesc;
    //private TextView txtTitle;

    public ImageViewHolder(View itemView) {
      super(itemView);
      profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
      videoThumb = (ImageView) itemView.findViewById(R.id.videoThumb);
      videoDuration = (TextView) itemView.findViewById(R.id.video_duration);
      userName = (TextView) itemView.findViewById(R.id.lblUserName);
      userTag = (TextView) itemView.findViewById(R.id.lblTagNumber);
      this.lblVideoDesc = itemView.findViewById(R.id.lblVideoDesc);
      //this.txtTitle = itemView.findViewById(R.id.txt_title);

      //setFonts
      // userName.setTypeface(FontStyles.font4Profile(mContext));
      userTag.setTypeface(FontStyles.font4Profile(mContext));
    }
  }

  private static class LoadingViewHolder extends RecyclerView.ViewHolder {
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
