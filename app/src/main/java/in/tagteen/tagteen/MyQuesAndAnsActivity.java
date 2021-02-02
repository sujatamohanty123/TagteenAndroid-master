package in.tagteen.tagteen;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import in.tagteen.tagteen.Fragments.MyAnswersFragment;
import in.tagteen.tagteen.Fragments.MyQuestionsFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class MyQuesAndAnsActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private TextView lblMyQuestions, lblMyAnswers;
    private LinearLayout layoutProgress;

    private String userId;

    private static final String TAG = "MyQuesAndAnsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ques_ans);

        this.initWidgets();
        this.loadData();
    }

    private void initWidgets() {
        this.userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

        this.layoutProgress = findViewById(R.id.layoutProgress);
        this.mViewPager = findViewById(R.id.viewpager);
        this.setupViewPager(this.mViewPager);

        this.tabLayout = findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(this.mViewPager);
        this.setupTabIcons();
    }

    private void loadData() {
        this.layoutProgress.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //getReactsCount();
            }
        }, 200);
    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        LinearLayout tabLayout1 = (LinearLayout)
                LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        ImageView tabImg = tabLayout1.findViewById(R.id.img);
        tabImg.setVisibility(View.GONE);
        this.lblMyQuestions = tabLayout1.findViewById(R.id.txt);
        this.lblMyQuestions.setText("My Questions");

        this.tabLayout.getTabAt(0).setCustomView(tabLayout1);

        LinearLayout tabLayout2 = (LinearLayout)
                LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        tabImg = tabLayout2.findViewById(R.id.img);
        tabImg.setVisibility(View.GONE);
        this.lblMyAnswers = tabLayout2.findViewById(R.id.txt);
        this.lblMyAnswers.setText("My Answers");


        this.tabLayout.getTabAt(1).setCustomView(tabLayout2);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new MyQuestionsFragment();
                    break;

                case 1:
                    fragment = new MyAnswersFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
