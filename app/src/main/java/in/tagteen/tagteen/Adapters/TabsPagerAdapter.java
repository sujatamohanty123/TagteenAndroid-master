package in.tagteen.tagteen.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;
    //Constructor to the class
    public TabsPagerAdapter(FragmentManager fm, int tabCount)
    {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position)
    {
        //Returning the current tabs
        switch (position) {
           /* case 0:
                AboutFragment TAB1 = new AboutFragment();
                return TAB1;
            case 1:
                int Flag=1;
                MomentsFeed TAB2 = new MomentsFeed(0);
                return TAB2;
            case 2:
                ProfilePhotosFragment TAB3 = new ProfilePhotosFragment();
                return TAB3;
            case 3:
                ProfileVideoFragment TAB4 = new ProfileVideoFragment();
                return TAB4;*/

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
