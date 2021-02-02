package in.tagteen.tagteen.Adapters;


import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import in.tagteen.tagteen.Fragments.ChatListFragment;
import in.tagteen.tagteen.Fragments.FriendsListFragment;
import in.tagteen.tagteen.Fragments.GroupListFragment;


public class DashBoardAdapter  extends FragmentStatePagerAdapter {

Activity activity;
    Context context;

    public DashBoardAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {

        switch ( position) {
            case 0:
                ChatListFragment tab1 = new ChatListFragment();
                return tab1;
            case 1:
                GroupListFragment tab2 = new GroupListFragment();
                return tab2;
            case 2:
                FriendsListFragment tab3=new FriendsListFragment();
                return tab3;

        }
        return null ;
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {

            case 0: return "CHATS";
            case 1: return "GROUPS";
            case 2: return "FRIENDS";
            default: return null;
        }
    }
}



