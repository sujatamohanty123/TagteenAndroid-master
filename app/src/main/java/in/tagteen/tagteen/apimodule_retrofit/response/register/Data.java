package in.tagteen.tagteen.apimodule_retrofit.response.register;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class Data {

  @SerializedName("tagged_number")
  private String taggedNumber;

  @SerializedName("device_id")
  private String deviceId;

  @SerializedName("profile_url")
  private String profileUrl;

  @SerializedName("mobile")
  private String mobile;

  @SerializedName("last_name")
  private String lastName;

  @SerializedName("is_tagged_user")
  private boolean isTaggedUser;

  @SerializedName("token")
  private String token;

  @SerializedName("country_code")
  private String countryCode;

  @SerializedName("user_id")
  private String userId;

  @SerializedName("dob")
  private String dob;

  @SerializedName("first_name")
  private String firstName;

  @SerializedName("verification_code")
  private String verificationCode;

  @SerializedName("email")
  private String email;

  public String getTaggedNumber() {
    return taggedNumber;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getProfileUrl() {
    return profileUrl;
  }

  public String getMobile() {
    return mobile;
  }

  public String getLastName() {
    return lastName;
  }

  public boolean isIsTaggedUser() {
    return isTaggedUser;
  }

  public String getToken() {
    return token;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getUserId() {
    return userId;
  }

  public String getDob() {
    return dob;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getVerificationCode() {
    return verificationCode;
  }

  public String getEmail() {
    return email;
  }

  @NotNull @Override public String toString() {
    return "Data{" +
        "taggedNumber='" + taggedNumber + '\'' +
        ", deviceId='" + deviceId + '\'' +
        ", profileUrl='" + profileUrl + '\'' +
        ", mobile='" + mobile + '\'' +
        ", lastName='" + lastName + '\'' +
        ", isTaggedUser=" + isTaggedUser +
        ", token='" + token + '\'' +
        ", countryCode='" + countryCode + '\'' +
        ", userId='" + userId + '\'' +
        ", dob='" + dob + '\'' +
        ", firstName='" + firstName + '\'' +
        ", verificationCode='" + verificationCode + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}