package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import in.tagteen.tagteen.ChatTreadActivity;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;

import java.util.ArrayList;



public class RecyclerViewGroupAdapter extends RecyclerView.Adapter<in.tagteen.tagteen.Adapters.RecyclerViewGroupAdapter.ItemRowHolder> {
    private ArrayList<SectionDataModel> dataList;
    private Animation fadeIn, fadeOut;
    private Context mContext;
    private String name[] = {"Ajit", "Rhul", "Pradeep", "Siba", "Sachin", "Jaya", "Mohit", "Ramesh", "Anooj", "Sandeep","mohit"};
    private int avatar[]={R.drawable.pro1,R.drawable.pro2,R.drawable.pro3,
            R.drawable.pro4,R.drawable.pro5,R.drawable.pro6,R.drawable.pro1,
            R.drawable.pro3,R.drawable.pro4,R.drawable.pro5
    };

    public RecyclerViewGroupAdapter(Context context, ArrayList<SectionDataModel> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public in.tagteen.tagteen.Adapters.RecyclerViewGroupAdapter.ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.online_user_list_item, null);
        in.tagteen.tagteen.Adapters.RecyclerViewGroupAdapter.ItemRowHolder mh = new in.tagteen.tagteen.Adapters.RecyclerViewGroupAdapter.ItemRowHolder(v);
        return mh;
    }



    @Override
    public void onBindViewHolder(in.tagteen.tagteen.Adapters.RecyclerViewGroupAdapter.ItemRowHolder itemRowHolder, int i) {

        ArrayList singleSectionItems = dataList.get(i).getAllItemsInSection();
        fadeIn = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(mContext,R.anim.fade_out);

        if(i<1){
            GroupOnlineListAdapter itemListDataAdapter = new GroupOnlineListAdapter(mContext, singleSectionItems);
            itemRowHolder.recycler_view_list.setHasFixedSize(true);
            itemRowHolder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);
            itemRowHolder.recycler_view_list.setNestedScrollingEnabled(false);
            itemRowHolder.relativeLayout.setVisibility(View.GONE);
            itemRowHolder.recycler_view_list.setVisibility(View.VISIBLE);
            itemRowHolder.border.setVisibility(View.VISIBLE);

        }else{

            final String sectionName = name[i-1];
            itemRowHolder.itemTitle.setText(sectionName);
            itemRowHolder.relativeLayout.setVisibility(View.VISIBLE);
            itemRowHolder.recycler_view_list.setVisibility(View.GONE);
            itemRowHolder.border.setVisibility(View.GONE);
            itemRowHolder.indicator.setVisibility(View.GONE);
            itemRowHolder.im.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemRowHolder.im.setCornerRadius(60);
            Glide
                    .with(mContext)
                    .load(avatar[i])
                    .fitCenter()
                    .into(itemRowHolder.im);
        }

        itemRowHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, ChatTreadActivity.class);
                mContext.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected RelativeLayout relativeLayout;
        protected RoundedImageView im;
        //    private View viewListContainer;
        private ImageView indicator;
        private TextView border;

        public ItemRowHolder(View view) {
            super(view);

            this.itemTitle = (TextView) view.findViewById(R.id.list_friend_name);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            this.relativeLayout = (RelativeLayout) view.findViewById(R.id.list_item_container);
            this.im =(RoundedImageView)view.findViewById(R.id.userViewImage);
            this.border    = (TextView)view.findViewById(R.id.border_line);
            this.indicator=(ImageView)view.findViewById(R.id.online_indicator);
        }

    }

}





