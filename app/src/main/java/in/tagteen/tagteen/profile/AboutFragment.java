package in.tagteen.tagteen.profile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import in.tagteen.tagteen.Adapters.MomentslistAdapter;
import in.tagteen.tagteen.ConnectivityReceiver;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoriesAdapter;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.InsertCoolModel;
import in.tagteen.tagteen.Model.LikeJsonInputModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.networkEngine.ModifiedAsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_DELETE_POST;
import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_REPORT_POST;

public class AboutFragment extends Fragment implements AsyncResponse {
  private static final String TAG = "AboutFragment";

  private CategoriesAdapter hobbyAdapter;
  private ArrayList<CategoryBean> hobbyList = new ArrayList<>();
  private NestedScrollView scrollerview;
  private String loggedInUserId, otherUserId, accessToken;
  private String output = "";
  private String first_name, last_name, mobile, email, school_name, introduction = "", dob_string;
  private TextView intro, edit;
  private LinearLayout intro_linear;
  private RecyclerView momentsRecyclerview;
  private MomentslistAdapter adapter;
  private LinearLayoutManager lm;
  private int page = 1;
  private int pagelimit = 10;
  private ArrayList<SectionDataModel> allSampleData = new ArrayList<>();
  private LinearLayout linerLoadmore;
  private boolean apiCallBlocler = false;
  private TextView phone, email_txt, school, dob, nofriensdslay, nofriensdslay1;
  private RelativeLayout relative;
  Dialog d;
  private TextView msg, name, continueorder, dismiss, ok;
  private LinearLayout buttonLayout;
  private RecyclerView recyviewCatUserInterest;

  private LinearLayout progress;
  private LinearLayout progressInterests;

  private TextView lblInterestTitle;
  private TextView lblPersonalInfoTitle;
  private TextView lblPostsTitle;

  private boolean isMyFriend, isPendingFriend;

