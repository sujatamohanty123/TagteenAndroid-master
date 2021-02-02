package in.tagteen.tagteen.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;

/**
 * Created by Jayattama Prusty on 05-Sep-17.
 */

public class LikeCountAdapter extends RecyclerView.Adapter<LikeCountAdapter.ItemRowHolder> {
   ArrayList<SectionDataModel.LikeModel> likeModelArrayList;
    Context context;

    public LikeCountAdapter(Context context, ArrayList<SectionDataModel.LikeModel> likeModelArrayList) {
        this.likeModelArrayList = likeModelArrayList;
        this.context = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_count_row, parent, false);
        return new LikeCountAdapter.ItemRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemRowHolder holder, int position) {
        SectionDataModel.LikeModel likeModel=likeModelArrayList.get(position);
        holder.textView.setText(likeModel.getLiked_by_name());
        holder.tagnum.setText(likeModel.getTag_num());
        Utils.loadProfilePic(context,holder.img,likeModel.getProfile_pic_url());
        if(likeModel.islike()==true){
            holder.heart.setImageResource(R.drawable.ic_svg_cool_select);
        }else {
            holder.heart.setImageResource(R.drawable.ic_svg_cool_unselect);
        }

    }

    @Override
    public int getItemCount() {
        return likeModelArrayList.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView textView,tagnum;
        ImageView heart;
        public ItemRowHolder(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.name);
            tagnum=(TextView)itemView.findViewById(R.id.tag);
            img=(ImageView) itemView.findViewById(R.id.img);
            heart=(ImageView) itemView.findViewById(R.id.like_heart);
        }
    }
}
