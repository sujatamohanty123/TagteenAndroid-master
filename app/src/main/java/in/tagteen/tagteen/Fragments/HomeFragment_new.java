package in.tagteen.tagteen.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import in.tagteen.tagteen.Adapters.HomePageAdapter;
import in.tagteen.tagteen.Fragments.youthtube.SearchShowRoomActivity;
import in.tagteen.tagteen.Fragments.youthtube.SupportingShowroomFragment;
import in.tagteen.tagteen.Fragments.youthtube.VideoGallery;
import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;
import in.tagteen.tagteen.Model.NotificationsData;
import in.tagteen.tagteen.NotificationActivity;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.SearchActivity;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.profile.UserProfileFragment;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.FloatingActionButton;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AndroidPermissionMarshMallo;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment_new extends Fragment implements AsyncResponse {
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private TextView unverifiedText, welcome_msg;
    private FloatingActionButton fabAdd;
    private AndroidPermissionMarshMallo permissionMarshMallo;
    private LinearLayout layoutRewards;
    private LinearLayout layoutSupporting;
    private ImageView profileButton;
    private RelativeLayout layoutNotifications;
    private AppCompatEditText txtSearch;
    private LinearLayout layout1, layout2, layout3, layout4, layout5;
    private AppCompatEditText phoneNumber1, phoneNumber2, phoneNumber3, phoneNumber4, phoneNumber5;
    private TextView lblNotificationsCount;
    private HomePageAdapter mSectionsPagerAdapter;
    private Dialog dialog;
    boolean isTaggedUser = SharedPreferenceSingleton.getInstance()
            .getBoolPreference(RegistrationConstants.IS_TAGGED_USER);

    //private TextView lblTabTrending;
    private TextView lblTabPopular;
    private TextView lblTabShowroom;
    private TextView lblTabLatest;
    //private TextView lblTabWebShows;
    private TextView lblTabRockstars;
    private TextView lblTabSupporting;
    private TextView lblTabLanguages;

    private ImageView imgExpandOptions;
    private boolean isOptionsExpanded = false;
    private int expandPosition = TAB_INDEX_SHOWROOM; // set to showroom by default

    private int unseletedTabTextSize = 14;
    private int selectedTabTextSize = 18;

    //public static final int TAB_INDEX_TRENDING = 0;
    public static final int TAB_INDEX_LATEST = 0;
    public static final int TAB_INDEX_SHOWROOM = 1;
    //public static final int TAB_INDEX_WEBSHOWS = 2;
    public static final int TAB_INDEX_LANGUAGES = 2;
    public static final int TAB_INDEX_ROCKSTARS = 3;
    public static final int TAB_INDEX_SUPPORTING = 4;
    public static final int TAB_INDEX_POPULAR = 5;

    public static final int TAB_COUNT = 1;
    private static final int LANDING_TAB = TAB_INDEX_LATEST;

    private OnPostDeleteListener onPostDeleteListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home_fragment_new, container, false);

        this.layoutRewards = (LinearLayout) rootView.findViewById(R.id.btnRewards);
        this.profileButton = rootView.findViewById(R.id.imgProfileIcon);
        this.layoutSupporting = rootView.findViewById(R.id.layoutSupporting);
        this.loadProfilePic();

        this.layoutNotifications = rootView.findViewById(in.tagteen.tagteen.R.id.layoutNotifications);
        this.txtSearch = rootView.findViewById(R.id.txtSearch);
        //searchButton = (ImageView) rootView.findViewById(R.id.btnSearch);
        this.unverifiedText = (TextView) rootView.findViewById(R.id.unverified_text);

        if (this.isTaggedUser) {
            this.unverifiedText.setVisibility(View.GONE);
        } else {
            this.unverifiedText.setText("unverified");
        }

        permissionMarshMallo = new AndroidPermissionMarshMallo(getActivity());
        mSectionsPagerAdapter = new HomePageAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) rootView.findViewById(R.id.home_viewpager);
        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs_home);
        fabAdd = (FloatingActionButton) rootView.findViewById(R.id.fabAdd);
        this.lblNotificationsCount = rootView.findViewById(R.id.lblNotificationsCount);
        tabLayout.setupWithViewPager(mViewPager);

        this.imgExpandOptions = rootView.findViewById(R.id.imgExpandOptions);

        this.createTabIcons();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // select showroom tab by default
        //mViewPager.setCurrentItem(1);
        this.tabLayout.getTabAt(LANDING_TAB).select();
        //this.lblTabPopular.setTextSize(14);

        this.txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToSearch();
            }
        });
        layoutRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isGuestLogin()) {
                    Utils.moveToRegistration(getActivity());
                } else {
                    Intent intent = new Intent(getActivity(), UserProfileFragment.class);
                    startActivity(intent);
                }
            }
        });
        this.profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isGuestLogin()) {
                    Utils.moveToRegistration(getActivity());
                } else {
                    Intent intent = new Intent(getActivity(), UserProfileFragment.class);
                    startActivity(intent);
                }
            }
        });
        this.layoutSupporting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isGuestLogin()) {
                    Utils.moveToRegistration(getActivity());
                } else {
                    Intent intent = new Intent(getActivity(), SupportingShowroomFragment.class);
                    startActivity(intent);
                }
            }
        });

        layoutNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNotifications();
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!permissionMarshMallo.checkPermissionForCamera()) {
                        permissionMarshMallo.requestPermissionForCamera();
                    } else if (!permissionMarshMallo.checkPermissionForRecord()) {
                        permissionMarshMallo.requestPermissionForRecord();
                    } else {
                        if (!permissionMarshMallo.checkPermissionForExternalStorage()) {
                            permissionMarshMallo.requestPermissionForExternalStorage();
                        } else if (!permissionMarshMallo.checkPermissionForRecord()) {
                            permissionMarshMallo.requestPermissionForRecord();
                        } else {
                            if (isTaggedUser) {
                                String dateStr = SharedPreferenceSingleton.getInstance()
                                        .getStringPreference(Constants.SHOWROOM_LAST_UPLOADED_AT);
                                Date lastUploadedAt = Utils.getDateFromString(dateStr);
                                if (Utils.is24HrsCompleted(lastUploadedAt)) {
                                    gotoVideoList();
                                } else {
                                    gotoVideoList();
                                    //Utils.showShortToast(getActivity(), "You can upload only one video per day.");
                                }
                            } else {
                                Utils.showUnverifiedUserDialog(getActivity());
                            }
                        }
                    }
                } else {
                    if (isTaggedUser) {
                        String dateStr = SharedPreferenceSingleton.getInstance()
                                .getStringPreference(Constants.SHOWROOM_LAST_UPLOADED_AT);
                        Date lastUploadedAt = Utils.getDateFromString(dateStr);
                        if (Utils.is24HrsCompleted(lastUploadedAt)) {
                            gotoVideoList();
                        } else {
                            gotoVideoList();
                            //Utils.showAlertDialog(getActivity(), "You can upload only one video per day.", "Alert");
                        }
                    } else {
                        Utils.showUnverifiedUserDialog(getActivity());
                    }
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetTabTextSize();

                imgExpandOptions.setVisibility(View.GONE);
                switch (position) {
                    /*case TAB_INDEX_TRENDING:
                        lblTabTrending.setTextSize(selectedTabTextSize);
                        lblTabTrending.setTypeface(null, Typeface.BOLD);
                        break;*/

                    case TAB_INDEX_SHOWROOM:
                        imgExpandOptions.setVisibility(View.GONE);
                        lblTabShowroom.setTextSize(selectedTabTextSize);
                        lblTabShowroom.setTypeface(null, Typeface.BOLD);
                        //fabAdd.setVisibility(View.VISIBLE);
                        showAlertOnFirstShowroomAccess();
                        expandOptions();
                        break;

                    case TAB_INDEX_LATEST:
                        lblTabLatest.setTextSize(selectedTabTextSize);
                        lblTabLatest.setTypeface(null, Typeface.BOLD);
                        //fabAdd.setVisibility(View.VISIBLE);
                        collapseOptions();
                        break;

                    /*case TAB_INDEX_WEBSHOWS:
                        lblTabWebShows.setTextSize(selectedTabTextSize);
                        lblTabWebShows.setTypeface(null, Typeface.BOLD);
                        break;*/

                    case TAB_INDEX_ROCKSTARS:
                        lblTabRockstars.setTextSize(selectedTabTextSize);
                        lblTabRockstars.setTypeface(null, Typeface.BOLD);
                        collapseOptions();
                        break;

                    case TAB_INDEX_SUPPORTING:
                        lblTabSupporting.setTextSize(selectedTabTextSize);
                        lblTabSupporting.setTypeface(null, Typeface.BOLD);
                        collapseOptions();
                        break;

                    case TAB_INDEX_POPULAR:
                        lblTabPopular.setTextSize(selectedTabTextSize);
                        lblTabPopular.setTypeface(null, Typeface.BOLD);
                        collapseOptions();
                        break;

                    case TAB_INDEX_LANGUAGES:
                        lblTabLanguages.setTextSize(selectedTabTextSize);
                        lblTabLanguages.setTypeface(null, Typeface.BOLD);
                        collapseOptions();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                int position = tab.getPosition();
                resetTabTextSize();

                imgExpandOptions.setVisibility(View.GONE);
                switch (position) {
                    /*case TAB_INDEX_TRENDING:
                        lblTabTrending.setTextSize(selectedTabTextSize);
                        lblTabTrending.setTypeface(null, Typeface.BOLD);
                        break;*/

                    case TAB_INDEX_SHOWROOM:
                        imgExpandOptions.setVisibility(View.GONE);
                        lblTabShowroom.setTextSize(selectedTabTextSize);
                        lblTabShowroom.setTypeface(null, Typeface.BOLD);
                        showAlertOnFirstShowroomAccess();
                        fabAdd.setVisibility(View.VISIBLE);
                        break;

                    case TAB_INDEX_LATEST:
                        lblTabLatest.setTextSize(selectedTabTextSize);
                        lblTabLatest.setTypeface(null, Typeface.BOLD);
                        fabAdd.setVisibility(View.VISIBLE);
                        break;

                    /*case TAB_INDEX_WEBSHOWS:
                        lblTabWebShows.setTextSize(selectedTabTextSize);
                        lblTabWebShows.setTypeface(null, Typeface.BOLD);
                        break;*/

                    case TAB_INDEX_ROCKSTARS:
                        lblTabRockstars.setTextSize(selectedTabTextSize);
                        lblTabRockstars.setTypeface(null, Typeface.BOLD);
                        break;

                    case TAB_INDEX_SUPPORTING:
                        lblTabSupporting.setTextSize(selectedTabTextSize);
                        lblTabSupporting.setTypeface(null, Typeface.BOLD);
                        break;

                    case TAB_INDEX_POPULAR:
                        lblTabPopular.setTextSize(selectedTabTextSize);
                        lblTabPopular.setTypeface(null, Typeface.BOLD);
                        break;

                    case TAB_INDEX_LANGUAGES:
                        lblTabLanguages.setTextSize(selectedTabTextSize);
                        lblTabLanguages.setTypeface(null, Typeface.BOLD);
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

        this.imgExpandOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOptionsExpanded) {
                    collapseOptions();
                } else {
                    expandOptions();
                }
            }
        });

        return rootView;
    }

    private void expandOptions() {
        this.imgExpandOptions.setImageResource(R.drawable.cross_icon);
        this.isOptionsExpanded = true;
        if (this.mSectionsPagerAdapter != null) {
            this.mSectionsPagerAdapter.expandOptions(this.expandPosition);
        }
    }

    private void collapseOptions() {
        this.imgExpandOptions.setImageResource(R.drawable.ic_category_hidden_icon);
        this.isOptionsExpanded = false;
        if (this.mSectionsPagerAdapter != null) {
            this.mSectionsPagerAdapter.collapseOptions(this.expandPosition);
        }
    }

    private void resetTabTextSize() {
        /*this.lblTabTrending.setTextSize(this.unseletedTabTextSize);
        this.lblTabTrending.setTypeface(null, Typeface.NORMAL);*/
        this.lblTabShowroom.setTextSize(this.unseletedTabTextSize);
        this.lblTabShowroom.setTypeface(null, Typeface.NORMAL);
        this.lblTabLatest.setTextSize(this.unseletedTabTextSize);
        this.lblTabLatest.setTypeface(null, Typeface.NORMAL);
        this.lblTabRockstars.setTextSize(this.unseletedTabTextSize);
        this.lblTabRockstars.setTypeface(null, Typeface.NORMAL);
        this.lblTabSupporting.setTextSize(this.unseletedTabTextSize);
        this.lblTabSupporting.setTypeface(null, Typeface.NORMAL);
        /*this.lblTabWebShows.setTextSize(this.unseletedTabTextSize);
        this.lblTabWebShows.setTypeface(null, Typeface.NORMAL);*/
        this.lblTabPopular.setTextSize(this.unseletedTabTextSize);
        this.lblTabPopular.setTypeface(null, Typeface.NORMAL);
        this.lblTabLanguages.setTextSize(this.unseletedTabTextSize);
        this.lblTabLanguages.setTypeface(null, Typeface.NORMAL);
    }

    private void showAlertOnFirstShowroomAccess() {
        boolean isShowroomAccessed = SharedPreferenceSingleton.getInstance()
                .getBoolPreference(RegistrationConstants.IS_SHOWROOM_ACCESSED);
        if (!isShowroomAccessed) {
            // not showing this for now
            //Utils.showShowroomFirstAccessAlert(getActivity());
        }
        SharedPreferenceSingleton.getInstance()
                .writeBoolPreference(RegistrationConstants.IS_SHOWROOM_ACCESSED, true);
    }

    public void setOnPostDeleteListener(OnPostDeleteListener onPostDeleteListener) {
        this.onPostDeleteListener = onPostDeleteListener;
        if (this.mSectionsPagerAdapter != null) {
            this.mSectionsPagerAdapter.setOnPostDeleteListener(this.onPostDeleteListener);
        }
    }

    public void moveToLandingTab() {
        if (this.mViewPager != null) {
            this.mViewPager.setCurrentItem(LANDING_TAB);
        }
    }

    private void moveToNotifications() {
        this.lblNotificationsCount.setVisibility(View.GONE);
        Intent intent = new Intent(getActivity(), NotificationActivity.class);
        startActivity(intent);
    }

    private void moveToSearch() {
        if (Utils.isGuestLogin()) {
            Utils.moveToRegistration(getActivity());
        } else {
            Intent it = new Intent(getActivity(), SearchActivity.class);
            startActivity(it);
        }
    }

    private void loadProfilePic() {
        String profilePicUrl = SharedPreferenceSingleton.getInstance()
                .getStringPreference(getContext(), RegistrationConstants.PROFILE_URL);
        Utils.loadProfilePic(getContext(), this.profileButton, profilePicUrl);
    }

    private void checkNotifications() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNotificationsCount();
            }
        }, 500);
    }

    private void getNotificationsCount() {
        SharedPreferenceSingleton.getInstance().init(getActivity());
        String userId =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String token =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<NotificationsData> call = methods.getAllNotifications(userId, token);
        call.enqueue(new Callback<NotificationsData>() {
            @Override
            public void onResponse(Call<NotificationsData> call, Response<NotificationsData> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    NotificationsData data = response.body();
                    if (data != null && data.getData() != null) {
                        int count = data.getData().size();
                        if (count > 0) {
                            //lblNotificationsCount.setText(count);
                            //lblNotificationsCount.setVisibility(View.VISIBLE);
                        } else {
                            //lblNotificationsCount.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationsData> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        switch (REQUEST_NUMBER) {
            case RequestConstants.REQUEST_FOR_TAG_FRIEND: {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    boolean isSuccess = jsonObject.getBoolean("success");
                    if (isSuccess) {
                        dialog.dismiss();
                    } else {
                        Utils.showShortToast(getActivity().getApplicationContext(), output);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean validatePhone(String mobile1) {
        String regexStr = "^[5-9][0-9]{9}$";
        if (mobile1.matches(regexStr)
                && !mobile1.equals("9999999999")
                && !mobile1.equals("8888888888")
                && !mobile1.equals("7777777777")
                && !mobile1.contains(" ")) {
            return true;
        } else {
            return false;
        }
    }

    private void createTabIcons() {
        /*this.lblTabTrending = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblTabTrending.setText("Popular");
        this.tabLayout.getTabAt(TAB_INDEX_TRENDING).setCustomView(this.lblTabTrending);*/

        this.lblTabLatest =
                (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblTabLatest.setText("For You");
        this.lblTabLatest.setTextSize(this.selectedTabTextSize);
        this.lblTabLatest.setTypeface(null, Typeface.BOLD);
        this.tabLayout.getTabAt(TAB_INDEX_LATEST).setCustomView(this.lblTabLatest);

        if (TAB_COUNT == 1) {
            return;
        }

        this.lblTabShowroom =
                (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblTabShowroom.setText("Categories");
        this.tabLayout.getTabAt(TAB_INDEX_SHOWROOM).setCustomView(this.lblTabShowroom);

        // webshows
        /*this.lblTabWebShows = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblTabWebShows.setText("WebShows");
        this.tabLayout.getTabAt(TAB_INDEX_WEBSHOWS).setCustomView(this.lblTabWebShows);*/

        this.lblTabRockstars =
                (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblTabRockstars.setText("Rockstars");
        this.tabLayout.getTabAt(TAB_INDEX_ROCKSTARS).setCustomView(this.lblTabRockstars);

        this.lblTabSupporting =
                (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblTabSupporting.setText("Supporting");
        this.tabLayout.getTabAt(TAB_INDEX_SUPPORTING).setCustomView(this.lblTabSupporting);

        this.lblTabPopular =
                (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblTabPopular.setText("Popular");
        //this.tabLayout.getTabAt(TAB_INDEX_POPULAR).setCustomView(this.lblTabPopular);

        this.lblTabLanguages =
                (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblTabLanguages.setText("Languages");
        this.tabLayout.getTabAt(TAB_INDEX_LANGUAGES).setCustomView(this.lblTabLanguages);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void gotoVideoList() {
        Intent gotoVideogallery = new Intent(getActivity(), VideoGallery.class);
        startActivity(gotoVideogallery);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.mSectionsPagerAdapter != null) {
            this.mSectionsPagerAdapter.onActivityResult(
                    requestCode, resultCode, data, this.mViewPager.getCurrentItem());
        }
    }
}