  public AboutFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_about, container, false);

    d = new Dialog(Objects.requireNonNull(getActivity()));
    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
    d.setCanceledOnTouchOutside(true);
    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    d.setContentView(R.layout.confirmationpopup);
    msg = (TextView) d.findViewById(R.id.message);
    name = (TextView) d.findViewById(R.id.yourname);
    continueorder = (TextView) d.findViewById(R.id.confirm);
    dismiss = (TextView) d.findViewById(R.id.dismiss);
    ok = (TextView) d.findViewById(R.id.confirm_ok_btn);
    buttonLayout = (LinearLayout) d.findViewById(R.id.button_layout);

    linerLoadmore = (LinearLayout) v.findViewById(R.id.linerLoadmore);
    scrollerview = (NestedScrollView) v.findViewById(R.id.Scrollerview);
    intro_linear = (LinearLayout) v.findViewById(R.id.linear);
    relative = (RelativeLayout) v.findViewById(R.id.relative);
    intro = (TextView) v.findViewById(R.id.intro);
    edit = (TextView) v.findViewById(R.id.edit);
    phone = (TextView) v.findViewById(R.id.phone);
    school = (TextView) v.findViewById(R.id.school);
    dob = (TextView) v.findViewById(R.id.dob);
    email_txt = (TextView) v.findViewById(R.id.email);
    nofriensdslay = (TextView) v.findViewById(R.id.nofriendslay);
    nofriensdslay1 = (TextView) v.findViewById(R.id.nofriendslay1);
    recyviewCatUserInterest = (RecyclerView) v.findViewById(R.id.recyclInterest);
    momentsRecyclerview = (RecyclerView) v.findViewById(R.id.buzz_recycler_view);
    this.progress = (LinearLayout) v.findViewById(R.id.layoutProgress);
    this.progressInterests = (LinearLayout) v.findViewById(R.id.layoutProgressInterests);

    this.lblInterestTitle = (TextView) v.findViewById(R.id.intrest);
    this.lblPersonalInfoTitle = (TextView) v.findViewById(R.id.profileInfo);
    this.lblPostsTitle = (TextView) v.findViewById(R.id.my_post);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

    otherUserId = getArguments().getString("user_id");
    accessToken =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    loggedInUserId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    Log.d(TAG, loggedInUserId);

    if (!this.loggedInUserId.equals(this.otherUserId)) {
      this.isMyFriend = getArguments().getBoolean(Constants.IS_MY_FRIEND);
      this.isPendingFriend = getArguments().getBoolean(Constants.IS_PENDING_FRIEND);
    }

    recyviewCatUserInterest.setLayoutManager(layoutManager);
    recyviewCatUserInterest.setNestedScrollingEnabled(true);

    lm = new LinearLayoutManager(getActivity());
    adapter = new MomentslistAdapter(getActivity(), allSampleData, AboutFragment.this, 2);
    momentsRecyclerview.setLayoutManager(lm);
    momentsRecyclerview.setAdapter(adapter);

    //if (!EventBus.getDefault().isRegistered(this)) {
    //  EventBus.getDefault().register(this);
    //}



    edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (output != null && !output.equalsIgnoreCase("")) {
          Intent intent = new Intent(getActivity(), EditProfileActivity.class);
          intent.putExtra("output", output);
          startActivity(intent);
        }
      }
    });
    return v;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    //EventBus.getDefault().unregister(this);
  }

  private void getProfile() {
    if (ConnectivityReceiver.isConnected() == false) {
      Utils.showShortToast(getActivity(), getString(R.string.no_connection));
      return;
    }
    ModifiedAsyncWorker mWorker = new ModifiedAsyncWorker(getActivity());
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    Log.e("api", ServerConnector.REQUEST_GET_USER_PROFILE + otherUserId);
    mWorker.execute(
        ServerConnector.REQUEST_GET_USER_PROFILE + otherUserId,
        BroadcastObject.toString(),
        RequestConstants.GET_REQUEST,
        RequestConstants.HEADER_YES,
        RequestConstants.REQUEST_GET_PROFILE);
  }

  @Override
  public void onResume() {
    super.onResume();
    this.getProfile();
  }

  @Override
  public void onRefresh() {
  }

  @Override
  public void ReceivedResponseFromServer(final String output, String REQUEST_NUMBER) {
    try {
      if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_GET_PROFILE)) {
        progress.setVisibility(View.GONE);
        progressInterests.setVisibility(View.GONE);
        scrollerview.smoothScrollTo(0, 0);
        relative.setVisibility(View.VISIBLE);
        this.output = output;
        JSONObject jsonObject = new JSONObject(output);
        JSONObject data = jsonObject.getJSONObject("data");
        Log.e("response", data.toString());
        //String tagged_number = data.getString("tagged_number");
        String path = data.getString("profile_url");
        String user_id = data.getString("user_id");
        first_name = data.getString("first_name");
        last_name = data.getString("last_name");
        mobile = data.getString("mobile");
        email = data.getString("email");
        dob_string = data.getString("dob");
        //String gender = data.getString("gender");
        boolean email_privacy = data.getBoolean("email_privacy");
        boolean mobile_privacy = data.getBoolean("mobile_privacy");
        boolean myfriends_privacy = data.getBoolean("myfriends_privacy");
        boolean myphotos_privacy = data.getBoolean("myphotos_privacy");
        boolean education_privacy = data.getBoolean("education_privacy");

        try {
          String currently_studying = data.getString("currently_studying");
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.CURRENTLY_STUDYING, currently_studying);
          if (currently_studying == null || currently_studying.trim().length() == 0) {
            currently_studying = Constants.SCHOOL;
          }
          String educationId = null;
          String courseId = null;
          String courseName = null;
          String degreeId = null;
          String degreeName = null;
          String standardYearId = null;
          String standardYearName = null;
          String studiesInfo = "Studying ";
          JSONObject educationObj = data.optJSONObject(Constants.EDUCATION_OBJ);
          if (educationObj != null) {
            educationId = educationObj.getString(Constants.ID);
          }
          if (currently_studying.equals(Constants.SCHOOL)) {
            JSONObject standardObj = data.optJSONObject(Constants.STANDARD_OBJ);
            if (standardObj != null) {
              standardYearName = standardObj.getString(Constants.STANDARD_NAME);
              if (!standardYearName.contains("Standard")) {
                standardYearName = "Standard " + standardYearName;
              }
              standardYearId = standardObj.getString(Constants.ID);
              studiesInfo += "in " + standardYearName;
            }
          } else {
            JSONObject degreeObj = data.optJSONObject(Constants.DEGREE_OBJ);
            if (degreeObj != null) {
              degreeName = degreeObj.getString(Constants.DEGREE_NAME);
              degreeId = degreeObj.getString(Constants.ID);
              studiesInfo += degreeName + " of ";
            }
            JSONObject courseObj = data.optJSONObject(Constants.COURSE_OBJ);
            if (courseObj != null) {
              courseName = courseObj.getString(Constants.COURSE_NAME);
              courseId = courseObj.getString(Constants.ID);
              studiesInfo += courseName;
            }
          }
          if (school_name != null) {
            studiesInfo += " in " + school_name;
            if (!currently_studying.equals(Constants.SCHOOL)) {
              JSONObject yearObj = data.optJSONObject(Constants.YEAR_OBJ);
              if (yearObj != null) {
                standardYearName = yearObj.getString(Constants.YEAR_NAME);
                standardYearId = yearObj.getString(Constants.ID);
                studiesInfo += " (" + standardYearName + ")";
                school.setText(studiesInfo);
              }
            }
          }
          //academic info
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.EDUCATION_ID, educationId);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.COURSE_ID, courseId);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.COURSE_NAME, courseName);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.DEGREE_ID, degreeId);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.DEGREE_NAME, degreeName);
          if (!currently_studying.equals(Constants.SCHOOL)) {
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.YEAR_ID, standardYearId);
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.YEAR_NAME, standardYearName);
          } else {
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.STANDARD_ID, standardYearId);
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(RegistrationConstants.STANDARD_NAME, standardYearName);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
        try {
          String pincode = data.getString("pincode");
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.PIN_CODE, pincode);
        } catch (JSONException e) {
          e.printStackTrace();
        }

        //String city = data.getString("city");
        //String be_a_fan_count = data.getString("be_a_fan_count");
        //String my_fan_count = data.getString("my_fan_count");
        //final boolean is_my_fan = data.getBoolean("is_my_fan");
        final boolean is_my_friend = data.getBoolean("is_my_friend");
        //final boolean is_tagged_user = data.getBoolean("is_tagged_user");
        JSONArray hobby = data.getJSONArray("Hobby");
        hobbyList.clear();
        for (int i = 0; i < hobby.length(); i++) {
          JSONObject object = hobby.getJSONObject(i);
          String categoryName = object.getString(Constants.CATEGORY_NAME_PARAM);
          int categoryId = object.getInt(Constants.CATEGORY_ID_PARAM);
          hobbyList.add(new CategoryBean(categoryId, categoryName));
        }

        boolean dob_privacy = data.getBoolean("dob_privacy");

        if (introduction == null || introduction.equalsIgnoreCase("")) {
          intro_linear.setVisibility(View.GONE);
        } else {
          intro_linear.setVisibility(View.VISIBLE);
          intro.setText(introduction);
        }
        hobbyAdapter = new CategoriesAdapter(hobbyList, getActivity(), "NoClick");
        recyviewCatUserInterest.setAdapter(hobbyAdapter);
        hobbyAdapter.notifyDataSetChanged();

        try {
          school_name = data.getString("school_name");
        } catch (JSONException e) {
          e.printStackTrace();
        }

        if (loggedInUserId.equalsIgnoreCase(otherUserId)) {
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.PROFILE_URL, path);

          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.SCHOOL_NAME, school_name);

          //SharedPreferenceSingleton.getInstance()
          //    .writeStringPreference(RegistrationConstants.GENDER, gender);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.FIRST_NAME, first_name);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.LAST_NAME, last_name);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.DATE_OF_BIRTHDAY, dob_string);

          edit.setVisibility(View.VISIBLE);
          nofriensdslay.setVisibility(View.GONE);
          lblPersonalInfoTitle.setText("Personal Info (only you can view)");
          email_txt.setText("Email : " + email);
          phone.setText("Phone Number : " + mobile);
          dob.setText("DOB : " + dob_string);
        } else {
          edit.setVisibility(View.GONE);
          otherUserId = user_id;
          if (is_my_friend) {
            if (email_privacy && mobile_privacy && dob_privacy && education_privacy) {
              nofriensdslay.setVisibility(View.VISIBLE);
              nofriensdslay.setText("Personal info are hidden");
              email_txt.setVisibility(View.GONE);
              phone.setVisibility(View.GONE);
              dob.setVisibility(View.GONE);
              school.setVisibility(View.GONE);
            } else {
              nofriensdslay.setVisibility(View.GONE);
              if (email_privacy) {
                email_txt.setVisibility(View.GONE);
              } else {
                email_txt.setText("Email : " + email);
              }
              if (mobile_privacy) {
                phone.setVisibility(View.GONE);
              } else {
                phone.setText("Phone Number : " + mobile);
              }
              if (dob_privacy) {
                dob.setVisibility(View.GONE);
              } else {
                dob.setText("DOB : " + dob_string);
              }
              if (education_privacy) {
                school.setVisibility(View.GONE);
              } else {
                //school.setText(studiesInfo);
                school.setVisibility(View.VISIBLE);
              }
            }
          } else {
            nofriensdslay.setVisibility(View.VISIBLE);
            nofriensdslay.setText("Personal info are hidden");
            email_txt.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            dob.setVisibility(View.GONE);
            school.setVisibility(View.GONE);
          }
        }
        callapiforgetPost(user_id);
      }
      //if (RequestConstants.REQUEST_REPORT_POST == REQUEST_NUMBER) {
      //
      //}
      //if (RequestConstants.REQUEST_DELETE_POST == REQUEST_NUMBER) {
      //
      //}
    } catch (Exception e) {
      Log.e("AboutFragment", output);
      e.printStackTrace();
    }
  }

  private void callapiforgetPost(String user_id) {
    if (!this.loggedInUserId.equals(this.otherUserId)) {
      if (this.isPendingFriend || !this.isMyFriend) {
        this.linerLoadmore.setVisibility(View.GONE);
        this.momentsRecyclerview.setVisibility(View.GONE);
        this.nofriensdslay1.setVisibility(View.VISIBLE);
        this.nofriensdslay1.setText("Posts are hidden by " + first_name + " " + last_name);
        return;
      }
    }
    Apimethods methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    Call<GetPostResponseModel> call = null;
    if (user_id.equalsIgnoreCase(loggedInUserId)) {
      call = methods.get_user_Post(user_id, page, pagelimit, 1, accessToken, loggedInUserId);
      Log.e("api call", call.request().url().toString());
    } else {
      call = methods.get_user_Post_friend_profile(user_id, loggedInUserId, page, pagelimit, 1);
      Log.e("api call", call.request().url().toString());
    }

    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(Call<GetPostResponseModel> call,
          Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        if (statuscode == 200) {
          apiCallBlocler = true;
          linerLoadmore.setVisibility(View.GONE);

          GetPostResponseModel getresponsemodel = response.body();
          Log.e("response", getresponsemodel.toString());
          ArrayList<GetPostResponseModel.PostDetails> getdatalist = null;
          if (getresponsemodel != null) {
            getdatalist = (ArrayList
                <GetPostResponseModel.PostDetails>) getresponsemodel.getData();
            if (page == 1) {
              allSampleData.clear();
              if (getdatalist.size() > 0) {
                for (int i = 0; i < getdatalist.size(); i++) {
                  SectionDataModel section = new SectionDataModel();
                  section.setIsPost(false);
                  section.setPostid(getdatalist.get(i).getId());
                  section.setShare_to(getdatalist.get(i).getShareTo());
                  section.setPost_creator_name(
                      getdatalist.get(i).getFirst_name() + " " + getdatalist.get(i).getLast_name());
                  section.setPosted_creator_tagged_number(getdatalist.get(i).getTagged_number());
                  section.setPost_creator_profilepic(getdatalist.get(i).getProfile_url());
                  section.setPost_created_date_time(getdatalist.get(i).getDateCreated());

                  section.setOwner_post_creator_name(
                      getdatalist.get(i).getOwner_first_name() + " " + getdatalist.get(i)
                          .getOwner_last_name());
                  section.setOwner_tagged_number(getdatalist.get(i).getOwner_tagged_number());
                  section.setOwner_post_creator_id(getdatalist.get(i).getOwner_post_creator_id());
                  section.setOwner_post_id(getdatalist.get(i).getOwner_post_id());
                  section.setOriginal_post_date(getdatalist.get(i).getOriginal_post_date());
                  section.setOwner_post_creator_profilepic(
                      getdatalist.get(i).getOwner_profile_url());
                  //////for imagelist////////////////////////////////
                  List<String> imagepathlist = new ArrayList<String>();
                  List<String> imagepathlist_height = new ArrayList<String>();
                  List<String> imagepathlist_width = new ArrayList<String>();
                  for (int j = 0; j < getdatalist.get(i).getImage().size(); j++) {
                    imagepathlist.add(getdatalist.get(i).getImage().get(j).getUrl());
                    imagepathlist_height.add(
                        String.valueOf(getdatalist.get(i).getImage().get(j).getHeight()));
                    imagepathlist_width.add(
                        String.valueOf(getdatalist.get(i).getImage().get(j).getWidth()));
                    section.setPost_image_createdby_creator_url(imagepathlist);
                    section.setPost_image_createdby_creator_height(imagepathlist_height);
                    section.setPost_image_createdby_creator_weidth(imagepathlist_width);
                  }
                  List<String> v_thumb_imagepathlist = new ArrayList<String>();
                  List<String> v_thumb_imagepathlist_height = new ArrayList<String>();
                  List<String> v_thumb_imagepathlist_width = new ArrayList<String>();
                  for (int j = 0; j < getdatalist.get(i).getVideoThumbnails().size(); j++) {
                    v_thumb_imagepathlist.add(
                        getdatalist.get(i).getVideoThumbnails().get(j).getUrl());
                    v_thumb_imagepathlist_height.add(
                        String.valueOf(getdatalist.get(i).getVideoThumbnails().get(j).getHeight()));
                    v_thumb_imagepathlist_width.add(
                        String.valueOf(getdatalist.get(i).getVideoThumbnails().get(j).getWidth()));
                    section.setPost_video_thumb_createdby_creator(v_thumb_imagepathlist);
                    section.setPost_video_thumb_createdby_creator_height(
                        v_thumb_imagepathlist_height);
                    section.setPost_video_thumb_createdby_creator_weidth(
                        v_thumb_imagepathlist_width);
                  }
                  if (getdatalist.get(i).getVideo() != null) {
                    section.setPost_video_url(getdatalist.get(i).getVideo().getUrl());
                    section.setPost_video_id(getdatalist.get(i).getVideo().getId());
                    section.setPost_video_height(getdatalist.get(i).getVideo().getHeight());
                    section.setPost_video_width(getdatalist.get(i).getVideo().getWidth());
                  }
                  List<String> videothumblist = new ArrayList<String>();
                  for (int j = 0; j < getdatalist.get(i).getVideoThumbnails().size(); j++) {
                    videothumblist.add(getdatalist.get(i).getVideoThumbnails().get(j).getUrl());
                    section.setPost_video_thumb_createdby_creator(videothumblist);
                  }
                  section.setCoolcount(getdatalist.get(i).getCoolCount());
                  section.setLikecount(getdatalist.get(i).getLikeCount());
                  section.setSwegcount(getdatalist.get(i).getSwegCount());
                  section.setNerdcount(getdatalist.get(i).getNerdCount());
                  section.setDabcount(getdatalist.get(i).getDabCount());
                  section.setCommentcount(getdatalist.get(i).getConversationCount());

                  section.setAction_flag(0);

                  if (getdatalist.get(i).getUserLike() == true) {
                    section.setAction_flag(5);
                  } else if (getdatalist.get(i).getUser_cool() == true) {
                    section.setAction_flag(1);
                  } else if (getdatalist.get(i).getUser_sweg() == true) {
                    section.setAction_flag(2);
                  } else if (getdatalist.get(i).getUser_nerd() == true) {
                    section.setAction_flag(3);
                  } else if (getdatalist.get(i).getUser_dab() == true) {
                    section.setAction_flag(4);
                  }

                  section.setText_description(getdatalist.get(i).getContent());
                  section.setPost_userid(loggedInUserId);
                  if (getdatalist.get(i).getShareTo()) {
                    section.setOwner_post_type_id(getdatalist.get(i).getOwner_post_type_id());
                  }
                  section.setPost_type_id(getdatalist.get(i).getPost_type_id());
                  section.setView_to(getdatalist.get(i).getView_to());

                  allSampleData.add(section);
                }
                momentsRecyclerview.setVisibility(View.VISIBLE);
                nofriensdslay1.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
              } else {
                momentsRecyclerview.setVisibility(View.GONE);
                nofriensdslay1.setVisibility(View.VISIBLE);
                if (loggedInUserId.equalsIgnoreCase(otherUserId)) {
                  nofriensdslay1.setText("you have no posts to show");
                } else {
                  nofriensdslay1.setText("Posts are hidden by " + first_name + " " + last_name);
                }
              }
            }
            page++;
          }
        }
        if (statuscode == 401) {

        }
      }

      @Override
      public void onFailure(@NonNull Call<GetPostResponseModel> call, @NonNull Throwable t) {
        Log.d(TAG, "Failed : " + call.request().url().toString());
        t.printStackTrace();
      }
    });
  }

  public void updateDataset(int position, int action_flag, final TextView txtReact,
      final ImageView imagereact) {
    allSampleData.get(position).setAction_flag(action_flag);

    if (action_flag == 5) {
      LikeJsonInputModel json = new LikeJsonInputModel();
      json.setPost_id(allSampleData.get(position).getPostid());
      json.setUser_id(loggedInUserId);
      int currentLikeCount = allSampleData.get(position).getLikecount();
      currentLikeCount = currentLikeCount + 1;
      allSampleData.get(position).setLikecount(currentLikeCount);
      CommonApicallModule.callApiForLike(json, accessToken, getActivity());
    } else {
      if (action_flag == 1) {
        int currentCount = allSampleData.get(position).getCoolcount();
        currentCount = currentCount + 1;
        allSampleData.get(position).setCoolcount(currentCount);
      } else if (action_flag == 2) {
        int currentCount = allSampleData.get(position).getSwegcount();
        currentCount = currentCount + 1;
        allSampleData.get(position).setSwegcount(currentCount);
      } else if (action_flag == 3) {
        int currentCount = allSampleData.get(position).getNerdcount();
        currentCount = currentCount + 1;
        allSampleData.get(position).setNerdcount(currentCount);
      } else if (action_flag == 4) {
        int currentCount = allSampleData.get(position).getDabcount();
        currentCount = currentCount + 1;
        allSampleData.get(position).setDabcount(currentCount);
      }
      InsertCoolModel json = new InsertCoolModel();
      json.setPost_id(allSampleData.get(position).getPostid());
      json.setFriend_user_id(loggedInUserId);
      json.setFlag(action_flag);
      CommonApicallModule.insertCoolSwagDebNerd(json, accessToken, getActivity());
    }
    if (action_flag == 1) {
      txtReact.setText("Cool");
      imagereact.setImageResource(R.drawable.svg_cool_emoji);
    } else if (action_flag == 2) {
      txtReact.setText("Swag");
      imagereact.setImageResource(R.drawable.svg_swag_emoji);
    } else if (action_flag == 4) {
      txtReact.setText("Dab");
      imagereact.setImageResource(R.drawable.svg_dab_emoji);
    } else if (action_flag == 3) {
      txtReact.setText("Nerd");
      imagereact.setImageResource(R.drawable.ic_nerd);
    } else if (action_flag == 5) {
      txtReact.setText("Heart");
      imagereact.setImageResource(R.drawable.ic_svg_heart);
    } else {
      txtReact.setText("React");
      imagereact.setImageResource(R.drawable.ic_svg_cool_select);
    }
    adapter.notifyDataSetChanged();
  }

  public void deleteDataset(int position, int action_flag, final TextView txtReact,
      final ImageView imagereact) {

    if (action_flag == 1) {
      int currentCount = allSampleData.get(position).getCoolcount();
      currentCount = currentCount - 1;
      allSampleData.get(position).setCoolcount(currentCount);
    } else if (action_flag == 2) {
      int currentCount = allSampleData.get(position).getSwegcount();
      currentCount = currentCount - 1;
      allSampleData.get(position).setSwegcount(currentCount);
    } else if (action_flag == 3) {
      int currentCount = allSampleData.get(position).getNerdcount();
      currentCount = currentCount - 1;
      allSampleData.get(position).setNerdcount(currentCount);
    } else if (action_flag == 4) {
      int currentCount = allSampleData.get(position).getDabcount();
      currentCount = currentCount - 1;
      allSampleData.get(position).setDabcount(currentCount);
    } else if (action_flag == 5) {
      int currentCount = allSampleData.get(position).getLikecount();
      currentCount = currentCount - 1;
      allSampleData.get(position).setLikecount(currentCount);
    }

    InsertCoolModel json = new InsertCoolModel();
    json.setFlag(action_flag);
    json.setPost_id(allSampleData.get(position).getPostid());
    json.setFriend_user_id(loggedInUserId);
    CommonApicallModule.deleteCoolSwagDebNerd(json, accessToken, getActivity());
    allSampleData.get(position).setAction_flag(0);

    txtReact.setText("React");
    imagereact.setImageResource(R.drawable.ic_svg_cool_select);
    adapter.notifyDataSetChanged();
  }

  public void share_dialog_show(final int position) {
    name.setVisibility(View.GONE);
    buttonLayout.setVisibility(View.GONE);
    continueorder.setVisibility(View.GONE);
    dismiss.setVisibility(View.GONE);
    ok.setVisibility(View.VISIBLE);
    msg.setText("You can only share a Public Post");
    ok.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        d.dismiss();
      }
    });
    d.show();
  }

  public void deleteOrReportPost(final int position) {
    final String postUserid = allSampleData.get(position).getPost_userid();
    final String postid = allSampleData.get(position).getPostid();
    if (loggedInUserId.equalsIgnoreCase(postUserid)) {
      name.setVisibility(View.GONE);
      buttonLayout.setVisibility(View.VISIBLE);
      continueorder.setVisibility(View.VISIBLE);
      dismiss.setVisibility(View.VISIBLE);
      ok.setVisibility(View.GONE);
      msg.setText("Are you sure you want to delete this post ?");
      dismiss.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          d.dismiss();
        }
      });
      continueorder.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          JSONObject BroadcastObject = new JSONObject();
          try {
            BroadcastObject.put("post_id", postid);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          String Url = REQUEST_DELETE_POST;
          reportDelete(Url, BroadcastObject, RequestConstants.REQUEST_DELETE_POST);
          allSampleData.remove(position);
          adapter.notifyDataSetChanged();
          d.dismiss();
        }
      });
      d.show();
    } else {
      final Dialog onLongPressdialog = new Dialog(getActivity());
      onLongPressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      onLongPressdialog.setContentView(R.layout.privacy_dialog);
      RelativeLayout public_relative =
          (RelativeLayout) onLongPressdialog.findViewById(R.id.relative1);
      RelativeLayout private_relative =
          (RelativeLayout) onLongPressdialog.findViewById(R.id.relative2);
      RelativeLayout frnds_relative =
          (RelativeLayout) onLongPressdialog.findViewById(R.id.relative3);
      RelativeLayout bff_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative4);
      RelativeLayout fan_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative5);
      final ImageView profile_image =
          (ImageView) onLongPressdialog.findViewById(R.id.imageProfilePic);
      final ImageView img1 = (ImageView) onLongPressdialog.findViewById(R.id.img1);
      final ImageView img2 = (ImageView) onLongPressdialog.findViewById(R.id.img2);
      final ImageView img3 = (ImageView) onLongPressdialog.findViewById(R.id.img3);
      final ImageView img4 = (ImageView) onLongPressdialog.findViewById(R.id.img4);
      final ImageView img5 = (ImageView) onLongPressdialog.findViewById(R.id.img5);
      TextView done = (TextView) onLongPressdialog.findViewById(R.id.done);
      TextView cancel = (TextView) onLongPressdialog.findViewById(R.id.cancel);
      TextView text = (TextView) onLongPressdialog.findViewById(R.id.text);
      TextView text1 = (TextView) onLongPressdialog.findViewById(R.id.text1);
      TextView text2 = (TextView) onLongPressdialog.findViewById(R.id.text2);
      TextView text3 = (TextView) onLongPressdialog.findViewById(R.id.text3);
      TextView text4 = (TextView) onLongPressdialog.findViewById(R.id.text4);
      TextView text5 = (TextView) onLongPressdialog.findViewById(R.id.text5);
      TextView text6 = (TextView) onLongPressdialog.findViewById(R.id.text6);
      text2.setText("Spam");
      text3.setText("Violent");
      text4.setText("Inappropriate");
      text5.setText("Self Harm");
      text6.setText("Offensive");
      done.setText("Report");
      text1.setVisibility(View.VISIBLE);
      cancel.setVisibility(View.VISIBLE);
      profile_image.setVisibility(View.VISIBLE);
      Glide.with(getActivity())
          .load(allSampleData.get(position).getPost_creator_profilepic())
          .fitCenter()
          .into(profile_image);
      text.setText(allSampleData.get(position).getPost_creator_name());
      cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onLongPressdialog.dismiss();
        }
      });

      public_relative.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          img1.setVisibility(View.VISIBLE);
          img2.setVisibility(View.GONE);
          img3.setVisibility(View.GONE);
          img4.setVisibility(View.GONE);
          img5.setVisibility(View.GONE);
        }
      });
      private_relative.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          img1.setVisibility(View.GONE);
          img2.setVisibility(View.VISIBLE);
          img3.setVisibility(View.GONE);
          img4.setVisibility(View.GONE);
          img5.setVisibility(View.GONE);
        }
      });
      frnds_relative.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          img1.setVisibility(View.GONE);
          img2.setVisibility(View.GONE);
          img3.setVisibility(View.VISIBLE);
          img4.setVisibility(View.GONE);
          img5.setVisibility(View.GONE);
        }
      });
      bff_relative.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          img1.setVisibility(View.GONE);
          img2.setVisibility(View.GONE);
          img3.setVisibility(View.GONE);
          img4.setVisibility(View.VISIBLE);
          img5.setVisibility(View.GONE);
        }
      });
      fan_relative.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          img1.setVisibility(View.GONE);
          img2.setVisibility(View.GONE);
          img3.setVisibility(View.GONE);
          img4.setVisibility(View.GONE);
          img5.setVisibility(View.VISIBLE);
        }
      });
      done.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String msg = "";
          if (img1.getVisibility() == View.VISIBLE) {
            msg = "Spam";
          }
          if (img2.getVisibility() == View.VISIBLE) {
            msg = "Violent";
          }
          if (img3.getVisibility() == View.VISIBLE) {
            msg = "Inappropriate";
          }
          if (img4.getVisibility() == View.VISIBLE) {
            msg = "Self Harm";
          }
          if (img5.getVisibility() == View.VISIBLE) {
            msg = "Offensive";
          }
          JSONObject BroadcastObject = new JSONObject();
          try {
            BroadcastObject.put("post_id", postid);
            BroadcastObject.put("user_id", loggedInUserId);
            BroadcastObject.put("message", msg);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          String Url = REQUEST_REPORT_POST;
          reportDelete(Url, BroadcastObject, RequestConstants.REQUEST_REPORT_POST);
          onLongPressdialog.dismiss();
        }
      });

      onLongPressdialog.show();
    }
  }

  private void reportDelete(String Url, JSONObject jsonObject, String code) {
    AsyncWorker mWorker = new AsyncWorker(getActivity());
    mWorker.delegate = this;
    mWorker.execute(Url, jsonObject.toString(), RequestConstants.POST_REQUEST,
        RequestConstants.HEADER_NO, code);
  }
}
