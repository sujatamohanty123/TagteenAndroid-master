package in.tagteen.tagteen.configurations;

public class RegistrationConstants {

    public static final String MOBILE = "mobile";
    public static final String COUNTRY_CODE = "country_code";

    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String DATE_OF_BIRTHDAY = "dob";
    public static final String GENDER = "gender";

    public static final String CITY = "city";
    public static final String PROFILE_URL = "profile_url";
    public static final String E_MAIL = "email";
    public static final String PASSWORD = "password";
    public static final String DEVICE_ID = "device_id";

    public static final String CURRENTLY_STUDYING = "currently_studying";
    public static final String SCHOOL_NAME = "school_name";
    public static final String SCHOOL_ADDRESS = "school_address";
    public static final String PIN_CODE = "pincode";
    public static final String EDUCATION_ID = "education_id";
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_NAME = "course_name";
    public static final String DEGREE_ID = "degree_id";
    public static final String DEGREE_NAME = "degree_name";
    public static final String YEAR_ID = "year_id";
    public static final String YEAR_NAME = "year_name";
    public static final String STANDARD_ID = "standard_id";
    public static final String STANDARD_NAME = "standard_name";
    public static final String DEVICE_TOKEN = "device_token";

    public static final String VERIFICATION_CODE = "verification_code";
    public static final String SUCCESS = "success";
    public static final String TRUE = "true";
    public static final String USER_ID = "user_id";
    public static final String HOBBY = "hobby";
    public static final String DATA = "data";
    public static final String TOKEN = "token";
    public static final String IS_TAGGED_USER = "is_tagged_user";
    public static final String IS_NOT_FIRST_ACCESS = "isNotFirstAccess";
    public static final String IS_FIRST_ACCESS_AFTER_VERIFIED = "isFirstAccessAfterVerified";
    public static final String IS_SHOWROOM_ACCESSED = "isShowroomAccessed";
    public static final String IS_MOMENTS_ACCESSED = "isMomentsAccessed";
    public static final String IS_CHAT_INITIATED = "isChatInitiated";
    public static final String TAGGED_NUMBER = "tagged_number";

    /*sharedContract */
    public static final String FRIEND_ID = "ch_friendId";
    public static final String FRIEND_NAME = "ch_friendName";
    public static final String FRIEND_TAG = "ch_friendTag";
    public static final String FRIEND_IMAGE = "ch_friendImage";

    /*scoket key*/
    public final static String ADD_CHAT_USER = "ADD_USER";
    public final static String NEW_CHAT_MESSAGE = "NEW_MESSAGE";
    public final static String TYPING = "TYPING";
    public final static String STOP_TYPING = "STOP_TYPING";
    public final static String DISCONNECT = "disconnect";
    public final static String USER_LEFT = "USER_LEFT";
    public final static String REMOVE_USRER = "remove user";
    public final static String SERVER_SENDER = "SERVER_SENDER";

    /*after server received*/
    public final static String SEND_STATUS = "SEND_STATUS";
    /*after receiver received*/
    public final static String MSG_RECEIVE_SUCCESS = "DELIVERED_STATUS";
    /*after view by reciever*/
    public final static String SEEN_STATUS = "SEEN_STATUS";

    public final static String NEW_EVENT = "NEW_EVENT";
    public final static String EVENT_DELIVERED_STATUS = "EVENT_DELIVERED_STATUS";
    public final static String USER_NAME = "username";
    public final static String STAFF_AVAILABILITY = "DISCONNECT";
    public final static String REMOVE_USER = "remove user";

    /*hiden and password key*/
    public static String GET_PASSWORD = "PASSWORD";
    public static String PASWORD_STATUS = "PASWORD_STATUS";
    public static String IS_LOCKED_FRIEND = "IS_LOCKED_FRIEND";

    /*AWS folders*/
    public static String POST_IMAGE = "inputs/";
    private static String FACE_REACTION_IMAGE = "Profile/Chat/FaceReaction";
    private static String CHAT_CAMERA_IMAGE = "Profile/chat/CameraImage";
    private static String CHAT_GALLERY_IMAGE = "Profile/chat/GalleryImages";
    private static String CHAT_DOCUMENTS = "Profile/chat/Doc";

    /*chat fragment event*/
    public final static int DEFAULT = 0;
    public final static int IMAGE = 1;
    public final static int CAMERA = 2;
    public final static int REACTION = 3;
    public final static int AUDIO_RECODER = 4;
    public final static int AUDIO_FILES = 5;
    public final static int DOCUMENT = 6;
    public final static int YOUTHTUBE = 7;
    public final static int EMOJI = 8;
    public final static int PRIVATE = 9;
    public final static int SOFT_KEY = 10;

    /*handle the camera*/
    public final static String SelfiActivity = "0";
    public final static String ChatFront = "1";
    public final static String ChatBack = "2";
    public final static String cameraReotation = "2";

    public static final String GOOGLE_PAY = "GooglePay";
    public static final String PAYTM = "PayTM";
    public static final String PHONE_PE = "PhonePe";

    public static final String REWARDS_PAYMENT_OPTION = "rewards_payment_option";
    public static final String REWARDS_PAYMENT_NUMBER = "rewards_payment_number";
}
