package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ChatHistoryResponseModel {
    @SerializedName("data")
    @Expose
    private ArrayList<ChatMessangerModel> chatMessangerModelArrayList;

    @SerializedName("success")
    @Expose
    private boolean success;

    public ArrayList<ChatMessangerModel> getChatMessangerModelArrayList() {
        return chatMessangerModelArrayList;
    }

    public void setChatMessangerModelArrayList(ArrayList<ChatMessangerModel> chatMessangerModelArrayList) {
        this.chatMessangerModelArrayList = chatMessangerModelArrayList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }



}
