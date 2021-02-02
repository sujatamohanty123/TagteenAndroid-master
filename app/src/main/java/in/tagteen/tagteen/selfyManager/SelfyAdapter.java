package in.tagteen.tagteen.selfyManager;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;


/**
 * Created by BSETEC on 15-12-2016.
 */

public class SelfyAdapter extends FragmentPagerAdapter {
    Context mContext;

    //Constructor to the class
    public SelfyAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                return new CustomVideo("");
            default:
                return null;
        }
    }


    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
