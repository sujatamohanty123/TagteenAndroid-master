package in.tagteen.tagteen.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.Adapters.HomePageAdapter;
import in.tagteen.tagteen.CreatePostActivity_Keypadheight;
import in.tagteen.tagteen.Fragments.youthtube.VideoGallery;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.utils.AndroidPermissionMarshMallo;


public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener{
    Animation alpha;
    TabLayout tabLayout;
    ImageView imageView;
    public ImageView fabAdd;
    private AndroidPermissionMarshMallo permissionMarshMallo;
    String fragmentload="";
    public HomeFragment(String fragmentload) {
        this.fragmentload=fragmentload;
    }

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        permissionMarshMallo = new AndroidPermissionMarshMallo(getActivity());
        HomePageAdapter mSectionsPagerAdapter = new HomePageAdapter(getActivity().getSupportFragmentManager());
        final ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.home_viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs_home);
        fabAdd = (ImageView) rootView.findViewById(R.id.fabAdd);
        tabLayout.setupWithViewPager(mViewPager);
        createTabIcons();
        mViewPager.addOnPageChangeListener(this);
        if(fragmentload.equalsIgnoreCase("teenfeedfragment")){
            mViewPager.setCurrentItem(1);
        }
       /* MainDashboardActivity.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() == 0) {
                    Intent it = new Intent(getActivity(), SearchActivity.class);
                    startActivity(it);
                } else if (mViewPager.getCurrentItem() == 1) {
                    Intent it = new Intent(getActivity(), SearchArticleActivity.class);
                    startActivity(it);
                } else if (mViewPager.getCurrentItem() == 2) {
                    Intent it = new Intent(getActivity(), SearchShowRoomActivity.class);
                    startActivity(it);
                }

            }
        });*/

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() == 2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!permissionMarshMallo.checkPermissionForCamera()) {
                            permissionMarshMallo.requestPermissionForCamera();
                        } else if (!permissionMarshMallo.checkPermissionForRecord()) {
                            permissionMarshMallo.requestPermissionForRecord();
                        } else {
                            if (!permissionMarshMallo.checkPermissionForExternalStorage()) {
                                permissionMarshMallo.requestPermissionForExternalStorage();
                            } else if (!permissionMarshMallo.checkPermissionForRecord()) {
                                permissionMarshMallo.requestPermissionForRecord();
                            } else {
                                gotoVideoList();
                            }
                        }
                    } else {
                        gotoVideoList();
                    }
                } else if (mViewPager.getCurrentItem() == 0) {
                   // Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                    //Intent intent = new Intent(getActivity(), CreatePostActivity_New.class);
                    Intent intent = new Intent(getActivity(), CreatePostActivity_Keypadheight.class);
                    startActivityForResult(intent, 2);
                } else {
                    /*Intent intent = new Intent(getActivity(), PostAnArticle.class);
                    startActivityForResult(intent, 2);*/
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fabAdd.setVisibility(View.VISIBLE);
                    fabAdd.setImageResource(R.drawable.ic_svg_post);
                    //MainDashboardActivity.searchButton.setImageResource(R.drawable.mdb_search);
                } else if (position == 1) {
                    //fabAdd.setImageResource(R.drawable.ic_svg_write);
                    fabAdd.setVisibility(View.GONE);
                    //MainDashboardActivity.searchButton.setImageResource(R.drawable.mdb_search_blue);
                } else if (position == 2) {
                    fabAdd.setVisibility(View.VISIBLE);
                    fabAdd.setImageResource(R.drawable.ic_svg_video);
                    //MainDashboardActivity.searchButton.setImageResource(R.drawable.mdb_search_green);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }

    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setText("MOMENTS");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabTwo.setText("TEEN FEED");
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabThree.setText("SHOWROOM");
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void gotoVideoList() {
        Intent gotoVideogallery = new Intent(getActivity(), VideoGallery.class);
        startActivity(gotoVideogallery);
    }
}
