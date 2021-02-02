package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import in.tagteen.tagteen.ChatTreadActivity;
import in.tagteen.tagteen.Model.SingleItemModel;
import in.tagteen.tagteen.R;

//import static in.tagteen.tagteen.R.id.picker;


public class GroupOnlineListAdapter extends RecyclerView.Adapter<in.tagteen.tagteen.Adapters.GroupOnlineListAdapter.SingleItemRowHolder> {
    private String Group_name[] = {"Ajit Gorup", "Rhul Gorup", "mohit Gorup", "Pradeep Gorup", "Siba Gorup", "Sachin Gorup", "Jaya Gorup", "Mohit Gorup", "Ramesh Gorup", "Anooj Gorup", "Sandeep Gorup"};

     private int avatar[]={R.drawable.pro1,R.drawable.pro2,R.drawable.pro3,
            R.drawable.pro4,R.drawable.pro5,R.drawable.pro6,R.drawable.pro1,
            R.drawable.pro3,R.drawable.pro4,R.drawable.pro5};

    private float live[] = {65f, 20f, 40f, 50f, 10f, 100f, 20f, 40f, 50f, 10f, 100f };

    private ArrayList<SingleItemModel> itemsList;
        private Context mContext;
        private Animation fadeIn;
        public GroupOnlineListAdapter(Context context, ArrayList<SingleItemModel> itemsList) {
            this.itemsList = itemsList;
            this.mContext = context;
        }

        @Override
        public in.tagteen.tagteen.Adapters.GroupOnlineListAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_card_group_online_view, null);
            in.tagteen.tagteen.Adapters.GroupOnlineListAdapter.SingleItemRowHolder mh = new in.tagteen.tagteen.Adapters.GroupOnlineListAdapter.SingleItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(in.tagteen.tagteen.Adapters.GroupOnlineListAdapter.SingleItemRowHolder holder, int i) {

            SingleItemModel singleItem = itemsList.get(i);
            holder.tvTitle.setText(Group_name[i]);
            //holder.holoSeekBar.setValue(live[i]);
            holder.itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.itemImage.setCornerRadius(60);
            Glide
                    .with(mContext)
                    .load(avatar[i])
                    .fitCenter()
                    .into(holder.itemImage);

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
            //private HoloCircleSeekBar holoSeekBar;
            public SingleItemRowHolder(View view) {
            super(view);

                this.tvTitle   = (TextView) view.findViewById(R.id.tvTitle);
                this.itemImage = (RoundedImageView) view.findViewById(R.id.online_userViewImage);
                this.indicator = (ImageView) view.findViewById(R.id.indicator_online_list);
                //holoSeekBar = (HoloCircleSeekBar) view.findViewById(picker);
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