package in.tagteen.tagteen.Adapters;


import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import in.tagteen.tagteen.Fragments.HomeFragment_new;
import in.tagteen.tagteen.Fragments.youthtube.LanguagesFragment;
import in.tagteen.tagteen.Fragments.youthtube.LatestShowroomFragment;
import in.tagteen.tagteen.Fragments.youthtube.PopularVideosFragment;
import in.tagteen.tagteen.Fragments.youthtube.RockstarsFragment;
import in.tagteen.tagteen.Fragments.youthtube.SupportingShowroomFragment;
import in.tagteen.tagteen.Fragments.youthtube.TrendingFragment;
import in.tagteen.tagteen.Fragments.youthtube.WebShowsActivity;
import in.tagteen.tagteen.Fragments.youthtube.YouthTubeFeed;
import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;

public class HomePageAdapter  extends FragmentStatePagerAdapter {
    //private MomentsFeed momentsFeed;
    //private TeenFeedFragment teenFeedFragment;
    private TrendingFragment trendingFragment;
    private PopularVideosFragment popularVideosFragment;
    private YouthTubeFeed youthTubeFeed;
    private LatestShowroomFragment latestShowroomFragment;
    private WebShowsActivity webshowsActivity;
    private RockstarsFragment rockstarsFragment;
    private SupportingShowroomFragment supportingShowroomFragment;
    private LanguagesFragment languagesFragment;

    private OnPostDeleteListener onPostDeleteListener;

    public HomePageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setOnPostDeleteListener(OnPostDeleteListener onPostDeleteListener) {
        this.onPostDeleteListener = onPostDeleteListener;
        /*if (this.momentsFeed != null) {
            this.momentsFeed.setOnPostDeleteListener(this.onPostDeleteListener);
        }*/
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            /*case HomeFragment_new.TAB_INDEX_TRENDING:
                if (this.trendingFragment == null) {
                    this.trendingFragment = new TrendingFragment();
                }
                return this.trendingFragment;*/

            case HomeFragment_new.TAB_INDEX_POPULAR:
                if (this.popularVideosFragment == null) {
                    this.popularVideosFragment = new PopularVideosFragment();
                }
                return this.popularVideosFragment;

            case HomeFragment_new.TAB_INDEX_SHOWROOM:
                if (this.youthTubeFeed == null) {
                    this.youthTubeFeed = new YouthTubeFeed();
                }
                return this.youthTubeFeed;

            case HomeFragment_new.TAB_INDEX_LATEST:
                if (this.latestShowroomFragment == null) {
                    this.latestShowroomFragment = new LatestShowroomFragment();
                }
                return this.latestShowroomFragment;

            /*case HomeFragment_new.TAB_INDEX_WEBSHOWS:
                if (this.webshowsActivity == null) {
                    this.webshowsActivity = new WebShowsActivity();
                }
                return this.webshowsActivity;*/

            case HomeFragment_new.TAB_INDEX_ROCKSTARS:
                if (this.rockstarsFragment == null) {
                    this.rockstarsFragment = new RockstarsFragment();
                }
                return this.rockstarsFragment;

            /*case HomeFragment_new.TAB_INDEX_SUPPORTING:
                if (this.supportingShowroomFragment == null) {
                    this.supportingShowroomFragment = new SupportingShowroomFragment();
                }
                return this.supportingShowroomFragment;*/

            case HomeFragment_new.TAB_INDEX_LANGUAGES:
                if (this.languagesFragment == null) {
                    this.languagesFragment = new LanguagesFragment();
                }
                return this.languagesFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return HomeFragment_new.TAB_COUNT;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, int position) {
        switch (position) {
            case HomeFragment_new.TAB_INDEX_POPULAR:
                if (this.popularVideosFragment != null) {
                    this.popularVideosFragment.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case HomeFragment_new.TAB_INDEX_SHOWROOM:
                if (this.youthTubeFeed != null) {
                    this.youthTubeFeed.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case HomeFragment_new.TAB_INDEX_LATEST:
                if (this.latestShowroomFragment != null) {
                    this.latestShowroomFragment.onActivityResult(requestCode, resultCode, data);
                }
                break;
            /*case HomeFragment_new.TAB_INDEX_WEBSHOWS:
                if (this.webshowsActivity != null) {
                    this.webshowsActivity.onActivityResult(requestCode, resultCode, data);
                }
                break;*/
            case HomeFragment_new.TAB_INDEX_ROCKSTARS:
                if (this.rockstarsFragment != null) {
                    this.rockstarsFragment.onActivityResult(requestCode, resultCode, data);
                }
                break;
            /*case HomeFragment_new.TAB_INDEX_SUPPORTING:
                if (this.supportingShowroomFragment != null) {
                    this.supportingShowroomFragment.onActivityResult(requestCode, resultCode, data);
                }
                break;*/
        }
    }

    public void expandOptions(int position) {
        switch (position) {
            case HomeFragment_new.TAB_INDEX_SHOWROOM:
                if (this.youthTubeFeed != null) {
                    this.youthTubeFeed.showCategories();
                }
                break;
        }
    }

    public void collapseOptions(int position) {
        switch (position) {
            case HomeFragment_new.TAB_INDEX_SHOWROOM:
                if (this.youthTubeFeed != null) {
                    this.youthTubeFeed.hideCategories();
                }
                break;
        }
    }
}


