package in.tagteen.tagteen;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import im.ene.toro.widget.Container;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.InsertCoolModel;
import in.tagteen.tagteen.Model.LikeJsonInputModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.base.AppBaseActivity;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.networkEngine.ModifiedAsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.VideoPreLoadingService;
import in.tagteen.tagteen.utils.reyclerutils.RecyclerViewUtils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_DELETE_POST;
import static in.tagteen.tagteen.configurations.ServerConnector.REQUEST_REPORT_POST;

/*import im.ene.toro.widget.Container;*/

public class VideoPartDetail extends AppBaseActivity implements MultiplePermissionsListener, AsyncResponse {
  private static final String PACKAGE_SCHEME = "package";
  private boolean apiCallBlocker = false;
  private String userid;
  private int pagenumber = 1;
  private int pageLimit = 10;
  private int forYouCategoryId = 0;
  private int forYouPageNumber = 1;
  private String post_vid_id = "";

  private LinearLayout layoutProgress;
  private Container rv_featureList;
  //private AAH_CustomRecyclerView recyclerView;
  //private ShowroomRelatedVideosAdapter adapter;
  private BasicAdapter adapter;

  private GetPostResponseModel.PostDetails selectedPostData;
  private ArrayList<GetPostResponseModel.PostDetails> postlist;
  private List<String> loadedPostIds;
  private MaterialDialog mPermissionDeniedDialog;
  private LinearLayoutManager layoutManager;
  private GetPostResponseModel.PostDetails shareData;

  private TabLayout tabLayout;
  private RelativeLayout layoutPostDetails;
  private RelativeLayout layoutProfileInfo;
  private ImageView imgComment;
  private ImageView imgShare;
  private ImageView imgMoreOptions;
  private ImageView imgProfilePic;
  private RecyclerView recyclerCategories;
  private TextView lblViewsCount;
  private TextView lblSupport;
  private TextView lblProfileDot;
  private TextView lblCommentsCount;
  private TextView lblPostDescription;
  private TextView lblUserName;
  private TextView lblUserTag;

  private TextView lblTabForYou;
  private TextView lblTabLatest;
  private TextView lblTabFollowing;

  private LinearLayout layoutURock;
  private ImageView imgURock;
  private TextView lblURocksCount;

  private GetPostResponseModel.PostDetails thePost;
  private VideoCategoriesAdapter categoriesAdapter;
  private ArrayList<CategoryBean> categoryList = new ArrayList<>();
  private int selectedCategoryIndex;
  private int selectedCategoryId;

  private static final String FOR_YOU = "For You";
  private static final String CHALLENGES = "Challenges";
  private static final String LATEST = "Latest";
  private static final String FOLLOWING = "Supporting";
  private static final int FOR_YOU_INDEX = 101;
  private static final int CHALLENGER_INDEX = 102;
  private static final int LATEST_INDEX = 103;
  private static final int FOLLOWING_INDEX = 104;

  private int unSeletedTabTextSize = 16;
  private int selectedTabTextSize = 18;

  private static final int FLAG_COMMENT = 0;

  private static final int TAB_INDEX_FOR_YOU = 0;
  private static final int TAB_INDEX_LATEST = 1;
  private static final int TAB_INDEX_FOLLOWING = 2;
  private static final int LANDING_TAB = TAB_INDEX_FOR_YOU;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    //View view = inflater.inflate(R.layout.video_details_activity_main, container, false);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.video_details_activity_main);

    this.loadedPostIds = new ArrayList<>();
    this.initWidgets();
    this.bindEvents();

    this.userid =
        SharedPreferenceSingleton.getInstance()
            .getStringPreference(this, RegistrationConstants.USER_ID);

    if (getIntent().getExtras() != null) {
      this.post_vid_id = getIntent().getStringExtra("post_vid_id");
      this.forYouCategoryId = getIntent().getIntExtra("category_id", 0);
      this.selectedCategoryId = this.forYouCategoryId;

      if (getIntent().getSerializableExtra(Constants.SHOWROOM_POST_DATA) != null) {
        if (getIntent().getSerializableExtra(
                Constants.SHOWROOM_POST_DATA) instanceof GetPostResponseModel.PostDetails) {
          this.selectedPostData =
                  (GetPostResponseModel.PostDetails) getIntent().getSerializableExtra(Constants.SHOWROOM_POST_DATA);
        }
      }
    }

    this.layoutManager = new LinearLayoutManager(this);
    this.rv_featureList.setLayoutManager(this.layoutManager);

    this.postlist = new ArrayList<>();
    if (this.selectedPostData != null) {
      this.layoutProgress.setVisibility(View.GONE);
      this.postlist.add(this.selectedPostData);
    }

