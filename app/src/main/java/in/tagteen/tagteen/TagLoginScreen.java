package in.tagteen.tagteen;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by user on 30-03-2017.
 */

public class TagLoginScreen extends AppCompatActivity {


    boolean isFade = false;
    ImageView image;
    EditText userName;
    EditText Password;
    Intent it;
    public final int CATEGORY_ID =0;
    LinearLayout loginView, backgroundView;
    Animation Alpha,bottomUp, bottomUp_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.tagteen.tagteen.R.layout.sign_up2);
        backgroundView=(LinearLayout)findViewById(in.tagteen.tagteen.R.id.login_container_tag_screen);
        image=(ImageView) findViewById(in.tagteen.tagteen.R.id.signup_logo_screen);

        bottomUp = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.slide_up);
        Alpha = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.alpha);
        bottomUp_layout = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.alpha);
        image.setVisibility(View.VISIBLE);
        loginView.setVisibility(View.VISIBLE);

        backgroundView.startAnimation(Alpha);
        Alpha = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.alpha);
        Alpha.setDuration(1500);
        bottomUp.setDuration(1500);
        image.startAnimation(bottomUp);
        bottomUp_layout.setDuration(1500);

        Alpha.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
                loginView.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });



        bottomUp.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                loginView.setVisibility(View.VISIBLE);
                loginView.startAnimation(bottomUp_layout);

            }
        });


//        image=(ImageView)findViewById(R.id.imageButton);
//        it =new Intent(this, SingUpAcadmicDetailActivity.class);
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(it);
//            }
//        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(in.tagteen.tagteen.R.anim.anim_slide_in_right, in.tagteen.tagteen.R.anim.anim_slide_out_left);
    }
}
