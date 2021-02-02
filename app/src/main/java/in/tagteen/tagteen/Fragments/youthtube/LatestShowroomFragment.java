package in.tagteen.tagteen.Fragments.youthtube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;

import in.tagteen.tagteen.DataCache;
import in.tagteen.tagteen.Fragments.VideoDataSender1;
import in.tagteen.tagteen.Fragments.youthtube.adapter.LatestShowroomVideosAdapter;
import in.tagteen.tagteen.Fragments.youthtube.adapter.TrendingHomeAdapter;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.WebshowModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.backgroundUpload.UploadService;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.compression.KplCompressor;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AppBarScrollHelper;
import in.tagteen.tagteen.utils.GridSpaceItemDecoration;
import in.tagteen.tagteen.utils.YouthtubeNupLoad;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.tagteen.tagteen.backgroundUpload.UploadService.POST_TYPE;
import static in.tagteen.tagteen.backgroundUpload.UploadService.ProgressDone;
import static in.tagteen.tagteen.backgroundUpload.UploadService.ShowRoomVideoUpload;

/**
 * A simple {@link Fragment} subclass.
 */
public class LatestShowroomFragment extends BaseFragment {
  //private LatestUsersAdapter usersAdapter;
  //private ArrayList<UserInfoBean> usersList;
  //private RecyclerView recyclerViewUsersList;

  private LatestShowroomVideosAdapter latestShowroomVideosAdapter;
  private RecyclerView recyclerViewLatestPosts;
  private String userId;
  private boolean isLoadingData = false;
  private int categoriesHeight;
  private boolean resetVideosListView = false;

  private int pageNumber = 1;
  private int pageLimit = 10;
  private int pageNumberBound = 150;
  private int serverCallCount = 1;
  private boolean stopLoadingDataFromServer = false;
  private boolean apiCallBlocler = false;

  private ProgressBar uploadProgress;
  private ProgressBar progressPreparingVideo;
  private TextView textProgress;
  private RelativeLayout layoutProgress;
  private VideoDataSender1 videoDataSender;
  private int uploadedProgress;
  private ImageView imgCancelUpload;
  private YouthtubeNupLoad youthtubeNupLoad;
  private int uploadingPostId = -1;
  private RecyclerView recyclerTrendingVideos;

  private ShimmerFrameLayout shimmerFrameLayout;
  private SwipeRefreshLayout swipeRefreshLayout;
  private AppBarLayout mAppBarLayout;
  private static final int LIVESHOWS_ID = 4;
  private WebshowModel.WebshowDetails mWebshowDetails;
  private CardView containerTrending;
  private ArrayList<GetPostResponseModel.PostDetails> postList = new ArrayList<>();
  TrendingHomeAdapter mTrendingHomeAdapter;
  private List<GetPostResponseModel.PostDetails> mtrendingVideoList;
  private ArrayList<WebshowModel.WebshowDetails> liveshows;

  private boolean isTaggedUser = SharedPreferenceSingleton.getInstance()
      .getBoolPreference(RegistrationConstants.IS_TAGGED_USER);

