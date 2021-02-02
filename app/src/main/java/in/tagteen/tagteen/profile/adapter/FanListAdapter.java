package in.tagteen.tagteen.profile.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.AntonyChanges;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetFanList;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.profile.UnfllowDialog;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;

/**
 * Created by lovekushvishwakarma on 10/10/17.
 */

@AntonyChanges
public class FanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private ArrayList<GetFanList.UserData> fanlist;
  private Context mContext;
  private boolean isLike = false;
  private String otherUserId;
  String login_usr_id, flag;

  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  private OnLoadMoreListener onLoadMoreListener;
  private boolean isLoading;
  private int visibleThreshold = 5;
  private int lastVisibleItem, totalItemCount;

  public FanListAdapter(ArrayList<GetFanList.UserData> fanlist, Context mContext, String flag,
      String otherUserId, RecyclerView recyclerView) {
    this.fanlist = fanlist;
    this.mContext = mContext;
    this.flag = flag;
    this.otherUserId = otherUserId;
    login_usr_id =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    final LinearLayoutManager linearLayoutManager =
        (LinearLayoutManager) recyclerView.getLayoutManager();
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
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
      }
    });
  }

  public void setLoaded() {
    isLoading = false;
  }

  public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
    this.onLoadMoreListener = onLoadMoreListener;
  }

  @Override
  public int getItemViewType(int position) {
    return this.fanlist.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
      return new ImageViewHolder(
          LayoutInflater.from(this.mContext).inflate(R.layout.fan_list_item, parent, false));
    } else if (viewType == VIEW_TYPE_LOADING) {
      View view = LayoutInflater.from(mContext).inflate(R.layout.load_more, parent, false);
      return new LoadingViewHolder(view);
    }
    return null;
  }

  @Override
  public int getItemCount() {
    return fanlist.size();
  }

  @AntonyChanges
  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holderView, final int position) {
    if (holderView instanceof ImageViewHolder) {
      ImageViewHolder holder = (ImageViewHolder) holderView;
      final GetFanList.UserData userData = fanlist.get(position);
      if (userData.is_myfan()) {
        holder.folowUnfollow.setText("Supporting");
        holder.folowUnfollow.setBackgroundResource(R.drawable.fnn_select);
        holder.folowUnfollow.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
      } else {
        holder.folowUnfollow.setText("Support");
        holder.folowUnfollow.setBackgroundResource(R.drawable.fnn_unselect);
        holder.folowUnfollow.setTextColor(mContext.getResources().getColor(R.color.white));
      }

      holder.folowUnfollow.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          new UnfllowDialog((Activity) mContext, userData, FanListAdapter.this, position);
        }
      });

      Utils.loadProfilePic(mContext, holder.imageProfilePic, userData.getProfile_url());
      holder.texttag.setText(userData.getTagged_number() + "");
      holder.textname.setText(userData.getFirst_name() + " " + userData.getLast_name());

      if (userData.getUser_id().equalsIgnoreCase(login_usr_id)) {
        holder.folowUnfollow.setVisibility(View.GONE);
      } else if (otherUserId.equals(login_usr_id) == false) {
        holder.folowUnfollow.setVisibility(View.GONE);
      } else {
        holder.folowUnfollow.setVisibility(View.VISIBLE);
      }

      holder.imageProfilePic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(mContext, userData.getUser_id());
        }
      });
      holder.textname.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(mContext, userData.getUser_id());
        }
      });
      holder.texttag.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(mContext, userData.getUser_id());
        }
      });
    }
  }

  private class ImageViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageProfilePic;
    private TextView folowUnfollow, texttag, textname;

    public ImageViewHolder(View itemView) {
      super(itemView);
      imageProfilePic = (ImageView) itemView.findViewById(R.id.imageProfilePic);
      folowUnfollow = (TextView) itemView.findViewById(R.id.folowUnfollow);
      folowUnfollow.setVisibility(View.GONE);
      texttag = (TextView) itemView.findViewById(R.id.texttag);
      textname = (TextView) itemView.findViewById(R.id.textname);
    }
  }

  private class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(View view) {
      super(view);
      progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }
  }

  public void updateAdapter(GetFanList.UserData userData, int pos) {
    try {
      fanlist.set(pos, userData);
      notifyDataSetChanged();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
