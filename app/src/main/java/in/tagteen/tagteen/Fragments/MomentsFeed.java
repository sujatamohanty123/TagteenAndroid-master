package in.tagteen.tagteen.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.tagteen.tagteen.Adapters.AllSelfyListHorizntalAdapter;
import in.tagteen.tagteen.Adapters.MomentslistAdapter;
import in.tagteen.tagteen.ConnectivityReceiver;
import in.tagteen.tagteen.CreatePostActivity_Keypadheight;
import in.tagteen.tagteen.Fragments.beans.FileDataSender;
import in.tagteen.tagteen.Fragments.youthtube.adapter.LatestUsersAdapter;
import in.tagteen.tagteen.Fragments.youthtube.bean.UserInfoBean;
import in.tagteen.tagteen.Fragments.youthtube.bean.VideoDataSender;
import in.tagteen.tagteen.Interfaces.OnFileUploadListener;
import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;
import in.tagteen.tagteen.Model.AllSelfyList;
import in.tagteen.tagteen.Model.FriendSeach;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.InsertCoolModel;
import in.tagteen.tagteen.Model.LikeJsonInputModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.Model.ShoutOutModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.SearchActivity;
import in.tagteen.tagteen.SelfiCameraPreview;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.backgroundUpload.UploadService;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.chatting.ActivityChat;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.selfyManager.VideoDataSender_selfy;
import in.tagteen.tagteen.selfyManager.VideoUploadMoment_selfy;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.FloatingActionButton;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.FileUploadHelper;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.tagteen.tagteen.backgroundUpload.UploadService.ImageTeenFeedImageUpload;
import static in.tagteen.tagteen.backgroundUpload.UploadService.POST_TYPE;
import static in.tagteen.tagteen.backgroundUpload.UploadService.ProgressDone;
import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_DELETE_POST;
import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_REPORT_POST;

