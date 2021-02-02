package in.tagteen.tagteen.profile;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.Adapters.ProfileVideoAdapter;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileVideoFragment extends Fragment {
  private static final String TAG = "ProfileVideoFragment";

  private RecyclerView recyclerView;
  private ProfileVideoAdapter adapter;
  private ArrayList<GetPostResponseModel.PostDetails> pvmlist = new ArrayList<>();
  private boolean apiCallBlocler = false;
  private int pagenumber = 1;
  private String userid;
  private String loggedInUserId;
  private TextView lblNoVideos;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_profile__video, container, false);
    this.recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
    this.lblNoVideos = (TextView) view.findViewById(R.id.lblNoShowroomVideos);
    if (getArguments() != null) {
      this.userid = getArguments().getString("user_id");
    }
    this.loggedInUserId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
    this.recyclerView.setLayoutManager(mLayoutManager);
    //this.recyclerView.setItemAnimator(new DefaultItemAnimator());
    this.recyclerView.setHasFixedSize(true);
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
        callapiforgetPost();
      }
    }, 1000);
  }

  void callapiforgetPost() {
    String token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
    Call<GetPostResponseModel> call = methods.getUserVideos(userid, pagenumber, 10, token);
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        apiCallBlocler = true;
        if (pvmlist != null && !pvmlist.isEmpty()) {
          pvmlist.remove(pvmlist.size() - 1);
          adapter.notifyItemRemoved(pvmlist.size());
        }
        if (pvmlist!=null&&pagenumber == 1) {
          pvmlist.clear();
        }
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> datalist =
              (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
          if (datalist == null || datalist.isEmpty()) {
            apiCallBlocler = false;
          } else {
            lblNoVideos.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Collections.sort(datalist, new Comparator<GetPostResponseModel.PostDetails>() {
              @Override
              public int compare(GetPostResponseModel.PostDetails obj1,
                  GetPostResponseModel.PostDetails obj2) {
                return obj1.getDateCreated() > obj2.getDateCreated() ? -1 : 1;
              }
            });
            pvmlist.addAll(datalist);

            if (adapter == null) {
              adapter = new ProfileVideoAdapter(getContext(), pvmlist, userid, recyclerView);
              recyclerView.setAdapter(adapter);

              adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                  if (apiCallBlocler) {
                    pvmlist.add(null);
                    adapter.notifyItemInserted(pvmlist.size() - 1);
                    apiCallBlocler = false;
                    pagenumber++;
                    callapiforgetPost();
                  }
                }
              });
            } else {
              adapter.notifyDataSetChanged();
              adapter.setLoaded();
            }
          }
          if (pvmlist == null || pvmlist.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            lblNoVideos.setVisibility(View.VISIBLE);
            if (loggedInUserId.equals(userid)) {
              lblNoVideos.setText("You don't have any showroom videos");
            } else {
              lblNoVideos.setText("No showroom videos to show");
            }
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, @NotNull Throwable t) {
        t.printStackTrace();
      }
    });
  }

  public int getTotalVideosCount() {
    if (this.pvmlist != null) {
      return this.pvmlist.size();
    }
    return 0;
  }

  public int getTotalURocksCount() {
    if (this.pvmlist != null) {
      int uRocksCount = 0;
      for (GetPostResponseModel.PostDetails data : this.pvmlist) {
        if (data != null && data.getLikeCount() != null) {
          uRocksCount += data.getLikeCount();
        }
      }
      return uRocksCount;
    }
    return 0;
  }
}
