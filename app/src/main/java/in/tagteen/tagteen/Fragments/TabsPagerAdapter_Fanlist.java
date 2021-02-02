package in.tagteen.tagteen.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import in.tagteen.tagteen.profile.FanList;

public class TabsPagerAdapter_Fanlist extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    Bundle bundle;
    String login_usr_id;
    public
    TabsPagerAdapter_Fanlist(FragmentManager fm, int tabCount, String login_usr_id) {
        super(fm);
        this.tabCount = tabCount;
        this.login_usr_id = login_usr_id;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        //Returning the current tabs
        switch (position) {
            case 0:
                bundle = new Bundle();
                bundle.putString("position", "bottom_tab");
                bundle.putString("flag", "1");
                bundle.putString("tittle", "My supporters");
                bundle.putString("user_id", login_usr_id);
                fragment = new FanList();
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                bundle = new Bundle();
                bundle.putString("position", "bottom_tab");
                bundle.putString("flag", "2");
                bundle.putString("tittle", "I am supporting");
                bundle.putString("user_id", login_usr_id);
                fragment = new FanList();
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
