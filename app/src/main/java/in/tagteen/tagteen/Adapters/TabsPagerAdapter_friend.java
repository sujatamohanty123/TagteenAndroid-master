package in.tagteen.tagteen.Adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;

import in.tagteen.tagteen.Fragments.BFFs;
import in.tagteen.tagteen.Fragments.FriendsAllFragment;
import in.tagteen.tagteen.Fragments.RequestFrament;
import in.tagteen.tagteen.Fragments.Search;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;

public class TabsPagerAdapter_friend extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    //Constructor to the class
    int flag = 1;
    GetAllUserFriendlist.Data datalist;
    ArrayList<GetAllUserFriendlist.FriendsUserList> friendsUserLists;
    Bundle bundle;

    public TabsPagerAdapter_friend(FragmentManager fm, int tabCount, GetAllUserFriendlist.Data datalist) {
        super(fm);
        //Initializing tab count
        this.tabCount = tabCount;
        this.datalist = datalist;
        this.friendsUserLists = (ArrayList<GetAllUserFriendlist.FriendsUserList>) datalist.getFriendsUserList();
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        //Returning the current tabs
        switch (position) {
            case 0:
                bundle = new Bundle();
                bundle.putSerializable("allfriends_list", friendsUserLists);
                bundle.putSerializable("bfflist", (Serializable) datalist.getBff());
                fragment = new FriendsAllFragment();
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                Search TAB2 = new Search();
                return TAB2;
            case 2:
                bundle = new Bundle();
                bundle.putSerializable("allfriends_list", friendsUserLists);
                bundle.putSerializable("bfflist", (Serializable) datalist.getBff());
                fragment = new BFFs();
                fragment.setArguments(bundle);
                return fragment;
            case 3:
                bundle = new Bundle();
                bundle.putSerializable("requestlist", (Serializable) datalist.getPendingUserList());
                bundle.putSerializable("allfriends_list", friendsUserLists);
                fragment = new RequestFrament();
                fragment.setArguments(bundle);
                return fragment;

            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }

}
