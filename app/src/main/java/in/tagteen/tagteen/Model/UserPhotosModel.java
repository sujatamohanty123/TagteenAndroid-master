package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayattama Prusty on 29-Aug-17.
 */

public class UserPhotosModel {
    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private List<Data> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @SerializedName("success")
    @Expose
    private Boolean success;
    public class Data {
        public List<Image> getImage() {
            return image;
        }

        public void setImage(List<Image> image) {
            this.image = image;
        }

        @SerializedName("image")
        @Expose
        private List<Image> image = null;
        public class Image {
            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            @SerializedName("url")
            @Expose
            private String url;
        }
    }
}
