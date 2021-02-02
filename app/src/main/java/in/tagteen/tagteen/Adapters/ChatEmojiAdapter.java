package in.tagteen.tagteen.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import in.tagteen.tagteen.Fragments.ProfileFragment;
import in.tagteen.tagteen.emoji.EmojiconGridFragment;


public class ChatEmojiAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ChatEmojiAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                EmojiconGridFragment tab1 = new EmojiconGridFragment();
                return tab1;
            case 1:
                ProfileFragment tab2 = new ProfileFragment();
                return tab2;
            case 2:
                ProfileFragment tab3 = new ProfileFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}