package in.tagteen.tagteen.database;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.Model.ChatMessage;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.SocketContracts;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.utils.DateTimeCalculation;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import io.socket.emitter.Emitter;

public class SocketServiceProvider extends Service {
    private TagteenApplication signalApplication;
//    private Realm realm;

    public static SocketServiceProvider instance = null;
    private Context mContext;
    private String mSocketSenderId;
    private String mSocketReceiverId, mFriendName;

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    private final IBinder myBinder = new MyBinder();

    public void IsBendable() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyBinder extends Binder {
        public SocketServiceProvider getService() {
            return SocketServiceProvider.this;
        }
    }

    @Override
    public void onCreate() {
        if (isInstanceCreated()) {
            return;
        }
        super.onCreate();
//        realm = Realm.getDefaultInstance();

        mContext = getApplicationContext();

        mSocketSenderId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        mSocketReceiverId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_ID);
        mFriendName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_NAME);

        signalApplication = (TagteenApplication) getApplication();

        if (signalApplication.getSocket() == null)
            signalApplication.mSocket = signalApplication.getSocket();


        signalApplication.getSocket().emit(RegistrationConstants.ADD_CHAT_USER, mSocketSenderId);
        signalApplication.getSocket().on(RegistrationConstants.NEW_CHAT_MESSAGE, onNewMessage);
        signalApplication.getSocket().on(RegistrationConstants.TYPING, onTyping);
        signalApplication.getSocket().on(RegistrationConstants.STOP_TYPING, onStopTyping);
        signalApplication.getSocket().on(RegistrationConstants.DISCONNECT, onDisconnect);
        signalApplication.getSocket().on(RegistrationConstants.USER_LEFT, onLeft);
        signalApplication.getSocket().on(RegistrationConstants.REMOVE_USRER, onRemoveUser);
        signalApplication.getSocket().on(RegistrationConstants.MSG_RECEIVE_SUCCESS, onReceiveSuccess);
        signalApplication.getSocket().on(RegistrationConstants.SERVER_SENDER, onSendStatus);
        signalApplication.getSocket().on(RegistrationConstants.SEEN_STATUS, onSeenStatus);
        signalApplication.getSocket().on(RegistrationConstants.NEW_EVENT, onNewEvent);
        signalApplication.getSocket().on(RegistrationConstants.EVENT_DELIVERED_STATUS, onEventDeleveredStatus);
        signalApplication.getSocket().connect();

