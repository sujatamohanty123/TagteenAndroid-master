package in.tagteen.tagteen.Model;


public class UserModel {

    private String  mName;
    private int     mImage;
    private String  mTagId;
    private String  mId;
    private int     mSchoolName;
    private String  mPin;

    public void  UserModel(String name,int image, String  tagId ){
      this.mName  =  name;
      this.mImage = image;
      this.mTagId = tagId;
  }
    public void  UserModel(String mId){
        this.mId=mId;
    }
    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public int getmSchoolName() {
        return mSchoolName;
    }

    public void setmSchoolName(int mSchoolName) {
        this.mSchoolName = mSchoolName;
    }

    public String getmPin() {
        return mPin;
    }

    public void setmPin(String mPin) {
        this.mPin = mPin;
    }

    public String getmTagId() {
        return mTagId;
    }

    public void setmTagId(String mTagId) {
        this.mTagId = mTagId;
    }
    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmImage() {
        return mImage;
    }

    public void setmImage(int mImage) {
        this.mImage = mImage;
    }

}
