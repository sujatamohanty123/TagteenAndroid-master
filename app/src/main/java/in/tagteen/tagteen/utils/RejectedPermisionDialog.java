package in.tagteen.tagteen.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import in.tagteen.tagteen.R;

/**
 * Created by BSETEC on 27-03-2017.
 */

public class RejectedPermisionDialog {

    Dialog popupdialg;
    TextView textviewMessage, textOk;
    Context mContext;
    public RejectedPermisionDialog(final Context mContext) {
        this.mContext=mContext;
        popupdialg = new Dialog(mContext);
        popupdialg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popupdialg.setContentView(R.layout.permision_rejected_layout);
        popupdialg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        textviewMessage = (TextView) popupdialg.findViewById(R.id.textMsg);
        textOk = (TextView) popupdialg.findViewById(R.id.textOk);

    }

    public void displayMsg(String msg) {
        textOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + mContext.getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                mContext.startActivity(i);
                popupdialg.dismiss();
            }
        });
        msg = msg + " permission needed for recording. Please allow in TagteenApplication AppSettings for additional functionality.";
        textviewMessage.setText(msg);
        popupdialg.show();
    }

    public void dismissDialog() {
        popupdialg.dismiss();
    }
}
