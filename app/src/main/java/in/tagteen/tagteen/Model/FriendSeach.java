package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by lovekushvishwakarma on 25/10/17.
 */

public class FriendSeach {

    @SerializedName("success")
    @Expose
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @SerializedName("data")
    @Expose
    private ArrayList<UserInfo> userInfos;

    public ArrayList<UserInfo> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(ArrayList<UserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    public class UserInfo {

        boolean isFan;


        @SerializedName("_id")
        @Expose
        private String _id;

        @SerializedName("first_name")
        @Expose
        private String first_name;

        @SerializedName("last_name")
        @Expose
        private String last_name;

        @SerializedName("profile_url")
        @Expose
        private String profile_url;

        @SerializedName("school_name")
        @Expose
        private String school_name;

        @SerializedName("tagged_number")
        @Expose
        private String tagged_number;

        @SerializedName("rank")
        @Expose
        private int rank;

        public boolean isFan() {
            return isFan;
        }

        public void setFan(boolean fan) {
            isFan = fan;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTagged_number() {
            return tagged_number;
        }

        public void setTagged_number(String tagged_number) {
            this.tagged_number = tagged_number;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public String getProfile_url() {
            return profile_url;
        }

        public void setProfile_url(String profile_url) {
            this.profile_url = profile_url;
        }

        public String getSchool_name() {
            return school_name;
        }

        public void setSchool_name(String school_name) {
            this.school_name = school_name;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
    }
}

