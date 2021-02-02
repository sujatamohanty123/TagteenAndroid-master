package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RewardsPaymentOptionInputModel {
    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("google_pay")
    @Expose
    private String googlePay;

    @SerializedName("paytm")
    @Expose
    private String paytm;

    @SerializedName("phone_pe")
    @Expose
    private String phonePe;

    public RewardsPaymentOptionInputModel(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGooglePay() {
        return googlePay;
    }

    public void setGooglePay(String googlePay) {
        this.googlePay = googlePay;
    }

    public String getPaytm() {
        return paytm;
    }

    public void setPaytm(String paytm) {
        this.paytm = paytm;
    }

    public String getPhonePe() {
        return phonePe;
    }

    public void setPhonePe(String phonePe) {
        this.phonePe = phonePe;
    }
}
