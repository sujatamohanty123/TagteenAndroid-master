package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RewardsInfoModels {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private List<RewardsInfoModel> dataList;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<RewardsInfoModel> getDataList() {
        return dataList;
    }

    public void setDataList(List<RewardsInfoModel> dataList) {
        this.dataList = dataList;
    }

    public class RewardsInfoModel {
        @SerializedName("user_info")
        @Expose
        private UserInfo userInfo;

        @SerializedName("total_video_post_count")
        @Expose
        private int totalVideoPostCount;

        @SerializedName("post_info")
        @Expose
        private List<PostInfo> postInfoList;

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public int getTotalVideoPostCount() {
            return totalVideoPostCount;
        }

        public void setTotalVideoPostCount(int totalVideoPostCount) {
            this.totalVideoPostCount = totalVideoPostCount;
        }

        public List<PostInfo> getPostInfoList() {
            return postInfoList;
        }

        public void setPostInfoList(List<PostInfo> postInfoList) {
            this.postInfoList = postInfoList;
        }
    }

    public class UserInfo {
        @SerializedName("_id")
        @Expose
        private String userId;

        @SerializedName("first_name")
        @Expose
        private String firstName;

        @SerializedName("last_name")
        @Expose
        private String lastName;

        @SerializedName("profile_url")
        @Expose
        private String profilePicUrl;

        @SerializedName("school_name")
        @Expose
        private String acedamicName;

        @SerializedName("pincode")
        @Expose
        private String pincode;

        @SerializedName("tagged_number")
        @Expose
        private String taggedNumber;

        @SerializedName("paytm")
        @Expose
        private String paytm;

        @SerializedName("google_pay")
        @Expose
        private String googlePay;

        @SerializedName("phone_pe")
        @Expose
        private String phonePe;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public String getProfilePicUrl() {
            return profilePicUrl;
        }

        public void setProfilePicUrl(String profilePicUrl) {
            this.profilePicUrl = profilePicUrl;
        }

        public String getAcedamicName() {
            return acedamicName;
        }

        public void setAcedamicName(String acedamicName) {
            this.acedamicName = acedamicName;
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

        public String getPaytm() {
            return paytm;
        }

        public void setPaytm(String paytm) {
            this.paytm = paytm;
        }

        public String getGooglePay() {
            return googlePay;
        }

        public void setGooglePay(String googlePay) {
            this.googlePay = googlePay;
        }

        public String getPhonePe() {
            return phonePe;
        }

        public void setPhonePe(String phonePe) {
            this.phonePe = phonePe;
        }
    }

    public class PostInfo {
        @SerializedName("_id")
        @Expose
        private String postId;

        @SerializedName("like_count")
        @Expose
        private int likeCount;

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }
    }
}
