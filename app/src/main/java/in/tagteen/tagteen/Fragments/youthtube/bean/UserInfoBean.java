package in.tagteen.tagteen.Fragments.youthtube.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfoBean implements Serializable {
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
    private String profileUrl;

    @SerializedName("tagged_number")
    @Expose
    private String tagNo;

    @SerializedName("rank")
    @Expose
    private int rank;

    private String userName;

    public UserInfoBean(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getTagNo() {
        return tagNo;
    }

    public void setTagNo(String tagNo) {
        this.tagNo = tagNo;
    }

    public String getPrintableName() {
        String ret = this.firstName;
        if (this.lastName != null) {
            ret += " " + this.lastName;
        }
        return ret;
    }

    public int getRank() {
        return rank;
    }
}
