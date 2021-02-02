package in.tagteen.tagteen.profile;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

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

public class ChangePassWord extends Activity implements View.OnClickListener, AsyncResponse {
    private EditText edtpass, edtpassNew, edtpassNewRe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chnage_password);

        ImageView imageback = (ImageView) findViewById(R.id.imageback);
        TextView textSave = (TextView) findViewById(R.id.textSave);
        TextView textCancel = (TextView) findViewById(R.id.textCancel);
        textSave.setOnClickListener(this);
        textCancel.setOnClickListener(this);
        imageback.setOnClickListener(this);

        edtpass = (EditText) findViewById(R.id.edtpass);
        edtpassNew = (EditText) findViewById(R.id.edtpassNew);
        edtpassNewRe = (EditText) findViewById(R.id.edtpassNewRe);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textSave:
                chanegPassfun();
                break;
            case R.id.textCancel:
                finish();
                break;
            case R.id.imageback:
                finish();
                break;
        }
    }

    private void chanegPassfun() {
        String pass = edtpass.getText().toString();
        String new_pass = edtpassNew.getText().toString();
        String re_new_pass = edtpassNewRe.getText().toString();
        if (new_pass.length() >= 6) {
            if (new_pass.equalsIgnoreCase(re_new_pass)) {
                chanegNow(pass, new_pass);
            } else {
                Toast.makeText(this, "Pasword not match...", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Minumum length should be 6 digit...", Toast.LENGTH_LONG).show();
        }
    }

    private void chanegNow(String oldpassword, String newpassword) {
        AsyncWorker mWorker = new AsyncWorker(ChangePassWord.this);
        mWorker.delegate = this;
        mWorker.delegate = this;
        final String usr_id = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        JSONObject BroadcastObject = new JSONObject();
        try {
            BroadcastObject.put("old_password", oldpassword);
            BroadcastObject.put("new_password", newpassword);
            BroadcastObject.put("user_id", usr_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWorker.execute(ServerConnector.REQUEST_CHANGE_PASSWORD, BroadcastObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_CHANGE_PASSWORD);
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
                finish();
            } else {
                Utils.showToast(this, Josnoutput.getString("something going wrong please try agaim..."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
