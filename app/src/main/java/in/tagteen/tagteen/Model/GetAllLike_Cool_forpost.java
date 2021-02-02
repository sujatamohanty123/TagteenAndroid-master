package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAllLike_Cool_forpost {

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

    public class Data {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("cool")
        @Expose
        private List<Cool> cool = null;
        @SerializedName("like")
        @Expose
        private List<Cool> like = null;
        @SerializedName("image")
        @Expose
        private List<Object> image = null;
        @SerializedName("conversation")
        @Expose
        private List<Object> conversation = null;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Cool> getCool() {
            return cool;
        }

        public void setCool(List<Cool> cool) {
            this.cool = cool;
        }

        public List<Cool> getLike() {
            return like;
        }

        public void setLike(List<Cool> like) {
            this.like = like;
        }

        public List<Object> getImage() {
            return image;
        }

        public void setImage(List<Object> image) {
            this.image = image;
        }

        public List<Object> getConversation() {
            return conversation;
        }

        public void setConversation(List<Object> conversation) {
            this.conversation = conversation;
        }

        public class Cool {

            @SerializedName("_id")
            @Expose
            private String id;
            @SerializedName("timestamp")
            @Expose
            private long timestamp;
            @SerializedName("user_id")
            @Expose
            private UserId userId;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public UserId getUserId() {
                return userId;
            }

            public void setUserId(UserId userId) {
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

}