package in.tagteen.tagteen.chatting.room;

import android.os.AsyncTask;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.room.tasks.DBTaskFactory;
import in.tagteen.tagteen.chatting.room.tasks.FetchLastMessageFactory;
import in.tagteen.tagteen.chatting.room.tasks.MessagesCountFactory;
import in.tagteen.tagteen.chatting.socket.MessageComparator;

/**
 * Created by tony00 on 5/25/2019.
 */
public class RecentMessageExecutor extends AsyncTask<Friend, Void, List<Friend>> {

    private RecentMessageExecutedListener listener;

    public RecentMessageExecutor(@Nullable RecentMessageExecutedListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Friend> doInBackground(Friend... friends) {
        List<Friend> friendWithMessage = new ArrayList<>();
        for (Friend friend : friends) {
            try {
                Message message = DBQueryExecutor.submitTask(DBTaskFactory.getCallableTask(
                        new FetchLastMessageFactory(friend.getId())
                ));
                if (message != null) {
                    int count = DBQueryExecutor.submitTask(
                            DBTaskFactory.getCallableTask(new MessagesCountFactory(friend.getId())));
                    friend.setLastMessage(message);
                    friend.setUnSeenMessagesCount(count);
                    friendWithMessage.add(friend);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (friendWithMessage.size() > 0)
            Collections.sort(friendWithMessage, new MessageComparator());
        return friendWithMessage;
    }

    @Override
    protected void onPostExecute(List<Friend> friends) {
        super.onPostExecute(friends);
        if (listener != null) listener.onExecuted(friends);
    }

    public interface RecentMessageExecutedListener {
        void onExecuted(List<Friend> friends);
    }

}