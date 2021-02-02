package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AcademicInfos {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private List<AcademicInfo> dataList;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<AcademicInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<AcademicInfo> dataList) {
        this.dataList = dataList;
    }

    public class AcademicInfo {
        @SerializedName("_id")
        @Expose
        private String id;

        @SerializedName("education_type_id")
        @Expose
        private int educationType;

        @SerializedName("education_type_name")
        @Expose
        private String educationName;

        @SerializedName("name")
        @Expose
        private String academicName;

        @SerializedName("pincode")
        @Expose
        private String pincode;

        @SerializedName("district")
        @Expose
        private String district;

        @SerializedName("state")
        @Expose
        private String state;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getEducationType() {
            return educationType;
        }

        public void setEducationType(int educationType) {
            this.educationType = educationType;
        }

        public String getEducationName() {
            return educationName;
        }

        public void setEducationName(String educationName) {
            this.educationName = educationName;
        }

        public String getAcademicName() {
            return academicName;
        }

        public void setAcademicName(String academicName) {
            this.academicName = academicName;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
