package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.Model.FriendSeach;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RockstarAdapter extends RecyclerView.Adapter<RockstarAdapter.ImageViewHolder> {
  private ArrayList<FriendSeach.UserInfo> rockStarsList;
  private Context mContext;

  public RockstarAdapter(Context mContext, ArrayList<FriendSeach.UserInfo> rockStarsList) {
    this.mContext = mContext;
    this.rockStarsList = rockStarsList;

    // sort by rank
    Collections.sort(this.rockStarsList, new Comparator<FriendSeach.UserInfo>() {
      @Override
      public int compare(FriendSeach.UserInfo lhs, FriendSeach.UserInfo rhs) {
        if (lhs.getRank() > rhs.getRank()) {
          return 1;
        } else if (lhs.getRank() < rhs.getRank()) {
          return -1;
        } else {
          return 0;
        }
      }
    });
  }

  @NonNull
  @Override
  public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    return new ImageViewHolder(
        LayoutInflater.from(parent.getContext()).inflate(R.layout.rockstar_row, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int position) {
    FriendSeach.UserInfo userInfo = this.rockStarsList.get(position);
    if (userInfo == null) {
      return;
    }

    Utils.loadRoundedImage(this.mContext, imageViewHolder.imgProfilePic,
        UrlUtils.getUpdatedImageUrl(userInfo.getProfile_url(), "large"), 250);

    String userName = userInfo.getFirst_name();
    if (userInfo.getLast_name() != null) {
      userName += " " + userInfo.getLast_name();
    }
    imageViewHolder.lblUsername.setText(userName);
    if (userInfo.getTagged_number() != null) {
      imageViewHolder.lblTagNo.setText(userInfo.getTagged_number());
    }

    // events
    imageViewHolder.imgProfilePic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // move to user profile
        Utils.gotoProfile(mContext, userInfo.get_id());
      }
    });
  }

  @Override
  public int getItemCount() {
    return this.rockStarsList != null ? this.rockStarsList.size() : 0;
  }

  public class ImageViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgProfilePic;
    private TextView lblUsername;
    private TextView lblTagNo;

    public ImageViewHolder(View itemView) {
      super(itemView);
      this.imgProfilePic = itemView.findViewById(R.id.imgProfilePic);
      this.lblUsername = itemView.findViewById(R.id.lblUserName);
      this.lblTagNo = itemView.findViewById(R.id.lblTagNumber);
    }
  }
}
