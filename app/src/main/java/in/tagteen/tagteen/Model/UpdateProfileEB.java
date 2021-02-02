package in.tagteen.tagteen.Model;

public class UpdateProfileEB {

  public UpdateProfileEB(boolean isProfileUpdated) {
    this.isProfileUpdated = isProfileUpdated;
  }

  private boolean isProfileUpdated;

  public boolean isProfileUpdated() {
    return isProfileUpdated;
  }

  public void setProfileUpdated(boolean profileUpdated) {
    isProfileUpdated = profileUpdated;
  }
}
