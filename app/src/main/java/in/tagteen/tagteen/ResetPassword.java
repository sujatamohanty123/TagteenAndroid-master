package in.tagteen.tagteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import in.tagteen.tagteen.Model.ResetPasswordDM;
import in.tagteen.tagteen.base.AppBaseActivity;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.GeneralApiUtils;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class ResetPassword extends AppBaseActivity implements AsyncResponse {
  public static final String EXTRA_MOBILE_NUMBER =
      BuildConfig.APPLICATION_ID + "EXTRA_MOBILE_NUMBER";
  private AppCompatEditText user_email_code, user_password, user_confirm_password;
  private ImageView imageButton;

  private ResetPasswordDM mResetPasswordDM;

  private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
  private String mVerificationId;
  private PhoneAuthProvider.ForceResendingToken mResendToken;
  private FirebaseAuth mAuth;
  private boolean mIsOtpVerified = false;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.reset_password_layout);
    initComponent();
    initFireBase();
    initUIWidgets();
    initClickListeners();
    if (mResetPasswordDM != null) {
      startPhoneNumberVerification("+91 " + mResetPasswordDM.getMobileNumber());
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mIsOtpVerified = false;
  }

  private void initClickListeners() {
    imageButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (!Objects.requireNonNull(user_email_code.getText()).toString().trim().isEmpty()
            && !mIsOtpVerified) {
          verifyPhoneNumberWithCode(mVerificationId,
              Objects.requireNonNull(user_email_code.getText())
                  .toString().trim());
        } else if (!Objects.requireNonNull(user_email_code.getText()).toString().trim().isEmpty()
            && mIsOtpVerified) {
          callApiToResetUserPassword();
        } else {
          Toast.makeText(ResetPassword.this, "Enter Valid Code", Toast.LENGTH_SHORT).show();
          user_email_code.requestFocus();
        }
      }
    });
  }

  private void initUIWidgets() {
    user_email_code = (AppCompatEditText) findViewById(R.id.user_email_code);
    user_password = (AppCompatEditText) findViewById(R.id.user_password);
    user_confirm_password = (AppCompatEditText) findViewById(R.id.user__confirm_password);
    imageButton = (ImageView) findViewById(R.id.imageButton);
  }

  private void initComponent() {
    if (getIntent().getExtras() != null) {
      mResetPasswordDM = getIntent().getExtras().getParcelable(EXTRA_MOBILE_NUMBER);
    }
  }

  @Override
  public void onRefresh() {

  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_RESET_PASSWORD)) {
      try {
        JSONObject jsonObject = new JSONObject(output);
        if (jsonObject.getBoolean("success")) {
          Toast.makeText(ResetPassword.this, "Your password has been successfully updated",
              Toast.LENGTH_LONG).show();
          mIsOtpVerified = false;
          Intent callLoginActivity = new Intent(ResetPassword.this, LoginActivity.class);
          callLoginActivity.setFlags(
              Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(callLoginActivity);
          finish();
        } else {
          Toast.makeText(ResetPassword.this, jsonObject.getString("error_msg"), Toast.LENGTH_LONG)
              .show();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void initFireBase() {
    mAuth = FirebaseAuth.getInstance();
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

      @Override public void onVerificationCompleted(
          @androidx.annotation.NonNull PhoneAuthCredential phoneAuthCredential) {
        if (!mIsOtpVerified) {
          signInWithPhoneAuthCredential(phoneAuthCredential);
        }
      }

      @Override public void onVerificationFailed(@androidx.annotation.NonNull FirebaseException e) {
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
          dismissProgressDialog();
          showErrorDialog("Invalid phone number.");
        } else if (e instanceof FirebaseTooManyRequestsException) {
          dismissProgressDialog();
          showErrorDialog(e.getLocalizedMessage());
        }
      }

      @Override
      public void onCodeSent(@androidx.annotation.NonNull String verificationId,
          @androidx.annotation.NonNull PhoneAuthProvider.ForceResendingToken token) {
        dismissProgressDialog();
        mVerificationId = verificationId;
        mResendToken = token;
      }
    };
  }

  private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              dismissProgressDialog();
              if (!mIsOtpVerified) {
                FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                if (user != null) {
                  mIsOtpVerified = true;
                }
                if (user != null && user_email_code.getText().toString().trim().isEmpty()) {
                  user_email_code.setText(credential.getSmsCode());
                }
                if (user != null
                    && !GeneralApiUtils.isStringEmpty(
                    Objects.requireNonNull(user_password.getText()).toString().trim())
                    && !GeneralApiUtils.isStringEmpty(
                    Objects.requireNonNull(user_confirm_password.getText()).toString().trim())) {
                  callApiToResetUserPassword();
                }
                // [END_EXCLUDE]
              } else {
                dismissProgressDialog();
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                  showErrorDialog("Invalid code.");
                }
              }
            }
          }
        });
  }

  private void callApiToResetUserPassword() {
    String phone = mResetPasswordDM.getMobileNumber();
    String password = Objects.requireNonNull(user_password.getText()).toString().trim();
    String cnfPassword = Objects.requireNonNull(user_confirm_password.getText()).toString().trim();

    if (!GeneralApiUtils.isStringEmpty(phone) && Utils.matchPassword(ResetPassword.this, password,
        cnfPassword)) {
      AsyncWorker mWorker = new AsyncWorker(ResetPassword.this);
      mWorker.delegate = ResetPassword.this;
      JSONObject BroadcastObject = new JSONObject();
      try {
        //BroadcastObject.put("verification_code", phone);
        BroadcastObject.put("mobile", phone);
        BroadcastObject.put("password", password);
      } catch (Exception e) {
        e.printStackTrace();
      }
      mWorker.execute(
          ServerConnector.REQUEST_RESET_PASSWORD,
          BroadcastObject.toString(),
          RequestConstants.POST_REQUEST,
          RequestConstants.HEADER_YES,
          RequestConstants.REQUEST_RESET_PASSWORD);
    } else {
      Utils.showToast(ResetPassword.this, "Passwords should match");
    }
    //} else {
    //  Utils.showToast(ResetPassword.this, "Invalid code");
    //}
  }

  private void startPhoneNumberVerification(String phoneNumber) {
    showProgressDialog();
    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,        // Phone number to verify
        30,                 // Timeout duration
        TimeUnit.SECONDS,   // Unit of timeout
        this,               // Activity (for callback binding)
        mCallbacks);        // OnVerificationStateChangedCallbacks
  }

  private void verifyPhoneNumberWithCode(String verificationId, String code) {
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
    signInWithPhoneAuthCredential(credential);
  }
}
