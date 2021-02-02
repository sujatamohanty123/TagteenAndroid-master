package in.tagteen.tagteen.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import in.tagteen.tagteen.utils.GeneralApiUtils;
import in.tagteen.tagteen.utils.TagteenApplication;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedPreferenceSingleton {
  private static SharedPreferenceSingleton mInstance;
  private Context mContext;
  private SharedPreferences myPreference;

  public static SharedPreferenceSingleton getInstance() {
    if (mInstance == null) {
      mInstance = new SharedPreferenceSingleton();
    }
    return mInstance;
  }

  public void init(Context context) {
    if (context == null) {
      context = TagteenApplication.getContext();
    }
    this.mContext = context;
    this.myPreference = PreferenceManager.getDefaultSharedPreferences(mContext);
  }

  public void writeStringPreference(String key, String value) {
    SharedPreferences.Editor mEditor = myPreference.edit();
    mEditor.putString(key, value);
    mEditor.commit();
  }

  public void writeStringPreference(Context context, String key, String value) {
    if (myPreference == null) {
      myPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }
    SharedPreferences.Editor mEditor = myPreference.edit();
    mEditor.putString(key, value);
    mEditor.commit();
  }

  public void writeStringListPreference(String key, List<String> stringList) {
    SharedPreferences.Editor mEditor = myPreference.edit();
    if (stringList == null) {
      mEditor.putStringSet(key, null);
    } else {
      mEditor.putStringSet(key, new HashSet<String>(stringList));
    }
    mEditor.commit();
  }

  public void writeBoolPreference(String key, boolean value) {
    SharedPreferences.Editor mEditor = myPreference.edit();
    mEditor.putBoolean(key, value);
    mEditor.commit();
  }

  public void clearPreference() {
        /*String educationId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.EDUCATION_ID);
        String courseId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.COURSE_ID);
        String courseName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.COURSE_NAME);
        String degreeId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.DEGREE_ID);
        String degreeName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.DEGREE_NAME);
        String yearId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.YEAR_ID);
        String yearName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.YEAR_NAME);
        String standardId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.STANDARD_ID);
        String standardName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.STANDARD_NAME);*/

    SharedPreferences.Editor mEditor = myPreference.edit();
    mEditor.clear();
    mEditor.commit();

        /*SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.EDUCATION_ID, educationId);
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.COURSE_ID, courseId);
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.COURSE_NAME, courseName);
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.DEGREE_ID, degreeId);
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.DEGREE_NAME, degreeName);
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.YEAR_NAME, yearName);
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.YEAR_ID, yearId);
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.STANDARD_ID, standardId);
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.STANDARD_NAME, standardName);*/
  }

  public void writeIntPreference(String key, int value) {
    SharedPreferences.Editor mEditor = myPreference.edit();
    mEditor.putInt(key, value);
    mEditor.commit();
  }

  public void writeFloatPreference(String key, Float value) {
    SharedPreferences.Editor mEditor = myPreference.edit();
    mEditor.putFloat(key, value);
    mEditor.commit();
  }

  public float getFloatPreference(String key) {
    return myPreference.getFloat(key, 0);
  }

  public int getIntPreference(String key) {
    return myPreference.getInt(key, -1);
  }

  public int getIntPreferenceNull(String key) {
    return myPreference.getInt(key, -1000);
  }

  public String getStringPreferenceNull(String key) {
    return myPreference.getString(key, null);
  }

  public String getStringPreference(String key) {
    return myPreference.getString(key, "");
  }

  public boolean getBoolPreference(String key) {
    return myPreference.getBoolean(key, false);
  }

  public boolean getBoolPreference(Context context, String key) {
    if (myPreference == null) {
      this.myPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }
    return myPreference.getBoolean(key, false);
  }

  public List<String> getStringListPreference(String key) {
    Set<String> stringSet = null;
    try {
      stringSet = myPreference.getStringSet(key, null);
    } catch (Exception e) {
      this.writeStringListPreference(key, null);
    }
    if (stringSet == null) {
      return null;
    }
    return new ArrayList<String>(stringSet);
  }

  public String getStringPreference(Context context, String key) {
    if (context != null && !GeneralApiUtils.isStringEmpty(
        PreferenceManager.getDefaultSharedPreferences(context).getString(key, ""))) {
      return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    } else if (myPreference != null && !GeneralApiUtils.isStringEmpty(getStringPreference(key))) {
      return getStringPreference(key);
    } else {
      return "";
    }
  }

  public void removePreference(String key) {
    SharedPreferences.Editor mEditor = myPreference.edit();
    mEditor.remove(key);
    mEditor.commit();
  }
}
