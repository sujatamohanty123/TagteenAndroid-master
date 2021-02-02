package in.tagteen.tagteen.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.Fragments.MomentsFeed;
import in.tagteen.tagteen.Model.AllSelfyList;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.SelfiActivity;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.selfyManager.TempSelfyMasterView;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;

/**
 * Created by ADMIN on 04-06-2017.
 */

public class AllSelfyListHorizntalAdapter
    extends RecyclerView.Adapter<AllSelfyListHorizntalAdapter.MyViewHolder> {
  public static final String PLACEHOLDER = "PlaceholderId";

  private Context context;
  private ArrayList<AllSelfyList.SelfyData> arrayList = new ArrayList<>();

  public AllSelfyListHorizntalAdapter(Context context,
      ArrayList<AllSelfyList.SelfyData> arrayList) {
    this.context = context;
    this.arrayList = arrayList;
  }

  @Override
  public AllSelfyListHorizntalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.all_selfy_list_item, parent, false);
    MyViewHolder myViewHolder = new MyViewHolder(view);
    return myViewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull AllSelfyListHorizntalAdapter.MyViewHolder holder,
      final int position) {

    try {
      final AllSelfyList.SelfyData data = arrayList.get(position);
      if (data.get_id().equals(PLACEHOLDER)) {
        holder.imgProfilePic.setVisibility(View.GONE);
        holder.lblAddMoments.setVisibility(View.VISIBLE);
        String profilePicUrl = SharedPreferenceSingleton.getInstance()
            .getStringPreference(RegistrationConstants.PROFILE_URL);
        if (profilePicUrl != null) {
          Utils.loadProfilePicImage(context, holder.imgMoments,
              UrlUtils.getUpdatedImageUrl(profilePicUrl, "large"));
        }
        holder.textname.setText("Add Moments");
        holder.textname.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        holder.textname.setTypeface(null, Typeface.BOLD);
      } else {
        Utils.loadProfilePic(context, holder.imgProfilePic,
          data.getProfile_url());

        // load image
        if (data.getImages() != null && data.getImages().size() > 0) {
          Utils.loadImageUsingGlide(this.context, holder.imgMoments,
              UrlUtils.getUpdatedImageUrl(data.getImages().get(0).getUrl(), "large"));
        }

        String name = data.getFirst_name() + " " + data.getLast_name();
        if (name.length() > 12) {
          name = name.substring(0, 10) + "..";
        }
        holder.textname.setText(name);
        holder.textname.setTextColor(context.getResources().getColor(R.color.full_black));
      }

      holder.imgMoments.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (data.get_id().equals(PLACEHOLDER)) {
            //Intent intent = new Intent(context, StatusPlaceholderActivity.class);
            Intent intent = new Intent(context, SelfiActivity.class);
            context.startActivity(intent);
          } else {
            Intent gotoSlefyView = new Intent(context, TempSelfyMasterView.class);
            gotoSlefyView.putExtra("_id", data.get_id());
            gotoSlefyView.putExtra("username", data.getFirst_name() + " " + data.getLast_name());
            gotoSlefyView.putExtra("userpic", data.getProfile_url());
            gotoSlefyView.putExtra("gender", data.getGender());
            ((Activity) context).startActivityForResult(gotoSlefyView, MomentsFeed.REQUEST_DELETE);
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public int getItemCount() {
    return arrayList.size();
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgMoments;
    private ImageView imgProfilePic;
    private TextView lblAddMoments;
    private TextView textname;

    public MyViewHolder(View itemView) {
      super(itemView);
      this.imgMoments = itemView.findViewById(R.id.imgMoments);
      this.imgProfilePic = itemView.findViewById(R.id.imgProfilePic);
      this.lblAddMoments = itemView.findViewById(R.id.lblAddMoments);
      this.textname = itemView.findViewById(R.id.textname);
    }
  }
}
