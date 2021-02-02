package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.tagteen.tagteen.R;


public class ChatMessengerActivityDialogAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    public ChatMessengerActivityDialogAdapter(Context c) {
        mInflater = LayoutInflater.from(c);
        mContext = c;
    }
    public int getCount() {
        return mThumbIds.length;
    }
    public Object getItem(int position) {
        return null;
    }
    public long getItemId(int position) {
        return 0;
    }
    // create a new ImageView for each item referenced by the
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {  // if it's not recycled,
            convertView = mInflater.inflate(R.layout.dialog_iteams, null);
//            convertView.setLayoutParams(new GridView.LayoutParams(100,150));
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.text);
            holder.icon  = (ImageView)convertView.findViewById(R.id.image);
            holder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.di_background);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.icon.setAdjustViewBounds(true);
//        holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);

        holder.relativeLayout.setBackgroundResource(mThumbIds[position]);
        holder.title.setText(categoryContent[position]);
        holder.icon.setImageResource(mThumbIcons[position]);
        return convertView;
    }
    class ViewHolder {
        TextView title;
        ImageView icon;
        RelativeLayout relativeLayout;
    }
    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.di_new_chat,      R.drawable.di_view_profile,       R.drawable.di_media,
            R.drawable.di_hide_chat,     R.drawable.di_lock_chat,          R.drawable.di_clear_chat,
            R.drawable.di_themes,        R.drawable.di_block,              R.drawable.di_create_group_with_user,
            R.drawable.di_mute,          R.drawable.di_privacy,            R.drawable.di_schedule_msg,
            R.drawable.di_op_hide_chat,  R.drawable.di_share_youth_tube,   R.drawable.di_back
    };

    private Integer[] mThumbIcons = {
            R.drawable.new_chat_icon,      R.drawable.ivc_view_profile,       R.drawable.media_small_icon,
            R.drawable.ivc_hide_chat,      R.drawable.ivc_lock_chat,          R.drawable.ivc_clear_chat,
            R.drawable.ivc_themes,         R.drawable.ivc_block,              R.drawable.ivc_create_group,
            R.drawable.ivc_mute,           R.drawable.privacy,                R.drawable.ivc_schedule_msg,
            R.drawable.ivc_new_chat,       R.drawable.ivc_share_youth_tube,   R.drawable.ivc_back
    };

    private String[] categoryContent = {
            "New Chat", "View profile","Media",
            "Hide Chat","Lock Chat", "Clear Chat",
            "Themes","Block", "Create Group with User",
            "Mute", "Privacy","Schedule Message",
            "Open Chat Head", " Share YouthTubeFeed Video","Back"
    };


}