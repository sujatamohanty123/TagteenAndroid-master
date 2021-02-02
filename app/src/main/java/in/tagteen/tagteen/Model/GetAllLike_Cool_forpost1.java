package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sujata on 25-01-2018.
 */

public class GetAllLike_Cool_forpost1 {
    @SerializedName("data")
    @Expose
    private List<Cool> data;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public List<Cool> getData() {
        return data;
    }

    public void setData(List<Cool> data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

        public class Cool {

            @SerializedName("_id")
            @Expose
            private String id;
            @SerializedName("timestamp")
            @Expose
            private String timestamp;
            @SerializedName("user_id")
            @Expose
            private GetAllLike_Cool_forpost.Data.Cool.UserId userId;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }

            public GetAllLike_Cool_forpost.Data.Cool.UserId getUserId() {
                return userId;
            }

            public void setUserId(GetAllLike_Cool_forpost.Data.Cool.UserId userId) {
                this.userId = userId;
            }

            public class UserId {

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

            }



    }
}
