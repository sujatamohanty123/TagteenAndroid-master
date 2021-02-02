package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayattama Prusty on 04-Sep-17.
 */

public class AllSelfyList {

    @SerializedName("success")
    @Expose
    public boolean success;

    @SerializedName("data")
    @Expose
    public List<SelfyData> selfyData;

    public class SelfyData {

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

        @SerializedName("gender")
        @Expose
        private String gender;

        @SerializedName("image")
        @Expose
        private ArrayList<Image> images;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
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

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public ArrayList<Image> getImages() {
            return images;
        }

        public void setImages(ArrayList<Image> images) {
            this.images = images;
        }
    }

    public class Image {
        @SerializedName("url")
        @Expose
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SelfyData> getSelfyData() {
        return selfyData;
    }

    public void setSelfyData(List<SelfyData> selfyData) {
        this.selfyData = selfyData;
    }
}
