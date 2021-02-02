package in.tagteen.tagteen.chatting.model;

import android.os.Parcel;
import android.os.Parcelable;

import in.tagteen.tagteen.chatting.room.Message;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Friend implements Serializable {

    @SerializedName("_id")
    private String id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("profile_url")
    private String profileImage;

    private String email;

    @SerializedName("school_name")
    private String school;

    private String pincode;

    @SerializedName("tagged_number")
    private String tagNumber;

    private String bff;

    private boolean isOnline;

    private transient Message lastMessage;

    private transient int unSeenMessagesCount;

    public Friend(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public String getBff() {
        return bff;
    }

    public void setBff(String bff) {
        this.bff = bff;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    public void setUnSeenMessagesCount(int unSeenMessagesCount) {
        this.unSeenMessagesCount = unSeenMessagesCount;
    }

    public int getUnSeenMessagesCount() {
        return unSeenMessagesCount;
    }


}
