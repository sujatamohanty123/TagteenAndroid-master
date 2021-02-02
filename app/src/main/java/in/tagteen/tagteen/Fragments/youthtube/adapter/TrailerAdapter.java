package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.tagteen.tagteen.Model.WebshowModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.WebShowPreviewActivity;
import in.tagteen.tagteen.WebShowsDetailsActivity;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<WebshowModel.WebshowDetails> postsList;
    private int webshowTypeId;

    public TrailerAdapter(
            Context mContext, ArrayList<WebshowModel.WebshowDetails> postsList, int webshowTypeId) {
        this.mContext = mContext;
        this.postsList = postsList;
        this.webshowTypeId = webshowTypeId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.trailer_video_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final WebshowModel.WebshowDetails data = this.postsList.get(position);
        if (data == null) {
            return;
        }

        if (data.getVideoThumbnailUrl() != null && data.getVideoThumbnailUrl().trim().length() > 0) {
            Utils.loadImageUsingGlide(
                    this.mContext, viewHolder.imgThumbnail, data.getVideoThumbnailUrl());
        }

        // bind events
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move to preview
                moveToPreview(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.postsList != null ? this.postsList.size() : 0;
    }

    private void moveToPreview(WebshowModel.WebshowDetails data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SHOWROOM_POST_DATA, data);

        Intent intent = new Intent(this.mContext, WebShowPreviewActivity.class);
        intent.putExtras(bundle);
        this.mContext.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}
