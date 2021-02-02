package in.tagteen.tagteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import org.json.JSONException;
import org.json.JSONObject;

import in.tagteen.tagteen.FCM.Config;
import in.tagteen.tagteen.chatting.socket.SocketConnection;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import in.tagteen.tagteen.configurations.AppConfigurationSetting;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class SplashActivityWithAnima extends AppCompatActivity implements AsyncResponse {
  ImageView imageView1;
  Animation bottomUp, bottomUp1;
  CoordinatorLayout lc;
  LinearLayout linearLayout, linearLayout_container;
  private View root;
  private final int SPLASH_DISPLAY_LENGTH = 1000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(in.tagteen.tagteen.R.layout.activity_splash_with_anima);
    getWindow().getAttributes().windowAnimations = in.tagteen.tagteen.R.style.Fade;
    lc = (CoordinatorLayout) findViewById(in.tagteen.tagteen.R.id.main_screen);
    imageView1 = (ImageView) findViewById(in.tagteen.tagteen.R.id.imgLogo1);

    SharedPreferenceSingleton.getInstance().init(SplashActivityWithAnima.this);
    String androidDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
        Settings.Secure.ANDROID_ID);
    SharedPreferenceSingleton.getInstance()
        .writeStringPreference(RegistrationConstants.DEVICE_ID, androidDeviceId);

    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
    String refreshedToken = pref.getString("regId", "");
    System.out.println("GCM Registration Token:" + refreshedToken);

    linearLayout_container = (LinearLayout) findViewById(in.tagteen.tagteen.R.id.splash_icon);
    bottomUp = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.slide_up);
    bottomUp1 = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.mid_scale_anim);
    bottomUp.setDuration(1500);
    linearLayout_container.startAnimation(bottomUp);
    linearLayout = (LinearLayout) findViewById(in.tagteen.tagteen.R.id.di_backgr_screen);
    bottomUp.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation arg0) {

      }

      @Override
      public void onAnimationRepeat(Animation arg0) {
      }

      @Override
      public void onAnimationEnd(Animation arg0) {
        linearLayout.setVisibility(View.VISIBLE);
        bottomUp1.setDuration(700);
        linearLayout.startAnimation(bottomUp1);
      }
    });

    bottomUp1.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation arg0) {

      }

      @Override
      public void onAnimationRepeat(Animation arg0) {
      }

      @Override
      public void onAnimationEnd(Animation arg0) {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            SharedPreferenceSingleton.getInstance().init(SplashActivityWithAnima.this);
            String Status = SharedPreferenceSingleton.getInstance()
                .getStringPreference(SplashActivityWithAnima.this,
                    ApplicationConstants.APPLICATION_STATUS);
            if (Status.equalsIgnoreCase(AppConfigurationSetting.AppLoginStatus.LOGIN_SUCCESS)) {
              moveToMainScreen();
            } else if (Status.equalsIgnoreCase(
                AppConfigurationSetting.AppLoginStatus.VERIFY_OTP_STATE)) {
              Intent intent = new Intent(SplashActivityWithAnima.this, OTPEntryActivity.class);
              startActivity(intent);
              SplashActivityWithAnima.this.finish();
            } else {
              loginAsGuest();
              //moveToMainScreenAsGuest();
            }
          }
        }, SPLASH_DISPLAY_LENGTH);
      }
    });
  }

  private void loginAsGuest() {
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;

    JSONObject jsonObject = new JSONObject();
    mWorker.execute(
            ServerConnector.REQUEST_FOR_GUEST_LOGIN,
            jsonObject.toString(),
            RequestConstants.GET_REQUEST,
            RequestConstants.HEADER_YES,
            RequestConstants.REQUEST_FOR_GUEST_LOGIN);
  }

  private void moveToMainScreenAsGuest() {
    Intent intent = new Intent(SplashActivityWithAnima.this, MainDashboardActivity.class);
    intent.putExtra("fragmentload", "");
    startActivity(intent);
    finish();
  }

  private void moveToMainScreen() {
    boolean isChatInitiated = SharedPreferenceSingleton.getInstance()
        .getBoolPreference(RegistrationConstants.IS_CHAT_INITIATED);
    isChatInitiated = true;
    if (!isChatInitiated) {
      this.logoutApp();
    } else {
      Intent intent = new Intent(SplashActivityWithAnima.this, MainDashboardActivity.class);
      intent.putExtra("fragmentload", "");
      startActivity(intent);
      finish();
    }
  }

  private void logoutApp() {
    SharedPreferenceSingleton.getInstance().clearPreference();
    ChatSessionManager.getInstance().clear();//tony00
    SharedPreferenceSingleton.getInstance()
        .writeBoolPreference(RegistrationConstants.IS_CHAT_INITIATED, true);

    Intent intent = new Intent(this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
  }

  @Override
  public void onRefresh() {

  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    switch (REQUEST_NUMBER) {
      case RequestConstants.REQUEST_FOR_GUEST_LOGIN:
        this.parseGuestLoginResponse(output);
        break;
    }
  }

  private void parseGuestLoginResponse(String jsonString) {
    try {
      JSONObject loginResponse = new JSONObject(jsonString);
      boolean responseStatus = loginResponse.getBoolean(RegistrationConstants.SUCCESS);
      if (responseStatus) {
        String Data = loginResponse.getString(RegistrationConstants.DATA);
        JSONObject responseData = new JSONObject(Data);

        SharedPreferenceSingleton.getInstance().init(SplashActivityWithAnima.this);
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.FIRST_NAME,
                        responseData.getString(RegistrationConstants.FIRST_NAME));
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.LAST_NAME,
                        responseData.getString(RegistrationConstants.LAST_NAME));
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.DATE_OF_BIRTHDAY,
                        responseData.getString(RegistrationConstants.DATE_OF_BIRTHDAY));
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.TOKEN,
                        responseData.getString(RegistrationConstants.TOKEN));
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.TAGGED_NUMBER,
                        responseData.getString(RegistrationConstants.TAGGED_NUMBER));
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.USER_ID,
                        responseData.getString(RegistrationConstants.USER_ID));
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.E_MAIL,
                        responseData.getString(RegistrationConstants.E_MAIL));
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.PROFILE_URL,
                        responseData.getString(RegistrationConstants.PROFILE_URL));
        SharedPreferenceSingleton.getInstance()
                .writeBoolPreference(RegistrationConstants.IS_TAGGED_USER,
                        responseData.getBoolean(RegistrationConstants.IS_TAGGED_USER));

        this.moveToMainScreenAsGuest();
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}