public class MomentsFeed extends BaseFragment
        implements ConnectivityReceiver.ConnectivityReceiverListener, AsyncResponse {

    private boolean mScreenLoadedOnce = false;

    public static final int REQUEST_DELETE = 0xec;
    private LatestUsersAdapter usersAdapter;
    private ArrayList<UserInfoBean> usersList;

    private boolean apiCallBlocler = false;
    private int page = 1;
    private int pageselfy = 1;
    private int pagelimit = 10;
    private ProgressBar uploadProgress;
    private TextView textProgress;
    private RelativeLayout layoutProgress;
    private ArrayList<AllSelfyList.SelfyData> allSelfyLists = new ArrayList<>();
    private List<String> selfieList = new ArrayList<>();
    private AllSelfyListHorizntalAdapter selfyListHorizntalAdapter;
    private RecyclerView recyviewCategories;
    private ArrayList<SectionDataModel> allSampleData = new ArrayList<>();
    private MomentslistAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout simpleSwipeRefreshLayout;
    private NestedScrollView nestedScrollView;
    private TextView no_internet, welcome_msg;
    private AppCompatEditText txtSearch;
    private LinearLayout layoutChat;
    private RelativeLayout buzz_main_container;
    private LinearLayout layoutMomentsInfo;
    private RecyclerView recyclerViewUsersList;
    private LinearLayout layoutMain;
    private View line;
    private VideoDataSender videoDataSender;
    private VideoDataSender_selfy videoDataSender_selfy;
    private boolean isTaggedUser = SharedPreferenceSingleton.getInstance()
            .getBoolPreference(RegistrationConstants.IS_TAGGED_USER);
    private final String logedInUserid =
            SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    private final String logedInUserFName =
            SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FIRST_NAME);
    private final String logedInUserLName =
            SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.LAST_NAME);
    private String Accesstoken =
            SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    private ShimmerFrameLayout shimmerFrameLayout;
    private Dialog d;
    private TextView msg, name, continueorder, dismiss, ok;
    private LinearLayout buttonLayout;
    private FloatingActionButton fabAddMoments;
    private TextView lblMarqueeText;
    private TextView txtMoments;

    private boolean isLoadingData = false;
    private OnPostDeleteListener onPostDeleteListener;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    Apimethods methods;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_moments, container, false);
        initComponent();
        buzz_main_container = (RelativeLayout) view.findViewById(R.id.buzz_main_container);
        this.layoutMomentsInfo = view.findViewById(R.id.layoutMomentsInfo);
        no_internet = (TextView) view.findViewById(R.id.no_internet);
        line = view.findViewById(R.id.line);
        txtMoments = view.findViewById(R.id.txt_moments);
        welcome_msg = (TextView) view.findViewById(R.id.welcome_msg);
        shimmerFrameLayout = (ShimmerFrameLayout) view.findViewById(R.id.shimmerLayout);
        this.fabAddMoments = view.findViewById(R.id.fabAddMoments);
        this.layoutChat = view.findViewById(R.id.layoutChat);
        this.lblMarqueeText = view.findViewById(R.id.lblMarqueeText);
        this.lblMarqueeText.setSelected(true);
        this.recyclerViewUsersList = view.findViewById(R.id.recyclerviewNewies);
        this.recyclerViewUsersList.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        this.recyclerViewUsersList.setLayoutManager(layoutManager);
        this.recyclerViewUsersList.setVisibility(View.VISIBLE);
        layoutMain = view.findViewById(R.id.frame_relative);
        checkConnection();
        /* checkVerifiedUser();*/

        d = new Dialog(Objects.requireNonNull(getContext()));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.confirmationpopup);
        msg = (TextView) d.findViewById(R.id.message);
        name = (TextView) d.findViewById(R.id.yourname);
        continueorder = (TextView) d.findViewById(R.id.confirm);
        dismiss = (TextView) d.findViewById(R.id.dismiss);
        this.ok = (TextView) d.findViewById(R.id.confirm_ok_btn);
        buttonLayout = (LinearLayout) d.findViewById(R.id.button_layout);
        simpleSwipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        uploadProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        textProgress = (TextView) view.findViewById(R.id.textPer);
        layoutProgress = (RelativeLayout) view.findViewById(R.id.layoutProgress);
        layoutProgress.setVisibility(View.GONE);
        videoDataSender = new VideoDataSender();
        videoDataSender_selfy = new VideoDataSender_selfy();
        this.nestedScrollView = view.findViewById(R.id.nestedScrollview);
        this.txtSearch = view.findViewById(R.id.txtSearch);
        recyviewCategories = (RecyclerView) view.findViewById(R.id.recyclerviewCategories);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager2 =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyviewCategories.setHasFixedSize(true);
        recyviewCategories.setLayoutManager(layoutManager2);
        this.initSelfieAdapter();
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        bindEvents();
        return view;
    }

    private void initComponent() {
        methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !mScreenLoadedOnce) {
            loadData();
            mScreenLoadedOnce = true;
        }
        // do the rest of the code here.

    }

    private void bindEvents() {
        this.simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh();
            }
        });
        this.txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), SearchActivity.class);
                startActivity(it);
            }
        });
        this.fabAddMoments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreatePostActivity_Keypadheight.class);
                startActivityForResult(intent, 2);
            }
        });
        this.layoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityChat.class);
                startActivity(intent);
            }
        });

        this.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
                                       int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (apiCallBlocler && isLoadingData == false) {
                        allSampleData.add(null);
                        adapter.notifyItemInserted(allSampleData.size() - 1);
                        apiCallBlocler = false;
                        callapiforgetPost();
                    }
                }
            }
        });
    }

    public void setOnPostDeleteListener(OnPostDeleteListener onPostDeleteListener) {
        this.onPostDeleteListener = onPostDeleteListener;
    }

    private void loadData() {
        getShoutText();
        callapiforgetPost();
        getAllSelfylist();
        getAllNewUsersList();
        //new Handler().postDelayed(new Runnable() {
        //  @Override
        //  public void run() {
        //    getShoutText();
        //    callapiforgetPost();
        //    getAllSelfylist();
        //    getAllNewUsersList();
        //  }
        //}, 200);
    }

    private void initSelfieAdapter() {
        this.addSelfiePlaceholder();
        if (this.selfyListHorizntalAdapter == null) {
            this.selfyListHorizntalAdapter =
                    new AllSelfyListHorizntalAdapter(getContext(), this.allSelfyLists);
            this.recyviewCategories.setAdapter(this.selfyListHorizntalAdapter);
        }
    }

    private void addSelfiePlaceholder() {
        AllSelfyList.SelfyData placeholderData = new AllSelfyList().new SelfyData();
        placeholderData.set_id(AllSelfyListHorizntalAdapter.PLACEHOLDER);
        this.allSelfyLists.add(placeholderData);
    }

    private void getAllSelfylist() {
        if (getActivity() == null) {
            return;
        }
        this.allSelfyLists.clear();
        this.selfieList.clear();
        SharedPreferenceSingleton.getInstance().init(getContext());
        String userid =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String token =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Log.d("token", token);
        Call<AllSelfyList> call = methods.getSelfyList(userid, pageselfy, token);

        API_Call_Retrofit.methodCalled(call.request().url().toString());
        call.enqueue(new Callback<AllSelfyList>() {
            @Override
            public void onResponse(@NotNull Call<AllSelfyList> call,
                                   @NotNull Response<AllSelfyList> response) {
                // add placeholder first
                addSelfiePlaceholder();

                int statuscode = response.code();
                if (statuscode == 200) {
                    AllSelfyList getresponsemodel = response.body();
                    ArrayList<AllSelfyList.SelfyData> getdatalist =
                            null;
                    if (getresponsemodel != null) {
                        getdatalist = (ArrayList<AllSelfyList.SelfyData>) getresponsemodel.getSelfyData();
                        for (AllSelfyList.SelfyData data : getdatalist) {
                            if (selfieList.contains(data.get_id())) {
                                continue;
                            }
                            if (!data.get_id().equals(logedInUserid)) {
                                allSelfyLists.add(data);
                            } else {
                                allSelfyLists.add(1, data);
                            }
                            selfieList.add(data.get_id());
                        }
                    }
                    selfyListHorizntalAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NotNull Call<AllSelfyList> call, @NotNull Throwable t) {

            }
        });
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.stopShimmerAnimation();
            buzz_main_container.setVisibility(View.VISIBLE);
            if (!isTaggedUser) {
                buzz_main_container.setVisibility(View.GONE);
                no_internet.setVisibility(View.GONE);
                no_internet.setText(
                        "Hi " + logedInUserFName + " " + logedInUserLName + "\n\n Welcome \nto \nTagteen");
            } else {
                no_internet.setVisibility(View.GONE);
                buzz_main_container.setVisibility(View.VISIBLE);
            }
        } else {
            buzz_main_container.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
            no_internet.setText("No Internet Connection!!!!");
        }
    }

    private void getShoutText() {

        Call<ShoutOutModel> call = methods.getShoutOutText();

        call.enqueue(new Callback<ShoutOutModel>() {
            @Override
            public void onResponse(@NotNull Call<ShoutOutModel> call,
                                   @NotNull Response<ShoutOutModel> response) {
                if (response.code() != 200) {
                    return;
                }

                if (response.body() != null && response.body().getShoutText() != null) {
                    lblMarqueeText.setText(Utils.getHtmlEscapedString(response.body().getShoutText()));
                } else {
                    lblMarqueeText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ShoutOutModel> call, Throwable t) {

            }
        });
    }

    private void callapiforgetPost() {
        SharedPreferenceSingleton.getInstance().init(getActivity());
        String userid =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String token =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Call<GetPostResponseModel> call = methods.getPost(userid, page, pagelimit, 1, token);

        API_Call_Retrofit.methodCalled(call.request().url().toString());
        call.enqueue(new Callback<GetPostResponseModel>() {
            @Override
            public void onResponse(Call<GetPostResponseModel> call,
                                   Response<GetPostResponseModel> response) {
                int statuscode = response.code();
                apiCallBlocler = true;
                if (page > 1 && allSampleData != null && allSampleData.size() > 0) {
                    allSampleData.remove(allSampleData.size() - 1);
                    adapter.notifyItemRemoved(allSampleData.size());
                }
                if (page == 1) {
                    allSampleData.clear();
                }
                simpleSwipeRefreshLayout.setRefreshing(false);
                if (statuscode == 200) {
                    page++;
                    GetPostResponseModel getresponsemodel = response.body();
                    ArrayList<GetPostResponseModel.PostDetails> getdatalist = (ArrayList
                            <GetPostResponseModel.PostDetails>) getresponsemodel.getData();
                    if (getdatalist.isEmpty()) {
                        apiCallBlocler = false;
                    }

                    for (int i = 0; i < getdatalist.size(); i++) {
                        String creatorId = getdatalist.get(i).getPostCreatorId();
                        if (creatorId == null || creatorId.trim().length() == 0) {
                            continue;
                        }
                        if (getdatalist.get(i).getShareTo()
                                && getdatalist.get(i).getOwner_post_type_id() == null) {
                            continue;
                        }
                        SectionDataModel section = new SectionDataModel();
                        section.setPostid(getdatalist.get(i).getId());
                        section.setShare_to(getdatalist.get(i).getShareTo());
                        section.setCategory_id(getdatalist.get(i).getCategorie_id());
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
                        section.setOwner_post_creator_profilepic(getdatalist.get(i).getOwner_profile_url());
                        //////for imagelist////////////////////////////////
                        List<String> imagepathlist = new ArrayList<String>();
                        List<String> imagepathlist_height = new ArrayList<String>();
                        List<String> imagepathlist_width = new ArrayList<String>();
                        if (getdatalist.get(i).getImage() != null) {
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
                        }
                        List<String> v_thumb_imagepathlist = new ArrayList<String>();
                        List<String> v_thumb_imagepathlist_height = new ArrayList<String>();
                        List<String> v_thumb_imagepathlist_width = new ArrayList<String>();
                        if (getdatalist.get(i).getVideoThumbnails() != null) {
                            for (int j = 0; j < getdatalist.get(i).getVideoThumbnails().size(); j++) {
                                v_thumb_imagepathlist.add(getdatalist.get(i).getVideoThumbnails().get(j).getUrl());
                                v_thumb_imagepathlist_height.add(
                                        String.valueOf(getdatalist.get(i).getVideoThumbnails().get(j).getHeight()));
                                v_thumb_imagepathlist_width.add(
                                        String.valueOf(getdatalist.get(i).getVideoThumbnails().get(j).getWidth()));
                                section.setPost_video_thumb_createdby_creator(v_thumb_imagepathlist);
                                section.setPost_video_thumb_createdby_creator_height(v_thumb_imagepathlist_height);
                                section.setPost_video_thumb_createdby_creator_weidth(v_thumb_imagepathlist_width);
                            }
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
                        section.setPost_userid(getdatalist.get(i).getPostCreatorId());
                        if (getdatalist.get(i).getShareTo()) {
                            section.setOwner_post_type_id(getdatalist.get(i).getOwner_post_type_id());
                        }
                        section.setPost_type_id(getdatalist.get(i).getPost_type_id());
                        section.setView_to(getdatalist.get(i).getView_to());

                        allSampleData.add(section);
                    }
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    layoutMain.setVisibility(View.VISIBLE);

                    if (adapter == null) {
                        adapter = new MomentslistAdapter(getActivity(), allSampleData, MomentsFeed.this, 1);
                        recyclerView.setAdapter(adapter);
                        // load more is implemented by nested scroll view
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    isLoadingData = false;
                }
                if (allSampleData == null || allSampleData.isEmpty()) {
                    buzz_main_container.setVisibility(View.VISIBLE);
                    layoutMomentsInfo.setVisibility(View.VISIBLE);
                    lblMarqueeText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    line.setVisibility(View.GONE);
                    recyviewCategories.setVisibility(View.GONE);
                    txtMoments.setVisibility(View.GONE);
                } else {
                    buzz_main_container.setVisibility(View.VISIBLE);
                    layoutMomentsInfo.setVisibility(View.GONE);
                    lblMarqueeText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    recyviewCategories.setVisibility(View.VISIBLE);
                    txtMoments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetPostResponseModel> call, Throwable t) {
                simpleSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void pullToRefresh() {
        this.simpleSwipeRefreshLayout.setRefreshing(true);
        this.page = 1;
        /*this.allSampleData.clear();
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }*/
        this.isLoadingData = true;
        this.getShoutText();
        this.callapiforgetPost();
        this.getAllSelfylist();
    }

    public void updateDataset(int position, int action_flag, final TextView txtReact,
                              final ImageView imagereact) {
        allSampleData.get(position).setAction_flag(action_flag);
        if (action_flag == 5) {
            txtReact.setText("Heart");
            imagereact.setImageResource(R.drawable.ic_svg_heart);
            this.notifyMomentsAdapter();

            LikeJsonInputModel json = new LikeJsonInputModel();
            json.setPost_id(allSampleData.get(position).getPostid());
            json.setUser_id(logedInUserid);
            int currentLikeCount = allSampleData.get(position).getLikecount();
            currentLikeCount = currentLikeCount + 1;
            allSampleData.get(position).setLikecount(currentLikeCount);
            CommonApicallModule.callApiForLike(json, Accesstoken, getActivity());
        } else {
            if (action_flag == 1) {
                int currentCount = allSampleData.get(position).getCoolcount();
                currentCount = currentCount + 1;
                allSampleData.get(position).setCoolcount(currentCount);

                txtReact.setText("Cool");
                imagereact.setImageResource(R.drawable.svg_cool_emoji);
            } else if (action_flag == 2) {
                int currentCount = allSampleData.get(position).getSwegcount();
                currentCount = currentCount + 1;
                allSampleData.get(position).setSwegcount(currentCount);

                txtReact.setText("Swag");
                imagereact.setImageResource(R.drawable.svg_swag_emoji);
            } else if (action_flag == 3) {
                int currentCount = allSampleData.get(position).getNerdcount();
                currentCount = currentCount + 1;
                allSampleData.get(position).setNerdcount(currentCount);

                txtReact.setText("Nerd");
                imagereact.setImageResource(R.drawable.ic_nerd);
            } else if (action_flag == 4) {
                int currentCount = allSampleData.get(position).getDabcount();
                currentCount = currentCount + 1;
                allSampleData.get(position).setDabcount(currentCount);

                txtReact.setText("Dab");
                imagereact.setImageResource(R.drawable.svg_dab_emoji);
            } else {
                txtReact.setText("React");
                imagereact.setImageResource(R.drawable.ic_svg_cool_select);
            }
            this.notifyMomentsAdapter();

            InsertCoolModel json = new InsertCoolModel();
            json.setPost_id(allSampleData.get(position).getPostid());
            json.setFriend_user_id(logedInUserid);
            json.setFlag(action_flag);
            CommonApicallModule.insertCoolSwagDebNerd(json, Accesstoken, getActivity());
        }
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
        json.setFriend_user_id(logedInUserid);
        CommonApicallModule.deleteCoolSwagDebNerd(json, Accesstoken, getActivity());
        allSampleData.get(position).setAction_flag(0);
        this.notifyMomentsAdapter();

        txtReact.setText("React");
        imagereact.setImageResource(R.drawable.ic_svg_cool_select);
    }

    private void notifyMomentsAdapter() {
        if (this.adapter != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 1000);
        }
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
        if (logedInUserid.equalsIgnoreCase(postUserid)) {
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
                    if (onPostDeleteListener != null) {
                        List<String> images = allSampleData.get(position).getPost_image_createdby_creator_url();
                        if (images != null) {
                            for (String imageUrl : images) {
                                onPostDeleteListener.onDelete(imageUrl);
                            }
                        }
                    }
                    allSampleData.remove(position);
                    adapter.notifyDataSetChanged();
                    d.dismiss();
                }
            });
            d.show();
        } else {
            final Dialog onLongPressdialog = new Dialog(getActivity());
            onLongPressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            onLongPressdialog.setCanceledOnTouchOutside(true);
            onLongPressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            onLongPressdialog.setContentView(R.layout.privacy_dialog);
            //onLongPressdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
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
            Utils.loadProfilePic(getContext(), profile_image, allSampleData.get(position).getPost_creator_profilepic());
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
                        BroadcastObject.put("user_id", logedInUserid);
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
        mWorker.execute(Url, jsonObject.toString(), RequestConstants.POST_REQUEST,
                RequestConstants.HEADER_NO, code);
    }

    private void reloadData() {
        if (FileDataSender.POST_TO != FileDataSender.POST_TO_MOMENTS) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TagteenApplication.getInstance().setConnectivityListener(MomentsFeed.this);
                try {
                    if (videoDataSender != null && videoDataSender.isCall()) {
                        SharedPreferenceSingleton.getInstance()
                                .writeBoolPreference(Constants.IS_MOMENTS_UPLOAD_IN_PROGRESS, true);
                        videoDataSender.setIsCall(false);
                        //                        layoutProgress.setVisibility(View.VISIBLE);
                        //                        uploadProgress.setProgress(0);
                        //                        new VideoUploadMoment(getActivity(), layoutProgress, uploadProgress, textProgress, MomentsFeed.this);

                        Intent serviceIntent = new Intent(getActivity(), UploadService.class);
                        serviceIntent.putExtra(POST_TYPE, ImageTeenFeedImageUpload);
                        serviceIntent.putExtra("PostType", "Moment");
                        ContextCompat.startForegroundService(getActivity(), serviceIntent);
                    } else if (videoDataSender_selfy != null && videoDataSender_selfy.isCall()) {
                        // add clippie
                        videoDataSender_selfy.setIsCall(false);
                        layoutProgress.setVisibility(View.VISIBLE);
                        uploadProgress.setProgress(0);
                        new VideoUploadMoment_selfy(
                                getActivity(), layoutProgress, uploadProgress, textProgress, MomentsFeed.this);
                    } else if (FileDataSender.HAS_FILE_TO_UPLOAD) {
                        FileDataSender.HAS_FILE_TO_UPLOAD = false;
                        layoutProgress.setVisibility(View.VISIBLE);
                        uploadProgress.setProgress(0);
                        new FileUploadHelper(
                                getActivity(),
                                layoutProgress,
                                uploadProgress,
                                textProgress,
                                Constants.POST_TYPE_MOMENTS_INT,
                                new OnFileUploadListener() {
                                    @Override
                                    public void OnFileUploaded() {
                                        uploadProgress.setProgress(0);
                                        textProgress.setText("100 %");
                                        layoutProgress.setVisibility(View.GONE);
                                        FileDataSender.clear();
                                        pullToRefresh();
                                    }

                                    @Override
                                    public void onFileUploadFailed() {
                                        layoutProgress.setVisibility(View.GONE);
                                        FileDataSender.clear();
                                    }
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra("from").equalsIgnoreCase("Moments") && intent.getStringExtra(
                    "result").equalsIgnoreCase(ProgressDone)) {
                pullToRefresh();
            }
        }
    };

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mMessageReceiver, new IntentFilter("my-event"));
        if (getActivity() != null) {
            this.reloadData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK && data.getData() != null) {
                // successfully captured the image
                // display it in image view
                Intent it = new Intent(getActivity(), SelfiCameraPreview.class);
                SharedPreferenceSingleton.getInstance().init(getActivity());
                SharedPreferenceSingleton.getInstance().writeStringPreference(
                        ApplicationConstants.CAPTURED_IMAGE_PATH, data.getData().getPath());
                startActivity(it);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Utils.showShortToast(getActivity(), getString(R.string.user_cancelled_image_capture));
            } else {
                Utils.showShortToast(getActivity(), getString(R.string.image_capture_failed));
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                //previewVideo();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Utils.showShortToast(getActivity(), getString(R.string.user_cancelled_video_capture));
            } else {
                Utils.showShortToast(getActivity(), getString(R.string.video_capture_failed));
            }
        } else if (requestCode == 2) {
            if (resultCode == getActivity().RESULT_OK && data != null) {
                this.pullToRefresh();
            }
        } else if (requestCode == REQUEST_DELETE) {

        } else if (requestCode == Constants.COMMENT_REQUEST_CODE) {
            if (this.adapter != null && data != null &&
                    data.getSerializableExtra(Constants.SELECTED_MODEL) != null) {
                SectionDataModel model =
                        (SectionDataModel) data.getSerializableExtra(Constants.SELECTED_MODEL);
                this.adapter.refreshSelectedModel(model);
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        try {
            if (RequestConstants.REQUEST_REPORT_POST == REQUEST_NUMBER) {
                // Toast.makeText(getActivity(), "Your post has been successfully submited.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Your Post has been Successfully Deleted.", Toast.LENGTH_SHORT).show();
            }
            if (RequestConstants.REQUEST_DELETE_POST == REQUEST_NUMBER) {
                //Toast.makeText(getActivity(), "Your post has been successfully submited.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Your Post has been Successfully Deleted.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllNewUsersList() {
        if (this.usersList == null) {
            this.usersList = new ArrayList<UserInfoBean>();
        }

        Call<FriendSeach> call = methods.getLatestVerifiedUsers(30);

        API_Call_Retrofit.methodCalled(call.request().url().toString());
        call.enqueue(new Callback<FriendSeach>() {
            @Override
            public void onResponse(@NotNull Call<FriendSeach> call, @NotNull Response<FriendSeach> response) {
                int statuscode = response.code();
                usersList.clear();
                if (statuscode == 200) {
                    FriendSeach responseModel = response.body();
                    ArrayList<FriendSeach.UserInfo> responseData = null;
                    if (responseModel != null) {
                        responseData = responseModel.getUserInfos();
                        if (responseData != null && !responseData.isEmpty()) {
                            for (FriendSeach.UserInfo userData : responseData) {
                                String userName = userData.getFirst_name() + " " + userData.getLast_name();
                                String userId = userData.get_id();
                                UserInfoBean user = new UserInfoBean(userId, userName);
                                user.setProfileUrl(userData.getProfile_url());
                                usersList.add(user);
                            }
                            usersAdapter = new LatestUsersAdapter(getContext(), usersList);
                            recyclerViewUsersList.setAdapter(usersAdapter);
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            layoutMain.setVisibility(View.VISIBLE);
                        }
                    }

                }
            }

            @Override
            public void onFailure(@NotNull Call<FriendSeach> call, @NotNull Throwable t) {

            }
        });
    }
}
