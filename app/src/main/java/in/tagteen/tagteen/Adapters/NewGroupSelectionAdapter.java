package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import in.tagteen.tagteen.ChatTreadActivity;
import in.tagteen.tagteen.Model.UserModel;
import in.tagteen.tagteen.R;

import java.util.ArrayList;


public class NewGroupSelectionAdapter extends RecyclerView.Adapter<in.tagteen.tagteen.Adapters.NewGroupSelectionAdapter.SingleItemRowHolder> {


    private ArrayList<UserModel> itemsList;
    private Context mContext;



    public NewGroupSelectionAdapter(Context context, ArrayList<UserModel> itemsList, int flag) {
        this.itemsList = itemsList;
        this.mContext = context;
    }


        @Override
        public in.tagteen.tagteen.Adapters.NewGroupSelectionAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_single_card, null);
            in.tagteen.tagteen.Adapters.NewGroupSelectionAdapter.SingleItemRowHolder mh = new in.tagteen.tagteen.Adapters.NewGroupSelectionAdapter.SingleItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(in.tagteen.tagteen.Adapters.NewGroupSelectionAdapter.SingleItemRowHolder holder,final int i) {

           UserModel singleItem = itemsList.get(i);

            holder.tvTitle.setText(singleItem.getmName());
            Glide
                    .with(mContext)
                    .load(singleItem.getmImage())
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