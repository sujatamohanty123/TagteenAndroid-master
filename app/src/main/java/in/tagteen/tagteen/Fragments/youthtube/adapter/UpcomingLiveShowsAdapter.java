package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.Model.WebshowModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;
import io.agora.openlive.utils.TimestampUtils;
import java.util.ArrayList;
import java.util.Date;

public class UpcomingLiveShowsAdapter
    extends RecyclerView.Adapter<UpcomingLiveShowsAdapter.ViewHolder> {
  private Context mContext;
  private ArrayList<WebshowModel.WebshowDetails> postsList;
  private SelectionListener selectionListener;

  public UpcomingLiveShowsAdapter(
      Context mContext,
      ArrayList<WebshowModel.WebshowDetails> postsList,
      SelectionListener selectionListener) {
    this.mContext = mContext;
    this.postsList = postsList;
    this.selectionListener = selectionListener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
    return new ViewHolder(LayoutInflater.from(
        parent.getContext()).inflate(R.layout.upcoming_liveshow_video_row, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
    final int mColorViral = ContextCompat.getColor(mContext, R.color.viral);
    final int mColorHot = ContextCompat.getColor(mContext, R.color.hot);
    final WebshowModel.WebshowDetails data = this.postsList.get(position);
    if (data == null) {
      return;
    }

    if (data.getVideoThumbnailUrl() != null && data.getVideoThumbnailUrl().trim().length() > 0) {
      Utils.loadRoundedImage(
          this.mContext,
          viewHolder.imgThumbnail,
          data.getVideoThumbnailUrl(),
          150);
    }

    Date showTime = Utils.getDateTimeFromString(data.getWebshowDate());
    Date currentTime = new Date();
    if (currentTime.after(showTime)) {
      viewHolder.txtTime.setText("Live Now");
      viewHolder.containerStatus.setBackground(
          ContextCompat.getDrawable(mContext, R.drawable.bg_background_viral));
    } else {
      viewHolder.txtTime.setText(TimestampUtils.convertDateToString(
          Utils.getDateTimeFromString(data.getWebshowDate()), TimestampUtils.HOUR_FORMAT));
      viewHolder.containerStatus.setBackground(
          ContextCompat.getDrawable(mContext, R.drawable.bg_background_hot));
    }

    // bind events
    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // move to details
        if (selectionListener != null) {
          selectionListener.select(data);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return this.postsList != null ? this.postsList.size() : 0;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView imgThumbnail;
    private TextView txtTime;
    private RelativeLayout containerStatus;

    public ViewHolder(View itemView) {
      super(itemView);
      this.imgThumbnail = (ImageView) itemView.findViewById(R.id.imgThumbnail);
      txtTime = itemView.findViewById(R.id.txt_time);
      containerStatus = itemView.findViewById(R.id.container_trending_status);
    }
  }

  public interface SelectionListener {
    void select(WebshowModel.WebshowDetails webshow);
  }
}
