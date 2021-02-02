package in.tagteen.tagteen.screen.signup;

import android.os.Parcel;
import android.os.Parcelable;

public class SignupDM implements Parcelable {

  private String mFirstName;
  private String mLastName;
  private String mDob;
  private String mPassword;
  private String mMobileNumber;
  private String mEmail;

  public SignupDM() {

  }

  protected SignupDM(Parcel in) {
    mFirstName = in.readString();
    mLastName = in.readString();
    mDob = in.readString();
    mPassword = in.readString();
    mMobileNumber = in.readString();
    mEmail = in.readString();
  }

  public static final Creator<SignupDM> CREATOR = new Creator<SignupDM>() {
    @Override
    public SignupDM createFromParcel(Parcel in) {
      return new SignupDM(in);
    }

    @Override
    public SignupDM[] newArray(int size) {
      return new SignupDM[size];
    }
  };

  public String getFirstName() {
    return mFirstName;
  }

  public void setFirstName(String firstName) {
    mFirstName = firstName;
  }

  public String getLastName() {
    return mLastName;
  }

  public void setLastName(String lastName) {
    mLastName = lastName;
  }

  public String getDob() {
    return mDob;
  }

  public void setDob(String dob) {
    mDob = dob;
  }

  public String getPassword() {
    return mPassword;
  }

  public void setPassword(String password) {
    mPassword = password;
  }

  public String getMobileNumber() {
    return mMobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    mMobileNumber = mobileNumber;
  }

  public String getEmail() {
    return mEmail;
  }

  public void setEmail(String email) {
    mEmail = email;
  }

  public static SignupDM transform(String firstName, String lastName, String dob, String email,
      String password, String mobileNumber) {
    SignupDM signupDM = new SignupDM();
    signupDM.setFirstName(firstName);
    signupDM.setLastName(lastName);
    signupDM.setEmail(email);
    signupDM.setPassword(password);
    signupDM.setDob(dob);
    signupDM.setMobileNumber(mobileNumber);
    return signupDM;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(mFirstName);
    parcel.writeString(mLastName);
    parcel.writeString(mDob);
    parcel.writeString(mPassword);
    parcel.writeString(mMobileNumber);
    parcel.writeString(mEmail);
  }
}
