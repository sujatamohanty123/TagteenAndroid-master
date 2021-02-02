package in.tagteen.tagteen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import in.tagteen.tagteen.Adapters.DashBoardAdapter;
import in.tagteen.tagteen.Fragments.ChatListFragment;
import in.tagteen.tagteen.Fragments.FriendsListFragment;
import in.tagteen.tagteen.Fragments.GroupListFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.utils.CircleTransform;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import io.socket.client.Socket;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class ChatDashBoardActivity extends Fragment implements ViewPager.OnPageChangeListener/*,ConnectivityReceiver.ConnectivityReceiverListener*/ {
    TabHost tabHost;
    private List<String> mTabList = new ArrayList<>();
    Dialog dialog;
    Animation alpha;
    LinearLayout linearLayout;
    TabLayout tabLayout;
    EditText txtOutput;
    ImageView imageDrawarView, cross_view;
    private final int SPEECH_RECOGNITION_CODE = 1;
    private ImageView btnMicrophone;
    Socket mSocket;
    Snackbar snackbar;
    CoordinatorLayout chatlist;
    String mUserFirstName,mUserLastName,mUserTag,mUserProfile,mUserId;
    ViewPager mViewPager;
    View  rootView;
    Toolbar searchtollbar;
    Menu search_menu;
    MenuItem item_search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(in.tagteen.tagteen.R.layout.chat_dash_board, container, false);

        this.setSearchtollbar();

        imageDrawarView = (ImageView) rootView.findViewById(in.tagteen.tagteen.R.id.search_tab_button);
        cross_view = (ImageView) rootView.findViewById(in.tagteen.tagteen.R.id.cross_tab);
        txtOutput = (EditText) rootView.findViewById(in.tagteen.tagteen.R.id.search_edittext);
        linearLayout = (LinearLayout) rootView.findViewById(in.tagteen.tagteen.R.id.search_layout);
        chatlist = (CoordinatorLayout) rootView.findViewById(R.id.main_content);
        alpha = AnimationUtils.loadAnimation(getActivity(), in.tagteen.tagteen.R.anim.tab_fab_out);
        alpha.setDuration(10);

        imageDrawarView.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        btnMicrophone = (ImageView) rootView.findViewById(in.tagteen.tagteen.R.id.btn_mic);
        btnMicrophone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSpeechToText();
                }
            });

        imageDrawarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(R.id.searchtoolbar,1,true,true);
                else
                    searchtollbar.setVisibility(View.VISIBLE);
                item_search.expandActionView();
            }
        });
        cross_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.INVISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                imageDrawarView.setVisibility(View.VISIBLE);
            }
        });
        DashBoardAdapter mSectionsPagerAdapter = new DashBoardAdapter
                (getChildFragmentManager(), getContext().getApplicationContext());
        mViewPager = (ViewPager) rootView.findViewById(in.tagteen.tagteen.R.id.chat_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) rootView.findViewById(in.tagteen.tagteen.R.id.tabs_chat);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);

        tabLayout.getSelectedTabPosition();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        SharedPreferenceSingleton.getInstance().init(getActivity());
        mUserProfile =SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.PROFILE_URL);
        mUserId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        mUserFirstName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FIRST_NAME);
        mUserLastName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.LAST_NAME);
        mUserTag = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TAGGED_NUMBER);
        return rootView;
    }

    public void setSearchtollbar(){
        searchtollbar = (Toolbar)rootView.findViewById(R.id.searchtoolbar);
        if (searchtollbar != null) {
            searchtollbar.inflateMenu(R.menu.menu_search);
            search_menu=searchtollbar.getMenu();

            searchtollbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        circleReveal(R.id.searchtoolbar,1,true,false);
                    else
                        searchtollbar.setVisibility(View.GONE);
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);

            MenuItemCompat.setOnActionExpandListener(item_search, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(R.id.searchtoolbar,1,true,false);
                    }
                    else
                        searchtollbar.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });
            this.initSearchView();
        } else {
            Log.d("toolbar", "setSearchtollbar: NULL");
        }
    }

    public void initSearchView(){
        final SearchView searchView =
                (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        // Enable/Disable Submit button in the keyboard

        searchView.setSubmitButtonEnabled(false);

        // Change search close button image

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);


        // set hint and the text colors

        EditText txtSearch = ((EditText) searchView.findViewById(R.id.search_src_text));
        txtSearch.setHint("Search..");
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(getResources().getColor(R.color.colorPrimary));


        // set the cursor

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                if(query.length()<1){
                    return;
                }
                Log.i("query", "" + query);
                DashBoardAdapter pagerAdapter = (DashBoardAdapter) mViewPager.getAdapter();
                for (int i = 0; i < pagerAdapter.getCount(); i++) {
                    Fragment viewPagerFragment = (Fragment) mViewPager.getAdapter().instantiateItem(mViewPager, i);
                    if (viewPagerFragment != null && viewPagerFragment.isAdded()) {

                        if (viewPagerFragment instanceof ChatListFragment) {
                            ChatListFragment chatFragment = (ChatListFragment) viewPagerFragment;
                            if (chatFragment != null) {
                                chatFragment.beginSearch(query, true); // Calling the method beginSearch of ChatFragment
                            }
                        } else if (viewPagerFragment instanceof GroupListFragment) {
                            GroupListFragment groupsFragment = (GroupListFragment) viewPagerFragment;
                            if (groupsFragment != null) {
                                groupsFragment.beginSearch(query); // Calling the method beginSearch of GroupsFragment
                            }
                        } else if (viewPagerFragment instanceof FriendsListFragment) {
                            FriendsListFragment friendsFragment = (FriendsListFragment) viewPagerFragment;
                            if (friendsFragment != null) {
                                friendsFragment.beginSearch(query); // Calling the method beginSearch of ContactsFragment
                            }
                        }
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow){
        final View myView = rootView.findViewById(viewID);

        int width=myView.getWidth();

        if(posFromRight>0)
            width-=(posFromRight*getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material))-(getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)/ 2);
        if(containsOverflow)
            width-=getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx=width;
        int cy=myView.getHeight()/2;

        Animator anim;
        if(isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0,(float)width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float)width, 0);

        anim.setDuration((long)220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isShow)
                {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if(isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(searchtollbar.getVisibility()==View.VISIBLE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                circleReveal(R.id.searchtoolbar,1,true,false);
            else
                searchtollbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void openDialog() {
        dialog = new Dialog(getActivity(), R.style.MyCustomDrawer);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_navigation_drawable);
        final Window layout = dialog.getWindow();
        layout.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        layout.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
        layout.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        layout.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_draware)));
        RoundedImageView myPic=(RoundedImageView) dialog.findViewById(R.id.fL_di_profile_pic);
        RoundedImageView groupPic=(RoundedImageView) dialog.findViewById(R.id.fL_di_add_pic);
        RoundedImageView addGroupIcon=(RoundedImageView) dialog.findViewById(R.id.fL_di_add_icon);
        TextView userName=(TextView) dialog.findViewById(R.id.fL_di_name);
        TextView usertag=(TextView) dialog.findViewById(R.id.fL_di_tag);
        TextView search=(TextView) dialog.findViewById(R.id.fL_di_search);
        TextView newBroadcast=(TextView) dialog.findViewById(R.id.fL_di_new_broadcast);
        TextView faq=(TextView) dialog.findViewById(R.id.fL_di_faq);
        TextView sendaTag=(TextView) dialog.findViewById(R.id.fL_di_send_tag);
        TextView addAFriend=(TextView) dialog.findViewById(R.id.fL_di_add_friend);
        LinearLayout addGroup=(LinearLayout)dialog.findViewById(R.id.fL_di_add_group);
        ImageView clossImage=(ImageView)dialog.findViewById(R.id.fL_di_cross);
        userName.setText(mUserFirstName+" "+ mUserLastName);
        usertag.setText(mUserTag);
        Glide.with(getActivity()).load(mUserProfile).fitCenter().centerCrop().placeholder(R.drawable.pr_pic).transform(new CircleTransform(getActivity())).into(myPic);
        Glide.with(getActivity()).load(mUserProfile).fitCenter().centerCrop().placeholder(R.drawable.pr_pic).transform(new CircleTransform(getActivity())).into(groupPic);
        addGroupIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_group_add_black_24dp));
        clossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(getActivity(),SearchActivity.class);
                startActivity(it);
            }
        });

        newBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(getActivity(),BroadcastListActivity.class);
                startActivity(it);
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendaTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addAFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent it=new Intent(getActivity(),NewGroupActivity.class);
                startActivity(it);
            }
        });
        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    txtOutput.setText(text);
                }
                break;
            }
        }
    }

   /* @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }*/
}