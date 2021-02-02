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

import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.util.Utils;

public class PopularVideosAdapter extends RecyclerView.Adapter<PopularVideosAdapter.ViewHolder> {
  private Context mContext;
  private ArrayList<GetPostResponseModel.PostDetails> postsList;

  public PopularVideosAdapter(Context mContext,
      ArrayList<GetPostResponseModel.PostDetails> postsList) {
    this.mContext = mContext;
    this.postsList = postsList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    return new ViewHolder(
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.popular_video_row, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
    final GetPostResponseModel.PostDetails data = this.postsList.get(position);
    if (data == null) {
      return;
    }

    if (data.getVideoThumbnails() != null && data.getVideoThumbnails().isEmpty() == false) {
      Utils.loadImageUsingGlide(this.mContext, viewHolder.imgThumbnail,
          UrlUtils.getUpdatedImageUrl(data.getVideoThumbnails().get(0).getUrl(), "large"));
    }

    viewHolder.lblViewCounts.setText(data.getView_count() + " Views");
    String millisecStr = data.getVideo_duration();
    long milisec = Long.valueOf(millisecStr);
    viewHolder.lblVideoDuration.setText(Utils.timeConversion(milisec));

    // description
    if (data.getContent() != null && data.getContent().trim().length() > 0) {
      viewHolder.lblDescription.setText(data.getContent());
      viewHolder.lblDescription.setVisibility(View.VISIBLE);
    }

    // bind events
    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // move to post
        Utils.moveToVideoDetails(mContext, data);
      }
    });
  }

  @Override
  public int getItemCount() {
    return this.postsList != null ? this.postsList.size() : 0;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgThumbnail;
    private TextView lblViewCounts;
    private TextView lblVideoDuration;
    private TextView lblDescription;

    public ViewHolder(View itemView) {
      super(itemView);
      this.lblViewCounts = (TextView) itemView.findViewById(R.id.lblViewsCount);
      this.imgThumbnail = (ImageView) itemView.findViewById(R.id.imgThumbnail);
      this.lblVideoDuration = itemView.findViewById(R.id.lblVideoDuration);
      this.lblDescription = itemView.findViewById(R.id.lblDescription);
    }
  }
}
