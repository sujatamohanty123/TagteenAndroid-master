package in.tagteen.tagteen.Fragments;


import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikeCountFragment extends Fragment {
    private static final String TAG = "LikeCountFragment";
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    public String postid, type, react_type;
    private TextView text_allcount, text_coolcount, text_heartcount, text_swegcount, text_nerdcount, text_dabcount;
    private int coolcount=0, heartcount=0, swegcount=0, nerdcount=0, dabcount=0, reactCount=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_like_count, container, false);
        Bundle bundle = getArguments();
        postid = bundle.getString("postid");
        type= bundle.getString("type");
        react_type= bundle.getString("react_type");
        this.reactCount = bundle.getInt(Constants.REACTS_COUNT);
        this.heartcount = bundle.getInt(Constants.LIKE_COUNT);
        this.coolcount = bundle.getInt(Constants.COOL_COUNT);
        this.swegcount = bundle.getInt(Constants.SWEG_COUNT);
        this.nerdcount = bundle.getInt(Constants.NERD_COUNT);
        this.dabcount = bundle.getInt(Constants.DAB_COUNT);

        //this.callApireactcounter();

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager1);
        this.setupViewPager(mViewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        this.setupTabIcons();
        this.text_allcount.setText("All("+reactCount+")");


        if (react_type.equalsIgnoreCase("cool")){
            mViewPager.setCurrentItem(2);
        }
        if (react_type.equalsIgnoreCase("dab")){
            mViewPager.setCurrentItem(3);
        }
        if (react_type.equalsIgnoreCase("nerd")){
            mViewPager.setCurrentItem(4);
        }
        if (react_type.equalsIgnoreCase("swag")){
            mViewPager.setCurrentItem(5);
        }
        if (react_type.equalsIgnoreCase("like")){
            mViewPager.setCurrentItem(1);
        }
        return view;
    }

    /*public  void  callApireactcounter() {
        ReactionInputJson reactjsoninputmodel=new ReactionInputJson();
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        reactjsoninputmodel.setPost_id(postid);
        Call<GetReactionModel> call = methods.get_reacts(reactjsoninputmodel, token);

        call.enqueue(new Callback<GetReactionModel>() {
            @Override
            public void onResponse(Call<GetReactionModel> call, Response<GetReactionModel> response) {
                int statuscode = response.code();
                if (response.code() == 200) {
                    GetReactionModel list = response.body();
                    coolcount = list.getData().getCool_count();
                    swegcount = list.getData().getSwag_count();
                    dabcount = list.getData().getDab_count();
                    nerdcount = list.getData().getNerd_count();
                    heartcount = list.getData().getLike_count();

                    text_coolcount.setText(" "+coolcount );
                    text_swegcount.setText(" "+swegcount );
                    text_dabcount.setText(" "+dabcount );
                    text_nerdcount.setText(" "+nerdcount );
                    text_heartcount.setText(" "+heartcount );
                } else if (response.code() == 401) {

                }
            }

            @Override
            public void onFailure(Call<GetReactionModel> call, Throwable t) {
                Log.d(TAG,"Failed : "+call.request().url().toString());
            }
        });
    }*/

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter_react adapter = new ViewPagerAdapter_react(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        LinearLayout tabLayout_linear = (LinearLayout)
                LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout, null);
        ImageView tab1Title= (ImageView) tabLayout_linear.findViewById(R.id.img);
        text_allcount = (TextView) tabLayout_linear.findViewById(R.id.txt);
        //tab1Title.setImageResource(R.drawable.ic_svg_cool_select);
        tab1Title.setVisibility(View.GONE);
        text_allcount.setText("All");
        tabLayout.getTabAt(0).setCustomView(tabLayout_linear);

        LinearLayout tabLayout_linear1 = (LinearLayout)
                LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout, null);
        ImageView tab1Title1 = (ImageView) tabLayout_linear1.findViewById(R.id.img);
        text_coolcount = (TextView) tabLayout_linear1.findViewById(R.id.txt);
        tab1Title1.setImageResource(R.drawable.svg_cool_emoji);
        text_coolcount.setText(" "+coolcount);
        tabLayout.getTabAt(2).setCustomView(tabLayout_linear1);

        LinearLayout tabLayout_linear2 = (LinearLayout)
                LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout, null);
        ImageView tab1Title2 = (ImageView) tabLayout_linear2.findViewById(R.id.img);
        text_heartcount = (TextView) tabLayout_linear2.findViewById(R.id.txt);
        tab1Title2.setImageResource(R.drawable.ic_svg_heart);
        text_heartcount.setText(" "+heartcount);
        tabLayout.getTabAt(1).setCustomView(tabLayout_linear2);

        LinearLayout tabLayout_linear3 = (LinearLayout)
                LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout, null);
        ImageView tab1Title3 = (ImageView) tabLayout_linear3.findViewById(R.id.img);
        text_swegcount = (TextView) tabLayout_linear3.findViewById(R.id.txt);
        tab1Title3.setImageResource(R.drawable.svg_swag_emoji);
        text_swegcount.setText(" "+swegcount);
        tabLayout.getTabAt(5).setCustomView(tabLayout_linear3);

        LinearLayout tabLayout_linear4 = (LinearLayout)
                LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout, null);
        ImageView tab1Title4= (ImageView) tabLayout_linear4.findViewById(R.id.img);
        text_nerdcount = (TextView) tabLayout_linear4.findViewById(R.id.txt);
        tab1Title4.setImageResource(R.drawable.ic_nerd);
        text_nerdcount.setText(" "+nerdcount);
        tabLayout.getTabAt(4).setCustomView(tabLayout_linear4);

        LinearLayout tabLayout_linear5 = (LinearLayout)
                LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout, null);
        ImageView tab1Title5 = (ImageView) tabLayout_linear5.findViewById(R.id.img);
        text_dabcount = (TextView) tabLayout_linear5.findViewById(R.id.txt);
        tab1Title5.setImageResource(R.drawable.svg_dab_emoji);
        text_dabcount.setText(" "+dabcount);
        tabLayout.getTabAt(3).setCustomView(tabLayout_linear5);
    }

    class ViewPagerAdapter_react extends FragmentPagerAdapter {
        public ViewPagerAdapter_react(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle bundle;
            switch (position) {
                case 0:
                    fragment = new AllUserAction().newInstance(position,postid, type);
                    break;

                case 1:
                    fragment = new AllUserAction().newInstance(position,postid, type);
                    break;

                case 2:
                    fragment = new AllUserAction().newInstance(position,postid, type);
                    break;

                case 3:
                    fragment = new AllUserAction().newInstance(position,postid, type);
                    break;

                case 4:
                    fragment = new AllUserAction().newInstance(position,postid, type);
                    break;
                case 5:
                    fragment = new AllUserAction().newInstance(position,postid, type);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}