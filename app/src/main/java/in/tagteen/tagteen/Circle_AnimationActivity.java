package in.tagteen.tagteen;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.github.jlmd.animatedcircleloadingview.animator.AnimationState;
import com.github.jlmd.animatedcircleloadingview.animator.ViewAnimator;
import com.github.jlmd.animatedcircleloadingview.component.finish.FinishedOkView;

import in.tagteen.tagteen.Fragments.beans.FileDataSender;
import in.tagteen.tagteen.Interfaces.OnFileUploadListener;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.utils.FileUploadHelper;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class Circle_AnimationActivity extends AppCompatActivity implements Animation.AnimationListener {
    private AnimatedCircleLoadingView animatedCircleLoadingView;
    TextView usrkey,tagtxt;
    FinishedOkView finishedOkView;
    AnimationState finishedState;
    public ViewAnimator viewAnimator;
    private AnimatedCircleLoadingView.AnimationListener animationListener;
    public boolean startAnimationIndeterminate;
    public boolean startAnimationDeterminate;
    public boolean stopAnimationOk;
    public boolean stopAnimationFailure;
    public int mainColor;
    Animation animFadein,bottomUp,Alpha;
    FrameLayout frameLayout;
    String tagged_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle__animation);

        tagged_no=getIntent().getStringExtra("tagged_no");
        usrkey=(TextView)findViewById(R.id.userkey);

        tagtxt=(TextView)findViewById(R.id.tagtext);
        usrkey.setText(tagged_no);

        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        startLoading();
        startPercentMockThread();

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        animFadein.setAnimationListener(this);

    }

    private void startLoading() {
        animatedCircleLoadingView.startDeterminate();
        usrkey.setVisibility(View.GONE);
        tagtxt.setVisibility(View.VISIBLE);
    }

    private void startPercentMockThread() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    for (int i = 0; i <= 100; i++) {
                        Thread.sleep(65);
                        changePercent(i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void changePercent(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.setPercent(percent);
                if(percent==100) {
                    finishOk();
                }
            }
        });
    }

    public void finishOk() {
        usrkey.setVisibility(View.VISIBLE);
        //tagtxt.setVisibility(View.GONE);
        String text = "<b>" + "Congratulations!!! " + "</b> " + "\n"+"Your tag number is";
        tagtxt.setText(Html.fromHtml(text ));
        usrkey.startAnimation(animFadein);
    }

    public void resetLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.resetLoading();
            }
        });
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        bottomUp = AnimationUtils.loadAnimation(this, R.anim.slide_up1);
        animatedCircleLoadingView.setVisibility(View.GONE);
        bottomUp.setDuration(1500);
        usrkey.startAnimation(bottomUp);

        bottomUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addFirstPost();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }) ;
    }

    private void addFirstPost() {
        String profileUrl = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.PROFILE_URL);
        if (profileUrl != null) {
            FileDataSender.CATEGORY_ID = 1;
            FileDataSender.VIEW_TO = 1;
            FileDataSender.FILE_PATH = null;
            FileDataSender.FILE_PATH_LIST = null;
            try {
                new FileUploadHelper(
                        this,
                        profileUrl,
                        Constants.POST_TYPE_MOMENTS_INT,
                        new OnFileUploadListener() {
                            @Override
                            public void OnFileUploaded() {
                                FileDataSender.clear();
                                moveToMainScreen();
                            }

                            @Override
                            public void onFileUploadFailed() {
                                FileDataSender.clear();
                                moveToMainScreen();
                            }
                        });
            } catch (Exception e) {
                this.moveToMainScreen();
            }
        } else {
            this.moveToMainScreen();
        }
    }

    private void moveToMainScreen() {
        Intent it = new Intent(Circle_AnimationActivity.this, MainDashboardActivity.class);
        it.putExtra("fragmentload", "");
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        usrkey.clearAnimation();


    }
}
