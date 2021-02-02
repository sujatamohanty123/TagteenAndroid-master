package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.tagteen.tagteen.Fragments.youthtube.bean.UserInfoBean;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.util.Utils;

public class LatestUsersAdapter extends RecyclerView.Adapter<LatestUsersAdapter.ImageViewHolder> {
    private ArrayList<UserInfoBean> usersList;
    private Context mContext;

    public LatestUsersAdapter(Context mContext, ArrayList<UserInfoBean> usersList) {
        this.mContext = mContext;
        this.usersList = usersList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.latest_user_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        UserInfoBean userInfo = this.usersList.get(position);
        if (userInfo.getUserName() != null) {
            holder.lblUserName.setText(userInfo.getUserName());
        }
        Utils.loadProfilePic(this.mContext, holder.imgProfilePic, userInfo.getProfileUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move to user profile
                Utils.gotoProfile(mContext, userInfo.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList != null ? usersList.size() : 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView lblUserName;
        private ImageView imgProfilePic;

        public ImageViewHolder(View itemView) {
            super(itemView);
            this.lblUserName = (TextView) itemView.findViewById(R.id.lblUserName);
            this.imgProfilePic = (ImageView) itemView.findViewById(R.id.imgProfilePic);
        }
    }
}