//        TagTeenEventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (isInstanceCreated()) {
//            return 0;
//        }
        super.onStartCommand(intent, flags, startId);
        connectConnection();
        return START_STICKY;
    }

    private void msgResponse(String response) {
        try {
            JSONObject data = new JSONObject(response);
            String creatorId = data.optString(SocketContracts.CHAT_CREATOR_ID);
            String receiverId = data.optString(SocketContracts.CHAT_RECEIVER_ID);
            String serverChatId = data.optString(SocketContracts._ID);
            String clientChatId = data.optString(SocketContracts.CLIENT_CHAT_ID);
            String message = data.optString(SocketContracts.CONTENT);
            String messageType = data.optString(SocketContracts.CHAT_TYPE);
            String isPrivate = data.optString(SocketContracts.IS_PRIVATE);
            String mediaUrl = "";
            if (data.has(SocketContracts.MEDIA_LINK)) {
                mediaUrl = data.optString(SocketContracts.MEDIA_LINK);
            }
            String privateMessageTimer = data.optString(SocketContracts.PRIVATE_MSG_TIMER);
            String isGroup = data.optString(SocketContracts.IS_GROUP);
            String date = data.optString(SocketContracts.DATE_CREATED);
            if (isPrivate == "false") {
                isPrivate = DatabaseContracts.IS_FALSE;
            } else if (isPrivate == "true") {
                isPrivate = DatabaseContracts.IS_TRUE;
            }
            String chatPageId;
            String isFromMe;
            JSONArray messageArray = new JSONArray();
            messageArray.put(serverChatId);

            if (mSocketSenderId.equals(creatorId)) {
                chatPageId = creatorId + receiverId;
                isFromMe = DatabaseContracts.IS_TRUE;
            } else {
                chatPageId = receiverId + creatorId;
                isFromMe = DatabaseContracts.IS_FALSE;
            }

            int msgTimer = 0;
            if (privateMessageTimer != null && !privateMessageTimer.isEmpty()) {
                msgTimer = Integer.parseInt(privateMessageTimer);
            }

            updateMyChat(clientChatId, serverChatId, DatabaseContracts.SENT, date, DatabaseContracts.IS_TRUE);


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private boolean updateMyChat(String chatId, String serverChatId, String messageStatus, String date, String uploadStatus) {

        long id = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS, messageStatus);

        if (messageStatus.equals(DatabaseContracts.SENT)) {
            contentValues.put(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID, serverChatId);
            id = DBOpt.getInstance(mContext).updateSendStatus(contentValues, chatId);

            TagTeenEventBus.sendEvent(mContext, "", TagTeenEventBus.TYPE_MESSAGE_SENT);
        } else {

            id = DBOpt.getInstance(mContext).updateDeliveredStatus(contentValues, serverChatId);
            TagTeenEventBus.sendEvent(mContext, "", TagTeenEventBus.TYPE_MESSAGE_DELIVERED);
        }
        return id > 0;

    }

    private void updateDeliveredSeenStatus(JSONObject data, String messageStatus) {

        String serverChatId = data.optString(SocketContracts.SERVER_CHAT_ID);

        updateMyChat("", serverChatId, messageStatus, "", DatabaseContracts.IS_TRUE);
    }

    private Emitter.Listener onNewEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject data = (JSONObject) args[0];
//                        eventResponse(data.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onEventDeleveredStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject data = (JSONObject) args[0];
                        Log.e("onEventDeleveredStatus", "" + data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onSeenStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        updateDeliveredSeenStatus(data, DatabaseContracts.SEEN);
                        Log.e("onSeenStatus", "" + data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onSendStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONObject data = (JSONObject) args[0];
                        Log.e("Server_sender", data + "");
                        msgResponse(data.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONObject data = (JSONObject) args[0];
                        String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");

                       /* if (mSocketSenderId.equals(TypedReceiverId)) {*/
//                        ab_friendStatus.setText("onLine");
//                        ab_friendStatus.setTextColor(Color.GREEN);
                      /*  }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onAddUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");
                     /*   Log.e("Add_user",""+data);
                        if (mSocketSenderId.equals(TypedReceiverId)) {*/
//                        ab_friendStatus.setText("onLine");
//                        ab_friendStatus.setTextColor(Color.GREEN);
                   /*     }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONObject data = (JSONObject) args[0];
                        Log.e("onTyping", "" + data);
                        TagTeenEventBus.sendEvent(mContext, data.toString(), TagTeenEventBus.TYPE_TYPING);
                     /*   String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");
                        if (mSocketSenderId.equals(TypedReceiverId)) {*/
//                        ab_friendStatus.setText("typing..");
//                        ab_friendStatus.setTextColor(Color.GREEN);
                        Log.e("onTyping", "" + data);
                  /*      }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                       /* String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");
                        if (mSocketSenderId.equals(TypedReceiverId)) {*/
//                        ab_friendStatus.setText("onLine");
//                        ab_friendStatus.setTextColor(Color.DKGRAY);
                        Log.e("onStopTyping", "" + data);
//                        Toast.makeText(ChatTreadActivity.this, "onTyping : " + data, Toast.LENGTH_SHORT).show();
                       /* }*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
//                        msgResponse(data.toString(), 0);

                        JSONObject js = null;
                        try {
                            js = new JSONObject();
                            JSONArray receiverId = new JSONArray();
                            receiverId.put(mSocketReceiverId);
                            js.put(SocketContracts.CHAT_CREATOR_ID, mSocketSenderId);
                            js.put(SocketContracts.CHAT_RECEIVER_ID, receiverId);
                            js.put(SocketContracts.SERVER_CHAT_ID, data.get("_id"));
                            signalApplication.getSocket().emit(RegistrationConstants.MSG_RECEIVE_SUCCESS, js);
//                            signalApplication.getSocket().emit(RegistrationConstants.SEEN_STATUS, js);

                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                        insertFriendMessageToDB(data.toString(), js.toString());
                        Log.e("onNewMessage", "" + data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onReceiveSuccess = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];

//                        updateDeliveredSeenStatus(data, DatabaseContracts.DELIVERED);
//                        msgResponse(data.toString(), MESSAGE_DELIVERED);
                        Log.e("onReceiveSuccess", "" + data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
//                        JSONObject data = (JSONObject) args[0];
//                        String TypedSenderId = data.getString("senderId");
//                        String TypedReceiverId = data.getString("mReceiverId");
//                        if (mSocketSenderId.equals(TypedReceiverId)) {
//                            ab_friendStatus.setText("offLine");
//                            ab_friendStatus.setTextColor(Color.DKGRAY);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener onRemoveUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");

//                        if (mSocketSenderId.equals(TypedReceiverId)) {
//                            ab_friendStatus.setText("offLine");
//                            ab_friendStatus.setTextColor(Color.DKGRAY);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void connectConnection() {
        instance = this;
        signalApplication.getSocket().connect();
    }

    private void disconnectConnection() {
        instance = null;
        signalApplication.getSocket().disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        signalApplication.getSocket().off(RegistrationConstants.NEW_CHAT_MESSAGE, onNewMessage);
        signalApplication.getSocket().off(RegistrationConstants.TYPING, onTyping);
        signalApplication.getSocket().off(RegistrationConstants.STOP_TYPING, onStopTyping);
        signalApplication.getSocket().off(RegistrationConstants.DISCONNECT, onDisconnect);
        signalApplication.getSocket().off(RegistrationConstants.USER_LEFT, onLeft);
        signalApplication.getSocket().off(RegistrationConstants.REMOVE_USRER, onRemoveUser);
        signalApplication.getSocket().off(RegistrationConstants.MSG_RECEIVE_SUCCESS, onReceiveSuccess);
        signalApplication.getSocket().off(RegistrationConstants.SERVER_SENDER, onSendStatus);
        signalApplication.getSocket().off(RegistrationConstants.SEEN_STATUS, onSeenStatus);
        signalApplication.getSocket().off(RegistrationConstants.NEW_EVENT, onNewEvent);
        signalApplication.getSocket().off(RegistrationConstants.EVENT_DELIVERED_STATUS, onEventDeleveredStatus);

        //@formatter:off
//            signalApplication.getSocket().off("message"  , message);
        //@formatter:on

        disconnectConnection();
    }


    private void addChatDataToList(ContentValues contentValues, String js) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setEventType(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.EVENT_TYPE));
        chatMessage.setServerId(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID));
        chatMessage.setMessage(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.MESSAGE));
        chatMessage.setMediaLink(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.MEDIA_LINK));
        chatMessage.setMsgType(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.MESSAGE_TYPE));
        chatMessage.setCreaterId(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.SENDER_ID));
        chatMessage.setReceiverId(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.FRIEND_ID));
        chatMessage.setFriendId(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.FRIEND_ID));
        chatMessage.setFriendName(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.FRIEND_NAME));
        chatMessage.setIsPrivate(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.ISPRIVATE_MSG));
        chatMessage.setServerChatId(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID));
        chatMessage.setIsGroup(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.IS_GROUP));
//        chatMessage.setPrivateMessageTime(contentValues.getAsInteger(DatabaseContracts.ChatContractDataBase.PRIVATE_MSG_TIME));
        chatMessage.setThumbNailImage(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.THUMB_NAIL));
        chatMessage.setDownLoadStatus(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.DOWNLOAD_STATUS));
        chatMessage.setUpLoadStatus(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS));
        String messageStatus = contentValues.getAsString(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS);
        chatMessage.setLocalPath(contentValues.getAsString(DatabaseContracts.ChatContractDataBase.LOCATPATH));
        chatMessage.setSendStatus(messageStatus);
        chatMessage.setJsonObject(js);
        String date = contentValues.getAsString(DatabaseContracts.ChatContractDataBase.DATE);
        String time = contentValues.getAsString(DatabaseContracts.ChatContractDataBase.TIME);

        chatMessage.setTime(time);


//        if (date == null || date == "nul" || date == "") {
//            chatMessage.setDate("00:00");
//        } else {
//            chatMessage.setDate(setTime(date));
//            chatMessage.setTime(date);
//        }

        String ispr = chatMessage.getIsPrivate();
        if (ispr == null) {
            ispr = DatabaseContracts.IS_FALSE;
            chatMessage.setIsPrivate(ispr);
        } else {
            chatMessage.setIsPrivate(ispr);
        }


        chatMessage.setMsgViewedStatus("0");
        String MsgType = chatMessage.getMsgType();

        TagTeenEventBus.sendEvent(mContext, chatMessage, TagTeenEventBus.TYPE_NEW_MESSAGE);

    }

    public String setTime(String chatTime) {

        String lastChatTime = null;
        try {
            String lastChatTime_ = DateTimeCalculation.convertToLocalTime(chatTime);
            JSONObject dateInfo = new JSONObject(lastChatTime_);
            long daysDifference = dateInfo.getLong("DAYDIFFERENCE");
            String time = dateInfo.getString("TIME");
            String date = dateInfo.getString("DATE");

            if (daysDifference == 0) {
                lastChatTime = time;
            } else if (daysDifference == 1) {
                lastChatTime = getResources().getString(R.string.yestarday);
            } else {
                lastChatTime = date;
            }

            return lastChatTime;
        } catch (Exception e) {
            e.printStackTrace();
            return lastChatTime;
        }

    }


    private void insertFriendMessageToDB(String response, String js) {


        JSONObject data = null;
        try {
            data = new JSONObject(response);
            String creatorId = data.getString(SocketContracts.CHAT_CREATOR_ID);
            String receiverId = data.getString(SocketContracts.CHAT_RECEIVER_ID);
            String serverChatId = data.optString(SocketContracts._ID);
            String clientChatId = data.getString(SocketContracts.CLIENT_CHAT_ID);
            String message = data.getString(SocketContracts.CONTENT);
            String messageType = data.getString(SocketContracts.CHAT_TYPE);
            String isPrivate = data.getString(SocketContracts.IS_PRIVATE);
            String mediaUrl = "";
            if (data.has(SocketContracts.MEDIA_LINK)) {
                mediaUrl = data.getString(SocketContracts.MEDIA_LINK);
            }
            String privateMessageTimer = data.getString(SocketContracts.PRIVATE_MSG_TIMER);
            String isGroup = data.getString(SocketContracts.IS_GROUP);
            String date = data.getString(SocketContracts.DATE_CREATED);

            if (isPrivate.equals("false")) {
                isPrivate = DatabaseContracts.IS_FALSE;
            } else {
                isPrivate = DatabaseContracts.IS_TRUE;
            }

            ContentValues contentValues = new ContentValues();

            DBOpt.getInstance(mContext).updateRecentTable(creatorId);
            contentValues.put(DatabaseContracts.ChatContractDataBase.SENDER_ID, creatorId);
            contentValues.put(DatabaseContracts.ChatContractDataBase.FRIEND_ID, creatorId);
            contentValues.put(DatabaseContracts.ChatContractDataBase.FRIEND_NAME, mFriendName);
            contentValues.put(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID, serverChatId);
            contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE, message);
            contentValues.put(DatabaseContracts.ChatContractDataBase.LOCATPATH, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_LINK, mediaUrl);
            contentValues.put(DatabaseContracts.ChatContractDataBase.TIME, String.valueOf(System.currentTimeMillis()));
            contentValues.put(DatabaseContracts.ChatContractDataBase.ISPRIVATE_MSG, isPrivate);
            contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS, DatabaseContracts.PENDING);
            contentValues.put(DatabaseContracts.ChatContractDataBase.THUMB_NAIL, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_TYPE, messageType);
            contentValues.put(DatabaseContracts.ChatContractDataBase.IS_GROUP, isGroup);
            contentValues.put(DatabaseContracts.ChatContractDataBase.DOWNLOAD_STATUS, DatabaseContracts.PENDING);
            contentValues.put(DatabaseContracts.ChatContractDataBase.PRIVATE_MSG_TIME, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_HEIGHT, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_WIDTH, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.EVENT_TYPE, DatabaseContracts.EVENT_NULL);

            addChatDataToList(contentValues, js);

            DBOpt.getInstance(mContext).insertChatData(contentValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}