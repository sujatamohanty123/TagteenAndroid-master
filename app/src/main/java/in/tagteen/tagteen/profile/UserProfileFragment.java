
package in.tagteen.tagteen.profile;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import in.tagteen.tagteen.ConnectivityReceiver;
import in.tagteen.tagteen.FanList_new;
import in.tagteen.tagteen.Fragments.FriendActivity;
import in.tagteen.tagteen.Interfaces.OnProfilePicEditListener;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.RewardsActivity;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.ModifiedAsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.CircleTransform;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class UserProfileFragment extends AppCompatActivity
    implements AsyncResponse, ConnectivityReceiver.ConnectivityReceiverListener {
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private FragmentPagerAdapter adapter;
  private String path;
  private String output = "";
  private ImageView imageUserPIc;
  private String login_usr_id, access_token;
  private TextView textUserTagno;
  private int[] mResources = {
      R.drawable.girl,
      R.drawable.pro1,
      R.drawable.pro2,
      R.drawable.pro3
  };
  private TextView no_internet;
  private LinearLayout buzz_main_container;

  private LinearLayout linear_txtSupporters, linear_txtSupportings, linear_edit, linear_Settings,
      linear_Support, linear_you_r_supporting,
      linear_tag_me, linear_tagged, linear_pending;
  private TextView txtSupporters, txtSupportings, txt_name;
  private ImageView image1, image2;
  private RelativeLayout relative;
  private ImageView placeholder;
  //private Context context;
  private LinearLayout layoutProgressProfilePic;

  private ImageView imgShareLink;
  private LinearLayout layoutShareLink;
  private ImageView imgShareLinkPic;
  private TextView lblShareLinkUsername;
  private TextView lblShareLinkTagno;
  private TextView lblShareLinkSupportCount;
  private TextView lblShareLinkText;
  private ImageView imgRewards;
  private ImageView imgUserBadge;
  private AppBarLayout mAppBarLayout;
  private CollapsingToolbarLayout mCollapsingToolbarLayout;

  private OnProfilePicEditListener onProfilePicEditListener;
  private ProfilePhotosFragment profilePhotosFragment;
  private ProfileVideoFragment profileVideoFragment;

  private int supportersCount;

  public UserProfileFragment() {

  }

  @Override
  public void onNetworkConnectionChanged(boolean isConnected) {
    showSnack(isConnected);
  }

  private interface CurrentFragment {
    @Nullable
    FragmentPagerFragment currentFragment();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_profile);

    this.buzz_main_container = findViewById(R.id.buzz_main_container);
    this.no_internet = findViewById(R.id.no_internet);
    mCollapsingToolbarLayout = findViewById(R.id.toolbar);
    mAppBarLayout = findViewById(R.id.appBarLayout);
    checkConnection();

    this.relative = findViewById(R.id.relative);
    this.placeholder = findViewById(R.id.placeholder);
    this.viewPager = findViewById(R.id.viewpager);
    this.imageUserPIc = findViewById(R.id.imageUserPIc);

    SharedPreferenceSingleton.getInstance().init(this);

    this.txt_name = findViewById(R.id.txt_name);
    this.textUserTagno = findViewById(R.id.textUserTagno);
    this.layoutProgressProfilePic = findViewById(R.id.layoutProgressProfilePic);

    this.image1 = findViewById(R.id.image1);
    this.image2 = findViewById(R.id.image2);
    this.linear_txtSupporters = findViewById(R.id.linear_txtSupporters);
    this.linear_txtSupportings = findViewById(R.id.linear_txtSupportings);
    this.linear_edit = findViewById(R.id.linear_edit);
    this.linear_Settings = findViewById(R.id.linear_Settings);
    this.linear_Support = findViewById(R.id.linear_Support);
    this.linear_you_r_supporting = findViewById(R.id.linear_you_r_supporting);
    this.linear_tag_me = findViewById(R.id.linear_tag_me);
    this.linear_tagged = findViewById(R.id.linear_tagged);
    this.linear_pending = findViewById(R.id.linear_pending);
    this.txtSupporters = findViewById(R.id.txtSupporters);
    this.txtSupportings = findViewById(R.id.txtSupportings);

    this.tabLayout = findViewById(R.id.tabs);
    /*this.tabLayout.addTab(tabLayout.newTab().setText("ABOUT"));
    this.tabLayout.addTab(tabLayout.newTab().setText("FRIENDS"));
    this.tabLayout.addTab(tabLayout.newTab().setText("PHOTOS"));*/
    this.tabLayout.addTab(tabLayout.newTab().setText("VIDEOS"));

    this.imgShareLink = findViewById(R.id.imgShareLink);
    this.imgShareLink.setVisibility(View.VISIBLE);
    this.layoutShareLink = findViewById(R.id.layoutShareLink);
    this.imgShareLinkPic = findViewById(R.id.imgShareLinkPic);
    this.lblShareLinkUsername = findViewById(R.id.lblShareLinkUsername);
    this.lblShareLinkTagno = findViewById(R.id.lblShareLinkTagNo);
    this.lblShareLinkSupportCount = findViewById(R.id.lblShareLinkSupportCount);
    this.lblShareLinkText = findViewById(R.id.lblShareLinkText);
    this.lblShareLinkText.setText(Utils.getShareLinkText());
    this.imgRewards = findViewById(R.id.imgRewards);
    this.imgRewards.setVisibility(View.VISIBLE);
    this.imgUserBadge = findViewById(R.id.imgRankBadge);

    access_token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    login_usr_id =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    //this.getProfile();

    linear_edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (output != null && !output.equalsIgnoreCase("")) {
          Intent intent = new Intent(UserProfileFragment.this, EditProfileActivity.class);
          intent.putExtra("output", output);
          startActivity(intent);
        }
      }
    });
    linear_Settings.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(UserProfileFragment.this, AppSettings.class);
        startActivity(intent);
      }
    });

    linear_txtSupportings.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String tittle = "I am supporting";
        gotoFanList("2", tittle);
      }
    });
    linear_txtSupporters.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String tittle = "My supporters";
        gotoFanList("1", tittle);
      }
    });
    this.imgShareLink.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        shareAppLink();
      }
    });
    this.txt_name.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        shareAppLink();
      }
    });
    this.textUserTagno.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        shareAppLink();
      }
    });
    this.imgRewards.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        moveToRewardsScreen();
      }
    });
    viewPager.setOffscreenPageLimit(1);
    adapter = new FragmentPagerAdapter(getSupportFragmentManager(), items(this, login_usr_id));
    viewPager.setAdapter(adapter);
    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {

        switch (tab.getPosition()) {
          case 0:
            viewPager.setCurrentItem(0);
            break;
          case 1:
            viewPager.setCurrentItem(1);
            break;
          case 2:
            viewPager.setCurrentItem(2);
            break;
          case 3:
            viewPager.setCurrentItem(3);
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
    //final CurrentFragment currentFragment = new CurrentFragmentImpl(viewPager, getChildFragmentManager());
    stopScroll();
  }

  public void setOnProfilePicEditListener(OnProfilePicEditListener onProfilePicEditListener) {
    this.onProfilePicEditListener = onProfilePicEditListener;
  }

  private void shareAppLink() {
    this.imgShareLinkPic.setImageDrawable(this.imageUserPIc.getDrawable());
    Utils.shareAppDownloadLink(this, this.layoutShareLink);
  }

  private void moveToRewardsScreen() {
    Intent intent = new Intent(this, RewardsActivity.class);
    intent.putExtra(Constants.SUPPORTERS_COUNT, this.supportersCount);
    int videosCount = 0;
    int uRocksCount = 0;
    if (this.profileVideoFragment != null) {
      videosCount = this.profileVideoFragment.getTotalVideosCount();
      uRocksCount = this.profileVideoFragment.getTotalURocksCount();
    }
    intent.putExtra(Constants.VIDEOS_COUNT, videosCount);
    intent.putExtra(Constants.U_ROCKS_COUNT, uRocksCount);
    startActivity(intent);
  }

  private void checkConnection() {
    boolean isConnected = ConnectivityReceiver.isConnected();
    showSnack(isConnected);
  }

  private void showSnack(boolean isConnected) {

    if (isConnected) {
      buzz_main_container.setVisibility(View.VISIBLE);
      no_internet.setVisibility(View.GONE);
    } else {
      buzz_main_container.setVisibility(View.GONE);
      no_internet.setVisibility(View.VISIBLE);
    }
  }

  private void gotoFanList(String flag, String tittle) {
    Intent in = new Intent(this, FanList_new.class);
    in.putExtra("flag", flag);
    in.putExtra("tittle", tittle);
    in.putExtra("user_id", login_usr_id);
    startActivity(in);
  }

  @Override
  public void onRefresh() {

  }

  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    if (this == null) {
      return;
    }
    try {
      if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_GET_PROFILE)) {

        this.output = output;
        JSONObject jsonObject = new JSONObject(output);
        JSONObject data = jsonObject.getJSONObject("data");
        String tagged_number = data.getString("tagged_number");
        path = data.getString("profile_url");
        String user_id = data.getString("user_id");
        String first_name = data.getString("first_name");
        String last_name = data.getString("last_name");
        String dob = data.optString("dob");
        String gender = "";//data.getString("gender");
        String currently_studying = "";// data.getString("currently_studying");
        String school_name = "";// data.getString("school_name");
        String pincode = "";//data.getString("pincode");
        boolean is_tagged_user = data.getBoolean("is_tagged_user");
        String city = "";//data.getString("city");
        String be_a_fan_count = data.getString("be_a_fan_count");
        String my_fan_count = data.getString("my_fan_count");
        //final boolean is_my_fan = data.getBoolean("is_my_fan");

        /*boolean email_privacy = data.getBoolean("email_privacy");
        boolean mobile_privacy = data.getBoolean("mobile_privacy");
        boolean myfriends_privacy = data.getBoolean("myfriends_privacy");
        boolean myphotos_privacy = data.getBoolean("myphotos_privacy");
        boolean education_privacy = data.getBoolean("education_privacy");
        boolean dob_privacy = data.getBoolean("dob_privacy");*/
        boolean isRankedUser = data.optBoolean("is_ranked_user");

        if (isRankedUser) {
          this.imgUserBadge.setVisibility(View.VISIBLE);
        } else {
          this.imgUserBadge.setVisibility(View.GONE);
        }
        placeholder.setVisibility(View.GONE);
        relative.setVisibility(View.VISIBLE);

        if (login_usr_id.equalsIgnoreCase(user_id)) {
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.PROFILE_URL, path);
          if (onProfilePicEditListener != null) {
            onProfilePicEditListener.onPhotoEdited();
          }
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.CURRENTLY_STUDYING, currently_studying);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.SCHOOL_NAME, school_name);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.PIN_CODE, pincode);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.GENDER, gender);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.FIRST_NAME, first_name);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.LAST_NAME, last_name);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.DATE_OF_BIRTHDAY, dob);
        }
        SharedPreferenceSingleton.getInstance()
            .writeBoolPreference(RegistrationConstants.IS_TAGGED_USER, is_tagged_user);
        String name = first_name + " " + last_name;
        txt_name.setText(name);
        lblShareLinkUsername.setText(name);
        supportersCount = Integer.valueOf(my_fan_count);
        txtSupporters.setText(my_fan_count + "");
        lblShareLinkSupportCount.setText(my_fan_count + "");
        txtSupportings.setText(be_a_fan_count + "");
        textUserTagno.setText(tagged_number);
        lblShareLinkTagno.setText(tagged_number);

        layoutProgressProfilePic.setVisibility(View.GONE);
        Glide.with(this)
            .load(UrlUtils.getUpdatedImageUrl(path, "large")) // add your image url
            .skipMemoryCache(true)
            .fitCenter()
            .transform(new CircleTransform(this)) // applying the image transformer
            .into(imageUserPIc);

        this.imageUserPIc.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Fragment fragment = new FullScreenProfilePicture();
            Bundle bundle = new Bundle();
            bundle.putString("ImageURL", path);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.main_content, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
          }
        });
      } else if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_UN_FAN)) {
        JSONObject dataJson = new JSONObject(output);

        if (dataJson.getBoolean("success")) {
          Utils.showToast(this, "" + dataJson.get("message"));
        } else {
          Utils.showToast(this, "something went wrong please try agian!");
        }
      } else if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_BE_A_FAN)) {
        JSONObject dataJson = new JSONObject(output);

        if (dataJson.getBoolean("success")) {
          Utils.showToast(this, "" + dataJson.get("message"));
        } else {
          Utils.showToast(this, "something went wrong please try agian!");
        }
      } else if (RequestConstants.SEND_FRIEND_REQUEST.equalsIgnoreCase(REQUEST_NUMBER)) {
        JSONObject dataJson = new JSONObject(output);

        Utils.showToast(this, dataJson.getString("message"));
      }
    } catch (Exception e) {
      Log.e("UserProfileFragment", output);
      e.printStackTrace();
    }
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        try {
          startScroll();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }, 500);
  }

  private void getProfile() {
    if (this.login_usr_id == null || this.login_usr_id.trim().length() == 0) {
      Utils.moveToRegistration(this);
      return;
    }

    ModifiedAsyncWorker mWorker = new ModifiedAsyncWorker(this);
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    mWorker.execute(ServerConnector.REQUEST_GET_USER_PROFILE + login_usr_id,
        BroadcastObject.toString(), RequestConstants.GET_REQUEST, RequestConstants.HEADER_YES,
        RequestConstants.REQUEST_GET_PROFILE);
  }

  @Override
  public void onResume() {
    super.onResume();
    TagteenApplication.getInstance().setConnectivityListener(this);
    this.getProfile();
    Utils.removeShareLinkTempImage();
  }

  public void notifyPostDeleted(String postImageUrl) {
    if (this.profilePhotosFragment != null) {
      this.profilePhotosFragment.notifyPostDeleted(postImageUrl);
    }
  }

  private List<FragmentPagerAdapter.Item> items(final Context context, final String login_usr_id) {
    final Resources r = context.getResources();
    final List<FragmentPagerAdapter.Item> items = new ArrayList<>(1);

    // about
    /*items.add(new FragmentPagerAdapter.Item(
        r.getString(R.string.tab),
        new FragmentPagerAdapter.Provider() {
          @Override
          public Fragment provide() {
            Fragment fragment = new AboutFragment();
            Bundle showroom_profile = new Bundle();
            showroom_profile.putString("user_id", login_usr_id);
            fragment.setArguments(showroom_profile);
            return fragment;
          }
        }
    ));*/

    // friends
    /*items.add(new FragmentPagerAdapter.Item(
        r.getString(R.string.tab),
        new FragmentPagerAdapter.Provider() {
          @Override
          public Fragment provide() {
            return new FriendActivity();
          }
        }
    ));*/

    // photos
    /*items.add(new FragmentPagerAdapter.Item(
        r.getString(R.string.tab),
        new FragmentPagerAdapter.Provider() {
          @Override
          public Fragment provide() {
            if (profilePhotosFragment == null) {
              profilePhotosFragment = new ProfilePhotosFragment();
            }
            Bundle follower_profile = new Bundle();
            follower_profile.putString("user_id", login_usr_id);
            profilePhotosFragment.setArguments(follower_profile);
            return profilePhotosFragment;
          }
        }
    ));*/

    // videos
    items.add(new FragmentPagerAdapter.Item(
        r.getString(R.string.tab),
        new FragmentPagerAdapter.Provider() {
          @Override
          public Fragment provide() {
            if (profileVideoFragment == null) {
              profileVideoFragment = new ProfileVideoFragment();
            }
            Bundle stat_profile = new Bundle();
            stat_profile.putString("user_id", login_usr_id);
            profileVideoFragment.setArguments(stat_profile);
            return profileVideoFragment;
          }
        }
    ));

    return items;
  }

  private static class CurrentFragmentImpl implements CurrentFragment {

    private final ViewPager mViewPager;
    private final FragmentManager mFragmentManager;
    private final FragmentPagerAdapter mAdapter;

    CurrentFragmentImpl(ViewPager pager, FragmentManager manager) {
      mViewPager = pager;
      mFragmentManager = manager;
      mAdapter = (FragmentPagerAdapter) pager.getAdapter();
    }

    @Override
    @Nullable
    public FragmentPagerFragment currentFragment() {
      final FragmentPagerFragment out;
      final int position = mViewPager.getCurrentItem();
      if (position < 0
          || position >= mAdapter.getCount()) {
        out = null;
      } else {
        final String tag = makeFragmentName(mViewPager.getId(), mAdapter.getItemId(position));
        final Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
          out = (FragmentPagerFragment) fragment;
        } else {
          // fragment is still not attached
          out = null;
        }
      }
      return out;
    }

    // this is really a bad thing from Google. One cannot possible obtain normally
    // an instance of a fragment that is attached. Bad, really bad
    private static String makeFragmentName(int viewId, long id) {
      return "android:switcher:" + viewId + ":" + id;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  private void stopScroll() {
    AppBarLayout.LayoutParams toolbarLayoutParams =
        (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
    toolbarLayoutParams.setScrollFlags(0);
    mCollapsingToolbarLayout.setLayoutParams(toolbarLayoutParams);

    CoordinatorLayout.LayoutParams appBarLayoutParams =
        (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
    appBarLayoutParams.setBehavior(null);
    mAppBarLayout.setLayoutParams(appBarLayoutParams);
  }

  private void startScroll() {
    AppBarLayout.LayoutParams toolbarLayoutParams =
        (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
    toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
    mCollapsingToolbarLayout.setLayoutParams(toolbarLayoutParams);

    CoordinatorLayout.LayoutParams appBarLayoutParams =
        (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
    appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
    mAppBarLayout.setLayoutParams(appBarLayoutParams);
  }
}
