package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddEducationModel {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("education_type_id")
    @Expose
    private int educationType;

    @SerializedName("education_type_name")
    @Expose
    private String educationTypeName;

    @SerializedName("district")
    @Expose
    private String district;

    @SerializedName("pincode")
    @Expose
    private String pincode;

    @SerializedName("state")
    @Expose
    private String state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEducationType() {
        return educationType;
    }

    public void setEducationType(int educationType) {
        this.educationType = educationType;
    }

    public String getEducationTypeName() {
        return educationTypeName;
    }

    public void setEducationTypeName(String educationTypeName) {
        this.educationTypeName = educationTypeName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }


}
