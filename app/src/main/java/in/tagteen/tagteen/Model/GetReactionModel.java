package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Sujata on 14-02-2018.
 */

public class GetReactionModel {
    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("like_count")
        @Expose
        private int like_count;
        @SerializedName("action_type_data_count")
        @Expose
        private int action_type_data_count;

        @SerializedName("cool_count")
        @Expose
        private int cool_count;
        @SerializedName("swag_count")
        @Expose
        private int swag_count;
        @SerializedName("nerd_count")
        @Expose
        private int nerd_count;

        @SerializedName("dab_count")
        @Expose
        private int dab_count;

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        @SerializedName("comment_count")
        @Expose
        private int comment_count;

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

        public int getLike_count() {
            return like_count;
        }

        public void setLike_count(int like_count) {
            this.like_count = like_count;
        }

        public int getAction_type_data_count() {
            return action_type_data_count;
        }

        public void setAction_type_data_count(int action_type_data_count) {
            this.action_type_data_count = action_type_data_count;
        }

        public int getCool_count() {
            return cool_count;
        }

        public void setCool_count(int cool_count) {
            this.cool_count = cool_count;
        }

        public int getSwag_count() {
            return swag_count;
        }

        public void setSwag_count(int swag_count) {
            this.swag_count = swag_count;
        }

        public int getNerd_count() {
            return nerd_count;
        }

        public void setNerd_count(int nerd_count) {
            this.nerd_count = nerd_count;
        }

        public int getDab_count() {
            return dab_count;
        }

        public void setDab_count(int dab_count) {
            this.dab_count = dab_count;
        }

        @SerializedName("total_count")
        @Expose
        private int total_count;
    }

}
