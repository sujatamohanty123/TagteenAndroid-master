package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserURocksModel {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private List<UserURockModel> dataList;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<UserURockModel> getDataList() {
        return dataList;
    }

    public void setDataList(List<UserURockModel> dataList) {
        this.dataList = dataList;
    }

    public int getWeeklyCount() {
        int count = 0;
        if (this.dataList != null) {
            for (UserURockModel model : this.dataList) {
                if (model.weeklyCountModel != null) {
                    count += model.weeklyCountModel.count;
                }
            }
        }
        return count;
    }

    public int getTotalCount() {
        int count = 0;
        if (this.dataList != null) {
            for (UserURockModel model : this.dataList) {
                if (model.totalCountModel != null) {
                    count += model.totalCountModel.count;
                }
            }
        }
        return count;
    }

    public class UserURockModel {
        @SerializedName("post_id")
        @Expose
        private String postId;

        @SerializedName("w_u_rock_c")
        @Expose
        private URockCountModel weeklyCountModel;

        @SerializedName("t_u_rock_c")
        @Expose
        private URockCountModel totalCountModel;

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public URockCountModel getWeeklyCountModel() {
            return weeklyCountModel;
        }

        public void setWeeklyCountModel(URockCountModel weeklyCountModel) {
            this.weeklyCountModel = weeklyCountModel;
        }

        public URockCountModel getTotalCountModel() {
            return totalCountModel;
        }

        public void setTotalCountModel(URockCountModel totalCountModel) {
            this.totalCountModel = totalCountModel;
        }
    }

    public class URockCountModel {
        @SerializedName("count")
        @Expose
        private int count;

        @SerializedName("from_d")
        @Expose
        private long fromDate;

        @SerializedName("to_d")
        @Expose
        private long toDate;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public long getFromDate() {
            return fromDate;
        }

        public void setFromDate(long fromDate) {
            this.fromDate = fromDate;
        }

        public long getToDate() {
            return toDate;
        }

        public void setToDate(long toDate) {
            this.toDate = toDate;
        }
    }
}
