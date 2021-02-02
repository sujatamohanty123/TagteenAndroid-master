package in.tagteen.tagteen.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import in.tagteen.tagteen.Fragments.GalleryFragment;


public class galleryAdapter  extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public galleryAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                GalleryFragment tab1 = new GalleryFragment();
                return tab1;
            case 1:
                GalleryFragment tab2 = new GalleryFragment();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}