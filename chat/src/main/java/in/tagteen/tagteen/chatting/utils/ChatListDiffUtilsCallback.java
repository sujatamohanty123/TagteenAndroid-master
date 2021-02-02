package in.tagteen.tagteen.chatting.utils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Date;
import java.util.List;

import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 5/22/2019.
 */
public class ChatListDiffUtilsCallback extends DiffUtil.Callback {

    private final List<Friend> oldFriends;
    private final List<Friend> newFriends;

    public ChatListDiffUtilsCallback(List<Friend> oldFriends, List<Friend> newFriends) {
        this.oldFriends = oldFriends;
        this.newFriends = newFriends;
    }

    @Override
    public int getOldListSize() {
        return oldFriends.size();
    }

    @Override
    public int getNewListSize() {
        return newFriends.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldFriends.get(oldItemPosition).getId().equals(newFriends.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Message oldMessage = oldFriends.get(oldItemPosition).getLastMessage();
        Message newMessage = newFriends.get(newItemPosition).getLastMessage();
        return new Date(oldMessage.getDate()).compareTo(new Date(newMessage.getDate())) == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return newFriends.get(newItemPosition);
    }
}
