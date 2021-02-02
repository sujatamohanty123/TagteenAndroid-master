package in.tagteen.tagteen.chatting.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.annotation.NonNull;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT serial_id FROM Mes"
        + "sage WHERE owner_id IN(:ownerId) AND client_id IN(:clientId) AND serial_id>(:offset)")
    List<Integer> getTotalMessageIds(@NonNull String ownerId,
                                     @NonNull String clientId,
                                     int offset);

    @Query("SELECT * FROM Message WHERE owner_id IN(:ownerId) AND client_id IN(:clientId) AND serial_id <= (:startId) AND serial_id >= (:endId)")
    List<Message> getMessages(@NonNull String ownerId,
                              @NonNull String clientId,
                              int startId,
                              int endId);

    @Query("SELECT * FROM Message WHERE owner_id IN(:ownerId) ORDER BY serial_id DESC LIMIT 1")
    LiveData<Message> getMessage(@NonNull String ownerId);

    @Query("SELECT * FROM Message WHERE owner_id IN(:ownerId) AND client_id IN(:clientId) ORDER BY serial_id DESC LIMIT 1")
    Message getMessageByReceiverId(@NonNull String ownerId,
                                   @NonNull String clientId);

    @Insert
    void addMessage(@NonNull Message message);

//    @Query("SELECT COUNT(*) FROM Message WHERE client_id IN(:receiverId)")
//    int getMessagesCount(@NonNull String receiverId);

//    @Query("UPDATE Message SET server_message_id = :serverMessageId, server_date = :serverDate WHERE message_id IN(:messageId) AND client_id IN(:receiverId)")
//    void updateMessage(@NonNull String messageId,
//                       @NonNull String serverMessageId,
//                       @NonNull String receiverId,
//                       @NonNull long serverDate);

    @Query("UPDATE Message SET server_message_id = :serverMessageId, server_date = :serverDate, status = :status WHERE owner_id IN(:ownerId) AND client_id IN(:clientId) AND message_id IN(:messageId)")
    void updateMessage(@NonNull String serverMessageId,
                       long serverDate,
                       @Message.Status int status,
                       @NonNull String messageId,
                       @NonNull String ownerId,
                       @NonNull String clientId);

    @Query("UPDATE Message SET status = :status WHERE owner_id IN(:ownerId) AND client_id IN(:clientId) AND server_message_id IN(:serverMessageIds)")
    void updateMessageStatus(
            @Message.Status int status,
            @NonNull String ownerId,
            @NonNull String clientId,
            @NonNull String... serverMessageIds);

//    @Query("UPDATE Message SET status = :statusTo WHERE client_id IN(:receiverId) AND type IN(:messageType) AND status IN(:statusIn)")
//    void updateSeenStatusForAll(@NonNull String receiverId,
//                                @Message.Type int messageType,
//                                @Message.Status int statusTo,
//                                @Message.Status int statusIn);

//    @Query("SELECT message_id FROM Message WHERE client_id IN(:receiverId) AND type IN(:messageType) AND status IN(:statusIn)")
//    List<String> getUnSeenOutGoingMessageIds(@NonNull String receiverId,
//                                             @Message.Type int messageType,
//                                             @Message.Status int statusIn);

    @Query("SELECT server_message_id FROM Message WHERE owner_id IN(:ownerId) AND client_id IN(:clientId) AND type IN(:messageType) AND status IN(:status)")
    List<String> getUnseenMessageIds(@NonNull String ownerId,
                                     @NonNull String clientId,
                                     @Message.Type int messageType,
                                     @Message.Status int status);

    @Query("SELECT COUNT(*) FROM Message WHERE owner_id IN(:ownerId) AND client_id IN(:clientId) AND type IN(:messageType) AND status NOT IN(:status)")
    int getUnseenInMessageCount(@NonNull String ownerId,
                                @NonNull String clientId,
                                @Message.Type int messageType,
                                @Message.Status int status);

    @Query("SELECT message_id FROM Message WHERE owner_id IN(:ownerId) AND client_id IN(:clientId) AND message_id IN(:messageId)")
    String getMessageId(@NonNull String messageId,
                        @NonNull String ownerId,
                        @NonNull String clientId);
}
