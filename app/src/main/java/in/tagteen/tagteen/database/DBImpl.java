package in.tagteen.tagteen.database;

/**
 * Created by WM-201 on 10/17/2016.
 */

public interface DBImpl {

    int DATABASE_VERSION = 1;
    String DB_NAME = "tagteen_chat.sqlite";

    String TBL_CONTACT = "contact";
    String TBL_BLOCK = "block";
    String TBL_CHAT = "chat";
    String TBL_RECENT = "recent";
    String TBL_GROUP = "group_info";
    String TBL_GROUP_MEMBER = "group_member";
    String TBL_PHOTO_STREAM = "photo_stream";

    String TBL_MESSAGE_CHAT = "MessageTable";
    String TBL_HIDE_LOCK = "HideLock";

    String CL_CONTACT_ID = "co_id";
    String CL_CONTACT_NAME = "co_name";
    String CL_CONTACT_NUMBER = "co_number";
    String CL_CONTACT_FILED_TYPE = "co_field_type";
    String CL_CONTACT_IMAGE = "co_image";

    String CL_CREATE_DATE = "create_date";
    String CL_MODIFIED_DATE = "modified_date";

    String CL_P_CHAT = "pchat";
    String CL_SYNC = "sync";
    String CL_PROCESS = "process";

    String CL_FAVORITE = "favorite";
    String CL_INVITE = "invite";

    String CL_VC_NAME = "vc_name";
    String CL_VC_PRO_IMAGE_UPDATE = "vc_image_update";
    String CL_VC_STATUS = "vc_status";
    String CL_VC_STATUS_DATE = "vc_status_date";

    String CL_VC_PRO_IMAGE_DOWNLOAD = "vc_image_download";

    String CL_PV_STATUS = "pv_status";
    String CL_PV_PRO_IMAGE = "pv_pro_image";
    String CL_PV_ONLINE = "pv_online";
    String CL_PV_LAST_SEEN = "pv_last_seen";

    String CL_DRAW_COLOR = "draw_color";

    String CL_BLOCK_CONTACT_NUMBER = "block_number";

    //Chat
    String CL_PACKET_ID = "packet_id";
    String CL_BUDDY = "buddy";
    String CL_FROM = "msg_from";
    String CL_CHAT_TYPE = "chat_type"; //chat, group
    String CL_MESSAGE = "message";
    String CL_PACKET = "packet";
    String CL_MESSAGE_TYPE = "msg_type"; //text, image etc
    String CL_TIME_EXPIRE = "expire_at";
    String CL_TIMESTAMP = "timestamp";
    String CL_TIME_SENT = "sent_at";
    String CL_TIME_DELIVERED = "delivered_at";
    String CL_TIME_READ = "read_at";
    String CL_TIME_SCHEDULE = "schedule_at";
    String CL_FILE_LOCALE = "file_locale";
    String CL_FILE_THUMB = "file_thumb";
    String CL_FILE_SERVER = "file_server";
    String CL_FILE_PROGRESS = "file_progress";
    String CL_SECURE = "secure";

    String CL_ARCHIVE = "archive";
    String CL_UNREAD_COUNT = "unread_count";

    //Group
    String CL_GROUP_ID = "group_id";
    String CL_GROUP_SUBJECT = "group_subject";
    String CL_GROUP_AVATAR = "group_avatar";
    String CL_GROUP_DELETED = "deleted";
    String CL_GROUP_CREATED_DATE = "created_at";
    String CL_GROUP_MEMBER = "group_member";
    String CL_GROUP_MEMBER_AFFIL = "affiliation";
    String CL_GROUP_LAST_MESSAGE = "last_message";


    String OWNER_ID = "ownerID";
    String CHAT_ID = "chatId";
    String CREATOR_ID = "creatorId";
    String RECEIVER_USER_ID = "receiverId";
    String CHAT_PAGE_ID = "chatPageId";
    String LOCATPATH = "media_local_path";
    String MESSAGE = "message_text";
    String MEDIA_LINK = "media_link";
    String THUMB_NAIL = "media_thumbnail";
    String ISPRIVATE_MSG = "isPrivateMsg";
    String DATE = "date";
    String ISVIEWED = "isViewed";
    String TIME = "time";
    String MEDIA_UPLOAD_STATUS = "media_upload_status";
    String MEDIA_DOWNLOAD_STATUS = "media_download_status";
    String MESSAGE_TYPE = "message_type";
    String IS_GROUP = "isGroup";
    String MESSAGE_STATUS = "message_status";
    String MESSAGE_EXP_TIME = "message_exp_time";
    String PRIVATE_MSG_TIME = "privateMsgTime";
    String MEDIA_HEIGHT = "media_height";
    String MEDIA_WIDTH = "media_width";
    String EVENT_TYPE = "event_type";
    String EVENT_STATUS = "event_status";
    String IS_REPLY = "isReply";
    String REPLY_TO_ID = "reply_chat_id";
    String REPLY_TO_MSG_TYPE = "reply_msg_type";
    String REPLY_MESSAGE = "reply_message";
    String REPLY_TO_MEDIA = "reply_media_link";
    String SERVER_CHAT_ID = "server_chatid";
    String SENDER_ID = "sender_id";
    String FRIEND_ID = "friendID";
    String FRIEND_NAME = "friendName";
    String FRIEND_IMAGE = "friendImage";
    String LOCK_STATUS = "lock_status";
    String HIDE_STATUS = "hide_status";
    String LOCK_ID = "id";
    String LOCK_FRIEND_ID = "friend_id";

