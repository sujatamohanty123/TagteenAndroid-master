package in.tagteen.tagteen.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ResetPasswordDM implements Parcelable {

  private String mMobileNumber;

  public ResetPasswordDM(String mobileNumber) {
    mMobileNumber = mobileNumber;
  }

  protected ResetPasswordDM(Parcel in) {
    mMobileNumber = in.readString();
  }

  public static final Creator<ResetPasswordDM> CREATOR = new Creator<ResetPasswordDM>() {
    @Override
    public ResetPasswordDM createFromParcel(Parcel in) {
      return new ResetPasswordDM(in);
    }

    @Override
    public ResetPasswordDM[] newArray(int size) {
      return new ResetPasswordDM[size];
    }
  };

  public String getMobileNumber() {
    return mMobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    mMobileNumber = mobileNumber;
  }

  @Override public String toString() {
    return "ResetPasswordDM{" +
        "mMobileNumber='" + mMobileNumber + '\'' +
        '}';
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(mMobileNumber);
  }
}
