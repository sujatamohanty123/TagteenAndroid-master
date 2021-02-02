package in.tagteen.tagteen.Adapters;


import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import in.tagteen.tagteen.ChatTreadActivity;
import in.tagteen.tagteen.Model.SingleItemModel;
import in.tagteen.tagteen.R;

import java.util.ArrayList;

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {
    private String name[] = {"Ajit", "Rhul", "Pradeep", "Siba", "Sachin", "Jaya", "Mohit", "Ramesh", "Anooj", "Sandeep","mohit"};

    private int avatar[]={R.drawable.pro1,R.drawable.pro2,R.drawable.pro3,
            R.drawable.pro4,R.drawable.pro5,R.drawable.pro6,R.drawable.pro1,
            R.drawable.pro3,R.drawable.pro4,R.drawable.pro5};
    private ArrayList<SingleItemModel> itemsList;
    private Context mContext;
    private Animation fadeIn;
    public SectionListDataAdapter(Context context, ArrayList<SingleItemModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_single_card, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        SingleItemModel singleItem = itemsList.get(i);

        holder.tvTitle.setText(name[i]);
        fadeIn = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
        fadeIn.setDuration(1500);
        holder.indicator.startAnimation(fadeIn);
        fadeIn.setRepeatCount(Animation.INFINITE);


        holder.itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.itemImage.setCornerRadius(60);
        Glide
                .with(mContext)
                .load(avatar[i])
                .fitCenter()
                .into(holder.itemImage);

       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        private TextView border;
        protected RoundedImageView itemImage;
        private ImageView indicator;

        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle   = (TextView) view.findViewById(R.id.tvTitle);
            this.itemImage = (RoundedImageView) view.findViewById(R.id.online_userViewImage);
            this.indicator = (ImageView) view.findViewById(R.id.indicator_online_list);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(v.getContext(), tvTitle.getText(), Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(mContext, ChatTreadActivity.class);
                    mContext.startActivity(it);


                }
            });


        }

    }

}