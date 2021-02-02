package in.tagteen.tagteen.profile.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.utils.CircleTransform;

/**
 * Created by lovekushvishwakarma on 10/10/17.
 */

public class ProfilePicsAdapter extends RecyclerView.Adapter<ProfilePicsAdapter.ImageViewHolder> {

    private ArrayList<String> categoryList;
    public static Context mContext;
    private boolean isLike = false;

    public ProfilePicsAdapter(ArrayList<String> categoryList, Context mContext) {
        this.categoryList = categoryList;
        this.mContext = mContext;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_pic_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        Glide.with(mContext)
                .load(categoryList.get(position)) // add your image url
                .transform(new CircleTransform(mContext)) // applying the image transformer
                .into(holder.imagePic);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagePic;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imagePic = (ImageView) itemView.findViewById(R.id.imagePic);

        }
    }


}
