package in.tagteen.tagteen.chatting.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import in.tagteen.tagteen.chatting.socket.SocketConstants;

@Entity(indices = {@Index(value = {"message_id", "server_message_id"}, unique = true),
        @Index(value = {"owner_id", "client_id"})})
public class Message implements Serializable {

    public static final int MESSAGE_IN = 0x64;
    public static final int MESSAGE_OUT = 0x65;

    public static final int NOT_SENT = 0x0;
    public static final int SENT = 0x1;
    public static final int DELIVERED = 0x2;
    public static final int SEEN = 0x3;

    @IntDef({MESSAGE_IN, MESSAGE_OUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    @IntDef({NOT_SENT, SENT, DELIVERED, SEEN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    @IntDef({SocketConstants.MessageType.PHOTO,
            SocketConstants.MessageType.VIDEO,
            SocketConstants.MessageType.AUDIO,
            SocketConstants.MessageType.FACIAL_REACTION,
            SocketConstants.MessageType.PING,
            SocketConstants.MessageType.SOUND_EMOJI,
            SocketConstants.MessageType.TEXT,
            SocketConstants.MessageType.DOCUMENT,
            SocketConstants.MessageType.REPLY,
            SocketConstants.MessageType.LOCATION,
            SocketConstants.MessageType.YOUTUBE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChatType {
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "serial_id")
    private int id;

    @ColumnInfo(name = "message_id")
    private String msgId;

    @ColumnInfo(name = "server_message_id")
    private String serverMsgId;

    @ColumnInfo(name = "owner_id")
    private String ownerId;

    @ColumnInfo(name = "client_id")
    private String clientId;

    private String message;

    @ColumnInfo(name = "status")
    private int messageStatus;

    @ColumnInfo(name = "type")
    private int messageType;

    @ColumnInfo(name = "chat_type")
    private int chatType;

    @ColumnInfo(name = "server_date")
    private long serverDate;

    private long date;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageStatus(@Status int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(@Type int messageType) {
        this.messageType = messageType;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getServerMsgId() {
        return serverMsgId;
    }

    public void setServerMsgId(String serverMsgId) {
        this.serverMsgId = serverMsgId;
    }

    public long getServerDate() {
        return serverDate;
    }

    public void setServerDate(long serverDate) {
        this.serverDate = serverDate;
    }

    public void setChatType(@ChatType int chatType) {
        this.chatType = chatType;
    }

    public int getChatType() {
        return chatType;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", msgId='" + msgId + '\'' +
                ", serverMsgId='" + serverMsgId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", message='" + message + '\'' +
                ", messageStatus=" + messageStatus +
                ", messageType=" + messageType +
                ", date=" + date +
                ", chatType=" + chatType +
                ", serverDate=" + serverDate +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return this.getMsgId().equals(((Message) obj).getMsgId());
    }
}
