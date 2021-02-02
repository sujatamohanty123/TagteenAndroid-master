package in.tagteen.tagteen.Model.webshows;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TxnTokenResponse {
    @SerializedName("mid")
    @Expose
    private String mid;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    @SerializedName("orderId")
    @Expose
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @SerializedName("amount")
    @Expose
    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @SerializedName("head")
    @Expose
    private Head tokenHeader;

    public Head getTokenHeader() {
        return tokenHeader;
    }

    public void setTokenHeader(Head tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    @SerializedName("body")
    @Expose
    private Body tokenBody;

    public Body getTokenBody() {
        return tokenBody;
    }

    public void setTokenBody(Body tokenBody) {
        this.tokenBody = tokenBody;
    }

    class Head {

    }

    public class Body {
        @SerializedName("txnToken")
        @Expose
        private String txnToken;

        public String getTxnToken() {
            return txnToken;
        }

        public void setTxnToken(String txnToken) {
            this.txnToken = txnToken;
        }
    }
}
