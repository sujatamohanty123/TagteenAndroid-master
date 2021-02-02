package in.tagteen.tagteen.Model;

import java.io.Serializable;

/**
 * Created by Sujata on 22-06-2018.
 */
public class NotificationResponseModel implements Serializable{
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public NotificationResponseModel.data getData() {
        return data;
    }

    public void setData(NotificationResponseModel.data data) {
        this.data = data;
    }

    private String message;
    private data data;
    public class data implements Serializable {
        private int n;
        private int nModified;

        public int getOk() {
            return ok;
        }

        public void setOk(int ok) {
            this.ok = ok;
        }

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }

        public int getnModified() {
            return nModified;
        }

        public void setnModified(int nModified) {
            this.nModified = nModified;
        }

        private int ok;
    }
}
