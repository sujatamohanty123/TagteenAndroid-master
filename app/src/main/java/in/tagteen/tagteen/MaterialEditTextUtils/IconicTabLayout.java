package in.tagteen.tagteen.MaterialEditTextUtils;

import android.content.Context;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

/**
 * Created by user on 05-04-2017.
 */

public class IconicTabLayout extends TabLayout {

    private TabViewProvider mTabViewProvider;

    public interface TabViewProvider {
        Tab newTabInstance(int tabPosition);
    }

    public IconicTabLayout(Context context) {
        super(context);
    }

    public IconicTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconicTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTabProvider(TabViewProvider provider){
        mTabViewProvider = provider;
    }

    @Override
    public void setupWithViewPager(ViewPager viewPager) {
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        } else {
            this.setTabsFromPagerAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this));
            this.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        }

    }

    @Override
    public void setTabsFromPagerAdapter(PagerAdapter adapter) {
        removeAllTabs();
        mTabViewProvider = mTabViewProvider == null && adapter instanceof TabViewProvider? (TabViewProvider)adapter : null;
        if(mTabViewProvider == null){
            super.setTabsFromPagerAdapter(adapter);
        }else{
            for (int i = 0, count = adapter.getCount(); i < count; ++i) {
                this.addTab(mTabViewProvider.newTabInstance(i));
            }

        }
    }
}