package in.tagteen.tagteen.Model.placements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApplyPlacementResponse {
    @SerializedName("Status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return this.status.equals("Success");
    }

    @SerializedName("Message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("application")
    @Expose
    private Application application;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    class Application {
        @SerializedName("placement_id")
        @Expose
        private String placementId;

        public String getPlacementId() {
            return placementId;
        }

        public void setPlacementId(String placementId) {
            this.placementId = placementId;
        }

        @SerializedName("applicant_id")
        @Expose
        private String applicantId;

        public String getApplicantId() {
            return applicantId;
        }

        public void setApplicantId(String applicantId) {
            this.applicantId = applicantId;
        }
    }
}
