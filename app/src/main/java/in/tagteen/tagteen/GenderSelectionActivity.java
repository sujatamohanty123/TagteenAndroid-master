package in.tagteen.tagteen;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class GenderSelectionActivity extends AppCompatActivity {
    private boolean isFade = false;
    private ImageView imageGoNext, mailImageView, femailImageView;
    private EditText userName;
    private EditText Password;
    private Intent it;
    public final int CATEGORY_ID = 0;
    private LinearLayout loginView, backgroundView;
    private Animation Alpha, fadeIn, fadeOut;
    private String MALE = "male";
    private String FEMALE = "female";
    private String Gender="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.tagteen.tagteen.R.layout.activity_gender_selection);
        mailImageView = (ImageView) findViewById(in.tagteen.tagteen.R.id.mail_icon_button);
        femailImageView = (ImageView) findViewById(in.tagteen.tagteen.R.id.femail_icon_button);
        imageGoNext = (ImageView) findViewById(in.tagteen.tagteen.R.id.Gender_imageButton);

        fadeIn = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.fade_out);
        fadeIn.setDuration(10);
        fadeIn.setDuration(10);
        mailImageView.startAnimation(fadeOut);
        femailImageView.startAnimation(fadeOut);

        SharedPreferenceSingleton.getInstance().init(GenderSelectionActivity.this);
        final String gender = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.GENDER);
        if (gender.equals(MALE)) {
            maleSelection();
        } else if(gender.equals(FEMALE)) {
            femaleSelection();
        }

        imageGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Gender.equalsIgnoreCase("")||Gender.isEmpty()||Gender.equalsIgnoreCase(null)){
                    Toast.makeText(getApplicationContext(), "Select Gender", Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferenceSingleton.getInstance().init(GenderSelectionActivity.this);
                    SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.GENDER, Gender);
                    Intent it = new Intent(GenderSelectionActivity.this, SingUpAcadmicDetailActivity.class);
                    startActivity(it);
                }

            }
        });

        mailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleSelection();
            }
        });

        femailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                femaleSelection();
            }
        });
    }

    public void maleSelection() {
        mailImageView.startAnimation(fadeIn);
        fadeIn.setDuration(100);
        femailImageView.startAnimation(fadeOut);
        fadeIn.setDuration(100);
        Gender = MALE;
    }

    public void femaleSelection() {
        femailImageView.startAnimation(fadeIn);
        fadeIn.setDuration(100);
        mailImageView.startAnimation(fadeOut);
        fadeIn.setDuration(100);
        Gender = FEMALE;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(in.tagteen.tagteen.R.anim.anim_slide_in_right, in.tagteen.tagteen.R.anim.anim_slide_out_left);
    }
}
