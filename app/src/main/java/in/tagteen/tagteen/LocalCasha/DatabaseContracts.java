package in.tagteen.tagteen.LocalCasha;



public class DatabaseContracts {

    public final static String TAGTEEN_DATABASE    = "tagteen_DataBase";
    public final static int DATABASE_VERSION   = 1;

    /*Database constant for MESSAGE TYPE*/

    public final static int ISIMAGE    = 1;
    public final static int ISVEDIO    = 2;
    public final static int ISAUDIO    = 3;
    public final static int ISREACTION = 4;
    public final static int ISPING     = 5;
    public final static int SOUND_EMOJI = 6;
    public final static int ISTEXTMSG  = 7;
    public final static int ISDOC      = 8;
    public final static int ISCHAT_SUB_MSG     = 9;
    /*Database constant*/
    public final static String IS_TRUE      = "1";
    public final static String IS_FALSE     = "0";
   /*chatCountKey*/
    public final static String CHAT_OF        = "count";
    /*MESSAGE STATUS*/

    public final static String PENDING         = "0";
    public final static String SENT            = "1";
    public final static String DELIVERED       = "2";
    public final static String SEEN            = "3";
    public final static String SENT_TO_SERVER  = "4";
    public final static String SERVER_RECEIVED = "5";
    public final static String SENDING = "6";


    /*event type*/

    public final static String EVENT_NULL        = "0";
    public final static String EVENT_LIKE        = "1";
    public final static String EVENT_UNlIKE      = "2";
    public final static String EVENT_RECALL      = "3";
    public final static String EVENT_AGREE       = "4";
    public final static String EVENT_DISAGREE    = "5";
    public final static String REPLY_EVENT       = "6";
  /*Database constant*/
    public static class ChatContractDataBase{

        public final static String CHAT_MESSANGER_TABLE = "chatMessangerTable";
        /*Local  id*/
        public final static String _ID = "_Id";
        /*Server id*/
        public final static String OWNER_ID = "ownerID";
      public final static String CHAT_ID = "chatId";
      public final static String CREATOR_ID = "creatorId";
      public final static String RECEIVER_USER_ID = "receiverId";
      public final static String CHAT_PAGE_ID = "chatPageId";
      public final static String LOCATPATH = "media_local_path";
      public final static String MESSAGE = "message_text";
      public final static String MEDIA_LINK = "media_link";
      public final static String THUMB_NAIL = "media_thumbnail";
      public final static String ISPRIVATE_MSG = "isPrivateMsg";
      public final static String DATE = "date";
      public final static String ISVIEWED = "isViewed";
      public final static String TIME = "time";
      public final static String MEDIA_UPLOAD_STATUS = "media_upload_status";
      public final static String DOWNLOAD_STATUS = "media_download_status";
      public final static String MESSAGE_TYPE = "message_type";
      public final static String IS_GROUP = "isGroup";
      public final static String IS_FROM_ME = "isFromMe";
      public final static String MESSAGE_STATUS = "message_status";
      public final static String MESSAGE_EXP_TIME = "message_exp_time";
      public final static String PRIVATE_MSG_TIME = "privateMsgTime";
      public final static String MEDIA_HEIGHT = "media_height";
      public final static String MEDIA_WIDTH = "media_width";
      public final static String EVENT_TYPE = "event_type";
      public final static String EVENT_STATUS = "event_status";
      public final static String IS_REPLY = "isReply";
      public final static String REPLY_TO_MSG_TYPE = "reply_msg_type";
      public final static String REPLY_MESSAGE = "reply_message";
      public final static String REPLY_TO_MEDIA = "reply_media_link";
      public final static String SERVER_CHAT_ID = "server_chatid";
      public final static String FRIEND_ID = "friendID";
      public final static String FRIEND_NAME = "friendName";
      public final static String FRIEND_IMAGE = "friendImage";
      public final static String SENDER_ID = "sender_id";
      public final static String IS_LOCKED = "isLocked";
      public final static String UNREAD_COUNT = "unreadCount";
    }

    public static class userLisTable{

        public final static String FRIEND_LIST_TABLE = "friendListTable";
        public final static String USERID = "userId";
        public final static String IS_GROUP = "isGroup";
        public final static String USERNAME = "userName";
        public final static String lAST_MESSAGE = "LastMessage";
        public final static String lAST_MESSAGE_DATE = "lastMsgDate";
        public final static String lAST_MESSAGE_TIME = "lastMsgTime";
        public final static String CHAT_COUNT = "chatCount";
        public final static String TAG_NUMBER = "tagNumber";
        public final static String PROFILE_IMAGE = "image";
        public final static String LAST_MESSAGE_STATUS = "msgStatus";
        public final static String IS_HIDEN = "isHide";
        public final static String IS_CHAT_STARTED = "isChatStarted";
        public final static String IS_LOCKED = "isLocked";
        public final static String IS_BLOCK = "isBlock";
        public final static String IS_MUTE  = "isMute";

    }


    public static class FaceReactionDataBase{

        public final static String REACTION_TABLE = "faceReactionTable";
        public final static String ID = "id";
        public final static String REACTION_NAME = "reactionName";
        public final static String REACTON_AWS_URL = "reactionUrl";
        public final static String REACTION_lOCAL_PATH = "reactionLocalPath";

    }
}
