package in.tagteen.tagteen.configurations;


public class ServerConnector {

    //public static String BASE_URL = "https://dev-api.tagteen.in";
    public static String BASE_URL = "https://pro-api.tagteen.in";
    //public static String BASE_URL = "http://13.233.207.7";
    public static String BASE_URL_retrofit = BASE_URL + "/write/api/v1.0/user/";
    public static String BASE_URL_retrofit_post = BASE_URL + "/write/api/v1.0/post/";

    public static String REQUEST_FOR_OTP = BASE_URL + "/write/api/v1.0/user/send_verification_code/";
    public static String REQUEST_FOR_REGISTER_USER = BASE_URL + "/write/api/v1.0/user/register/";
    public static String REQUEST_FOR_TAG_FRIEND = BASE_URL + "/write/api/v1.0/user/tag_friend/";
    public static String REQUEST_FOR_LOGIN = BASE_URL + "/write/api/v1.0/user/login/";
    public static String REQUEST_FOR_GUEST_LOGIN = "http://3.6.38.217/tagteen/api/login";
    public static String UNAUTHORISED_USER_LIST = BASE_URL + "/write/api/v1.0/user/get_all_unauthorized_user/";
    public static String FRIEND_LIST = BASE_URL + "/write/api/v1.0/user/get_all_user_friend_list/";
    public static String GET_ALL_CHAT_HISTORY = BASE_URL + "/write/api/v1.0/user/get_all_chat_history/";
    public static String REQUEST_GET_ALL_CATEGORIES = BASE_URL + "/read/api/v1.0/user/get_all_categories/";
    public static String REQUEST_DELETE_YOUTHFEED = BASE_URL + "/write/api/v1.0/user/post_delete/";
    public static String REQUEST_RESET_PASSWORD = BASE_URL + "/write/api/v1.0/user/reset_password/";
    public static String REQUEST_GET_USER_PROFILE = BASE_URL + "/read/api/v1.0/user/get_user_profile/";
    public static String REQUEST_UNFAN = BASE_URL + "/write/api/v1.0/user/un_fan/";
    public static String REQUEST_BE_A_FAN = BASE_URL + "/write/api/v1.0/user/be_a_fan/";
    public static String REQUEST_CHANGE_PASSWORD = BASE_URL + "/write/api/v1.0/user/change_user_password/";
    public static String REQUEST_DEACTIVATE_ACC = BASE_URL + "/write/api/v1.0/user/update_user_deactivation/";
    public static String REQUEST_DELETE_ACC = BASE_URL_retrofit+"remove_user_by_admin/";
    public static String REQUEST_UPDATE_PROFILE = BASE_URL + "/write/api/v1.0/user/update_edit_profile/";

    public static String REQUEST_GET_USER_FANLIST = BASE_URL + "/write/api/v1.0/user/get_user_fan_list/";

    public static String SEND_FRIEND_REQUEST = BASE_URL + "/write/api/v1.0/user/give_friend_request/";
    public static String REQUEST_UPDATE_YOUTHTUBE_VIEWCOUNT = BASE_URL + "/write/api/v1.0/user/update_youtube_view_count/";
    public static String REQUEST_LOGOUT = BASE_URL + "/write/api/v1.0/user/logout/";
    public static String REQUEST_GET_ARTICLE_DETAIL = BASE_URL + "/write/api/v1.0/user/get_post/";
    public static String REQUEST_TEEN_FEED_CATEGORIES = BASE_URL + "/write/api/v1.0/user/get_all_user_category/";
    public static String REQUEST_ADD_SELFY = BASE_URL + "/write/api/v1.0/user/insert_my_selfie/";
    public static String REQUEST_DELETE_POST = BASE_URL + "/write/api/v1.0/user/post_delete/";
    public static String REQUEST_REPORT_POST = BASE_URL + "/write/api/v1.0/user/insert_report/";
    public static String REQUEST_SEARCH_ARTICLE = BASE_URL + "/write/api/v1.0/user/search_articles/";
    public static String REQUEST_GET_SELFY_REACTION = BASE_URL + "/write/api/v1.0/user/get_reaction_on_my_selfie/";
    public static String REQUEST_DELETE_MYSELFY = BASE_URL + "/write/api/v1.0/user/delete_my_selfie/";
    public static String REQUEST_GET_MYSELFY_POS = BASE_URL + "/write/api/v1.0/user/get_my_selfie_post/";
    public static String FORGOT_PASSWORD = BASE_URL + "/write/api/v1.0/user/forgot_password/";
    public static String REQUEST_ADD_SELFIE_REACTION = BASE_URL + "/write/api/v1.0/user/insert_selfie_action/";


    //attach userid with url
    // pass token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiI5NzM4NDA3OTMyIn0.A0XZxDf9n7EoSfK9-T7JzAmt62SxKIR50v-Cs8_ZY2E

    public static String searchlist = BASE_URL + "/write/api/v1.0/user/search_user/";

    // pass token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiI5NzM4NDA3OTMyIn0.A0XZxDf9n7EoSfK9-T7JzAmt62SxKIR50v-Cs8_ZY2E
    //pass path=search string like name,mob number

    public static String addbff = BASE_URL + "/write/api/v1.0/user/add_bff/";
    // pass token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiI5NzM4NDA3OTMyIn0.A0XZxDf9n7EoSfK9-T7JzAmt62SxKIR50v-Cs8_ZY2E
    // body=
    /*{
        "user_id" : "59841df9bdada6550e920af7",
            "friend_user_id" : ["599d45ef2833af61dbffb161"]
    }*/

    public static String removebff = BASE_URL + "/write/api/v1.0/user/remove_bff/";
    // pass token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiI5NzM4NDA3OTMyIn0.A0XZxDf9n7EoSfK9-T7JzAmt62SxKIR50v-Cs8_ZY2E
    // body=

    /*{
        "user_id" : "59841df9bdada6550e920af7",
            "friend_user_id" : "59704cfd2e171116dfe4d0c5"
    }*/

    public static String createpost = BASE_URL + "/write/api/v1.0/user/add_post/";

    /*METHOD : POST
    INPUT : {
        "postCreatorId" : "599bdfc84ad8a70761a34d71",
                "content" : "Hello images1",
                "video" : {"url":"image.url", "height":"12", "width":"12"}
    }
    or
    {
        "postCreatorId" : "599bdfc84ad8a70761a34d71",
            "content" : "Hello images1",
            "image" : [{"url":"image.url", "height":"12", "width":"12"} ]
    }
*/
}
