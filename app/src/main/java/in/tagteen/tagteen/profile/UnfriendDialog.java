package in.tagteen.tagteen.profile;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import in.tagteen.tagteen.Interfaces.UserProfileCallbackListener;
import in.tagteen.tagteen.Model.GetFanList;
import in.tagteen.tagteen.Model.JsonModelForBff;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/**
 * Created by lovekushvishwakarma on 12/10/17.
 */

public class UnfriendDialog implements AsyncResponse {

    Activity activity;
    GetFanList.UserData userData;
    UserProfileCallbackListener callbackListener;
    final Dialog dialog;

    public UnfriendDialog(Activity activity, final GetFanList.UserData userData, UserProfileCallbackListener callbackListener) {
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

        if(userData.isTaggedMe()){
            textBeUnfan.setText("UnFriend");
            unfollow.setText("UnFriend");
        }else{
            textBeUnfan.setText("Tag");
            unfollow.setText("Tag");
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
        String usr_id = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String access_token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);

        String url;
        if (userData.isTaggedMe()) {
            JsonModelForBff jsonObjectremove = new JsonModelForBff();
            jsonObjectremove.setUser_id(usr_id);
            jsonObjectremove.setFriend_user_id(userData.getUser_id());
            CommonApicallModule.callForUnfriend(jsonObjectremove, access_token, activity);
        } else {
            url = ServerConnector.REQUEST_FOR_TAG_FRIEND;
        }
        //mWorker.execute(url, BroadcastObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_UN_FAN);
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
            } else {
                Utils.showToast(activity, "something went wrong please try agian!");
            }
            this.callbackListener.unFriendCallback(true);
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
