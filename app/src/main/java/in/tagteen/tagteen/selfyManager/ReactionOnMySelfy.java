package in.tagteen.tagteen.selfyManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.selfyManager.models.SelfyReactModel;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AndroidPermissionMarshMallo;

/**
 * Created by lovekushvishwakarma on 16/10/17.
 */

public class ReactionOnMySelfy implements AsyncResponse {

    Dialog dialog;
    TempSelfyMasterView context;
    AndroidPermissionMarshMallo permissionMarshMallo;
    ArrayList<SelfyReactModel> reactList;
    LinearLayout listView;
    ImageView imageDelete;
    String selfyId ;

    public ReactionOnMySelfy(final TempSelfyMasterView context, String selfyId, String gender) {
        this.context = context;
        this.selfyId=selfyId;
        dialog = new Dialog(context, R.style.DialogSlideAnim);
        permissionMarshMallo = new AndroidPermissionMarshMallo((Activity) context);
        dialog.setContentView(R.layout.reactin_onmy_selfy);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        lp.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

       reactList = new ArrayList<>();
        listView = (LinearLayout) dialog.findViewById(R.id.listView);
        imageDelete = (ImageView) dialog.findViewById(R.id.imageDelete);
        TextView lblTitle = (TextView) dialog.findViewById(R.id.textLable);
        if (gender.equalsIgnoreCase("male")) {
            lblTitle.setText("Selfie King made by");
        } else {
            lblTitle.setText("Selfie Queen made by");
        }

        getAllreactions();

        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogshow();
                //deleteSelfy();
            }
        });

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    ((TempSelfyMasterView) context).onResume();
                }
                return true;
            }
        });
    }

    private void dialogshow() {
        final Dialog d = new Dialog(context);
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
        msg.setText("Are you sure ?");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        continueorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                deleteSelfy();
            }
        });
        d.show();
    }

    private void deleteSelfy() {
        AsyncWorker mWorker = new AsyncWorker(context);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        try {
            BroadcastObject.put("selfie_id", selfyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWorker.execute(ServerConnector.REQUEST_DELETE_MYSELFY, BroadcastObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_DELETE_MYSELFY);
    }

    private void getAllreactions() {
        AsyncWorker mWorker = new AsyncWorker(context);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        try {
            BroadcastObject.put("selfie_id", selfyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWorker.execute(ServerConnector.REQUEST_GET_SELFY_REACTION, BroadcastObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_GET_SELFY_REACTION);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {

        if (REQUEST_NUMBER.equalsIgnoreCase(RequestConstants.REQUEST_GET_SELFY_REACTION)) {
            try {
                JSONObject jsonObject = new JSONObject(output);
                JSONObject data = jsonObject.getJSONObject("data");
                boolean success = jsonObject.getBoolean("success");

                String post_id = data.getString("_id");
                JSONObject post_creator_id = data.getJSONObject("post_creator_id");
                String _id = post_creator_id.getString("_id");
                String first_name = post_creator_id.getString("first_name");
                String last_name = post_creator_id.getString("last_name");
                String gender = post_creator_id.getString("gender");
                String profile_url = post_creator_id.getString("profile_url");
                String email = post_creator_id.getString("email");
                String school_name = post_creator_id.getString("school_name");
                String pincode = post_creator_id.getString("pincode");
                String tagged_number = post_creator_id.getString("tagged_number");

                JSONArray react = data.getJSONArray("action_type");
                listView.removeAllViews();
                for (int i = 0; i < react.length(); i++) {
                    JSONObject inderdata = react.getJSONObject(i);
                    String id = inderdata.getString("_id");
                    String timestamp = inderdata.getString("timestamp");
                    JSONObject user_id = inderdata.getJSONObject("user_id");
                    String _id_action = user_id.getString("_id");
                    String first_name_action = user_id.getString("first_name");
                    String last_name_action = user_id.getString("last_name");
                    String profile_url_action = user_id.getString("profile_url");
                    String tagged_number_action = user_id.getString("tagged_number");
                    reactList.add(new SelfyReactModel(id, timestamp, _id_action));


                    View child = ((Activity) context).getLayoutInflater().inflate(R.layout.child_reaction, null);
                    TextView txtName = (TextView) child.findViewById(R.id.textName);
                    TextView textTag = (TextView) child.findViewById(R.id.textTag);
                    TextView textTime = (TextView) child.findViewById(R.id.textTime);
                    ImageView imageUser = (ImageView) child.findViewById(R.id.imageUser);

                    Utils.loadProfilePic(context, imageUser, profile_url_action);
                    textTime.setText(Utils.getRelativeTime(Long.parseLong(timestamp)));
                    txtName.setText(first_name_action+" "+last_name_action);
                    textTag.setText(tagged_number_action);
                    listView.addView(child);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (REQUEST_NUMBER.equalsIgnoreCase(RequestConstants.REQUEST_DELETE_MYSELFY)) {

            try {
                JSONObject jsonObject = new JSONObject(output);
                String message = jsonObject.getString("message");
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    //@ant22
//                    dialog.dismiss();
                    context.setResult(Activity.RESULT_OK);
                    context.finish();
                   // ((TempSelfyMasterView) context).onResume();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
