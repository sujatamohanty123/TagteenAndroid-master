package in.tagteen.tagteen;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import in.tagteen.tagteen.Model.PhoneVerificationModel;
import in.tagteen.tagteen.Model.PhoneVerificationRequestModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.screen.signup.SignupDM;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.util.age_calculator.Age;
import in.tagteen.tagteen.util.age_calculator.AgeCalculator;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private String month;
    private Calendar myCalendar = Calendar.getInstance(TimeZone.getDefault());
    private ImageView imageGoNext;
    private AppCompatEditText userFirstName, userLastName, input_user_Mob, input_user_email,
            input_user_password, input_user_confmPass;
    private AppCompatEditText userDOB;
    private Intent it;
    private LinearLayout backgroundView;
    private Animation Alpha, bottomUp;
    private DatePickerDialog.OnDateSetListener date;
    private Age age;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.tagteen.tagteen.R.layout.sign_up2);
        builder = new AlertDialog.Builder(this);
        imageGoNext = (ImageView) findViewById(in.tagteen.tagteen.R.id.imageButton);
        userFirstName = (AppCompatEditText) findViewById(R.id.Input_First_Name);
        userLastName = (AppCompatEditText) findViewById(R.id.input_Last_Name);
        input_user_Mob = (AppCompatEditText) findViewById(R.id.input_user_Mob);
        input_user_email = (AppCompatEditText) findViewById(R.id.input_user_email);
        input_user_password = (AppCompatEditText) findViewById(R.id.input_user_password);
        input_user_confmPass = (AppCompatEditText) findViewById(R.id.input_user_confmPass);

        userDOB = (AppCompatEditText) findViewById(R.id.input_user_DOB);
        SharedPreferenceSingleton.getInstance().init(SignUpActivity.this);
        String FName = SharedPreferenceSingleton.getInstance()
                .getStringPreference(RegistrationConstants.FIRST_NAME);
        String LName = SharedPreferenceSingleton.getInstance()
                .getStringPreference(RegistrationConstants.LAST_NAME);
        String DOB = SharedPreferenceSingleton.getInstance()
                .getStringPreference(RegistrationConstants.DATE_OF_BIRTHDAY);

        userFirstName.setText(FName);
        userLastName.setText(LName);
        userDOB.setText(DOB);

        try {
            if (DOB != null && !DOB.equalsIgnoreCase("")) {
                String date[] = DOB.split("-");
                int dd = Integer.parseInt(date[0]);
                int mm = Integer.parseInt(date[1]);
                mm = mm - 1;
                int yy = Integer.parseInt(date[2]);

                myCalendar.set(Calendar.YEAR, yy);
                myCalendar.set(Calendar.MONTH, mm);
                myCalendar.set(Calendar.DAY_OF_MONTH, dd);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date birthDate = dateFormat.parse(
                        dateFormat.format(myCalendar.getTime())); //Yeh !! It's my date of birth :-)
                age = AgeCalculator.calculateAge(birthDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        it = new Intent(this, OTPEntryActivity.class);
        imageGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_name = userFirstName.getText().toString().trim();
                String last_name = userLastName.getText().toString().trim();
                String DOB = userDOB.getText().toString().trim();
                String mobileNo = input_user_Mob.getText().toString().trim();
                String email = input_user_email.getText().toString().trim();
                String password = input_user_password.getText().toString().trim();
                String confmPass = input_user_confmPass.getText().toString().trim();

                if (!first_name.equals(null) && !first_name.equals("") && first_name.length() > 2) {
                    if (!last_name.equals(null) && !last_name.equals("") && last_name.length() > 2) {
                        if (age != null && !DOB.equals(first_name) && !DOB.equals("")) {
                            if (first_name.length() <= 20 && first_name.length() >= 3) {
                                if (last_name.length() < 20 && last_name.length() >= 3) {
                                    if (age.getYears() >= 13 && age.getYears() <= 29) {
                                        if (Utils.isMobileNumbervalid(mobileNo)) {
                                            if (Utils.isemailvalid(email)) {
                                                if (password.length() >= 6) {
                                                    if (password.equalsIgnoreCase(confmPass)) {
                                                        verifyPhoneNumber(mobileNo, email, password);
                                                    } else {
                                                        input_user_confmPass.setError("Password not match");
                                                    }
                                                } else {
                                                    input_user_password.setError("Password should be atleast 6 digit");
                                                }
                                            } else {
                                                input_user_email.setError("Email id is not valid");
                                            }
                                        } else {
                                            input_user_Mob.setError("Mobile number is not valid");
                                        }
                                    } else {
                                        userDOB.setError("You are not in age Criteria for Application use!");
                                    }
                                } else {
                                    userDOB.setError("Last Name length should be 3-20 char");
                                }
                            } else {
                                userDOB.setError("First Name length should be 3-20 char");
                            }
                        } else {
                            userDOB.setError("Please Enter Correct Date Of Birth");
                        }
                    } else {
                        userLastName.setError("Please Enter The Last Name");
                    }
                } else {
                    userFirstName.setError("Please Enter The First  Name");
                }
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                monthOfYear = monthOfYear + 1;

                month = String.valueOf(monthOfYear);
                if (monthOfYear < 10) {
                    month = "0" + monthOfYear;
                }
                String dob = dayOfMonth + "-" + month + "-" + year;
                updateLabel(dob);
            }
        };

        final DatePickerDialog datePickerDialog =
                new DatePickerDialog(SignUpActivity.this, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        userDOB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                userDOB.setError(null);
                hideSoftKeyboard();
                datePickerDialog.dismiss();
                datePickerDialog.show();
                return true;
            }
        });

        backgroundView =
                (LinearLayout) findViewById(in.tagteen.tagteen.R.id.login_container_tag_screen);
        backgroundView.setVisibility(View.GONE);
        ImageView imageLogo = (ImageView) findViewById(in.tagteen.tagteen.R.id.signup_logo_screen);

        bottomUp = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.slide_up);
        Alpha = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.alpha);
        imageLogo.setVisibility(View.VISIBLE);

        bottomUp.setDuration(1500);
        imageLogo.startAnimation(bottomUp);

        bottomUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                backgroundView.setVisibility(View.VISIBLE);
                Alpha.setDuration(1500);
                backgroundView.startAnimation(Alpha);
            }
        });

        Alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });
    }

    private void updateLabel(String dob) {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        userDOB.setText(dob);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date birthDate = dateFormat.parse(
                    dateFormat.format(myCalendar.getTime())); //Yeh !! It's my date of birth :-)
            age = AgeCalculator.calculateAge(birthDate);
            if (age.getYears() < 13 || age.getYears() > 29) {
                showDoNotRegisterDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDoNotRegisterDialog() {
        new MaterialDialog.Builder(this).title(R.string.signup_error_registration)
                .content(R.string.signup_error_registration_age)
                .positiveText(R.string.general_label_ok)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (dialog.isShowing() && !isFinishing()) {
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    public void hideSoftKeyboard() {
        Utils.hideKeyboard(this, getCurrentFocus());
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void verifyPhoneNumber(final String mobileNo, final String email, final String pasword) {
        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        PhoneVerificationRequestModel json = new PhoneVerificationRequestModel();
        json.setMobile(mobileNo);
        json.setCountryCode("+91");

        Call<PhoneVerificationModel> call = methods.verifyMobileNumber(json);

        call.enqueue(new Callback<PhoneVerificationModel>() {
            @Override
            public void onResponse(@NotNull Call<PhoneVerificationModel> call,
                                   @NotNull Response<PhoneVerificationModel> response) {
                PhoneVerificationModel responseModel = response.body();
                if (responseModel != null && responseModel.getSuccess()) {
                    if (responseModel.getData() != null && responseModel.getData().getMessage() != null) {
                        if (Constants.NUMBER_ALREADY_EXISTS.equalsIgnoreCase(
                                responseModel.getData().getMessage())) {
                            moveToLogin(mobileNo);
                        } else if (Constants.NEW_NUMBER.equalsIgnoreCase(
                                responseModel.getData().getMessage())) {
                            moveToSignUp(mobileNo, email, pasword);
                        } else {
                            alertInvalidNumber();
                        }
                    } else {
                        alertInvalidNumber();
                    }
                } else {
                    String msg = Constants.INVALID_NUMBER;
                    if (responseModel != null
                            && responseModel.getData() != null
                            && responseModel.getData().getMessage() != null) {
                        msg = responseModel.getData().getMessage();
                    }
                    Utils.showShortToast(SignUpActivity.this, msg);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PhoneVerificationModel> call, @NotNull Throwable t) {
                //  imgVerify.setEnabled(true);
            }
        });
    }

    private void alertInvalidNumber() {
        Utils.showShortToast(this, Constants.INVALID_NUMBER);
    }

    private void moveToSignUp(String mobileNo, String email, String password) {
        SharedPreferenceSingleton.getInstance().init(SignUpActivity.this);
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(this, RegistrationConstants.FIRST_NAME,
                        Objects.requireNonNull(userFirstName.getText()).toString());
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(this, RegistrationConstants.LAST_NAME, Objects.requireNonNull(
                        userLastName.getText()).toString());
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(this, RegistrationConstants.DATE_OF_BIRTHDAY,
                        Objects.requireNonNull(userDOB.getText()).toString());
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(this, RegistrationConstants.PASSWORD, password);
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(this, RegistrationConstants.MOBILE, mobileNo);
        SharedPreferenceSingleton.getInstance()
                .writeStringPreference(this, RegistrationConstants.E_MAIL, email);
        it.putExtra(OTPEntryActivity.EXTRA_SIGNUP_DM,
                SignupDM.transform(userFirstName.getText().toString(), userLastName.getText()
                        .toString(), userDOB.getText().toString(), email, password, mobileNo));
        startActivity(it);
    }

    private void moveToLogin(String mobileNo) {

        builder.setMessage("Your Mobile number "
                + mobileNo
                + " is already register with us, Please try with login and forgot password if don't remember the password.")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferenceSingleton.getInstance().writeStringPreference(
                                RegistrationConstants.MOBILE, mobileNo);
                        Intent it = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(it);
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Hi!");
        alert.show();
        ;
    }
}
