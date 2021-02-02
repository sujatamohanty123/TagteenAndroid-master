package in.tagteen.tagteen;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.View;
import android.widget.ImageView;

import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class PasswordActivity extends AppCompatActivity {
    private ImageView image;
    private Intent it;
    private AppCompatEditText userPassword, userConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.tagteen.tagteen.R.layout.sign_up4);
        userPassword = (AppCompatEditText) findViewById(R.id.user_password);
        userConfirmPassword = (AppCompatEditText) findViewById(R.id.user__confirm_password);
        image = (ImageView) findViewById(in.tagteen.tagteen.R.id.imageButton);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = userPassword.getText().toString().trim();
                String confirmPassword = userConfirmPassword.getText().toString().trim();
                if(password.length()<6){
                    userPassword.setError("Please Enter min 6 digits");
                }else{
                   if(password.equals(confirmPassword)){
                       SharedPreferenceSingleton.getInstance().init(PasswordActivity.this);
                       SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.PASSWORD, userPassword.getText().toString());
                       it = new Intent(PasswordActivity.this, SingUpPhoneEmailActivity.class);
                       startActivity(it);
                   } else{
                       userConfirmPassword.setError("Please Enter Correct Password");
                   }
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(in.tagteen.tagteen.R.anim.anim_slide_in_right, in.tagteen.tagteen.R.anim.anim_slide_out_left);
    }
}
