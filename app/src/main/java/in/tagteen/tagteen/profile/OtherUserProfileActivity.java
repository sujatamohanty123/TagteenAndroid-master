
package in.tagteen.tagteen.profile;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import in.tagteen.tagteen.AntonyChanges;
import in.tagteen.tagteen.ConnectivityReceiver;
import in.tagteen.tagteen.FanList_new;
import in.tagteen.tagteen.Interfaces.UserProfileCallbackListener;
import in.tagteen.tagteen.Model.GetFanList;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.networkEngine.ModifiedAsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.CircleTransform;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import ru.noties.scrollable.ScrollableLayout;

/**
 * Created by lovekushvishwakarma on 26/10/17.
 */

@AntonyChanges
public class OtherUserProfileActivity extends AppCompatActivity
    implements AsyncResponse, ConnectivityReceiver.ConnectivityReceiverListener,
    UserProfileCallbackListener {
  private TextView no_internet;
  private LinearLayout buzz_main_container;
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private FragmentPagerAdapter adapter;
  private String path;
  private ImageView imageUserPIc, imageback;
  private String login_usr_id, other_user_id;
  private TextView textUserTagno, txt_name, lblUnverifiedTag;
  private String first_name;
  private String last_name;
  private boolean isTaggedUser;

  private LinearLayout linear_txtSupporters, linear_txtSupportings, linear_edit, linear_Settings,
      linear_Support, linear_you_r_supporting,
      linear_tag_me, linear_tagged, linear_pending;
  private TextView txtSupporters, txtSupportings;
  private ImageView image1, image2;
  private ImageView imgUserBadge;

  private RelativeLayout relative;
  private ImageView placeholder;
  private LinearLayout layoutProgressProfilePic;

  private ProfileVideoFragment profileVideoFragment;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_profile);

    other_user_id = getIntent().getStringExtra(Constants.USER_ID);
    SharedPreferenceSingleton.getInstance().init(this);
    login_usr_id =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    isTaggedUser = SharedPreferenceSingleton.getInstance()
        .getBoolPreference(RegistrationConstants.IS_TAGGED_USER);
    relative = (RelativeLayout) findViewById(R.id.relative);
    placeholder = (ImageView) findViewById(R.id.placeholder);
    final ScrollableLayout scrollableLayout =
        (ScrollableLayout) findViewById(R.id.scrollable_layout);
    imageUserPIc = (ImageView) findViewById(R.id.imageUserPIc);
    textUserTagno = (TextView) findViewById(R.id.textUserTagno);
    txt_name = (TextView) findViewById(R.id.txt_name);
    imageback = (ImageView) findViewById(R.id.imageback);
    this.layoutProgressProfilePic = (LinearLayout) findViewById(R.id.layoutProgressProfilePic);
    this.lblUnverifiedTag = (TextView) findViewById(R.id.lblUnverifiedTag);
    this.imgUserBadge = findViewById(R.id.imgRankBadge);
    this.imgUserBadge.setVisibility(View.GONE);

    image1 = (ImageView) findViewById(R.id.image1);
    image2 = (ImageView) findViewById(R.id.image2);
    linear_txtSupporters = (LinearLayout) findViewById(R.id.linear_txtSupporters);
    linear_txtSupportings = (LinearLayout) findViewById(R.id.linear_txtSupportings);
    linear_edit = (LinearLayout) findViewById(R.id.linear_edit);
    linear_Settings = (LinearLayout) findViewById(R.id.linear_Settings);
    linear_Support = (LinearLayout) findViewById(R.id.linear_Support);
    linear_you_r_supporting = (LinearLayout) findViewById(R.id.linear_you_r_supporting);
    linear_tag_me = (LinearLayout) findViewById(R.id.linear_tag_me);
    linear_tagged = (LinearLayout) findViewById(R.id.linear_tagged);
    linear_pending = (LinearLayout) findViewById(R.id.linear_pending);
    txtSupporters = (TextView) findViewById(R.id.txtSupporters);
    txtSupportings = (TextView) findViewById(R.id.txtSupportings);

    linear_edit.setVisibility(View.GONE);
    linear_Settings.setVisibility(View.GONE);

    tabLayout = (TabLayout) findViewById(R.id.tabs);
    /*tabLayout.addTab(tabLayout.newTab().setText("ABOUT"));
    tabLayout.addTab(tabLayout.newTab().setText("FRIENDS"));
    tabLayout.addTab(tabLayout.newTab().setText("PHOTOS"));*/
    tabLayout.addTab(tabLayout.newTab().setText("VIDEOS"));
    imageback.setVisibility(View.VISIBLE);
    imageback.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });

    linear_txtSupportings.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String tittle = first_name + " is Supporting";
        gotoFanList("2", tittle);
      }
    });
    linear_txtSupporters.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String title = first_name + "'s " + "Supporters";
        gotoFanList("1", title);
      }
    });
  }

  private void checkConnection() {
    boolean isConnected = ConnectivityReceiver.isConnected();
    showSnack(isConnected);
  }

  private void showSnack(boolean isConnected) {
        /*if (isConnected) {
            buzz_main_container.setVisibility(View.VISIBLE);
            no_internet.setVisibility(View.GONE);
        } else {
            buzz_main_container.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }*/
  }

  @Override
  public void onNetworkConnectionChanged(boolean isConnected) {
    showSnack(isConnected);
  }

  @Override
  public void onResume() {
    super.onResume();
    TagteenApplication.getInstance().setConnectivityListener(this);
    this.getProfile();
  }

  private void beAFanApi() {
    ModifiedAsyncWorker mWorker = new ModifiedAsyncWorker(this);
    mWorker.delegate = this;
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    try {
      BroadcastObject.put("user_id", login_usr_id);
      BroadcastObject.put("friend_user_id", other_user_id);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mWorker.execute(ServerConnector.REQUEST_BE_A_FAN, BroadcastObject.toString(),
        RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES,
        RequestConstants.REQUEST_BE_A_FAN);
  }

  private void sendFriendRequest() {
    ModifiedAsyncWorker mWorker = new ModifiedAsyncWorker(this);
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    try {
      JSONObject userObject = new JSONObject();
      userObject.put("friend_user_id", other_user_id);
      userObject.put("ignored", false);
      userObject.put("accepted", false);
      userObject.put("requested_by", true);

      JSONObject friend_obj = new JSONObject();
      friend_obj.put("request_user_id", login_usr_id);
      friend_obj.put("ignored", false);
      friend_obj.put("accepted", false);
      friend_obj.put("requested_by", false);

      BroadcastObject.put("request_user_id", login_usr_id);
      BroadcastObject.put("friend_user_id", other_user_id);
      BroadcastObject.put("user_obj", userObject);
      BroadcastObject.put("friend_obj", friend_obj);
    } catch (Exception e) {
      e.printStackTrace();
    }

    mWorker.execute(ServerConnector.SEND_FRIEND_REQUEST, BroadcastObject.toString(),
        RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES,
        RequestConstants.SEND_FRIEND_REQUEST);
  }

  private void gotoFanList(String flag, String tittle) {
    Intent in = new Intent(this, FanList_new.class);
    in.putExtra("flag", flag);
    in.putExtra("tittle", tittle);
    in.putExtra("user_id", other_user_id);
    startActivity(in);
  }

  @Override
  public void onRefresh() {

  }

  @Override
  public void unSupportCallback(boolean success) {
    if (success) {
      this.linear_you_r_supporting.setVisibility(View.GONE);
      this.linear_Support.setVisibility(View.VISIBLE);
    } else {
      this.linear_you_r_supporting.setVisibility(View.VISIBLE);
      this.linear_Support.setVisibility(View.GONE);
    }
  }

  @Override
  public void unFriendCallback(boolean success) {
    if (success) {
      this.linear_tagged.setVisibility(View.GONE);
      this.linear_tag_me.setVisibility(View.GONE);
    } else {
      this.linear_tagged.setVisibility(View.GONE);
      this.linear_tag_me.setVisibility(View.GONE);
    }
  }

  @AntonyChanges
  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    try {
      if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_GET_PROFILE)) {
        JSONObject jsonObject = new JSONObject(output);
        JSONObject data = jsonObject.getJSONObject("data");
        String tagged_number = data.getString("tagged_number");
        path = data.getString("profile_url");
        //String user_id = data.getString("user_id");
        first_name = data.getString("first_name");
        last_name = data.getString("last_name");
        //String email = data.getString("email");
        //String dob = data.getString("dob");
        //String gender = data.getString("gender");
        //String currently_studying = data.getString("currently_studying");
        //String school_name = data.getString("school_name");
        //String pincode = data.getString("pincode");
        //String city = data.getString("city");
        String be_a_fan_count = data.getString("be_a_fan_count");
        String my_fan_count = data.getString("my_fan_count");
        final boolean is_my_fan = data.getBoolean("is_my_fan");
        final boolean is_my_friend = data.getBoolean("is_my_friend");
        final boolean is_pending_friend = data.getBoolean("is_pending_friend");
        boolean is_tagged_user = data.getBoolean("is_tagged_user");

        //boolean email_privacy = data.getBoolean("email_privacy");
        //boolean mobile_privacy = data.getBoolean("mobile_privacy");
        boolean myfriends_privacy = data.getBoolean("myfriends_privacy");
        boolean myphotos_privacy = data.getBoolean("myphotos_privacy");
        //boolean education_privacy = data.getBoolean("education_privacy");
        //boolean dob_privacy = data.getBoolean("dob_privacy");

        boolean isRankedUser = data.optBoolean("is_ranked_user");
        if (isRankedUser) {
          this.imgUserBadge.setVisibility(View.VISIBLE);
        } else {
          this.imgUserBadge.setVisibility(View.GONE);
        }

        placeholder.setVisibility(View.GONE);
        relative.setVisibility(View.VISIBLE);
        if (!is_tagged_user) {
          lblUnverifiedTag.setVisibility(View.VISIBLE);
        } else {
          lblUnverifiedTag.setVisibility(View.GONE);
        }

        if (is_my_fan) {
          linear_you_r_supporting.setVisibility(View.VISIBLE);
        } else {
          linear_Support.setVisibility(View.VISIBLE);
        }
        if (is_pending_friend) {
          linear_pending.setVisibility(View.VISIBLE);
        } else {
          if (is_my_friend) {
            linear_tagged.setVisibility(View.GONE);
          } else {
            linear_tag_me.setVisibility(View.GONE);
          }
        }
        linear_tag_me.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            linear_tag_me.setClickable(false);
            linear_tag_me.setEnabled(false);
            sendFriendRequest();
          }
        });
        linear_tagged.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            linear_tagged.setClickable(false);
            linear_tagged.setEnabled(false);
            unFriend();
          }
        });
        linear_Support.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (!isTaggedUser) {
              Utils.showUnverifiedUserDialog(OtherUserProfileActivity.this);
            } else {
              linear_Support.setEnabled(false);
              beAFanApi();
            }
          }
        });
        linear_you_r_supporting.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            unSupport();
          }
        });
        txt_name.setText(first_name + " " + last_name);
        txtSupporters.setText(my_fan_count + "");
        txtSupportings.setText(be_a_fan_count + "");
        textUserTagno.setText(tagged_number);

        layoutProgressProfilePic.setVisibility(View.GONE);
        Glide.with(this)
            .load(UrlUtils.getUpdatedImageUrl(path, "large")) // add your image url
            .placeholder(R.drawable.profile_pic_bg) // making this null, no place holder
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

        if (adapter == null) {
          viewPager = (ViewPager) findViewById(R.id.viewpager);
          viewPager.setOffscreenPageLimit(1);
          adapter = new FragmentPagerAdapter(getSupportFragmentManager(),
              items(this, other_user_id, is_my_friend, is_pending_friend, myphotos_privacy,
                  myfriends_privacy));
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
        }
      } else if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_UN_FAN)) {
        JSONObject dataJson = new JSONObject(output);

        if (dataJson.getBoolean("success")) {
          Utils.showToast(this, "" + dataJson.get("message"));
        } else {
          Utils.showToast(this, "Something went wrong please try again");
        }
      } else if (REQUEST_NUMBER.equals(RequestConstants.REQUEST_BE_A_FAN)) {
        JSONObject dataJson = new JSONObject(output);

        if (dataJson.getBoolean("success")) {
          Utils.showToast(this, "" + dataJson.get("message"));
          linear_you_r_supporting.setVisibility(View.VISIBLE);
          linear_Support.setVisibility(View.GONE);
        } else {
          Utils.showToast(this, "Something went wrong please try again");
          linear_Support.setClickable(true);
          linear_Support.setEnabled(true);
        }
      } else if (RequestConstants.SEND_FRIEND_REQUEST.equalsIgnoreCase(REQUEST_NUMBER)) {
        JSONObject dataJson = new JSONObject(output);
        if (dataJson.getBoolean("success")) {
          linear_pending.setVisibility(View.VISIBLE);
          linear_tag_me.setVisibility(View.GONE);
          linear_tagged.setVisibility(View.GONE);
        } else {
          linear_tag_me.setClickable(true);
          linear_tag_me.setEnabled(true);
          linear_tagged.setClickable(true);
          linear_tagged.setEnabled(true);
        }
        Utils.showToast(this, dataJson.getString("message"));
      }
    } catch (Exception e) {
      Log.e("OtherUserProfile", output);
    }
  }

  private void unFriend() {
    GetFanList.UserData userData = new GetFanList().new UserData();
    userData.setFirst_name(this.first_name);
    userData.setLast_name(this.last_name);
    userData.setProfile_url(this.path);
    userData.setTaggedMe(true);
    new UnfriendDialog(this, userData, this);
  }

  private void unSupport() {
    GetFanList.UserData userData = new GetFanList().new UserData();
    userData.setFirst_name(this.first_name);
    userData.setLast_name(this.last_name);
    userData.setProfile_url(this.path);
    userData.setIs_myfan(true);
    userData.setUser_id(this.other_user_id);
    new UnfllowDialog(this, userData, this);
  }

  private void fanUnfanAPI(boolean is_my_fan) {
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    String usr_id =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    try {
      BroadcastObject.put("user_id", usr_id);
      BroadcastObject.put("friend_user_id", "" + other_user_id);
    } catch (Exception e) {
      e.printStackTrace();
    }
    String url;
    if (is_my_fan) {
      url = ServerConnector.REQUEST_UNFAN;
      mWorker.execute(url, BroadcastObject.toString(), RequestConstants.POST_REQUEST,
          RequestConstants.HEADER_YES, RequestConstants.REQUEST_UN_FAN);
    } else {
      url = ServerConnector.REQUEST_BE_A_FAN;
      mWorker.execute(url, BroadcastObject.toString(), RequestConstants.POST_REQUEST,
          RequestConstants.HEADER_YES, RequestConstants.REQUEST_BE_A_FAN);
    }
  }

  private void getProfile() {
    ModifiedAsyncWorker mWorker = new ModifiedAsyncWorker(this);
    mWorker.delegate = this;
    JSONObject BroadcastObject = new JSONObject();
    mWorker.execute(
        ServerConnector.REQUEST_GET_USER_PROFILE + other_user_id,
        BroadcastObject.toString(),
        RequestConstants.GET_REQUEST,
        RequestConstants.HEADER_YES,
        RequestConstants.REQUEST_GET_PROFILE);
  }

  private List<FragmentPagerAdapter.Item> items(
      final Context context,
      final String other_user_id,
      final boolean is_my_friend,
      final boolean is_pending_friend,
      final boolean myphotos_privacy,
      final boolean myfriends_privacy) {
    final Resources r = context.getResources();
    final List<FragmentPagerAdapter.Item> items = new ArrayList<>(1);

    // showrooms
    /*items.add(new FragmentPagerAdapter.Item(
        r.getString(R.string.tab),
        new FragmentPagerAdapter.Provider() {
          @Override
          public Fragment provide() {

            Fragment fragment = new AboutFragment();
            Bundle showroom_profile = new Bundle();
            showroom_profile.putString("user_id", other_user_id);
            showroom_profile.putBoolean("is_my_friend", is_my_friend);
            showroom_profile.putBoolean("isPendingFriend", is_pending_friend);
            fragment.setArguments(showroom_profile);
            return fragment;
          }
        }
    ));*/

    // followers
    /*items.add(new FragmentPagerAdapter.Item(
        r.getString(R.string.tab),
        new FragmentPagerAdapter.Provider() {
          @Override
          public Fragment provide() {
            Fragment fragment = new ProfileFriendFragment();
            Bundle follower_profile = new Bundle();
            follower_profile.putString("user_id", other_user_id);
            follower_profile.putBoolean("is_my_friend", is_my_friend);
            follower_profile.putBoolean("myfriends_privacy", myfriends_privacy);
            fragment.setArguments(follower_profile);
            return fragment;
          }
        }
    ));*/

    // followers
    /*items.add(new FragmentPagerAdapter.Item(
        r.getString(R.string.tab),
        new FragmentPagerAdapter.Provider() {
          @Override
          public Fragment provide() {
            Fragment fragment = new ProfilePhotosFragment();
            Bundle follower_profile = new Bundle();
            follower_profile.putString("user_id", other_user_id);
            follower_profile.putBoolean("is_my_friend", is_my_friend);
            follower_profile.putBoolean("myphotos_privacy", myphotos_privacy);
            fragment.setArguments(follower_profile);
            return fragment;
          }
        }
    ));*/

    // status
    items.add(new FragmentPagerAdapter.Item(
        r.getString(R.string.tab),
        new FragmentPagerAdapter.Provider() {
          @Override
          public Fragment provide() {
            if (profileVideoFragment == null) {
              profileVideoFragment = new ProfileVideoFragment();
            }
            Bundle stat_profile = new Bundle();
            stat_profile.putString("user_id", other_user_id);
            profileVideoFragment.setArguments(stat_profile);
            return profileVideoFragment;
          }
        }
    ));

    return items;
  }
}
