package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sujata on 14-06-2018.
 */
public class GetAllLReact implements Serializable {
    public GetAllLReact.data getData() {
        return data;
    }

    public void setData(GetAllLReact.data data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private data data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @SerializedName("success")
    @Expose
    private Boolean success;
    public class data implements Serializable {
        @SerializedName("d_like")
        @Expose
        private List<d_like> d_like;
        @SerializedName("d_dab")
        @Expose
        private List<d_like> d_dab;
        @SerializedName("d_nerd")
        @Expose
        private List<d_like> d_nerd;
        @SerializedName("d_swag")
        @Expose
        private List<d_like> d_swag;

        public List<GetAllLReact.data.d_like> getD_cool() {
            return d_cool;
        }

        public void setD_cool(List<GetAllLReact.data.d_like> d_cool) {
            this.d_cool = d_cool;
        }

        public List<GetAllLReact.data.d_like> getD_like() {
            return d_like;
        }

        public void setD_like(List<GetAllLReact.data.d_like> d_like) {
            this.d_like = d_like;
        }

        public List<GetAllLReact.data.d_like> getD_dab() {
            return d_dab;
        }

        public void setD_dab(List<GetAllLReact.data.d_like> d_dab) {
            this.d_dab = d_dab;
        }

        public List<GetAllLReact.data.d_like> getD_nerd() {
            return d_nerd;
        }

        public void setD_nerd(List<GetAllLReact.data.d_like> d_nerd) {
            this.d_nerd = d_nerd;
        }

        public List<GetAllLReact.data.d_like> getD_swag() {
            return d_swag;
        }

        public void setD_swag(List<GetAllLReact.data.d_like> d_swag) {
            this.d_swag = d_swag;
        }

        @SerializedName("d_cool")
        @Expose
        private List<d_like> d_cool;
        public class d_like implements Serializable {
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
            @SerializedName("timestamp")
            @Expose
            private long timestamp;

            public String getFlag() {
                return flag;
            }

            public void setFlag(String flag) {
                this.flag = flag;
            }

            private String flag;

            public String getSchoolName() {
                return schoolName;
            }

            public void setSchoolName(String schoolName) {
                this.schoolName = schoolName;
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

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            @SerializedName("school_name")
            @Expose
            private String schoolName;
            @SerializedName("pincode")
            @Expose
            private String pincode;
            @SerializedName("tagged_number")
            @Expose
            private String taggedNumber;
        }
    }
}
