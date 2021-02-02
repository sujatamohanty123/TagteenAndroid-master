package io.agora.openlive.chatlist;

public class ChatModel {

    String userId, userName, comment;

    public ChatModel(String userId, String userName, String comment) {
        this.userId = userId;
        this.userName = userName;
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
