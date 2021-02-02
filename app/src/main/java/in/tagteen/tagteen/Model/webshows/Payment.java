package in.tagteen.tagteen.Model.webshows;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment {
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @SerializedName("user_id")
    @Expose
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @SerializedName("webshows_id")
    @Expose
    private String webshowId;

    public String getWebshowId() {
        return webshowId;
    }

    public void setWebshowId(String webshowId) {
        this.webshowId = webshowId;
    }

    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @SerializedName("payment_platform")
    @Expose
    private String paymentPlatform;

    public String getPaymentPlatform() {
        return paymentPlatform;
    }

    public void setPaymentPlatform(String paymentPlatform) {
        this.paymentPlatform = paymentPlatform;
    }
}
