package in.tagteen.tagteen;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import in.tagteen.tagteen.FCM.Config;
import in.tagteen.tagteen.Fragments.HomeFragment_new;
import in.tagteen.tagteen.Fragments.KnowledgeFragments;
import in.tagteen.tagteen.Fragments.VideoDataSender1;
import in.tagteen.tagteen.Fragments.youthtube.VideoGallery;
import in.tagteen.tagteen.Fragments.youthtube.WebShowsActivity;
import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.VideoEditor.StartActivity;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.chatting.socket.SocketConnection;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import in.tagteen.tagteen.configurations.AppConfigurationSetting;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.database.SocketServiceProvider;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.profile.UserProfileFragment;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.GeneralApiUtils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainDashboardActivity extends AppCompatActivity implements AsyncResponse {
  private static final String TAG = MainDashboardActivity.class.getSimpleName();
  private BroadcastReceiver notificationBroadcastReceiver;
  private ImageView homeButton, socialButton, knowledgeButton, jobsButton;
  private TextView lblTalent, lblKnowledge, lblSocial, lblPlacements;
  private LinearLayout layoutAddTalentVideo;
  private ViewPager viewPager;
  private FrameLayout frameLayout;
  private ViewPagerAdapter adapter;

  private HomeFragment_new homeFragment;
  //private MomentsFeed momentsFeed;
  private KnowledgeFragments knowledgeFragments;
  private GamingActivity gamingFragment;

  private VideoDataSender1 videoDataSender;

  private GetAllUserFriendlist.Data datalist = new GetAllUserFriendlist.Data();
  private String fragmentLoad = "";
  private boolean doubleBackToExitPressedOnce = false;

  private static final int TALENT_TAB_INDEX = 0;
  //private static final int SOCIAL_TAB_INDEX = 1;
  private static final int KNOWLEDGE_TAB_INDEX = 1;
  //private static final int GAMING_TAB_INDEX = 2;

  private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
  private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;
  private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
  private Uri fileUri;
  private SocketServiceProvider mBoundService;
  private boolean mIsBound = false;
  private boolean isTaggedUser = false;
  private boolean isNotFirstAccess = false;
  private boolean isFirstAccessAfterVerified = false;
  private String mobileNumber;
  ArrayList<GetPostResponseModel.PostDetails> data=new ArrayList<>();

  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Utils.checkPermissions(this);
    setContentView(R.layout.activity_example_maiin_tab);
    SharedPreferenceSingleton pref = SharedPreferenceSingleton.getInstance();
    pref.init(this);
    if (GeneralApiUtils.isStringEmpty(
        pref.getStringPreference(this, ApplicationConstants.APPLICATION_STATUS)) ||
        !pref.getStringPreference(this, ApplicationConstants.APPLICATION_STATUS)
            .equalsIgnoreCase(AppConfigurationSetting.AppLoginStatus.LOGIN_SUCCESS)) {
      pref.writeStringPreference(this,
          ApplicationConstants.APPLICATION_STATUS,
          AppConfigurationSetting.AppLoginStatus.LOGIN_SUCCESS);
    }
    this.isTaggedUser = pref.getBoolPreference(RegistrationConstants.IS_TAGGED_USER);
    this.isNotFirstAccess = pref.getBoolPreference(RegistrationConstants.IS_NOT_FIRST_ACCESS);
    this.isFirstAccessAfterVerified =
        pref.getBoolPreference(RegistrationConstants.IS_FIRST_ACCESS_AFTER_VERIFIED);
    this.mobileNumber = pref.getStringPreference(RegistrationConstants.MOBILE);

    this.updateAccessToken();
    loadPostsFromServer();
    //tony00
    fragmentLoad = "";
    if (getIntent() != null && getIntent().getExtras() != null) {
      fragmentLoad = getIntent().getStringExtra(Constants.LOAD_FRAGMENT);
    }

    videoDataSender = new VideoDataSender1();
    callApiFriendsList();

    this.homeButton = (ImageView) findViewById(R.id.bottom_tab_home_icon);
    this.socialButton = (ImageView) findViewById(R.id.bottom_tab_Chat_icon);
    this.layoutAddTalentVideo = findViewById(R.id.layoutAddTalentVideo);
    this.knowledgeButton = (ImageView) findViewById(R.id.bottom_tab_notifiation_icon);
    this.jobsButton = (ImageView) findViewById(R.id.bottom_tab_profile_icon);

    this.lblTalent = findViewById(R.id.lblTalent);
    this.lblKnowledge = findViewById(R.id.lblKnowledge);
    this.lblSocial = findViewById(R.id.lblSocial);
    this.lblPlacements = findViewById(R.id.lblPlacements);

    this.viewPager = (ViewPager) findViewById(R.id.viewpager);
    this.frameLayout = (FrameLayout) findViewById(R.id.dash_board_viewpager);
    this.frameLayout.setVisibility(View.GONE);
    this.viewPager.setVisibility(View.VISIBLE);
    this.viewPager.setOffscreenPageLimit(3);
    this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
    this.viewPager.setAdapter(this.adapter);

    if (fragmentLoad.equalsIgnoreCase(Constants.MY_PROFILE)) {
      startActivity(new Intent(this, UserProfileFragment.class));
      return;
    } else if (fragmentLoad.equalsIgnoreCase(Constants.OTHER_PROFILE)) {
      String userId = getIntent().getStringExtra(Constants.USER_ID);
      if (userId != null && userId.trim().length() > 0) {
        Utils.gotoProfile(this, userId);
        return;
      }
    } else if (fragmentLoad.equalsIgnoreCase("social")) {
      //viewPager.setCurrentItem(SOCIAL_TAB_INDEX);
    }

    this.homeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        viewPager.setCurrentItem(TALENT_TAB_INDEX);
      }
    });
    this.socialButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        moveToWebShows();
        //viewPager.setCurrentItem(SOCIAL_TAB_INDEX);
      }
    });
    this.knowledgeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //viewPager.setCurrentItem(KNOWLEDGE_TAB_INDEX);
        CelebritiesAcivityCall();
      }
    });
    this.jobsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //viewPager.setCurrentItem(GAMING_TAB_INDEX);
        moveToGamingScreen();
      }
    });
    this.layoutAddTalentVideo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //moveToWebShows();
        gotoVideoList();
                /*if (isTaggedUser) {
                    showAlertOnFirstShowroomUpload();
                } else {
                    Utils.showUnverifiedUserDialog(MainDashboardActivity.this);
                }*/
      }
    });

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        homeButton.setColorFilter(
            getResources().getColor(R.color.conversation_list_item_background_read_dark));
        knowledgeButton.setColorFilter(
            getResources().getColor(R.color.conversation_list_item_background_read_dark));
        socialButton.setColorFilter(
            getResources().getColor(R.color.conversation_list_item_background_read_dark));
        jobsButton.setColorFilter(
            getResources().getColor(R.color.conversation_list_item_background_read_dark));

        lblTalent.setTextColor(getResources().getColor(R.color.full_black));
        lblKnowledge.setTextColor(getResources().getColor(R.color.full_black));
        lblSocial.setTextColor(getResources().getColor(R.color.full_black));
        lblPlacements.setTextColor(getResources().getColor(R.color.full_black));

        switch (position) {
          case TALENT_TAB_INDEX:
            homeButton.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
            lblTalent.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            break;

          case KNOWLEDGE_TAB_INDEX:
            knowledgeButton.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
            lblKnowledge.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            break;

          /*case SOCIAL_TAB_INDEX:
            socialButton.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
            lblSocial.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            showAlertOnFirstMomentsAccess();
            break;*/

          /*case GAMING_TAB_INDEX:
            jobsButton.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
            lblPlacements.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            break;*/
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    notificationBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

        // checking for type intent filter
        if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
          // gcm successfully registered
          // now subscribe to `global` topic to receive app wide notifications
          FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

          displayFirebaseRegId();
        } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
          String message = intent.getStringExtra("message");
        }
      }
    };
    //this.congratsOnFirstVerifiedAccess();
  }

  private void updateAccessToken() {
    String lastAccessedOn = SharedPreferenceSingleton.getInstance().getStringPreference(Constants.ACCESS_TOKEN_UPDATED_ON);
    Date verifiedOn = Utils.getDateFrom24HrsFormatStr(lastAccessedOn);
    if (Utils.isSameDay(verifiedOn, new Date()) == false) {
      AsyncWorker mWorker = new AsyncWorker(this);
      mWorker.delegate = this;
      JSONObject jsonObject = new JSONObject();
      mWorker.execute(
              ServerConnector.REQUEST_LOGOUT,
              jsonObject.toString(),
              RequestConstants.GET_REQUEST,
              RequestConstants.HEADER_YES,
              RequestConstants.REQUEST_LOGOUT);
    }
  }

  private void refreshScreen() {
    finish();
    overridePendingTransition(0, 0);
    startActivity(getIntent());
    overridePendingTransition(0, 0);
  }

  private void loginRequest() {
    if (this == null) {
      return;
    }
    if (this.mobileNumber == null || this.mobileNumber.trim().length() == 0) {
      return;
    }
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put(RegistrationConstants.MOBILE, this.mobileNumber);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    mWorker.execute(
            ServerConnector.REQUEST_FOR_LOGIN,
            jsonObject.toString(),
            RequestConstants.POST_REQUEST,
            RequestConstants.HEADER_NO,
            RequestConstants.REQUEST_FOR_LOGIN);
  }

  private void moveToGamingScreen() {
    Intent intent = new Intent(this, GamingActivity.class);
    startActivity(intent);
  }

  private void gotoVideoList() {
    if (Utils.isGuestLogin()) {
      Utils.moveToRegistration(this);
    } else {
      dialog_show_gallery();
    }
  }

  private void dialog_show_gallery() {
    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
    builder1.setMessage("Choose an action");
    builder1.setCancelable(true);

    builder1.setPositiveButton(
            "Gallery",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
      Intent gotoVideogallery = new Intent(MainDashboardActivity.this, VideoGallery.class);
      startActivity(gotoVideogallery);
              }
            });

    builder1.setNegativeButton(
            "Camera",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent gotoVideogallery = new Intent(MainDashboardActivity.this, StartActivity.class);
                startActivity(gotoVideogallery);
              }
            });

    AlertDialog alert11 = builder1.create();
    alert11.show();
  }

  private void showAlertOnFirstShowroomUpload() {
    boolean isAlreadyUploaded =
        SharedPreferenceSingleton.getInstance().getBoolPreference(Constants.IS_SHOWROOM_UPLOADED);
    if (!isAlreadyUploaded) {
      SharedPreferenceSingleton.getInstance()
          .writeBoolPreference(Constants.IS_SHOWROOM_UPLOADED, true);
      this.alertFirstTimeShowroomUpload();
    } else {
      this.verifyVideoUploadPerDayAndProceed();
    }
  }

  private void moveToWebShows() {
    Intent intent = new Intent(this, WebShowsActivity.class);
    startActivity(intent);
  }

  private void alertFirstTimeShowroomUpload() {
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setCancelable(false);
    dialog.setTitle("Information");
    dialog.setMessage(getString(R.string.no_camera));
    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int id) {
        verifyVideoUploadPerDayAndProceed();
      }
    });
    final AlertDialog alert = dialog.create();
    alert.show();
  }

  private void verifyVideoUploadPerDayAndProceed() {
    String dateStr = SharedPreferenceSingleton.getInstance()
        .getStringPreference(Constants.SHOWROOM_LAST_UPLOADED_AT);
    Date lastUploadedAt = Utils.getDateFromString(dateStr);
    if (!Utils.isSameDay(lastUploadedAt, new Date())) {
      gotoVideoList();
    } else {
      //gotoVideoList();
      Utils.showAlertDialog(
          MainDashboardActivity.this, "You can upload only one video per day.", "Alert");
    }
  }

  private void congratsOnFirstVerifiedAccess() {
    if (this == null) {
      return;
    }
    if (!this.isTaggedUser) {
      if (!this.isNotFirstAccess) {
        SharedPreferenceSingleton.getInstance()
            .writeBoolPreference(RegistrationConstants.IS_NOT_FIRST_ACCESS, true);
        Utils.showAlertDialog(this, getString(R.string.unverified_msg), "");
      }
      SharedPreferenceSingleton.getInstance()
          .writeBoolPreference(RegistrationConstants.IS_FIRST_ACCESS_AFTER_VERIFIED, true);
    } else if (this.isFirstAccessAfterVerified) {
      SharedPreferenceSingleton.getInstance()
          .writeBoolPreference(RegistrationConstants.IS_FIRST_ACCESS_AFTER_VERIFIED, false);
      Utils.showAlertDialog(this, "Congratulations!!! You're Verified Now..", "Alert");
    }
  }

  private void displayFirebaseRegId() {
    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
    String regId = pref.getString("regId", null);
    Log.e(TAG, "Firebase reg id: " + regId);
    if (!TextUtils.isEmpty(regId)) {
      //txtRegId.setText("Firebase Reg Id: " + regId);
    } else {
      //txtRegId.setText("Firebase Reg Id is not received yet!");
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    // register GCM registration complete receiver
    LocalBroadcastManager.getInstance(this).registerReceiver(notificationBroadcastReceiver,
        new IntentFilter(Config.REGISTRATION_COMPLETE));

    // register new push message receiver
    // by doing this, the activity will be notified each time a new message arrives
    LocalBroadcastManager.getInstance(this).registerReceiver(notificationBroadcastReceiver,
        new IntentFilter(Config.PUSH_NOTIFICATION));

    //tony00
    // clear the notification area when the app is opened
    //        NotificationUtils.clearNotifications(getApplicationContext());

    if (this.viewPager != null
        && this.homeFragment != null
        && videoDataSender != null
        && videoDataSender.isCall()) {
      this.viewPager.setCurrentItem(0);
      this.homeFragment.moveToLandingTab();
    }
  }

  @Override
  protected void onPause() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationBroadcastReceiver);
    super.onPause();
  }

  private void callApiFriendsList() {
    SharedPreferenceSingleton.getInstance().init(MainDashboardActivity.this);
    String userid =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    String token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);

    Apimethods methods =
        API_Call_Retrofit.getretrofit(MainDashboardActivity.this).create(Apimethods.class);
    Call<GetAllUserFriendlist> call = methods.getalluserfriendslist(userid, token);
    //API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetAllUserFriendlist>() {
      @Override
      public void onResponse(@NotNull Call<GetAllUserFriendlist> call,
          @NotNull Response<GetAllUserFriendlist> response) {
        if (response.code() == 200) {
          GetAllUserFriendlist getlistdata = response.body();
          if (getlistdata != null) {
            datalist = getlistdata.getData();
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<GetAllUserFriendlist> call, @NotNull Throwable t) {
      }
    });
  }

  @Override
  public void onStart() {
    super.onStart();
    try {
      String refreshedToken = FirebaseInstanceId.getInstance().getToken();
      Log.d("Firbase id login", "Refreshed token: " + refreshedToken);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // if the result is capturing Image
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE ||
        requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE ||
        requestCode == 2) {
      /*if (this.momentsFeed != null) {
        this.momentsFeed.onActivityResult(requestCode, resultCode, data);
      }*/
            /*if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                Intent it = new Intent(this, SelfiCameraPreview.class);
                SharedPreferenceSingleton.getInstance().init(this);
                SharedPreferenceSingleton.getInstance().writeStringPreference(
                        ApplicationConstants.CAPTURED_IMAGE_PATH, fileUri.getPath());
                startActivity(it);
            } else if (resultCode == RESULT_CANCELED) {
                Utils.showShortToast(this, getString(R.string.user_cancelled_image_capture));
            } else {
                Utils.showShortToast(this, getString(R.string.image_capture_failed));
            }*/
      //} else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            /*if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                //previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                Utils.showShortToast(this, getString(R.string.user_cancelled_video_capture));
            } else {
                Utils.showShortToast(this, getString(R.string.video_capture_failed));
            }*/
    } else if (requestCode == Constants.COMMENT_REQUEST_CODE ||
        requestCode == Constants.VIDEO_FULLSCREEN_CODE) {
      if (this.homeFragment != null) {
        this.homeFragment.onActivityResult(requestCode, resultCode, data);
      }
    } else if (requestCode == Constants.KNOWLEDGE_VIDEO_CAPTURE_REQUEST_CODE) {
      if (this.knowledgeFragments != null) {
        this.knowledgeFragments.onActivityResult(requestCode, resultCode, data);
      }
    }
  }

  private void bindService() {
    Intent intent = new Intent(this, SocketServiceProvider.class);
    bindService(intent, socketConnection, Context.BIND_AUTO_CREATE);
  }

  protected ServiceConnection socketConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

      SocketServiceProvider.MyBinder binder = (SocketServiceProvider.MyBinder) service;
      mBoundService = binder.getService();
      mIsBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mBoundService = null;
      mIsBound = false;
    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mIsBound) {
      unbindService(socketConnection);
      mIsBound = false;
    }
  }

  @Override
  public void onRefresh() {

  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    switch (REQUEST_NUMBER) {
      case RequestConstants.REQUEST_LOGOUT:
        loginRequest();
        break;

      case RequestConstants.REQUEST_FOR_LOGIN: {
        try {
          JSONObject loginResponse = new JSONObject(output);
          boolean responseStatus = loginResponse.getBoolean(RegistrationConstants.SUCCESS);
          if (responseStatus) {
            String data = loginResponse.getString(RegistrationConstants.DATA);
            JSONObject responseData = new JSONObject(data);

            String userId = responseData.getString(RegistrationConstants.USER_ID);
            String token = responseData.getString(RegistrationConstants.TOKEN);
            ChatSessionManager chatSessionManager = ChatSessionManager.getInstance();
            chatSessionManager.initiateSession(userId, token);
            SocketConnection.getConnection().bindCreator();

            SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.TOKEN, token);
            SharedPreferenceSingleton.getInstance()
                    .writeStringPreference(Constants.ACCESS_TOKEN_UPDATED_ON, Utils.getNowInDateString());
            refreshScreen();
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fragmentManager) {
      super(fragmentManager);
    }

    @NonNull @Override
    public Fragment getItem(int position) {
      switch (position) {
        case TALENT_TAB_INDEX:
          if (homeFragment == null) {
            homeFragment = new HomeFragment_new();
            homeFragment.setOnPostDeleteListener(new OnPostDeleteListener() {
              @Override
              public void onDelete(String postId) {

              }
            });
          }
          return homeFragment;
        //case SOCIAL_TAB_INDEX:
          /*if (momentsFeed == null) {
            momentsFeed = new MomentsFeed();
            momentsFeed.setOnPostDeleteListener(new OnPostDeleteListener() {
              @Override
              public void onDelete(String postId) {

              }
            });
          }
          return momentsFeed;*/
        case KNOWLEDGE_TAB_INDEX:
          if (knowledgeFragments == null) {
            knowledgeFragments = new KnowledgeFragments();
          }
          return knowledgeFragments;

        /*case GAMING_TAB_INDEX:
          if (gamingFragment == null) {
            gamingFragment = new GamingActivity();
          }
          return gamingFragment;*/
      }
      return null;
    }

    @Override
    public int getCount() {
      return 2;
    }
  }

  private void CelebritiesAcivityCall() {
    if (data!=null && !data.isEmpty()) {
      Intent intent = new Intent(MainDashboardActivity.this, VideoPartDetail.class);
      intent.putExtra("post_creator_id", data.get(0).getOwner_post_creator_id());
      intent.putExtra("post_vid_id", data.get(0).getOwner_post_id());
      intent.putExtra("category_id", data.get(0).getCategorie_id());
      intent.putExtra(Constants.SHOWROOM_POST_DATA, data);
      startActivity(intent);
    }
  }
  public void loadPostsFromServer() {
    String token =
            SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    String userId =
            SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    Apimethods methods = API_Call_Retrofit.getretrofitPost(this).create(Apimethods.class);
    Call<GetPostResponseModel> call =
            methods.getAllShowroomPostCelebrity(userId,1, 10, 3, token);

    API_Call_Retrofit.methodCalled("Latest: " + call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
                             @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          data =
                  (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
          Log.d("url=",data.toString());
        }

      }

      @Override
      public void onFailure(Call<GetPostResponseModel> call, Throwable t) {
        Log.d("url=",call.toString());
      }
    });
  }

  private void showAlertOnFirstMomentsAccess() {
    boolean isMomentsAccessed = SharedPreferenceSingleton.getInstance()
        .getBoolPreference(RegistrationConstants.IS_MOMENTS_ACCESSED);
    if (!isMomentsAccessed) {
      String msg = "Post anything here, your personal moments photos, videos, selfies, " +
          "memes and all your friends and supporters will be able to see and react to your moments";
      Utils.showAlertDialog(this, msg, "Moments");
    }
    SharedPreferenceSingleton.getInstance()
        .writeBoolPreference(RegistrationConstants.IS_MOMENTS_ACCESSED, true);
  }

  @Override
  public void onBackPressed() {
    if (doubleBackToExitPressedOnce || getSupportFragmentManager().getBackStackEntryCount() > 0) {
      super.onBackPressed();
      return;
    }

    this.doubleBackToExitPressedOnce = true;
    Utils.showShortToast(this, "Press again to exit");

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        doubleBackToExitPressedOnce = false;
      }
    }, 2000);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case Constants.REQUEST_CODE_ASK_PERMISSIONS:
        String message = "You need to grant access to ";
        boolean isMissedPermission = false;
        final List<String> permissionsList = new ArrayList<String>();
        for (int index = permissions.length - 1; index >= 0; --index) {
          if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
            message = message + ", " + permissions[index];
            permissionsList.add(permissions[index]);
            isMissedPermission = true;
          }
        }
        if (isMissedPermission) {
          Utils.showShortToast(this, message);
        }
        break;
    }
  }
}
