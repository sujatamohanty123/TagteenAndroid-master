package in.tagteen.tagteen;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatEditText;

import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;

import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;


public class SingUpPhoneEmailActivity extends AppCompatActivity implements AsyncResponse {
    private ImageView image;
    private AppCompatEditText phoneNumber, emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.tagteen.tagteen.R.layout.sign_up3);
        image = (ImageView) findViewById(in.tagteen.tagteen.R.id.imageButton);
        phoneNumber = (AppCompatEditText) findViewById(R.id.input_phone);
        emailId = (AppCompatEditText) findViewById(R.id.user_Email_id);

        this.setMobileNumber();

        // event
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.setEnabled(false);
                String mobile = phoneNumber.getText().toString();
                String regexStr = "^[5-9][0-9]{9}$";
                String email = emailId.getText().toString().trim();

                if (mobile.matches(regexStr) && !mobile.equals("9999999999") && !mobile.equals("8888888888") && !mobile.equals("7777777777") && !mobile.contains(" ")) {
                    /*if (email.length() == 0) {
                        RequestForOTP();
                    } else */if (email.trim().length() > 0 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        moveToOtpScreen();
                    } else {
                        emailId.setError("Please Enter Correct Email Id");
                        image.setEnabled(true);
                    }
                } else {
                    phoneNumber.setError("Please Enter Correct Mobile Number");
                    image.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void setMobileNumber() {
        String mobileNumber = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.MOBILE);
        if (mobileNumber != null && mobileNumber.trim().length() > 0) {
            this.phoneNumber.setText(mobileNumber);
            this.phoneNumber.setEnabled(false);
        }
    }

    private void moveToOtpScreen() {
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.MOBILE, phoneNumber.getText().toString());
        /*String email = emailId.getText().toString();
        if (email.length() == 0) {
            email = "-";
        }*/
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.E_MAIL, emailId.getText().toString());
        Intent it = new Intent(this, OTPEntryActivity.class);
        startActivity(it);
    }


    @Override
    public void onRefresh() {}

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        switch (REQUEST_NUMBER) {
        }
    }
}
