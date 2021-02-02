package in.tagteen.tagteen.Fragments.youthtube;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import in.tagteen.tagteen.ConnectivityReceiver;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoriesAdapter;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.Fragments.youthtube.adapter.YouthFeedMainAdapter;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AppBarScrollHelper;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.github.tcking.giraffecompressor.GiraffeCompressor;
//import net.ypresto.androidtranscoder.MediaTranscoder;
//import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;
//import app.frantic.kplcompressor.KplCompressor;

public class YouthTubeFeed extends BaseFragment
    implements AsyncResponse, ConnectivityReceiver.ConnectivityReceiverListener {
  private boolean mScreenLoadedOnce = false;
  private boolean apiCallBlocler = false;
  private int pagenumber_YouthTubeFeed;
  private int pageLimit = 10;
  private int pageNumberBound = 150;
  private int serverCallCount = 1;

  public static CategoriesAdapter categoryAdapter;
  public static ArrayList<CategoryBean> categoryList = new ArrayList<>();
  private RecyclerView recyviewCategories;

  private YouthFeedMainAdapter youthAdapterMain;
  private RecyclerView recyclerView;
  private String userId;
  private boolean isLoadingData = false;

  private SwipeRefreshLayout simpleSwipeRefreshLayout;

  private ArrayList<GetPostResponseModel.PostDetails> youthList = new ArrayList<>();
  private int select_caregory_id = 0;
  private TextView no_internet, welcome_msg;
  private RelativeLayout buzz_main_container;
  private LinearLayoutManager gridLayoutManager;
  private LinearLayout layoutMain;
  private ShimmerFrameLayout shimmerFrameLayout;
  private boolean isTaggedUser = SharedPreferenceSingleton.getInstance()
      .getBoolPreference(RegistrationConstants.IS_TAGGED_USER);

  private int categoriesHeight;
  private boolean resetVideosListView = false;
  private AppBarLayout mAppBarLayout;
  Apimethods methods;

  public static YouthTubeFeed newInstance() {
    return new YouthTubeFeed();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_showroom, container, false);
    methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    layoutMain = view.findViewById(R.id.frame_relative);
    mAppBarLayout = view.findViewById(R.id.app_bar);
    buzz_main_container = (RelativeLayout) view.findViewById(R.id.buzz_main_container);
    no_internet = (TextView) view.findViewById(R.id.no_internet);
    welcome_msg = (TextView) view.findViewById(R.id.welcome_msg);
    shimmerFrameLayout = (ShimmerFrameLayout) view.findViewById(R.id.shimmerLayout);
    checkConnection();
    simpleSwipeRefreshLayout =
        (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
    simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

    recyviewCategories = (RecyclerView) view.findViewById(R.id.recyclerviewCategories);
    recyviewCategories.setHasFixedSize(true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(
        getContext(), LinearLayoutManager.HORIZONTAL, false);
    recyviewCategories.setLayoutManager(layoutManager);
    //this.recyviewCategories.setVisibility(View.VISIBLE);
    this.categoriesHeight = Utils.getPxFromDp(getContext(), 85);

    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    gridLayoutManager = new LinearLayoutManager(
        getContext(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(gridLayoutManager);

    userId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    this.hideCategories();
    this.bindEvents();
    AppBarScrollHelper.init(simpleSwipeRefreshLayout, mAppBarLayout, recyclerView);
    return view;
  }

  private void bindEvents() {
    this.simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        pullToRefresh(false);
      }
    });
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    this.pagenumber_YouthTubeFeed = Utils.getRandomNumberForShowroom(this.pageNumberBound);
    //this.loadData();
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser && !mScreenLoadedOnce) {
      loadData();
      mScreenLoadedOnce = true;
    }
    // do the rest of the code here.

  }

  private void loadData() {
    callapiforgetPost();
    getAllCategories();
    //new Handler().postDelayed(new Runnable() {
    //  @Override
    //  public void run() {
    //    callapiforgetPost();
    //    getAllCategories();
    //  }
    //}, 2000);
  }

  private void checkConnection() {
    boolean isConnected = ConnectivityReceiver.isConnected();
    showSnack(isConnected);
  }

  private void showSnack(boolean isConnected) {

    if (isConnected) {
      layoutMain.setVisibility(View.VISIBLE);
      // shimmerFrameLayout.startShimmerAnimation();
      shimmerFrameLayout.stopShimmerAnimation();
      buzz_main_container.setVisibility(View.VISIBLE);
      no_internet.setVisibility(View.GONE);
      if (isTaggedUser) {
        welcome_msg.setVisibility(View.VISIBLE);
      } else {
        welcome_msg.setVisibility(View.GONE);
      }
    } else {
      buzz_main_container.setVisibility(View.GONE);
      no_internet.setVisibility(View.VISIBLE);
    }
  }

  public void pullToRefresh(boolean isVideoAdded) {
    this.simpleSwipeRefreshLayout.setRefreshing(true);
    if (this.select_caregory_id == 0) {
      if (isVideoAdded == false) {
        this.pagenumber_YouthTubeFeed = Utils.getRandomNumberForShowroom(this.pageNumberBound);
      } else {
        this.pagenumber_YouthTubeFeed = 1;
      }
      //this.pagenumber_YouthTubeFeed = 1;
    } else {
      this.pagenumber_YouthTubeFeed = 1;
    }
    this.serverCallCount = 1;
    this.apiCallBlocler = true;
    this.callapiforgetPost();
  }

  private void callapiforgetPost() {
    String token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);

    Call<GetPostResponseModel> call;
    if (select_caregory_id == 0) {
      call = methods.getAllShowroomPostRanked(userId, pagenumber_YouthTubeFeed, pageLimit, 3, token);
    } else {
      call = methods.getRelatedVideoPost(select_caregory_id, pagenumber_YouthTubeFeed, pageLimit,
          userId, token);
    }

    //API_Call_Retrofit.methodCalled("showroom : " + call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        apiCallBlocler = true;
        if (serverCallCount > 1 && youthList != null && !youthList.isEmpty()) {
          youthList.remove(youthList.size() - 1);
          youthAdapterMain.notifyItemRemoved(youthList.size());
        }
        if (serverCallCount == 1) {
          youthList.clear();
        }
        simpleSwipeRefreshLayout.setRefreshing(false);
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
          }
          if (responseData != null) {
            if (responseData.isEmpty()) {
              apiCallBlocler = false;
            } else {
              youthList.addAll(responseData);
            }
          }

          if (youthAdapterMain == null) {
            youthAdapterMain =
                new YouthFeedMainAdapter(youthList, getContext(), select_caregory_id,
                    recyclerView);
            //PagerSnapHelper linearSnapHelper = new LinearSnapHelperOneByOne();
            //linearSnapHelper.attachToRecyclerView(recyclerView);
            recyclerView.setAdapter(youthAdapterMain);

            youthAdapterMain.setOnLoadMoreListener(new OnLoadMoreListener() {
              @Override
              public void onLoadMore() {
                if (apiCallBlocler && !isLoadingData) {
                  youthList.add(null);
                  youthAdapterMain.notifyItemInserted(youthList.size() - 1);
                  apiCallBlocler = false;
                  if (select_caregory_id == 0) {
                    pagenumber_YouthTubeFeed = Utils.getRandomNumberForShowroom(pageNumberBound);
                  } else {
                    pagenumber_YouthTubeFeed++;
                  }
                  serverCallCount++;
                  callapiforgetPost();
                }
              }
            });
            youthAdapterMain.setOnScrollListener(new YouthFeedMainAdapter.OnScrollListener() {
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
                //moveBanner(distance);
              }
            });
            youthAdapterMain.setOnCallbackListener(new OnCallbackListener() {
              @Override
              public void OnComplete() {
                //pullToRefresh();
              }
            });
          } else {
            youthAdapterMain.notifyDataSetChanged();
            youthAdapterMain.setLoaded();
          }
          isLoadingData = false;

          shimmerFrameLayout.stopShimmerAnimation();
          shimmerFrameLayout.setVisibility(View.GONE);
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {
        simpleSwipeRefreshLayout.setRefreshing(false);
      }
    });
  }

  private void moveBanner(int distance) {
    //Utils.showShortToast(getActivity(), "Distance : " + distance);
        /*this.recyviewCategories.setTranslationY(-distance);
        this.recyclerView.setTranslationY(-distance);
        //this.layoutMain.forceLayout();
        //this.recyclerView.requestLayout();
        if (distance > 30 && this.resetVideosListView == false) {
            ViewGroup.LayoutParams params = this.recyclerView.getLayoutParams();
            params.height = this.recyclerView.getHeight() + this.categoriesHeight;
            this.recyclerView.setLayoutParams(params);
            this.resetVideosListView = true;
        } else if (distance == 0 && this.resetVideosListView == true) {
            ViewGroup.LayoutParams params = this.recyclerView.getLayoutParams();
            params.height = this.recyclerView.getHeight() - this.categoriesHeight;
            this.recyclerView.setLayoutParams(params);
            this.resetVideosListView = false;
        }*/
  }

  public void showCategories() {
    this.recyviewCategories.setVisibility(View.VISIBLE);
  }

  public void hideCategories() {
    this.recyviewCategories.setVisibility(View.GONE);
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Constants.COMMENT_REQUEST_CODE) {
      if (this.youthAdapterMain != null && data != null &&
          data.getSerializableExtra(Constants.SELECTED_MODEL) != null) {

      }
    } else if (requestCode == Constants.VIDEO_FULLSCREEN_CODE) {
      if (youthAdapterMain != null && data != null) {
        youthAdapterMain.resumeVideoPosition(
            data.getLongExtra(Constants.VIDEO_CURRENT_POSITION, 0));
      }
    }
  }

  @Override
  public void onRefresh() {

  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    try {
      JSONObject data = new JSONObject(output);
      if (REQUEST_NUMBER.equalsIgnoreCase(RequestConstants.REQUEST_DELETE_YOUTHTUBE_POST)) {
        if (data.getBoolean("success")) {
          Utils.showToast(getContext(), data.getString("message"));
        }
      } else if (REQUEST_NUMBER.equalsIgnoreCase(RequestConstants.REQUEST_GET_CATEGORIES)) {
        if (data.getBoolean("success")) {
          categoryList.clear();
          JSONArray dataArr = data.getJSONArray("data");
          categoryList.add(new CategoryBean(0, "All"));
          for (int i = 0; i < dataArr.length(); i++) {
            JSONObject object = dataArr.getJSONObject(i);
            String categoryName = object.getString(Constants.CATEGORY_NAME_PARAM);
            int categoryId = object.getInt(Constants.CATEGORY_ID_PARAM);
            categoryList.add(new CategoryBean(categoryId, categoryName));
          }
          categoryAdapter = new CategoriesAdapter(categoryList, getContext(), "single");
          recyviewCategories.setAdapter(categoryAdapter);
          categoryAdapter.setOnItemClickListener(new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int clickedposition) {
              select_caregory_id = clickedposition;
              pagenumber_YouthTubeFeed = 1;
              serverCallCount = 1;
              youthList.clear();
              callapiforgetPost();
            }
          });
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void deleteFeed(int position) {
    if (ConnectivityReceiver.isConnected() == false) {
      Utils.showShortToast(this.getActivity(), getString(R.string.no_connection));
      return;
    }
    AsyncWorker mWorker = new AsyncWorker(getContext());
    mWorker.delegate = this;
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    try {
      BroadcastObject.put("post_id", youthList.get(position).getId());
      youthList.remove(position);
      youthAdapterMain.notifyDataSetChanged();
    } catch (Exception e) {
      e.printStackTrace();
    }
    mWorker.execute(
        ServerConnector.REQUEST_DELETE_YOUTHFEED,
        BroadcastObject.toString(),
        RequestConstants.POST_REQUEST,
        RequestConstants.HEADER_YES,
        RequestConstants.REQUEST_DELETE_YOUTHTUBE_POST);
  }

  private void getAllCategories() {
    if (getActivity() == null) {
      return;
    }
    AsyncWorker mWorker = new AsyncWorker(getContext());
    mWorker.delegate = this;
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    mWorker.execute(
        ServerConnector.REQUEST_GET_ALL_CATEGORIES,
        BroadcastObject.toString(),
        RequestConstants.GET_REQUEST,
        RequestConstants.HEADER_YES,
        RequestConstants.REQUEST_GET_CATEGORIES);
  }

  @Override
  public void onNetworkConnectionChanged(boolean isConnected) {
    if (this.getActivity() == null) {
      return;
    }
    showSnack(isConnected);
  }
}