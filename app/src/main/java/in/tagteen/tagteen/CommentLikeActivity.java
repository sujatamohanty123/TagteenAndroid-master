package in.tagteen.tagteen;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;


import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import in.tagteen.tagteen.Adapters.MainAdapter;
import in.tagteen.tagteen.Model.GetReactionModel;
import in.tagteen.tagteen.Model.ReactionInputJson;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommentLikeActivity extends AppCompatActivity {
    PagerSlidingTabStrip tabs;
    ViewPager pager;
    MainAdapter adapter;
    int flag;
    String postid;
    int keypadshow=0;
    LinearLayout linearLayout;
    ArrayList<ViewPagerTab> tabsList= new ArrayList<>();
    int commentCount=0,reactCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_comment);

        Bundle bundle = getIntent().getExtras();
        flag = bundle.getInt("comment_select_flag");
        keypadshow = bundle.getInt("keypadshow");
        postid = bundle.getString("postid");


        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.viewpager);

        linearLayout=(LinearLayout) findViewById(R.id.linearLayout);
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(keypadshow==1){
            inputMethodManager.showSoftInput(linearLayout,InputMethodManager.SHOW_FORCED);
        }else{
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
        }

        callApireactcounter();

        tabsList.add(new ViewPagerTab(R.drawable.ic_svg_comment,+ commentCount + "Comments", 0));
        tabsList.add(new ViewPagerTab(R.drawable.ic_svg_cool_select,+ reactCount + " Reacts", 1));
        FragmentActivity activity = (FragmentActivity) (this);
        FragmentManager fm = activity.getSupportFragmentManager();
        adapter = new MainAdapter(fm, tabsList, postid);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        if (flag == 0) {
            pager.setCurrentItem(0);
        } else {
            pager.setCurrentItem(1);
        }

    }

    public  void  callApireactcounter() {
        ReactionInputJson reactjsoninputmodel=new ReactionInputJson();
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        reactjsoninputmodel.setPost_id(postid);
        Call<GetReactionModel> call =methods.get_reacts(reactjsoninputmodel, token);
        call.enqueue(new Callback<GetReactionModel>() {
            @Override
            public void onResponse(Call<GetReactionModel> call, Response<GetReactionModel> response) {
                int statuscode = response.code();
                if (response.code() == 200) {
                    GetReactionModel list = response.body();

                    commentCount=list.getData().getTotal_count();
                    reactCount=list.getData().getTotal_count();


                } else if (response.code() == 401) {
                    Utils.showShortToast(CommentLikeActivity.this, Constants.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Call<GetReactionModel> call, Throwable t) {
                Utils.showShortToast(CommentLikeActivity.this, Constants.SERVER_ERROR);
            }
        });
    }
}
