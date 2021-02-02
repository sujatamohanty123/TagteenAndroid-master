package in.tagteen.tagteen.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.DataCache;
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
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class ProfileVideoAdapter extends RecyclerView.Adapter<ProfileVideoAdapter.MyViewHolder>
        implements AsyncResponse {

    private ArrayList<GetPostResponseModel.PostDetails> pvmlist;
    private Context context;
    private String userId;
    private int selectedPosition;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView desc, name, viewcount, like, time, videoducation;
        public ImageView image, profilepic;
        RelativeLayout relative1;
        public ImageView moreOption;

        public MyViewHolder(View view) {
            super(view);
            desc = (TextView) view.findViewById(R.id.videoDesc);
            name = (TextView) view.findViewById(R.id.txtname);
            moreOption = (ImageView) view.findViewById(R.id.moreoption);
            viewcount = (TextView) view.findViewById(R.id.textViewCount);
            like = (TextView) view.findViewById(R.id.textRocks);
            time = (TextView) view.findViewById(R.id.textTime);
            videoducation = (TextView) view.findViewById(R.id.videoducation);
            image = (ImageView) view.findViewById(R.id.iconId);
            profilepic = (ImageView) view.findViewById(R.id.imagePic);
            relative1 = (RelativeLayout) view.findViewById(R.id.relative1);
        }
    }

    public ProfileVideoAdapter(Context context, ArrayList<GetPostResponseModel.PostDetails> pvmlist,
                               String userId, RecyclerView recyclerView) {
        this.context = context;
        this.pvmlist = pvmlist;
        this.userId = userId;

        final LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
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
                            }, 200);
                        }
                        isLoading = true;
                    }
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

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_profile_video_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {
        final GetPostResponseModel.PostDetails data = pvmlist.get(position);
        if (data == null) {
            return;
        }
        final String login_usr_id =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        if (data.getContent() != null) {
            holder.desc.setText(data.getContent());
        }
        Picasso.with(this.context)
                .load(UrlUtils.getUpdatedImageUrl(data.getVideoThumbnails().get(0).getUrl(), "large"))
                .into(holder.image);
        Utils.loadProfilePic(context, holder.profilepic, data.getProfile_url());
        holder.name.setText(data.getFirst_name() + " " + data.getLast_name());
        holder.like.setText("" + data.getLikeCount());
        holder.viewcount.setText("" + data.getView_count() + " Views");
        holder.time.setText("" + Utils.getRelativeTime(data.getDateCreated()));
        holder.relative1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<GetPostResponseModel.PostDetails> subList =
                        pvmlist.subList(position + 1, pvmlist.size());
                DataCache.getInstance().setPostlist(subList, false);

                Utils.moveToVideoDetails(context, data);
            }
        });
        int vid_duration_millisec = Integer.parseInt(data.getVideo_duration());
        holder.videoducation.setText(Utils.timeConversion(vid_duration_millisec));

        if (this.userId.equals(login_usr_id) == false) {
            holder.moreOption.setVisibility(View.GONE);
        }
        holder.moreOption.setTag(true);
        holder.moreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.moreOption, data.getId(), position);
            }
        });
    }

    private void showPopupMenu(View view, final String postId, final int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();
        inflater.inflate(R.menu.popup_menu_delete, menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        selectedPosition = position;
                        deleteFeed(postId);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void deleteFeed(String postId) {
        AsyncWorker mWorker = new AsyncWorker(context);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        try {
            BroadcastObject.put("post_id", postId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWorker.execute(ServerConnector.REQUEST_DELETE_YOUTHFEED, BroadcastObject.toString(),
                RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES,
                RequestConstants.REQUEST_DELETE_YOUTHTUBE_POST);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        if (REQUEST_NUMBER == RequestConstants.REQUEST_DELETE_YOUTHTUBE_POST) {
            if (this.pvmlist != null && this.pvmlist.isEmpty() == false &&
                    this.selectedPosition >= 0 && this.selectedPosition < this.pvmlist.size()) {
                this.pvmlist.remove(this.selectedPosition);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return pvmlist.size();
    }
}