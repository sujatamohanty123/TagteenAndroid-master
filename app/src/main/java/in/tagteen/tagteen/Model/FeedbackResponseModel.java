package in.tagteen.tagteen.Model;

import java.io.Serializable;

/**
 * Created by Sujata on 08-06-2018.
 */
public class FeedbackResponseModel implements Serializable {
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

    public FeedbackResponseModel.data getData() {
        return data;
    }

    public void setData(FeedbackResponseModel.data data) {
        this.data = data;
    }

    private String message;
    private  data data;
    public class data implements Serializable {
        private int __v;
        private String user_id;
        private String date_created;
        private String message;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getDate_created() {
            return date_created;
        }

        public void setDate_created(String date_created) {
            this.date_created = date_created;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        private String _id;
    }
}
