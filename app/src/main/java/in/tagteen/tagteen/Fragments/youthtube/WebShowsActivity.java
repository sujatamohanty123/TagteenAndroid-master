package in.tagteen.tagteen.Fragments.youthtube;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import in.tagteen.tagteen.Fragments.youthtube.adapter.ClipsAdapter;
import in.tagteen.tagteen.Fragments.youthtube.adapter.TrailerAdapter;
import in.tagteen.tagteen.Fragments.youthtube.adapter.UpcomingLiveShowsAdapter;
import in.tagteen.tagteen.Fragments.youthtube.adapter.WebShowsAdapter;
import in.tagteen.tagteen.Model.SimpleActionModel;
import in.tagteen.tagteen.Model.WebshowModel;
import in.tagteen.tagteen.Model.webshows.Payment;
import in.tagteen.tagteen.Model.webshows.PaymentStatus;
import in.tagteen.tagteen.Model.webshows.TxnTokenResponse;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.GeneralApiUtils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import io.agora.openlive.Constants;
import io.agora.openlive.activities.LiveActivityUserDM;
import io.agora.openlive.activities.LiveBroadcasterActivity;
import io.agora.openlive.activities.LiveViewerActivity;
import io.agora.openlive.utils.ImageUrlUtils;
import io.agora.openlive.utils.IntentUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebShowsActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {
  private RecyclerView recyclerUpcomingLiveShows; // 4
  private RecyclerView recyclerShortShows; // 1
  private RecyclerView recyclerTrailers; // 7
  private RecyclerView recyclerMovieShows; // 3
  private RecyclerView recyclerClips; // 5
  private RecyclerView recyclerFeaturedTrailers;

  private TextView lblLiveshowsComingSoon;
  private TextView lblNoUpcomingLiveShows;
  private TextView lblNoShortShows;
  private TextView lblNoFeaturedTrailer;
  private TextView lblNoMovieShows;
  private TextView lblNoClips;
  private TextView lblNoTrailers;
  private ImageView imgShare;

  private RelativeLayout container;
  private RelativeLayout loadingSpinner;

  private ImageView imgBanner;
  private TextView lblLiveShowDescription;
  private TextView lblLiveAt;
  private WebshowModel.WebshowDetails selectedLiveShow;

  private int page = 1;
  private int pageLimit = 1000;
  private String userId;
  private String userName;
  private String userImage;

  private ProgressDialog progressDialog;

  private static final int PAYMENT_REQUEST_CODE = 3456;

  private static final int SHORTSHOWS_ID = 1;
  private static final int WEBSERIES_ID = 2;
  private static final int MOVIESHOWS_ID = 3;
  private static final int LIVESHOWS_ID = 4;
  private static final int CLIPS_ID = 5;
  private static final int TRAILERS_ID = 6;
  private static final int FEATURED_TRAILERS_ID = 7;

  private CountDownTimer countDownTimer;

  public WebShowsActivity() {
    // Required empty public constructor
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // full screen
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    setContentView(R.layout.fragment_webshows);
    this.initWidgets();
    this.bindEvents();
    this.loadData();
    initClickListeners();
  }

  private void initClickListeners() {
    imgShare.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (selectedLiveShow != null && !GeneralApiUtils.isStringEmpty(
            selectedLiveShow.getHostedBy())) {
          shareImage(selectedLiveShow.getVideoThumbnailUrl(), selectedLiveShow.getHostedBy());
        }
      }
    });
  }

  private void initWidgets() {
    SharedPreferenceSingleton.getInstance().init(this);
    userId =
        SharedPreferenceSingleton.getInstance()
            .getStringPreference(this, RegistrationConstants.USER_ID);
    userName = SharedPreferenceSingleton.getInstance()
        .getStringPreference(this, RegistrationConstants.FIRST_NAME);
    userImage = SharedPreferenceSingleton.getInstance()
        .getStringPreference(this, RegistrationConstants.PROFILE_URL);
    imgShare = findViewById(R.id.img_share);
    this.recyclerUpcomingLiveShows = findViewById(R.id.recyclerUpcomingLiveShows);
    this.recyclerUpcomingLiveShows.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    this.recyclerShortShows = findViewById(R.id.recyclerShortShows);
    this.recyclerShortShows.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    this.recyclerFeaturedTrailers = findViewById(R.id.recyclerFeaturedTrailers);
    this.recyclerFeaturedTrailers.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    this.recyclerTrailers = findViewById(R.id.recyclerTrailers);
    this.recyclerTrailers.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    this.recyclerMovieShows = findViewById(R.id.recyclerMovieShows);
    this.recyclerMovieShows.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    this.recyclerClips = findViewById(R.id.recyclerClips);
    this.recyclerClips.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    this.lblLiveshowsComingSoon = findViewById(R.id.lblLiveshowsComingSoon);
    this.lblNoUpcomingLiveShows = findViewById(R.id.lblNoUpcomingLiveShows);
    this.lblNoShortShows = findViewById(R.id.lblNoShortShows);
    this.lblNoFeaturedTrailer = findViewById(R.id.lblNoFeaturedTrailer);
    this.lblNoMovieShows = findViewById(R.id.lblNoMovieShows);
    this.lblNoClips = findViewById(R.id.lblNoClips);
    this.lblNoTrailers = findViewById(R.id.lblNoTrailers);

    this.lblLiveShowDescription = findViewById(R.id.lblLiveShowDescription);
    this.lblLiveAt = findViewById(R.id.lblLiveAt);
    this.imgBanner = findViewById(R.id.imgBanner);

    this.container = findViewById(R.id.container);
    this.loadingSpinner = findViewById(R.id.loadingSpinner);
  }

  private void bindEvents() {
    this.lblLiveAt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        goLive();
        //broadcastLive();
      }
    });
  }

  private void getChannelToken() {
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<SimpleActionModel> call = methods.getWebshowToken();
    call.enqueue(new Callback<SimpleActionModel>() {
      @Override
      public void onResponse(Call<SimpleActionModel> call, Response<SimpleActionModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          SimpleActionModel responseModel = response.body();
        }
      }

      @Override
      public void onFailure(Call<SimpleActionModel> call, Throwable t) {
        Utils.showShortToast(WebShowsActivity.this, "Couldn't get channel token");
      }
    });
  }

  private void goLive() {
    if (this.selectedLiveShow == null) {
      return;
    }
    if (this.userId.equals(this.selectedLiveShow.getUserId())) {
      // broadcaster
      this.broadcastLive();
    } else {
      this.joinLive();
    }
  }

  private void joinLive() {
    if (!this.selectedLiveShow.isUserPaid()) {
      this.initiateOrder(this.selectedLiveShow.getWebshowId());
      return;
    }

    // check time to go live
    Date showTime = Utils.getDateTimeFromString(this.selectedLiveShow.getWebshowDate());
    if (showTime == null) {
      Utils.showShortToast(this, "Show time not specified");
      return;
    }

    Date currentTime = new Date();
    if (currentTime.after(new Date())) {
      return;
    }

    Intent intent = new Intent(this, LiveViewerActivity.class);
    //intent.putExtra(Constants.KEY_CLIENT_ROLE, io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
    intent.putExtra(Constants.CHANNEL_NAME, this.selectedLiveShow.getChannelName());
    intent.putExtra(Constants.TOKEN, this.selectedLiveShow.getToken());
    intent.putExtra(Constants.UID, this.selectedLiveShow.getUid());
    intent.putExtra(Constants.HOSTED_BY, this.selectedLiveShow.getHostedBy());
    intent.putExtra(Constants.HOST_USER_ID, this.selectedLiveShow.getUserId());
    LiveActivityUserDM userDM = new LiveActivityUserDM();
    userDM.setUserId(userId);
    userDM.setUserName(userName);
    userDM.setUserImage(userImage);
    if (selectedLiveShow != null) {
      userDM.setBroadcasterName(selectedLiveShow.getHostedBy());
      userDM.setBroadcasterImage(selectedLiveShow.getVideoThumbnailUrl());
      userDM.setBrodcasterProfileImage(selectedLiveShow.getVideoFileUrl());
    }
    intent.putExtra(LiveViewerActivity.EXTRA_PARCELABLE_DATA, userDM);
    startActivity(intent);
  }

  private void broadcastLive() {
    Date showTime = Utils.getDateTimeFromString(this.selectedLiveShow.getWebshowDate());
    Date currentTime = new Date();
    if (selectedLiveShow != null && selectedLiveShow.getWebshowDate() != null
        && currentTime.after(showTime)) {
      Intent intent = new Intent(this, LiveBroadcasterActivity.class);
      //intent.putExtra(Constants.KEY_CLIENT_ROLE, io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
      intent.putExtra(Constants.CHANNEL_NAME, this.selectedLiveShow.getChannelName());
      intent.putExtra(Constants.TOKEN, this.selectedLiveShow.getToken());
      intent.putExtra(Constants.UID, this.selectedLiveShow.getUid());
      intent.putExtra(Constants.HOSTED_BY, this.selectedLiveShow.getHostedBy());
      LiveActivityUserDM userDM = new LiveActivityUserDM();
      userDM.setUserId(userId);
      userDM.setUserName(userName);
      userDM.setUserImage(userImage);
      userDM.setBroadcasterName(selectedLiveShow.getHostedBy());
      userDM.setBroadcasterImage(selectedLiveShow.getVideoThumbnailUrl());
      userDM.setBrodcasterProfileImage(selectedLiveShow.getVideoFileUrl());
      intent.putExtra(LiveBroadcasterActivity.EXTRA_PARCELABLE_DATA, userDM);
      startActivity(intent);
    } else {
      Toast.makeText(this, "Cannot start Broadcast before showtime", Toast.LENGTH_SHORT).show();
    }
  }

  private void loadData() {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        getUpcomingLiveShows();
        getShortShows();
        getTrailers();
        getFeaturedTrailers();
        getMovieShows();
        getClipVideos();
      }
    }, 1000);
  }

  private void getUpcomingLiveShows() {
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<WebshowModel> call =
        methods.getWebshowVideos(this.page, this.pageLimit, LIVESHOWS_ID, this.userId);
    Log.e("show api", call.request().url().toString());
    call.enqueue(new Callback<WebshowModel>() {
      @Override
      public void onResponse(@NonNull Call<WebshowModel> call,
          @NonNull Response<WebshowModel> response) {
        loadingSpinner.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);

        int statuscode = response.code();
        if (statuscode == 200) {
          WebshowModel getresponsemodel = response.body();
          ArrayList<WebshowModel.WebshowDetails> responseData =
              (ArrayList<WebshowModel.WebshowDetails>) getresponsemodel.getData();

          if (responseData != null && responseData.isEmpty() == false) {
            selectedLiveShow = responseData.get(0);
            setBanner();

            UpcomingLiveShowsAdapter adapter =
                new UpcomingLiveShowsAdapter(
                    WebShowsActivity.this,
                    responseData,
                    new UpcomingLiveShowsAdapter.SelectionListener() {
                      @Override
                      public void select(WebshowModel.WebshowDetails webshow) {
                        selectedLiveShow = webshow;
                        setBanner();
                      }
                    });
            recyclerUpcomingLiveShows.setAdapter(adapter);
          } else {
            imgBanner.setVisibility(View.GONE);
            lblLiveAt.setVisibility(View.GONE);
            lblNoUpcomingLiveShows.setVisibility(View.VISIBLE);
            lblLiveshowsComingSoon.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onFailure(Call<WebshowModel> call, Throwable t) {

      }
    });
  }

  private void setBanner() {
    if (this.selectedLiveShow == null) {
      return;
    }
    if (this.selectedLiveShow.getVideoThumbnailUrl() != null) {
      Utils.loadImageUsingGlideWithPlaceholder(
          this, this.imgBanner, this.selectedLiveShow.getVideoThumbnailUrl(), R.color.full_black);
    }
    if (this.selectedLiveShow.getDescription() != null) {
      this.lblLiveShowDescription.setText(this.selectedLiveShow.getDescription());
    }

    this.setLiveAt(Utils.getDateTimeFromString(this.selectedLiveShow.getWebshowDate()));
  }

  private void setLiveAt(Date liveAt) {
    if (this.countDownTimer != null) {
      this.countDownTimer.cancel();
    }

    if (liveAt == null) {
      return;
    }

    long currentTimeInMills = System.currentTimeMillis();
    long diffInMills = liveAt.getTime() - currentTimeInMills;

    this.countDownTimer = new CountDownTimer(diffInMills, 1000) {
      @Override
      public void onTick(long millis) {
        String liveIn;
        if (TimeUnit.MILLISECONDS.toDays(millis) > 0) {
          liveIn = String.format("%02d:%02d:%02d:%02d",
              TimeUnit.MILLISECONDS.toDays(millis),
              TimeUnit.MILLISECONDS.toHours(millis) % 24,
              TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
              TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
        } else {
          liveIn = String.format("%02d:%02d:%02d",
              TimeUnit.MILLISECONDS.toHours(millis),
              TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
              TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
        }
        lblLiveAt.setText(liveIn);
      }

      @Override
      public void onFinish() {
        if (selectedLiveShow != null && selectedLiveShow.getUserId().equals(userId)) {
          lblLiveAt.setText("Go Live");
        } else if (selectedLiveShow != null) {
          lblLiveAt.setText("Watch Live");
        }
      }
    }.start();
  }

  private void getShortShows() {
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<WebshowModel> call =
        methods.getWebshowVideos(this.page, this.pageLimit, SHORTSHOWS_ID, this.userId);
    call.enqueue(new Callback<WebshowModel>() {
      @Override
      public void onResponse(Call<WebshowModel> call, Response<WebshowModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          WebshowModel getresponsemodel = response.body();
          ArrayList<WebshowModel.WebshowDetails> responseData =
              (ArrayList<WebshowModel.WebshowDetails>) getresponsemodel.getData();

          if (responseData != null && !responseData.isEmpty()) {
            WebShowsAdapter adapter =
                new WebShowsAdapter(WebShowsActivity.this, responseData, SHORTSHOWS_ID);
            recyclerShortShows.setAdapter(adapter);
          } else {
            lblNoShortShows.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onFailure(Call<WebshowModel> call, Throwable t) {

      }
    });
  }

  private void getTrailers() {
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<WebshowModel> call =
        methods.getWebshowVideos(this.page, this.pageLimit, TRAILERS_ID, this.userId);

    call.enqueue(new Callback<WebshowModel>() {
      @Override
      public void onResponse(Call<WebshowModel> call, Response<WebshowModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          WebshowModel getresponsemodel = response.body();
          ArrayList<WebshowModel.WebshowDetails> responseData =
              (ArrayList<WebshowModel.WebshowDetails>) getresponsemodel.getData();

          if (responseData != null && !responseData.isEmpty()) {
            TrailerAdapter adapter =
                new TrailerAdapter(WebShowsActivity.this, responseData, TRAILERS_ID);
            recyclerTrailers.setAdapter(adapter);
          } else {
            lblNoTrailers.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onFailure(Call<WebshowModel> call, Throwable t) {

      }
    });
  }

  private void getFeaturedTrailers() {
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<WebshowModel> call =
        methods.getWebshowVideos(this.page, this.pageLimit, FEATURED_TRAILERS_ID, this.userId);

    call.enqueue(new Callback<WebshowModel>() {
      @Override
      public void onResponse(Call<WebshowModel> call, Response<WebshowModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          WebshowModel getresponsemodel = response.body();
          ArrayList<WebshowModel.WebshowDetails> responseData =
              (ArrayList<WebshowModel.WebshowDetails>) getresponsemodel.getData();

          if (responseData != null && !responseData.isEmpty()) {
            TrailerAdapter adapter = new TrailerAdapter(
                WebShowsActivity.this, responseData, FEATURED_TRAILERS_ID);
            recyclerFeaturedTrailers.setAdapter(adapter);
          } else {
            lblNoFeaturedTrailer.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onFailure(Call<WebshowModel> call, Throwable t) {

      }
    });
  }

  private void getMovieShows() {
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<WebshowModel> call =
        methods.getWebshowVideos(this.page, this.pageLimit, MOVIESHOWS_ID, this.userId);

    call.enqueue(new Callback<WebshowModel>() {
      @Override
      public void onResponse(Call<WebshowModel> call, Response<WebshowModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          WebshowModel getresponsemodel = response.body();
          ArrayList<WebshowModel.WebshowDetails> responseData =
              (ArrayList<WebshowModel.WebshowDetails>) getresponsemodel.getData();

          if (responseData != null && !responseData.isEmpty()) {
            WebShowsAdapter adapter =
                new WebShowsAdapter(WebShowsActivity.this, responseData, MOVIESHOWS_ID);
            recyclerMovieShows.setAdapter(adapter);
          } else {
            lblNoMovieShows.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onFailure(Call<WebshowModel> call, Throwable t) {

      }
    });
  }

  private void getClipVideos() {
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<WebshowModel> call =
        methods.getWebshowVideos(this.page, this.pageLimit, CLIPS_ID, this.userId);

    call.enqueue(new Callback<WebshowModel>() {
      @Override
      public void onResponse(Call<WebshowModel> call, Response<WebshowModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          WebshowModel getresponsemodel = response.body();
          ArrayList<WebshowModel.WebshowDetails> responseData =
              (ArrayList<WebshowModel.WebshowDetails>) getresponsemodel.getData();

          if (responseData != null && !responseData.isEmpty()) {
            ClipsAdapter adapter = new ClipsAdapter(WebShowsActivity.this, responseData);
            recyclerClips.setAdapter(adapter);
          } else {
            lblNoClips.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onFailure(Call<WebshowModel> call, Throwable t) {

      }
    });
  }

  private void showProgressDialog(String msg) {
    if (this == null || msg == null) {
      return;
    }

    this.progressDialog = new ProgressDialog(this);
    this.progressDialog.setMessage(msg);
    this.progressDialog.show();
  }

  private void hideProgressDialog() {
    if (this.progressDialog == null) {
      return;
    }
    this.progressDialog.hide();
  }

  // payment methods
  private void initiateOrder(String webshowId) {
    this.showProgressDialog("Initiating payment...");
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<TxnTokenResponse> call = methods.initiateOrder(this.userId, "1.0", webshowId);
    call.enqueue(new Callback<TxnTokenResponse>() {
      @Override
      public void onResponse(Call<TxnTokenResponse> call, Response<TxnTokenResponse> response) {
        hideProgressDialog();
        if (response.code() == 200) {
          TxnTokenResponse tokenResponse = response.body();
          String txnToken = tokenResponse.getTokenBody().getTxnToken();

          placeOrder(
              tokenResponse.getMid(),
              tokenResponse.getOrderId(),
              txnToken,
              tokenResponse.getAmount());
        }
      }

      @Override
      public void onFailure(Call<TxnTokenResponse> call, Throwable t) {
        hideProgressDialog();
        Utils.showShortToast(WebShowsActivity.this, "Failed to initiate payment...");
      }
    });
  }

  private void placeOrder(String mid, String orderId, String txnToken, String amount) {
    //String callback = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId;
    String callback = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId;
    PaytmOrder paytmOrder = new PaytmOrder(orderId, mid, txnToken, amount, callback);
    TransactionManager manager = new TransactionManager(paytmOrder, this);
    manager.startTransaction(this, PAYMENT_REQUEST_CODE);
  }

  private void insertWebshowPayment(String transactionId) {
    this.showProgressDialog("Updating payment... Please wait...");

    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Payment payment = new Payment();
    payment.setTransactionId(transactionId);
    payment.setUserId(this.userId);
    payment.setWebshowId(this.selectedLiveShow.getWebshowId());
    payment.setPaymentStatus("Done");
    payment.setPaymentPlatform("Paytm");

    Call<PaymentStatus> call = methods.insertWebshowPayment(payment);

    call.enqueue(new Callback<PaymentStatus>() {
      @Override
      public void onResponse(Call<PaymentStatus> call, Response<PaymentStatus> response) {
        hideProgressDialog();
        int statusCode = response.code();
        if (statusCode == 200) {
          selectedLiveShow.setUserPaid(true);
          joinLive();
        }
      }

      @Override
      public void onFailure(Call<PaymentStatus> call, Throwable t) {
        hideProgressDialog();
        Utils.showShortToast(WebShowsActivity.this, "Payment failed");
      }
    });
  }

  @Override
  public void onTransactionResponse(Bundle bundle) {
    this.hideProgressDialog();
  }

  @Override
  public void networkNotAvailable() {
    this.hideProgressDialog();
  }

  @Override
  public void clientAuthenticationFailed(String s) {
    this.hideProgressDialog();
  }

  @Override
  public void someUIErrorOccurred(String s) {
    this.hideProgressDialog();
  }

  @Override
  public void onErrorLoadingWebPage(int i, String s, String s1) {
    this.hideProgressDialog();
  }

  @Override
  public void onBackPressedCancelTransaction() {
    this.hideProgressDialog();
  }

  @Override
  public void onTransactionCancel(String s, Bundle bundle) {
    this.hideProgressDialog();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    hideProgressDialog();
    if (PAYMENT_REQUEST_CODE == requestCode) {
      if (data != null && data.getExtras() != null) {
        String response = data.getStringExtra("response");
        if (response != null && response.trim().length() > 0) {
          String txnId = null;
          String errorMsg = null;
          String responseMsg = null;
          String status = null;
          try {
            JSONObject responseObj = new JSONObject(response);
            txnId = responseObj.optString("TXNID");
            errorMsg = responseObj.optString("errorMessage");
            status = responseObj.optString("STATUS");
            responseMsg = responseObj.optString("RESPMSG");
          } catch (JSONException e) {
            // do nothing
          }

          if (status != null && status.equalsIgnoreCase("TXN_FAILURE")) {
            if (errorMsg == null || errorMsg.trim().length() == 0) {
              errorMsg = "Could not process payment";
            }
            Utils.showShortToast(WebShowsActivity.this, errorMsg);
            return;
          }

          if (responseMsg != null && responseMsg.trim().length() > 0) {
            Utils.showShortToast(WebShowsActivity.this, responseMsg);
          }
          insertWebshowPayment(txnId);
        } else {
          String message = data.getStringExtra("nativeSdkForMerchantMessage");
          if (message != null && message.trim().length() > 0) {
            if (message.equals("onBackPressedCancelTransaction") || message.equals(
                "onTransactionCancel")) {
              Utils.showShortToast(this, "Transaction cancelled.");
            } else if (message.equals("networkNotAvailable")) {
              Utils.showShortToast(this, "Please check your network.");
            } else if (message.equals("clientAuthenticationFailed")) {
              Utils.showShortToast(this, "Authentication Failed.");
            } else {
              Utils.showShortToast(this, "Something went wrong.");
            }
          } else {
            Utils.showShortToast(this, "Something went wrong.");
          }
        }
      }
    }
  }

  private void shareImage(String imageUrl, String broadcasterName) {
    Log.e("url", ImageUrlUtils.getUpdatedImageUrl(imageUrl, "large"));
    IntentUtils.removeShareLinkTempImage();
    Picasso.with(this).load(ImageUrlUtils.getUpdatedImageUrl(imageUrl, "large")).into(new Target() {
      @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        IntentUtils.shareBrodcasterWebShowAppDownloadLink(WebShowsActivity.this, bitmap,
            broadcasterName);
      }

      @Override public void onBitmapFailed(Drawable errorDrawable) {
      }

      @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
      }
    });
  }
}
