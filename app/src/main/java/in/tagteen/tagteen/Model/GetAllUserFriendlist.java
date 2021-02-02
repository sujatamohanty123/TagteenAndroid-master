package in.tagteen.tagteen.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetAllUserFriendlist implements Serializable{

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static class Data {

        @SerializedName("pending_user_list")
        @Expose
        private List<FriendsUserList> pendingUserList = null;
        @SerializedName("friends_user_list")
        @Expose
        private List<FriendsUserList> friendsUserList = null;
        @SerializedName("bff")
        @Expose
        private List<FriendsUserList> bff = null;

        public List<FriendsUserList> getPendingUserList() {
            return pendingUserList;
        }

        public void setPendingUserList(List<FriendsUserList> pendingUserList) {
            this.pendingUserList = pendingUserList;
        }

        public List<FriendsUserList> getFriendsUserList() {
            return friendsUserList;
        }

        public void setFriendsUserList(List<FriendsUserList> friendsUserList) {
            this.friendsUserList = friendsUserList;
        }

        public List<FriendsUserList> getBff() {
            return bff;
        }

        public void setBff(List<FriendsUserList> bff) {
            this.bff = bff;
        }

    }


    public class FriendsUserList implements Serializable,Comparable{

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("profile_url")
        @Expose
        private String profileUrl;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("school_name")
        @Expose
        private String schoolName;
        @SerializedName("pincode")
        @Expose
        private String pincode;

        @SerializedName("tagged_number")
        @Expose
        private String taggedNumber;

        @SerializedName("bff")
        @Expose
        private boolean bff;

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

        public String getProfileUrl() {
            return profileUrl;
        }

        public void setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getTaggedNumber() {
            return taggedNumber;
        }

        public void setTaggedNumber(String taggedNumber) {
            this.taggedNumber = taggedNumber;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            return 0;
        }

        public boolean isBff() {
            return bff;
        }

        public void setBff(boolean bff) {
            this.bff = bff;
        }
    }

}
