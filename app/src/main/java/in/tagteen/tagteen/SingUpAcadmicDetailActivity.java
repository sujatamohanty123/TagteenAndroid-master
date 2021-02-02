package in.tagteen.tagteen;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.tagteen.tagteen.Adapters.TextWithRadioAdapter;
import in.tagteen.tagteen.Fragments.beans.SearchInputJson;
import in.tagteen.tagteen.Model.AcademicInfos;
import in.tagteen.tagteen.Model.EducationsModel;
import in.tagteen.tagteen.Model.GuidNameTuple;
import in.tagteen.tagteen.Model.SearchModel;
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

public class SingUpAcadmicDetailActivity extends AppCompatActivity implements AsyncResponse{
    private ImageView imageGoNext;
    private RadioGroup academicSelectionGroup;
    private AutoCompleteTextView txtAcademyName;
    private AppCompatEditText txtAcademyPin, txtCourseName, txtDegreeName, txtStandardYear;
    private TextInputLayout inputCourse, inputDegree;
    private Button btnPincodeInfo;
    private String currentlyStudying;
    private String fromScreen = "";

    private List<GuidNameTuple> courseInfos;
    private List<GuidNameTuple> degreeInfos;
    private List<GuidNameTuple> yearInfos;
    private List<GuidNameTuple> standardInfos;

