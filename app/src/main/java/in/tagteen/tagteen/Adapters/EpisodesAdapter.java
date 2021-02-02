package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.WebshowModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.WebShowPreviewActivity;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<WebshowModel.WebshowDetails> postsList;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;

    public EpisodesAdapter(
            Context mContext,
            ArrayList<WebshowModel.WebshowDetails> postsList,
            RecyclerView recyclerView) {
        this.mContext = mContext;
        this.postsList = postsList;

        final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = gridLayoutManager.getItemCount();
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onLoadMoreListener.onLoadMore();
                            }
                        }, 200);
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.episode_video_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        WebshowModel.WebshowDetails data = this.postsList.get(position);

        if (data.getVideoThumbnailUrl() != null && data.getVideoThumbnailUrl().trim().length() > 0) {
            Utils.loadImageUsingGlide(
                    this.mContext, viewHolder.imgThumbnail, data.getVideoThumbnailUrl());
        }

        // bind events
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move to details
                moveToPreview(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.postsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }

    private void moveToPreview(WebshowModel.WebshowDetails data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SHOWROOM_POST_DATA, data);

        Intent intent = new Intent(this.mContext, WebShowPreviewActivity.class);
        intent.putExtras(bundle);
        this.mContext.startActivity(intent);
    }
}
