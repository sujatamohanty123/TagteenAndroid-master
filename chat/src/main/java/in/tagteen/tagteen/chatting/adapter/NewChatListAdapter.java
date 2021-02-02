package in.tagteen.tagteen.chatting.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import in.tagteen.tagteen.chatting.R;
import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.utils.ChatFactory;

/**
 * Created by tony00 on 5/4/2019.
 */
public class NewChatListAdapter extends BaseAdapter {
    private List<Friend> friends;

    public NewChatListAdapter(List<Friend> friends) {
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Friend getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = (LinearLayout) convertView;
        if (layout == null) {
            layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.new_friend_item, parent, false);
        }
        ChatFactory.loadCircleImage(parent.getContext(),
                getItem(position).getProfileImage(), layout.findViewById(R.id.image_view));
//        ChatFactory.loadImage(parent.getContext(),
//                getItem(position).getProfileImage(),
//                layout.findViewById(R.id.image_view));
        ((TextView)layout.findViewById(R.id.name_view)).setText(getItem(position).getName());
        return layout;
    }

}
