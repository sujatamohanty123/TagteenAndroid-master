package in.tagteen.tagteen.profile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import in.tagteen.tagteen.LoginActivity;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class Delete_user_Account extends AppCompatActivity implements View.OnClickListener, AsyncResponse {
    EditText edtpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user__account);

        ImageView imageback = (ImageView) findViewById(R.id.imageback);
        TextView textDeleteaccount = (TextView) findViewById(R.id.textDelete);
        TextView textCancel = (TextView) findViewById(R.id.textCancel);
        textDeleteaccount.setOnClickListener(this);
        textCancel.setOnClickListener(this);
        imageback.setOnClickListener(this);

        edtpass = (EditText) findViewById(R.id.edtpass);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textDelete:
                chanegNow();
                break;
            case R.id.textCancel:
                finish();
                break;
            case R.id.imageback:
                finish();
                break;
        }
    }


    private void chanegNow() {
        String pass = edtpass.getText().toString();
        if(!pass.equalsIgnoreCase("")){
            Dialog_confirmation_show();
        }else{
            Utils.showToast(this,"Please enter correct password!");
        }
    }

    private void Dialog_confirmation_show() {
        final Dialog d = new Dialog(Delete_user_Account.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.confirmationpopup);
        final TextView msg = (TextView) d.findViewById(R.id.message);
        TextView name = (TextView) d.findViewById(R.id.yourname);
        TextView continueorder = (TextView) d.findViewById(R.id.confirm);
        TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
        name.setVisibility(View.GONE);
        msg.setText("Are you sure you want to delete your account ?");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        continueorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncWorker mWorker = new AsyncWorker(Delete_user_Account.this);
                mWorker.delegate = Delete_user_Account.this;
                String id = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
                String url = ServerConnector.REQUEST_DELETE_ACC + id ;
                mWorker.execute(url,"", RequestConstants.GET_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_ACC_DELETE);
            }
        });
        d.show();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        try {
            JSONObject Josnoutput = new JSONObject(output);
            JSONObject Josnoutput_MSG = Josnoutput.getJSONObject("message");
            int n=Josnoutput_MSG.getInt("n");
            int ok=Josnoutput_MSG.getInt("ok");

            if (Josnoutput.getBoolean("success") && n==1 && ok==1) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Utils.showToast(this, Josnoutput.getString("something going wrong please try agaim..."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
