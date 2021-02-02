package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ChatMessangerModel {


    @SerializedName("_id")
    @Expose
    private String _id;




    @SerializedName("is_received")
    @Expose
    private boolean isReceivedStatus;

    @SerializedName("chat_receiver_id")
    @Expose
    private String chat_receiver_id;


    @SerializedName("chat_creator_id")
    @Expose
    private String chat_creator_id;

    @SerializedName("media_link")
    @Expose
    private String image;

    @SerializedName("content")
    @Expose
    private String content;


    @SerializedName("date_created")
    @Expose
    private String dateCreated;


    @SerializedName("is_group")
    @Expose
    private boolean isGroup;

    @SerializedName("chat_type_id")
    @Expose
    private String massageType;


    @SerializedName("client_chat_id")
    @Expose
    private String clientId;



    @SerializedName("is_private")
    @Expose
    private String isPrivate;

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }


    public String getMassageType() {
        return massageType;
    }

    public void setMassageType(String massageType) {
        this.massageType = massageType;
    }



    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getChat_receiver_id() {
        return chat_receiver_id;
    }

    public void setChat_receiver_id(String chat_receiver_id) {
        this.chat_receiver_id = chat_receiver_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isReceivedStatus() {
        return isReceivedStatus;
    }

    public void setReceivedStatus(boolean receivedStatus) {
        isReceivedStatus = receivedStatus;
    }

    public String getChat_creator_id() {
        return chat_creator_id;
    }

    public void setChat_creator_id(String chat_creator_id) {
        this.chat_creator_id = chat_creator_id;
    }
}
