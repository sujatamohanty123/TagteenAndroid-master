package in.tagteen.tagteen.profile;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.tagteen.tagteen.Model.FeedbackModel;
import in.tagteen.tagteen.Model.FeedbackResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.utils.Commons;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {
    private ImageView back;
    EditText comment;
    TextView submit;
    FeedbackModel likeJsonInputModel=new FeedbackModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        back=(ImageView)findViewById(R.id.back);
        comment=(EditText) findViewById(R.id.comment);
        submit=(TextView) findViewById(R.id.submit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FeedbackActivity.this,AppSettings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!comment.getText().toString().equalsIgnoreCase((""))) {
                    callfeedback_api();
                }else {
                    Toast.makeText(getApplicationContext(), "Enter Your Comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callfeedback_api() {
        String userid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        likeJsonInputModel.setUser_id(userid);
        likeJsonInputModel.setMessage(comment.getText().toString());
        Call<FeedbackResponseModel> call =methods.insert_feedback(userid,likeJsonInputModel,"application/json");
        String url = call.request().url().toString();
        Log.d("url", "url=" + url);
        call.enqueue(new Callback<FeedbackResponseModel>() {
            @Override
            public void onResponse(Call<FeedbackResponseModel> call, Response<FeedbackResponseModel> response) {
                int statuscode = response.code();
                if (response.code() == 200) {
                    FeedbackResponseModel model = response.body();
                    if(model.isSuccess()){
                        comment.setText("");
                        Commons.hideSoftKeyboard(getWindow(), FeedbackActivity.this);
                        //Toast.makeText(getApplicationContext(), "Successfully Submitted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<FeedbackResponseModel> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
