package in.tagteen.tagteen;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import in.tagteen.tagteen.Model.FriendSeach;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.RewardsPaymentOptionInputModel;
import in.tagteen.tagteen.Model.SimpleActionModel;
import in.tagteen.tagteen.Model.UserURockInputJson;
import in.tagteen.tagteen.Model.UserURocksModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardsActivity extends AppCompatActivity {
  private ImageView imgBack;
  private ImageView imgWallet;
  private ImageView imgAdminAccess;

  private TextView lblEqualsOneRupee;
  private TextView lblRewardsInfo;
  //private TextView lblTotalEarned;
  private TextView lblEarnedThisWeek;
  private TextView lblInstantRewardBadge;
  private TextView lblTermsOfRewards;
  private TextView lblMarqueeText;
  private KonfettiView viewKonfetti;
  private boolean unlocked = false;

  private TextView lblSupportersCount;
  private TextView lblVideosCount;
  private TextView lblURocksCount;
  private TextView txtRewardStatus;
  private TextView txtRewardDescription;

  private ImageView imgSelfieBanner;

  private int earnedThisWeek = 0;
  private String loggedInUserId =
      SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
  private String token =
      SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
  private String paymentOption = SharedPreferenceSingleton.getInstance()
      .getStringPreference(RegistrationConstants.REWARDS_PAYMENT_OPTION);
  private String paymentNumber = SharedPreferenceSingleton.getInstance()
      .getStringPreference(RegistrationConstants.REWARDS_PAYMENT_NUMBER);

  private static final String TAG = "RewardsActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_rewards);

    this.initWidgets();
    this.bindEvents();

    this.loadData();
    this.checkUserHasTalentVideos();
    this.getTopRockstars();
  }

  private void loadData() {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        getUserEarned();
      }
    }, 200);
  }

  private void initWidgets() {
    int supportersCount = getIntent().getIntExtra(Constants.SUPPORTERS_COUNT, 0);
    int videosCount = getIntent().getIntExtra(Constants.VIDEOS_COUNT, 0);
    int uRocksCount = getIntent().getIntExtra(Constants.U_ROCKS_COUNT, 0);

    this.lblSupportersCount = findViewById(R.id.lblSupportersCount);
    this.lblSupportersCount.setText("" + supportersCount);
    this.lblVideosCount = findViewById(R.id.lblVideosCount);
    this.lblVideosCount.setText("" + videosCount);
    this.lblURocksCount = findViewById(R.id.lblURocksCount);
    this.lblURocksCount.setText("" + uRocksCount);
    txtRewardStatus = findViewById(R.id.txt_reward_status);
    txtRewardDescription = findViewById(R.id.txt_reward_description);

    this.imgBack = (ImageView) findViewById(R.id.imgBack);
    this.imgWallet = (ImageView) findViewById(R.id.imgWallet);
    this.lblRewardsInfo = (TextView) findViewById(R.id.lblRewardsInfo);
    //this.lblTotalEarned = (TextView) findViewById(R.id.lblTotalEarnedNew);
    //this.lblTotalEarned.setSelected(true);
    this.lblEarnedThisWeek = (TextView) findViewById(R.id.lblEarnedThisWeek);
    this.viewKonfetti = (KonfettiView) findViewById(R.id.viewKonfetti);
    this.imgAdminAccess = (ImageView) findViewById(R.id.imgAdminAccess);
    this.lblInstantRewardBadge = findViewById(R.id.lblInstantRewardBadge);
    this.lblEqualsOneRupee = findViewById(R.id.lblEqualsOneRupee);
    this.lblMarqueeText = findViewById(R.id.lblMarqueeText);
    this.lblMarqueeText.setSelected(true);

    //this.lblRewardsInfo.setText(Utils.getRewardsInfo(this));
    this.lblEqualsOneRupee.setText(Utils.getEqualsOneRupeeString(this) + getString(R.string.star));

    this.lblTermsOfRewards = findViewById(R.id.lblTermsOfRewards);
    SpannableString ss =
        new SpannableString(getString(R.string.star) + " " + getString(R.string.terms_of_rewards));
    ClickableSpan clickableSpan = new ClickableSpan() {
      @Override
      public void onClick(View view) {
        moveToTermsOfRewards();
      }

      @Override
      public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(true);
      }
    };
    ss.setSpan(clickableSpan, 2, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    this.lblTermsOfRewards.setText(ss);
    this.lblTermsOfRewards.setMovementMethod(LinkMovementMethod.getInstance());
    this.lblTermsOfRewards.setHighlightColor(Color.TRANSPARENT);

    this.imgSelfieBanner = findViewById(R.id.imgSelfieBanner);
    validateRewardStatus(supportersCount, uRocksCount, videosCount);
  }

  @SuppressLint("SetTextI18n")
  private void validateRewardStatus(int supportersCount, int uRocksCount, int videosCount) {
    if (supportersCount > 100 && videosCount > 20 && uRocksCount > 1000) {
      txtRewardStatus.setText("Your Profile is Monetised");
      txtRewardDescription.setVisibility(View.GONE);
    } else {
      txtRewardStatus.setText("Your Profile is Not Monitised Yet");
      txtRewardDescription.setVisibility(View.VISIBLE);
      txtRewardDescription.setText(
          "You need a minimum 100 supporters, 20 talent videos and 1000 U Rocks for monetisation");
    }
  }

  private void moveToTermsOfRewards() {
    Intent intent = new Intent(RewardsActivity.this, TermsOfRewardsActivity.class);
    startActivity(intent);
  }

  private void bindEvents() {
    this.imgBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    this.imgWallet.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showPaymentOptionDialog();
      }
    });
    this.lblEarnedThisWeek.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        unlockAction();
        lblEarnedThisWeek.setText(getString(R.string.rupee_symbol) + " " + earnedThisWeek);
      }
    });
    this.imgAdminAccess.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        moveToAdminAccessScreen();
      }
    });
    this.imgSelfieBanner.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Utils.showAlertDialog(RewardsActivity.this, Constants.SELFIE_ALERT_MSG, "Rewards");
      }
    });
  }

  private void moveToAdminAccessScreen() {
    Intent intent = new Intent(this, AdminAccessActivity.class);
    startActivity(intent);
  }

  private void unlockAction() {
    if (this.unlocked) {
      return;
    }
    this.unlocked = true;
    this.viewKonfetti.build()
        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
        .setDirection(0.0, 359.0)
        .setSpeed(1f, 5f)
        .setFadeOutEnabled(true)
        .setTimeToLive(2000L)
        .addShapes(Shape.RECT, Shape.CIRCLE)
        .addSizes(new Size(12, 5))
        .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
        .burst(300);
  }

  private void showPaymentOptionDialog() {
    final Dialog dialog = new Dialog(this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_payment_option);

    final RadioButton radioGooglePay = (RadioButton) dialog.findViewById(R.id.radioGooglePay);
    final RadioButton radioPaytm = (RadioButton) dialog.findViewById(R.id.radioPaytm);
    final RadioButton radioPhonePe = (RadioButton) dialog.findViewById(R.id.radioPhonePe);
    final EditText txtMobileNumber = (EditText) dialog.findViewById(R.id.txtMobileNumer);
    Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

    if (this.paymentOption != null) {
      if (this.paymentOption.equals(RegistrationConstants.GOOGLE_PAY)) {
        radioGooglePay.setChecked(true);
      } else if (this.paymentOption.equals(RegistrationConstants.PAYTM)) {
        radioPaytm.setChecked(true);
      } else if (this.paymentOption.equals(RegistrationConstants.PHONE_PE)) {
        radioPhonePe.setChecked(true);
      }

      if (this.paymentNumber != null) {
        txtMobileNumber.setText(this.paymentNumber);
      }
    }

    radioGooglePay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        radioPaytm.setChecked(false);
        radioPhonePe.setChecked(false);
      }
    });
    radioPaytm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        radioGooglePay.setChecked(false);
        radioPhonePe.setChecked(false);
      }
    });
    radioPhonePe.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        radioGooglePay.setChecked(false);
        radioPaytm.setChecked(false);
      }
    });
    btnSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!radioGooglePay.isChecked() && !radioPaytm.isChecked() && !radioPhonePe.isChecked()) {
          Utils.showShortToast(RewardsActivity.this, "Please select payment option");
          return;
        }
        String mobileNumber = txtMobileNumber.getText().toString();
        if (mobileNumber == null || mobileNumber.length() < 10) {
          txtMobileNumber.setError("Please enter valid mobile number");
          txtMobileNumber.requestFocus();
          return;
        }
        RewardsPaymentOptionInputModel inputJson =
            new RewardsPaymentOptionInputModel(loggedInUserId);
        if (radioGooglePay.isChecked()) {
          inputJson.setGooglePay(mobileNumber);
        } else if (radioPaytm.isChecked()) {
          inputJson.setPaytm(mobileNumber);
        } else {
          inputJson.setPhonePe(mobileNumber);
        }
        updatePaymentOption(inputJson);
        dialog.dismiss();
      }
    });
    dialog.show();
  }

  private void getUserEarned() {
    UserURockInputJson inputJson = new UserURockInputJson();
    inputJson.setUserId(this.loggedInUserId);
    inputJson.setApiSelect(1);

    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<UserURocksModel> call = methods.getUserURocksCount(inputJson, token);
    call.enqueue(new Callback<UserURocksModel>() {
      @Override
      public void onResponse(Call<UserURocksModel> call, Response<UserURocksModel> response) {
        if (response.code() == 200) {
          UserURocksModel model = response.body();
          //lblTotalEarned.setText(getString(R.string.rupee_symbol) + " " + model.getTotalCount());
          earnedThisWeek = model.getWeeklyCount();
        }
      }

      @Override
      public void onFailure(Call<UserURocksModel> call, Throwable t) {
        Log.d(TAG, "Failed : " + call.request().url().toString());
        t.printStackTrace();
      }
    });
  }

  private void updatePaymentOption(final RewardsPaymentOptionInputModel inputJson) {
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<SimpleActionModel> call = methods.updateRewardsPaymentOption(inputJson);
    call.enqueue(new Callback<SimpleActionModel>() {
      @Override
      public void onResponse(Call<SimpleActionModel> call, Response<SimpleActionModel> response) {
        if (response.code() == 200) {
          SimpleActionModel model = response.body();
          if (model.getSuccess()) {
            if (inputJson.getGooglePay() != null) {
              SharedPreferenceSingleton.getInstance().writeStringPreference(
                  RegistrationConstants.REWARDS_PAYMENT_OPTION, RegistrationConstants.GOOGLE_PAY);
              SharedPreferenceSingleton.getInstance().writeStringPreference(
                  RegistrationConstants.REWARDS_PAYMENT_NUMBER, inputJson.getGooglePay());
            } else if (inputJson.getPaytm() != null) {
              SharedPreferenceSingleton.getInstance().writeStringPreference(
                  RegistrationConstants.REWARDS_PAYMENT_OPTION, RegistrationConstants.PAYTM);
              SharedPreferenceSingleton.getInstance().writeStringPreference(
                  RegistrationConstants.REWARDS_PAYMENT_NUMBER, inputJson.getPaytm());
            } else if (inputJson.getPhonePe() != null) {
              SharedPreferenceSingleton.getInstance().writeStringPreference(
                  RegistrationConstants.REWARDS_PAYMENT_OPTION, RegistrationConstants.PHONE_PE);
              SharedPreferenceSingleton.getInstance().writeStringPreference(
                  RegistrationConstants.REWARDS_PAYMENT_NUMBER, inputJson.getPhonePe());
            }
            paymentOption = SharedPreferenceSingleton.getInstance()
                .getStringPreference(RegistrationConstants.REWARDS_PAYMENT_OPTION);
            paymentNumber = SharedPreferenceSingleton.getInstance()
                .getStringPreference(RegistrationConstants.REWARDS_PAYMENT_NUMBER);
            Utils.showShortToast(RewardsActivity.this, "Successfully updated");
          }
        }
      }

      @Override
      public void onFailure(Call<SimpleActionModel> call, Throwable t) {
        Log.d(TAG, "Failed : " + call.request().url().toString());
        t.printStackTrace();
      }
    });
  }

  private void checkUserHasTalentVideos() {
    if (!Utils.isNetworkAvailable(this)) {
      return;
    }
    String token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<GetPostResponseModel> call = methods.getUserVideos(loggedInUserId, 1, 1, token);
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(Call<GetPostResponseModel> call,
          Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          GetPostResponseModel responseModel = response.body();
          if (responseModel.getData() != null && responseModel.getData().size() > 0) {
            lblInstantRewardBadge.setVisibility(View.VISIBLE);
          } else {
            lblInstantRewardBadge.setVisibility(View.GONE);
          }
        }
      }

      @Override
      public void onFailure(Call<GetPostResponseModel> call, Throwable t) {
        // do nothing
      }
    });
  }

  private void getTopRockstars() {
    if (!Utils.isNetworkAvailable(this)) {
      return;
    }
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<FriendSeach> call = methods.getAllRockstars();
    call.enqueue(new Callback<FriendSeach>() {
      @Override
      public void onResponse(Call<FriendSeach> call, Response<FriendSeach> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          FriendSeach responseModel = response.body();
          ArrayList<FriendSeach.UserInfo> usersList = responseModel.getUserInfos();
          if (usersList != null) {
            String rockstars = "Rockstars ";
            int i = 0;
            for (FriendSeach.UserInfo userInfo : usersList) {
              if (i > 9) {
                break;
              }
              String username = userInfo.getFirst_name();
              if (userInfo.getLast_name() != null) {
                username += " " + userInfo.getLast_name();
              }
              if (i > 0) {
                rockstars += "  ";
              }
              rockstars += (i + 1) + ". " + username;
              i++;
            }
            lblMarqueeText.setText(rockstars);
          }
        }
      }

      @Override
      public void onFailure(Call<FriendSeach> call, Throwable t) {

      }
    });
  }
}
