package in.tagteen.tagteen.Fragments.youthtube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import in.tagteen.tagteen.Fragments.youthtube.adapter.RockstarAdapter;
import in.tagteen.tagteen.Model.FriendSeach;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.base.BaseFragment;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RockstarsFragment extends BaseFragment {
  //private RecyclerView recyclerTrendingVideos;
  private boolean mScreenLoadedOnce = false;
  private RecyclerView recyclerViewRockstars;
  private SwipeRefreshLayout swipeRefreshLayout;

  private ArrayList<FriendSeach.UserInfo> rockstarList;
  private RockstarAdapter rockstarAdapter;
  Apimethods methods;

  public RockstarsFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_rockstars, container, false);

    this.initWidgets(view);
    initComponent();
    this.bindEvents();

    return view;
  }

  private void initComponent() {
    if (this.rockstarList == null) {
      this.rockstarList = new ArrayList<FriendSeach.UserInfo>();
    }
    rockstarAdapter = new RockstarAdapter(getContext(), rockstarList);
    methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
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
    getAllRockStars();
    //new Handler().postDelayed(new Runnable() {
    //    @Override
    //    public void run() {
    //        //getTrendingVideos();
    //        getAllRockStars();
    //    }
    //}, 500);
  }

  private void initWidgets(View view) {
    //this.recyclerTrendingVideos = view.findViewById(R.id.recyclerTrendingVideos);
    //this.recyclerTrendingVideos.setLayoutManager(
    //        new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerViewRockstars = view.findViewById(R.id.recyclerViewRockstars);
    this.recyclerViewRockstars.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

    this.swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
  }

  private void bindEvents() {
    this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        pullToRefresh();
      }
    });
  }

  private void pullToRefresh() {
    this.swipeRefreshLayout.setRefreshing(true);
    getAllRockStars();
  }

  private void getAllRockStars() {
    //if (getActivity() == null) {
    //  return;
    //}
    rockstarList.clear();
    showProgressDialog();
    Call<FriendSeach> call = methods.getAllRockstars();

    //API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<FriendSeach>() {
      @Override
      public void onResponse(@NotNull Call<FriendSeach> call,
          @NotNull Response<FriendSeach> response) {
        dismissProgressDialog();
        int statuscode = response.code();
        if (statuscode == 200) {
          FriendSeach responseModel = response.body();
          ArrayList<FriendSeach.UserInfo> usersList =
              null;
          if (responseModel != null) {
            usersList = (ArrayList<FriendSeach.UserInfo>) responseModel.getUserInfos();
          }

          if (usersList != null) {
            rockstarList.addAll(usersList);
          }
          recyclerViewRockstars.setAdapter(rockstarAdapter);
        }
      }

      @Override
      public void onFailure(@NotNull Call<FriendSeach> call, @NotNull Throwable t) {
        dismissProgressDialog();
        showErrorDialog(t.getLocalizedMessage());
      }
    });
  }

  //private void getTrendingVideos() {
  //    Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
  //    String userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
  //    Call<GetPostResponseModel> call = methods.getTrendingVideos(userId);
  //
  //    API_Call_Retrofit.methodCalled(call.request().url().toString());
  //    call.enqueue(new Callback<GetPostResponseModel>() {
  //        @Override
  //        public void onResponse(Call<GetPostResponseModel> call, Response<GetPostResponseModel> response) {
  //            swipeRefreshLayout.setRefreshing(false);
  //            int statuscode = response.code();
  //            if (statuscode == 200) {
  //                GetPostResponseModel getresponsemodel = response.body();
  //                ArrayList<GetPostResponseModel.PostDetails> responseData =
  //                        (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
  //
  //                PopularVideosAdapter adapter = new PopularVideosAdapter(getActivity(), responseData);
  //                recyclerTrendingVideos.setAdapter(adapter);
  //            }
  //        }
  //
  //        @Override
  //        public void onFailure(Call<GetPostResponseModel> call, Throwable t) {
  //            swipeRefreshLayout.setRefreshing(false);
  //        }
  //    });
  //}
}
