package in.tagteen.tagteen;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/**
 * Created by Mathivanan on 24-02-2018.
 */

public class FullScreenDialog extends DialogFragment {



    public static final String TAG = "FullScreenDialog";



    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

    }



    @Override

    public void onStart() {

        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {

            int width = ViewGroup.LayoutParams.MATCH_PARENT;

            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

    }



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {

        super.onCreateView(inflater, parent, state);

        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_chat_navigation, parent, false);

        String mFriendName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_NAME);
        String mFriendId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_ID);
        String mFriendImage = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_IMAGE);
        String  mFriendTag = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_TAG);
        String  login_user = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.PROFILE_URL);

        RoundedImageView friendimage=(RoundedImageView)view.findViewById(R.id.friend_image);
        RoundedImageView friend_group_image1=(RoundedImageView)view.findViewById(R.id.friend_group_image1);
        RoundedImageView login_user_image=(RoundedImageView)view.findViewById(R.id.login_user_image);
        TextView friend_name=(TextView)view.findViewById(R.id.friend_name);
        TextView search=(TextView)view.findViewById(R.id.search);
        TextView friends_tagNumber=(TextView)view.findViewById(R.id.friends_tagNumber);
        LinearLayout unhide_chat=(LinearLayout)view.findViewById(R.id.unhide_chat);
        LinearLayout unlock_chat=(LinearLayout)view.findViewById(R.id.unlock_chat);
        Glide.with(this).load(mFriendImage).fitCenter().into(friendimage);
        friend_name.setText(mFriendName);
        friends_tagNumber.setText(mFriendTag);
        Glide.with(this).load(mFriendImage).fitCenter().into(friend_group_image1);
        Glide.with(this).load(login_user).fitCenter().into(login_user_image);
        int isLocked = SharedPreferenceSingleton.getInstance().getIntPreference(RegistrationConstants.IS_LOCKED_FRIEND);
        if(isLocked==1){
            unlock_chat.setVisibility(View.VISIBLE);
        }else {
            unlock_chat.setVisibility(View.GONE);
        }
        /*int isHide = SharedPreferenceSingleton.getInstance().getIntPreference(RegistrationConstants.IS_HIDE_CHAT_FRIEND);
        if(isHide==1){
            unhide_chat.setVisibility(View.VISIBLE);
        }else {
            unhide_chat.setVisibility(View.GONE);
        }*/
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;

    }
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

}
