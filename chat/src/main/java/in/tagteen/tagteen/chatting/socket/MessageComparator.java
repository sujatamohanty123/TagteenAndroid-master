package in.tagteen.tagteen.chatting.socket;

import android.util.Log;

import java.util.Comparator;
import java.util.Date;

import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 5/12/2019.
 */
public class MessageComparator implements Comparator<Friend> {

    @Override
    public int compare(Friend friend1, Friend friend2) {
        Message msg1 = friend1.getLastMessage();
        Message msg2 = friend2.getLastMessage();

        Date msgDate1 = new Date(msg1.getMessageType() == Message.MESSAGE_IN
                ? msg1.getServerDate() : msg1.getDate());

        Date msgDate2 = new Date(msg2.getMessageType() == Message.MESSAGE_IN
                ? msg2.getServerDate() : msg2.getDate());

        if (msgDate1.compareTo(msgDate2) > 0)
            return -1;
        else if (msgDate1.compareTo(msgDate2) < 0)
            return 1;
        return 0;
    }
}
