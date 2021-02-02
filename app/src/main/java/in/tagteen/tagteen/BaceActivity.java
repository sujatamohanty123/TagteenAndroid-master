package in.tagteen.tagteen;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.tagteen.tagteen.LocalCasha.DataBaseHelper;
import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.Model.ChatHistoryResponseModel;
import in.tagteen.tagteen.Model.ChatMessangerModel;
import in.tagteen.tagteen.Model.getChatJSONInputModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.SocketContracts;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaceActivity extends AppCompatActivity {
    Socket mSocket;
    String mSocketSenderId;
    SQLiteDatabase db;
    DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceSingleton.getInstance().init(this);
        mSocketSenderId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();
        socketInit();
    }

    private void socketInit() {
        TagteenApplication tagteenApplication = (TagteenApplication) getApplication();
        mSocket = tagteenApplication.getSocket();
        mSocket.on(RegistrationConstants.ADD_CHAT_USER, onAddUser);
        mSocket.emit(RegistrationConstants.ADD_CHAT_USER, mSocketSenderId);
        mSocket.on(RegistrationConstants.NEW_CHAT_MESSAGE, onNewMessage);
        mSocket.on(RegistrationConstants.DISCONNECT, onDisconnect);
        mSocket.on(RegistrationConstants.MSG_RECEIVE_SUCCESS, onReceiveSuccess);
        mSocket.on(RegistrationConstants.SERVER_SENDER, onSendStatus);
        mSocket.on(RegistrationConstants.SEND_STATUS, onSendStatus);
        mSocket.on(RegistrationConstants.SEEN_STATUS, onSeenStatus);
        mSocket.connect();
    }


    private Emitter.Listener onSeenStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject data = (JSONObject) args[0];
                        Log.e("onServerReceiveSuccess",""+data);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject data = (JSONObject) args[0];
                        msgResponse(data.toString(),1);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");
                        Log.e("Add_user",""+data);
                        if (mSocketSenderId.equals(TypedReceiverId)) {
                        }
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        Log.e("onNewMasssage", "" + data);
                        msgResponse(data.toString(),0);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");

                        if (mSocketSenderId.equals(TypedReceiverId)) {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.emit(RegistrationConstants.DISCONNECT,mSocketSenderId);
    }

    public void msgResponse(String Response, int type){
        try{
            JSONObject data=new JSONObject(Response);
            String creatorId = data.getString(SocketContracts.CHAT_CREATOR_ID);
            String receiverId=data.getString(SocketContracts.CHAT_RECEIVER_ID);
            String chatId = data.getString(SocketContracts._ID);
            String clientChatId = data.getString(SocketContracts.CLIENT_CHAT_ID);
            String message = data.getString(SocketContracts.CONTENT);
            String massageType = data.getString(SocketContracts.CHAT_TYPE);
            String isPrivate = data.getString(SocketContracts.IS_PRIVATE);
            String mediaUrl=data.getString(SocketContracts.MEDIA_LINK);
            String privateMessageTimer=data.getString(SocketContracts.PRIVATE_MSG_TIMER);
            String isGroup=data.getString(SocketContracts.IS_GROUP);
            String date=data.getString(SocketContracts.DATE_CREATED);

            String chatPageId;
            String isFromMe;
            JSONArray messageArray = new JSONArray();
            messageArray.put(chatId);

            if (mSocketSenderId.equals(creatorId)) {
                chatPageId = creatorId+ receiverId;
                isFromMe = DatabaseContracts.IS_TRUE;
            } else {
                chatPageId = receiverId + creatorId;
                isFromMe = DatabaseContracts.IS_FALSE;
            }

            int msgTimer=Integer.parseInt(privateMessageTimer);
            if(type==0){
                InsertChatData(chatId, creatorId, receiverId, message, null, mediaUrl, massageType, null, date, "newTime", chatPageId, isFromMe, isGroup, isPrivate, privateMessageTimer);
                mSocket.emit(RegistrationConstants.MSG_RECEIVE_SUCCESS, messageArray, mSocketSenderId, receiverId);
                mSocket.emit(RegistrationConstants.SEEN_STATUS,messageArray,mSocketSenderId, receiverId);
            }else{
                updateMyChat(clientChatId, chatId,DatabaseContracts.DELIVERED,date);
            }
        }catch (Exception e){}
    }

    public long InsertChatData(String id, String senderId, String reciverId, String msg, String localPath, String awsUrl,
                               String MsgType, String isViewed, String Date, String Time, String page_id, String is_FromMe, String isgroup, String isPrivate, String privateMessageTime) {

        String DefautValue = null;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.CHAT_ID, id);
        contentValues.put(DatabaseContracts.ChatContractDataBase.CREATOR_ID, senderId);
        contentValues.put(DatabaseContracts.ChatContractDataBase.RECEIVER_USER_ID, reciverId);
        contentValues.put(DatabaseContracts.ChatContractDataBase.CHAT_PAGE_ID, page_id);
        contentValues.put(DatabaseContracts.ChatContractDataBase.IS_FROM_ME, is_FromMe);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE, msg);
        contentValues.put(DatabaseContracts.ChatContractDataBase.LOCATPATH, localPath);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_LINK, awsUrl);
        contentValues.put(DatabaseContracts.ChatContractDataBase.ISVIEWED, isViewed);
        contentValues.put(DatabaseContracts.ChatContractDataBase.DATE, Date);
        contentValues.put(DatabaseContracts.ChatContractDataBase.TIME, Time);
        contentValues.put(DatabaseContracts.ChatContractDataBase.ISPRIVATE_MSG, isPrivate);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS, DatabaseContracts.PENDING);
        contentValues.put(DatabaseContracts.ChatContractDataBase.THUMB_NAIL, DefautValue);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_TYPE, MsgType);
        contentValues.put(DatabaseContracts.ChatContractDataBase.IS_GROUP, isgroup);
        contentValues.put(DatabaseContracts.ChatContractDataBase.DOWNLOAD_STATUS, DatabaseContracts.PENDING);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS ,DatabaseContracts.DELIVERED);
        contentValues.put(DatabaseContracts.ChatContractDataBase.PRIVATE_MSG_TIME, privateMessageTime);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_HEIGHT, DefautValue);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_WIDTH, DefautValue);
        long insertedId = db.insert(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, null, contentValues);
        return insertedId;
    }
    private boolean updateMyChat(String _Id, String chatId, String massageStatus,String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.CHAT_ID, chatId);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS,massageStatus);
        contentValues.put(DatabaseContracts.ChatContractDataBase.DATE,date);
        db.update( DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, contentValues, DatabaseContracts.ChatContractDataBase._ID + "= ? ", new String[]{_Id});
        return true;
    }

    private void callChatHistory(String userid) {
        final getChatJSONInputModel getjsoninput = new getChatJSONInputModel();
        getjsoninput.setChatReceiverId(userid);
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        Call<ChatHistoryResponseModel> call = methods.chatHistory(getjsoninput, token);
        call.enqueue(new Callback<ChatHistoryResponseModel>() {

            @Override
            public void onResponse(Call<ChatHistoryResponseModel> call, Response<ChatHistoryResponseModel> response) {
                int statuscode = response.code();
                if (statuscode == 200) {
                    ChatHistoryResponseModel chathistorymodel = response.body();
                    ArrayList<ChatMessangerModel> chatMessangerModelArrayList = chathistorymodel.getChatMessangerModelArrayList();
                    for (int i = 0; i < chatMessangerModelArrayList.size(); i++) {
                        ChatMessangerModel chm = chatMessangerModelArrayList.get(i);
                        String chatPageId;
                        String isFromMe;
                        String creatorId=chm.getChat_creator_id();
                        if (mSocketSenderId.equals(creatorId)) {
                            chatPageId = chm.getChat_creator_id() + chm.getChat_receiver_id();
                            isFromMe = DatabaseContracts.IS_TRUE;
                        } else {
                            chatPageId = chm.getChat_receiver_id() + chm.getChat_creator_id();
                            isFromMe = DatabaseContracts.IS_FALSE;
                        }
                        String massageType = chm.getMassageType();
                        String mediaUrl = null;

                        if (mSocketSenderId.equals(creatorId)) {
                          //  updateMyChat(chm.getClientId(),chm.get_id(),DatabaseContracts.IS_TRUE,);
                        } else {
                           /* InsertChatData(String msg, String localPath, String awsUrl,
                                    String MsgType, String isViewed, String Date, String Time, String page_id, String is_FromMe, String isgroup, String isPrivate, String privateMessageTime)
                            */

                             //   InsertChatData(chm.get_id(), chm.getChat_creator_id(), chm.getChat_receiver_id(), chm.getContent(), "null", chm.getImage(), chatPageId, , chm.getMassageType(), chm.getContent(), mediaUrl, isFromMe);

                            }
                    }
                } else if (statuscode == 401) {
                }

            }

            @Override
            public void onFailure(Call<ChatHistoryResponseModel> call, Throwable t) {

            }
        });
    }
    }

