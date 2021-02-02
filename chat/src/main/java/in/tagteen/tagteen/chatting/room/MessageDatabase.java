package in.tagteen.tagteen.chatting.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import in.tagteen.tagteen.chatting.socket.ChatProcessLifecycleManager;

@Database(entities = {Message.class}, version = 1, exportSchema = false)
public abstract class MessageDatabase extends RoomDatabase {

    private static MessageDatabase database;

    public static MessageDatabase getInstance() {
        if (database == null)
            database = Room.databaseBuilder(ChatProcessLifecycleManager.getApplicationContext(),
                    MessageDatabase.class, "message_db").build();
        return database;
    }

    public abstract MessageDao getMessageDao();

}
