package in.tagteen.tagteen.configurations;

import androidx.annotation.StringDef;
import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AppConfigurationSetting {
  public static int SPLASH_PAGE_DELAY = 1000;
  //public static String[] APPLICATION_STATUS =
  //    {"registrationState", "isWaitingForOTP", "isOnDashBoard"};
  public static String[] PASSWORD_SETTING_STATUS =
      {DatabaseContracts.IS_FALSE, DatabaseContracts.IS_TRUE};
  public static String LOCK_PASSWORD = "lockPassword";
  public static String HIDE_FRIEND_PIN = "hideFriendPin";
  public static String OPW_REGISTRATION_Id = "registration_Id";
  public static String OPW_PRE_KEYS = "pre_Keys";
  public static String OPW_SIGNED_PRE_KEY = "signed_Pre_Key";
  public static String OPW_IDENTITY_KEY_PAIR = "identity_Key_Pair";

  public static final String OTP_SENDER_TRANSACTIONAL = "TXTTAG";
  public static final int OTP_LENGTH = 4;

  public static final String PROFILE_IMAGE = "Profile/Images";
  public static final String SELF_REACTION_IMAGE = "SelfReaction/Images";
  public static final String POST_IMAGE = "Post/Images";
  public static final String SELF_POST_IMAGE = "SelfiPost/Images";

  public static final String POST_VIDEO = "Post/Video";
  public static final String SELF_POST_VIDEO = "SelfiPost/Video";
  public static final String YOUTH_TUBE_VIDEO = "Youth_Tube/Video";

  @StringDef({
      AppLoginStatus.REGISTRATION_STATE, AppLoginStatus.VERIFY_OTP_STATE,
      AppLoginStatus.LOGIN_SUCCESS
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface AppLoginStatus {
    String REGISTRATION_STATE = "registrationState";
    String VERIFY_OTP_STATE = "isWaitingForOTP";
    String LOGIN_SUCCESS = "isOnDashBoard";
  }
}
