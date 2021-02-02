package in.tagteen.tagteen;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Fragments.beans.CampusLiveInputJson;
import in.tagteen.tagteen.Fragments.beans.FileDataSender;
import in.tagteen.tagteen.Fragments.youthtube.bean.VideoDataSender;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnFileUploadListener;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.AddFloatingActionButton;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.FileUploadHelper;
import in.tagteen.tagteen.utils.VideoUploadMoment;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_DELETE_POST;
import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_REPORT_POST;

public class CampusLiveFragment extends BaseFragment implements AsyncResponse {
    private ImageView imgPlacement;
    private ImageView imgFilter;
    private AddFloatingActionButton btnCreatePost;
    private RecyclerView recyclerCampusLive;
    private TextView lblNoPosts;
    private CampusLiveAdapter campusLiveAdapter;
    private ProgressBar loadingSpinner;
    private TextView lblAcademicName;

    private List<SectionDataModel> models = new ArrayList<SectionDataModel>();
    private List<String> loadedPostIds = new ArrayList<String>();

    private VideoDataSender videoDataSender;
    private ProgressBar uploadProgress;
    private TextView textProgress;
    private RelativeLayout layoutProgress;
    //private RelativeLayout layoutHeader;
    //private View toolbarShadow;

    private int searchType = 1;
    private int page = 1;
    private int pageLimit = 10;
    private boolean apiCallBlocler = false;
    private String loggedInUserId;

