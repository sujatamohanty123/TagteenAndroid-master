package in.tagteen.tagteen.chatting.socket;

import in.tagteen.tagteen.chatting.utils.ChatSessionManager;

public final class SocketConstants {

//    https://dev-api.tagteen.in/write/api/v1.0/user/login/
//
//    ajit
//    mobile:"8917681857"
//    sonu dutta
//    8328959802
//    password: 123123
//            5b6de14d036d357a314ebbbd
//    token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiI4MzI4OTU5ODAyIn0.DyME0-m7arkRgLgbbOMVBPICXFJlbHMxWr8wuciIB1o

    public static final String HOST = "https://pro-api.tagteen.in";
//    public static final String HOST = "https://dev-api.tagteen.in";

    //testing
//    public static final String HOST = "http://13.233.207.7";
//    public static final String HOST = "http://13.232.53.84:8000";
//    public static final String HOST = "http://122.171.66.217:8000";

    private static final String SERVICE = "/write/api/v1.0/user/";

    public static final String CHAT_DETAILS = HOST + SERVICE + "get_all_user_friend_list/";
    public static final String CHAT_DELIVERY = HOST + "/write/api/v1.0/chat/delivery_chat";
    public static final String CHAT_HISTORY = HOST + "/write/api/v1.0/user/get_all_chat_history";
    public static final String CHAT_DELETE = HOST + "/write/api/v1.0/user/delete_all_chat";

    private SocketConstants() throws IllegalAccessException {
        throw new IllegalAccessException("Cannot create an instance for SocketConstants");
    }

    public static class Event {

        /**
         * Emit as soon as you launch the app only if user is logged in and verified
         */
        public static final String INIT_USER = "ADD_USER";

        /**
         * Emit for new message or will be called when new message arrives
         */
        public static final String NEW_MESSAGE = "NEW_MESSAGE";

        /**
         * Emit when typing or will be called when a specific user starts typing
         */
        public static final String TYPING = "TYPING";

        /**
         * Emit when stops typing
         */
        public static final String STOP_TYPING = "STOP_TYPING";

        /**
         * Emit while the app is getting Logout or Crashing or Closing or Terminating
         */
        public static final String DISCONNECT = "DISCONNECT";

        /**
         * Emit while the app is getting Logout or Crashing or Closing or Terminating
         */
        public static final String USER_LEFT = "USER_LEFT";

        /**
         * Emit whenever you received a message or will be called when a friend received yours.
         */
        public static final String DELIVERY_STATUS = "DELIVERED_STATUS";

        /**
         * Emit after seeing a message or will be called when a friend has seen yours.
         */
        public static final String SEEN_STATUS = "SEEN_STATUS";

        /**
         * Called when your message sent to the server
         */
        public static final String SERVER_SENDER = "SERVER_SENDER";

        /**
         * Called when any event has been received by the receiver
         */
        public static final String NEW_DELIVERED = "NEW_EVENT_DELIVERED";

        /**
         * Called when an event SEEN_STATUS has been seen by the receiver
         */
        public static final String NEW_SEEN = "NEW_EVENT_SEEN";

        /**
         * Delete local storage(database or file) on receiving this
         */
        public static final String RECALL = "RECALL";

        /**
         * Acknowledge the recall
         */
        public static final String ACK_CALL = "ACK_RECALL";

        /**
         * Emit for other events like Liked,Agree,Recal...
         */
        public static final String NEW_EVENT = "NEW_EVENT";

        public static final String ONLINE_FRIENDS = "GET_ONLINE_USER_FRIENDS_LIST";

        private static final String[] events = {
                INIT_USER, NEW_MESSAGE, TYPING, STOP_TYPING,
                DISCONNECT, USER_LEFT, DELIVERY_STATUS, SEEN_STATUS,
                SERVER_SENDER, NEW_DELIVERED, NEW_SEEN, RECALL,
                ACK_CALL, NEW_EVENT, ONLINE_FRIENDS
        };

        public static String[] get() {
            return events;
        }
    }

    public static final class MessageType {

        public static final int PHOTO = 1;
        public static final int VIDEO = 2;
        public static final int AUDIO = 3;
        public static final int FACIAL_REACTION = 4;
        public static final int PING = 5;
        public static final int SOUND_EMOJI = 6;
        public static final int TEXT = 7;
        public static final int DOCUMENT = 8;
        public static final int REPLY = 9;
        public static final int LOCATION = 10;
        public static final int YOUTUBE = 11;

    }

}
