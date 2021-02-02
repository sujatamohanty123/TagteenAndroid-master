package io.agora.openlive.activities;

import android.os.Parcel;
import android.os.Parcelable;

public class LiveActivityUserDM implements Parcelable {
  private String mUserName;
  private String mUserImage;
  private String mUserId;
  private String mBroadcasterImage;
  private String mBroadcasterName;
  private String mBrodcasterProfileImage;

  public LiveActivityUserDM() {

  }

  protected LiveActivityUserDM(Parcel in) {
    mUserName = in.readString();
    mUserImage = in.readString();
    mUserId = in.readString();
    mBroadcasterImage = in.readString();
    mBroadcasterName = in.readString();
    mBrodcasterProfileImage = in.readString();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(mUserName);
    dest.writeString(mUserImage);
    dest.writeString(mUserId);
    dest.writeString(mBroadcasterImage);
    dest.writeString(mBroadcasterName);
    dest.writeString(mBrodcasterProfileImage);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<LiveActivityUserDM> CREATOR = new Creator<LiveActivityUserDM>() {
    @Override
    public LiveActivityUserDM createFromParcel(Parcel in) {
      return new LiveActivityUserDM(in);
    }

    @Override
    public LiveActivityUserDM[] newArray(int size) {
      return new LiveActivityUserDM[size];
    }
  };

  public String getUserName() {
    return mUserName;
  }

  public void setUserName(String userName) {
    mUserName = userName;
  }

  public String getUserImage() {
    return mUserImage;
  }

  public void setUserImage(String userImage) {
    mUserImage = userImage;
  }

  public String getUserId() {
    return mUserId;
  }

  public void setUserId(String userId) {
    mUserId = userId;
  }

  public String getBroadcasterImage() {
    return mBroadcasterImage;
  }

  public void setBroadcasterImage(String broadcasterImage) {
    mBroadcasterImage = broadcasterImage;
  }

  public String getBroadcasterName() {
    return mBroadcasterName;
  }

  public void setBroadcasterName(String broadcasterName) {
    mBroadcasterName = broadcasterName;
  }

  public String getBrodcasterProfileImage() {
    return mBrodcasterProfileImage;
  }

  public void setBrodcasterProfileImage(String brodcasterProfileImage) {
    mBrodcasterProfileImage = brodcasterProfileImage;
  }
}
