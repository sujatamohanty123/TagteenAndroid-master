package in.tagteen.tagteen;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.tagteen.tagteen.Fragments.AllUserAction;
import in.tagteen.tagteen.Fragments.LikeCountFragment;
import in.tagteen.tagteen.Fragments.CommentsFragment;
import in.tagteen.tagteen.Model.GetReactionModel;
import in.tagteen.tagteen.Model.ReactionInputJson;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.emoji.Emojicon;
import in.tagteen.tagteen.emoji.EmojiconGridFragment;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentLikeActivity_new extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, ChatTreadActivity.OnEmojiconBackspaceClickedListener{
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private int commentCount = 0, reactCount = 0;
    private int flag;
    private String postid, type, react_type;
    private int keypadshow = 0;
    private boolean blockReacts = false;
    private LinearLayout linearLayout;
    private TextView tab1,tab2;
    private LinearLayout progress;
    private SectionDataModel selectedModel;
    private CommentsFragment commentsFragment;

    private static final String TAG = "CommentLikeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_like_new);

        Bundle bundle = getIntent().getExtras();
        flag = bundle.getInt(Constants.SELECT_COMMENT_LIKE);
        keypadshow = bundle.getInt(Constants.SHOW_KEYPAD);
        postid = bundle.getString(Constants.POST_ID);
        type = bundle.getString("type");
        react_type = bundle.getString("react_type");
        this.commentCount = bundle.getInt(Constants.COMMENTS_COUNT);
        this.reactCount = bundle.getInt(Constants.REACTS_COUNT);
        this.blockReacts = bundle.getBoolean(Constants.BLOCK_REACTS, false);
        this.selectedModel = (SectionDataModel) bundle.getSerializable(Constants.SELECTED_MODEL);

        progress = (LinearLayout) findViewById(R.id.layoutProgress);
        linearLayout=(LinearLayout) findViewById(R.id.linearLayout);
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (keypadshow==1){
            inputMethodManager.showSoftInput(linearLayout,InputMethodManager.SHOW_FORCED);
        } else {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
        }

        this.loadData();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        this.setupTabIcons();

        if (flag == 0) {
            mViewPager.setCurrentItem(0);
        } else {
            mViewPager.setCurrentItem(1);
        }
        this.progress.setVisibility(View.GONE);
    }

    private void loadData() {
        this.progress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getReactsCount();
            }
        }, 200);
    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        LinearLayout tabLayout_linear = (LinearLayout)
                LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        ImageView tab1Title = (ImageView) tabLayout_linear.findViewById(R.id.img);
        tab1 = (TextView) tabLayout_linear.findViewById(R.id.txt);
        if (type != null && type.equals(Constants.POST_TYPE_CAMPUSLIVE)) {
            tab1Title.setImageResource(R.drawable.ic_reply_icon);
            tab1Title.setColorFilter(
                    ContextCompat.getColor(
                            CommentLikeActivity_new.this, R.color.blue_300), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            tab1Title.setImageResource(R.drawable.ic_svg_comment);
        }
        if (commentCount == 1 || commentCount == 0) {
            if (type != null && type.equals(Constants.POST_TYPE_CAMPUSLIVE)) {
                tab1.setText(commentCount + " Reply");
            } else {
                tab1.setText(commentCount + " Comment");
            }
        } else {
            if (type != null && type.equals(Constants.POST_TYPE_CAMPUSLIVE)) {
                tab1.setText(commentCount + " Replies");
            } else {
                tab1.setText(commentCount + " Comments");
            }
        }
        tabLayout.getTabAt(0).setCustomView(tabLayout_linear);

        if (this.blockReacts == true) {
            return;
        }
        LinearLayout tabLayout_linear2 = (LinearLayout)
                LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        ImageView tab2Title = (ImageView) tabLayout_linear2.findViewById(R.id.img);
        tab2 = (TextView) tabLayout_linear2.findViewById(R.id.txt);

        if (type.equalsIgnoreCase("moment")) {
            tab2Title.setImageResource(R.drawable.ic_svg_cool_select);
            if (reactCount == 1 || reactCount == 0) {
                tab2.setText(" " + reactCount + " React");
            } else{
                tab2.setText(" " + reactCount + " Reacts");
            }
        } else if (type.equalsIgnoreCase("teenfeed")) {
            tab2Title.setImageResource(R.drawable.ic_light_bulb_activated);
            tab2.setText(" " + reactCount + " Helpful");
        } else if(type.equalsIgnoreCase("showroom")){
            tab2Title.setImageResource(R.drawable.ic_bottom_youthube);
            tab2Title.setColorFilter(ContextCompat.getColor(CommentLikeActivity_new.this, R.color.red_600), android.graphics.PorterDuff.Mode.SRC_IN);
            tab2.setText(" " + reactCount + " U rocked");
        }
        tabLayout.getTabAt(1).setCustomView(tabLayout_linear2);
    }

    private void getReactsCount() {
        ReactionInputJson reactjsoninputmodel=new ReactionInputJson();
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        reactjsoninputmodel.setPost_id(postid);
        Call<GetReactionModel> call = methods.get_reacts(reactjsoninputmodel, token);

        call.enqueue(new Callback<GetReactionModel>() {
            @Override
            public void onResponse(Call<GetReactionModel> call, Response<GetReactionModel> response) {
                int statuscode = response.code();
                if (response.code() == 200) {
                    GetReactionModel model = response.body();
                    if (model != null && model.getData() != null) {
                        commentCount = model.getData().getComment_count();
                        reactCount = model.getData().getTotal_count();
                    }
                } else if (response.code() == 401) {

                }
            }

            @Override
            public void onFailure(Call<GetReactionModel> call, Throwable t) {
                Log.d(TAG,"Failed : "+call.request().url().toString() + " post " + postid);
                t.printStackTrace();
            }
        });
    }

    private void setCounts() {
        if (commentCount == 1 || commentCount == 0) {
            if (type != null && type.equals(Constants.POST_TYPE_CAMPUSLIVE)) {
                tab1.setText(commentCount + " Reply");
            } else {
                tab1.setText(commentCount + " Comment");
            }
        } else {
            if (type != null && type.equals(Constants.POST_TYPE_CAMPUSLIVE)) {
                tab1.setText(commentCount + " Replies");
            } else {
                tab1.setText(commentCount + " Comments");
            }
        }

        if (type.equalsIgnoreCase("moment")) {
            if (reactCount == 1 || reactCount == 0) {
                tab2.setText(" " + reactCount + " React");
            } else{
                tab2.setText(" " + reactCount + " Reacts");
            }
        } else if (type.equalsIgnoreCase("teenfeed")) {
            tab2.setText(" " + reactCount + " Helpful");
        } else if(type.equalsIgnoreCase("showroom")){
            tab2.setText(" " + reactCount + " U rocked");
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle bundle;
            switch (position) {
                case 0:
                    commentsFragment = new CommentsFragment();
                    bundle = new Bundle();
                    bundle.putString("postid", postid);
                    if (type != null && type.equals(Constants.POST_TYPE_CAMPUSLIVE)) {
                        bundle.putString(Constants.COMMENTS_TYPE, Constants.POST_TYPE_CAMPUSLIVE);
                    }
                    commentsFragment.setArguments(bundle);
                    commentsFragment.setCommentFragmentListener(new CommentsFragment.CommentFragmentListener() {
                        @Override
                        public void setCount(int count) {
                            if (count == 1 || count == 0) {
                                if (type != null && type.equals(Constants.POST_TYPE_CAMPUSLIVE)) {
                                    tab1.setText(count + " Reply");
                                } else {
                                    tab1.setText(count + " Comment");
                                }
                            } else {
                                if (type != null && type.equals(Constants.POST_TYPE_CAMPUSLIVE)) {
                                    tab1.setText(count + " Replies");
                                } else {
                                    tab1.setText(count + " Comments");
                                }
                            }
                            if (selectedModel != null) {
                                selectedModel.setCommentcount(count);
                            }
                        }
                    });
                    return commentsFragment;

                case 1:
                    if (type.equalsIgnoreCase("moment")) {
                        fragment = new LikeCountFragment();
                        bundle = new Bundle();
                        bundle.putString("postid", postid);
                        bundle.putString("type", type);
                        bundle.putString("react_type", react_type);
                        bundle.putInt(Constants.REACTS_COUNT, reactCount);
                        if (selectedModel != null) {
                            bundle.putInt(Constants.LIKE_COUNT, selectedModel.getLikecount());
                            bundle.putInt(Constants.COOL_COUNT, selectedModel.getCoolcount());
                            bundle.putInt(Constants.SWEG_COUNT, selectedModel.getSwegcount());
                            bundle.putInt(Constants.NERD_COUNT, selectedModel.getNerdcount());
                            bundle.putInt(Constants.DAB_COUNT, selectedModel.getDabcount());
                        }
                        fragment.setArguments(bundle);
                        return fragment;
                    } else if(type.equalsIgnoreCase("teenfeed")|| type.equalsIgnoreCase("showroom")){
                        fragment = new AllUserAction().newInstance(position, postid, type);
                        return fragment;
                    }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            if (blockReacts) {
                return 1;
            }
            return 2;
        }
    }

    @Override
    public void onBackPressed() {
        if (this.commentsFragment != null) {
            if (this.commentsFragment.isEmojiKeypadOpen()) {
                return;
            }
        }
        if (this.selectedModel != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.SELECTED_MODEL, this.selectedModel);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        if (commentsFragment != null) {
            commentsFragment.onEmojiconClicked(emojicon);
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        if (commentsFragment != null) {
            commentsFragment.onEmojiconBackspaceClicked(v);
        }
    }
}
