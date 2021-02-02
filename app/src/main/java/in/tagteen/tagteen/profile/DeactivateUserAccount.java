package in.tagteen.tagteen.profile;

import android.app.Activity;
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

import androidx.annotation.Nullable;

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

/**
 * Created by lovekushvishwakarma on 12/10/17.
 */

public class DeactivateUserAccount extends Activity implements View.OnClickListener, AsyncResponse {

    EditText edtpass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deactivate_account);

        ImageView imageback = (ImageView) findViewById(R.id.imageback);
        TextView textDeactivate = (TextView) findViewById(R.id.textDeactivate);
        TextView textCancel = (TextView) findViewById(R.id.textCancel);
        TextView delete_account = (TextView) findViewById(R.id.delete_account);
        textDeactivate.setOnClickListener(this);
        textCancel.setOnClickListener(this);
        imageback.setOnClickListener(this);
        delete_account.setOnClickListener(this);

        edtpass = (EditText) findViewById(R.id.edtpass);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textDeactivate:
                chanegNow();
                break;
            case R.id.textCancel:
                finish();
                break;
            case R.id.imageback:
                finish();
                break;
            case R.id.delete_account:
                Intent in=new Intent(DeactivateUserAccount.this, Delete_user_Account.class);
                startActivity(in);
        }
    }


    private void chanegNow() {
        String pass = edtpass.getText().toString();
        if(!pass.equalsIgnoreCase("")){
            Dialog_confirmation_show(pass);
        }else{
            Utils.showToast(this,"Please enter correct password!");
        }
    }

    private void Dialog_confirmation_show(final String pass) {
        final Dialog d = new Dialog(DeactivateUserAccount.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.confirmationpopup);
        final TextView msg = (TextView) d.findViewById(R.id.message);
        TextView name = (TextView) d.findViewById(R.id.yourname);
        TextView continueorder = (TextView) d.findViewById(R.id.confirm);
        TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
        TextView confirm_ok_btn=(TextView) d.findViewById(R.id.confirm_ok_btn);
        confirm_ok_btn.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        msg.setText("Are you sure you want to deactivate your account ?");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        continueorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncWorker mWorker = new AsyncWorker(DeactivateUserAccount.this);
                mWorker.delegate = DeactivateUserAccount.this;
                mWorker.delegate = DeactivateUserAccount.this;
                final String usr_id = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
                JSONObject BroadcastObject = new JSONObject();
                try {
                    BroadcastObject.put("password", pass);
                    BroadcastObject.put("user_id", usr_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mWorker.execute(ServerConnector.REQUEST_DEACTIVATE_ACC, BroadcastObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_ACC_DEACTIVATE);
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
            if (Josnoutput.getBoolean("success")) {
                Utils.showToast(this, Josnoutput.getString("message"));
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
