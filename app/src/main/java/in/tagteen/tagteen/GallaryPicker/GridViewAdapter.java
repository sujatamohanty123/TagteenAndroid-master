package in.tagteen.tagteen.GallaryPicker;


import android.app.Activity;
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

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;
import in.tagteen.tagteen.TagteenInterface.RemoveImage;
import in.tagteen.tagteen.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> implements RemoveImage{
    private Activity context;
    private ViewHolder viewHolder;
    private ArrayList<String> selectedImages = new ArrayList<>();
    private ArrayList<Model_images> al_menu;
    public GalleryClickedPosition delegate=null;
    private int ImageSelected=0;
    private int int_position;
    private int avatar[]={R.drawable.pro1,R.drawable.pro2,R.drawable.pro3,
            R.drawable.pro4,R.drawable.pro5,R.drawable.pro6,R.drawable.pro1,
            R.drawable.pro3,R.drawable.pro4,R.drawable.pro5};
    private ArrayList<String> imagePath = new ArrayList<>();

    public GridViewAdapter(
            Activity context, ArrayList<Model_images> al_menu, int int_position, List<String> selectedImages) {
        this.al_menu = al_menu;
        this.context = context;
        this.int_position = int_position;
        if (selectedImages != null) {
            this.selectedImages.addAll(selectedImages);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridview_iteam, null);
        ViewHolder mh = new ViewHolder(v);
        delegate = (GalleryClickedPosition) context;
        return mh;
    }

    @Override
    public void onBindViewHolder(ViewHolder itemRowHolder, int position) {
        if (selectedImages.contains(al_menu.get(int_position).getAl_imagepath().get(position))){
            itemRowHolder.selectedlayout.setVisibility(View.VISIBLE);
        } else {
            itemRowHolder.selectedlayout.setVisibility(View.GONE);
        }
        if (position==0){
            itemRowHolder.linearLayout.setVisibility(View.VISIBLE);
        } else {
            itemRowHolder.linearLayout.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load("file://" + al_menu.get(int_position).getAl_imagepath().get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()
                .centerCrop()
                .into(itemRowHolder.iv_image);
    }

    @Override
    public int getItemCount() {
        int count=0;
        try {
            if(al_menu.size() > int_position){
                count = al_menu.get(int_position).getAl_imagepath().size();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void callBack(String imagePath) {
      int position =  getCategoryPos(imagePath);
        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;
        LinearLayout linearLayout;
        RelativeLayout selectedlayout;

        public ViewHolder(View convertView) {
            super(convertView);
            this.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            this.linearLayout=(LinearLayout)convertView.findViewById(R.id.cameraLayout_view);
            this.selectedlayout=(RelativeLayout)convertView.findViewById(R.id.slected_icon);

           iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    if (clickedPosition==0){
                        delegate.clicked(selectedImages,1);
                    } else {
                        if (selectedImages.size()<10){
                            selectedlayout.setVisibility(View.VISIBLE);

                            String ImageUrl = al_menu.get(int_position).getAl_imagepath().get(clickedPosition);
                            selectedImages.add(ImageUrl);
                            delegate.clicked(selectedImages,0);
                        }else{
                            Utils.showShortToast(context, "You can select only 10 images at a time");
                        }
                    }
                }
           });

            selectedlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedlayout.setVisibility(View.GONE);
                    int clickedPosition = getAdapterPosition();
                    String ImageUrl = al_menu.get(int_position).getAl_imagepath().get(clickedPosition);
                    selectedImages.remove(ImageUrl);
                    delegate.clicked(selectedImages,0);
                }
            });
        }

    }
    private int getCategoryPos(String category) {
        return al_menu.get(int_position).getAl_imagepath().indexOf(category);
    }
}