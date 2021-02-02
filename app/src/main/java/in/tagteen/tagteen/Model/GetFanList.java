package in.tagteen.tagteen.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jayattama Prusty on 01-Sep-17.
 */

public class GetFanList {

    @SerializedName("success")
    @Expose
    boolean success;


    @SerializedName("data")
    @Expose
    private List<UserData> data = null;

    public List<UserData> getData() {
        return data;
    }

    public void setData(List<UserData> data) {
        this.data = data;
    }

    public class UserData implements Comparable,Serializable {
        @SerializedName("is_my_fan")
        @Expose
        private boolean is_myfan;

        @SerializedName("isTaggedMe")
        @Expose
        private boolean isTaggedMe;

        @SerializedName("first_name")
        @Expose
        private String first_name;

        @SerializedName("last_name")
        @Expose
        private String last_name;

        @SerializedName("profile_url")
        @Expose
        private String profile_url;

        @SerializedName("email")
        @Expose
        private String email;

        @SerializedName("school_name")
        @Expose
        private String school_name;

        @SerializedName("pincode")
        @Expose
        private String pincode;

        @SerializedName("tagged_number")
        @Expose
        private String tagged_number;

        @SerializedName("user_id")
        @Expose
        private String user_id;

        public boolean is_myfan() {
            return is_myfan;
        }

        public void setIs_myfan(boolean is_myfan) {
            this.is_myfan = is_myfan;
        }

        public boolean isTaggedMe() {
            return isTaggedMe;
        }

        public void setTaggedMe(boolean taggedMe) {
            isTaggedMe = taggedMe;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSchool_name() {
            return school_name;
        }

        public void setSchool_name(String school_name) {
            this.school_name = school_name;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getTagged_number() {
            return tagged_number;
        }

        public void setTagged_number(String tagged_number) {
            this.tagged_number = tagged_number;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            return 0;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
