package in.tagteen.tagteen.Fragments.youthtube;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.Fragments.youthtube.adapter.SupportingVideosAdapter;
import in.tagteen.tagteen.Fragments.youthtube.bean.UserInfoBean;
import in.tagteen.tagteen.Model.GetFanList;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupportingShowroomFragment extends AppCompatActivity {
  private boolean mScreenLoadedOnce = false;
  private LinearLayout layoutContainer;
  private LinearLayout layoutProgress;
  private TextView lblNoSupporting;

  private int pageLimit = 10;
  private int pageNumber = 1;
  private String userId;

  private ArrayList<UserInfoBean> fansList;
  private boolean apiCallBlocker;

  Apimethods methods;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_supporting_showroom);

    this.initWidgets();
    this.initComponent();
  }

  /*@Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_supporting_showroom, container, false);

    this.initWidgets(view);
    initComponent();
    return view;
  }*/

  private void initComponent() {
    methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
  }

  /*@Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser && !mScreenLoadedOnce) {
      loadData();
      mScreenLoadedOnce = true;
    }
    // do the rest of the code here.

  }*/

  private void loadData() {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        getSupportingsList();
      }
    }, 500);
  }

  private void initWidgets() {
    this.layoutContainer = findViewById(R.id.layoutContainer);
    this.layoutProgress = findViewById(R.id.layoutProgress);
    this.layoutProgress.setVisibility(View.VISIBLE);
    this.lblNoSupporting = findViewById(R.id.lblNoSupporting);
    this.lblNoSupporting.setVisibility(View.GONE);

    this.fansList = new ArrayList<UserInfoBean>();
    this.userId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    this.loadData();
  }

  private void getSupportingsList() {
    String token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    //showProgressDialog();
    Call<GetFanList> call =
        methods.getMyFanList(this.userId, this.pageNumber, this.pageLimit, token);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetFanList>() {
      @Override
      public void onResponse(@NotNull Call<GetFanList> call,
          @NotNull Response<GetFanList> response) {
        disableProgress();
        apiCallBlocker = true;
        int statuscode = response.code();
        if (statuscode == 200) {
          pageNumber++;
          GetFanList getresponsemodel = response.body();
          ArrayList<GetFanList.UserData> dataList =
              null;
          if (getresponsemodel != null) {
            dataList = (ArrayList<GetFanList.UserData>) getresponsemodel.getData();
          }
          if (dataList == null || dataList.isEmpty()) {
            apiCallBlocker = false;
          } else {
            for (GetFanList.UserData userData : dataList) {
              //fansIdlist.add(userData.getUser_id());
              String userName = userData.getFirst_name() + " " + userData.getLast_name();
              UserInfoBean userInfo = new UserInfoBean(userData.getUser_id(), userName);
              userInfo.setTagNo(userData.getTagged_number());
              userInfo.setProfileUrl(userData.getProfile_url());
              fansList.add(userInfo);
            }
          }
          if (fansList == null || fansList.isEmpty()) {
            lblNoSupporting.setVisibility(View.VISIBLE);
          }
          loadUserVideos();
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetFanList> call, @NotNull Throwable t) {
        disableProgress();
      }
    });
  }

  private void disableProgress() {
    if (this == null) {
      return;
    }
    this.layoutProgress.setVisibility(View.GONE);
  }

  private void loadUserVideos() {
    if (this == null || this.fansList == null || this.fansList.isEmpty()) {
      return;
    }

    this.layoutContainer.removeAllViews();
    LayoutInflater inflater = LayoutInflater.from(this);
    for (final UserInfoBean userInfo : this.fansList) {
      View view =
          inflater.inflate(R.layout.supporting_showroom_videos_row, this.layoutContainer, false);
      TextView lblUserName = view.findViewById(R.id.lblUserName);
      TextView lblTagNumber = view.findViewById(R.id.lblTagNumber);
      ImageView imgProfilePic = view.findViewById(R.id.imgProfilePic);
      RecyclerView recyclerVideosList = view.findViewById(R.id.recyclerVideosList);
      recyclerVideosList.setLayoutManager(
          new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

      lblUserName.setText(userInfo.getUserName());
      lblTagNumber.setText(userInfo.getTagNo());
      Utils.loadProfilePic(this, imgProfilePic, userInfo.getProfileUrl());
      ArrayList<GetPostResponseModel.PostDetails> datalist =
          new ArrayList<GetPostResponseModel.PostDetails>();
      SupportingVideosAdapter adapter = new SupportingVideosAdapter(this, datalist);
      recyclerVideosList.setAdapter(adapter);

      // bind events
      imgProfilePic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(SupportingShowroomFragment.this, userInfo.getUserId());
        }
      });
      lblUserName.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(SupportingShowroomFragment.this, userInfo.getUserId());
        }
      });
      lblTagNumber.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(SupportingShowroomFragment.this, userInfo.getUserId());
        }
      });
      this.layoutContainer.addView(view);

      this.loadProfileVideos(view, adapter, userInfo.getUserId(), datalist);
    }
  }

  private void loadProfileVideos(
      View view, final SupportingVideosAdapter adapter, String userId,
      final ArrayList<GetPostResponseModel.PostDetails> datalist) {
    String token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    //showProgressDialog();
    Call<GetPostResponseModel> call = methods.getUserVideos(userId, 1, 10, token);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        //dismissProgressDialog();
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> responseData =
              null;
          if (getresponsemodel != null) {
            responseData = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
          }
          if (responseData != null && !responseData.isEmpty()) {
            datalist.addAll(responseData);
            adapter.notifyDataSetChanged();
          } else {
            // on no videos
            view.setVisibility(View.GONE);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetPostResponseModel> call, Throwable t) {
        //dismissProgressDialog();
        Log.e("ProfileVideo", t.getMessage());
        t.printStackTrace();
      }
    });
  }
}
