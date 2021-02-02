package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EducationsModel {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("data")
    @Expose
    private EducationModel data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public EducationModel getData() {
        return data;
    }

    public void setData(EducationModel data) {
        this.data = data;
    }

    public class EducationModel {
        @SerializedName("get_courses_name")
        @Expose
        private List<CourseInfo> courseInfos;

        @SerializedName("get_degree_name")
        @Expose
        private List<DegreeInfo> degreeInfos;

        @SerializedName("get_year_name")
        @Expose
        private List<YearInfo> yearInfos;

        @SerializedName("get_standard_name")
        @Expose
        private List<StandardInfo> standardInfos;

        public List<CourseInfo> getCourseInfos() {
            return courseInfos;
        }

        public void setCourseInfos(List<CourseInfo> courseInfos) {
            this.courseInfos = courseInfos;
        }

        public List<DegreeInfo> getDegreeInfos() {
            return degreeInfos;
        }

        public void setDegreeInfos(List<DegreeInfo> degreeInfos) {
            this.degreeInfos = degreeInfos;
        }

        public List<YearInfo> getYearInfos() {
            return yearInfos;
        }

        public void setYearInfos(List<YearInfo> yearInfos) {
            this.yearInfos = yearInfos;
        }

        public List<StandardInfo> getStandardInfos() {
            return standardInfos;
        }

        public void setStandardInfos(List<StandardInfo> standardInfos) {
            this.standardInfos = standardInfos;
        }
    }

    public class CourseInfo {
        @SerializedName("_id")
        @Expose
        private String id;

        @SerializedName("Courses")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class DegreeInfo {
        @SerializedName("_id")
        @Expose
        private String id;

        @SerializedName("degree")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class YearInfo {
        @SerializedName("_id")
        @Expose
        private String id;

        @SerializedName("Year")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class StandardInfo {
        @SerializedName("_id")
        @Expose
        private String id;

        @SerializedName("Standard")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
