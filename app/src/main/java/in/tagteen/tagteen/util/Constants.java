package in.tagteen.tagteen.util;

import android.Manifest;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lovekushvishwakarma on 15/10/17.
 */

public class Constants {
    public static final String APP_FOLDER_NAME = "TagTeen";
    public static final String VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR = "VideosComp";
    public static final int animationTime=1500;
    public static final String GUEST_USER_ID = "5c51ffb831ffd23a3ed2bcc3";

    public static final List<String> CAMPUS_LIVE_COLOR_LIST =
            Arrays.asList("#708090", "#F4A460", "#4169E1", "#228B22", "#9400D3", "#FF00FF", "#FFD700", "#DC143C");
    public static final List<String> IMAGE_EXTENSIONS = Arrays.asList(Constants.JPG, Constants.JPEG, Constants.PNG);
    public static final List<String> VIDEO_EXTENSIONS = Arrays.asList(Constants.MP4, Constants.GP3, Constants.MKV);
    public static final List<String> AUDIO_EXTENSIONS = Arrays.asList(Constants.MP3, Constants.AAC, Constants.OGG);

    public static final String JPG = "jpg";
    public static final String JPEG = "jpeg";
    public static final String PNG = "png";
    public static final String MP4 = "mp4";
    public static final String GP3 = "3gp";
    public static final String MKV = "mkv";
    public static final String MP3 = "mp3";
    public static final String AAC = "aac";
    public static final String OGG = "ogg";

    // catgegory names
    public static final String ALL = "All";
    public static final String FOR_YOU = "For You";
    public static final String ACTING = "Acting";
    public static final String ANIMATION = "Animation & Comics";
    public static final String BEAUTY = "Beauty/Make-up";
    public static final String CAREERS = "Careers";
    public static final String CAR_BIKES = "Cars & Bikes";
    public static final String COMPUTER_GADGETS = "Computers & Gadgets";
    public static final String FOOD_COOKING = "Food & cooking";
    public static final String DANCING = "Dancing";
    public static final String FUNNY_STANDUP = "Funny & Stand up Comic";
    public static final String GAMING = "Gaming";
    public static final String HEALTH_FITNESS = "Health & Fitness";
    public static final String HOME_LIFESTYLE = "Home & Lifestyle";
    public static final String JOURNALISM = "Journalism";
    public static final String MAGIC = "Magic Tricks";
    public static final String MARKETING = "Marketing & Business";
    public static final String MILITARY = "Military";
    public static final String MOVIES = "Movies & TV Shows";
    public static final String MUSIC = "Music";
    public static final String PAINTING = "Painting";
    public static final String PHOTOGRAPHY = "Photography";
    public static final String POLITICS = "Politics";
    public static final String SELF_IMPROVEMENT = "Self Improvement";
    public static final String SHOPPING = "Shopping";
    public static final String SPORTS = "Sports";
    public static final String STUDIES_EDUCATION = "Studies & Education";
    public static final String TRAVELLING = "Travelling";
    public static final String WRITING = "Writing";
    public static final String DJ = "DJ";
    public static final String ENTREPRENEURS = "Entrepreneurs";
    public static final String FASHION = "Fashion & Modelling";

    public static final String SCHOOL     = "school";
    public static final String COLLEGE    = "college";
    public static final String UNIVERSITY = "university";

    // request codes
    public static final int TRIM_VIDEO_REQUEST_CODE = 300;
    public static final int ADD_CAMPUS_LIVE_POST_CODE = 301;
    public static final int EDIT_CATEGORIES_REQUEST_CODE = 302;
    public static final int EDIT_ACADEMIC_REQUEST_CODE = 121;
    public static final int COMMENT_REQUEST_CODE = 111;
    public static final int VIDEO_FULLSCREEN_CODE = 131;
    public static final int KNOWLEDGE_VIDEO_CAPTURE_REQUEST_CODE = 212;