    private SwipeRefreshLayout simpleSwipeRefreshLayout;
    private boolean isLoadingInProgress = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_campus_live, container, false);

        this.initWidgets(view);
        this.bindEvents();

        return view;
    }

    private void initWidgets(View view) {
        this.loggedInUserId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        this.imgPlacement = (ImageView) view.findViewById(R.id.imgPlacement);
        this.imgFilter = (ImageView) view.findViewById(R.id.imgFilter);
        this.btnCreatePost = (AddFloatingActionButton) view.findViewById(R.id.btnCreatePost);
        this.recyclerCampusLive = (RecyclerView) view.findViewById(R.id.recyclerCampusLive);
        this.simpleSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        this.simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        this.lblNoPosts = (TextView) view.findViewById(R.id.lblNoPosts);
        this.loadingSpinner = (ProgressBar) view.findViewById(R.id.loadingSpinner);
        this.lblAcademicName = view.findViewById(R.id.lblAcademicName);
        String academicName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.SCHOOL_NAME);
        if (academicName != null) {
            this.lblAcademicName.setText(academicName);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        this.recyclerCampusLive.setLayoutManager(layoutManager);
        this.loadPosts();

        // video params
        this.videoDataSender = new VideoDataSender();
        this.uploadProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        this.textProgress = (TextView) view.findViewById(R.id.textPer);
        this.layoutProgress = (RelativeLayout) view.findViewById(R.id.layoutProgress);
        this.layoutProgress.setVisibility(View.GONE);
        //this.layoutHeader = (RelativeLayout) findViewById(R.id.layouttop);
        //this.toolbarShadow = (View) findViewById(R.id.toolbar_shadow);
    }

    private void loadPosts() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCampusLivePosts();
            }
        }, 200);
    }

    private void getCampusLivePosts() {
        this.isLoadingInProgress = true;
        SharedPreferenceSingleton.getInstance().init(getActivity());
        String userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String educationId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.EDUCATION_ID);
        String courseId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.COURSE_ID);
        String degreeId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.DEGREE_ID);
        String yearId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.YEAR_ID);
        String standardId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.STANDARD_ID);

        CampusLiveInputJson inputJson = new CampusLiveInputJson();
        inputJson.setUserId(userId);
        inputJson.setEducationId(educationId);
        if (this.searchType == 2) {
            inputJson.setCourseId(courseId);
        } else if (this.searchType == 3) {
            inputJson.setCourseId(courseId);
            inputJson.setDegreeId(degreeId);
        } else if (this.searchType == 4) {
            inputJson.setCourseId(courseId);
            inputJson.setDegreeId(degreeId);
            inputJson.setYearId(yearId);
        } else if (this.searchType == 5) {
            inputJson.setStandardId(standardId);
        }

        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<GetPostResponseModel> call = methods.getCampusLivePosts(this.searchType, this.page, this.pageLimit, inputJson);

        call.enqueue(new Callback<GetPostResponseModel>() {
            @Override
            public void onResponse(Call<GetPostResponseModel> call, Response<GetPostResponseModel> response) {
                loadingSpinner.setVisibility(View.GONE);
                int statuscode = response.code();
                apiCallBlocler = true;
                isLoadingInProgress = false;
                if (page == 1) {
                    models.clear();
                }
                if (page > 1 && models != null && models.isEmpty() == false) {
                    models.remove(models.size() - 1);
                    campusLiveAdapter.notifyItemRemoved(models.size());
                }
                simpleSwipeRefreshLayout.setRefreshing(false);
                if (statuscode == 200) {
                    page++;
                    GetPostResponseModel getresponsemodel = response.body();
                    ArrayList<GetPostResponseModel.PostDetails> responseList = (ArrayList
                            <GetPostResponseModel.PostDetails>) getresponsemodel.getData();
                    if (responseList.isEmpty()) {
                        apiCallBlocler = false;
                    }

                    for (int i = 0; i < responseList.size(); i++) {
                        SectionDataModel section = new SectionDataModel();
                        GetPostResponseModel.PostDetails model = responseList.get(i);
                        if (loadedPostIds.contains(model.getId())) {
                            continue;
                        }
                        loadedPostIds.add(model.getId());
                        section.setPostid(model.getId());
                        section.setShare_to(model.getShareTo());
                        section.setCategory_id(model.getCategorie_id());
                        section.setPost_creator_name(model.getFirst_name() + " " + model.getLast_name());
                        section.setPosted_creator_tagged_number(model.getTagged_number());
                        section.setPost_creator_profilepic(model.getProfile_url());
                        section.setPost_created_date_time(model.getDateCreated());

                        section.setOwner_post_creator_name(model.getOwner_first_name() + " " + model.getOwner_last_name());
                        section.setOwner_tagged_number(model.getOwner_tagged_number());
                        section.setOwner_post_creator_id(model.getOwner_post_creator_id());
                        section.setOwner_post_id(model.getOwner_post_id());
                        section.setPost_created_date_time(model.getDateCreated());
                        section.setOwner_post_creator_profilepic(model.getOwner_profile_url());
                        //////for imagelist////////////////////////////////
                        List<String> imagepathlist = new ArrayList<String>();
                        List<String> imagepathlist_height = new ArrayList<String>();
                        List<String> imagepathlist_width = new ArrayList<String>();
                        if (model.getImage() != null) {
                            for (int j = 0; j < model.getImage().size(); j++) {
                                imagepathlist.add(model.getImage().get(j).getUrl());
                                imagepathlist_height.add(String.valueOf(model.getImage().get(j).getHeight()));
                                imagepathlist_width.add(String.valueOf(model.getImage().get(j).getWidth()));
                                section.setPost_image_createdby_creator_url(imagepathlist);
                                section.setPost_image_createdby_creator_height(imagepathlist_height);
                                section.setPost_image_createdby_creator_weidth(imagepathlist_width);
                            }
                        }
                        List<String> v_thumb_imagepathlist = new ArrayList<String>();
                        List<String> v_thumb_imagepathlist_height = new ArrayList<String>();
                        List<String> v_thumb_imagepathlist_width = new ArrayList<String>();
                        if (model.getVideoThumbnails() != null) {
                            for (int j = 0; j < model.getVideoThumbnails().size(); j++) {
                                v_thumb_imagepathlist.add(model.getVideoThumbnails().get(j).getUrl());
                                v_thumb_imagepathlist_height.add(String.valueOf(model.getVideoThumbnails().get(j).getHeight()));
                                v_thumb_imagepathlist_width.add(String.valueOf(model.getVideoThumbnails().get(j).getWidth()));
                                section.setPost_video_thumb_createdby_creator(v_thumb_imagepathlist);
                                section.setPost_video_thumb_createdby_creator_height(v_thumb_imagepathlist_height);
                                section.setPost_video_thumb_createdby_creator_weidth(v_thumb_imagepathlist_width);
                            }
                        }
                        if (model.getVideo() != null) {
                            section.setPost_video_url(model.getVideo().getUrl());
                            section.setPost_video_id(model.getVideo().getId());
                            section.setPost_video_height(model.getVideo().getHeight());
                            section.setPost_video_width(model.getVideo().getWidth());
                        }
                        List<String> videothumblist = new ArrayList<String>();
                        for (int j = 0; j < model.getVideoThumbnails().size(); j++) {
                            videothumblist.add(model.getVideoThumbnails().get(j).getUrl());
                            section.setPost_video_thumb_createdby_creator(videothumblist);
                        }
                        section.setCoolcount(model.getCoolCount());
                        section.setLikecount(model.getLikeCount());
                        section.setSwegcount(model.getSwegCount());
                        section.setNerdcount(model.getNerdCount());
                        section.setDabcount(model.getDabCount());

                        section.setCommentcount(model.getConversationCount());
                        section.setAction_flag(0);

                        if (model.getUserLike() == true) {
                            section.setAction_flag(5);
                        } else if (model.getUser_cool() == true) {
                            section.setAction_flag(1);
                        } else if (model.getUser_sweg() == true) {
                            section.setAction_flag(2);
                        } else if (model.getUser_nerd() == true) {
                            section.setAction_flag(3);
                        } else if (model.getUser_dab() == true) {
                            section.setAction_flag(4);
                        }

                        section.setText_description(model.getContent());
                        section.setPost_userid(model.getPostCreatorId());
                        if (model.getShareTo()) {
                            section.setOwner_post_type_id(model.getOwner_post_type_id());
                        }
                        section.setPost_type_id(model.getPost_type_id());
                        section.setView_to(model.getView_to());

                        models.add(section);
                    }

                    if (campusLiveAdapter == null) {
                        campusLiveAdapter = new CampusLiveAdapter(getActivity(), models, recyclerCampusLive);
                        recyclerCampusLive.setAdapter(campusLiveAdapter);
                        campusLiveAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                if (apiCallBlocler) {
                                    models.add(null);
                                    campusLiveAdapter.notifyItemInserted(models.size() - 1);
                                    apiCallBlocler = false;
                                    getCampusLivePosts();
                                }
                            }
                        });
                        campusLiveAdapter.setPostActionListener(new CampusLiveAdapter.PostActionListener() {
                            @Override
                            public void onDeleteOrReportPost(int position) {
                                deleteOrReportPost(position);
                            }
                        });
                        campusLiveAdapter.setOnCallbackListener(new OnCallbackListener() {
                            @Override
                            public void OnComplete() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //layoutHeader.setVisibility(View.INVISIBLE);
                                        //toolbarShadow.setVisibility(View.INVISIBLE);
                                    }
                                }, 500);
                            }
                        });
                    } else {
                        campusLiveAdapter.notifyDataSetChanged();
                        campusLiveAdapter.setLoaded();
                    }

                    if (models.isEmpty()) {
                        recyclerCampusLive.setVisibility(View.GONE);
                        lblNoPosts.setVisibility(View.VISIBLE);
                    } else {
                        recyclerCampusLive.setVisibility(View.VISIBLE);
                        lblNoPosts.setVisibility(View.GONE);
                    }
                }
                if (statuscode == 401) {
                    Log.d("url", "url=" + call.request().url().toString());
                }
            }

            @Override
            public void onFailure(Call<GetPostResponseModel> call, Throwable t) {
                loadingSpinner.setVisibility(View.GONE);
                //Utils.showShortToast(getActivity(), "Campuslive failed loading");
            }
        });
    }

    private void bindEvents() {

        this.imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(imgFilter);
            }
        });
        this.btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCreateCampusLive();
            }
        });
        simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh();
            }
        });
    }

    private void pullToRefresh() {
        if (this.isLoadingInProgress) {
            return;
        }
        this.simpleSwipeRefreshLayout.setRefreshing(true);
        this.loadPosts(this.searchType);
    }

    private void moveToCreateCampusLive() {
        boolean isUploadInProgress = SharedPreferenceSingleton.getInstance().getBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS);
        /*if (isUploadInProgress) {
            Utils.showShortToast(this, Constants.CAMPUSLIVE_UPLOAD_IN_PROGRESS_MSG);
            return;
        }*/
        Intent intent = new Intent(getActivity(), AddCampusLivePostActivity.class);
        startActivityForResult(intent, Constants.ADD_CAMPUS_LIVE_POST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.getActivity() == null) {
            return;
        }
        if (FileDataSender.POST_TO != FileDataSender.POST_TO_CAMPUSLIVE) {
            return;
        }
        if (this.videoDataSender != null && this.videoDataSender.isCall()) {
            SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, true);
            this.videoDataSender.setIsCall(false);
            this.layoutProgress.setVisibility(View.VISIBLE);
            this.uploadProgress.setProgress(0);
            VideoUploadMoment videoUploadMoment =
                    new VideoUploadMoment(
                            getActivity(), this.layoutProgress, this.uploadProgress, this.textProgress);
            videoUploadMoment.setOnVideoUploadListener(new VideoUploadMoment.OnVideoUploadListener() {
                @Override
                public void OnVideoUploaded() {
                    loadPosts(searchType);
                }
            });
        } else if (FileDataSender.HAS_FILE_TO_UPLOAD) {
            SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, true);
            FileDataSender.HAS_FILE_TO_UPLOAD = false;
            this.layoutProgress.setVisibility(View.VISIBLE);
            this.uploadProgress.setProgress(0);
            new FileUploadHelper(
                    getActivity(),
                    this.layoutProgress,
                    this.uploadProgress,
                    this.textProgress,
                    Constants.POST_TYPE_CAMPUSLIVE_INT,
                    new OnFileUploadListener() {
                        @Override
                        public void OnFileUploaded() {
                            SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, false);
                            FileDataSender.clear();
                            loadPosts(searchType);
                        }

                        @Override
                        public void onFileUploadFailed() {
                            SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, false);
                            FileDataSender.clear();
                        }
                    });
        } else {
            //loadPosts(searchType);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.ADD_CAMPUS_LIVE_POST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                pullToRefresh();
            }
        } else if (requestCode == Constants.COMMENT_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (this.campusLiveAdapter != null && data.getSerializableExtra(Constants.SELECTED_MODEL) != null) {
                    SectionDataModel model = (SectionDataModel) data.getSerializableExtra(Constants.SELECTED_MODEL);
                    this.campusLiveAdapter.refreshSelectedModel(model);
                }
            }
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();
        inflater.inflate(R.menu.menu_campus_live, menu);

        String currentlyStudying =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.CURRENTLY_STUDYING);
        if (currentlyStudying != null) {
            MenuItem allMenu = menu.findItem(R.id.optionAll);
            MenuItem courseMenu = menu.findItem(R.id.optionByCourse);
            MenuItem degreeMenu = menu.findItem(R.id.optionByDegree);
            MenuItem yearMenu = menu.findItem(R.id.optionByYear);
            MenuItem standardMenu = menu.findItem(R.id.optionByStandard);
            if (currentlyStudying.equals(Constants.SCHOOL)) {
                courseMenu.setVisible(false);
                degreeMenu.setVisible(false);
                yearMenu.setVisible(false);
            } else {
                standardMenu.setVisible(false);
            }

            allMenu.setTitle(Constants.ALL_POSTS);
            courseMenu.setTitle(Constants.MY_COURSE_POSTS);
            degreeMenu.setTitle(Constants.MY_DEGREE_POSTS);
            yearMenu.setTitle(Constants.MY_YEAR_POSTS);
            standardMenu.setTitle(Constants.MY_STANDARD_POSTS);
            String title = null;
            switch (this.searchType) {
                case 1:
                    title = "<b>" + Constants.ALL_POSTS + "</b>";
                    allMenu.setTitle(Html.fromHtml(title));
                    break;
                case 2:
                    title = "<b>" + Constants.MY_COURSE_POSTS + "</b>";
                    courseMenu.setTitle(Html.fromHtml(title));
                    break;
                case 3:
                    title = "<b>" + Constants.MY_DEGREE_POSTS + "</b>";
                    degreeMenu.setTitle(Html.fromHtml(title));
                    break;
                case 4:
                    title = "<b>" + Constants.MY_YEAR_POSTS + "</b>";
                    yearMenu.setTitle(Html.fromHtml(title));
                    break;
                case 5:
                    title = "<b>" + Constants.MY_STANDARD_POSTS + "</b>";
                    standardMenu.setTitle(Html.fromHtml(title));
                    break;
            }
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.optionAll:
                        loadPosts(1);
                        break;

                    case R.id.optionByCourse:
                        loadPosts(2);
                        break;

                    case R.id.optionByDegree:
                        loadPosts(3);
                        break;

                    case R.id.optionByYear:
                        loadPosts(4);
                        break;

                    case R.id.optionByStandard:
                        loadPosts(5);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void loadPosts(int option) {
        this.searchType = option;
        this.models.clear();
        if (this.campusLiveAdapter != null) {
            this.campusLiveAdapter.notifyDataSetChanged();
        }
        this.loadedPostIds.clear();
        this.page = 1;
        this.getCampusLivePosts();
    }

    private void deleteOrReportPost(final int position) {
        final SectionDataModel model = this.models.get(position);
        if (this.loggedInUserId.equalsIgnoreCase(model.getPost_userid())) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.confirmationpopup);
            TextView msg = (TextView) dialog.findViewById(R.id.message);
            TextView lblName = (TextView) dialog.findViewById(R.id.yourname);
            lblName.setVisibility(View.GONE);
            TextView lblContinue = (TextView) dialog.findViewById(R.id.confirm);
            TextView lblDismiss = (TextView) dialog.findViewById(R.id.dismiss);
            TextView lblOk = (TextView) dialog.findViewById(R.id.confirm_ok_btn);
            lblOk.setVisibility(View.GONE);
            LinearLayout buttonLayout = (LinearLayout) dialog.findViewById(R.id.button_layout);

            buttonLayout.setVisibility(View.VISIBLE);
            lblContinue.setVisibility(View.VISIBLE);
            lblDismiss.setVisibility(View.VISIBLE);
            msg.setText("Are you sure you want to delete this post ?");
            lblDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            lblContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject BroadcastObject = new JSONObject();
                    try {
                        BroadcastObject.put("post_id", model.getPostid());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String Url = REQUEST_DELETE_POST;
                    reportDelete(Url, BroadcastObject, RequestConstants.REQUEST_DELETE_POST);
                    models.remove(position);
                    campusLiveAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            final Dialog onLongPressdialog = new Dialog(getActivity());
            onLongPressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            onLongPressdialog.setCanceledOnTouchOutside(true);
            onLongPressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            onLongPressdialog.setContentView(R.layout.privacy_dialog);
            //onLongPressdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            RelativeLayout public_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative1);
            RelativeLayout private_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative2);
            RelativeLayout frnds_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative3);
            RelativeLayout bff_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative4);
            RelativeLayout fan_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative5);
            final ImageView profile_image = (ImageView) onLongPressdialog.findViewById(R.id.imageProfilePic);
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
            Utils.loadProfilePic(getContext(), profile_image, model.getPost_creator_profilepic());
            text.setText(model.getPost_creator_name());
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
                        BroadcastObject.put("post_id", model.getPostid());
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
        mWorker.delegate = this;
        mWorker.execute(Url, jsonObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_NO, code);
    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {

    }

    @Override
    public void onRefresh() {

    }
}
