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


public class ChatDashboardDialogAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    int catag_icon;

    public ChatDashboardDialogAdapter(Context c) {
        mInflater = LayoutInflater.from(c);
        mContext = c;
//        this.catag_icon = catag_icon;

    }
    public int getCount() {
        return categoryContent.length;
    }
    public Object getItem(int position) {
        return null;
    }
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatDashboardDialogAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dialog_iteams, null);

            holder = new ChatDashboardDialogAdapter.ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.text);
            holder.icon  = (ImageView)convertView.findViewById(R.id.image);
            holder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.di_background);

            convertView.setTag(holder);
        } else {
            holder = (ChatDashboardDialogAdapter.ViewHolder) convertView.getTag();
        }

       /* holder.relativeLayout.setBackgroundResource(mThumbIds[position]);
        holder.title.setText(categoryContent[position]);
        holder.icon.setImageResource(mThumbIconsForChats[position]);*/
      /*  if(catag_icon == 1){



        }else {*/
            holder.relativeLayout.setBackgroundResource(mThumbIds[position]);
            holder.title.setText(categoryContent[position]);
            holder.icon.setImageResource(mThumbIconsForChats[position]);
     /*   }*/

        return convertView;
    }
    class ViewHolder {
        TextView title;
        ImageView icon;
        RelativeLayout relativeLayout;
    }

    private Integer[] mThumbIds = {

            R.drawable.di_new_chat,      R.drawable.di_view_profile,       R.drawable.di_media,
            R.drawable.di_hide_chat,     R.drawable.di_lock_chat,          R.drawable.di_clear_chat,
            R.drawable.di_themes,        R.drawable.di_block,              R.drawable.di_create_group_with_user,
            R.drawable.di_mute,          R.drawable.di_privacy,            R.drawable.di_schedule_msg,
            R.drawable.di_op_hide_chat,  R.drawable.di_share_youth_tube,   R.drawable.di_back

    };

    private Integer[] mThumbIconsForChats = {
            R.drawable.new_chat_icon,     R.drawable.new_group_icon,         R.drawable.broad_cast_icon,
            R.drawable.ivc_hide_chat,     R.drawable.ivc_lock_chat,          R.drawable.delete_icon,
            R.drawable.send_tag_icon,     R.drawable.ivc_view_profile,       R.drawable.more_settings_icon,
            R.drawable.faqs_icon,         R.drawable.ivc_search_icon,        R.drawable.ivc_back
    };

 /*   private Integer[] mThumbIconsForGroups= {
            R.drawable.new_group_icon,     R.drawable.broad_cast_icon,       R.drawable.exit_group,
            R.drawable.ivc_hide_chat,     R.drawable.ivc_lock_chat,          R.drawable.delete_icon,
            R.drawable.send_tag_icon,     R.drawable.ivc_view_profile,       R.drawable.more_settings_icon,
            R.drawable.faqs_icon,         R.drawable.ivc_search_icon,        R.drawable.ivc_back
    };*/

    private String[] categoryContent = {
            "New Chat", "New Group","Broadcast",
            "Hide Chat","Lock Chat", "Delete Chat",
            "Send A Tag","View Your Profile", "AppSettings",
            "FAQs", "Search","Back"
    };

/*    private String[] categoryContentInGroup = {
            "New Group","Broadcast","Exit Group",
            "Hide Group","Lock Group", "Delete Group",
            "Send A Tag","View Your Profile", "AppSettings",
            "FAQs", "Search","Back"
    };

    private String[] categoryContentInFriends = {
            "New Group","Broadcast","Exit Group",
            "Hide Group","Lock Group", "Delete Group",
            "Send A Tag","View Your Profile", "AppSettings",
            "FAQs", "Search","Back"
    };*/
}