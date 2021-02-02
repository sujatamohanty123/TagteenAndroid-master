
package in.tagteen.tagteen.profile;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoriesAdapter;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.HobbiesActivity2_new;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.SingUpAcadmicDetailActivity;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.profile.adapter.ProfilePicsAdapter;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.util.age_calculator.Age;
import in.tagteen.tagteen.util.age_calculator.AgeCalculator;
import in.tagteen.tagteen.utils.ChooseImageDialog;
import in.tagteen.tagteen.utils.CircleTransform;
import in.tagteen.tagteen.utils.GeneralApiUtils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import jp.wasabeef.blurry.Blurry;
import life.knowledge4.videotrimmer.utils.AbsoluteFilePath;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static in.tagteen.tagteen.utils.ChooseImageDialog.CAMERA_PIC_REQUEST;
import static in.tagteen.tagteen.utils.ChooseImageDialog.GALLERY_PIC_REQUEST;

/**
 * Created by lovekushvishwakarma on 06/10/17.
 */

public class EditProfileActivity extends AppCompatActivity implements AsyncResponse {

  private JSONArray jsonArray;
  private Calendar myCalendar = Calendar.getInstance(TimeZone.getDefault());

  private CategoriesAdapter hobbyAdapter;
  private ArrayList<CategoryBean> catelistUserInterest = new ArrayList<>();
  private ProfilePicsAdapter profilePicsAdapter;
  private ArrayList<String> picList = new ArrayList<>();
  private TextView lblAcademicInfoTitle;
  private EditText edtFname, edtLname, edtBirthday;
  private TextView lblPhoneNumber, lblEmailId, lblEditPhoneNumber, lblEditEmailId;
  private TextView lblEditAcademicInfo, lblAcademicName, lblPincode, lblCourseName, lblDegreeName,
      lblStandardYear;
  private RelativeLayout layoutCourseInfo, layoutDegreeInfo;
  private Switch switchFriends, switchPhotos, switchBirthday, switchEducation, switchMobile,
      switchEmail;
  private ImageView imageEditPic, imageUserPIc;
  private ChooseImageDialog dialogImage;
  private TextView textUpdate, textAddInterest;
  private String path, imageUploadPath;
  private Uri uri;
  private Uri uncroppedUri;
  private boolean isImageSelected = false;
  private RadioGroup radioGroupGender;
  private DatePickerDialog.OnDateSetListener date;
  private RadioButton radiomale, radiofemale;
  private Age age;
  private ImageView imageBg, imageUsertemp, imageback;
  private ArrayList<String> hoobieslist = new ArrayList<>();
  private Uri mCropImageUri;

  private String city;
  private String introduction;
  private String currentlyStudying;
  private String schoolAddress;
  private String educationId;
  private String courseId;
  private String degreeId;
  private String standardYearId;

  private String mobileNumber;
  private String emailId;
  private boolean isPasswordVerified = false;
  private boolean isTaggedUser;

