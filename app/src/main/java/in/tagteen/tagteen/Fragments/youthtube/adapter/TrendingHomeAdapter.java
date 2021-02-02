package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import in.tagteen.tagteen.DataCache;
import in.tagteen.tagteen.Fragments.youthtube.WebShowsActivity;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.WebshowModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class TrendingHomeAdapter extends RecyclerView.Adapter<TrendingHomeAdapter.ViewHolder> {
  private Context mContext;
  private ArrayList<GetPostResponseModel.PostDetails> postsList;

  public TrendingHomeAdapter(Context mContext) {
    this.mContext = mContext;
    this.postsList = new ArrayList<>();
  }

  @NonNull
  @Override
  public TrendingHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    return new TrendingHomeAdapter.ViewHolder(
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_home_trending_row, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull TrendingHomeAdapter.ViewHolder viewHolder, int position) {
    final GetPostResponseModel.PostDetails data = this.postsList.get(position);

    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // move to post
        if (data.isWebShow()) {
          moveToWebShows();
        } else {
          moveToPost(position, data);
        }
      }
    });

    if (data.isWebShow()) {
      int colorLiveShow = ContextCompat.getColor(mContext, R.color.red_600);
      if (data.getImage() != null && !data.getImage().isEmpty()) {
        Glide.with(mContext)
                .load(data.getImage().get(0).getUrl())
                .into(viewHolder.imgUser);
      }
      viewHolder.txtName.setText("Live");
      viewHolder.txtName.setTextColor(mContext.getResources().getColor(R.color.red_600));

      viewHolder.txtStatus.setText("Live now");
      viewHolder.imgStatus.setVisibility(View.GONE);

      viewHolder.imgUser.setBorderColor(colorLiveShow);
      viewHolder.containerStatus.setBackground(
              ContextCompat.getDrawable(mContext, R.drawable.bg_background_liveshow));
      return;
    }

    final int mColorViral = ContextCompat.getColor(mContext, R.color.colorPrimary);
    final int mColorHot = ContextCompat.getColor(mContext, R.color.hot);
    //if (data == null) {
    //  return;
    //}

    //if (data.getVideoThumbnails() != null && data.getVideoThumbnails().isEmpty() == false) {
    //  Utils.loadImageUsingGlide(this.mContext, viewHolder.imgThumbnail,
    //      UrlUtils.getUpdatedImageUrl(data.getVideoThumbnails().get(0).getUrl(), "large"));
    //}

    if (data.getProfile_url() != null) {
      Glide.with(mContext)
          .load(UrlUtils.getUpdatedImageUrl(data.getVideoThumbnails().get(0).getUrl(), "large"))
          .into(viewHolder.imgUser);
    }

    viewHolder.txtName.setTextColor(mContext.getResources().getColor(R.color.black2));
    viewHolder.txtName.setText(data.getFirst_name());
    viewHolder.imgStatus.setVisibility(View.VISIBLE);
    viewHolder.imgStatus.setImageResource(R.drawable.ic_flame);
    if (position % 2 == 0) {
      viewHolder.txtStatus.setText("Viral");
      viewHolder.containerStatus.setBackground(
          ContextCompat.getDrawable(mContext, R.drawable.bg_background_viral));
      viewHolder.imgUser.setBorderColor(mColorViral);
    } else {
      viewHolder.txtStatus.setText("Hot");
      viewHolder.containerStatus.setBackground(
          ContextCompat.getDrawable(mContext, R.drawable.bg_background_hot));
      viewHolder.imgUser.setBorderColor(mColorHot);
    }

    // description
    //if (data.getContent() != null && data.getContent().trim().length() > 0) {
    //  viewHolder.lblDescription.setText(data.getContent());
    //  viewHolder.lblDescription.setVisibility(View.VISIBLE);
    //}
    //viewHolder.lblViewCounts.setText(data.getView_count() + " Views");
    //String millisecStr = data.getVideo_duration();
    //long milisec = Long.valueOf(millisecStr);
    //viewHolder.lblVideoDuration.setText(Utils.timeConversion(milisec));
    //if (data.isWebShow()) {
    //  viewHolder.containerViews.setVisibility(View.GONE);
    //  viewHolder.lblDescription.setText(data.getContent());
    //  viewHolder.lblDescription.setVisibility(View.VISIBLE);
    //} else {
    //  viewHolder.containerViews.setVisibility(View.VISIBLE);
    //}

    // bind events

  }

  private void moveToWebShows() {
    Intent intent = new Intent(mContext, WebShowsActivity.class);
    mContext.startActivity(intent);
  }

  private void moveToPost(int position, GetPostResponseModel.PostDetails data) {
    List<GetPostResponseModel.PostDetails> subList =
            this.postsList.subList(position + 1, this.postsList.size());
    DataCache.getInstance().setPostlist(subList, false);

    Utils.moveToVideoDetails(mContext, data);
  }

  public void setData(List<GetPostResponseModel.PostDetails> trendingVideoList) {
    postsList.clear();
    postsList.addAll(trendingVideoList);
    notifyDataSetChanged();
  }

  public void addLiveShows(ArrayList<WebshowModel.WebshowDetails> liveShows) {
    if (liveShows == null || liveShows.isEmpty()) {
      return;
    }

    int addedIndex = 0;
    for (WebshowModel.WebshowDetails liveshow : liveShows) {
      if (liveshow.getVideoThumbnailUrl() == null || liveshow.getVideoThumbnailUrl().trim().length() == 0) {
        continue;
      }
      GetPostResponseModel.PostDetails data = new GetPostResponseModel.PostDetails();
      List<GetPostResponseModel.PostDetails.Image> images = new ArrayList<>();
      GetPostResponseModel.PostDetails.Image image = data.new Image(liveshow.getVideoThumbnailUrl());
      images.add(image);
      data.setImage(images);
      data.setWebShow(true);
      this.postsList.add(addedIndex, data);
      addedIndex++;
    }
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return this.postsList != null ? this.postsList.size() : 0;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    //private ImageView imgThumbnail;
    //private TextView lblViewCounts;
    //private TextView lblVideoDuration;
    //private TextView lblDescription;
    private RelativeLayout containerStatus;
    private CircleImageView imgUser;
    private ImageView imgStatus;
    private TextView txtStatus;
    private TextView txtName;
    //private LinearLayout containerViews;

    public ViewHolder(View itemView) {
      super(itemView);
      //this.lblViewCounts = (TextView) itemView.findViewById(R.id.lblViewsCount);
      //this.imgThumbnail = (ImageView) itemView.findViewById(R.id.imgThumbnail);
      //this.lblVideoDuration = itemView.findViewById(R.id.lblVideoDuration);
      //this.lblDescription = itemView.findViewById(R.id.lblDescription);
      imgUser = itemView.findViewById(R.id.img_user);
      containerStatus = itemView.findViewById(R.id.container_trending_status);
      imgStatus = itemView.findViewById(R.id.img_trending_status);
      txtStatus = itemView.findViewById(R.id.txt_trending_status);
      txtName = itemView.findViewById(R.id.txt_trending_username);
      //containerViews = itemView.findViewById(R.id.container_views);
    }
  }
}
