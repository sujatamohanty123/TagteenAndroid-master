package in.tagteen.tagteen.Adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import in.tagteen.tagteen.Fragments.LikeCountFragment;
import in.tagteen.tagteen.Fragments.CommentsFragment;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.ViewPagerTab;

/**
 * Created by qq on 22.06.2015.
 */
public class MainAdapter extends FragmentPagerAdapter
        implements PagerSlidingTabStrip.CustomTabProvider {

    ArrayList<ViewPagerTab> tabs=new ArrayList<>();
    String postid;

    public MainAdapter(FragmentManager fm, ArrayList<ViewPagerTab> tabs, String postid) {
        super(fm);
        this.tabs = tabs;
        this.postid = postid;
    }

    @Override
    public View getCustomTabView(ViewGroup viewGroup, int i) {
        LinearLayout tabLayout = (LinearLayout)
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tab_layout, null);

        ImageView tabTitle = (ImageView) tabLayout.findViewById(R.id.img);
        TextView badge = (TextView) tabLayout.findViewById(R.id.txt);

        ViewPagerTab tab = tabs.get(i);

        tabTitle.setImageResource(tab.icon);
        badge.setText(tab.notifications);
        return tabLayout;
    }

    @Override
    public void tabSelected(View view) {
       /* LinearLayout tabLayout = (LinearLayout) view;
        TextView badge = (TextView) tabLayout.findViewById(R.id.txt);
        badge.setVisibility(View.GONE);*/
    }

    @Override
    public void tabUnselected(View view) {

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle bundle;
        switch (position) {
            case 0:
                fragment = new CommentsFragment();
                bundle = new Bundle();
                bundle.putString("postid", postid);
                fragment.setArguments(bundle);
                return fragment;

            case 1:
                fragment = new LikeCountFragment();
                bundle = new Bundle();
                bundle.putString("postid", postid);
                fragment.setArguments(bundle);
                return fragment;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}