    // intent params
    public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    public static final String VIDEO_PICK = "Video_pick";
    public static final String PATH_VIDEO_RECORDED = "videoPath";
    public static final String PATH_IMAGE_CAPTURED = "imagePath";
    public static final String SELECTED_CATEGORIES = "selectedCategories";
    public static final String SELECTED_IMAGE_PATHS = "selectedImagePaths";
    public static final String TRIMMED_VIDEO_PATH = "trimmedVideoPath";
    public static final String IMAGE_BITMAP = "imageBitmap";
    public static final String IS_SINGLE_SELECTION = "isSingleSelection";
    public static final String SHOWROOM_POST_DATA = "showroomPostData";
    public static final String SELETED_IMAGE_INDEX = "selectedImageIndex";
    public static final String VIDEO_THUMBNAIL_PATH = "videoThumbnail";
    public static final String WEBSHOW_TYPE_ID = "webshowTypeId";
    public static final String PLACEMENT = "placement";
    public static final String SUPPORTERS_COUNT = "supportersCount";
    public static final String VIDEOS_COUNT = "videosCount";
    public static final String U_ROCKS_COUNT = "uRocksCount";
    public static final String QUESTION_ID = "questionId";
    public static final String QUESTION_TITLE = "questionTitle";
    public static final String PROFILE_PIC_URL = "profilePicUrl";
    public static final String POSTED_BY = "postedBy";
    public static final String POSTED_BY_TAG_NO = "postedByTagNo";
    public static final String VIEW_COUNT = "viewCount";

    public static final String FROM_SCREEN = "from";
    public static final String SIGNUP_SCREEN = "signup";
    public static final String EDIT_PROFILE_SCREEN = "edit";
    public static final String ADD_CAMPUS_LIVE_POST_SCREEN = "addCampusLivePostScreen";

    public static final String COMMENTS_COUNT = "commentsCount";
    public static final String REACTS_COUNT = "reactsCount";
    public static final String LIKE_COUNT = "likeCount";
    public static final String COOL_COUNT = "coolCount";
    public static final String SWEG_COUNT = "swegCount";
    public static final String NERD_COUNT = "nerdCount";
    public static final String DAB_COUNT = "dabCount";
    public static final String SELECT_COMMENT_LIKE = "comment_select_flag";
    public static final int FLAG_COMMENT = 0;
    public static final int FLAG_REACTS = 1;
    public static final String SHOW_KEYPAD = "keypadshow";
    public static final String BLOCK_REACTS = "blockReacts";
    public static final String SELECTED_MODEL = "selectedModel";
    public static final String COMMENTS_TYPE = "type";

    public static final String POST_ID = "postid";
    public static final String OTHER_USER_ID = "otherUserId";
    public static final String IS_MY_FRIEND = "isMyFriend";
    public static final String IS_PENDING_FRIEND = "isPendingFriend";
    public static final String DISABLE_VIDEO = "disableVideo";
    public static final String DISABLE_PHOTO = "disablePhoto";
    public static final String DISABLE_CAMERA_SWITCHING = "disableCameraSwitching";
    public static final String DEFAULT_FRONT_CAMERA = "defaultFrontCamera";
    public static final String VIDEO_RECORD_DURATION = "videoRecordDuration";

    // msg, logs
    public static final String SERVER_ERROR = "Cannot connect server";
    public static final String MOMENTS_UPLOAD_IN_PROGRESS_MSG = "Please wait until your moment is fully uploaded";
    public static final String SHOWROOM_UPLOAD_IN_PROGRESS_MSG = "Please wait until your talent video is fully uploaded";
    public static final String CAMPUSLIVE_UPLOAD_IN_PROGRESS_MSG = "Please wait until your post is fully uploaded";
    public static final String UPLOAD_FAILED_MSG = "Uploading failed because your internet connection has dropped. Please upload again.";

    public static final String SELFIE_ALERT_MSG = "Everyday win rewards upto Rs. 100 just by uploading your best Selfie or Clippie in Moments.";

    // mobile verification
    public static final String NUMBER_ALREADY_EXISTS = "User present in the DB";
    public static final String NEW_NUMBER = "User not found";
    public static final String INVALID_NUMBER = "";

