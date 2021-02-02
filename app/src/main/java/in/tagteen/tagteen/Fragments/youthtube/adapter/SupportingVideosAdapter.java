package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.DataCache;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;

public class SupportingVideosAdapter extends RecyclerView.Adapter<SupportingVideosAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<GetPostResponseModel.PostDetails> videosList;

    public SupportingVideosAdapter(Context mContext, ArrayList<GetPostResponseModel.PostDetails> videosList) {
        this.mContext = mContext;
        this.videosList = videosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.supporter_video_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final GetPostResponseModel.PostDetails data = this.videosList.get(position);
        if (data == null) {
            return;
        }

        if (data.getVideoThumbnails() != null && data.getVideoThumbnails().isEmpty() == false) {
            Utils.loadImageUsingGlide(
                    this.mContext, viewHolder.imgThumbnail, UrlUtils.getUpdatedImageUrl(data.getVideoThumbnails().get(0).getUrl(),"large"));
        }

        viewHolder.lblViewCounts.setText(data.getLikeCount() + " U Rocks");
        String millisecStr = data.getVideo_duration();
        long milisec = Long.valueOf(millisecStr);
        viewHolder.lblVideoDuration.setText(Utils.timeConversion(milisec));

        // bind events
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move to post
                moveToPost(position, data);
            }
        });
    }

    private void moveToPost(int position, GetPostResponseModel.PostDetails data) {
        List<GetPostResponseModel.PostDetails> subList =
                this.videosList.subList(position + 1, this.videosList.size());
        DataCache.getInstance().setPostlist(subList, false);

        Utils.moveToVideoDetails(mContext, data);
    }

    @Override
    public int getItemCount() {
        return this.videosList != null ? this.videosList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private TextView lblViewCounts;
        private TextView lblVideoDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            this.lblViewCounts = (TextView) itemView.findViewById(R.id.lblViewsCount);
            this.imgThumbnail = (ImageView) itemView.findViewById(R.id.imgThumbnail);
            this.lblVideoDuration = itemView.findViewById(R.id.lblVideoDuration);
        }
    }
}
