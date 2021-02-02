package in.tagteen.tagteen.Adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.MaterialEditTextUtils.ColorGenerator;
import in.tagteen.tagteen.Model.CreatePostJsonResponseModel;
import in.tagteen.tagteen.Model.Friend;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.utils.CircleTransform;
import in.tagteen.tagteen.utils.ShapeImageView;
import android.widget.Filter;

public class FriendsListAdapter extends BaseAdapter implements Filterable {

    private Activity mContext;
    private LayoutInflater inflater;
    private String[] mThumbIds;
    ImageView indicator;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private Animation fadeIn, fadeOut;
    List<Friend> friendss=new ArrayList<>();

    private List<Friend> friendFilteredList=new ArrayList<>();
    private List<Friend> friendNameList = new ArrayList<>();
    public FriendsListAdapter(Activity mContext,  List<Friend> friends) {
        this.mContext = mContext;
        this.friendss=friends;
        for(int i=1;i<=friendss.size();i++) {
            Friend friend=friendss.get(i);
            friendNameList.add(friend);
        }
        friendFilteredList = friendNameList;
    }

    @Override
    public int getCount() {
        return  friendss.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_list_iteam, null);
        }
        fadeIn = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(mContext,R.anim.fade_out);
        Friend friendListModel = friendss.get(position);
        TextView group_name= (TextView)convertView.findViewById(R.id.list_friend_name);
        TextView tagView= (TextView)convertView.findViewById(R.id.friends_tagNumber);
        ImageView indi=(ImageView)convertView.findViewById(R.id.online_indicator_icon);
        RoundedImageView im=(RoundedImageView)convertView.findViewById(R.id.userViewImage);
        group_name.setText(friendListModel.getName());
        tagView.setText(friendListModel.getTag());
        fadeIn = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(mContext,R.anim.fade_out);
        im.setScaleType(ImageView.ScaleType.CENTER_CROP);
        im.setCornerRadius(150);
        fadeIn.setDuration(1500);
        indi.startAnimation(fadeIn);
        fadeIn.setRepeatCount(Animation.INFINITE);
        Glide.with(mContext).load(friendListModel.getImg()).placeholder(R.drawable.placeholder).transform(new CircleTransform(mContext)).into(im);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (constraint.toString().length() > 0) {
                    List<Friend> founded = new ArrayList<>();
                    for (Friend item : friendNameList) {
                        if (item.getName().toLowerCase().contains(constraint)) {
                            founded.add(item);
                        }
                    }

                    result.values = founded;
                    result.count = founded.size();

                } else {
                    result.values = friendNameList;
                    result.count = friendNameList.size();
                }
                return result;


            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                clear();
//                for (StudentDummy item : (List<StudentDummy>) results.values) {
//                    add(item);
//                }

                friendFilteredList = (List<Friend>) results.values;

                if (friendFilteredList.size() == 0) {

//                    selectConcernStudentActivity.setErrorMessage("No result found for '"+constraint+"'");
                } else {
//                    selectConcernStudentActivity.setErrorMessage("");
                }
                notifyDataSetChanged();

            }

        };

    }
}
