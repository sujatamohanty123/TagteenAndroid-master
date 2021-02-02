package in.tagteen.tagteen.Adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import in.tagteen.tagteen.GallaryPicker.Model_images;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;
import in.tagteen.tagteen.TagteenInterface.RemoveImage;


public class ChatGalleryAdapter extends RecyclerView.Adapter<ChatGalleryAdapter.ViewHolder> implements RemoveImage {

       Activity context;
       in.tagteen.tagteen.Adapters.ChatGalleryAdapter.ViewHolder viewHolder;
       ArrayList<String> selectedImage=new ArrayList<>();
       ArrayList<Model_images> al_menu;
       public GalleryClickedPosition delegate=null;
       private int ImageSelected=0;
       int int_position;
       private int avatar[]={R.drawable.pro1, R.drawable.pro2, R.drawable.pro3,
               R.drawable.pro4, R.drawable.pro5, R.drawable.pro6, R.drawable.pro1,
               R.drawable.pro3, R.drawable.pro4, R.drawable.pro5};


       public ChatGalleryAdapter(Activity context, ArrayList<Model_images> al_menu, int int_position, GalleryClickedPosition delegate) {
           this.al_menu = al_menu;
           this.context = context;
           this.int_position = int_position;
           this.delegate  = delegate;
       }

       @Override
       public in.tagteen.tagteen.Adapters.ChatGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
           View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridview_iteam, null);
           in.tagteen.tagteen.Adapters.ChatGalleryAdapter.ViewHolder mh = new in.tagteen.tagteen.Adapters.ChatGalleryAdapter.ViewHolder(v);
           return mh;
       }




       @Override
       public void onBindViewHolder(in.tagteen.tagteen.Adapters.ChatGalleryAdapter.ViewHolder itemRowHolder, int position) {

           itemRowHolder.selectedlayout.setVisibility(View.GONE);
           if(position==0){
               itemRowHolder.linearLayout.setVisibility(View.VISIBLE);
           }else{
               itemRowHolder.linearLayout.setVisibility(View.GONE);
           }

           Glide.with(context)
                   .load("file://" + al_menu.get(int_position).getAl_imagepath().get(position))
                   .diskCacheStrategy(DiskCacheStrategy.NONE)
                   .skipMemoryCache(true)
                   .fitCenter()
                   .centerCrop()
                   .into(itemRowHolder.iv_image);

     /*  itemRowHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent it = new Intent(mContext, ChatTreadActivity.class);
               mContext.startActivity(it);
           }
       });*/
       }

       @Override
       public int getItemCount() {
           return al_menu.get(int_position).getAl_imagepath().size();
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
                       if(clickedPosition==0){
                           delegate.clicked(selectedImage,1);
                       }else{
                           if(selectedImage.size()<5){
                               selectedlayout.setVisibility(View.VISIBLE);

                               String ImageUrl = al_menu.get(int_position).getAl_imagepath().get(clickedPosition);
                               selectedImage.add(ImageUrl);
                               delegate.clicked(selectedImage,0);
                           }else{
                               Toast.makeText(context, "U Can select only 5 Image at a time", Toast.LENGTH_LONG).show();
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
                       selectedImage.remove(ImageUrl);
                       delegate.clicked(selectedImage,0);
                   }
               });
           }

       }
       private int getCategoryPos(String category) {
           return al_menu.get(int_position).getAl_imagepath().indexOf(category);
       }





/*
public class ChatGalleryAdapter extends ArrayAdapter<Model_images>  {

   Context context;
   ViewHolder viewHolder;
   ArrayList<Model_images> al_menu = new ArrayList<>();
   int int_position;


   public ChatGalleryAdapter(Context context, ArrayList<Model_images> al_menu, int int_position) {
       super(context, R.layout.adapter_photosfolder, al_menu);
       this.al_menu = al_menu;
       this.context = context;
       this.int_position = int_position;


   }

   @Override
   public int getCount() {

       Log.e("ADAPTER LIST SIZE", al_menu.get(int_position).getAl_imagepath().size() + "");
       return al_menu.get(int_position).getAl_imagepath().size();
   }

   @Override
   public int getItemViewType(int position) {
       return position;
   }

   @Override
   public int getViewTypeCount() {
       if (al_menu.get(int_position).getAl_imagepath().size() > 0) {
           return al_menu.get(int_position).getAl_imagepath().size();
       } else {
           return 1;
       }
   }

   @Override
   public long getItemId(int position) {
       return position;
   }


   @Override
   public View getView(final int position, View convertView, ViewGroup parent) {

       if (convertView == null) {

           viewHolder = new ViewHolder();
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_iteam, parent, false);
           viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
           viewHolder.linearLayout=(LinearLayout)convertView.findViewById(R.id.cameraLayout_view);
           viewHolder.selectedlayout=(RelativeLayout)convertView.findViewById(R.id.slected_icon);

           convertView.setTag(viewHolder);

       } else {

           viewHolder = (ViewHolder) convertView.getTag();
       }


       viewHolder.selectedlayout.setVisibility(View.GONE);
if(position==0){
   viewHolder.linearLayout.setVisibility(View.VISIBLE);
}else{
   viewHolder.linearLayout.setVisibility(View.GONE);
}

       Glide.with(context)
               .load("file://" + al_menu.get(int_position).getAl_imagepath().get(position))
               .diskCacheStrategy(DiskCacheStrategy.NONE)
               .skipMemoryCache(true)
               .fitCenter()
               .centerCrop()
               .into(viewHolder.iv_image);

       return convertView;

   }



   private static class ViewHolder  {
       TextView tv_foldern, tv_foldersize;
       ImageView iv_image;
       LinearLayout linearLayout;
       RelativeLayout selectedlayout;

      */
/* @Override implements GalleryClickedPosition
       public void clicked(int Position) {
           selectedlayout.setVisibility(View.VISIBLE);
       }*//*

   }


}
*/


}
