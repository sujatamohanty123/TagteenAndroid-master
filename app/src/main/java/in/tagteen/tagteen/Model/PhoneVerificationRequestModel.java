package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vinoth on 30-Oct-18.
 */

public class PhoneVerificationRequestModel {

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("country_code")
    @Expose
    private String countryCode;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
