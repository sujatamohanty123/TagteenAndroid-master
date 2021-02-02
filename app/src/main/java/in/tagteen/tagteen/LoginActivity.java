package in.tagteen.tagteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import in.tagteen.tagteen.FCM.Config;
import in.tagteen.tagteen.FCM.FcmTokenManager;
import in.tagteen.tagteen.chatting.socket.SocketConnection;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.GeneralApiUtils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

//import com.bcgdv.asia.lib.dots.DotsProgressIndicator;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {
  private LinearLayout imageButton;
  private LinearLayout layoutPassword;
  private AppCompatEditText tag_EditText, password_EditText;
  private Animation fade_In, fade_Out;
  private boolean isButtonFade;
  private boolean isButtonScrolled = false;
  private ImageView image;
  private LinearLayout backgroundView;
  private Animation Alpha, bottomUp;
  private ScrollView welComeScrollView;

  // activity state
  static boolean isActivityActive = false;

  private String mVerificationId;
  private boolean mIsOtpVerified = false;
  private boolean mVerificationInProgress = false;
  private FirebaseAuth mAuth;
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallback;
  private PhoneAuthProvider.ForceResendingToken mResendToken;

  private static final String DEFAULT_COUNTRY_CODE = "+91";
  private static final String DEFAULT_FIRST_NAME = "Tagteen";
  private static final String DEFAULT_LAST_NAME = "User";
  private static final String DEFAULT_PROFILE_PIC = "https://ttprofileurl.s3.ap-south-1.amazonaws.com/DefaultProfilePic.png";

  private final TextWatcher watcher1 = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
      if (!tag_EditText.getText().toString().equals("")) {

      }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
      if (tag_EditText.getText().toString().equals("")) {
        tag_EditText.setHint("Enter mobile number");
        if (!isButtonFade) {
          imageButton.startAnimation(fade_Out);
          isButtonFade = true;
        }
      } else {
        if (isButtonFade) {
          imageButton.startAnimation(fade_In);
          isButtonFade = false;
          if (!isButtonScrolled) {
            welComeScrollView.scrollBy(0, +30);
            isButtonScrolled = true;
          }
        }
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.welcome_activity);
    String mobileNo = getIntent().getStringExtra(RegistrationConstants.MOBILE);
    imageButton = (LinearLayout) findViewById(R.id.welCome_imageButton);
    tag_EditText = (AppCompatEditText) findViewById(R.id.input_username);
    password_EditText = (AppCompatEditText) findViewById(R.id.input_password);
    welComeScrollView = (ScrollView) findViewById(R.id.welCome_scrollView);
    layoutPassword = findViewById(R.id.input_layout_password);

    tag_EditText.setFocusable(false);
    tag_EditText.setFocusableInTouchMode(true);
    tag_EditText.setHint("Enter mobile number");
    password_EditText.setHint("Enter OTP");
    if (mobileNo != null) {
      tag_EditText.setText("" + mobileNo);
    }
    mAuth = FirebaseAuth.getInstance();
    this.initVerificationCallback();

    fade_In = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.fade_in);
    fade_Out = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.fade_out);
    imageButton.startAnimation(fade_Out);
    isButtonFade = true;

    imageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //loginRequest();
        verifyOTPAndLogin();
      }
    });

    tag_EditText.addTextChangedListener(watcher1);

    tag_EditText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });

    tag_EditText.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return false;
      }
    });

    backgroundView = (LinearLayout) findViewById(in.tagteen.tagteen.R.id.container_welcome_screen);
    backgroundView.setVisibility(View.GONE);
    image = (ImageView) findViewById(in.tagteen.tagteen.R.id.login_logo_welcome_screen);
    bottomUp = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.slide_up);
    Alpha = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.alpha);
    image.setVisibility(View.VISIBLE);
    bottomUp.setDuration(Constants.animationTime);
    image.startAnimation(bottomUp);

    bottomUp.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation arg0) {
      }

      @Override
      public void onAnimationRepeat(Animation arg0) {
      }

      @Override
      public void onAnimationEnd(Animation arg0) {
        Alpha.setDuration(Constants.animationTime);
        backgroundView.startAnimation(Alpha);
      }
    });

    Alpha.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation arg0) {
        backgroundView.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationRepeat(Animation arg0) {
      }

      @Override
      public void onAnimationEnd(Animation arg0) {
      }
    });
  }

  private void initVerificationCallback() {
    this.verificationCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

      @Override
      public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        signInWithPhoneAuthCredential(phoneAuthCredential);
        mVerificationInProgress = false;
      }

      @Override
      public void onVerificationFailed(@NonNull FirebaseException e) {
        mVerificationInProgress = false;
        enableButton(true);
        Utils.showToast(LoginActivity.this, e.getMessage());
      }

      @Override
      public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        mVerificationId = verificationId;
        mResendToken = forceResendingToken;
        enableButton(true);
      }
    };
  }

  private void enableButton(boolean enable) {
    this.imageButton.setEnabled(enable);
    if (enable) {
      if (!isButtonFade) {
        imageButton.startAnimation(fade_Out);
        isButtonFade = true;
      }
    } else {
      if (isButtonFade) {
        imageButton.startAnimation(fade_In);
        isButtonFade = false;
      }
    }
  }

  private Typeface fontTyperFace() {
    Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/rockwellstd.ttf");
    return typeface;
  }

  private void verifyOTPAndLogin() {
    this.enableButton(false);
    this.layoutPassword.setVisibility(View.VISIBLE);
    String otp = Objects.requireNonNull(this.password_EditText.getText()).toString().trim();
    if (!GeneralApiUtils.isStringEmpty(otp) && mIsOtpVerified) {
      loginRequest();
    } else if (!GeneralApiUtils.isStringEmpty(otp) &&
            !GeneralApiUtils.isStringEmpty(mVerificationId)) {
      verifyPhoneNumberWithCode(mVerificationId, otp);
    } if (!this.mVerificationInProgress) {
      this.startPhoneNumberVerification();
    } else {
      if (this.password_EditText.getText().toString().trim().isEmpty()) {
        this.password_EditText.setError("Enter OTP");
        this.password_EditText.requestFocus();
      }
      this.enableButton(true);
    }
  }

  private void startPhoneNumberVerification() {
    String phoneNumber = this.tag_EditText.getText().toString();
    if (phoneNumber == null || phoneNumber.trim().length() == 0) {
      this.tag_EditText.setError("Enter mobile number");
      this.tag_EditText.requestFocus();
      return;
    }

    if (!phoneNumber.startsWith("+91")) {
      phoneNumber = "+91" + phoneNumber;
    }
    PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            verificationCallback);        // OnVerificationStateChangedCallbacks
    this.mVerificationInProgress = true;
  }

  private void verifyPhoneNumberWithCode(String verificationId, String code) {
    // [START verify_with_code]
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
    // [END verify_with_code]
    signInWithPhoneAuthCredential(credential);
  }

  private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && !mIsOtpVerified) {
                  // Sign in success, update UI with the signed-in user's information
                  FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                  if (user != null) {
                    if (Objects.requireNonNull(password_EditText.getText())
                            .toString()
                            .trim()
                            .isEmpty()) {
                      password_EditText.setText(credential.getSmsCode());
                    }
                    mIsOtpVerified = true;
                    loginRequest();
                  }
                } else if (!task.isSuccessful()) {
                  // Sign in failed, display a message and update the UI
                  if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Utils.showToast(LoginActivity.this, "Invalid code");
                    enableButton(true);
                  }
                }
              }
            });
  }

  @Override
  public void finish() {
    super.finish();
  }

  public void loginRequest() {
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = LoginActivity.this;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put(RegistrationConstants.MOBILE, tag_EditText.getText().toString().trim());
      jsonObject.put(RegistrationConstants.COUNTRY_CODE, DEFAULT_COUNTRY_CODE);
      jsonObject.put(RegistrationConstants.FIRST_NAME, DEFAULT_FIRST_NAME);
      jsonObject.put(RegistrationConstants.LAST_NAME, DEFAULT_LAST_NAME);
      jsonObject.put(RegistrationConstants.PROFILE_URL, DEFAULT_PROFILE_PIC);
      String androidDeviceId =
              Settings.Secure.getString(
                      getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
      jsonObject.put(RegistrationConstants.DEVICE_ID, androidDeviceId);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    mWorker.execute(
        ServerConnector.REQUEST_FOR_REGISTER_USER,
            jsonObject.toString(),
        RequestConstants.POST_REQUEST,
        RequestConstants.HEADER_NO,
        RequestConstants.REQUEST_FOR_REGISTER);
  }

  @Override
  public void onRefresh() {
  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    if (isActivityActive == false) {
      return;
    }
    switch (REQUEST_NUMBER) {
      case RequestConstants.REQUEST_FOR_REGISTER: {
        try {
          JSONObject loginResponse = new JSONObject(output);
          boolean responseStatus = loginResponse.getBoolean(RegistrationConstants.SUCCESS);
          if (responseStatus) {
            String Data = loginResponse.getString(RegistrationConstants.DATA);
            JSONObject responseData = new JSONObject(Data);

            String userId = responseData.getString(RegistrationConstants.USER_ID);
            String token = responseData.getString(RegistrationConstants.TOKEN);
            ChatSessionManager chatSessionManager = ChatSessionManager.getInstance();
            chatSessionManager.initiateSession(userId, token);
            SocketConnection.getConnection().bindCreator();

            SharedPreferenceSingleton.getInstance().init(LoginActivity.this);
            SharedPreferenceSingleton.getInstance()
                    .writeStringPreference(Constants.ACCESS_TOKEN_UPDATED_ON, Utils.getNowInDateString());
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.FIRST_NAME,
                    responseData.getString(RegistrationConstants.FIRST_NAME));
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.LAST_NAME,
                    responseData.getString(RegistrationConstants.LAST_NAME));
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.TOKEN, token);
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.TAGGED_NUMBER,
                    responseData.getString(RegistrationConstants.TAGGED_NUMBER));
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.USER_ID, userId);
            SharedPreferenceSingleton.getInstance()
                    .writeStringPreference(RegistrationConstants.MOBILE,
                            responseData.getString(RegistrationConstants.MOBILE));
            /*SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.E_MAIL,
                    responseData.getString(RegistrationConstants.E_MAIL));*/
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.PROFILE_URL,
                    responseData.getString(RegistrationConstants.PROFILE_URL));
            SharedPreferenceSingleton.getInstance()
                .writeBoolPreference(RegistrationConstants.IS_TAGGED_USER,
                    responseData.getBoolean(RegistrationConstants.IS_TAGGED_USER));


            registerFcmToken();
          } else {
            Utils.showAlertDialog(LoginActivity.this, "Invalid Credentials", "Alert");
          }
        } catch (JSONException e) {
          Utils.showAlertDialog(LoginActivity.this, "Invalid Credentials", "Alert");
        }
      }
    }
  }

  //@tony00
  private void registerFcmToken() {
    SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);

    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
        new OnSuccessListener<InstanceIdResult>() {
          @Override public void onSuccess(InstanceIdResult instanceIdResult) {
            FcmTokenManager fcmTokenManager = FcmTokenManager.createFrom(LoginActivity.this);
            fcmTokenManager.saveFcmToken(instanceIdResult.getToken());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("regId", instanceIdResult.getToken());
            editor.commit();
            Intent it = new Intent(LoginActivity.this, MainDashboardActivity.class);
            it.putExtra("fragmentload", "");
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
          }
        });
  }

  @Override
  protected void onStart() {
    isActivityActive = true;
    super.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    isActivityActive = false;
  }
}
