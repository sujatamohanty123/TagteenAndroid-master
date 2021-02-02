package in.tagteen.tagteen.Adapters;

import android.app.Activity;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.PostLikesInputModel;
import in.tagteen.tagteen.Model.PostLikesModel;
import in.tagteen.tagteen.Model.RewardsInfoModels;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAccessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Activity mContext;

    private List<RewardsInfoModels.RewardsInfoModel> dataList;
    private List<RewardsInfoModels.RewardsInfoModel> allDataList;
    private Map<String, Integer> weeklyLikesCount;
    private Calendar startDate;
    private Calendar endDate;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public AdminAccessAdapter(
            Activity context,
            List<RewardsInfoModels.RewardsInfoModel> dataList,
            RecyclerView recyclerView,
            Calendar startDate,
            Calendar endDate) {
        this.mContext = context;
        this.dataList = dataList;
        this.allDataList = new ArrayList<>();
        if (dataList != null) {
            this.allDataList.addAll(dataList);
        }
        this.weeklyLikesCount = new HashMap<String, Integer>();
        this.startDate = startDate;
        this.endDate = endDate;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
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
        this.isLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void loadLikesBetweenDates(Calendar startDate, Calendar endDate) {
        if (startDate == null || endDate == null) {
            return;
        }
        this.startDate = startDate;
        this.endDate = endDate;
        this.weeklyLikesCount.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return this.dataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_rewards_row, null);
            return new ItemRowHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.load_more, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemRowHolder) {
            ItemRowHolder itemRowHolder = (ItemRowHolder) holder;
            RewardsInfoModels.RewardsInfoModel model = this.dataList.get(position);

            final RewardsInfoModels.UserInfo userInfo = model.getUserInfo();
            Utils.loadProfilePic(this.mContext, itemRowHolder.imgProfilePic, userInfo.getProfilePicUrl());
            itemRowHolder.lblPostedBy.setText(userInfo.getFirstName() + " " + userInfo.getLastName());
            itemRowHolder.lblTaggedNumber.setText(userInfo.getTaggedNumber());

            if (userInfo.getPaytm() != null) {
                itemRowHolder.lblPaymentMode.setText("Paytm");
                itemRowHolder.lblPaymentNumber.setText("ph:" + userInfo.getPaytm());
            } else if (userInfo.getGooglePay() != null) {
                itemRowHolder.lblPaymentMode.setText("G Pay");
                itemRowHolder.lblPaymentNumber.setText("ph:" + userInfo.getGooglePay());
            } else if (userInfo.getPhonePe() != null) {
                itemRowHolder.lblPaymentMode.setText("Phone Pe");
                itemRowHolder.lblPaymentNumber.setText("ph:" + userInfo.getPhonePe());
            } else {
                itemRowHolder.lblPaymentMode.setText("Payment mode");
                itemRowHolder.lblPaymentNumber.setText("No number");
            }

            List<RewardsInfoModels.PostInfo> postInfos = model.getPostInfoList();
            itemRowHolder.layoutPosts.removeAllViews();
            int totalLikes = 0;
            int weeklyCumulative = 0;
            if (postInfos != null) {
                for (RewardsInfoModels.PostInfo postInfo : postInfos) {
                    /*View view = LayoutInflater.from(mContext).inflate(R.layout.rewards_video_row, null);
                    ImageView imgPost = view.findViewById(R.id.imgPost);
                    TextView lblPostId = view.findViewById(R.id.lblPostId);
                    TextView lblLikesCount = view.findViewById(R.id.lblLikesCount);
                    lblPostId.setText(postInfo.getPostId());

                    String likesCount = "Total : " + postInfo.getLikeCount();
                    if (this.weeklyLikesCount != null && this.weeklyLikesCount.get(postInfo.getPostId()) != null) {
                        weeklyCumulative += this.weeklyLikesCount.get(postInfo.getPostId());
                        likesCount += "\nWeek : " + this.weeklyLikesCount.get(postInfo.getPostId());
                    } else {
                        likesCount += "\nWeek : " + "...";
                        // load from server
                        getWeeklyLikes(postInfo.getPostId(), position);
                    }
                    lblLikesCount.setText(likesCount);

                    itemRowHolder.layoutPosts.addView(view);*/
                    totalLikes += postInfo.getLikeCount();
                }
            }

            itemRowHolder.lblTotalLikes.setText("Grand total likes : " + totalLikes);
            //itemRowHolder.lblLikesByWeek.setText("Current week :" + weeklyCumulative);
            itemRowHolder.lblLikesByWeek.setVisibility(View.GONE);

            // events
            itemRowHolder.imgProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.gotoProfile(mContext, userInfo.getUserId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.dataList.size();
    }

    private void getWeeklyLikes(final String postId, final int position) {
        if (this.startDate == null || this.endDate == null) {
            return;
        }
        PostLikesInputModel inputModel = new PostLikesInputModel();
        inputModel.setFromDate(String.valueOf(this.startDate.getTime().getTime()));
        inputModel.setToDate(String.valueOf(this.endDate.getTime().getTime()));
        inputModel.setPostId(postId);
        Apimethods methods = API_Call_Retrofit.getretrofit(mContext).create(Apimethods.class);
        Call<PostLikesModel> call = methods.getPostVideoCountByDate(inputModel);
        call.enqueue(new Callback<PostLikesModel>() {
            @Override
            public void onResponse(Call<PostLikesModel> call, Response<PostLikesModel> response) {
                int statuscode = response.code();
                if (statuscode == 200) {
                    PostLikesModel responseModel = response.body();
                    if (responseModel != null && responseModel.getData() != null) {
                        weeklyLikesCount.put(postId, responseModel.getData().getCount());
                        notifyItemChanged(position);
                    }
                }
            }

            @Override
            public void onFailure(Call<PostLikesModel> call, Throwable t) {

            }
        });
    }

    private class ItemRowHolder extends RecyclerView.ViewHolder {
        private ImageButton imgProfilePic;
        private TextView lblPostedBy;
        private TextView lblTaggedNumber;
        private TextView lblPaymentMode;
        private TextView lblPaymentNumber;
        private TextView lblTotalLikes;
        private TextView lblLikesByWeek;
        private LinearLayout layoutPosts;

        public ItemRowHolder(View view) {
            super(view);

            this.imgProfilePic = view.findViewById(R.id.imgUserProfile);
            this.lblPostedBy = view.findViewById(R.id.lblPostedBy);
            this.lblTaggedNumber = view.findViewById(R.id.lblTaggedNumber);
            this.lblPaymentMode = view.findViewById(R.id.lblPaymentMode);
            this.lblPaymentNumber = view.findViewById(R.id.lblPaymentNumber);
            this.lblTotalLikes = view.findViewById(R.id.lblTotalLikes);
            this.lblLikesByWeek = view.findViewById(R.id.lblLikesByWeek);
            this.layoutPosts = view.findViewById(R.id.layoutPosts);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    public void filter(String filter) {
        if (this.allDataList == null) {
            return;
        }
        this.dataList.clear();
        if (filter == null || filter.trim().length() == 0) {
            this.dataList.addAll(this.allDataList);
        } else {
            filter = filter.toLowerCase();
            for (RewardsInfoModels.RewardsInfoModel model : this.allDataList) {
                String firstName = model.getUserInfo().getFirstName().toLowerCase();
                String lastName = model.getUserInfo().getLastName().toLowerCase();
                if (firstName.contains(filter) || lastName.contains(filter)) {
                    this.dataList.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }
}
