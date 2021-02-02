package in.tagteen.tagteen.profile;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

class FragmentPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {

    interface Provider {
        Fragment provide();
    }

    static class Item {

        final String name;
        final Provider provider;

        Item(String name, Provider provider) {
            this.name = name;
            this.provider = provider;
        }
    }

    private final List<Item> mItems;

    FragmentPagerAdapter(FragmentManager fm, List<Item> items) {
        super(fm);
        mItems = items;
    }

    @Override
    public Fragment getItem(int position) {
        return mItems.get(position).provider.provide();
    }

    @Override
    public int getCount() {
        return mItems != null
                ? mItems.size()
                : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mItems.get(position).name;
    }
}
