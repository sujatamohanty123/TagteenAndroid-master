package in.tagteen.tagteen.chatting.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import android.content.Context;

import in.tagteen.tagteen.chatting.room.Message;

public class ChatDetailViewModel extends ViewModel {

    private LiveData<PagedList<Message>> messageList;

    public void init(Context context, String receiverId) {
//        MessageDao db = MessageDatabase.getApplicationContext(context).getMessageDao();
//        DataSource.Factory<Integer, Message> messageDataSource = db.getMessages(receiverId);
//
//        LivePagedListBuilder<Integer, Message> pagedListBuilder =
//                new LivePagedListBuilder<Integer, Message>(messageDataSource, 30);
//        messageList = pagedListBuilder.build();
    }

    public LiveData<PagedList<Message>> getMessageList() {
        return messageList;
    }

}