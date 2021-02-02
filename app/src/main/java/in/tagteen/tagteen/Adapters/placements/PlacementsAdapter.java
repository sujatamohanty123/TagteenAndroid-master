package in.tagteen.tagteen.Adapters.placements;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.placements.Placements;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;

public class PlacementsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Placements.Placement> placementsList;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener onItemClickListener;

    public PlacementsAdapter(Context context, List<Placements.Placement> placementsList, RecyclerView recyclerView) {
        this.context = context;
        this.placementsList = placementsList;

        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    isLoading = true;
                    if (onLoadMoreListener != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onLoadMoreListener.onLoadMore();
                            }
                        }, 200);
                    }
                }
            }
        });
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return this.placementsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.placement_row, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(this.context).inflate(R.layout.load_more, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else if (holder instanceof ViewHolder) {
            final Placements.Placement placement = this.placementsList.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.lblPlacementTitle.setText(placement.getPlacementTitle());
            viewHolder.lblPlacementProvider.setText(placement.getDescription());

            String location = "";
            if (placement.getCity() != null) {
                location += placement.getCity();
            }
            if (placement.getCountry() != null && placement.getCountry().trim().length() > 0) {
                if (location.trim().length() > 0) {
                    location += ", ";
                }
                location += placement.getCountry();
            }

            viewHolder.lblLocation.setText(location);

            Date createdAt = this.getDateFromString(placement.getCreatedAt());
            if (createdAt != null) {
                viewHolder.lblPostedOn.setText(Utils.getPlacementPostedAgo(createdAt.getTime()));
            } else {
                viewHolder.lblPostedOn.setVisibility(View.GONE);
            }

            final boolean alreadyApplied;
            if (placement.getIsApplied().equals("0")) {
                alreadyApplied = false;
                viewHolder.lblApply.setText("Apply");
            } else {
                alreadyApplied = true;
                viewHolder.lblApply.setText("Applied");
            }

            if (placement.getThumbnail() != null) {
                Utils.loadImageUsingGlideWithPlaceholder(
                        this.context, viewHolder.imgPlacementPic, placement.getThumbnail(), R.drawable.tag_icon);
            }

            // bind events
            viewHolder.lblApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alreadyApplied) {
                        return;
                    }

                    if (onItemClickListener != null) {
                        onItemClickListener.apply(position, placement);
                    }
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClicked(position, placement);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.placementsList != null ? this.placementsList.size() : 0;
    }

    private Date getDateFromString(String dateStr) {
        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            Log.d("Placements", e.getMessage());
        }
        return date;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPlacementPic;
        private TextView lblPlacementTitle;
        private TextView lblPlacementProvider;
        private TextView lblLocation;
        private TextView lblPostedOn;
        private TextView lblApply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imgPlacementPic = itemView.findViewById(R.id.imgPlacementPic);
            this.lblPlacementTitle = itemView.findViewById(R.id.lblPlacementTitle);
            this.lblPlacementProvider = itemView.findViewById(R.id.lblPlacementProvider);
            this.lblLocation = itemView.findViewById(R.id.lblLocation);
            this.lblPostedOn = itemView.findViewById(R.id.lblPostedOn);
            this.lblApply = itemView.findViewById(R.id.lblApply);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, Placements.Placement placement);
        void apply(int position, Placements.Placement placement);
    }
}
