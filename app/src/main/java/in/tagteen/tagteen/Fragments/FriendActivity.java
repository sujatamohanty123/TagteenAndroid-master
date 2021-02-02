package in.tagteen.tagteen.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.tagteen.tagteen.Adapters.TabsPagerAdapter_friend;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendActivity extends Fragment  {
    private TabLayout tabLayout_friend;
    private ViewPager viewPager_friend;
    private GetAllUserFriendlist.Data datalist = new GetAllUserFriendlist.Data();
    private TabsPagerAdapter_friend adapter;
    private ArrayList<GetAllUserFriendlist.FriendsUserList> friendlist = new ArrayList<>();
    public static ArrayList<GetAllUserFriendlist.FriendsUserList> bfflist = new ArrayList<>();
    private ArrayList<GetAllUserFriendlist.FriendsUserList> requestfriendlist = new ArrayList<>();

    private TextView lblAll;
    private TextView lblSearch;
    private TextView lblBFFs;
    private TextView lblRequests;
    private int unseletedTabTextSize = 14;
    private int selectedTabTextSize = 18;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.activity_friend, container, false);

        tabLayout_friend = (TabLayout) view.findViewById(R.id.tabs_friend);
        this.createTabs();
        /*tabLayout_friend.addTab(tabLayout_friend.newTab().setText("ALL"));
        tabLayout_friend.addTab(tabLayout_friend.newTab().setText("SEARCH"));
        tabLayout_friend.addTab(tabLayout_friend.newTab().setText("BFFs"));
        tabLayout_friend.addTab(tabLayout_friend.newTab().setText("REQUESTS"));*/

        viewPager_friend = (ViewPager) view.findViewById(R.id.viewpager_friend);

        viewPager_friend.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout_friend));
        viewPager_friend.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                resetTabTextSize();
                if (position == 0) {
                    lblAll.setTextSize(selectedTabTextSize);
                    lblAll.setTypeface(null, Typeface.BOLD);
                } else if (position == 1) {
                    lblSearch.setTextSize(selectedTabTextSize);
                    lblSearch.setTypeface(null, Typeface.BOLD);
                } else if (position == 2) {
                    lblBFFs.setTextSize(16);
                    lblBFFs.setTypeface(null, Typeface.BOLD);
                } else if (position == 3) {
                    lblRequests.setTextSize(selectedTabTextSize);
                    lblRequests.setTypeface(null, Typeface.BOLD);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        tabLayout_friend.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_friend.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callApiFriendsList();
    }

    private void createTabs() {
        tabLayout_friend.addTab(tabLayout_friend.newTab());
        tabLayout_friend.addTab(tabLayout_friend.newTab());
        tabLayout_friend.addTab(tabLayout_friend.newTab());
        tabLayout_friend.addTab(tabLayout_friend.newTab());

        this.lblAll = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblAll.setTypeface(null, Typeface.BOLD);
        this.lblAll.setTextSize(this.selectedTabTextSize);
        this.lblAll.setText("All");
        this.tabLayout_friend.getTabAt(0).setCustomView(this.lblAll);

        this.lblSearch = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblSearch.setText("Search");
        this.tabLayout_friend.getTabAt(1).setCustomView(this.lblSearch);

        this.lblBFFs = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblBFFs.setText("BFFs");
        this.tabLayout_friend.getTabAt(2).setCustomView(this.lblBFFs);

        this.lblRequests = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        this.lblRequests.setText("Requests");
        this.tabLayout_friend.getTabAt(3).setCustomView(this.lblRequests);
    }

    private void resetTabTextSize() {
        this.lblAll.setTextSize(this.unseletedTabTextSize);
        this.lblAll.setTypeface(null, Typeface.NORMAL);
        this.lblSearch.setTextSize(this.unseletedTabTextSize);
        this.lblSearch.setTypeface(null, Typeface.NORMAL);
        this.lblBFFs.setTextSize(this.unseletedTabTextSize);
        this.lblBFFs.setTypeface(null, Typeface.NORMAL);
        this.lblRequests.setTextSize(this.unseletedTabTextSize);
        this.lblRequests.setTypeface(null, Typeface.NORMAL);
    }

    private void callApiFriendsList() {
        SharedPreferenceSingleton.getInstance().init(getActivity());
        String userid= SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String token= SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);

        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<GetAllUserFriendlist> call =methods.getalluserfriendslist(userid,token);
        call.enqueue(new Callback<GetAllUserFriendlist>() {
            @Override
            public void onResponse(Call<GetAllUserFriendlist> call, Response<GetAllUserFriendlist> response) {
                if(response.code()==200){
                    GetAllUserFriendlist getlistdata=response.body();
                    datalist = getlistdata.getData();

                    bfflist = (ArrayList<GetAllUserFriendlist.FriendsUserList>) datalist.getBff();
                    friendlist= (ArrayList<GetAllUserFriendlist.FriendsUserList>) datalist.getFriendsUserList();
                    if (!isAdded()) {
                        return;
                    }
                    adapter = new TabsPagerAdapter_friend(getChildFragmentManager(), tabLayout_friend.getTabCount(),datalist);
                    viewPager_friend.setAdapter(adapter);

                    /*if(fragmentload.equalsIgnoreCase("8")){
                        viewPager_friend.setCurrentItem(3);
                    }
                    if(fragmentload.equalsIgnoreCase("9")){
                        viewPager_friend.setCurrentItem(0);
                    }*/
                }
            }

            @Override
            public void onFailure(Call<GetAllUserFriendlist> call, Throwable t) {
                Log.d("FriendActivity","Failed url:"+call.request().url().toString());
            }
        });

    }

    public ArrayList<GetAllUserFriendlist.FriendsUserList> getFriendlist() {
        return friendlist;
    }

    public void setFriendlist(ArrayList<GetAllUserFriendlist.FriendsUserList> friendlist) {
        this.friendlist = friendlist;
    }

    public ArrayList<GetAllUserFriendlist.FriendsUserList> getBfflist() {
        return bfflist;
    }

    public void setBfflist(ArrayList<GetAllUserFriendlist.FriendsUserList> bfflist) {
        this.bfflist = bfflist;
    }

    public ArrayList<GetAllUserFriendlist.FriendsUserList> getRequestfriendlist() {
        return requestfriendlist;
    }

    public void setRequestfriendlist(ArrayList<GetAllUserFriendlist.FriendsUserList>
                                             requestfriendlist) {
        this.requestfriendlist = requestfriendlist;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //Write down your refresh code here, it will call every time user come to this fragment.
            //If you are using listview with custom adapter, just call notifyDataSetChanged().
            //callApiFriendsList();
        }
    }
}
