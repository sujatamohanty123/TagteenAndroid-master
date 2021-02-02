package in.tagteen.tagteen.profile;

import android.app.Activity;
import android.app.Dialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import in.tagteen.tagteen.Interfaces.UserProfileCallbackListener;
import in.tagteen.tagteen.Model.GetFanList;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.profile.adapter.FanListAdapter;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/**
 * Created by lovekushvishwakarma on 12/10/17.
 */

public class UnfllowDialog implements AsyncResponse {

    Activity activity;
    GetFanList.UserData userData;
    FanListAdapter adapter;
    UserProfileCallbackListener callbackListener;
    final Dialog dialog;
    int pos;

    public UnfllowDialog(Activity activity, final GetFanList.UserData userData, final RecyclerView.Adapter adapter, final int pos) {
        this.activity = activity;
        this.userData = userData;
        if (adapter instanceof FanListAdapter) {
            this.adapter = (FanListAdapter) adapter;
        }
        this.pos = pos;
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unfollow_dialog);
        // Custom Android Allert Dialog Title
        dialog.setTitle("Custom Dialog Example");

        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView unfollow = (TextView) dialog.findViewById(R.id.unfollow);
        TextView textname = (TextView) dialog.findViewById(R.id.textname);
        TextView textBeUnfan= (TextView) dialog.findViewById(R.id.textBeUnfan);
        ImageView profilepic = (ImageView) dialog.findViewById(R.id.profilepic);

        if(userData.is_myfan()){
            textBeUnfan.setText("UnSupport");
            unfollow.setText("UnSupport");
        }else{
            textBeUnfan.setText("Support");
            unfollow.setText("Support");
        }

        textname.setText(userData.getFirst_name() + " " + userData.getLast_name());
        // Click cancel to dismiss android custom dialog box
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Your android custom dialog ok action
        // Action for custom dialog ok button click
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUnFan(userData);
            }
        });

        Utils.loadProfilePic(activity, profilepic, userData.getProfile_url());
        dialog.show();
    }

    public UnfllowDialog(Activity activity, final GetFanList.UserData userData, UserProfileCallbackListener callbackListener) {
        this.activity = activity;
        this.userData = userData;
        this.callbackListener = callbackListener;
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unfollow_dialog);
        // Custom Android Allert Dialog Title
        dialog.setTitle("Custom Dialog Example");

        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView unfollow = (TextView) dialog.findViewById(R.id.unfollow);
        TextView textname = (TextView) dialog.findViewById(R.id.textname);
        TextView textBeUnfan= (TextView) dialog.findViewById(R.id.textBeUnfan);
        ImageView profilepic = (ImageView) dialog.findViewById(R.id.profilepic);

        if(userData.is_myfan()){
            textBeUnfan.setText("UnSupport");
            unfollow.setText("UnSupport");
        }else{
            textBeUnfan.setText("Support");
            unfollow.setText("Support");
        }

        textname.setText(userData.getFirst_name() + " " + userData.getLast_name());
        // Click cancel to dismiss android custom dialog box
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Your android custom dialog ok action
        // Action for custom dialog ok button click
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUnFan(userData);
            }
        });

        Utils.loadProfilePic(activity, profilepic, userData.getProfile_url());
        dialog.show();

    }

    private void requestUnFan(GetFanList.UserData userData) {
        AsyncWorker mWorker = new AsyncWorker(activity);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        String usr_id = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        try {
            BroadcastObject.put("user_id", usr_id);
            BroadcastObject.put("friend_user_id", "" + userData.getUser_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url;
        if (userData.is_myfan()) {
            url = ServerConnector.REQUEST_UNFAN;
        } else {
            url = ServerConnector.REQUEST_BE_A_FAN;
        }
        mWorker.execute(url, BroadcastObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_UN_FAN);
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        String url;
        try {
            JSONObject dataJson = new JSONObject(output);

            if (dataJson.getBoolean("success")) {
                Utils.showToast(activity, "" + dataJson.get("message"));
                if (userData.is_myfan()) {
                    userData.setIs_myfan(false);
                    url = ServerConnector.REQUEST_UNFAN;
                } else {
                    userData.setIs_myfan(true);
                    url = ServerConnector.REQUEST_BE_A_FAN;
                }
                if (this.adapter != null) {
                    this.adapter.updateAdapter(userData, pos);
                }
                if (this.callbackListener != null) {
                    this.callbackListener.unSupportCallback(true);
                }
            } else {
                Utils.showToast(activity, "something went wrong please try agian!");
            }
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
