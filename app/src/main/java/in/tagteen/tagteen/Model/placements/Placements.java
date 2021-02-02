package in.tagteen.tagteen.Model.placements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Placements {
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

    @SerializedName("Placements")
    @Expose
    private List<Placement> placementList;

    public List<Placement> getPlacementList() {
        return placementList;
    }

    public void setPlacementList(List<Placement> placementList) {
        this.placementList = placementList;
    }

    public class Placement implements Serializable {
        @SerializedName("id")
        @Expose
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @SerializedName("placement_id")
        @Expose
        private String placementId;

        public String getPlacementId() {
            return placementId;
        }

        public void setPlacementId(String placementId) {
            this.placementId = placementId;
        }

        @SerializedName("placement_title")
        @Expose
        private String placementTitle;

        public String getPlacementTitle() {
            return placementTitle;
        }

        public void setPlacementTitle(String placementTitle) {
            this.placementTitle = placementTitle;
        }

        @SerializedName("placement_industry_id")
        @Expose
        private String industryId;

        public String getIndustryId() {
            return industryId;
        }

        public void setIndustryId(String industryId) {
            this.industryId = industryId;
        }

        @SerializedName("placement_language_id")
        @Expose
        private String languageId;

        public String getLanguageId() {
            return languageId;
        }

        public void setLanguageId(String languageId) {
            this.languageId = languageId;
        }

        @SerializedName("placement_employer_id")
        @Expose
        private String employerId;

        public String getEmployerId() {
            return employerId;
        }

        public void setEmployerId(String employerId) {
            this.employerId = employerId;
        }

        @SerializedName("placement_company_id")
        @Expose
        private String companyId;

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        @SerializedName("placement_address_id")
        @Expose
        private String addressId;

        public String getAddressId() {
            return addressId;
        }

        public void setAddressId(String addressId) {
            this.addressId = addressId;
        }

        @SerializedName("placement_description")
        @Expose
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @SerializedName("placement_city")
        @Expose
        private String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        @SerializedName("placement_country")
        @Expose
        private String country;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        @SerializedName("is_featured")
        @Expose
        private String featured;

        public String getFeatured() {
            return featured;
        }

        public void setFeatured(String featured) {
            this.featured = featured;
        }

        @SerializedName("placement_pay")
        @Expose
        private String payScale;

        public String getPayScale() {
            return payScale;
        }

        public void setPayScale(String payScale) {
            this.payScale = payScale;
        }

        @SerializedName("placement_media_link")
        @Expose
        private String mediaLink;

        public String getMediaLink() {
            return mediaLink;
        }

        public void setMediaLink(String mediaLink) {
            this.mediaLink = mediaLink;
        }

        @SerializedName("placement_thumbnail")
        @Expose
        private String thumbnail;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        @SerializedName("placement_created_at")
        @Expose
        private String createdAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        @SerializedName("placement_created_by")
        @Expose
        private String createdBy;

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        @SerializedName("placement_media_type")
        @Expose
        private String mediaType;

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        @SerializedName("placement_del_status")
        @Expose
        private String deletedStatus;

        public String getDeletedStatus() {
            return deletedStatus;
        }

        public void setDeletedStatus(String deletedStatus) {
            this.deletedStatus = deletedStatus;
        }

        @SerializedName("placement_active_status")
        @Expose
        private String activeStatus;

        public String getActiveStatus() {
            return activeStatus;
        }

        public void setActiveStatus(String activeStatus) {
            this.activeStatus = activeStatus;
        }

        @SerializedName("placement_type")
        @Expose
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @SerializedName("placement_last_date")
        @Expose
        private String lastDate;

        public String getLastDate() {
            return lastDate;
        }

        public void setLastDate(String lastDate) {
            this.lastDate = lastDate;
        }

        @SerializedName("placement_age_limit")
        @Expose
        private String ageLimit;

        public String getAgeLimit() {
            return ageLimit;
        }

        public void setAgeLimit(String ageLimit) {
            this.ageLimit = ageLimit;
        }

        @SerializedName("placement_qualification")
        @Expose
        private String qualification;

        public String getQualification() {
            return qualification;
        }

        public void setQualification(String qualification) {
            this.qualification = qualification;
        }

        @SerializedName("placement_experience")
        @Expose
        private String experience;

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        @SerializedName("placement_vacancies")
        @Expose
        private String vacancies;

        public String getVacancies() {
            return vacancies;
        }

        public void setVacancies(String vacancies) {
            this.vacancies = vacancies;
        }

        @SerializedName("placement_contact_id")
        @Expose
        private String contactId;

        public String getContactId() {
            return contactId;
        }

        public void setContactId(String contactId) {
            this.contactId = contactId;
        }

        @SerializedName("placement_opt_languages")
        @Expose
        private String optionalLanguages;

        public String getOptionalLanguages() {
            return optionalLanguages;
        }

        public void setOptionalLanguages(String optionalLanguages) {
            this.optionalLanguages = optionalLanguages;
        }

        @SerializedName("placement_mandatory_languages")
        @Expose
        private String mandatoryLanguages;

        public String getMandatoryLanguages() {
            return mandatoryLanguages;
        }

        public void setMandatoryLanguages(String mandatoryLanguages) {
            this.mandatoryLanguages = mandatoryLanguages;
        }

        @SerializedName("View_count")
        @Expose
        private String viewCount;

        public String getViewCount() {
            return viewCount;
        }

        public void setViewCount(String viewCount) {
            this.viewCount = viewCount;
        }

        @SerializedName("applied_count")
        @Expose
        private String appliedCount;

        public String getAppliedCount() {
            return appliedCount;
        }

        public void setAppliedCount(String appliedCount) {
            this.appliedCount = appliedCount;
        }

        @SerializedName("is_applied")
        @Expose
        private String isApplied;

        public String getIsApplied() {
            return isApplied;
        }

        public void setIsApplied(String isApplied) {
            this.isApplied = isApplied;
        }
    }
}