  private Dialog dialog;
  private AppCompatEditText txtVerificationCode;
  private AppCompatEditText txtNewPhoneNumber;
  private AppCompatEditText txtNewEmail;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_profile);

    this.initWidgets();
    this.bindEvents();
    this.bindAcademicDetails();
  }

  private void initWidgets() {
    this.isTaggedUser = SharedPreferenceSingleton.getInstance()
        .getBoolPreference(RegistrationConstants.IS_TAGGED_USER);
    this.hoobieslist.clear();

    this.imageback = (ImageView) findViewById(R.id.imageback);
    this.imageUsertemp = (ImageView) findViewById(R.id.imageUsertemp);
    this.imageEditPic = (ImageView) findViewById(R.id.imageEditPic);
    this.imageUserPIc = (ImageView) findViewById(R.id.imageUserPIc);
    this.imageBg = (ImageView) findViewById(R.id.imageBg);

    this.lblAcademicInfoTitle = (TextView) findViewById(R.id.lblAcademicInfoTitle);
    this.lblEditAcademicInfo = (TextView) findViewById(R.id.lblEditAcademicInfo);
    this.lblAcademicName = (TextView) findViewById(R.id.lblAcademicName);
    this.lblPincode = (TextView) findViewById(R.id.lblPincode);
    this.lblCourseName = (TextView) findViewById(R.id.lblCourseName);
    this.lblDegreeName = (TextView) findViewById(R.id.lblDegreeName);
    this.lblStandardYear = (TextView) findViewById(R.id.lblStandardYearName);

    this.lblPhoneNumber = findViewById(R.id.lblPhoneNumber);
    this.lblEmailId = findViewById(R.id.lblEmail);
    this.lblEditPhoneNumber = findViewById(R.id.lblEditPhoneNumber);
    this.lblEditEmailId = findViewById(R.id.lblEditEmail);

    this.layoutCourseInfo = (RelativeLayout) findViewById(R.id.layoutCourseInfo);
    this.layoutDegreeInfo = (RelativeLayout) findViewById(R.id.layoutDegreeInfo);

    this.textAddInterest = (TextView) findViewById(R.id.textAddInterest);
    this.textUpdate = (TextView) findViewById(R.id.textUpdate);
    this.edtFname = (EditText) findViewById(R.id.edtFname);
    this.edtLname = (EditText) findViewById(R.id.edtLname);
    this.edtBirthday = (EditText) findViewById(R.id.edtBirthday);
    this.radiomale = (RadioButton) findViewById(R.id.male);
    this.radiofemale = (RadioButton) findViewById(R.id.female);

    this.switchFriends = (Switch) findViewById(R.id.switchFriends);
    this.switchPhotos = (Switch) findViewById(R.id.switchPhotos);
    this.switchBirthday = (Switch) findViewById(R.id.switchBirthday);
    this.switchEducation = (Switch) findViewById(R.id.switchEducation);
    this.switchMobile = (Switch) findViewById(R.id.switchMobile);
    this.switchEmail = (Switch) findViewById(R.id.switchEmail);

    this.radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);

    RecyclerView recyviewCatUserInterest = (RecyclerView) findViewById(R.id.recyclInterest);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    recyviewCatUserInterest.setLayoutManager(layoutManager);
    //DividerItemDecoration mDividerItemDecorationCate = new DividerItemDecoration(recyviewCatUserInterest.getContext(), layoutManager.getOrientation());
    //recyviewCatUserInterest.addItemDecoration(mDividerItemDecorationCate);
    hobbyAdapter = new CategoriesAdapter(catelistUserInterest, this, "NoClick");
    recyviewCatUserInterest.setAdapter(hobbyAdapter);
    hobbyAdapter.notifyDataSetChanged();

    Intent intent = getIntent();
    if (intent.hasExtra("output")) {
      String output = intent.getStringExtra("output");
      try {
        JSONObject jsonObject = new JSONObject(output);
        JSONObject data = jsonObject.getJSONObject("data");
        String first_name = data.getString("first_name");
        String last_name = data.getString("last_name");
        mobileNumber = data.getString("mobile");
        emailId = data.optString("email");
        String dob = data.optString("dob");
        String sgender = "";//data.getString("gender");
        city = "";//data.getString("city");
        String profile_url = data.getString("profile_url");
        currentlyStudying = "";// data.getString("currently_studying");
        String school_name = "";// data.getString("school_name");
        schoolAddress = "";///data.getString("school_address");
        int pincode = 0;// data.getInt("pincode");
        JSONArray hobby = new JSONArray();// data.getJSONArray("Hobby");

        boolean email_privacy = data.optBoolean("email_privacy");
        boolean mobile_privacy = data.optBoolean("mobile_privacy");
        boolean myfriends_privacy = data.optBoolean("myfriends_privacy");
        boolean myphotos_privacy = data.optBoolean("myphotos_privacy");
        boolean education_privacy = data.optBoolean("education_privacy");
        boolean dob_privacy = data.optBoolean("dob_privacy");

        jsonArray = new JSONArray();
        for (int i = 0; i < hobby.length(); i++) {
          JSONObject object = hobby.getJSONObject(i);
          String categoryName = object.getString(Constants.CATEGORY_NAME_PARAM);
          int categoryId = object.getInt(Constants.CATEGORY_ID_PARAM);
          catelistUserInterest.add(new CategoryBean(categoryId, categoryName));
          hoobieslist.add("" + categoryId);
          jsonArray.put(categoryId);
        }
        Log.d("hobby loaded", jsonArray.toString());
        hobbyAdapter.notifyDataSetChanged();

        //set values
        if (sgender.equalsIgnoreCase("male")) {
          radiomale.setChecked(true);
        } else {
          radiofemale.setChecked(true);
        }
        edtFname.setText("" + first_name);
        edtLname.setText("" + last_name);
        lblPhoneNumber.setText(mobileNumber);
        if (emailId == null || emailId.equalsIgnoreCase("NAN")) {
          emailId = "";
        }
        lblEmailId.setText(emailId);
        edtBirthday.setText("" + dob);
        lblAcademicName.setText("" + school_name);
        lblPincode.setText("" + pincode);

        switchBirthday.setChecked(dob_privacy);
        switchEducation.setChecked(education_privacy);
        switchEmail.setChecked(email_privacy);
        switchFriends.setChecked(myfriends_privacy);
        switchMobile.setChecked(mobile_privacy);
        switchPhotos.setChecked(myphotos_privacy);

        imageUploadPath = profile_url;
        if (imageUploadPath.equalsIgnoreCase("")) {
          Glide.with(EditProfileActivity.this)
              .load(R.drawable.default_userpic) // add your image url
              .transform(
                  new CircleTransform(EditProfileActivity.this)) // applying the image transformer
              .into(imageUserPIc);
        } else {
          Glide.with(EditProfileActivity.this)
              .load(UrlUtils.getUpdatedImageUrl(profile_url, "large")) // add your image url
              .transform(
                  new CircleTransform(EditProfileActivity.this)) // applying the image transformer
              .into(imageUserPIc);
          Glide.with(EditProfileActivity.this)
              .load(UrlUtils.getUpdatedImageUrl(profile_url,
                  "large")) // add your image url// applying the image transformer
              .into(imageUsertemp);

          new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              Blurry.with(EditProfileActivity.this)
                  .radius(25)
                  .sampling(1)
                  .color(Color.argb(66, 255, 255, 0))
                  .async()
                  .capture(findViewById(R.id.imageUsertemp))
                  .into(imageBg);
            }

          }, 600);
        }
      } catch (Exception e) {
        Log.e("Edit profile", e.getMessage());
      }
    }
  }

  private void bindEvents() {
    this.imageEditPic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //CropImage.startPickImageActivity(EditProfileActivity.this);
        moveToFaceCaptureScreen();
      }
    });

    this.textUpdate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isValidChanges()) {
          if (isImageSelected) {
            File file_for_selectedPath = new File(path);
            uploadProfileImage(file_for_selectedPath, EditProfileActivity.this,
                RegistrationConstants.POST_IMAGE);
          } else {
            updateProfile();
          }
        }
      }
    });

    this.date = new DatePickerDialog.OnDateSetListener() {

      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear,
          int dayOfMonth) {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        monthOfYear = monthOfYear + 1;

        String month = String.valueOf(monthOfYear);
        if (monthOfYear < 10) {
          month = "0" + monthOfYear;
        }
        String day = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
          day = "0" + dayOfMonth;
        }
        String dob = day + "-" + month + "-" + year;
        updateLabel(dob);
      }
    };

    final DatePickerDialog datePickerDialog = new DatePickerDialog(
        EditProfileActivity.this, date, myCalendar.get(Calendar.YEAR),
        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
    this.edtBirthday.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        hideSoftKeyboard();
        datePickerDialog.dismiss();
        datePickerDialog.show();
      }
    });

    this.textAddInterest.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent hobbiesIntent = new Intent(EditProfileActivity.this, HobbiesActivity2_new.class);
        hobbiesIntent.putExtra("from", "edit");
        hobbiesIntent.putStringArrayListExtra(Constants.SELECTED_CATEGORIES, hoobieslist);
        startActivityForResult(hobbiesIntent, Constants.EDIT_CATEGORIES_REQUEST_CODE);
      }
    });
    this.lblEditAcademicInfo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(EditProfileActivity.this, SingUpAcadmicDetailActivity.class);
        intent.putExtra(Constants.FROM_SCREEN, Constants.EDIT_PROFILE_SCREEN);
        startActivityForResult(intent, Constants.EDIT_ACADEMIC_REQUEST_CODE);
      }
    });
    this.lblEditPhoneNumber.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showMobileOrEmailUpdateDialog(true, false);
      }
    });
    this.lblEditEmailId.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showMobileOrEmailUpdateDialog(false, true);
      }
    });

    this.imageback.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
  }

  private void moveToFaceCaptureScreen() {
    if (!this.isTaggedUser) {
      Utils.showUnverifiedUserDialog(this);
      return;
    }
    CropImage.startPickImageActivity(EditProfileActivity.this);
        /*Intent intent = new Intent(EditProfileActivity.this, FaceRecognitionActivity.class);
        startActivity(intent);*/
  }

  private void bindAcademicDetails() {
    this.currentlyStudying = SharedPreferenceSingleton.getInstance()
        .getStringPreference(RegistrationConstants.CURRENTLY_STUDYING);
    this.educationId = SharedPreferenceSingleton.getInstance()
        .getStringPreference(RegistrationConstants.EDUCATION_ID);
    this.courseId = SharedPreferenceSingleton.getInstance()
        .getStringPreference(RegistrationConstants.COURSE_ID);
    this.degreeId = SharedPreferenceSingleton.getInstance()
        .getStringPreference(RegistrationConstants.DEGREE_ID);

    String schoolName = SharedPreferenceSingleton.getInstance()
        .getStringPreference(RegistrationConstants.SCHOOL_NAME);
    String pinCode =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.PIN_CODE);
    String courseName = SharedPreferenceSingleton.getInstance()
        .getStringPreference(RegistrationConstants.COURSE_NAME);
    String degreeName = SharedPreferenceSingleton.getInstance()
        .getStringPreference(RegistrationConstants.DEGREE_NAME);

    String standardYearName = null;
    if (this.currentlyStudying.equals(Constants.SCHOOL)) {
      this.lblAcademicInfoTitle.setText("School Info");
      standardYearName = SharedPreferenceSingleton.getInstance()
          .getStringPreference(RegistrationConstants.STANDARD_NAME);
      this.standardYearId = SharedPreferenceSingleton.getInstance()
          .getStringPreference(RegistrationConstants.STANDARD_ID);

      this.layoutCourseInfo.setVisibility(View.GONE);
      this.layoutDegreeInfo.setVisibility(View.GONE);
    } else {
      if (this.currentlyStudying.equals(Constants.COLLEGE)) {
        this.lblAcademicInfoTitle.setText("College Info");
      } else {
        this.lblAcademicInfoTitle.setText("University Info");
      }
      standardYearName = SharedPreferenceSingleton.getInstance()
          .getStringPreference(RegistrationConstants.YEAR_NAME);
      this.standardYearId = SharedPreferenceSingleton.getInstance()
          .getStringPreference(RegistrationConstants.YEAR_ID);

      this.layoutCourseInfo.setVisibility(View.VISIBLE);
      this.layoutDegreeInfo.setVisibility(View.VISIBLE);
    }

    if (schoolName != null) {
      this.lblAcademicName.setText(schoolName);
    }
    if (pinCode != null) {
      this.lblPincode.setText(pinCode);
    }
    if (courseName != null) {
      this.lblCourseName.setText(courseName);
    }
    if (degreeName != null) {
      this.lblDegreeName.setText(degreeName);
    }
    if (standardYearName != null) {
      this.lblStandardYear.setText(standardYearName);
    }
  }

  private boolean isValidChanges() {
    String firstName = this.edtFname.getText().toString();
    if (firstName.trim().length() == 0) {
      this.edtFname.setError("First name cannot be empty");
      return false;
    }
    String lastName = this.edtLname.getText().toString();
    if (lastName.trim().length() == 0) {
      this.edtLname.setError("Last name cannot be empty");
      return false;
    }
    return true;
  }

  private void showMobileOrEmailUpdateDialog(final boolean editMobileNumber, boolean editEmail) {
    this.dialog = new Dialog(this);
    this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.dialog.setContentView(R.layout.dialog_edit_phone_email);

    TextView lblTitle = dialog.findViewById(R.id.lblTitle);
    if (editMobileNumber) {
      lblTitle.setText("Update mobile number");
    } else {
      lblTitle.setText("Update email");
    }

    TextInputLayout layoutPhoneNumber = dialog.findViewById(R.id.inputLayoutNewPhoneNumber);
    this.txtNewPhoneNumber = dialog.findViewById(R.id.txtNewPhoneNumber);
    if (this.mobileNumber != null && !this.mobileNumber.equals("null")) {
      this.txtNewPhoneNumber.setText(this.mobileNumber);
      this.txtNewPhoneNumber.setSelection(this.mobileNumber.length());
    }
    TextInputLayout layoutEmail = dialog.findViewById(R.id.inputLayoutNewEmail);
    this.txtNewEmail = dialog.findViewById(R.id.txtNewEmail);
    if (this.emailId != null && !this.emailId.equals("null")) {
      this.txtNewEmail.setText(this.emailId);
      this.txtNewEmail.setSelection(this.emailId.length());
    }
    if (editMobileNumber) {
      layoutPhoneNumber.setVisibility(View.VISIBLE);
    } else {
      layoutEmail.setVisibility(View.VISIBLE);
    }

    final TextInputLayout layoutVerificationCode =
        dialog.findViewById(R.id.inputLayoutVerificationCode);
    this.txtVerificationCode = dialog.findViewById(R.id.txtVerificationCode);

    final TextView lblDone = dialog.findViewById(R.id.lblDone);
    lblDone.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isPasswordVerified) {
          if (editMobileNumber) {
            if (txtNewPhoneNumber.getText().toString().trim().length() == 0) {
              txtNewPhoneNumber.setError("Required field");
              txtNewPhoneNumber.requestFocus();
              return;
            }
          } else {
            if (txtNewEmail.getText().toString().trim().length() == 0) {
              txtNewEmail.setError("Required field");
              txtNewEmail.requestFocus();
              return;
            }
          }
          updateNewMobileEmail();
        } else {
          if (layoutVerificationCode.getVisibility() == View.GONE) {
            // send verification code
            if (editMobileNumber) {
              if (txtNewPhoneNumber.getText().toString().trim().length() == 0) {
                txtNewPhoneNumber.setError("Required field");
                txtNewPhoneNumber.requestFocus();
                return;
              }
            } else {
              if (txtNewEmail.getText().toString().trim().length() == 0) {
                txtNewEmail.setError("Required field");
                txtNewEmail.requestFocus();
                return;
              }
            }
            layoutVerificationCode.setVisibility(View.VISIBLE);
          } else {
            // verify verification code
            if (txtVerificationCode.getText().toString().trim().length() == 0) {
              txtVerificationCode.setError("Required field");
              txtVerificationCode.requestFocus();
              return;
            }
            verifyUserPasssword();
          }
        }
      }
    });
    this.dialog.show();
  }

  private void updateNewMobileEmail() {
    this.mobileNumber = this.txtNewPhoneNumber.getText().toString();
    this.lblPhoneNumber.setText(this.mobileNumber);
    this.emailId = this.txtNewEmail.getText().toString();
    this.lblEmailId.setText(this.emailId);
    this.dismissDialog(this.dialog);
  }

  private void verifyUserPasssword() {
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    try {
      BroadcastObject.put(RegistrationConstants.MOBILE, this.mobileNumber);
      BroadcastObject.put(RegistrationConstants.PASSWORD,
          this.txtVerificationCode.getText().toString());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    mWorker.execute(
        ServerConnector.REQUEST_FOR_LOGIN,
        BroadcastObject.toString(),
        RequestConstants.POST_REQUEST,
        RequestConstants.HEADER_NO,
        RequestConstants.REQUEST_FOR_LOGIN);
  }

  private void updateProfile() {
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    int selectedId = radioGroupGender.getCheckedRadioButtonId();
    RadioButton radioButton = (RadioButton) findViewById(selectedId);
    String gender = radioButton.getText().toString();
    String user_id =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    try {
      BroadcastObject.put("user_id", user_id);
      BroadcastObject.put("first_name", edtFname.getText().toString());
      BroadcastObject.put("last_name", edtLname.getText().toString());
      BroadcastObject.put("dob", "NAN");
      BroadcastObject.put("mobile", lblPhoneNumber.getText().toString());
      String emailId = lblEmailId.getText().toString();
      if (emailId == null || emailId.trim().length() == 0) {
        emailId = "NAN";
      }
      BroadcastObject.put("email", emailId);
      BroadcastObject.put("gender", gender);
      if (city == null || city.trim().length() == 0) {
        city = "NAN";
      }
      BroadcastObject.put("city", city);
      BroadcastObject.put("introduction", "NAN");
      Log.d("hobby", jsonArray.toString());
      BroadcastObject.put("hobby", jsonArray);
      if (this.currentlyStudying == null || this.currentlyStudying.trim().length() == 0) {
        this.currentlyStudying = "NAN";
      }
      BroadcastObject.put("currently_studying", this.currentlyStudying);
      BroadcastObject.put("profile_url", imageUploadPath);
      BroadcastObject.put("school_name",
          GeneralApiUtils.checkUpdateStringIfEmpty(lblAcademicName.getText().toString()));
      if (this.schoolAddress == null || this.schoolAddress.trim().length() == 0) {
        this.schoolAddress = "NAN";
      }
      BroadcastObject.put("school_address", this.schoolAddress);
      BroadcastObject.put("pincode",
          GeneralApiUtils.checkUpdateStringIfEmpty(lblPincode.getText().toString()));

      if (this.educationId != null && this.educationId.trim().length() > 0) {
        BroadcastObject.put("education_id", this.educationId);
      }
      if (this.courseId != null && this.courseId.trim().length() > 0) {
        BroadcastObject.put("courses_id", this.courseId);
      }
      if (this.degreeId != null && this.degreeId.trim().length() > 0) {
        BroadcastObject.put("degree_id", this.degreeId);
      }
      if (this.standardYearId != null
          && this.standardYearId.trim().length() > 0
          && this.currentlyStudying != null) {
        if (this.currentlyStudying.equals(Constants.SCHOOL)) {
          BroadcastObject.put("standard_id", this.standardYearId);
        } else {
          BroadcastObject.put("years_id", this.standardYearId);
        }
      }

      BroadcastObject.put("email_privacy", switchEmail.isChecked());
      BroadcastObject.put("mobile_privacy", switchMobile.isChecked());
      BroadcastObject.put("education_privacy", switchEducation.isChecked());
      BroadcastObject.put("dob_privacy", switchBirthday.isChecked());
      BroadcastObject.put("myphotos_privacy", switchPhotos.isChecked());
      BroadcastObject.put("myfriends_privacy", switchFriends.isChecked());
    } catch (Exception e) {
      Log.e("EditProfile", e.getMessage());
    }
    mWorker.execute(
        ServerConnector.REQUEST_UPDATE_PROFILE,
        BroadcastObject.toString(),
        RequestConstants.POST_REQUEST,
        RequestConstants.HEADER_YES,
        RequestConstants.REQUEST_UPDATE_PROFILE);
  }

  private String getHobbies() {
    StringBuffer ss = new StringBuffer();
    for (int i = 0; i < catelistUserInterest.size(); i++) {
      ss.append(catelistUserInterest.get(i).getCategoryName());
      ss.append(",");
    }
    String finalVal = ss.toString().replace(",", " ").trim();
    finalVal = finalVal.replace(" ", ",");
    return finalVal;
  }

  @Override
  public void onRefresh() {

  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    try {
      Log.e("EditProfile", output);
      if (RequestConstants.REQUEST_UPDATE_PROFILE.equalsIgnoreCase(REQUEST_NUMBER)) {
        EditProfileActivity.this.finish();
      } else if (RequestConstants.REQUEST_FOR_LOGIN.equals(REQUEST_NUMBER)) {
        JSONObject loginResponse = new JSONObject(output);
        boolean responseStatus = loginResponse.getBoolean(RegistrationConstants.SUCCESS);
        if (responseStatus) {
          isPasswordVerified = true;
          this.updateNewMobileEmail();
        } else {
          this.txtVerificationCode.setError("Incorrect password");
          this.txtVerificationCode.requestFocus();
        }
      }
    } catch (Exception e) {
      Log.e("EditProfile", e.getMessage());
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // super.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GALLERY_PIC_REQUEST) {
      if (resultCode == RESULT_OK) {
        if (data != null) {
          try {
            final Uri imageUri = data.getData();
            gotoCrop(imageUri);
          } catch (Exception e) {
            e.printStackTrace();
          }
          editIconVisible(View.VISIBLE);
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
      }
    } else if (requestCode == CAMERA_PIC_REQUEST) {
      if (data != null) {
        String imagePath = data.getStringExtra(Constants.PATH_IMAGE_CAPTURED);
        if (imagePath != null) {
          Uri picUri = Uri.parse(new File(imagePath).toString());
          path = imagePath;
          gotoCrop(picUri);
        }
      }
    } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
        && resultCode == Activity.RESULT_OK) {
      if (data != null) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        isImageSelected = true;
        final Uri picUri = result.getUri();
        path = AbsoluteFilePath.getPath(EditProfileActivity.this, picUri);
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            Glide.with(EditProfileActivity.this)
                .load(picUri) // add your image url
                .transform(
                    new CircleTransform(EditProfileActivity.this)) // applying the image transformer
                .into(imageUserPIc);
            editIconVisible(View.VISIBLE);
          }
        }, 100);
      } else {
        if (this.uri != null) {
          isImageSelected = true;
          path = AbsoluteFilePath.getPath(EditProfileActivity.this, uri);
          new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              Glide.with(EditProfileActivity.this)
                  .load(uri) // add your image url
                  .transform(new CircleTransform(
                      EditProfileActivity.this)) // applying the image transformer
                  .into(imageUserPIc);
              editIconVisible(View.VISIBLE);
            }
          }, 100);
        } else {
          Utils.showToast(this, "Could not update profile pic");
        }
      }
    } else if (requestCode == Constants.EDIT_ACADEMIC_REQUEST_CODE) {
      this.bindAcademicDetails();
    } else if (requestCode == Constants.EDIT_CATEGORIES_REQUEST_CODE) {
      if (data == null) {
        return;
      }
      String list = data.getStringExtra("hobbyliststring");
      try {
        jsonArray = new JSONArray(list);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      List<String> hobbylistarry = data.getStringArrayListExtra("hobbylist");
      catelistUserInterest.clear();
      for (int i = 0; i < hobbylistarry.size(); i++) {
        catelistUserInterest.add(new CategoryBean(i, hobbylistarry.get(i)));
      }
      hobbyAdapter.notifyDataSetChanged();
    } else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
        && resultCode == Activity.RESULT_OK) {
      Uri imageUri = CropImage.getPickImageResultUri(this, data);
      if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
        // request permissions and handle the result in onRequestPermissionsResult()
        mCropImageUri = imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
              CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
        }
      } else {
        // no permissions required or already granted, can start crop image activity
        gotoCrop(imageUri);
      }
    }
  }

  private void editIconVisible(int visiblity) {
    imageEditPic.setVisibility(visiblity);
  }

  private void gotoCrop(Uri imageUri) {
    CropImage.activity(imageUri).start(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        CropImage.startPickImageActivity(this);
      } else {
        Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
            .show();
      }
    }
    if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
      if (mCropImageUri != null
          && grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // required permissions granted, start crop image activity
        gotoCrop(mCropImageUri);
      } else {
        Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  void uploadProfileImage(final File file, Context context, String S3Folder) {
    final ProgressDialog dialog = new ProgressDialog(EditProfileActivity.this);
    dialog.setTitle("Image Uploading...");
    dialog.setMessage("Please wait image uploading...");
    dialog.show();
    String bucket = "tagteen-images";//AWSUtility.getBucketName();
    String fileName = file.getName();
    if (fileName.length() > 20) {
      fileName = fileName.substring(fileName.length() - 20, fileName.length());
    }

    final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + "010101" + "_" + fileName;
    imageUploadPath = "https://" + bucket + ".s3.ap-south-1.amazonaws.com/" + OBJECT_KEY;

    TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
        bucket,     // The bucket to upload to
        OBJECT_KEY,   //  The key for the uploaded object
        file       //  The file where the data to upload exists
    );

    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
          //callapiPost(value, desc, vdHeight, vdWidth);
          dismissDialog(dialog);
          updateProfile();
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int per = (int) ((bytesCurrent * 100) / bytesTotal);
      }

      @Override
      public void onError(int id, Exception ex) {
        dismissDialog(dialog);
      }
    });
  }

  private void updateLabel(String dob) {
    try {
      String myFormat = "dd-MM-yyyy";
      SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat);
      Date birthDate = dateFormat.parse(dateFormat.format(myCalendar.getTime()));
      age = AgeCalculator.calculateAge(birthDate);
      if (age != null && (age.getYears() < 13 || age.getYears() > 23)) {
        Utils.showShortToast(this, "Age criteria not met!");
      } else {
        edtBirthday.setText(dob);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void dismissDialog(Dialog dialog) {
    if (this == null || dialog == null) {
      return;
    }
    dialog.dismiss();
  }

  public void hideSoftKeyboard() {
    if (getCurrentFocus() != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }
}