    //Query
    String QUERY_CREATE_CONTACT_TABLE = "create table " + TBL_CONTACT + "("
            + CL_CONTACT_ID + " text , "
            + CL_CONTACT_NAME + " text , "
            + CL_CONTACT_NUMBER + " text , "
            + CL_CONTACT_FILED_TYPE + " text , "
            + CL_CONTACT_IMAGE + " text , "
            + CL_P_CHAT + " integer default 0, "
            + CL_SYNC + " integer default 0, "
            + CL_PROCESS + " integer default 0, "
            + CL_FAVORITE + " integer default 0, "
            + CL_INVITE + " integer default 0, "
            + CL_VC_NAME + " text , "
            + CL_VC_PRO_IMAGE_UPDATE + " text , "
            + CL_VC_PRO_IMAGE_DOWNLOAD + " text , "
            + CL_VC_STATUS + " text , "
            + CL_VC_STATUS_DATE + " text , "
            + CL_PV_STATUS + " integer default 0, "
            + CL_PV_PRO_IMAGE + " integer default 0, "
            + CL_PV_ONLINE + " integer default 0, "
            + CL_PV_LAST_SEEN + " integer default 0, "
            + CL_DRAW_COLOR + " integer default 0, "
            + CL_CREATE_DATE + " text , "
            + REPLY_TO_MEDIA + " text, "
            + REPLY_TO_MSG_TYPE + " text, "
            + CL_MODIFIED_DATE + " text )";

    //Query
    String QUERY_CREATE_CHAT_TABLE = "create table " + TBL_MESSAGE_CHAT + "("
            + CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + FRIEND_ID + " text , "
            + FRIEND_NAME + " text , "
            + FRIEND_IMAGE + " text , "
            + SENDER_ID + " text, "
            + SERVER_CHAT_ID + " text, "
            + IS_GROUP + " BOOLEAN DEFAULT false , "
            + MESSAGE + " text , "
            + ISPRIVATE_MSG + " BOOLEAN DEFAULT false , "
            + DATE + " text , "
            + TIME + " text , "
            + CL_TIMESTAMP + " integer default 0, "
            + MESSAGE_STATUS + " integer default 0, "
            + MESSAGE_TYPE + " text , "
            + MESSAGE_EXP_TIME + " text , "
            + EVENT_TYPE + " text , "
            + EVENT_STATUS + " text , "
            + IS_REPLY + " boolean default false , "
            + REPLY_TO_ID + " text , "
            + LOCATPATH + " text, "
            + MEDIA_LINK + " text, "
            + THUMB_NAIL + " text, "
            + MEDIA_HEIGHT + " text, "
            + MEDIA_WIDTH + " text, "
            + REPLY_TO_MEDIA + " text, "
            + REPLY_TO_MSG_TYPE + " text, "
            + MEDIA_UPLOAD_STATUS + " text, "
            + PRIVATE_MSG_TIME + " text, "
            + MEDIA_DOWNLOAD_STATUS + " text )";

    String QUERY_CREATE_LOCK_TABLE = "CREATE TABLE " + TBL_HIDE_LOCK + " ("
            + LOCK_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , "
            + OWNER_ID + " TEXT , "
            + LOCK_FRIEND_ID + " TEXT , "
            + LOCK_STATUS + " INTEGER DEFAULT 0 , "
            + HIDE_STATUS + " INTEGER DEFAULT 0 )";

    String QUERY_CREATE_RECENT_TABLE = "create table " + TBL_RECENT + "("
            + CL_PACKET_ID + " text , "
            + LOCK_FRIEND_ID + " text , "
            + MESSAGE_TYPE + " text , "
            + CL_UNREAD_COUNT + " integer default 0 , "
            + CL_TIMESTAMP + " text )";

    String QUERY_CREATE_GROUP_TABLE = "create table " + TBL_GROUP + "("
            + CL_GROUP_ID + " text , "
            + CL_GROUP_AVATAR + " text , "
            + CL_GROUP_SUBJECT + " text , "
            + CL_DRAW_COLOR + " integer default 0 , "
            + CL_GROUP_DELETED + " integer default 0 , "
            + CL_GROUP_LAST_MESSAGE + " text , "
            + CL_GROUP_CREATED_DATE + " text )";

    String QUERY_CREATE_GROUP_MEMBER_TABLE = "create table " + TBL_GROUP_MEMBER + "("
            + CL_GROUP_ID + " text , "
            + CL_GROUP_MEMBER + " text , "
            + CL_GROUP_MEMBER_AFFIL + " integer default 0 )";

    String QUERY_CREATE_PHOTO_STREAM_TABLE = "create table " + TBL_PHOTO_STREAM + "("
            + CL_PACKET_ID + " text , "
            + CL_BUDDY + " text , "
            + CL_FROM + " text , "
            + CL_CHAT_TYPE + " text , "
            + CL_MESSAGE_TYPE + " text , "
            + CL_SECURE + " integer default 0, "
            + CL_TIME_SCHEDULE + " text , "
            + CL_FILE_LOCALE + " text , "
            + CL_FILE_THUMB + " text , "
            + CL_FILE_SERVER + " text , "
            + CL_TIMESTAMP + " text )";

}
