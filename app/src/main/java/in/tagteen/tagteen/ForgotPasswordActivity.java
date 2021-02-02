package in.tagteen.tagteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatEditText;
import in.tagteen.tagteen.Model.ResetPasswordDM;
import in.tagteen.tagteen.base.AppBaseActivity;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Utils;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

//import com.bcgdv.asia.lib.dots.DotsProgressIndicator;

//import com.bcgdv.asia.lib.dots.DotsProgressIndicator;

public class ForgotPasswordActivity extends AppBaseActivity implements AsyncResponse {
  AppCompatEditText passwordEditText;
  LinearLayout sendEmailLayout, successLayout;
  ImageView button_send;
  //LinearLayout dotsProgressIndicator;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(in.tagteen.tagteen.R.layout.activity_forgot_password);
    //dotsProgressIndicator = (LinearLayout) findViewById(R.id.dotsProgressIndicator);
    //dotsProgressIndicator.setVisibility(View.GONE);
    passwordEditText = (AppCompatEditText) findViewById(in.tagteen.tagteen.R.id.email_for_password);
    passwordEditText.setHint("Enter your mobile number");

    sendEmailLayout =
        (LinearLayout) findViewById(in.tagteen.tagteen.R.id.login_container_tag_screen);
    successLayout = (LinearLayout) findViewById(in.tagteen.tagteen.R.id.succses_msg);
    button_send = (ImageView) findViewById(in.tagteen.tagteen.R.id.imageButton_send_email);
    button_send.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String mobile = Objects.requireNonNull(passwordEditText.getText()).toString();
        String regexStr = "^[5-9][0-9]{9}$";
        if (mobile.matches(regexStr)
            && !mobile.equals("9999999999")
            && !mobile.equals("8888888888")
            && !mobile.equals("7777777777")
            && !mobile.contains(" ")) {
          //forgotpassApi(mobile);
          Intent intent = new Intent(ForgotPasswordActivity.this, ResetPassword.class);
          ResetPasswordDM resetPasswordDM = new ResetPasswordDM(mobile);
          intent.putExtra(ResetPassword.EXTRA_MOBILE_NUMBER, resetPasswordDM);
          startActivity(intent);
        } else {
          passwordEditText.setError("Please Enter Correct Mobile Number");
        }
      }
    });
  }

  private void forgotpassApi(String mobile) {
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;
    mWorker.delegate = ForgotPasswordActivity.this;
    JSONObject BroadcastObject = new JSONObject();
    try {

      BroadcastObject.put("mobile", mobile);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    mWorker.execute(
        ServerConnector.FORGOT_PASSWORD,
        BroadcastObject.toString(),
        RequestConstants.POST_REQUEST,
        RequestConstants.HEADER_NO,
        RequestConstants.FORGOT_PASSWORD_API);
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

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    try {
      if (RequestConstants.FORGOT_PASSWORD_API.equalsIgnoreCase(REQUEST_NUMBER)) {
        JSONObject data = new JSONObject(output);
        boolean success = data.getBoolean("success");
        JSONObject data1 = data.getJSONObject("data");
        if (success) {
          //dotsProgressIndicator.setVisibility(View.GONE);
          String message = data1.getString("message");
          Utils.showToast(ForgotPasswordActivity.this, message);
          Intent it = new Intent(ForgotPasswordActivity.this, ResetPassword.class);
          startActivity(it);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
