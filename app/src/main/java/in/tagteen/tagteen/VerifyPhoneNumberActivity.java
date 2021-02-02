package in.tagteen.tagteen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import org.json.JSONException;
import org.json.JSONObject;

import in.tagteen.tagteen.Model.PhoneVerificationModel;
import in.tagteen.tagteen.Model.PhoneVerificationRequestModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VerifyPhoneNumberActivity extends AppCompatActivity {
    private ImageView imgVerify;
    private AppCompatEditText txtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        this.imgVerify = findViewById(R.id.imgVerify);
        this.txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        this.imgVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgVerify.setEnabled(false);
                String mobileNumber = txtPhoneNumber.getText().toString();
                String regexStr = "^[5-9][0-9]{9}$";

                if (mobileNumber.matches(regexStr) &&
                        !mobileNumber.equals("9999999999") &&
                        !mobileNumber.equals("8888888888") &&
                        !mobileNumber.equals("7777777777") &&
                        mobileNumber.trim().length() != 0) {
                    try {
                        verifyPhoneNumber();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    txtPhoneNumber.setError("Invalid Mobile Number");
                    imgVerify.setEnabled(true);
                }
            }
        });
    }

    private void verifyPhoneNumber() throws JSONException {
        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        PhoneVerificationRequestModel json = new PhoneVerificationRequestModel();
        json.setMobile(this.txtPhoneNumber.getText().toString());
        json.setCountryCode("+91");

        Call<PhoneVerificationModel> call = methods.verifyMobileNumber(json);
        API_Call_Retrofit.methodCalled("mobile : " + call.request().url().toString());
        call.enqueue(new Callback<PhoneVerificationModel>() {
            @Override
            public void onResponse(Call<PhoneVerificationModel> call, Response<PhoneVerificationModel> response) {
                PhoneVerificationModel responseModel = response.body();
                if (responseModel.getSuccess()) {
                    if (responseModel.getData() != null && responseModel.getData().getMessage() != null) {
                        if (Constants.NUMBER_ALREADY_EXISTS.equalsIgnoreCase(responseModel.getData().getMessage())) {
                            moveToLogin();
                        } else if (Constants.NEW_NUMBER.equalsIgnoreCase(responseModel.getData().getMessage())) {
                            moveToSignUp();
                        } else {
                            alertInvalidNumber();
                        }
                    } else {
                        alertInvalidNumber();
                    }
                } else {
                    imgVerify.setEnabled(true);
                    String msg = Constants.INVALID_NUMBER;
                    if (responseModel.getData() != null && responseModel.getData().getMessage() != null) {
                        msg = responseModel.getData().getMessage();
                    }
                    Utils.showShortToast(VerifyPhoneNumberActivity.this, msg);
                }
            }

            @Override
            public void onFailure(Call<PhoneVerificationModel> call, Throwable t) {
                imgVerify.setEnabled(true);
            }
        });
    }

    private void alertInvalidNumber() {
        this.imgVerify.setEnabled(true);
        Utils.showShortToast(this, Constants.INVALID_NUMBER);
    }

    private void moveToSignUp() {
        SharedPreferenceSingleton.getInstance().writeStringPreference(
                RegistrationConstants.MOBILE, txtPhoneNumber.getText().toString());
        Intent it = new Intent(this, SignUpActivity.class);
        startActivity(it);
    }

    private void moveToLogin() {
        SharedPreferenceSingleton.getInstance().writeStringPreference(
                RegistrationConstants.MOBILE, txtPhoneNumber.getText().toString());
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
    }
}
