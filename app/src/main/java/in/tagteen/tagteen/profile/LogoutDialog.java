package in.tagteen.tagteen.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import in.tagteen.tagteen.LoginActivity;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/**
 * Created by lovekushvishwakarma on 12/10/17.
 */

public class LogoutDialog implements AsyncResponse {

    Activity activity;

    public LogoutDialog(final Activity activity) {
        this.activity = activity;

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.logout_dilaog);
        // Custom Android Allert Dialog Title
        dialog.setTitle("Log Out of tagteen");

        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView unfollow = (TextView) dialog.findViewById(R.id.textYes);
        // Click cancel to dismiss android custom dialog box
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceSingleton.getInstance().clearPreference();
                ChatSessionManager.getInstance().clear();//tony00

                Intent intent = new Intent(activity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                dialog.dismiss();

                AppSettings.logout();
            }
        });
        dialog.show();

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
            System.out.print(output);
    }

}
