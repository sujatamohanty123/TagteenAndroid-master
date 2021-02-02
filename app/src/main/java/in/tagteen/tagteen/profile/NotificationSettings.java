package in.tagteen.tagteen.profile;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import in.tagteen.tagteen.R;

/**
 * Created by lovekushvishwakarma on 12/10/17.
 */

public class NotificationSettings {
    private Activity activity;
    public NotificationSettings(Activity activity){
        this.activity=activity;

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.notification_setting);

        dialog.show();
    }
}
