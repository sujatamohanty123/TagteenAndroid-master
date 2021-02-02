package in.tagteen.tagteen.Fragments.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CampusLiveInputJson {
    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("education_id")
    @Expose
    private String educationId;

    @SerializedName("courses_id")
    @Expose
    private String courseId;

    @SerializedName("degree_id")
    @Expose
    private String degreeId;

    @SerializedName("year_id")
    @Expose
    private String yearId;

    @SerializedName("standard_id")
    @Expose
    private String standardId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEducationId() {
        return educationId;
    }

    public void setEducationId(String educationId) {
        this.educationId = educationId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getDegreeId() {
        return degreeId;
    }

    public void setDegreeId(String degreeId) {
        this.degreeId = degreeId;
    }

    public String getYearId() {
        return yearId;
    }

    public void setYearId(String yearId) {
        this.yearId = yearId;
    }

    public String getStandardId() {
        return standardId;
    }

    public void setStandardId(String standardId) {
        this.standardId = standardId;
    }
}
