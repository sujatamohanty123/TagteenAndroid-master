package in.tagteen.tagteen;

/**
 * Created by deepshikha on 3/3/17.
 */

import android.app.Activity;
import android.content.Intent;
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

import in.tagteen.tagteen.GallaryPicker.Model_images;
import in.tagteen.tagteen.TagteenInterface.RemoveImage;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;


public class AlbumGridViewAdapter extends RecyclerView.Adapter<AlbumGridViewAdapter.ViewHolder> implements RemoveImage {
    private Activity context;
    private ArrayList<String> selectedImage = new ArrayList<>();
    private ArrayList<Model_images> al_menu;
    private int int_position;
    private LinearLayout ok;
    private boolean isSingleSelection;

    public AlbumGridViewAdapter(Activity context, ArrayList<Model_images> al_menu, int int_position, LinearLayout ok, boolean isSingleSelection) {
        this.al_menu = al_menu;
        this.context = context;
        this.int_position = int_position;
        this.ok=ok;
        this.isSingleSelection = isSingleSelection;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridview_iteam, null);
        ViewHolder mh = new ViewHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ViewHolder itemRowHolder, int position) {
        itemRowHolder.selectedlayout.setVisibility(View.GONE);
        if (position==0) {
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
        int count = 0;
        try {
            if (al_menu.size()>int_position) {
                count = al_menu.get(int_position).getAl_imagepath().size();
            }
        } catch (Exception e) {
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
                    ok.setVisibility(View.VISIBLE);
                    if (isSingleSelection) {
                        int clickedPosition = getAdapterPosition();
                        String imageUrl = al_menu.get(int_position).getAl_imagepath().get(clickedPosition);
                        sendResult(imageUrl);
                    } else {
                        if (selectedImage.size() < 10) {
                            selectedlayout.setVisibility(View.VISIBLE);
                            int clickedPosition = getAdapterPosition();
                            String ImageUrl = al_menu.get(int_position).getAl_imagepath().get(clickedPosition);
                            selectedImage.add(ImageUrl);
                        } else {
                            Utils.showShortToast(context, "You can select only 10 images at a time");
                        }
                    }
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent();
                    it.putStringArrayListExtra("selectedImage", selectedImage);
                    context.setResult(5,it);
                    context.finish();
                }
            });
            selectedlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedlayout.setVisibility(View.GONE);
                    int clickedPosition = getAdapterPosition();
                    String ImageUrl = al_menu.get(int_position).getAl_imagepath().get(clickedPosition);
                    selectedImage.remove(ImageUrl);
                    if (selectedImage.isEmpty()) {
                        ok.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void sendResult(String imagePath) {
        Intent intent = new Intent();
        intent.putExtra(Constants.SELECTED_IMAGE_PATHS, imagePath);
        context.setResult(context.RESULT_OK, intent);
        context.finish();
    }

    private int getCategoryPos(String category) {
        return al_menu.get(int_position).getAl_imagepath().indexOf(category);
    }
}