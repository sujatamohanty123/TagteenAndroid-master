package in.tagteen.tagteen.Model;


import java.io.Serializable;

import in.tagteen.tagteen.LocalCasha.DatabaseContracts;

public class ChatMessage implements Serializable {

    private String clintId;
    private String serverId;
    private String createrId;
    private String receiverId;
    private String isFromMe;
    private String chatPageId;
    private String isGroup;
    private String isPrivate;
    private String msgType;
    private String message;
    private String mediaLink;
    private String localPath;
    private String date;
    private String Time;
    private int privateMessageTime;
    private String msgViewedStatus;
    private String thumbNailImage;
    private String downLoadStatus;
    private String upLoadStatus;
    private String docFileName;
    private String docFileSize;
    private String sendStatus;
    private String eventType;
    private String isReply;
    private String replyToMSGId;
    private String replyToChatType;
    private String replyToContent;
    private String replyToLink;
    private String friendName;
    private String friendId;
    private int isLocked;
    private String unreadCount;
    private String jsonObject;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public int isLocked() {
        return isLocked;
    }

    public void setLocked(int locked) {
        isLocked = locked;
    }

    public ChatMessage() {
    }

    ;

    public ChatMessage(String isFromMe, String clintId, String createrId, String receiverId, String msgType, String message, String mediaLink,
                       String time, String chatPageId, String isGroup, String isPrivate, int privateMessageTime, String sendStatus, String serverId, String localPath, String date) {
        this.isFromMe = isFromMe;
        this.clintId = clintId;
        this.createrId = createrId;
        this.receiverId = receiverId;
        this.msgType = msgType;
        this.message = message;
        this.mediaLink = mediaLink;
        this.Time = time;
        this.chatPageId = chatPageId;
        this.isGroup = isGroup;
        this.isPrivate = isPrivate;
        this.privateMessageTime = privateMessageTime;
        this.date = date;
        this.serverId = serverId;
        this.msgViewedStatus = DatabaseContracts.PENDING;
        this.thumbNailImage = null;
        this.downLoadStatus = DatabaseContracts.PENDING;
        this.upLoadStatus = DatabaseContracts.PENDING;
        this.sendStatus = sendStatus;
        this.docFileName = null;
        this.docFileSize = null;
        this.localPath = localPath;
    }

    ;

    public ChatMessage(String isFromMe, String clintId, String createrId, String receiverId, String msgType, String message, String mediaLink,
                       String time, String chatPageId, String isGroup, String isPrivate, int privateMessageTime, String serverId,
                       String msgViewedStatus, String thumbNailImage, String downLoadStatus, String upLoadStatus, String sendStatus, String localPath) {

        this.isFromMe = isFromMe;
        this.clintId = clintId;
        this.createrId = createrId;
        this.receiverId = receiverId;
        this.msgType = msgType;
        this.message = message;
        this.mediaLink = mediaLink;
        this.Time = time;
        this.chatPageId = chatPageId;
        this.isGroup = isGroup;
        this.isPrivate = isPrivate;
        this.privateMessageTime = privateMessageTime;
        this.date = date;
        this.serverId = serverId;
        this.msgViewedStatus = msgViewedStatus;
        this.thumbNailImage = thumbNailImage;
        this.downLoadStatus = downLoadStatus;
        this.upLoadStatus = upLoadStatus;
        this.docFileName = null;
        this.docFileSize = null;
        this.sendStatus = sendStatus;
        this.localPath = localPath;
    }

    ;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getIsReply() {
        return isReply;
    }

    public void setIsReply(String isReply) {
        this.isReply = isReply;
    }

    public String getReplyToMSGId() {
        return replyToMSGId;
    }

    public void setReplyToMSGId(String replyToMSGId) {
        this.replyToMSGId = replyToMSGId;
    }

    public String getReplyToChatType() {
        return replyToChatType;
    }

    public void setReplyToChatType(String replyToChatType) {
        this.replyToChatType = replyToChatType;
    }

    public String getReplyToContent() {
        return replyToContent;
    }

    public void setReplyToContent(String replyToContent) {
        this.replyToContent = replyToContent;
    }

    public String getReplyToLink() {
        return replyToLink;
    }

    public void setReplyToLink(String replyToLink) {
        this.replyToLink = replyToLink;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getClintId() {
        return clintId;
    }

    public void setServerChatId(String clintId) {
        this.clintId = clintId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getIsFromMe() {
        return isFromMe;
    }

    public void setIsFromMe(String isFromMe) {
        this.isFromMe = isFromMe;
    }

    public String getChatPageId() {
        return chatPageId;
    }

    public void setChatPageId(String chatPageId) {
        this.chatPageId = chatPageId;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public int getPrivateMessageTime() {
        return privateMessageTime;
    }

    public void setPrivateMessageTime(int privateMessageTime) {
        this.privateMessageTime = privateMessageTime;
    }

    public String getMsgViewedStatus() {
        return msgViewedStatus;
    }

    public void setMsgViewedStatus(String msgViewedStatus) {
        this.msgViewedStatus = msgViewedStatus;
    }

    public String getThumbNailImage() {
        return thumbNailImage;
    }

    public void setThumbNailImage(String thumbNailImage) {
        this.thumbNailImage = thumbNailImage;
    }

    public String getDownLoadStatus() {
        return downLoadStatus;
    }

    public void setDownLoadStatus(String downLoadStatus) {
        this.downLoadStatus = downLoadStatus;
    }

    public String getUpLoadStatus() {
        return upLoadStatus;
    }

    public void setUpLoadStatus(String upLoadStatus) {
        this.upLoadStatus = upLoadStatus;
    }

    public String getDocFileName() {
        return docFileName;
    }

    public void setDocFileName(String docFileName) {
        this.docFileName = docFileName;
    }

    public String getDocFileSize() {
        return docFileSize;
    }

    public void setDocFileSize(String docFileSize) {
        this.docFileSize = docFileSize;
    }


    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

}