    private Map<String, AcademicInfos.AcademicInfo> academicInfoMap;
    private String educationId;
    private String courseId;
    private String degreeId;
    private String standardYearId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.tagteen.tagteen.R.layout.sign_up_page);

        this.initWidgets();
        this.bindEvents();
        this.loadData();
    }

    private void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadEducationInfos();
            }
        }, 200);
    }

    private void initWidgets() {
        Intent intent = getIntent();
        this.fromScreen = intent.getStringExtra(Constants.FROM_SCREEN);

        this.academicInfoMap = new HashMap<>();
        this.courseInfos = new ArrayList<>();
        this.degreeInfos = new ArrayList<>();
        this.yearInfos = new ArrayList<>();
        this.standardInfos = new ArrayList<>();

        this.txtAcademyName = (AutoCompleteTextView) findViewById(R.id.academic_name);
        this.txtAcademyPin = (AppCompatEditText)findViewById(R.id.academic_location_pin);
        this.txtCourseName = (AppCompatEditText) findViewById(R.id.txtCourseName);
        this.txtCourseName.setHint("Course name");
        this.txtDegreeName = (AppCompatEditText) findViewById(R.id.txtDegreeName);
        this.txtDegreeName.setHint("Degree name");
        this.txtStandardYear = (AppCompatEditText) findViewById(R.id.txtStandardYear);

        this.inputCourse = (TextInputLayout) findViewById(R.id.inputCourse);
        this.inputDegree = (TextInputLayout) findViewById(R.id.inputDegree);

        this.imageGoNext = (ImageView)findViewById(in.tagteen.tagteen.R.id.imageButton_acca);
        this.academicSelectionGroup = (RadioGroup)findViewById(R.id.academicRadioGroup);
        this.btnPincodeInfo = (Button) findViewById(R.id.btnPincodeInfo);

        SharedPreferenceSingleton.getInstance().init(SingUpAcadmicDetailActivity.this);
        String currentlyStudying = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.CURRENTLY_STUDYING);
        String schoolName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.SCHOOL_NAME);
        String pinCode = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.PIN_CODE);
        this.educationId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.EDUCATION_ID);
        this.courseId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.COURSE_ID);
        this.degreeId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.DEGREE_ID);

        if (currentlyStudying.equals(Constants.UNIVERSITY)) {
            this.setAcademicUniversity();
            this.standardYearId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.YEAR_ID);
            this.academicSelectionGroup.check(R.id.university_Button);
            if (schoolName.isEmpty()) {
                this.txtAcademyName.setHint("University Name-city");
            } else {
                this.txtAcademyName.setText(schoolName);
                this.txtAcademyName.setSelection(schoolName.length() - 1);
            }
            if (pinCode.isEmpty()) {
                this.txtAcademyPin.setHint("University Pin Code");
            } else {
                this.txtAcademyPin.setText(pinCode);
            }
            this.bindCourseDegreeDetails();
        } else if(currentlyStudying.equals(Constants.COLLEGE)) {
            this.setAcademicCollege();
            this.standardYearId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.YEAR_ID);
            this.academicSelectionGroup.check(R.id.college_Button);
            if (schoolName.isEmpty()) {
                this.txtAcademyName.setHint("College Name");
            } else {
                this.txtAcademyName.setText(schoolName);
                this.txtAcademyName.setSelection(schoolName.length() - 1);
            }
            if (pinCode.isEmpty()) {
                this.txtAcademyPin.setHint("College Pin Code");
            } else {
                this.txtAcademyPin.setText(pinCode);
            }
            this.bindCourseDegreeDetails();
        } else {
            this.setAcademicSchool();
            this.standardYearId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.STANDARD_ID);
            this.academicSelectionGroup.check(R.id.school_Button);
            if (schoolName.isEmpty()) {
                this.txtAcademyName.setHint("School Name");
            } else {
                this.txtAcademyName.setText(schoolName);
                this.txtAcademyName.setSelection(schoolName.length() - 1);
            }
            if (pinCode.isEmpty()) {
                this.txtAcademyPin.setHint("School Pin Code");
            } else {
                this.txtAcademyPin.setText(pinCode);
            }
            String yearName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.STANDARD_NAME);
            if (yearName != null) {
                this.txtStandardYear.setText(yearName);
            }
        }
    }

    private void bindCourseDegreeDetails() {
        String courseName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.COURSE_NAME);
        if (courseName != null) {
            this.txtCourseName.setText(courseName);
        }
        String degreeName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.DEGREE_NAME);
        if (degreeName != null) {
            this.txtDegreeName.setText(degreeName);
        }
        String yearName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.YEAR_NAME);
        if (yearName != null) {
            this.txtStandardYear.setText(yearName);
        }
    }

    private void bindEvents() {
        this.txtAcademyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                if (str != null) {
                    int length = str.length();
                    if (length > 0) {
                        searchAcademicDetails(str);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        this.txtAcademyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                String selectedItem = (String) adapter.getItemAtPosition(position);
                AcademicInfos.AcademicInfo info = academicInfoMap.get(selectedItem);
                if (info != null && info.getPincode() != null) {
                    educationId = info.getId();
                    txtAcademyPin.setText(info.getPincode());
                    Utils.hideKeyboard(SingUpAcadmicDetailActivity.this, getCurrentFocus());
                }
            }
        });
        this.imageGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentlyStudying.equals(Constants.SCHOOL)) {
                    if (courseId == null || txtCourseName.getText().toString().trim().length() == 0) {
                        showCoursesDialog();
                        return;
                    }
                    if (degreeId == null || txtDegreeName.getText().toString().trim().length() == 0) {
                        showDegreesDialog();
                        return;
                    }
                }
                if (standardYearId == null || txtStandardYear.getText().toString().trim().length() == 0) {
                    if (currentlyStudying.equals(Constants.SCHOOL)) {
                        showStandardYearsDialog(standardInfos);
                        return;
                    } else {
                        showStandardYearsDialog(yearInfos);
                        return;
                    }
                }
                String acaName = txtAcademyName.getText().toString().trim();
                if (acaName.length() == 0) {
                    txtAcademyName.setError("Required field");
                    txtAcademyName.requestFocus();
                    return;
                }
                String acaPin = txtAcademyPin.getText().toString().trim();
                if (acaPin.length() == 6) {
                    validateEducationDetails();
                } else {
                    txtAcademyPin.setError("Enter Correct Pin code");
                    txtAcademyPin.requestFocus();
                }
            }
        });
        this.academicSelectionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.school_Button) {
                    setAcademicSchool();
                } else if(checkedId == R.id.college_Button) {
                    setAcademicCollege();
                } else {
                    setAcademicUniversity();
                }
            }
        });
        this.btnPincodeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPincodeInfo();
            }
        });
        this.txtCourseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCoursesDialog();
            }
        });
        this.txtDegreeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDegreesDialog();
            }
        });
        this.txtStandardYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentlyStudying.equals(Constants.SCHOOL)) {
                    showStandardYearsDialog(standardInfos);
                } else {
                    showStandardYearsDialog(yearInfos);
                }
            }
        });
    }

    private void validateEducationDetails() {
        String acaName = this.txtAcademyName.getText().toString().trim();
        AcademicInfos.AcademicInfo info = this.academicInfoMap.get(acaName);
        if (info != null || this.educationId != null) {
            this.moveToNextScreen();
        } else {
            this.showCreateEducationDialog();
        }
    }

    private void showCreateEducationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_eduction);

        TextView lblTitle = dialog.findViewById(R.id.lblTitle);
        lblTitle.setText("Please provide your " + this.currentlyStudying + " info");

        final AppCompatEditText txtNewAcademicName = dialog.findViewById(R.id.txtNewAcademic);
        txtNewAcademicName.setHint(this.currentlyStudying + " name");
        txtNewAcademicName.setText(this.txtAcademyName.getText().toString());
        final AppCompatEditText txtNewPincode = dialog.findViewById(R.id.txtNewPincode);
        txtNewPincode.setText(this.txtAcademyPin.getText().toString());
        final AppCompatEditText txtNewDistrict = dialog.findViewById(R.id.txtNewDistrict);
        final AppCompatEditText txtNewState = dialog.findViewById(R.id.txtNewState);

        TextView lblDone = dialog.findViewById(R.id.lblDone);
        lblDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtNewAcademicName.getText().toString().trim().length() == 0) {
                    txtNewAcademicName.setError("Required field");
                    txtNewAcademicName.requestFocus();
                    return;
                }
                if (txtNewAcademicName.getText().toString().trim().length() == 0) {
                    txtNewPincode.setError("Required field");
                    txtNewPincode.requestFocus();
                    return;
                }
                if (txtNewDistrict.getText().toString().trim().length() == 0) {
                    txtNewDistrict.setError("Required field");
                    txtNewDistrict.requestFocus();
                    return;
                }
                if (txtNewState.getText().toString().trim().length() == 0) {
                    txtNewState.setError("Required field");
                    txtNewState.requestFocus();
                    return;
                }
                String academicName = txtNewAcademicName.getText().toString();
                String pincode = txtNewPincode.getText().toString();
                txtAcademyName.setText(academicName);
                txtAcademyPin.setText(pincode);
                String district = txtNewDistrict.getText().toString();
                String state = txtNewState.getText().toString();
                dialog.dismiss();

                insertEducationDetails(academicName, pincode, district, state);
            }
        });
        dialog.show();
    }

    private void moveToNextScreen() {
        SharedPreferenceSingleton.getInstance().init(this);
        SharedPreferenceSingleton.getInstance().writeStringPreference(
                RegistrationConstants.CURRENTLY_STUDYING, this.currentlyStudying);
        SharedPreferenceSingleton.getInstance().writeStringPreference(
                RegistrationConstants.SCHOOL_NAME, this.txtAcademyName.getText().toString());
        SharedPreferenceSingleton.getInstance().writeStringPreference(
                RegistrationConstants.PIN_CODE, this.txtAcademyPin.getText().toString());

        SharedPreferenceSingleton.getInstance().writeStringPreference(
                RegistrationConstants.EDUCATION_ID, this.educationId);
        if (this.currentlyStudying.equals(Constants.SCHOOL)) {
            SharedPreferenceSingleton.getInstance().writeStringPreference(
                    RegistrationConstants.STANDARD_ID, this.standardYearId);
            SharedPreferenceSingleton.getInstance().writeStringPreference(
                    RegistrationConstants.STANDARD_NAME, this.txtStandardYear.getText().toString());
        } else {
            SharedPreferenceSingleton.getInstance().writeStringPreference(
                    RegistrationConstants.COURSE_ID, this.courseId);
            SharedPreferenceSingleton.getInstance().writeStringPreference(
                    RegistrationConstants.COURSE_NAME, this.txtCourseName.getText().toString());
            SharedPreferenceSingleton.getInstance().writeStringPreference(
                    RegistrationConstants.DEGREE_ID, this.degreeId);
            SharedPreferenceSingleton.getInstance().writeStringPreference(
                    RegistrationConstants.DEGREE_NAME, this.txtDegreeName.getText().toString());
            SharedPreferenceSingleton.getInstance().writeStringPreference(
                    RegistrationConstants.YEAR_ID, this.standardYearId);
            SharedPreferenceSingleton.getInstance().writeStringPreference(
                    RegistrationConstants.YEAR_NAME, this.txtStandardYear.getText().toString());
        }

        if (this.fromScreen != null && this.fromScreen.equals(Constants.EDIT_PROFILE_SCREEN)) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent(this, HobbiesActivity2_new.class);
            intent.putExtra(Constants.FROM_SCREEN, Constants.SIGNUP_SCREEN);
            startActivity(intent);
        }
    }

    private void showCoursesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_eduction_option);

        TextView lblTitle = (TextView) dialog.findViewById(R.id.lblTitle);
        lblTitle.setText("Select Course");
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.listOptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TextWithRadioAdapter adapter = new TextWithRadioAdapter(this, this.courseInfos, this.courseId);
        recyclerView.setAdapter(adapter);

        TextView lblDone = (TextView) dialog.findViewById(R.id.lblDone);
        lblDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuidNameTuple tuple = adapter.getSelectedItem();
                if (tuple != null) {
                    courseId = tuple.getId();
                    txtCourseName.setText(tuple.getName());
                }
                dialog.dismiss();
                if (degreeId == null || txtDegreeName.getText().toString().trim().length() == 0) {
                    showDegreesDialog();
                }
            }
        });

        dialog.show();
    }

    private void showDegreesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_eduction_option);

        TextView lblTitle = (TextView) dialog.findViewById(R.id.lblTitle);
        lblTitle.setText("Select Degree");
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.listOptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TextWithRadioAdapter adapter = new TextWithRadioAdapter(this, this.degreeInfos, this.degreeId);
        recyclerView.setAdapter(adapter);

        TextView lblDone = (TextView) dialog.findViewById(R.id.lblDone);
        lblDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuidNameTuple tuple = adapter.getSelectedItem();
                if (tuple != null) {
                    degreeId = tuple.getId();
                    txtDegreeName.setText(tuple.getName());
                }
                dialog.dismiss();
                if (standardYearId == null || txtStandardYear.getText().toString().trim().length() == 0) {
                    if (currentlyStudying.equals(Constants.SCHOOL)) {
                        showStandardYearsDialog(standardInfos);
                    } else {
                        showStandardYearsDialog(yearInfos);
                    }
                }
            }
        });

        dialog.show();
    }

    private void showStandardYearsDialog(List<GuidNameTuple> tuples) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_eduction_option);

        TextView lblTitle = (TextView) dialog.findViewById(R.id.lblTitle);
        if (this.currentlyStudying.equals(Constants.SCHOOL)) {
            lblTitle.setText("Select Standard");
        } else {
            lblTitle.setText("Select Year");
        }
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.listOptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TextWithRadioAdapter adapter = new TextWithRadioAdapter(this, tuples, this.standardYearId);
        recyclerView.setAdapter(adapter);

        TextView lblDone = (TextView) dialog.findViewById(R.id.lblDone);
        lblDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuidNameTuple tuple = adapter.getSelectedItem();
                if (tuple != null) {
                    standardYearId = tuple.getId();
                    txtStandardYear.setText(tuple.getName());
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showPincodeInfo() {
        String msg = "Every place in India has a 6-digit pincode e.g. 751016, please write the 6-digit pincode of your school/college/University address";
        Utils.showAlertDialog(SingUpAcadmicDetailActivity.this, msg, "Info");
    }

    private void clear() {
        this.txtAcademyName.setText("");
        this.txtAcademyPin.setText("");
        this.txtCourseName.setText("");
        this.txtDegreeName.setText("");
        this.txtStandardYear.setText("");
    }

    public void setAcademicSchool() {
        this.clear();
        this.txtAcademyName.setHint("School Name");
        this.txtAcademyPin.setHint("School Pin Code");
        this.txtStandardYear.setHint("Standard");
        this.inputCourse.setVisibility(View.GONE);
        this.inputDegree.setVisibility(View.GONE);
        this.currentlyStudying = Constants.SCHOOL;
    }

    public void setAcademicCollege() {
        this.clear();
        this.txtAcademyName.setHint("College Name");
        this.txtAcademyPin.setHint("College Pin Code");
        this.txtStandardYear.setHint("Year");
        this.inputCourse.setVisibility(View.VISIBLE);
        this.inputDegree.setVisibility(View.VISIBLE);
        currentlyStudying = Constants.COLLEGE;
    }

    public void setAcademicUniversity() {
        this.clear();
        this.txtAcademyName.setHint("University Name");
        this.txtAcademyPin.setHint("University Pin Code");
        this.txtStandardYear.setHint("Year");
        this.inputCourse.setVisibility(View.VISIBLE);
        this.inputDegree.setVisibility(View.VISIBLE);
        this.currentlyStudying = Constants.UNIVERSITY;
    }

    private void loadEducationInfos() {
        SharedPreferenceSingleton.getInstance().init(SingUpAcadmicDetailActivity.this);
        Apimethods methods = API_Call_Retrofit.getretrofit(SingUpAcadmicDetailActivity.this).create(Apimethods.class);
        Call<EducationsModel> call = methods.getEduactionNames();
        call.enqueue(new Callback<EducationsModel>() {
            @Override
            public void onResponse(Call<EducationsModel> call, Response<EducationsModel> response) {
                int statuscode = response.code();
                if (statuscode == 200) {
                    EducationsModel educationsModel = response.body();
                    EducationsModel.EducationModel model = educationsModel.getData();

                    if (model.getCourseInfos() != null) {
                        for (EducationsModel.CourseInfo info : model.getCourseInfos()) {
                            GuidNameTuple tuple = new GuidNameTuple(info.getId(), info.getName());
                            courseInfos.add(tuple);
                        }
                    }
                    if (model.getDegreeInfos() != null) {
                        for (EducationsModel.DegreeInfo info : model.getDegreeInfos()) {
                            if (info.getName().equalsIgnoreCase("polytechnic")) {
                                continue;
                            }
                            GuidNameTuple tuple = new GuidNameTuple(info.getId(), info.getName());
                            degreeInfos.add(tuple);
                        }
                    }
                    if (model.getYearInfos() != null) {
                        for (EducationsModel.YearInfo info : model.getYearInfos()) {
                            GuidNameTuple tuple = new GuidNameTuple(info.getId(), info.getName());
                            yearInfos.add(tuple);
                        }
                    }
                    if (model.getStandardInfos() != null) {
                        for (EducationsModel.StandardInfo info : model.getStandardInfos()) {
                            String name = "Standard " + info.getName();
                            GuidNameTuple tuple = new GuidNameTuple(info.getId(), name);
                            standardInfos.add(tuple);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EducationsModel> call, Throwable t) {
                Log.d("Failed", "url=" + call.request().url().toString());
            }
        });
    }

    private void searchAcademicDetails(String searchtext) {
        SearchInputJson inputJson = new SearchInputJson();
        inputJson.setSearch_data(searchtext);
        SharedPreferenceSingleton.getInstance().init(SingUpAcadmicDetailActivity.this);
        Apimethods methods = API_Call_Retrofit.getretrofit(SingUpAcadmicDetailActivity.this).create(Apimethods.class);
        Call<AcademicInfos> call = methods.searchEducationDetails(inputJson);
        call.enqueue(new Callback<AcademicInfos>() {
            @Override
            public void onResponse(Call<AcademicInfos> call, Response<AcademicInfos> response) {
                int statuscode = response.code();
                if (statuscode == 200) {
                    AcademicInfos responseModel = response.body();
                    List<AcademicInfos.AcademicInfo> dataList = responseModel.getDataList();
                    if (dataList != null) {
                        List<String> academicList = new ArrayList<String>();
                        for (AcademicInfos.AcademicInfo info : dataList) {
                            academicInfoMap.put(info.getAcademicName(), info);
                            if (!academicList.contains(info.getAcademicName())) {
                                academicList.add(info.getAcademicName());
                            }
                        }
                        ArrayAdapter<String> academicListAdapter =
                                new ArrayAdapter<String>(
                                        SingUpAcadmicDetailActivity.this,
                                        R.layout.custom_list_item_textview, R.id.item, academicList);
                        txtAcademyName.setAdapter(academicListAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<AcademicInfos> call, Throwable t) {

            }
        });
    }

    private void insertEducationDetails(final String academicName, String pincode, String district, String state) {
        AcademicInfos.AcademicInfo academicInfo = new AcademicInfos(). new AcademicInfo();
        academicInfo.setAcademicName(academicName);
        academicInfo.setPincode(pincode);
        academicInfo.setDistrict(district);
        academicInfo.setState(state);
        academicInfo.setEducationName(this.currentlyStudying);
        if (this.currentlyStudying.equals(Constants.SCHOOL)) {
            academicInfo.setEducationType(1);
        } else if (this.currentlyStudying.equals(Constants.COLLEGE)) {
            academicInfo.setEducationType(2);
        } else {
            academicInfo.setEducationType(3);
        }

        SharedPreferenceSingleton.getInstance().init(SingUpAcadmicDetailActivity.this);
        Apimethods methods = API_Call_Retrofit.getretrofit(SingUpAcadmicDetailActivity.this).create(Apimethods.class);
        Call<SearchModel> call = methods.insertEducationDetails(academicInfo);
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                int statuscode = response.code();
                if (statuscode == 200) {
                    SearchModel responseModel = response.body();
                    if (responseModel != null && responseModel.getSuccess()) {
                        searchAcademicDetails(academicName);
                        Utils.showShortToast(SingUpAcadmicDetailActivity.this, "Successfully added");
                    } else {
                        Utils.showShortToast(SingUpAcadmicDetailActivity.this, "Could not add academic details");
                    }
                } else {
                    Utils.showShortToast(SingUpAcadmicDetailActivity.this, "Could not add academic details");
                }
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                Utils.showShortToast(SingUpAcadmicDetailActivity.this, "Could not add academic details");
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(in.tagteen.tagteen.R.anim.anim_slide_in_right, in.tagteen.tagteen.R.anim.anim_slide_out_left);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {

    }
}