  public LatestShowroomFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_latest_showroom_new, container, false);
    mtrendingVideoList = new ArrayList<>();
    this.liveshows = new ArrayList<>();
    this.initWidgets(view);
    this.bindWidgets();

    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    this.pageNumber = Utils.getRandomNumberForShowroom(this.pageNumberBound);
    this.loadData();
  }

  private void loadData() {
    this.loadPostsFromServer();
    this.getUpcomingLiveShows();
    this.getTrendingVideos();

    //new Handler().postDelayed(new Runnable() {
    //  @Override
    //  public void run() {
    //    loadPostsFromServer();
    //    //getAllNewUsersList();
    //    getTrendingVideos();
    //  }
    //}, 500);
  }

  private void initWidgets(View view) {
    this.shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
    this.swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    mAppBarLayout = view.findViewById(R.id.app_bar);
    containerTrending = view.findViewById(R.id.container_trending);
    this.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
    //this.recyclerViewUsersList = view.findViewById(R.id.recyclerviewNewies);
    //this.recyclerViewUsersList.setHasFixedSize(true);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    //this.recyclerViewUsersList.setLayoutManager(layoutManager);
    //this.recyclerViewUsersList.setVisibility(View.VISIBLE);
    this.categoriesHeight = Utils.getPxFromDp(getContext(), 85);

    this.uploadProgress = view.findViewById(R.id.progressBar);
    this.progressPreparingVideo = view.findViewById(R.id.progressPreparingVideo);
    this.textProgress = view.findViewById(R.id.textPer);
    this.imgCancelUpload = view.findViewById(R.id.imgCancelUpload);
    this.layoutProgress = view.findViewById(R.id.layoutProgress);
    this.layoutProgress.setVisibility(View.GONE);
    this.videoDataSender = new VideoDataSender1();

    this.recyclerViewLatestPosts = view.findViewById(R.id.recyclerViewPosts);
    this.recyclerViewLatestPosts.setLayoutManager(
        new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
    int spacing = Utils.getPxFromDp(getContext(), 8);
    this.recyclerViewLatestPosts.addItemDecoration(new GridSpaceItemDecoration(2, spacing, true));

    this.userId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    this.recyclerTrendingVideos = view.findViewById(R.id.recyclerTrendingVideos);
    this.recyclerTrendingVideos.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    mTrendingHomeAdapter = new TrendingHomeAdapter(getContext());
    this.recyclerTrendingVideos.setAdapter(mTrendingHomeAdapter);
  }

  private void bindWidgets() {
    this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        pullToRefresh();
      }
    });
    this.textProgress.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (uploadingPostId != -1) {
          if (textProgress.getText().equals("Retry")) {
            retryVideoUpload();
          } else {
            //AWSUtility.mTransferUtility.pause(uploadingPostId);
            //textProgress.setText("Retry");
          }
        }
      }
    });
    this.imgCancelUpload.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (uploadingPostId != -1) {
          layoutProgress.setVisibility(View.GONE);
          AWSUtility.getTransferUtility().cancel(uploadingPostId);
        }
      }
    });
    AppBarScrollHelper.init(swipeRefreshLayout, mAppBarLayout, recyclerViewLatestPosts);
  }

  public void pullToRefresh() {
    this.uploadingPostId = -1;
    this.swipeRefreshLayout.setRefreshing(true);
    this.serverCallCount = 1;
    this.pageNumber = Utils.getRandomNumberForShowroom(this.pageNumberBound);
    this.apiCallBlocler = true;
    this.liveshows.clear();
    this.loadPostsFromServer();
    //this.getAllNewUsersList();
    this.getUpcomingLiveShows();
    this.getTrendingVideos();
  }

  public void setUploadedProgress(int progress) {
    if (this.uploadedProgress > progress) {
      return;
    }
    this.uploadedProgress = progress;
    this.textProgress.setText(progress + "%");
    this.uploadProgress.setProgress(progress);
  }

  public void setUploadingPostId(int uploadingPostId) {
    this.uploadingPostId = uploadingPostId;
    //this.imgCancelUpload.setVisibility(View.VISIBLE);
  }

  public void onVideoUploadFailed(int id) {
    this.uploadingPostId = id;
    this.textProgress.setText("Retry");
  }

  private void retryVideoUpload() {
    if (AWSUtility.getTransferUtility() != null) {
      if (Utils.isNetworkAvailable(Objects.requireNonNull(getContext()))) {
        Utils.showShortToast(getContext(), "Resuming upload");
        TransferObserver mUploadMediaAwsObserver =
            AWSUtility.getTransferUtility().resume(this.uploadingPostId);
        textProgress.setText(uploadedProgress + "%");
        if (mUploadMediaAwsObserver != null) {
          //this.youthtubeNupLoad.resumeVideoUpload(mUploadMediaAwsObserver);
        }
        this.uploadingPostId = -1;
      } else {
        Utils.showShortToast(getContext(), "Please check your internet connection");
      }
    }
  }

  // handler for received Intents for the "my-event" event
  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      if (Objects.requireNonNull(intent.getStringExtra("from")).equalsIgnoreCase("showrooms")
          && Objects
          .requireNonNull(intent.getStringExtra(
              "result"))
          .equalsIgnoreCase(ProgressDone)) {
        pullToRefresh();
      }
    }
  };

  @Override
  public void onResume() {
    super.onResume();
    // Register mMessageReceiver to receive messages.
    LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()))
        .registerReceiver(mMessageReceiver, new IntentFilter("my-event"));
    if (getActivity() != null) {
      this.reloadData();
    }
  }

  @Override
  public void onPause() {
    LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()))
        .unregisterReceiver(mMessageReceiver);
    super.onPause();
  }

  private void reloadData() {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        try {
          if (videoDataSender != null && videoDataSender.isCall()) {
            SharedPreferenceSingleton.getInstance()
                .writeBoolPreference(Constants.IS_SHOWROOW_UPLOAD_IN_PROGRESS, true);
            videoDataSender.setIsCall(false);

            //                        progressPreparingVideo.setVisibility(View.VISIBLE);
            //                        uploadedProgress = 0;
            //                        uploadProgress.setProgress(0);
            //                        textProgress.setText("0%");
            //                        textProgress.setTextColor(getResources().getColor(R.color.white));
            //                        layoutProgress.setVisibility(View.VISIBLE);
            //
            //                        long fileSizeInMB = Utils.getFileSizeInMB(videoDataSender.getVideoPath());
            //                        if (fileSizeInMB > Constants.MAX_SIZE_RAW_VIDEO_UPLOAD) {
            //                            Utils.showToast(getActivity(), "Preparing to upload...");
            //                            new VideoCompressAsyncTask().execute();
            //                        } else {
            //                            progressPreparingVideo.setVisibility(View.GONE);
            //                            textProgress.setTextColor(getResources().getColor(R.color.colorAccent));
            //                            youthtubeNupLoad = new YouthtubeNupLoad(getActivity(), layoutProgress, uploadProgress, textProgress, LatestShowroomFragment.this);
            //                        }

            Intent serviceIntent = new Intent(getActivity(), UploadService.class);
            serviceIntent.putExtra(POST_TYPE, ShowRoomVideoUpload);
            ContextCompat.startForegroundService(Objects.requireNonNull(getActivity()),
                serviceIntent);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }, 200);
  }

  private void loadPostsFromServer() {
    String token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    Apimethods methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    Call<GetPostResponseModel> call =
        methods.getAllShowroomPostRanked(this.userId, this.pageNumber, this.pageLimit, 3, token);

    API_Call_Retrofit.methodCalled("Latest: " + call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        apiCallBlocler = true;
        if (serverCallCount > 1 && postList != null && !postList.isEmpty()) {
          postList.remove(postList.size() - 1);
          latestShowroomVideosAdapter.notifyItemRemoved(postList.size());
        }
        if (serverCallCount == 1) {
          postList.clear();
        }
        swipeRefreshLayout.setRefreshing(false);
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
          int addedPosition = postList.size() - 1;
          int addedPostCount = 0;
          if (responseData != null) {
            if (responseData.isEmpty()) {
              apiCallBlocler = false;
            } else {
              for (GetPostResponseModel.PostDetails postData : responseData) {
                postList.add(postData);
                addedPostCount++;
                /*if (Utils.isLatestPost(postData.getDateCreated())) {
                  postList.add(postData);
                  addedPostCount++;
                } else {
                  apiCallBlocler = false;
                  break;
                }*/
              }
            }
          }

          if (latestShowroomVideosAdapter == null) {
            latestShowroomVideosAdapter =
                new LatestShowroomVideosAdapter(postList, getContext(), 0, recyclerViewLatestPosts);
            recyclerViewLatestPosts.setAdapter(latestShowroomVideosAdapter);

            latestShowroomVideosAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
              @Override
              public void onLoadMore() {
                if (apiCallBlocler && isLoadingData == false) {
                  postList.add(null);
                  latestShowroomVideosAdapter.notifyItemInserted(postList.size() - 1);
                  apiCallBlocler = false;
                  pageNumber = Utils.getRandomNumberForShowroom(pageNumberBound);
                  serverCallCount++;
                  loadPostsFromServer();
                }
              }
            });
            latestShowroomVideosAdapter.setOnScrollListener(
                new LatestShowroomVideosAdapter.OnScrollListener() {
                  @Override
                  public void show() {
                    //showCategories();
                  }

                  @Override
                  public void hide() {
                    //hideCategories();
                  }

                  @Override
                  public void onMoved(int distance) {
                    //moveCategories(distance);
                  }
                });
            latestShowroomVideosAdapter.setOnCallbackListener(new OnCallbackListener() {
              @Override
              public void OnComplete() {
                //pullToRefresh();
              }
            });
          } else {
            if (serverCallCount == 1) {
              latestShowroomVideosAdapter.notifyDataSetChanged();
            } else {
              if (addedPosition < 0) {
                addedPosition = 0;
              }
              if (addedPostCount > 0) {
                latestShowroomVideosAdapter.notifyItemRangeChanged(addedPosition, addedPostCount);
              }
            }
            latestShowroomVideosAdapter.setLoaded();
          }
          latestShowroomVideosAdapter.setLoadedPageNumber(pageNumber);
          isLoadingData = false;

          shimmerFrameLayout.stopShimmerAnimation();
          shimmerFrameLayout.setVisibility(View.GONE);
        }
      }

      @Override
      public void onFailure(Call<GetPostResponseModel> call, Throwable t) {

      }
    });
  }

  private void moveCategories(int distance) {
    this.containerTrending.setTranslationY(-distance);
    this.recyclerViewLatestPosts.setTranslationY(-distance);
    if (distance > 30 && this.resetVideosListView == false) {
      ViewGroup.LayoutParams params = this.recyclerViewLatestPosts.getLayoutParams();
      params.height = this.recyclerViewLatestPosts.getHeight() + this.categoriesHeight;
      this.recyclerViewLatestPosts.setLayoutParams(params);
      this.resetVideosListView = true;
    } else if (distance == 0 && this.resetVideosListView == true) {
      ViewGroup.LayoutParams params = this.recyclerViewLatestPosts.getLayoutParams();
      params.height = this.recyclerViewLatestPosts.getHeight() - this.categoriesHeight;
      this.recyclerViewLatestPosts.setLayoutParams(params);
      this.resetVideosListView = false;
    }
  }

  //private void getAllNewUsersList() {
  //    if (this.usersList == null) {
  //        this.usersList = new ArrayList<UserInfoBean>();
  //    }
  //
  //    Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
  //    Call<FriendSeach> call = methods.getLatestVerifiedUsers(30);
  //
  //    API_Call_Retrofit.methodCalled(call.request().url().toString());
  //    call.enqueue(new Callback<FriendSeach>() {
  //        @Override
  //        public void onResponse(Call<FriendSeach> call, Response<FriendSeach> response) {
  //            int statuscode = response.code();
  //            usersList.clear();
  //            if (statuscode == 200) {
  //                FriendSeach responseModel = response.body();
  //                ArrayList<FriendSeach.UserInfo> responseData = responseModel.getUserInfos();
  //                if (responseData != null) {
  //                    if (!responseData.isEmpty()) {
  //                        for (FriendSeach.UserInfo userData : responseData) {
  //                            String userName = userData.getFirst_name() + " " + userData.getLast_name();
  //                            String userId = userData.get_id();
  //                            UserInfoBean user = new UserInfoBean(userId, userName);
  //                            user.setProfileUrl(userData.getProfile_url());
  //                            usersList.add(user);
  //                        }
  //                    }
  //
  //                    usersAdapter = new LatestUsersAdapter(getActivity(), usersList);
  //                    recyclerViewUsersList.setAdapter(usersAdapter);
  //                }
  //            }
  //        }
  //
  //        @Override
  //        public void onFailure(Call<FriendSeach> call, Throwable t) {
  //
  //        }
  //    });
  //}

  private void getTrendingVideos() {
    Apimethods methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    String userId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    Call<GetPostResponseModel> call = methods.getTrendingVideos(userId);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(Call<GetPostResponseModel> call,
          Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          if (getresponsemodel != null) {
            ArrayList<GetPostResponseModel.PostDetails> responseData =
                    (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            mtrendingVideoList.clear();
            //if (mWebshowDetails != null) {
            //  GetPostResponseModel.PostDetails webShow = new GetPostResponseModel.PostDetails();
            //  List<GetPostResponseModel.PostDetails.Image> mImages = new ArrayList<>(1);
            //  GetPostResponseModel.PostDetails.Image imageData = webShow.new Image();
            //  imageData.setUrl(mWebshowDetails.getVideoThumbnailUrl());
            //  webShow.setContent(mWebshowDetails.getTitle());
            //  webShow.setVideoThumbnails(mImages);
            //  webShow.setWebShow(true);
            //  mtrendingVideoList.add(webShow);
            //}
            mtrendingVideoList.addAll(responseData);
            mTrendingHomeAdapter.setData(mtrendingVideoList);
            mTrendingHomeAdapter.addLiveShows(liveshows);
          }
        }
      }

      @Override
      public void onFailure(Call<GetPostResponseModel> call, Throwable t) {

      }
    });
  }

  private void getUpcomingLiveShows() {
    Apimethods methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    Call<WebshowModel> call =
        methods.getWebshowVideos(1, 3, LIVESHOWS_ID, this.userId);

    call.enqueue(new Callback<WebshowModel>() {
      @Override
      public void onResponse(Call<WebshowModel> call, Response<WebshowModel> response) {

        int statuscode = response.code();
        if (statuscode == 200) {
          WebshowModel getresponsemodel = response.body();
          ArrayList<WebshowModel.WebshowDetails> responseData =
              (ArrayList<WebshowModel.WebshowDetails>) getresponsemodel.getData();
          if (responseData != null && !responseData.isEmpty()) {
            for (WebshowModel.WebshowDetails data : responseData) {
              Date showTime = Utils.getDateTimeFromString(data.getWebshowDate());
              Date currentTime = new Date();
              if (currentTime.after(showTime)) {
                liveshows.add(data);
              }
            }
          }
        }
      }

      @Override
      public void onFailure(Call<WebshowModel> call, Throwable t) {
        mWebshowDetails = null;
        //mTrendingHomeAdapter.setData(mtrendingVideoList);
      }
    });
  }
}
