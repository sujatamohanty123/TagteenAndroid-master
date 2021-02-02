package in.tagteen.tagteen.Fragments.youthtube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.Fragments.youthtube.adapter.PopularVideosAdapter;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopularVideosFragment extends Fragment {
  private boolean mScreenLoadedOnce = false;
  private RecyclerView recyclerTrendingVideos;
  private RecyclerView recyclerTechVideos;
  private RecyclerView recyclerGamingVideos;
  private RecyclerView recyclerMusicalVideos;
  private RecyclerView recyclerDancingVideos;
  private RecyclerView recyclerFunnyVideos;
  private RecyclerView recyclerActingVideos;
  private RecyclerView recyclerEducationVideos;

  private int pageLimit = 30;
  private String userId;

  Apimethods methods;

  public PopularVideosFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_popular, container, false);
    initComponent();
    this.initWidgets(view);

    return view;
  }

  private void initComponent() {
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

  private void initWidgets(View view) {
    this.recyclerTrendingVideos = view.findViewById(R.id.recyclerTrendingVideos);
    this.recyclerTrendingVideos.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerTechVideos = view.findViewById(R.id.recyclerTechVideos);
    this.recyclerTechVideos.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerGamingVideos = view.findViewById(R.id.recyclerGamingVideos);
    this.recyclerGamingVideos.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerMusicalVideos = view.findViewById(R.id.recyclerMusicalVideos);
    this.recyclerMusicalVideos.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerDancingVideos = view.findViewById(R.id.recyclerDancingVideos);
    this.recyclerDancingVideos.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerFunnyVideos = view.findViewById(R.id.recyclerFunnyVideos);
    this.recyclerFunnyVideos.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerActingVideos = view.findViewById(R.id.recyclerActingVideos);
    this.recyclerActingVideos.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerEducationVideos = view.findViewById(R.id.recyclerEducationVideos);
    this.recyclerEducationVideos.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.userId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
  }

  private void loadData() {
    getTrendingVideos();
    getTechVideos();
    getGamingVideos();
    getMusicalVideos();
    getDancingVideos();
    getFunnyVideos();
    getActingVideos();
    getEducationVideos();
    //new Handler().postDelayed(new Runnable() {
    //    @Override
    //    public void run() {
    //        getTrendingVideos();
    //        getTechVideos();
    //        getGamingVideos();
    //        getMusicalVideos();
    //        getDancingVideos();
    //        getFunnyVideos();
    //        getActingVideos();
    //        getEducationVideos();
    //    }
    //}, 2000);
  }

  private void getTrendingVideos() {
    Call<GetPostResponseModel> call = methods.getTopVideos(this.userId, this.pageLimit);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            PopularVideosAdapter adapter = new PopularVideosAdapter(getContext(), responseData);
            recyclerTrendingVideos.setAdapter(adapter);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {

      }
    });
  }

  private void getTechVideos() {
    //Call<GetPostResponseModel> call = methods.getRelatedVideoPost(1, 1, pageLimit, userId, token);
    Call<GetPostResponseModel> call = methods.getCategoryVideos(this.userId, this.pageLimit, 6, 1);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            PopularVideosAdapter adapter = new PopularVideosAdapter(getContext(), responseData);
            recyclerTechVideos.setAdapter(adapter);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {

      }
    });
  }

  private void getGamingVideos() {
    Call<GetPostResponseModel> call = methods.getCategoryVideos(this.userId, this.pageLimit, 13, 1);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            PopularVideosAdapter adapter = new PopularVideosAdapter(getContext(), responseData);
            recyclerGamingVideos.setAdapter(adapter);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {

      }
    });
  }

  private void getMusicalVideos() {
    Call<GetPostResponseModel> call = methods.getCategoryVideos(this.userId, this.pageLimit, 21, 1);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            PopularVideosAdapter adapter = new PopularVideosAdapter(getContext(), responseData);
            recyclerMusicalVideos.setAdapter(adapter);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {

      }
    });
  }

  private void getDancingVideos() {
    Call<GetPostResponseModel> call = methods.getCategoryVideos(this.userId, this.pageLimit, 8, 1);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            PopularVideosAdapter adapter = new PopularVideosAdapter(getContext(), responseData);
            recyclerDancingVideos.setAdapter(adapter);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {

      }
    });
  }

  private void getFunnyVideos() {

    Call<GetPostResponseModel> call = methods.getCategoryVideos(this.userId, this.pageLimit, 12, 1);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            PopularVideosAdapter adapter = new PopularVideosAdapter(getContext(), responseData);
            recyclerFunnyVideos.setAdapter(adapter);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {

      }
    });
  }

  private void getActingVideos() {
    Call<GetPostResponseModel> call = methods.getCategoryVideos(this.userId, this.pageLimit, 1, 1);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            PopularVideosAdapter adapter = new PopularVideosAdapter(getContext(), responseData);
            recyclerActingVideos.setAdapter(adapter);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {

      }
    });
  }

  private void getEducationVideos() {
    Call<GetPostResponseModel> call = methods.getCategoryVideos(this.userId, this.pageLimit, 28, 1);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            PopularVideosAdapter adapter = new PopularVideosAdapter(getContext(), responseData);
            recyclerEducationVideos.setAdapter(adapter);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {

      }
    });
  }
}
