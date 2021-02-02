package in.tagteen.tagteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
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
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.apimodule_retrofit.response.register.Data;
import in.tagteen.tagteen.apimodule_retrofit.response.register.RegisterPostModel;
import in.tagteen.tagteen.apimodule_retrofit.response.register.RegisterUserResponse;
import in.tagteen.tagteen.base.AppBaseActivity;
import in.tagteen.tagteen.chatting.socket.SocketConnection;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import in.tagteen.tagteen.configurations.AppConfigurationSetting;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.screen.signup.SignupDM;
import in.tagteen.tagteen.services.ActiveActivitiesTracker;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.GeneralApiUtils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPEntryActivity extends AppBaseActivity implements AsyncResponse {

  private static final String TAG = "PhoneAuthActivity";
  public static final String EXTRA_SIGNUP_DM = "EXTRA_SIGNUP_DM";

  private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

  private static final int STATE_INITIALIZED = 1;
  private static final int STATE_CODE_SENT = 2;
  private static final int STATE_VERIFY_FAILED = 3;
  private static final int STATE_VERIFY_SUCCESS = 4;
  private static final int STATE_SIGNIN_FAILED = 5;
  private static final int STATE_SIGNIN_SUCCESS = 6;

  // [START declare_auth]
  private FirebaseAuth mAuth;
  // [END declare_auth]

  private boolean mVerificationInProgress = false;
  private String mVerificationId;
  private PhoneAuthProvider.ForceResendingToken mResendToken;
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

  private ImageView image;
  private static OTPEntryActivity instance;
  private AppCompatEditText mOtpInputEditText;

  private TextView otp_number;
  private int[] selectedHobbies;
  private JSONArray jsonArray;

  // activity state
  static boolean isActivityActive = false;
  private SignupDM mSignupDM;
  Apimethods methods;
  private boolean mIsOtpVerified = false;

  public static OTPEntryActivity instance() {
    return instance;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(in.tagteen.tagteen.R.layout.activity_otpentry);
    mAuth = FirebaseAuth.getInstance();
    initComponent();
    otp_number = (TextView) findViewById(R.id.otp_number);

    String mobileNp =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.MOBILE);
    otp_number.setText("+91-" + mobileNp);

    SharedPreferenceSingleton.getInstance().init(this);
    SharedPreferenceSingleton.getInstance()
        .writeStringPreference(ApplicationConstants.APPLICATION_STATUS,
            AppConfigurationSetting.AppLoginStatus.VERIFY_OTP_STATE);
    image = (ImageView) findViewById(in.tagteen.tagteen.R.id.otp_imageButton);
    mOtpInputEditText = (AppCompatEditText) findViewById(R.id.otp_entry_activity);

    //  this.requestForOTP();

    image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String otp = Objects.requireNonNull(mOtpInputEditText.getText()).toString().trim();
        if (!GeneralApiUtils.isStringEmpty(otp) && mIsOtpVerified) {
          sendUserDetail();
        } else if (!GeneralApiUtils.isStringEmpty(otp) && !GeneralApiUtils.isStringEmpty(
            mVerificationId)) {
          verifyPhoneNumberWithCode(mVerificationId, otp);
        } else {
          mOtpInputEditText.setError("Enter OTP");
          mOtpInputEditText.requestFocus();
        }
      }
    });

    mOtpInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
      }
    });

    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

      @Override
      public void onVerificationCompleted(@NotNull PhoneAuthCredential credential) {
        // This callback will be invoked in two situations:
        // 1 - Instant verification. In some cases the phone number can be instantly
        //     verified without needing to send or enter a verification code.
        // 2 - Auto-retrieval. On some devices Google Play services can automatically
        //     detect the incoming verification SMS and perform verification without
        //     user action.
        Log.d("", "onVerificationCompleted:" + credential);
        // [START_EXCLUDE silent]
        mVerificationInProgress = false;
        // [END_EXCLUDE]

        // [START_EXCLUDE silent]
        // Update the UI and attempt sign in with the phone credential
        //  updateUI(STATE_VERIFY_SUCCESS, credential);
        // [END_EXCLUDE]
        signInWithPhoneAuthCredential(credential);
      }

      @Override
      public void onVerificationFailed(FirebaseException e) {
        // This callback is invoked in an invalid request for verification is made,
        // for instance if the the phone number format is not valid.
        Log.w("", "onVerificationFailed", e);
        // [START_EXCLUDE silent]
        mVerificationInProgress = false;
        // [END_EXCLUDE]

        if (e instanceof FirebaseAuthInvalidCredentialsException) {
          // Invalid request
          // [START_EXCLUDE]
          //mBinding.fieldPhoneNumber.setError("Invalid phone number.");
          // [END_EXCLUDE]
        } else if (e instanceof FirebaseTooManyRequestsException) {
          // The SMS quota for the project has been exceeded
          // [START_EXCLUDE]
          Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
              Snackbar.LENGTH_SHORT).show();
          // [END_EXCLUDE]
        }

        // Show a message and update the UI
        // [START_EXCLUDE]
        ///     updateUI(STATE_VERIFY_FAILED);
        // [END_EXCLUDE]
      }

      @Override
      public void onCodeSent(@NonNull String verificationId,
          @NonNull PhoneAuthProvider.ForceResendingToken token) {
        // The SMS verification code has been sent to the provided phone number, we
        // now need to ask the user to enter the code and then construct a credential
        // by combining the code with a verification ID.
        Log.d("", "onCodeSent:" + verificationId);

        // Save verification ID and resending token so we can use them later
        mVerificationId = verificationId;
        mResendToken = token;

        // [START_EXCLUDE]
        // Update UI
        //   updateUI(STATE_CODE_SENT);
        // [END_EXCLUDE]
      }
    };

    startPhoneNumberVerification("+91 " + mobileNp);
  }

  private void initComponent() {
    if (getIntent().getExtras() != null
        && getIntent().getExtras().getParcelable(EXTRA_SIGNUP_DM) != null) {
      mSignupDM = getIntent().getExtras().getParcelable(EXTRA_SIGNUP_DM);
    }
    methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
  }

  @Override
  protected void onStart() {
    super.onStart();
    ActiveActivitiesTracker.activityStarted();
    instance = this;
    isActivityActive = true;
  }

  @Override
  protected void onStop() {
    super.onStop();
    isActivityActive = false;
  }

  public void setOTPField(String code) {
    try {
      mOtpInputEditText.setText(code);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(in.tagteen.tagteen.R.anim.anim_slide_in_right,
        in.tagteen.tagteen.R.anim.anim_slide_out_left);
  }

  @Override
  public void onRefresh() {

  }

  //private void requestForOTP() {
  //  String phoneNumber =
  //      SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.MOBILE);
  //  AsyncWorker mWorker = new AsyncWorker(this);
  //  mWorker.delegate = this;
  //  JSONObject BroadcastObject = new JSONObject();
  //  try {
  //    BroadcastObject.put("mobile", phoneNumber);
  //    BroadcastObject.put("country_code", "+91");
  //  } catch (JSONException e) {
  //    e.printStackTrace();
  //  }
  //  mWorker.execute(
  //      ServerConnector.REQUEST_FOR_OTP,
  //      BroadcastObject.toString(),
  //      RequestConstants.POST_REQUEST,
  //      RequestConstants.HEADER_NO,
  //      RequestConstants.REQUEST_FOR_OTP);
  //}

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    //case RequestConstants.REQUEST_FOR_OTP:
    //  try {
    //    JSONObject jsonObject = new JSONObject(output);
    //    boolean isSuccess = jsonObject.getBoolean("success");
    //    if (!isSuccess) {
    //      Utils.showShortToast(this, "Something went wrong");
    //    }
    //  } catch (JSONException e) {
    //    e.printStackTrace();
    //  }
    //  break;
    if (RequestConstants.REQUEST_FOR_REGISTER.equals(REQUEST_NUMBER)) {
      try {
        JSONObject loginResponse = new JSONObject(output);
        boolean responseStatus = loginResponse.getBoolean(RegistrationConstants.SUCCESS);
        //if (loginResponse.getBoolean("success")) {
        if (!responseStatus) {
          String error_msg = loginResponse.getString("error_msg");
          Utils.showAlertDialog(OTPEntryActivity.this, error_msg, "Alert");
        } else {
          String Data = loginResponse.getString(RegistrationConstants.DATA);
          JSONObject responseData = new JSONObject(Data);

          SharedPreferenceSingleton.getInstance().init(OTPEntryActivity.this);
          //                        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.CURRENTLY_STUDYING, responseData.getString(RegistrationConstants.CURRENTLY_STUDYING));
          //                        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.SCHOOL_NAME, responseData.getString(RegistrationConstants.SCHOOL_NAME));
          //                        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.PIN_CODE, responseData.getString(RegistrationConstants.PIN_CODE));
          //                        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.GENDER, responseData.getString(RegistrationConstants.GENDER));
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.FIRST_NAME,
                  responseData.getString(RegistrationConstants.FIRST_NAME));
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.LAST_NAME,
                  responseData.getString(RegistrationConstants.LAST_NAME));
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.DATE_OF_BIRTHDAY,
                  responseData.getString(RegistrationConstants.DATE_OF_BIRTHDAY));

          try {
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.TOKEN,
                    responseData.getString(RegistrationConstants.TOKEN));
          } catch (JSONException e) {
            e.printStackTrace();
          }
          // SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.HOBBY, responseData.getJSONArray(RegistrationConstants.HOBBY).toString());
          try {
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.TAGGED_NUMBER,
                    responseData.getString(RegistrationConstants.TAGGED_NUMBER));
          } catch (JSONException e) {
            e.printStackTrace();
          }
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.MOBILE,
                  responseData.getString(RegistrationConstants.MOBILE));
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.USER_ID,
                  responseData.getString(RegistrationConstants.USER_ID));
          try {
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.E_MAIL,
                    responseData.getString(RegistrationConstants.E_MAIL));
          } catch (JSONException e) {
            e.printStackTrace();
          }
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.PROFILE_URL,
                  responseData.getString(RegistrationConstants.PROFILE_URL));
          try {
            SharedPreferenceSingleton.getInstance()
                .writeBoolPreference(RegistrationConstants.IS_TAGGED_USER,
                    responseData.getBoolean(RegistrationConstants.IS_TAGGED_USER));
          } catch (JSONException e) {
            e.printStackTrace();
          }

          //tony00
          String userId = responseData.getString(RegistrationConstants.USER_ID);
          try {
            String token = responseData.getString(RegistrationConstants.TOKEN);
            ChatSessionManager chatSessionManager = ChatSessionManager.getInstance();
            chatSessionManager.initiateSession(userId, token);
          } catch (Exception e) {
            e.printStackTrace();
          }

          SocketConnection.getConnection().bindCreator();

          //tony00
          registerFcmToken(responseData.getString(RegistrationConstants.TAGGED_NUMBER));
        }
      } catch (JSONException e) {
        Log.e("error", e.getLocalizedMessage());
        Toast.makeText(OTPEntryActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        Utils.showAlertDialog(OTPEntryActivity.this, "Something went wrong", "Alert");
      }
    }
  }

  private void sendUserDetail() {
    //String password =
    //    SharedPreferenceSingleton.getInstance()
    //        .getStringPreference(this, RegistrationConstants.PASSWORD);
    //String currentlyStudying = SharedPreferenceSingleton.getInstance()
    //    .getStringPreference(RegistrationConstants.CURRENTLY_STUDYING);
    //String school_name = SharedPreferenceSingleton.getInstance()
    //    .getStringPreference(RegistrationConstants.SCHOOL_NAME);
    //String pin_code =
    //    SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.PIN_CODE);
    //String gender =
    //    SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.GENDER);

    // education details
    //String educationId = SharedPreferenceSingleton.getInstance()
    //    .getStringPreference(RegistrationConstants.EDUCATION_ID);
    //String courseId = SharedPreferenceSingleton.getInstance()
    //    .getStringPreference(RegistrationConstants.COURSE_ID);
    //String degreeId = SharedPreferenceSingleton.getInstance()
    //    .getStringPreference(RegistrationConstants.DEGREE_ID);
    //String standardYearId = null;
    //if (currentlyStudying.equals(Constants.SCHOOL)) {
    //  standardYearId = SharedPreferenceSingleton.getInstance()
    //      .getStringPreference(RegistrationConstants.STANDARD_ID);
    //} else {
    //  standardYearId = SharedPreferenceSingleton.getInstance()
    //      .getStringPreference(RegistrationConstants.YEAR_ID);
    //}

    //String refreshedToken = pref.getString("regId", "");
    //System.out.println("GCM Registration Token:" + refreshedToken);

    //AsyncWorker mWorker = new AsyncWorker(this);
    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
        new OnSuccessListener<InstanceIdResult>() {
          @Override public void onSuccess(InstanceIdResult instanceIdResult) {

            //try {
            //  String hobbiesstr =
            //      SharedPreferenceSingleton.getInstance()
            //          .getStringPreference(RegistrationConstants.HOBBY);
            //  jsonArray = new JSONArray(hobbiesstr);
            //  selectedHobbies = new int[jsonArray.length()];
            //
            //  for (int i = 0; i < jsonArray.length(); ++i) {
            //    selectedHobbies[i] = jsonArray.optInt(i);
            //  }
            //} catch (Exception e) {
            //  e.printStackTrace();
            //}

            //mWorker.delegate = OTPEntryActivity.this;
            callApiToRegisterUser(instanceIdResult.getToken());

            //Log.e("signup params", profileObject.toString());
            //mWorker.execute(ServerConnector.REQUEST_FOR_REGISTER_USER, profileObject.toString(),
            //    RequestConstants.POST_REQUEST, RequestConstants.HEADER_NO,
            //    RequestConstants.REQUEST_FOR_REGISTER);
          }
        });
  }

  private void callApiToRegisterUser(String token) {
    showProgressDialog();
    String first_name = "";
    String last_name = "";
    String DOB = "";
    String mobile = "";
    String e_mail = "";
    String profileUrl = "";
    String password = "";
    SharedPreferenceSingleton.getInstance().init(OTPEntryActivity.this);
    SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
    SharedPreferences.Editor editor = pref.edit();
    editor.putString("regId", token);
    editor.commit();

    if (mSignupDM != null && !GeneralApiUtils.isStringEmpty(mSignupDM.getFirstName())) {
      first_name = mSignupDM.getFirstName();
      last_name = mSignupDM.getLastName();
      DOB = mSignupDM.getDob();
      mobile =
          mSignupDM.getMobileNumber();
      e_mail = mSignupDM.getEmail();
      password = mSignupDM.getPassword();
      profileUrl = SharedPreferenceSingleton.getInstance()
          .getStringPreference(this, RegistrationConstants.PROFILE_URL);
    }

    String device_id = Utils.getDeviceId(this);

    //JSONObject profileObject = new JSONObject();
    //try {
    //  profileObject.put(RegistrationConstants.PROFILE_URL, profileUrl);
    //  // profileObject.put(RegistrationConstants.CITY, "-");
    //  //profileObject.put(RegistrationConstants.SCHOOL_ADDRESS, "-");
    //  profileObject.put(RegistrationConstants.DEVICE_TOKEN, token);
    //  profileObject.put(RegistrationConstants.COUNTRY_CODE, "+91");
    //  profileObject.put(RegistrationConstants.PASSWORD, password);
    //  // profileObject.put(RegistrationConstants.CURRENTLY_STUDYING, currentlyStudying);
    //  // profileObject.put(RegistrationConstants.SCHOOL_NAME, school_name);
    //  //profileObject.put(RegistrationConstants.PIN_CODE, pin_code);
    //  //profileObject.put(RegistrationConstants.GENDER, gender);
    //  profileObject.put(RegistrationConstants.FIRST_NAME, first_name);
    //  profileObject.put(RegistrationConstants.LAST_NAME, last_name);
    //  profileObject.put(RegistrationConstants.DATE_OF_BIRTHDAY, DOB);
    //  //  profileObject.put(RegistrationConstants.HOBBY, jsonArray);
    //  profileObject.put(RegistrationConstants.MOBILE, mobile);
    //  profileObject.put(RegistrationConstants.E_MAIL, e_mail);
    //  profileObject.put(RegistrationConstants.DEVICE_ID, device_id);
    //  profileObject.put(RegistrationConstants.VERIFICATION_CODE,
    //      Objects.requireNonNull(mOtpInputEditText.getText()).toString().trim());

    //            if (educationId != null && educationId.trim().length() > 0) {
    //                profileObject.put(RegistrationConstants.EDUCATION_ID, educationId);
    //            }
    //            if (courseId != null && courseId.trim().length() > 0) {
    //                profileObject.put("courses_id", courseId);
    //            }
    //            if (degreeId != null && degreeId.trim().length() > 0) {
    //                profileObject.put(RegistrationConstants.DEGREE_ID, degreeId);
    //            }
    //            if (standardYearId != null && standardYearId.trim().length() > 0 && currentlyStudying != null) {
    //                if (currentlyStudying.equals(Constants.SCHOOL)) {
    //                    profileObject.put(RegistrationConstants.STANDARD_ID, standardYearId);
    //                } else {
    //                    profileObject.put("years_id", standardYearId);
    //                }
    //            }
    methods.register(
        RegisterPostModel.transform(first_name, last_name, DOB, e_mail, password, mobile,
            profileUrl, device_id,
            Objects.requireNonNull(mOtpInputEditText.getText()).toString().trim()))
        .enqueue(new Callback<RegisterUserResponse>() {
          @Override public void onResponse(@NotNull Call<RegisterUserResponse> call,
              @NotNull Response<RegisterUserResponse> loginResponse) {
            dismissProgressDialog();
            if (loginResponse.isSuccessful() && loginResponse.body() != null && loginResponse.body()
                .isSuccess()) {
              Data responseData = loginResponse.body().getData();
              SharedPreferenceSingleton.getInstance().init(OTPEntryActivity.this);
              SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(OTPEntryActivity.this, RegistrationConstants.FIRST_NAME,
                      responseData.getFirstName());
              SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(OTPEntryActivity.this, RegistrationConstants.LAST_NAME,
                      responseData.getLastName());
              SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(OTPEntryActivity.this,
                      RegistrationConstants.DATE_OF_BIRTHDAY,
                      responseData.getDob());
              SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(OTPEntryActivity.this, RegistrationConstants.TOKEN,
                      responseData.getToken());
              SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(OTPEntryActivity.this, RegistrationConstants.TAGGED_NUMBER,
                      String.valueOf(responseData.getTaggedNumber()));
              SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(OTPEntryActivity.this, RegistrationConstants.MOBILE,
                      responseData.getMobile());
              SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(OTPEntryActivity.this, RegistrationConstants.USER_ID,
                      responseData.getUserId());
              SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(OTPEntryActivity.this, RegistrationConstants.E_MAIL,
                      responseData.getEmail());
              SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(OTPEntryActivity.this, RegistrationConstants.PROFILE_URL,
                      responseData.getProfileUrl());
              SharedPreferenceSingleton.getInstance()
                  .writeBoolPreference(RegistrationConstants.IS_TAGGED_USER,
                      responseData.isIsTaggedUser());
              String token = responseData.getToken();
              ChatSessionManager chatSessionManager = ChatSessionManager.getInstance();
              chatSessionManager.initiateSession(responseData.getUserId(), token);
              SocketConnection.getConnection().bindCreator();
              registerFcmToken(responseData.getTaggedNumber());
            } else if (loginResponse.errorBody() != null) {
              try {
                showErrorDialog(loginResponse.errorBody().string());
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }

          @Override public void onFailure(@NotNull Call<RegisterUserResponse> call,
              @NotNull Throwable throwable) {
            dismissProgressDialog();
            showErrorDialog(throwable.getLocalizedMessage());
          }
        });
  }

  //@tony00
  private void registerFcmToken(String tagNumber) {
    SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
    String token = pref.getString("regId", "");

    if (!TextUtils.isEmpty(token)) {
      FcmTokenManager fcmTokenManager = FcmTokenManager.createFrom(this, () -> {
        Intent intent = new Intent(OTPEntryActivity.this, Circle_AnimationActivity.class);
        intent.putExtra("tagged_no", tagNumber);
        startActivity(intent);
      });
      fcmTokenManager.saveFcmToken(token);
    }
  }

  //Firebase
  private void startPhoneNumberVerification(String phoneNumber) {
    // [START start_phone_auth]
    PhoneAuthProvider.getInstance().verifyPhoneNumber(
        phoneNumber,        // Phone number to verify
        60,                 // Timeout duration
        TimeUnit.SECONDS,   // Unit of timeout
        this,               // Activity (for callback binding)
        mCallbacks);        // OnVerificationStateChangedCallbacks
    // [END start_phone_auth]

    mVerificationInProgress = true;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mIsOtpVerified = false;
  }

  private void verifyPhoneNumberWithCode(String verificationId, String code) {
    // [START verify_with_code]
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
    // [END verify_with_code]
    signInWithPhoneAuthCredential(credential);
  }

  private void resendVerificationCode(String phoneNumber,
      PhoneAuthProvider.ForceResendingToken token) {
    PhoneAuthProvider.getInstance().verifyPhoneNumber(
        phoneNumber,        // Phone number to verify
        60,                 // Timeout duration
        TimeUnit.SECONDS,   // Unit of timeout
        this,               // Activity (for callback binding)
        mCallbacks,         // OnVerificationStateChangedCallbacks
        token);             // ForceResendingToken from callbacks
  }

  private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful() && !mIsOtpVerified) {
              // Sign in success, update UI with the signed-in user's information
              Log.d(TAG, "signInWithCredential:success");

              FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
              //System.out.println("Firebase User --" + user.getEmail());
              // [START_EXCLUDE]
              //updateUI(STATE_SIGNIN_SUCCESS, user);
              // [END_EXCLUDE]
              if (user != null) {
                if (Objects.requireNonNull(mOtpInputEditText.getText())
                    .toString()
                    .trim()
                    .isEmpty()) {
                  mOtpInputEditText.setText(credential.getSmsCode());
                }
                mIsOtpVerified = true;
                sendUserDetail();
              }
            } else if (!task.isSuccessful()) {
              // Sign in failed, display a message and update the UI
              Log.w(TAG, "signInWithCredential:failure", task.getException());
              if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                System.out.println("Invadi Credentials");
                // The verification code entered was invalid
                // [START_EXCLUDE silent]
                // mBinding.fieldVerificationCode.setError("Invalid code.");
                // [END_EXCLUDE]
              }
              // [START_EXCLUDE silent]
              // Update UI
              //updateUI(STATE_SIGNIN_FAILED);
              // [END_EXCLUDE]
            }
          }
        });
  }

  private void signOut() {
    mAuth.signOut();
    // updateUI(STATE_INITIALIZED);
  }
}