    this.cachePostList();
    this.initDialog();
    this.initAdapter();

    if (DataCache.getInstance().isDoLoadMore()) {
      this.loadData();
    }
  }

  private void cachePostList() {
      if (DataCache.getInstance().getPostlist() == null) {
          return;
      }

      this.forYouPageNumber = DataCache.getInstance().getPageLoaded() + 1;
      for (GetPostResponseModel.PostDetails data : DataCache.getInstance().getPostlist()) {
        if (data == null) {
          continue;
        }
        if (this.selectedPostData == null ||
                !this.selectedPostData.getId().equals(data.getId())) {
          this.loadedPostIds.add(data.getId());
          this.postlist.add(data);
        }
      }
  }

  private void initAdapter() {
    this.adapter = new BasicAdapter(this, this.postlist,
        this.rv_featureList,
        new BasicAdapter.OnShareItemCallback() {
          @Override public void onClickShareButton(GetPostResponseModel.PostDetails data) {
            //downloadVideo(data);
            shareData = data;
            checkPermissions();
          }
        });
    this.adapter.setOnFullScreenExpandListener(new BasicAdapter.OnFullScreenExpandListener() {
      @Override
      public void moveToFullScreenMode(String videoUrl, long resumePosition) {
        moveToFullScreen(videoUrl, resumePosition);
      }
    });
    this.adapter.setOnBindItemListener(new BasicAdapter.OnBindItemListener() {
      @Override
      public void onBindItem(GetPostResponseModel.PostDetails data) {
        bindItem(data);
      }
    });
    this.adapter.setOnItemClickedListener(new BasicAdapter.OnItemClickedListener() {
      @Override
      public void onItemClicked(boolean isControlsVisible) {
        toggleBottomDetails(isControlsVisible);
      }
    });
    this.rv_featureList.addItemDecoration(
        RecyclerViewUtils.newVerticalSpacingItemDecoration(Objects.requireNonNull(this),
            R.dimen.spacing_16,
            false, false));
    this.rv_featureList.addItemDecoration(
        RecyclerViewUtils.newDividerItemDecoration(this, R.color.gray2));
    //PagerSnapHelper linearSnapHelper = new LinearSnapHelperOneByOne();
    //linearSnapHelper.attachToRecyclerView(this.rv_featureList);
    //SnapHelper snapHelper = new GravitySnapHelper(Gravity.TOP);
    //snapHelper.attachToRecyclerView(this.rv_featureList);
    this.rv_featureList.setAdapter(adapter);
  }

  private void initWidgets() {
    this.layoutProgress = findViewById(R.id.layoutProgress);
    this.rv_featureList = findViewById(R.id.player_container);
    PagerSnapHelper snapHelper = new PagerSnapHelper();
    snapHelper.attachToRecyclerView(this.rv_featureList);

    this.tabLayout = findViewById(R.id.tabs);
    this.createTabIcons();
    this.tabLayout.getTabAt(LANDING_TAB).select();
    this.selectedCategoryId = FOR_YOU_INDEX;
    this.selectedCategoryIndex = FOR_YOU_INDEX;

    this.layoutPostDetails = findViewById(R.id.layoutPostDetails);
    this.layoutProfileInfo = findViewById(R.id.layoutProfileInfo);
    this.lblSupport = findViewById(R.id.lblSupport);
    this.lblProfileDot = findViewById(R.id.lblProfileDot);
    this.imgComment = findViewById(R.id.imgComment);
    this.imgShare = findViewById(R.id.imgShare);
    this.imgMoreOptions = findViewById(R.id.imgMoreOptions);
    this.imgProfilePic = findViewById(R.id.imgProfilePic);
    this.lblViewsCount = findViewById(R.id.lblViewsCount);
    this.lblCommentsCount = findViewById(R.id.lblCommentsCount);
    this.lblPostDescription = findViewById(R.id.lblPostDescription);
    this.lblUserName = findViewById(R.id.lblUserName);
    this.lblUserTag = findViewById(R.id.lblUserTag);

    this.layoutURock = findViewById(R.id.layoutURock);
    this.imgURock = findViewById(R.id.imgURock);
    this.lblURocksCount = findViewById(R.id.lblURocksCount);

    this.recyclerCategories = findViewById(R.id.recyclerCategories);
    this.recyclerCategories.setHasFixedSize(true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false);
    this.recyclerCategories.setLayoutManager(layoutManager);

    this.getAllCategories();
  }

  private void bindEvents() {
    this.layoutProfileInfo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Utils.gotoProfile(VideoPartDetail.this, thePost.getPostCreatorId());
      }
    });
    this.lblSupport.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        supportUser();
      }
    });
    this.lblCommentsCount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        moveToComments(FLAG_COMMENT, 0);
      }
    });
    this.imgComment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        moveToComments(FLAG_COMMENT, 1);
      }
    });
    this.imgShare.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sharePost();
      }
    });
    this.imgMoreOptions.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showPopupMenu(v);
      }
    });
    this.layoutURock.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toggleLike();
      }
    });
    this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        resetTabTextSize();

        int position = tab.getPosition();
        switch (position) {
          case TAB_INDEX_FOR_YOU:
            selectedCategoryId = FOR_YOU_INDEX;
            selectedCategoryIndex = FOR_YOU_INDEX;
            categoriesAdapter.notifyDataSetChanged();
            lblTabForYou.setTextSize(selectedTabTextSize);
            lblTabForYou.setTypeface(null, Typeface.BOLD);
            lblTabForYou.setTextColor(getResources().getColor(R.color.colorPrimary));
            loadVideos();
            break;

          case TAB_INDEX_LATEST:
            selectedCategoryId = LATEST_INDEX;
            selectedCategoryIndex = LATEST_INDEX;
            categoriesAdapter.notifyDataSetChanged();
            lblTabLatest.setTextSize(selectedTabTextSize);
            lblTabLatest.setTypeface(null, Typeface.BOLD);
            lblTabLatest.setTextColor(getResources().getColor(R.color.colorPrimary));
            loadVideos();
            break;

          case TAB_INDEX_FOLLOWING:
            selectedCategoryId = FOLLOWING_INDEX;
            selectedCategoryIndex = FOLLOWING_INDEX;
            categoriesAdapter.notifyDataSetChanged();
            lblTabFollowing.setTextSize(selectedTabTextSize);
            lblTabFollowing.setTypeface(null, Typeface.BOLD);
            lblTabFollowing.setTextColor(getResources().getColor(R.color.colorPrimary));
            loadVideos();
            break;
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });
  }

  private void createTabIcons() {
    this.tabLayout.addTab(this.tabLayout.newTab());
    this.tabLayout.addTab(this.tabLayout.newTab());
    this.tabLayout.addTab(this.tabLayout.newTab());

    this.lblTabForYou =
            (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
    this.lblTabForYou.setText(FOR_YOU);
    this.lblTabForYou.setTextSize(this.selectedTabTextSize);
    this.lblTabForYou.setTypeface(null, Typeface.BOLD);
    this.tabLayout.getTabAt(TAB_INDEX_FOR_YOU).setCustomView(this.lblTabForYou);

    this.lblTabLatest =
            (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
    this.lblTabLatest.setText(LATEST);
    this.lblTabLatest.setTextColor(getResources().getColor(R.color.white));
    this.lblTabLatest.setTextSize(this.unSeletedTabTextSize);
    this.tabLayout.getTabAt(TAB_INDEX_LATEST).setCustomView(this.lblTabLatest);

    this.lblTabFollowing =
            (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
    this.lblTabFollowing.setText(FOLLOWING);
    this.lblTabFollowing.setTextColor(getResources().getColor(R.color.white));
    this.lblTabFollowing.setTextSize(this.unSeletedTabTextSize);
    this.tabLayout.getTabAt(TAB_INDEX_FOLLOWING).setCustomView(this.lblTabFollowing);
  }

  private void resetTabTextSize() {
    this.lblTabForYou.setTextSize(this.unSeletedTabTextSize);
    this.lblTabForYou.setTypeface(null, Typeface.NORMAL);
    this.lblTabForYou.setTextColor(getResources().getColor(R.color.white));
    this.lblTabLatest.setTextSize(this.unSeletedTabTextSize);
    this.lblTabLatest.setTypeface(null, Typeface.NORMAL);
    this.lblTabLatest.setTextColor(getResources().getColor(R.color.white));
    this.lblTabFollowing.setTextSize(this.unSeletedTabTextSize);
    this.lblTabFollowing.setTypeface(null, Typeface.NORMAL);
    this.lblTabFollowing.setTextColor(getResources().getColor(R.color.white));
  }

  private void setUserLike() {
    if (this.thePost.getUserLike()) {
      this.lblURocksCount.setTextColor(getResources().getColor(R.color.red_600));
      this.imgURock.setColorFilter(ContextCompat.getColor(this, R.color.red_600),
              android.graphics.PorterDuff.Mode.SRC_IN);
      this.layoutURock.setBackgroundTintList(
              getResources().getColorStateList(R.color.white));
    } else {
      this.lblURocksCount.setTextColor(getResources().getColor(R.color.white));
      this.imgURock.setColorFilter(ContextCompat.getColor(this, R.color.white),
              android.graphics.PorterDuff.Mode.SRC_IN);
      this.layoutURock.setBackgroundTintList(
              getResources().getColorStateList(R.color.red_600));
    }
  }

  private void toggleLike() {
    if (Utils.isGuestLogin()) {
      Utils.moveToRegistration(this);
    } else {
      if (this.thePost.getUserLike()) {
        this.thePost.setLikeCount(this.thePost.getLikeCount() -1);
        this.removeReaction();
      } else {
        this.thePost.setLikeCount(this.thePost.getLikeCount() + 1);
        this.likePost();
      }
      this.thePost.setUserLike(!this.thePost.getUserLike());
      this.lblURocksCount.setText("" + this.thePost.getLikeCount());
      this.setUserLike();
    }
  }

  private void removeReaction() {
    if (Utils.isGuestLogin()) {
      Utils.moveToRegistration(this);
    } else {
      if (this.thePost == null) {
        return;
      }
      InsertCoolModel json = new InsertCoolModel();
      json.setFlag(5);
      json.setPost_id(this.thePost.getId());
      json.setFriend_user_id(this.userid);
      String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
      CommonApicallModule.deleteCoolSwagDebNerd(json, token, this);
    }
  }

  private void likePost() {
    if (Utils.isGuestLogin()) {
      Utils.moveToRegistration(this);
    } else {
      if (this.thePost == null) {
        return;
      }
      LikeJsonInputModel likeJsonInputModel = new LikeJsonInputModel();
      String token = SharedPreferenceSingleton.getInstance()
              .getStringPreference(this, RegistrationConstants.TOKEN);
      likeJsonInputModel.setUser_id(this.userid);
      likeJsonInputModel.setPost_id(this.thePost.getId());
      // remove reaction before inserting
      //removeReaction();
      CommonApicallModule.callApiForLike(likeJsonInputModel, token, this);
    }
  }

  private void toggleBottomDetails(boolean isControlsVisible) {
    if (isControlsVisible) {
      this.layoutPostDetails.setVisibility(View.GONE);
    } else {
      this.layoutPostDetails.setVisibility(View.VISIBLE);
    }
  }

  private void sharePost() {
    shareData = thePost;
    //this.downloadVideoAndShare(this.thePost);
    checkPermissions();
  }

  private void moveToComments(int flag, int showKeypad) {
    if (Utils.isGuestLogin()) {
      Utils.moveToRegistration(this);
    } else {
      Bundle bundle = new Bundle();
      Intent in = new Intent(this, CommentLikeActivity_new.class);
      bundle.putInt("comment_select_flag", flag);
      bundle.putInt("keypadshow", showKeypad);
      bundle.putString("postid", this.thePost.getId());
      bundle.putString("type", "showroom");
      bundle.putString("react_type", "");
      bundle.putInt(Constants.COMMENTS_COUNT, this.thePost.getConversationCount());
      bundle.putBoolean(Constants.BLOCK_REACTS, true);
      bundle.putInt(Constants.REACTS_COUNT, this.thePost.getLikeCount());
      in.putExtras(bundle);
      startActivity(in);
    }
  }

  private void showPopupMenu(View view) {
    if (Utils.isGuestLogin()) {
      Utils.moveToRegistration(this);
    } else {
      String postUserId = this.thePost.getPostCreatorId();
      PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
      MenuInflater inflater = popupMenu.getMenuInflater();
      Menu menu = popupMenu.getMenu();

      if (this.userid.equalsIgnoreCase(postUserId)) {
        inflater.inflate(R.menu.popup_menu_delete, menu);
      } else {
        inflater.inflate(R.menu.popup_menu_report, menu);
        //inflater.inflate(R.menu.popup_menu_delete, menu);
      }
      popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
          switch (menuItem.getItemId()) {
            case R.id.action_delete:
              deletePost();
              break;

            case R.id.action_report:
              reportPost();
              break;
          }
          return false;
        }
      });
      popupMenu.show();
    }
  }

  private void deletePost() {
    final Dialog d = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    d.setContentView(R.layout.confirmationpopup);
    final TextView msg = (TextView) d.findViewById(R.id.message);
    TextView name = (TextView) d.findViewById(R.id.yourname);
    TextView ok = (TextView) d.findViewById(R.id.confirm_ok_btn);
    TextView continueorder = (TextView) d.findViewById(R.id.confirm);
    TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
    name.setVisibility(View.GONE);
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
        d.dismiss();
        if (userid.equalsIgnoreCase(thePost.getPostCreatorId())) {
          JSONObject BroadcastObject = new JSONObject();
          try {
            BroadcastObject.put("post_id", thePost.getId());
          } catch (JSONException e) {
            e.printStackTrace();
          }
          String Url = REQUEST_DELETE_POST;
          reportDelete("LoginUser", Url, BroadcastObject, RequestConstants.REQUEST_DELETE_POST);
        } else {
          JSONObject BroadcastObject = new JSONObject();
          try {
            BroadcastObject.put("post_id", thePost.getId());
            BroadcastObject.put("user_id", userid);
            BroadcastObject.put("message", "-");
          } catch (JSONException e) {
            e.printStackTrace();
          }
          String Url = REQUEST_REPORT_POST;
          reportDelete("OtherUser", Url, BroadcastObject, RequestConstants.REQUEST_REPORT_POST);
        }
      }
    });
    d.show();
  }

  private void reportPost() {
    final Dialog onLongPressdialog = new Dialog(this);
    onLongPressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    onLongPressdialog.setContentView(R.layout.privacy_dialog);
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

    Utils.loadImageUsingGlideCenterCrop(
            this, profile_image, this.thePost.getProfile_url());

    text.setText(this.thePost.getFirst_name() + " " + this.thePost.getLast_name());
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
        } else if (img2.getVisibility() == View.VISIBLE) {
          msg = "Violent";
        } else if (img3.getVisibility() == View.VISIBLE) {
          msg = "Inappropriate";
        } else if (img4.getVisibility() == View.VISIBLE) {
          msg = "Self Harm";
        } else if (img5.getVisibility() == View.VISIBLE) {
          msg = "Offensive";
        }
        JSONObject BroadcastObject = new JSONObject();
        try {
          BroadcastObject.put("post_id", thePost.getId());
          BroadcastObject.put("user_id", userid);
          BroadcastObject.put("message", msg);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        String Url = REQUEST_REPORT_POST;
        reportDelete("OtherUser", Url, BroadcastObject, RequestConstants.REQUEST_REPORT_POST);
        onLongPressdialog.dismiss();
      }
    });

    onLongPressdialog.show();
  }

  private void reportDelete(String Tag, String Url, JSONObject jsonObject, String code) {
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;
    mWorker.delegate = this;
    mWorker.execute(Url, jsonObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_NO, code);
  }

  private void checkPermissions() {
    Dexter.withContext(this)
        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        .withListener(this).check();
  }

  private void loadData() {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        getRelatedVideos();
      }
    }, 1000);
  }

  public void setOnCallbackListener(OnCallbackListener onCallbackListener) {
    if (this.adapter != null) {
      this.adapter.setOnCallbackListener(onCallbackListener);
    }
  }

  private void getRelatedVideos() {
    String token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
    Call<GetPostResponseModel> call = null;
    if (this.selectedCategoryId == LATEST_INDEX) {
      call = methods.getallshowroomvid(this.userid, this.pagenumber, this.pageLimit, 3, token);
    } else if (this.selectedCategoryId == FOR_YOU_INDEX) {
      call = methods.getAllShowroomPostRanked(this.userid, this.forYouPageNumber, this.pageLimit, 3, token);
      DataCache.getInstance().setPageLoaded(forYouPageNumber);
      forYouPageNumber++;
    } else if (this.selectedCategoryId == FOLLOWING_INDEX) {
      call = methods.getAllShowroomFollowingPosts(this.userid, this.pagenumber, this.pageLimit, 3, token);
    } else {
      call = methods.getRelatedVideoPost(this.selectedCategoryId, this.pagenumber, this.pageLimit, this.userid, token);
    }
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(Call<GetPostResponseModel> call,
          Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        apiCallBlocker = true;
        layoutProgress.setVisibility(View.GONE);
        if (statuscode == 200) {
          pagenumber++;
          GetPostResponseModel responseModel = response.body();
          List<GetPostResponseModel.PostDetails> dataList = responseModel.getData();
          if (dataList == null || dataList.isEmpty()) {
            apiCallBlocker = false;
          }
          if (dataList != null) {
            ArrayList<String> videoUrls = new ArrayList<>();
            int itemCount = 0;
            final int addedPosition = postlist.size();
            for (int i = 0; i < dataList.size(); i++) {
              GetPostResponseModel.PostDetails data = dataList.get(i);
              loadedPostIds.add(data.getId());
              if (data.getId().equalsIgnoreCase(post_vid_id) == false) {
                String videoUrl = UrlUtils.getUpdatedVideoUrl(data.getVideo().getUrl());
                videoUrls.add(videoUrl);
                postlist.add(data);
                itemCount++;
              } else if (selectedPostData == null) {
                postlist.add(0, data);
                itemCount++;
              }
            }

            if (itemCount > 0) {
              if (selectedCategoryId == FOR_YOU_INDEX) {
                DataCache.getInstance().addPosts(dataList);
              }
              //startPreLoadingService(videoUrls);
              if (adapter != null) {
                adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                  @Override
                  public void onLoadMore() {
                    if (apiCallBlocker) {
                      apiCallBlocker = false;
                      pagenumber++;
                      getRelatedVideos();
                    }
                  }
                });
                adapter.notifyItemRangeInserted(addedPosition, itemCount);
              }
            }
            adapter.setLoaded();
          }
        }
        if (statuscode == 401) {

        }
      }

      @Override
      public void onFailure(Call<GetPostResponseModel> call, Throwable t) {
      }
    });
  }

  private void startPreLoadingService(ArrayList<String> videoUrls) {
    if (this == null) {
      return;
    }
    Intent serviceIntent = new Intent(this, VideoPreLoadingService.class);
    serviceIntent.putStringArrayListExtra(Constants.VIDEOS_LIST, videoUrls);
    VideoPreLoadingService.enqueueWork(this, serviceIntent);
  }

  private void getAllCategories() {
    if (this == null) {
      return;
    }
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    mWorker.execute(
            ServerConnector.REQUEST_GET_ALL_CATEGORIES,
            BroadcastObject.toString(),
            RequestConstants.GET_REQUEST,
            RequestConstants.HEADER_YES,
            RequestConstants.REQUEST_GET_CATEGORIES);
  }

  private void bindItem(GetPostResponseModel.PostDetails data) {
    if (data == null) {
      return;
    }
    this.layoutPostDetails.setVisibility(View.VISIBLE);

    this.thePost = data;

    // reset supporting
    if (data.isMyFan()) {
      this.lblProfileDot.setVisibility(View.GONE);
      this.lblSupport.setVisibility(View.GONE);
    } else {
      this.lblProfileDot.setVisibility(View.VISIBLE);
      this.lblSupport.setVisibility(View.VISIBLE);
    }
    // set user like
    this.setUserLike();

    // user info
    this.lblUserName.setText(data.getFirst_name() + " " + data.getLast_name());
    this.lblUserTag.setText(data.getTagged_number());

    // profile pic
    Utils.loadProfilePic(this, this.imgProfilePic, data.getProfile_url());

    // description
    if (data.getContent() == null || data.getContent().trim().length() == 0) {
      this.lblPostDescription.setVisibility(View.GONE);
    } else {
      this.lblPostDescription.setVisibility(View.VISIBLE);
      this.lblPostDescription.setText(data.getContent());
      //Utils.makeTextViewResizable(this.feedDesc, 3, "More", true);
    }

    // U Rock count
    this.lblURocksCount.setText("" + data.getLikeCount());

    this.lblViewsCount.setText(data.getView_count() + " Views");
    /*if (data.getView_count() == 1) {
      viewsAndComments += data.getView_count() + " View . ";
    } else if (data.getView_count() > 1) {
      viewsAndComments += data.getView_count() + " Views . ";
    }*/

    this.lblCommentsCount.setText(data.getConversationCount() + " Comments");
    /*if (data.getConversationCount() == 1) {
      viewsAndComments += data.getConversationCount() + " Comment";
    } else if (data.getConversationCount() > 1) {
      viewsAndComments += data.getConversationCount() + " Comments";
    }*/
  }

  private void moveToFullScreen(String videoUrl, long resumePosition) {
    Intent intent = new Intent(this, FullscreenVideoActivity.class);
    intent.putExtra(Constants.VIDEO_CURRENT_POSITION, resumePosition);
    intent.putExtra(Constants.VIDEO_URL, videoUrl);
    overridePendingTransition(0, 0);
    startActivityForResult(intent, Constants.VIDEO_FULLSCREEN_CODE);
  }

  public void resumeVideoPosition(long resumePosition) {
    if (this.adapter != null) {
      this.adapter.resumeVideoPosition(resumePosition);
    }
  }

  private void initDialog() {
    mPermissionDeniedDialog =
        new MaterialDialog.Builder(Objects.requireNonNull(this)).title(
            "Permission Required")
            .positiveText("Go To Settings")
            .negativeText(R.string.general_label_cancel)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override
              public void onClick(@NonNull MaterialDialog dialog,
                  @NonNull DialogAction which) {
                //should open settings page to enable permission
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(PACKAGE_SCHEME, getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
              }
            })
            .build();
  }

  @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
    if (report.areAllPermissionsGranted() && shareData != null) {
      downloadVideoAndShare(shareData);
      //downloadUsingFetch(shareData);
    } else if (report.isAnyPermissionPermanentlyDenied()) {
      mPermissionDeniedDialog.show();
    } else {
      Toast.makeText(this, "Need Permissions ", Toast.LENGTH_SHORT).show();
    }
  }

  private void downloadVideoAndShare(GetPostResponseModel.PostDetails data) {
    if (data == null) {
      return;
    }
    String videoUrl = data.getVideo().getUrl();
    String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
    final String url = UrlUtils.getTrimmedVideoUrl(data.getVideo().getUrl());

    File appDir = new File(Utils.getAppFolderPath());
    final File outputFile = new File(appDir, fileName);
    if (outputFile.exists()) {
      this.shareVideo(outputFile, data.getId());
      return;
    }

    showProgressDialog("Downloading 0%");

    new AsyncTask<Void, String, String>() {
      @Override
      protected String doInBackground(Void... voids) {
        try {
          // https://stackoverflow.com/questions/11503791/progress-bar-completed-download-display-in-android
          URL u = new URL(url);
          URLConnection conn = u.openConnection();
          int contentLength = conn.getContentLength();
          FileOutputStream fout = new FileOutputStream(outputFile);
          InputStream in = conn.getInputStream();

          byte[] buffer = new byte[1024];
          int len1 = 0;
          long total = 0;

          while ((len1 = in.read(buffer)) > 0) {
            total += len1;
            publishProgress("" + (int)((total*100)/contentLength));
            fout.write(buffer, 0, len1);
          }
          fout.close();

          /*DataInputStream stream = new DataInputStream(u.openStream());
          byte[] buffer = new byte[contentLength];
          stream.readFully(buffer);
          stream.close();

          DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
          fos.write(buffer);
          fos.flush();
          fos.close();*/
        } catch (IOException e) {

        }
        return null;
      }

      @Override
      protected void onPostExecute(String result) {
        if (VideoPartDetail.this == null) {
          return;
        }
        dismissProgressDialog();
        shareVideo(outputFile, data.getId());
      }

      @Override
      protected void onProgressUpdate(String... values) {
        try {
          int progress = Integer.parseInt(values[0]);
          setMessage("Downloading " + progress + "%");
        } catch (Exception e) {

        }
      }
    }.execute();

    //String key = fileName.substring(0, fileName.lastIndexOf("."));
    /*TransferObserver mUploadMediaAwsObserver =
            AWSUtility.getTransferUtility().download(fileName, outputFile);
    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        dismissProgressDialog();
        if (state.equals(TransferState.COMPLETED)) {
          Uri uri = Uri.fromFile(outputFile);
          Utils.shareVideoAppDownloadLink(getApplicationContext(), uri, data.getId());
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int percent = (int) ((bytesCurrent * 100) / bytesTotal);
        setMessage("Sharing Video " + percent + "%");
      }

      @Override
      public void onError(int id, Exception ex) {
        dismissProgressDialog();
      }
    });*/
  }

  private void shareVideo(File videoFile, String postId) {
    Uri uri;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      uri = FileProvider.getUriForFile(
              this, BuildConfig.APPLICATION_ID + ".provider", videoFile);
    } else {
      uri = Uri.fromFile(videoFile);
    }
    Utils.shareVideoAppDownloadLink(this, uri, postId);
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list,
      PermissionToken token) {
    new AlertDialog.Builder(Objects.requireNonNull(this)).setTitle("Permission Required")
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            token.cancelPermissionRequest();
          }
        })
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            token.continuePermissionRequest();
          }
        })
        .setOnDismissListener(new DialogInterface.OnDismissListener() {
          @Override public void onDismiss(DialogInterface dialog) {
            token.cancelPermissionRequest();
          }
        })
        .show();
  }

  private void loadVideos() {
    this.pagenumber = 1;
    this.postlist.clear();
    this.loadedPostIds.clear();
    if (this.selectedCategoryId == CHALLENGER_INDEX) {
      return;
    }
    if (this.selectedCategoryId == FOR_YOU_INDEX) {
      this.selectedCategoryId = this.forYouCategoryId;
      this.postlist.add(this.selectedPostData);
    }
    if (DataCache.getInstance().isDoLoadMore() || this.selectedCategoryId != FOR_YOU_INDEX) {
      this.getRelatedVideos();
    }
    this.adapter.notifyDataSetChanged();
    if (this.selectedCategoryId == FOR_YOU_INDEX) {
      new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
          cachePostList();
          if (adapter != null && postlist != null) {
            adapter.notifyDataSetChanged();
          }
        }
      }, 300);
    }
  }

  @Override
  public void onRefresh() {

  }

  private void supportUser() {
    if (Utils.isGuestLogin()) {
      Utils.moveToRegistration(this);
    } else {
      if (!this.lblSupport.getText().toString().equalsIgnoreCase(getString(R.string.support))) {
        return;
      }
      thePost.setMyFan(true);
      ModifiedAsyncWorker mWorker = new ModifiedAsyncWorker(this);
      mWorker.delegate = this;
      mWorker.delegate = this;
      JSONObject BroadcastObject = new JSONObject();
      try {
        BroadcastObject.put("user_id", userid);
        BroadcastObject.put("friend_user_id", thePost.getPostCreatorId());
      } catch (Exception e) {
        e.printStackTrace();
      }
      mWorker.execute(ServerConnector.REQUEST_BE_A_FAN, BroadcastObject.toString(),
              RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES,
              RequestConstants.REQUEST_BE_A_FAN);
    }
  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    try {
      JSONObject data = new JSONObject(output);
      if (REQUEST_NUMBER.equalsIgnoreCase(RequestConstants.REQUEST_GET_CATEGORIES)) {
        if (data.getBoolean("success")) {
          this.initCategories();
          JSONArray dataArr = data.getJSONArray("data");
          for (int i = 0; i < dataArr.length(); i++) {
            JSONObject object = dataArr.getJSONObject(i);
            String categoryName = object.getString(Constants.CATEGORY_NAME_PARAM);
            int categoryId = object.getInt(Constants.CATEGORY_ID_PARAM);
            this.categoryList.add(new CategoryBean(categoryId, categoryName));
          }
          this.categoriesAdapter = new VideoCategoriesAdapter(this.categoryList);
          this.recyclerCategories.setAdapter(this.categoriesAdapter);
        }
      } else if (RequestConstants.REQUEST_REPORT_POST == REQUEST_NUMBER) {
        Utils.showShortToast(this,"Your post has been successfully submitted.");
      } else if (RequestConstants.REQUEST_DELETE_POST == REQUEST_NUMBER) {
        // TODO :  reload
        getFragmentManager().popBackStack();
      } else if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_UN_FAN)) {
        if (data.getBoolean("success")) {
          Utils.showToast(this, "" + data.get("message"));
        } else {
          Utils.showToast(this, "Something went wrong please try again");
        }
      } else if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_BE_A_FAN)) {
        if (data.getBoolean("success")) {
          Utils.showToast(this, "" + data.get("message"));
          this.lblProfileDot.setVisibility(View.GONE);
          this.lblSupport.setVisibility(View.GONE);
        } else {
          Utils.showToast(this, "Something went wrong please try again");
        }
      }
    } catch (Exception e) {
      // do nothing
    }
  }

  private void initCategories() {
    if (this.categoryList == null) {
      this.categoryList = new ArrayList<>();
    }
    this.categoryList.clear();
    //this.categoryList.add(new CategoryBean(FOR_YOU_INDEX, FOR_YOU));
    //this.categoryList.add(new CategoryBean(CHALLENGER_INDEX, CHALLENGES));
    //this.categoryList.add(new CategoryBean(LATEST_INDEX, LATEST));
  }

  class VideoCategoriesAdapter extends RecyclerView.Adapter<VideoCategoriesAdapter.ViewHolder> {
    private List<CategoryBean> categoryBeanList;

    public VideoCategoriesAdapter(List<CategoryBean> categoryBeanList) {
      this.categoryBeanList = categoryBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
      View view =
              LayoutInflater.from(VideoPartDetail.this).inflate(
                      R.layout.video_category_row, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoCategoriesAdapter.ViewHolder viewHolder, final int position) {
      CategoryBean categoryBean = this.categoryBeanList.get(position);
      viewHolder.lblCategory.setText(categoryBean.getCategoryName());
      viewHolder.lblCategory.setTextColor(getResources().getColor(R.color.white));
      if (CHALLENGES.equals(categoryBean.getCategoryName())) {
        viewHolder.lblCategory.setBackgroundTintList(
                getResources().getColorStateList(R.color.grey));
        return;
      }
      if (selectedCategoryIndex == position) {
        viewHolder.lblCategory.setBackgroundTintList(
                getResources().getColorStateList(R.color.colorPrimary));
      } else {
        viewHolder.lblCategory.setBackgroundTintList(
                getResources().getColorStateList(R.color.colorPrimaryAlpha));
      }

      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          CategoryBean categoryBean = categoryBeanList.get(position);
          if (CHALLENGES.equals(categoryBean.getCategoryName())) {
            return;
          }
          selectedCategoryIndex = position;
          selectedCategoryId = categoryBean.getId();
          notifyDataSetChanged();
          loadVideos();
        }
      });
    }

    @Override
    public int getItemCount() {
      return this.categoryBeanList != null ? this.categoryBeanList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      private TextView lblCategory;

      public ViewHolder(@NonNull View itemView) {
        super(itemView);

        this.lblCategory = itemView.findViewById(R.id.lblCategoryName);
      }
    }
  }
}
