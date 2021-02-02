package in.tagteen.tagteen.Fragments.youthtube;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.SpacesItemDecoration;
import in.tagteen.tagteen.widgets.AsymmetricRecyclerView;
import in.tagteen.tagteen.widgets.AsymmetricRecyclerViewAdapter;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendingFragment extends Fragment {
    private AsymmetricRecyclerView recyclerViewTrending;
    //private SwipeRefreshLayout swipeRefreshLayout;

    private TrendingAdapter trendingAdapter;

    private ArrayList<GetPostResponseModel.PostDetails> postlist;
    private int pageLimit = 20;
    private String userId;

    public TrendingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        this.initWidgets(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.loadData();
    }

    private void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getTrendingVideos();
            }
        }, 2000);
    }

    private void initWidgets(View view) {
        this.userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

        //this.swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        //this.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        this.recyclerViewTrending = view.findViewById(R.id.recyclerViewTrending);
        this.recyclerViewTrending.setRequestedColumnCount(3);
        this.recyclerViewTrending.setRequestedHorizontalSpacing(Utils.getPxFromDp(getActivity(), 3));
        int spacing = Utils.getPxFromDp(getActivity(), 3);
        this.recyclerViewTrending.addItemDecoration(new SpacesItemDecoration(spacing));

        this.postlist = new ArrayList<>();
    }

    private void getTrendingVideos() {
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<GetPostResponseModel> call = methods.getTopVideos(this.userId, this.pageLimit);
        //Call<GetPostResponseModel> call = methods.getTrendingVideos(this.userId);
        call.enqueue(new Callback<GetPostResponseModel>() {
            @Override
            public void onResponse(Call<GetPostResponseModel> call, Response<GetPostResponseModel> response) {
                int statuscode = response.code();
                if (statuscode == 200) {
                    GetPostResponseModel responseModel = response.body();
                    ArrayList<GetPostResponseModel.PostDetails> responseData =
                            (ArrayList<GetPostResponseModel.PostDetails>) responseModel.getData();
                    if (responseData != null) {
                        for (GetPostResponseModel.PostDetails postData : responseData) {
                            postlist.add(postData);
                        }
                    }
                    trendingAdapter = new TrendingAdapter(getActivity(), postlist);
                    recyclerViewTrending.setAdapter(trendingAdapter);
                }
            }

            @Override
            public void onFailure(Call<GetPostResponseModel> call, Throwable t) {

            }
        });
    }
}
