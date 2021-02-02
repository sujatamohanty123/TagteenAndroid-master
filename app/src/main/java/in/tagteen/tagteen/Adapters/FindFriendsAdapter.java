package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tagteen.tagteen.Model.FriendSeach;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.MyViewHolder> {
    private List<FriendSeach.UserInfo> friendlist;
    private Context context;
    private String userid;
    private String token;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, tagNumber, folowUnfollow;
        public ImageView imageProfilePic;
        RelativeLayout layoutmain;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.textname);
            folowUnfollow = (TextView) view.findViewById(R.id.folowUnfollow);
            tagNumber = (TextView) view.findViewById(R.id.texttag);
            imageProfilePic = (ImageView) view.findViewById(R.id.imageProfilePic);
            layoutmain = (RelativeLayout) view.findViewById(R.id.layoutmain);
        }
    }

    public FindFriendsAdapter(Context context, List<FriendSeach.UserInfo> friendlist, int flag) {
        this.context = context;
        this.friendlist = friendlist;
        SharedPreferenceSingleton.getInstance().init(context);
        userid =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        token =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.fan_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FriendSeach.UserInfo friend = friendlist.get(position);
        holder.userName.setText(friend.getFirst_name() + " " + friend.getLast_name());
        holder.tagNumber.setText(friend.getTagged_number() + "");
        Utils.loadProfilePic(context, holder.imageProfilePic, friend.getProfile_url());

        holder.folowUnfollow.setVisibility(View.GONE);
        if (friend.isFan()) {
            holder.folowUnfollow.setText("You're a Fan");
            holder.folowUnfollow.setBackgroundResource(R.drawable.fnn_select);
            holder.folowUnfollow.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.folowUnfollow.setText("Be a Fan");
            holder.folowUnfollow.setBackgroundResource(R.drawable.fnn_unselect);
            holder.folowUnfollow.setTextColor(context.getResources().getColor(R.color.white));
        }

        holder.folowUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.layoutmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.gotoProfile(context, friend.get_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.friendlist != null ? friendlist.size() : 0;
    }
}
