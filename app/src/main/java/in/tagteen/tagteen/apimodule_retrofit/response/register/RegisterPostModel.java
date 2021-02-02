package in.tagteen.tagteen.apimodule_retrofit.response.register;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class RegisterPostModel {

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("password")
    private String password;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("profile_url")
    private String profileUrl;

    @SerializedName("dob")
    private String dob;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("verification_code")
    private String verificationCode;

    @SerializedName("email")
    private String email;

    public static RegisterPostModel transform(String firstName, String lastName, String dob, String email, String password, String mobile, String profileUrl, String deviceId, String verificationCode) {
        RegisterPostModel model = new RegisterPostModel();
        model.setCountryCode("+91");
        model.setFirstName(firstName);
        model.setLastName(lastName);
        model.setDob(dob);
        model.setEmail(email);
        model.setPassword(password);
        model.setMobile(mobile);
        model.setProfileUrl(profileUrl);
        model.setDeviceId(deviceId);
        model.setVerificationCode(verificationCode);
        return model;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    @Override
    public String toString() {
        return
                "RegisterPostModel{" +
                        "country_code = '" + countryCode + '\'' +
                        ",password = '" + password + '\'' +
                        ",device_id = '" + deviceId + '\'' +
                        ",profile_url = '" + profileUrl + '\'' +
                        ",dob = '" + dob + '\'' +
                        ",mobile = '" + mobile + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",verification_code = '" + verificationCode + '\'' +
                        ",email = '" + email + '\'' +
                        "}";
    }
}