    // constants
    public static final String POST_TYPE_MOMENTS = "moment";
    public static final String POST_TYPE_TEENFEED = "teenfeed";
    public static final String POST_TYPE_SHOWROOM = "showroom";
    public static final String POST_TYPE_CAMPUSLIVE = "campusLive";

    public static final int POST_TYPE_MOMENTS_INT = 1;
    public static final int POST_TYPE_TEENFEED_INT = 2;
    public static final int POST_TYPE_SHOWROOM_INT = 3;
    public static final int POST_TYPE_CAMPUSLIVE_INT = 4;

    public static final String REACT_COOL = "cool";
    public static final String REACT_SWAG = "swag";
    public static final String REACT_NERD = "nerd";
    public static final String REACT_DAB = "dab";
    public static final String REACT_LIKE = "like";

    public static final String PDF = "pdf";
    public static final String DOC = "doc";
    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";

    // campus live filters
    public static final String ALL_POSTS = "All posts";
    public static final String MY_COURSE_POSTS = "My course posts";
    public static final String MY_DEGREE_POSTS = "My degree posts";
    public static final String MY_YEAR_POSTS = "My year posts";
    public static final String MY_STANDARD_POSTS = "My standard posts";

    public static final String INTERNSHIPS = "Internships";
    public static final String FULL_TIME = "Full-Time";
    public static final String PART_TIME = "Part-Time";
    public static final String VOLUNTARY = "Voluntary";

    // server params
    public static final String CATEGORY_NAME_PARAM = "categorie_name";
    public static final String CATEGORY_ID_PARAM = "_id";

    public static final String ID = "_id";
    public static final String EDUCATION_OBJ = "education_id";
    public static final String COURSE_OBJ = "courses_id";
    public static final String COURSE_NAME = "Courses";
    public static final String DEGREE_OBJ = "degree_id";
    public static final String DEGREE_NAME = "degree";
    public static final String YEAR_OBJ = "years_id";
    public static final String YEAR_NAME = "Year";
    public static final String STANDARD_OBJ = "standard_id";
    public static final String STANDARD_NAME = "Standard";

    // shared prefs
    public static final String IS_MOMENTS_UPLOAD_IN_PROGRESS = "momentsUploadInProgress";
    public static final String IS_SHOWROOW_UPLOAD_IN_PROGRESS = "showroomUploadInProgress";
    public static final String IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS = "campusliveUploadInProgress";
    public static final String VIDEO_URL = "videoUrl";
    public static final String VIDEO_CURRENT_POSITION = "videoCurrentPosition";
    public static final String OLD_NOTIFICATIONS_COUNT = "oldNotificationsCount";
    public static final String CHAT_INFO_EXPANDED = "chatInfoExpanded";
    public static final String SHOWROOM_LAST_UPLOADED_AT = "showroomLastUploadedAt";
    public static final String IS_SHOWROOM_UPLOADED = "isShowroomUploaded";
    public static final String VIDEOS_LIST = "videosList";
    public static final String ACCESS_TOKEN_UPDATED_ON = "accessTokenUpdatedOn";
    public static final String USER_PARAM = "user";
    public static final String POST_PARAM = "post";

    public static final String LOAD_FRAGMENT = "fragmentload";
    public static final String MY_PROFILE = "myProfile";
    public static final String OTHER_PROFILE = "otherProfile";
    public static final String USER_ID = "userId";

    public static final String KNOWLEDGE_QUESTION_TYPE = "knowledgeQuestion";
    public static final String KNOWLEDGE_ANSWER_TYPE = "knowledgeAnswer";
    public static final String KNOWLEDGE_POST_TYPE = "knowledgePostType";

    // permissions
    public static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public final static int REQUEST_CODE_ASK_PERMISSIONS = 100;

    public static final String DUMMY_THUMBNAIL_URL = "http://3.6.38.217/tagteen/dummy_thumbnail";
    public static final int MAX_SIZE_RAW_VIDEO_UPLOAD = 10;
}
