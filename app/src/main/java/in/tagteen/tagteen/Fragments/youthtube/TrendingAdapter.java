package in.tagteen.tagteen.Fragments.youthtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

import java.util.ArrayList;

import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.widgets.AGVRecyclerViewAdapter;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<GetPostResponseModel.PostDetails> postsList;

    public TrendingAdapter(Context mContext, ArrayList<GetPostResponseModel.PostDetails> postsList) {
        this.mContext = mContext;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {

        } else {

        }
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.trending_video_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final GetPostResponseModel.PostDetails data = this.postsList.get(position);

        if (data == null) {
            return;
        }

        if (data.getVideoThumbnails() != null && data.getVideoThumbnails().isEmpty() == false) {
            Utils.loadImageUsingGlide(
                    this.mContext, viewHolder.imgThumbnail, data.getVideoThumbnails().get(0).getUrl());
        }

        // bind events
        viewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move to details
                moveToDetailsScreen(data);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return this.postsList != null ? this.postsList.size() : 0;
    }

    private void moveToDetailsScreen(GetPostResponseModel.PostDetails data) {
        /*WebShowsDetailsActivity fragment = new WebShowsDetailsActivity();
        Bundle bundle = new Bundle();
        bundle.putString("post_creator_id",data.getPostCreatorId());
        bundle.putString("post_vid_id",data.getId());
        bundle.putInt("category_id",data.getCategorie_id());
        bundle.putSerializable(Constants.SHOWROOM_POST_DATA, data);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;

        public ViewHolder(View view) {
            super(view);
            this.imgThumbnail = view.findViewById(R.id.imgThumbnail);
        }
    }
}
