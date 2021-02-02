package in.tagteen.tagteen.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;


public class SelectedGalaryImageAdapter extends RecyclerView.Adapter<SelectedGalaryImageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> imagePath;
    public GalleryClickedPosition delegate=null;
    private OnItemClickListener mOnItemClickListener;

    private int avatar[] = {R.drawable.pro1, R.drawable.pro2, R.drawable.pro3,
            R.drawable.pro4, R.drawable.pro5, R.drawable.pro6, R.drawable.pro1,
            R.drawable.pro3, R.drawable.pro4, R.drawable.pro5};

    public SelectedGalaryImageAdapter(Context context, ArrayList<String> imagePath) {
        this.imagePath = imagePath;
        this.context = context;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, ArrayList<String> imagePath, int clickedPosition);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridview_iteam, null);
        ViewHolder mh = new ViewHolder(v);
        delegate =(GalleryClickedPosition)context;

        return mh;
    }


    @Override
    public void onBindViewHolder(ViewHolder itemRowHolder, int position) {
        itemRowHolder.selectedlayout.setVisibility(View.GONE);
        itemRowHolder.crossImage.setVisibility(View.GONE);
        itemRowHolder.lblEditImage.setVisibility(View.VISIBLE);

        if (position == 0) {
            itemRowHolder.linearLayout.setVisibility(View.GONE);
        } else {
            itemRowHolder.linearLayout.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(imagePath.get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()
                .centerCrop()
                .into(itemRowHolder.iv_image);
    }

    @Override
    public int getItemCount() {
        return imagePath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_foldern, tv_foldersize;
        private ImageView iv_image,crossImage;
        private LinearLayout linearLayout;
        private RelativeLayout selectedlayout;
        private TextView lblEditImage;

        public ViewHolder(View convertView) {
            super(convertView);
            this.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            this.linearLayout = (LinearLayout) convertView.findViewById(R.id.cameraLayout_view);
            this.selectedlayout = (RelativeLayout) convertView.findViewById(R.id.slected_icon);
            this.crossImage = (ImageView) convertView.findViewById(R.id.remove_image);
            this.lblEditImage = convertView.findViewById(R.id.lblEditImage);

            crossImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    String ImageUrl = imagePath.get(clickedPosition);
                    imagePath.remove(ImageUrl);
                    delegate.clicked(imagePath,0);

                }
            });

            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        int clickedPosition = getAdapterPosition();
                        mOnItemClickListener.onItemClick(v,imagePath,clickedPosition);
                    }
                }
            });
            this.lblEditImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        int clickedPosition = getAdapterPosition();
                        mOnItemClickListener.onItemClick(view, imagePath, clickedPosition);
                    }
                }
            });
        }

    }

}