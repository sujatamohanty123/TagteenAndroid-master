package in.tagteen.tagteen.profile.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.CircleTransform;

/**
 * Created by Sujata on 07-02-2018.
 */

public class ProfileFriendsListAdapter extends RecyclerView.Adapter<ProfileFriendsListAdapter.ViewHolder> {

    private Activity mContext;
    private LayoutInflater inflater;
    ArrayList<GetAllUserFriendlist.FriendsUserList> friends = new ArrayList<>();

    public ProfileFriendsListAdapter(Activity mContext, ArrayList<GetAllUserFriendlist.FriendsUserList> friends) {
        this.mContext = mContext;
        this.friends = friends;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lblUsername;
        private TextView lblTagNumber;
        private ImageView imgOnline;
        private RoundedImageView imgProfilePic;

        public ViewHolder(View v) {
            super(v);
            this.lblUsername =  v.findViewById(R.id.list_friend_name);
            this.lblTagNumber = v.findViewById(R.id.friends_tagNumber);
            this.imgOnline = v.findViewById(R.id.online_indicator_icon);
            this.imgProfilePic = v.findViewById(R.id.userViewImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_iteam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        GetAllUserFriendlist.FriendsUserList model = friends.get(i);
        if (model != null) {
            viewHolder.lblUsername.setText(model.getFirstName() + " " + model.getLastName());
            viewHolder.lblTagNumber.setText(model.getTaggedNumber());
            viewHolder.imgOnline.setVisibility(View.GONE);
            Glide.with(mContext)
                    .load(model.getProfileUrl())
                    .placeholder(R.drawable.placeholder)
                    .transform(new CircleTransform(mContext))
                    .into(viewHolder.imgProfilePic);

            // bind events
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.gotoProfile(mContext, model.getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.friends.size();
    }
}