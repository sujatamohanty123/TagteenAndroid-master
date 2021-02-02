package in.tagteen.tagteen.Adapters;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Model.Friend;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.UserSelectionList;
import in.tagteen.tagteen.utils.CircleTransform;


public class ForwordAdapter extends RecyclerView.Adapter<ForwordAdapter.MyViewHolder> {

    private Activity mContext;
    private List<Friend> group = new ArrayList<>();
    private RelativeLayout relative;
    private UserSelectionList userSelectionList;
    private List<Friend> selectedList  = new ArrayList<>();
    public ForwordAdapter(Activity mContext, List<Friend> group,UserSelectionList userSelectionList) {
        this.mContext = mContext;
        this.group = group;
        this.userSelectionList = userSelectionList;
    }

    public ForwordAdapter() {}

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView group_name, Designation;
        RoundedImageView im;
        CheckBox rb;
        protected RecyclerView recycler_view_list;
        protected RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            group_name = (TextView) view.findViewById(R.id.list_friend_name);
            Designation = (TextView) view.findViewById(R.id.teacherDesignation);
            im = (RoundedImageView) view.findViewById(R.id.userViewImage);
            rb = (CheckBox) view.findViewById(R.id.button);
            recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relative);
            relative = (RelativeLayout) view.findViewById(R.id.lest_iteam_id);
        }
    }

    @Override
    public ForwordAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_group_row, parent, false);
        return new ForwordAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ForwordAdapter.MyViewHolder holder, final int position) {

        final Friend friend = group.get(position);
        holder.group_name.setText(friend.getName());
        holder.Designation.setText(friend.getTag());
        holder.im.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.im.setCornerRadius(60);
        Glide.with(mContext)
                .load(friend.getImag()).placeholder(R.drawable.placeholder).transform(new CircleTransform(mContext))
                .fitCenter()
                .into(holder.im);
        holder.rb.setOnCheckedChangeListener(null);
        if (friend.isSelected()) {
            holder.rb.setChecked(true);
        } else {
            holder.rb.setChecked(false);
        }

        holder.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                friend.setSelected(isChecked);
                notifyDataSetChanged();
                if (isChecked) {
                    selectedList.add(friend);
                    userSelectionList.selectionList(selectedList);

                } else {
                    selectedList.remove(friend.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return group.size();
    }
}