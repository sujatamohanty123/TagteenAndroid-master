package in.tagteen.tagteen.Fragments;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import in.tagteen.tagteen.Adapters.TeenFeedAdapter;
import in.tagteen.tagteen.ConnectivityReceiver;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.utils.GridSpaceItemDecoration;
import in.tagteen.tagteen.utils.TeenFeedUpload;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;


public class TeenFeedFragment extends Fragment implements AsyncResponse,ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = "TeenFeedFragment";

    private StaggeredGridLayoutManager mLayoutManager;
    private ArrayList<SectionDataModel> allSampleData = new ArrayList<SectionDataModel>();
    private SwipeRefreshLayout simpleSwipeRefreshLayout;
    private RelativeLayout layoutProgress;
    private ProgressBar progressBar;
    private TextView textPer;
    private TeenFeedAdapter adapter;
    private boolean apiCallBlocler = false;
    private TextView no_internet;
    private LinearLayout buzz_main_container;
    private RecyclerView recyclerView;
    private int page = 1, pagelimit = 10;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout linear_main;

    private boolean isLoadingData = false;

    public TeenFeedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_teen_feed, container, false);

        linear_main=(LinearLayout) rootView.findViewById(R.id.frame_relative);
        buzz_main_container=(LinearLayout) rootView.findViewById(R.id.buzz_main_container);
        no_internet=(TextView) rootView.findViewById(R.id.no_internet);
        shimmerFrameLayout = (ShimmerFrameLayout) rootView.findViewById(R.id.shimmerLayout);
        checkConnection();

        layoutProgress = (RelativeLayout)rootView.findViewById(R.id.layoutProgress);
        layoutProgress.setVisibility(View.GONE);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        textPer =(TextView)rootView.findViewById(R.id.textPer);

        simpleSwipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.simpleSwipeRefreshLayout);
        simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);
        mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        int spacing = Utils.getPxFromDp(getActivity(), 5);
        this.recyclerView.addItemDecoration(new GridSpaceItemDecoration(2, spacing, true));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callapiforgetPost();
            }
        }, 500);

        simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh();
            }
        });
        return rootView;
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            //shimmerFrameLayout.startShimmerAnimation();
            shimmerFrameLayout.stopShimmerAnimation();
            buzz_main_container.setVisibility(View.VISIBLE);
            no_internet.setVisibility(View.GONE);
        } else {
            buzz_main_container.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {

    }

    public  void pullToRefresh(){
        this.simpleSwipeRefreshLayout.setRefreshing(true);
        this.page = 1;
        this.isLoadingData = true;
        this.callapiforgetPost();
    }

    private void postDraftArticle() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TagteenApplication.getInstance().setConnectivityListener(TeenFeedFragment.this);
                if (TeenFeedUpload.iscall){
                    progressBar.setProgress(0);
                    new TeenFeedUpload(getActivity(), layoutProgress, progressBar, textPer);
                }
            }
        }, 500);
    }
    @Override
    public void onResume() {
        super.onResume();
        this.postDraftArticle();
    }

    private void callapiforgetPost() {
        String userid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<GetPostResponseModel> call = methods.get_teenfeed_articlePost(userid, page, pagelimit, token);

        call.enqueue(new Callback<GetPostResponseModel>() {
            @Override
            public void onResponse(Call<GetPostResponseModel> call, Response<GetPostResponseModel> response) {
                int statuscode = response.code();
                apiCallBlocler = true;
                if (page > 1 && allSampleData != null && allSampleData.size() > 0) {
                    allSampleData.remove(allSampleData.size() - 1);
                    adapter.notifyItemRemoved(allSampleData.size());
                }
                if (page == 1) {
                    allSampleData.clear();
                }
                simpleSwipeRefreshLayout.setRefreshing(false);
                if (statuscode == 200) {
                    GetPostResponseModel getresponsemodel = response.body();
                    ArrayList<GetPostResponseModel.PostDetails> getdatalist = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
                    if (getdatalist.isEmpty()) {
                        apiCallBlocler = false;
                    }
                    int addedPosition = allSampleData.size() - 1;
                    for (int i = 0; i < getdatalist.size(); i++) {
                        if (!getdatalist.get(i).getFirst_name().equalsIgnoreCase("")) {
                            SectionDataModel section = new SectionDataModel();
                            section.setPostid(getdatalist.get(i).getId());
                            section.setPost_userid(getdatalist.get(i).getPostCreatorId());
                            section.setPost_creator_name(getdatalist.get(i).getFirst_name() + " " + getdatalist.get(i).getLast_name());
                            section.setPosted_creator_tagged_number(getdatalist.get(i).getTagged_number());
                            section.setPost_creator_profilepic(getdatalist.get(i).getProfile_url());
                            section.setPost_created_date_time(getdatalist.get(i).getDateCreated());
                            section.setCategory(getdatalist.get(i).getCategorie_name());
                            //////for imagelist////////////////////////////////
                            List<String> imagepathlist = new ArrayList<String>();
                            for (int j = 0; j < getdatalist.get(i).getImage().size(); j++) {
                                //String desc=getdatalist.get(i).getImage().get(j).getUrl();
                                // section.setPost_image_createdby_creator(getdatalist.get(i).getImage().get(j).getUrl());
                                imagepathlist.add(getdatalist.get(i).getImage().get(j).getUrl());
                                section.setPost_image_createdby_creator_url(imagepathlist);
                            }

                            section.setUserLike(getdatalist.get(i).getUserLike());
                            section.setCoolcount(getdatalist.get(i).getCoolCount());
                            section.setLikecount(getdatalist.get(i).getLikeCount());
                            section.setCommentcount(getdatalist.get(i).getConversationCount());

                            String desc = getdatalist.get(i).getContent();
                            section.setText_description(getdatalist.get(i).getContent());
                            section.setViewCount(getdatalist.get(i).getView_count());

                            allSampleData.add(section);
                        }
                    }
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    linear_main.setVisibility(View.VISIBLE);
                    if (adapter == null) {
                        adapter = new TeenFeedAdapter(getActivity(), allSampleData, recyclerView);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                if (apiCallBlocler && isLoadingData == false) {
                                    allSampleData.add(null);
                                    adapter.notifyItemInserted(allSampleData.size() - 1);
                                    apiCallBlocler = false;
                                    callapiforgetPost();
                                }
                            }
                        });
                    } else {
                        if (page == 1) {
                            adapter.notifyDataSetChanged();
                        } else {
                            if (addedPosition < 0) {
                                addedPosition = 0;
                            }
                            if (getdatalist.size() > 0) {
                                adapter.notifyItemRangeChanged(addedPosition, getdatalist.size());
                            }
                        }
                        adapter.setLoaded();
                    }
                    isLoadingData = false;
                    page++;
                }

                if (statuscode == 401) {
                    Log.d("url", "url=" + call.request().url().toString());
                }
            }

            @Override
            public void onFailure(Call<GetPostResponseModel> call, Throwable t) {
                Log.d(TAG, " failed = " + call.request().url().toString());
                simpleSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
