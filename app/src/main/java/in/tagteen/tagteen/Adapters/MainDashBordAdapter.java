package in.tagteen.tagteen.Adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import in.tagteen.tagteen.ChatDashBoardActivity;
import in.tagteen.tagteen.Fragments.FriendsListFragment;
import in.tagteen.tagteen.Fragments.GroupListFragment;

import in.tagteen.tagteen.Fragments.HomeFragment_new;
import in.tagteen.tagteen.R;



public class MainDashBordAdapter extends FragmentStatePagerAdapter  {

    Context context;
    int mIconsList[]={R.drawable.home,R.drawable.selfie,
            R.drawable.notification,R.drawable.profilr};
    String mTabList[]={"CHATS","GROUPS","FRIENDS"};

    public MainDashBordAdapter(FragmentManager fm,Context context) {
      super(fm);
        this.context=context;
    }


    @Override
    public Fragment getItem(int position) {

        switch ( position) {
            case 0:
                HomeFragment_new tab1 = new HomeFragment_new();
                return tab1;

            case 1:
                ChatDashBoardActivity tab2 = new ChatDashBoardActivity();
                return tab2;

          /*  case 2:
                SelfiFragment tab3=new SelfiFragment();
                return tab3;*/

            case 2:
                GroupListFragment tab3 = new GroupListFragment();
                return tab3;

            case 3:
                FriendsListFragment tab4=new FriendsListFragment();
                return tab4;

        }
        return null ;
    }
    @Override
    public int getCount() {
        return 4;
    }
}