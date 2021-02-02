package in.tagteen.tagteen.chatting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.tagteen.tagteen.chatting.R;
import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.utils.ChatFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony00 on 2/11/2019.
 */
public class CircularImagesList extends HorizontalScrollView {

    private List<Friend> friendsList = new ArrayList<>();
    private LinearLayout container;
    private OnUserClickListener onUserClickListener;

    public CircularImagesList(Context context) {
        this(context, null);
    }

    public CircularImagesList(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CircularImagesList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        container = new LinearLayout(context);
        addView(container,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setUsers(List<Friend> friends){
        friendsList.clear();
        friendsList.addAll(friends);
        invalidateUsers();
    }

    public void addUser(Friend friend){
        friendsList.add(friend);
        invalidateUsers();
    }

    public List<Friend> getUsersList() {
        return friendsList;
    }

    private void invalidateUsers(){
        for(Friend friend:friendsList){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.item_online_user,null, true);
            ChatFactory.loadCircleImage(getContext(),
                    friend.getProfileImage(),
                    (ImageView)layout.findViewById(R.id.image));
            ((TextView)layout.findViewById(R.id.name)).setText(friend.getName());
            layout.setOnClickListener(v -> {
                if(onUserClickListener!=null)
                    onUserClickListener.onUserClick(friend);
            });
            container.addView(layout);
        }
    }

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public interface OnUserClickListener{
        void onUserClick(Friend friend);
    }

}
