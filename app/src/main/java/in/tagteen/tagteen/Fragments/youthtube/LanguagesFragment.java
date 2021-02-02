package in.tagteen.tagteen.Fragments.youthtube;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import in.tagteen.tagteen.Fragments.youthtube.adapter.LanguageVideoAdapter;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.Language;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguagesFragment extends BaseFragment {
  private boolean mScreenLoadedOnce = false;
  private RecyclerView recyclerViewLanguages;
  private RecyclerView recyclerViewPosts;
  private SwipeRefreshLayout swipeRefreshLayout;
  private TextView lblNoVideos;

  private LanguageVideoAdapter videoAdapter;

  private int selectedLanguageIndex = 0;
  private int selectedLanguageId = 3; // English
  private int pageNumber = 1;
  private int pageLimit = 10;
  private String userId;
  private boolean apiCallBlocker;
  private boolean isLoadingData = false;

  private int headerHeight;
  private boolean resetVideosListView = false;

  private List<GetPostResponseModel.PostDetails> postList;

  Apimethods methods;

  public LanguagesFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_languages, container, false);
    initComponent();
    this.initWidgets(view);
    this.bindEvents();

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

  private void loadData() {
    getLanguageVideos();
    //new Handler().postDelayed(new Runnable() {
    //  @Override
    //  public void run() {
    //    getLanguageVideos();
    //  }
    //}, 1000);
  }

  private void initWidgets(View view) {
    this.headerHeight = Utils.getPxFromDp(getActivity(), 45);

    this.recyclerViewLanguages = view.findViewById(R.id.recyclerviewLanguages);
    this.recyclerViewLanguages.setHasFixedSize(true);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(
            getActivity(), LinearLayoutManager.HORIZONTAL, false);
    this.recyclerViewLanguages.setLayoutManager(layoutManager);

    this.recyclerViewPosts = view.findViewById(R.id.recyclerViewLanguagePosts);
    LinearLayoutManager postLayoutManager =
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    this.recyclerViewPosts.setLayoutManager(postLayoutManager);

    this.swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    this.lblNoVideos = view.findViewById(R.id.lblNoVideos);

    List<Language> languagesList = Utils.getLanguages();

    LanguagesAdapter adapter = new LanguagesAdapter(languagesList);
    this.recyclerViewLanguages.setAdapter(adapter);

    this.postList = new ArrayList<>();
    this.userId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
  }

  private void bindEvents() {
    this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        pullToRefresh();
      }
    });
  }

  class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.ViewHolder> {
    private List<Language> languagesList;

    public LanguagesAdapter(List<Language> languagesList) {
      this.languagesList = languagesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
      View view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.language_row, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
      viewHolder.lblLanguage.setText(this.languagesList.get(position).getLanguage());
      if (selectedLanguageIndex == position) {
        viewHolder.lblLanguage.setBackgroundTintList(
            getResources().getColorStateList(R.color.colorPrimary));
        viewHolder.lblLanguage.setTextColor(getResources().getColor(R.color.white));
      } else {
        viewHolder.lblLanguage.setBackgroundTintList(
            getResources().getColorStateList(R.color.white));
        viewHolder.lblLanguage.setTextColor(getResources().getColor(R.color.full_black));
      }

      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          selectedLanguageIndex = position;
          selectedLanguageId = languagesList.get(position).getId();
          notifyDataSetChanged();
          pullToRefresh();
        }
      });
    }

    @Override
    public int getItemCount() {
      return this.languagesList != null ? this.languagesList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      private TextView lblLanguage;

      public ViewHolder(@NonNull View itemView) {
        super(itemView);

        this.lblLanguage = itemView.findViewById(R.id.lblLanguage);
      }
    }
  }

  private void pullToRefresh() {
    this.pageNumber = 1;
    this.swipeRefreshLayout.setRefreshing(true);
    getLanguageVideos();
  }

  private void getLanguageVideos() {
    String token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    if (pageNumber == 1 && !swipeRefreshLayout.isRefreshing()) {
      showProgressDialog();
    }
    Call<GetPostResponseModel> call = methods.getShowroomVideosByLanguage(
        this.userId, this.pageNumber, this.pageLimit, 3, this.selectedLanguageId, token);

    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        if (pageNumber == 1 && !swipeRefreshLayout.isRefreshing()) {
          dismissProgressDialog();
        }
        swipeRefreshLayout.setRefreshing(false);
        int statuscode = response.code();
        apiCallBlocker = true;
        if (postList != null && !postList.isEmpty()) {
          postList.remove(postList.size() - 1);
          videoAdapter.notifyItemRemoved(postList.size());
        }
        if (pageNumber == 1) {
          assert postList != null;
          postList.clear();
        }

        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
          }
          if (responseData != null) {
            if (responseData.isEmpty()) {
              apiCallBlocker = false;
            } else {
              postList.addAll(responseData);
            }
          }

          if (postList != null && postList.isEmpty()) {
            lblNoVideos.setVisibility(View.VISIBLE);
            recyclerViewPosts.setVisibility(View.GONE);
          } else {
            lblNoVideos.setVisibility(View.GONE);
            recyclerViewPosts.setVisibility(View.VISIBLE);
          }

          if (videoAdapter == null) {
            videoAdapter = new LanguageVideoAdapter(getActivity(), postList, recyclerViewPosts);
            recyclerViewPosts.setAdapter(videoAdapter);

            videoAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
              @Override
              public void onLoadMore() {
                if (apiCallBlocker && !isLoadingData) {
                  postList.add(null);
                  videoAdapter.notifyItemInserted(postList.size() - 1);
                  apiCallBlocker = false;
                  pageNumber++;
                  getLanguageVideos();
                }
              }
            });
            videoAdapter.setOnScrollListener(new LanguageVideoAdapter.OnScrollListener() {
              @Override
              public void show() {
                // do nothing
              }

              @Override
              public void hide() {
                // do nothing
              }

              @Override
              public void onMoved(int distance) {
                moveHeader(distance);
              }
            });
          } else {
            videoAdapter.notifyDataSetChanged();
            videoAdapter.setLoaded();
          }
          isLoadingData = false;
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {
        Log.d("Languages", t.getMessage());
        if (pageNumber == 1 && !swipeRefreshLayout.isRefreshing()) {
          dismissProgressDialog();
        }
        swipeRefreshLayout.setRefreshing(false);
      }
    });
  }

  private void moveHeader(int distance) {
    this.recyclerViewLanguages.setTranslationY(-distance);
    this.recyclerViewPosts.setTranslationY(-distance);
    if (distance > 30 && this.resetVideosListView == false) {
      ViewGroup.LayoutParams params = this.recyclerViewPosts.getLayoutParams();
      params.height = this.recyclerViewPosts.getHeight() + this.headerHeight;
      this.recyclerViewPosts.setLayoutParams(params);
      this.resetVideosListView = true;
    } else if (distance == 0 && this.resetVideosListView == true) {
      ViewGroup.LayoutParams params = this.recyclerViewPosts.getLayoutParams();
      params.height = this.recyclerViewPosts.getHeight() - this.headerHeight;
      this.recyclerViewPosts.setLayoutParams(params);
      this.resetVideosListView = false;
    }
  }
}
