package in.tagteen.tagteen.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.Model.ChatMessage;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.utils.DateTimeCalculation;

public class DBOpt extends SQLiteOpenHelper implements DBImpl {
    private Context mContext;
    private SQLiteDatabase database;

    private DBOpt(Context context) {

        super(context, DB_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public static DBOpt getInstance(Context context) {
        return new DBOpt(context);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL(QUERY_CREATE_CONTACT_TABLE);
        sqLiteDatabase.execSQL(QUERY_CREATE_LOCK_TABLE);
        sqLiteDatabase.execSQL(QUERY_CREATE_CHAT_TABLE);
        sqLiteDatabase.execSQL(QUERY_CREATE_RECENT_TABLE);
//        sqLiteDatabase.execSQL(QUERY_CREATE_GROUP_TABLE);
//        sqLiteDatabase.execSQL(QUERY_CREATE_GROUP_MEMBER_TABLE);
//        sqLiteDatabase.execSQL(QUERY_CREATE_PHOTO_STREAM_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QUERY_CREATE_CONTACT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TBL_HIDE_LOCK);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TBL_CHAT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TBL_RECENT);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QUERY_CREATE_GROUP_TABLE);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QUERY_CREATE_GROUP_MEMBER_TABLE);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QUERY_CREATE_PHOTO_STREAM_TABLE);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<ChatMessage> getChatList(String isPrivateMode,String querystring,boolean issearching) {
        ArrayList<ChatMessage> chatMessageList = new ArrayList<>();

        try {
            database = getReadableDatabase();
            StringBuilder friendIDS = new StringBuilder("");
            if ( !issearching){
                String sqHideLock = "SELECT  friend_id FROM " + TBL_HIDE_LOCK + " WHERE  hide_status = 1 ";
                Cursor cursor = database.rawQuery(sqHideLock, null);
                if (cursor.moveToFirst()) {
                    do {
                        if (friendIDS.toString().isEmpty())
                            friendIDS.append(cursor.getString(cursor.getColumnIndex(LOCK_FRIEND_ID)));
                        else
                            friendIDS.append(friendIDS).append(", ").append(cursor.getString(cursor.getColumnIndex(LOCK_FRIEND_ID)));

                    } while (cursor.moveToNext());
                }
            }

            String sql = "SELECT  mt.*,(SELECT Count(hl.id) FROM HideLock hl WHERE hl.friend_id = mt.friendID AND hl.lock_status = 1) as isLocked, (SELECT unread_count FROM recent rc WHERE rc.friend_id = mt.friendID) as unreadCount FROM  MessageTable mt ";;
            if (!friendIDS.toString().isEmpty() && !issearching) {
                sql = "SELECT  mt.*,(SELECT Count(hl.id) FROM HideLock hl WHERE hl.friend_id = mt.friendID AND hl.lock_status = 1) as isLocked FROM  MessageTable mt  WHERE mt.FriendID  NOT IN (" + friendIDS + ")";
            }

            if (querystring!=null){
                sql=sql+" WHERE  mt.friendName LIKE'%"+querystring+"%'";

            }

            sql=sql+" GROUP BY mt.FriendID ORDER BY mt.chatid DESC LIMIT 0,20";

            Cursor res = database.rawQuery(sql, null);

            if (res.moveToFirst()) {
                do {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setEventType(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.EVENT_TYPE)));
                    chatMessage.setServerId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.CHAT_ID)));
                    chatMessage.setMessage(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MESSAGE)));
                    chatMessage.setMediaLink(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MEDIA_LINK)));
                    chatMessage.setMsgType(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MESSAGE_TYPE)));
                    chatMessage.setCreaterId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.SENDER_ID)));
                    chatMessage.setReceiverId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.FRIEND_ID)));
                    chatMessage.setFriendName(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.FRIEND_NAME)));
                    chatMessage.setFriendId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.FRIEND_ID)));
                    chatMessage.setIsPrivate(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.ISPRIVATE_MSG)));
                    chatMessage.setServerChatId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID)));
//                    chatMessage.setChatPageId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.CHAT_PAGE_ID)));
                    chatMessage.setIsGroup(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.IS_GROUP)));
                    chatMessage.setPrivateMessageTime(res.getInt(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.PRIVATE_MSG_TIME)));
                    chatMessage.setThumbNailImage(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.THUMB_NAIL)));
                    chatMessage.setDownLoadStatus(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.DOWNLOAD_STATUS)));
                    chatMessage.setUpLoadStatus(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS)));
//                    String view = res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.ISVIEWED));
                    String messageStatus = res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS));
                    chatMessage.setLocalPath(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.LOCATPATH)));
                    chatMessage.setLocked(res.getInt(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.IS_LOCKED)));
                    chatMessage.setUnreadCount(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.UNREAD_COUNT)));
                    chatMessage.setSendStatus(messageStatus);
                    String date = res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.DATE));
                    if (date == null || date == "nul" || date == "") {
                        chatMessage.setDate("00:00");
                    } else {
                        chatMessage.setDate(setTime(date));
                        chatMessage.setTime(date);
                    }

                    String ispr = chatMessage.getIsPrivate();
                    if (ispr == null) {
                        ispr = DatabaseContracts.IS_FALSE;
                        chatMessage.setIsPrivate(ispr);
                    } else {
                        chatMessage.setIsPrivate(ispr);
                    }


                    chatMessage.setMsgViewedStatus("0");


                    String MsgType = chatMessage.getMsgType();
//                    if (MsgType.equals(DatabaseContracts.ISIMAGE + "") || MsgType.equals(DatabaseContracts.ISVEDIO + "") || MsgType.equals(DatabaseContracts.ISDOC + "") || MsgType.equals(DatabaseContracts.ISAUDIO + "")) {
//                        if (chatMessage.getIsFromMe() == DatabaseContracts.IS_FALSE) {
//                            if (chatMessage.getDownLoadStatus() == DatabaseContracts.IS_FALSE) {
//                                MediaDownload(chatMessage.getMediaLink(), chatMessage.getClintId());
//                            }
//                        } else {
//                            if (chatMessage.getSendStatus() == DatabaseContracts.PENDING) {
//                                if (chatMessage.getUpLoadStatus() == DatabaseContracts.IS_FALSE) {
//                                    File file = new File(chatMessage.getLocalPath());
//                                    MediaUpload(file, this, chatMessage.getMsgType(), mSocketSenderId, chatMessage.getClintId());
//                                } else {
//                                    senderMessage(chatMessage.getMsgType(), chatMessage.getMediaLink(), chatMessage.getMessage(), chatMessage.getClintId(), chatMessage.getIsPrivate());
//                                }
//                            }
//                        }
//                    } else {
//                        if (chatMessage.getSendStatus() == DatabaseContracts.PENDING) {
//                            senderMessage(chatMessage.getMsgType(), chatMessage.getMediaLink(), chatMessage.getMessage(), chatMessage.getClintId(), chatMessage.getIsPrivate());
//                        }
//                    }
//                    modelId.add(chatMessage.getClintId());

                    chatMessageList.add(chatMessage);
                } while (res.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();

        }

        return chatMessageList;
    }

    public ArrayList<ChatMessage> getChatDetail(String friendID) {
        ArrayList<ChatMessage> chatMessageList = new ArrayList<>();

        try {
            database = getReadableDatabase();


            String sql = "SELECT  * from " + TBL_MESSAGE_CHAT + " WHERE " + FRIEND_ID + " = '" + friendID + "' LIMIT 0,100";

            Cursor res = database.rawQuery(sql, null);

            if (res.moveToFirst()) {
                do {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setEventType(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.EVENT_TYPE)));
                    chatMessage.setServerId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.CHAT_ID)));
                    chatMessage.setMessage(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MESSAGE)));
                    chatMessage.setMediaLink(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MEDIA_LINK)));
                    chatMessage.setMsgType(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MESSAGE_TYPE)));
                    chatMessage.setCreaterId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.SENDER_ID)));
                    chatMessage.setReceiverId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.FRIEND_ID)));
                    chatMessage.setIsPrivate(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.ISPRIVATE_MSG)));
                    chatMessage.setServerChatId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID)));
//                    chatMessage.setChatPageId(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.CHAT_PAGE_ID)));
                    chatMessage.setIsGroup(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.IS_GROUP)));
                    chatMessage.setPrivateMessageTime(res.getInt(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.PRIVATE_MSG_TIME)));
                    chatMessage.setThumbNailImage(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.THUMB_NAIL)));
                    chatMessage.setDownLoadStatus(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.DOWNLOAD_STATUS)));
                    chatMessage.setUpLoadStatus(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS)));
//                    String view = res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.ISVIEWED));
                    String messageStatus = res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS));
                    chatMessage.setLocalPath(res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.LOCATPATH)));
                    chatMessage.setSendStatus(messageStatus);
                    String date = res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.DATE));
                    String time = res.getString(res.getColumnIndex(DatabaseContracts.ChatContractDataBase.TIME));
                    chatMessage.setTime(time);

//                    if (date == null || date == "nul" || date == "") {
//                        chatMessage.setDate("00:00");
//                    } else {
//                        chatMessage.setDate(setTime(date));
//                        chatMessage.setTime(date);
//                    }

                    String ispr = chatMessage.getIsPrivate();
                    if (ispr == null) {
                        ispr = DatabaseContracts.IS_FALSE;
                        chatMessage.setIsPrivate(ispr);
                    } else {
                        chatMessage.setIsPrivate(ispr);
                    }


                    chatMessage.setMsgViewedStatus("0");

                    chatMessageList.add(chatMessage);
                } while (res.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();

        }

        return chatMessageList;
    }

    public long insertChatData(ContentValues contentValues) {

        database = getWritableDatabase();
        long insertedId = database.insert(TBL_MESSAGE_CHAT, null, contentValues);
        return insertedId;
    }

    public long updateSendStatus(ContentValues values, String chatId) {
        long id = 0;
        try {
            database = getWritableDatabase();
            id = database.update(TBL_MESSAGE_CHAT, values,
                    CHAT_ID + " = '" + chatId + "'",
                    null);

            Log.d("update_chat", "id " + " >" + id);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }

        return id;
    }

    public void lockFriend(String friendId) {

        try {
            ContentValues values = new ContentValues();
            database = getReadableDatabase();

            String sql = "SELECT  * FROM " + TBL_HIDE_LOCK + " WHERE " + LOCK_FRIEND_ID + " = '" + friendId + "' AND " + HIDE_STATUS + " = 1";
            Cursor cursor = database.rawQuery(sql, null);
            database = getWritableDatabase();
            if (cursor.moveToFirst()) {
                do {

                    values.put(LOCK_STATUS, 1);
                    long id = database.update(TBL_HIDE_LOCK, values,
                            FRIEND_ID + " = " + friendId,
                            null);

                    Log.d("lock_friend_updated", "id " + " >" + id);
                } while (cursor.moveToNext());
            } else {

                values.put(LOCK_FRIEND_ID, friendId);
                values.put(LOCK_STATUS, 1);
                long id = database.insert(TBL_HIDE_LOCK, null, values);

                Log.d("lock_friend_inserted", "id " + " >" + id);
            }




        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public void unLockFriend(String friendId) {

        try {
            ContentValues values = new ContentValues();
            database = getReadableDatabase();

            String sql = "SELECT  * FROM " + TBL_HIDE_LOCK + " WHERE " + LOCK_FRIEND_ID + " = '" + friendId + "' AND " + HIDE_STATUS + " = 1";
            Cursor cursor = database.rawQuery(sql, null);
            database = getWritableDatabase();
            if (cursor.moveToFirst()) {
                do {

                    values.put(LOCK_STATUS, 0);
                    long id = database.update(TBL_HIDE_LOCK, values,
                            FRIEND_ID + " = " + friendId,
                            null);

                    Log.d("lock_friend_updated", "id " + " >" + id);
                } while (cursor.moveToNext());
            } else {

                long id = database.delete(TBL_HIDE_LOCK, LOCK_FRIEND_ID + " =? ", new String[]{friendId});

                Log.d("unlock_friend", " " + " >" + id);
            }




        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public void hideFriend(String friendId) {

        try {
            ContentValues values = new ContentValues();
            database = getReadableDatabase();
            String sql = "SELECT  * FROM " + TBL_HIDE_LOCK + " WHERE " + LOCK_FRIEND_ID + " = '" + friendId + "'";
            Cursor cursor = database.rawQuery(sql, null);
            database = getWritableDatabase();
            if (cursor.moveToFirst()) {
                do {
                    values.put(HIDE_STATUS, 1);
                    long id = database.update(TBL_HIDE_LOCK, values,
                            FRIEND_ID + " = " + friendId,
                            null);
                    Log.d("hide_friend_updated", "id " + " >" + id);
                } while (cursor.moveToNext());
            } else {
                values.put(LOCK_FRIEND_ID, friendId);
                values.put(HIDE_STATUS, 1);
                long id = database.insert(TBL_HIDE_LOCK, null, values);
                Log.d("hide_friend_inserted", "id " + " >" + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public void unHideFriend(String friendId) {

        try {
            ContentValues values = new ContentValues();
            database = getReadableDatabase();

            String sql = "SELECT  * FROM " + TBL_HIDE_LOCK + " WHERE " + LOCK_FRIEND_ID + " = '" + friendId + "'";
            Cursor cursor = database.rawQuery(sql, null);
            database = getWritableDatabase();
            if (cursor.moveToFirst()) {
                do {
                    values.put(HIDE_STATUS, 0);
                    long id = database.update(TBL_HIDE_LOCK, values,
                            FRIEND_ID + " = " + friendId,
                            null);
                    Log.d("hide_friend_updated", "id " + " >" + id);
                } while (cursor.moveToNext());
            } else {
                long id = database.delete(TBL_HIDE_LOCK, LOCK_FRIEND_ID + " =? ", new String[]{friendId});
                Log.d("un_hide_friend", " " + " >" + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public long updateDeliveredStatus(ContentValues values, String serverChatId) {
        long id = 0;
        try {
            database = getWritableDatabase();
            id = database.update(TBL_MESSAGE_CHAT, values,
                    SERVER_CHAT_ID + " = '" + serverChatId + "'",
                    null);

            Log.d("update_chat", "id " + " >" + id);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }

        return id;
    }

    public void updateRecentTable(String senderId) {

        try {
            database = getWritableDatabase();
            ContentValues values = new ContentValues();
            Cursor cursor = database.query(TBL_RECENT, new String[]{CL_UNREAD_COUNT},
                    LOCK_FRIEND_ID + " =? ", new String[]{senderId}, null, null, null);

            long recentId = 0;
            if (cursor.moveToFirst()) {
                int unread = cursor.getInt(0);
                values.put(CL_UNREAD_COUNT, (++unread));

                recentId = database.update(TBL_RECENT, values, LOCK_FRIEND_ID + " =? ", new String[]{senderId});
            } else {
                values.put(LOCK_FRIEND_ID, senderId);
                values.put(CL_UNREAD_COUNT, 1);
                recentId = database.insert(TBL_RECENT, null, values);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }


    }

    public void resetUnreadCount(String friendId) {

        try {
            database = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CL_UNREAD_COUNT, 0);

            database.update(TBL_RECENT, values, LOCK_FRIEND_ID + " =? ", new String[]{friendId});


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public void updateChatValue(String key, String value, String serverChatId) {

        try {
            database = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(key, value);

            long id = database.update(TBL_MESSAGE_CHAT, values, SERVER_CHAT_ID + " =? ", new String[]{serverChatId});

            Log.d("updateChatValue", "< " + key + "|" + id + " >");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

//    public void addContact(ContactVo vo) {
//
//        try {
//
//            database = getWritableDatabase();
//
//            Cursor cursor = database.query(TBL_CONTACT, new String[]{CL_CONTACT_NAME},
//                    CL_CONTACT_NUMBER + " =? ",
//                    new String[]{vo.getPhoneNumber()},
//                    null, null, null);
//
//            if (cursor.moveToFirst()) {
//                Logger.d("duplicate_contact", "with name " + " >" + cursor.getString(0));
//                return;
//            }
//
//            ContentValues values = new ContentValues();
//            values.put(CL_CONTACT_ID, vo.getContactId());
//            values.put(CL_CONTACT_NAME, vo.getContactName());
//            values.put(CL_CONTACT_IMAGE, vo.getContactImage());
//            values.put(CL_CONTACT_NUMBER, vo.getPhoneNumber());
//            values.put(CL_CONTACT_FILED_TYPE, vo.getFiledType());
//            values.put(CL_FAVORITE, vo.getFavorite());
//            values.put(CL_CREATE_DATE, vo.currentTimeMillis());
//            values.put(CL_SYNC, ContactManager.STATUS_ADD);
//            values.put(CL_PROCESS, 1);
//            values.put(CL_DRAW_COLOR, ColorGenerator.MATERIAL.getRandomColor());
//
//            long id = database.insert(TBL_CONTACT, null, values);
//
//            Logger.d("new_contact_id", "id " + " >" + id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//        }
//
//    }
//
//
//    public void updateContactStatus(ArrayList<String> contactList, boolean favorite) {
//
//        if (contactList == null) {
//            return;
//        }
//        try {
//            database = getWritableDatabase();
//
//            //delete contact
//            database.delete(TBL_CONTACT, CL_SYNC + " =? ",
//                    new String[]{String.valueOf(ContactManager.STATUS_DELETE)});
//
//            ContentValues values = new ContentValues();
//
//            //reset all
//            values.put(CL_P_CHAT, 0);
//            values.put(CL_SYNC, ContactManager.STATUS_SYNCED);
//            database.update(TBL_CONTACT, values, null, null);
//
//
//            if (contactList.size() == 0) {
//                return;
//            }
//
//
//            String data = "";
//            for (String d : contactList) {
//                data = data + "'" + d + "',";
//            }
//            data = data.substring(0, data.length() - 1);
//            //update all
//            String query;
//            if (favorite) {
//                query = "UPDATE contact SET pchat=1, favorite=1 WHERE co_number IN (" + data + ")";
//            } else {
//                query = "UPDATE contact SET pchat=1 WHERE co_number IN (" + data + ")";
//            }
//            database.execSQL(query);
//            Logger.d("pchat_count", ">" + query);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public void updateProcess(int status) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_PROCESS, status);
//
//            long id = database.update(TBL_CONTACT, values, null, null);
//
//            Logger.d("updateProcess", " " + " >" + id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public void updateProcess(int status, String conId) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_PROCESS, status);
//
//            long id = database.update(TBL_CONTACT, values, CL_CONTACT_ID + " =?", new String[]{conId});
//
//            Logger.d("updateProcess", " " + " >" + id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public void updateFileServerPath(int status) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_PROCESS, status);
//
//            long id = database.update(TBL_CONTACT, values, null, null);
//
//            Logger.d("updateProcess", " " + " >" + id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public void updateSyncForDeleteContact() {
//        //set flag fro not found
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_SYNC, ContactManager.STATUS_DELETE);
//            values.put(CL_PROCESS, 1);
//
//            long id = database.update(TBL_CONTACT, values, CL_PROCESS + "=? ", new String[]{"0"});
//
//            Logger.d("updateSyncForDeleteContact", " " + " >" + id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public void deleteContact(String conId) {
//
//        try {
//            database = getWritableDatabase();
//
//            long id = database.delete(TBL_CONTACT, CL_CONTACT_ID + " =? ", new String[]{conId});
//
//            Logger.d("deleteContact", " " + " >" + id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public void updateSyncForDeleteContact(String conId) {
//        //set flag fro not found
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_SYNC, ContactManager.STATUS_DELETE);
//            values.put(CL_PROCESS, 1);
//
//            long id = database.update(TBL_CONTACT, values,
//                    CL_PROCESS + "=? AND " + CL_CONTACT_ID + " =? ",
//                    new String[]{"0", conId});
//
//            Logger.d("updateSyncForDeleteContact", " " + " >" + id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public int markAsFavorite(String contactId, boolean isFav) {
//        int up = isFav ? 1 : 0;
//        try {
//            database = getWritableDatabase();
//
//
//            ContentValues values = new ContentValues();
//            values.put(CL_FAVORITE, up);
//
//            long id = database.update(TBL_CONTACT, values, CL_CONTACT_ID + "=? ", new String[]{contactId});
//
//            Logger.d("markAsFavorite", " " + " >" + id);
//
//            return up;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//        return up;
//    }
//
//    public int markAsInvited(String contactId) {
//        try {
//            database = getWritableDatabase();
//
//
//            ContentValues values = new ContentValues();
//            values.put(CL_INVITE, 1);
//
//            long id = database.update(TBL_CONTACT, values, CL_CONTACT_ID + "=? ", new String[]{contactId});
//
//            Logger.d("markAsInvited", " " + " >" + id);
//
//            return 1;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//        return 0;
//    }
//
//    public void markAsBlock(String phoneNum, boolean isBlock) {
//
//        try {
//            database = getWritableDatabase();
//
//            if (isBlock) {
//                ContentValues values = new ContentValues();
//                values.put(CL_BLOCK_CONTACT_NUMBER, phoneNum);
//                long id = database.insert(TBL_BLOCK, null, values);
//            } else {
//                long id = database.delete(TBL_BLOCK, CL_CONTACT_NUMBER + "=? ", new String[]{phoneNum});
//            }
//
//            Logger.d("markAsBlock", " " + " >" + isBlock);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public ArrayList<String> updateBlockList(List<PrivacyItem> bList) {
//
//        try {
//            database = getWritableDatabase();
//
//            database.delete(TBL_BLOCK, null, null);
//
//            if (bList != null) {
//                for (PrivacyItem item : bList) {
//                    ContentValues values = new ContentValues();
//                    values.put(CL_BLOCK_CONTACT_NUMBER, JabberId.getUser(item.getValue()));
//                    database.insert(TBL_BLOCK, null, values);
//                }
//
//                Logger.d("updateBlockList", " " + " >");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//        return getBlockList();
//    }
//
//    public ArrayList<String> getBlockList() {
//        ArrayList<String> contactVos = new ArrayList<>();
//
//        try {
//            database = getReadableDatabase();
//
//            Cursor cursor = database.query(TBL_BLOCK, null, null, null, null, null, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    contactVos.add(cursor.getString(0));
//                } while (cursor.moveToNext());
//
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//
//        }
//
//        return contactVos;
//    }
//
//    public String getContactName(Context context, String phoneNum) {
//
//
//        try {
//            database = getReadableDatabase();
//
//            Cursor cursor = database.query(TBL_CONTACT, new String[]{CL_CONTACT_NAME},
//                    CL_CONTACT_NUMBER + " =? ", new String[]{phoneNum}, null, null, null);
//
//            if (cursor.moveToFirst()) {
//                return cursor.getString(0);
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//
//        }
//
//
//        return ContactVo.getDisplayPhoneFormat(context, phoneNum);
//    }
//
//    public int getUnreadCount(Context context, String buddy) {
//
//
//        try {
//            database = getReadableDatabase();
//
//            Cursor cursor = database.query(TBL_RECENT, new String[]{CL_UNREAD_COUNT},
//                    CL_BUDDY + " =? ", new String[]{buddy}, null, null, null);
//
//            if (cursor.moveToFirst()) {
//                return cursor.getInt(0);
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//
//        }
//
//
//        return 0;
//    }
//
//    public int getTotalUnreadCount() {
//        try {
//            database = getReadableDatabase();
//
//            String sql = "SELECT SUM(" + CL_UNREAD_COUNT + ") FROM " + TBL_RECENT;
//            Cursor cursor = database.rawQuery(sql, null);
//
//            if (cursor.moveToFirst()) {
//                return cursor.getInt(0);
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//
//        }
//
//
//        return 0;
//    }
//
//    public String getGroupName(Context context, String id) {
//
//        try {
//            database = getReadableDatabase();
//
//            Cursor cursor = database.query(TBL_GROUP, new String[]{CL_GROUP_SUBJECT},
//                    CL_GROUP_ID + " =? ", new String[]{id}, null, null, null);
//
//            if (cursor.moveToFirst()) {
//                return cursor.getString(0);
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//
//        }
//
//
//        return "Group Chat";
//    }
//
//    public long removeAllContact() {
//
//        try {
//            database = getWritableDatabase();
//            return database.delete(TBL_CONTACT, null, null);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//        }
//
//        return 0;
//    }
//
//    public long removeContactById(String id) {
//
//        try {
//            database = getWritableDatabase();
//            return database.delete(TBL_CONTACT, CL_CONTACT_ID + "=? ", new String[]{id});
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//        return 0;
//    }
//
//
//    public void updateContact(ContactVo vo) {
//
//        int addNew = 0;
//        try {
//            database = getWritableDatabase();
//
//            Cursor cursor = database.query(TBL_CONTACT, new String[]{CL_CONTACT_ID, CL_CONTACT_NAME, CL_PROCESS, CL_SYNC},
//                    CL_CONTACT_NUMBER + " =? AND " + CL_SYNC + "!=? ",
//                    new String[]{vo.getPhoneNumber(), String.valueOf(ContactManager.STATUS_DELETE)},
//                    null, null, null);
//
//            if (cursor.moveToFirst()) {
//                String cId = cursor.getString(0);
//                String name = cursor.getString(1);
//                int process = cursor.getInt(2);
//                int sync = cursor.getInt(3);
//
//                if (!cId.equals(vo.getContactId()) && process == 1) {
//                    return;
//                }
//
//                ContentValues values = new ContentValues();
//                values.put(CL_CONTACT_IMAGE, vo.getContactImage());
//                values.put(CL_CONTACT_FILED_TYPE, vo.getFiledType());
//                values.put(CL_MODIFIED_DATE, vo.currentTimeMillis());
//                values.put(CL_CONTACT_ID, vo.getContactId());
//
//                if (!name.equals(vo.getContactName())) {
//                    values.put(CL_CONTACT_NAME, vo.getContactName());
//                    values.put(CL_SYNC, sync != ContactManager.STATUS_ADD ? ContactManager.STATUS_UPDATE : sync);
//                }
//                values.put(CL_PROCESS, 1);
//
//                long id = database.update(TBL_CONTACT, values,
//                        CL_CONTACT_NUMBER + " =? AND " + CL_SYNC + "!=? ",
//                        new String[]{vo.getPhoneNumber(), String.valueOf(ContactManager.STATUS_DELETE)});
//
//                Logger.d("update_contact_id", "id " + " >" + id + "  - " + vo.getContactName());
//            } else {
//                addNew = 1;
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//        if (addNew == 1) {
//            addContact(vo);
//        }
//    }
//
//    public void printContact() {
//
//
//        try {
//            database = getWritableDatabase();
//
//            Cursor cursor = database.query(TBL_CONTACT, null,
//                    null,
//                    null,
//                    null, null, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    String id = cursor.getString(cursor.getColumnIndex(CL_CONTACT_ID));
//                    String name = cursor.getString(cursor.getColumnIndex(CL_CONTACT_NAME));
//                    String phone = cursor.getString(cursor.getColumnIndex(CL_CONTACT_NUMBER));
//                    int sync = cursor.getInt(cursor.getColumnIndex(CL_SYNC));
//                    int p = cursor.getInt(cursor.getColumnIndex(CL_PROCESS));
//
//                    Logger.d("contact", "s-" + sync + " p-" + p + "  id - " + id + "  " + name + " " + phone);
//
//                } while (cursor.moveToNext());
//
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public HashMap<String, String> getContactsForSync(int limit, int offset) {
//        HashMap<String, String> map = new HashMap<>();
//
//        try {
//            database = getWritableDatabase();
//
//            String sql = "SELECT %s , %s FROM %s LIMIT %d OFFSET %d";
//            Object[] objects = {CL_CONTACT_NAME, CL_CONTACT_NUMBER, TBL_CONTACT, limit, offset};
//            Cursor cursor = database.rawQuery(String.format(sql, objects), null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    //<name,phone>
//                    map.put(cursor.getString(1), cursor.getString(0));
//
//                } while (cursor.moveToNext());
//
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//        return map;
//    }
//
//    public int countForContactUpdateSync() {
//        Cursor cursor = null;
//        try {
//            database = getReadableDatabase();
//
//            cursor = database.query(TBL_CONTACT, new String[]{CL_CONTACT_ID},
//                    CL_SYNC + "!=?",
//                    new String[]{String.valueOf(ContactManager.STATUS_SYNCED)},
//                    null, null, null);
//
//            return cursor.getCount();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            database.close();
//        }
//
//        return 0;
//    }
//
//    public ArrayList<ContactVo> getContactsForUpdateSync(int limit, int offset) {
//        ArrayList<ContactVo> contactVos = new ArrayList<>();
//
//        try {
//            database = getWritableDatabase();
//
//            String sql = "SELECT %s , %s , %s FROM %s WHERE %s != %d LIMIT %d OFFSET %d";
//            Object[] objects = {CL_CONTACT_NAME, CL_CONTACT_NUMBER, CL_SYNC, TBL_CONTACT,
//                    CL_SYNC, ContactManager.STATUS_SYNCED, limit, offset};
//
//            String query = String.format(sql, objects);
//            Cursor cursor = database.rawQuery(query, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    //<name,phone, sync>
//                    ContactVo vo = new ContactVo(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
//                    contactVos.add(vo);
//
//                } while (cursor.moveToNext());
//
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//
//        }
//
//        return contactVos;
//    }
//
//


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
                lastChatTime = mContext.getResources().getString(R.string.yestarday);
            } else {
                lastChatTime = date;
            }

            return lastChatTime;
        } catch (Exception e) {
            e.printStackTrace();
            return lastChatTime;
        }

    }
//
//
////    public ArrayList<ContactVo> getPeople(boolean isFavorite) {
////        ArrayList<ContactVo> contactVos = new ArrayList<>();
////
////        try {
////            database = getWritableDatabase();
////
////            String sql = "SELECT *, group_concat(%s,':')as %s, group_concat(%s,':')as %s, group_concat(%s,':')as %s " +
////                    "FROM %s WHERE %s != %d %s group by %s ORDER BY LOWER(%s) ASC ";
////            //LIMIT %d OFFSET %d
////            String fav = "";
////            if (isFavorite) {
////                fav = " AND " + CL_FAVORITE + " =1 AND " + CL_P_CHAT + " =1";
////            }
////            Object[] objects = {CL_CONTACT_NUMBER, CL_CONTACT_NUMBER, CL_CONTACT_FILED_TYPE,
////                    CL_CONTACT_FILED_TYPE, CL_P_CHAT, CL_P_CHAT, TBL_CONTACT, CL_SYNC,
////                    ContactManager.STATUS_DELETE, fav, CL_CONTACT_NAME, CL_CONTACT_NAME};
////
////            String query = String.format(sql, objects);
////            Cursor cursor = database.rawQuery(query, null);
////
////            if (cursor.moveToFirst()) {
////                do {
////                    try {
////                        contactVos.add(wrapContactObj(cursor, true));
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////
////                } while (cursor.moveToNext());
////
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        } finally {
////            if (database != null)
////                database.close();
////
////        }
////
////        return contactVos;
////    }
//
//    public ArrayList<ContactVo> getPeople2(boolean isFavorite) {
//        ArrayList<ContactVo> contactVos = new ArrayList<>();
//        LinkedHashMap<String, Integer> uniMap = new LinkedHashMap<>();
//
//        try {
//            database = getWritableDatabase();
//
//            String selection = CL_SYNC + " != ? ";
//            String selectionArg[] = {String.valueOf(ContactManager.STATUS_DELETE)};
//
//            if (isFavorite) {
//                selection = selection + " AND " + CL_FAVORITE + " =? ";
//                selectionArg = new String[]{String.valueOf(ContactManager.STATUS_DELETE), "1"};
//            }
//
//
//            Cursor cursor = database.query(TBL_CONTACT, null,
//                    selection,
//                    selectionArg,
//                    null, null, CL_CONTACT_NAME + " COLLATE NOCASE ASC");
//
//            if (cursor.moveToFirst()) {
//                do {
//                    wrapContactObj2(cursor, uniMap, contactVos, isFavorite);
//                } while (cursor.moveToNext());
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//        return contactVos;
//    }
//
//    private void wrapContactObj2(Cursor cursor, LinkedHashMap<String, Integer> map,
//                                 ArrayList<ContactVo> contactVos, boolean isFav) throws Exception {
//
//
//        String cId = cursor.getString(cursor.getColumnIndex(CL_CONTACT_ID));
//        int pchat = cursor.getInt(cursor.getColumnIndex(CL_P_CHAT));
//
//        int replaceIndex = -1;
//        if (!isFav && map.containsKey(cId)) {
//            if (pchat == 0) {
//                return;
//            }
//
//            replaceIndex = map.get(cId);
//
//        }
//
//
//        ContactVo vo = new ContactVo();
//        vo.setpChat(pchat);
//        vo.setContactId(cId);
//
//        vo.setPhoneNumber(cursor.getString(cursor.getColumnIndex(CL_CONTACT_NUMBER)));
//        vo.setFiledType(cursor.getString(cursor.getColumnIndex(CL_CONTACT_FILED_TYPE)));
//        vo.setContactName(cursor.getString(cursor.getColumnIndex(CL_CONTACT_NAME)));
//        vo.setContactImage(cursor.getString(cursor.getColumnIndex(CL_CONTACT_IMAGE)));
//        vo.setFavorite(cursor.getInt(cursor.getColumnIndex(CL_FAVORITE)));
//        vo.setInvite(cursor.getInt(cursor.getColumnIndex(CL_INVITE)));
//
//        vo.setPvLastSeen(cursor.getInt(cursor.getColumnIndex(CL_PV_LAST_SEEN)));
//        vo.setPvOnline(cursor.getInt(cursor.getColumnIndex(CL_PV_ONLINE)));
//        vo.setPvProImage(cursor.getInt(cursor.getColumnIndex(CL_PV_PRO_IMAGE)));
//        vo.setPvStatus(cursor.getInt(cursor.getColumnIndex(CL_PV_STATUS)));
//
//        vo.setVcName(cursor.getString(cursor.getColumnIndex(CL_VC_NAME)));
//        vo.setVcImageUpdate(cursor.getString(cursor.getColumnIndex(CL_VC_PRO_IMAGE_UPDATE)));
//        vo.setVcImageDownload(cursor.getString(cursor.getColumnIndex(CL_VC_PRO_IMAGE_DOWNLOAD)));
//        vo.setVcStatus(cursor.getString(cursor.getColumnIndex(CL_VC_STATUS)));
//        vo.setVcStatusDate(cursor.getString(cursor.getColumnIndex(CL_VC_STATUS_DATE)));
//
//        vo.setDrawColor(cursor.getInt(cursor.getColumnIndex(CL_DRAW_COLOR)));
//
//        if (isFav) {
//            contactVos.add(vo);
//            return;
//        }
//
//        if (replaceIndex >= 0) {
//            contactVos.remove(replaceIndex);
//            contactVos.add(replaceIndex, vo);
//        } else {
//            contactVos.add(vo);
//            map.put(cId, pchat == 1 ? -1 : contactVos.size() - 1);
//        }
//    }
//
//    public ArrayList<ContactVo> getChatPeople() {
//        ArrayList<ContactVo> contactVos = new ArrayList<>();
//
//        try {
//            database = getWritableDatabase();
//
//            Cursor cursor = database.query(TBL_CONTACT, null, CL_P_CHAT + " =? AND " + CL_SYNC + " != ?"
//                    , new String[]{"1", String.valueOf(ContactManager.STATUS_DELETE)}, null, null, CL_CONTACT_NAME + " ASC");
//
//            if (cursor.moveToFirst()) {
//                do {
//                    try {
//                        contactVos.add(wrapContactObj(cursor));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } while (cursor.moveToNext());
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//        return contactVos;
//    }
//
//    public ContactVo getContactForChat(String contactId, String phone) {
//
//        try {
//            database = getWritableDatabase();
//
//            Cursor cursor = database.query(TBL_CONTACT, null,
//                    CL_CONTACT_NUMBER + " =?  AND " + CL_SYNC + " != ?"
//                    , new String[]{phone, String.valueOf(ContactManager.STATUS_DELETE)}, null, null, null);
//
//            if (cursor.moveToFirst()) {
//
//                try {
//                    return wrapContactObj(cursor);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//        return new ContactVo(phone);
//    }
//
//    public ArrayList<ContactVo> getContacts(String contactId) {
//        ArrayList<ContactVo> contactVos = new ArrayList<>();
//        try {
//            database = getWritableDatabase();
//
//            Cursor cursor = database.query(TBL_CONTACT, null, CL_CONTACT_ID + " =? AND " + CL_SYNC + " != ?"
//                    , new String[]{contactId, String.valueOf(ContactManager.STATUS_DELETE)}, null, null, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    try {
//                        ContactVo vo = wrapContactObj(cursor);
//                        if (vo.isPChat())
//                            contactVos.add(0, vo);
//                        else
//                            contactVos.add(vo);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } while (cursor.moveToNext());
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//        return contactVos;
//    }
//
//    public ArrayList<String> getPeopleIds() {
//        ArrayList<String> contactVos = new ArrayList<>();
//
//        try {
//            database = getWritableDatabase();
//
//            Cursor cursor = database.query(TBL_CONTACT, new String[]{CL_CONTACT_NUMBER},
//                    CL_P_CHAT + " =? AND " + CL_SYNC + " != ?",
//                    new String[]{"1", String.valueOf(ContactManager.STATUS_DELETE)}, null, null, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    try {
//                        contactVos.add(cursor.getString(0));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } while (cursor.moveToNext());
//
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//        return contactVos;
//    }
//
//    public void updateVCard(ContactVo vo) {
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_VC_NAME, vo.getVcName());
//            values.put(CL_VC_PRO_IMAGE_UPDATE, vo.getVcImageUpdate());
//            values.put(CL_VC_STATUS, vo.getVcStatus());
//            values.put(CL_VC_STATUS_DATE, vo.getVcStatusDate());
//
//            values.put(CL_PV_STATUS, vo.getPvStatus());
//            values.put(CL_PV_PRO_IMAGE, vo.getPvProImage());
//            values.put(CL_PV_ONLINE, vo.getPvOnline());
//            values.put(CL_PV_LAST_SEEN, vo.getPvLastSeen());
//
//            long id = database.update(TBL_CONTACT, values,
//                    CL_CONTACT_NUMBER + " =? ",
//                    new String[]{vo.getPhoneNumber()});
//
//            Logger.d("update_contact_vcard", "id " + " >" + id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//    }
//
//    public String getVCImageDownTime(String phone) {
//
//        try {
//            database = getReadableDatabase();
//
//            Cursor cursor = database.query(TBL_CONTACT, new String[]{CL_VC_PRO_IMAGE_DOWNLOAD}, CL_CONTACT_NUMBER + " =? "
//                    , new String[]{phone}, null, null, null);
//
//            if (cursor.moveToFirst()) {
//                return cursor.getString(0);
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//        return null;
//    }
//
//    public void updateVCImageDownTime(String phone, String time) {
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_VC_PRO_IMAGE_DOWNLOAD, time);
//
//            long id = database.update(TBL_CONTACT, values,
//                    CL_CONTACT_NUMBER + " =? ",
//                    new String[]{phone});
//
//            Logger.d("updateVCImageDownTime", "id " + " >" + id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//    }
//
//    private ContactVo wrapContactObj(Cursor cursor) throws Exception {
//        ContactVo vo = new ContactVo();
//
//
//        String phone = cursor.getString(cursor.getColumnIndex(CL_CONTACT_NUMBER));
//        int pchat = cursor.getInt(cursor.getColumnIndex(CL_P_CHAT));
//        String type = cursor.getString(cursor.getColumnIndex(CL_CONTACT_FILED_TYPE));
//
//        vo.setPhoneNumber(phone);
//        vo.setpChat(pchat);
//        vo.setFiledType(type);
//
//
//        vo.setContactId(cursor.getString(cursor.getColumnIndex(CL_CONTACT_ID)));
//        vo.setContactName(cursor.getString(cursor.getColumnIndex(CL_CONTACT_NAME)));
//        vo.setContactImage(cursor.getString(cursor.getColumnIndex(CL_CONTACT_IMAGE)));
//        vo.setFavorite(cursor.getInt(cursor.getColumnIndex(CL_FAVORITE)));
//        vo.setInvite(cursor.getInt(cursor.getColumnIndex(CL_INVITE)));
//
//        vo.setPvLastSeen(cursor.getInt(cursor.getColumnIndex(CL_PV_LAST_SEEN)));
//        vo.setPvOnline(cursor.getInt(cursor.getColumnIndex(CL_PV_ONLINE)));
//        vo.setPvProImage(cursor.getInt(cursor.getColumnIndex(CL_PV_PRO_IMAGE)));
//        vo.setPvStatus(cursor.getInt(cursor.getColumnIndex(CL_PV_STATUS)));
//
//        vo.setVcName(cursor.getString(cursor.getColumnIndex(CL_VC_NAME)));
//        vo.setVcImageUpdate(cursor.getString(cursor.getColumnIndex(CL_VC_PRO_IMAGE_UPDATE)));
//        vo.setVcImageDownload(cursor.getString(cursor.getColumnIndex(CL_VC_PRO_IMAGE_DOWNLOAD)));
//        vo.setVcStatus(cursor.getString(cursor.getColumnIndex(CL_VC_STATUS)));
//        vo.setVcStatusDate(cursor.getString(cursor.getColumnIndex(CL_VC_STATUS_DATE)));
//
//        vo.setDrawColor(cursor.getInt(cursor.getColumnIndex(CL_DRAW_COLOR)));
//
//        return vo;
//    }
//
//    public synchronized boolean addChat(ChatVo vo) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_PACKET_ID, vo.getPacketId());
//            values.put(CL_TIMESTAMP, vo.getTimestamp());
//            values.put(CL_BUDDY, vo.getBuddy());
//            values.put(CL_CHAT_TYPE, vo.getChatType());
//
//            Cursor cursor1 = database.query(TBL_CHAT, new String[]{CL_BUDDY},
//                    CL_PACKET_ID + " =? AND " + CL_BUDDY + " =? ", new String[]{vo.getPacketId(), vo.getBuddy()}, null, null, null);
//
//            if (cursor1.moveToFirst()) {
//                updateChat(vo, database);
//                return false;
//            }
//
//            values.put(CL_ARCHIVE, 0);
//
//            Cursor cursor = database.query(TBL_RECENT, new String[]{CL_UNREAD_COUNT},
//                    CL_BUDDY + " =? ", new String[]{vo.getBuddy()}, null, null, null);
//
//            long recentId = 0;
//            if (cursor.moveToFirst()) {
//                int unread = cursor.getInt(0);
//                values.put(CL_UNREAD_COUNT, vo.isUnread() ? (++unread) : unread);
//
//                recentId = database.update(TBL_RECENT, values, CL_BUDDY + " =? ", new String[]{vo.getBuddy()});
//                values.remove(CL_UNREAD_COUNT);
//            } else {
//                values.put(CL_UNREAD_COUNT, vo.isUnread() ? 1 : 0);
//                recentId = database.insert(TBL_RECENT, null, values);
//                values.remove(CL_UNREAD_COUNT);
//            }
//
//            values.remove(CL_ARCHIVE);
//
//            values.put(CL_FROM, vo.getFrom());
//            values.put(CL_MESSAGE, vo.getMessage());
//            values.put(CL_PACKET, vo.getPacket());
//            values.put(CL_MESSAGE_TYPE, vo.getMsgType());
//            values.put(CL_TIME_EXPIRE, vo.getTimeExpire());
//            values.put(CL_TIME_SENT, vo.getTimeSent());
//            values.put(CL_TIME_DELIVERED, vo.getTimeDelivered());
//            values.put(CL_TIME_READ, vo.getTimeRead());
//            values.put(CL_TIME_SCHEDULE, vo.getTimeSchedule());
//            values.put(CL_FILE_LOCALE, vo.getFileLocale());
//            values.put(CL_FILE_THUMB, vo.getFileThumb());
//            values.put(CL_FILE_SERVER, vo.getFileServer());
//            values.put(CL_FILE_PROGRESS, vo.getFileProgress());
//            values.put(CL_SECURE, vo.isSecure() ? 1 : 0);
//
//            long id = database.insert(TBL_CHAT, null, values);
//
//            Logger.d("recentId|newChatId", "< " + recentId + "|" + id + " >");
//            Logger.d("CL_TIME_EXPIRE", "< " + "|" + vo.getTimeExpire() + " >");
//
//            if (!vo.isFromNotify() &&
//                    ChatVo.Util.TYPE_GROUP.equals(vo.getChatType())) {
//                updateMessageTime(database, vo.getBuddy());
//            }
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//        return false;
//    }
//
//    private void updateChat(ChatVo vo, SQLiteDatabase database) {
//        ContentValues values = new ContentValues();
//        // values.put(CL_TIMESTAMP, vo.getTimestamp());
//        values.put(CL_MESSAGE, vo.getMessage());
//        values.put(CL_PACKET, vo.getPacket());
//        values.put(CL_FILE_THUMB, vo.getFileThumb());
//        values.put(CL_TIME_SENT, vo.getTimeSent());
//
//        long id = database.update(TBL_CHAT, values, CL_PACKET_ID + " =?", new String[]{vo.getPacketId()});
//
//        Logger.d("updateChat|updateChat", "< -" + "|" + id + " >");
//    }
//
//    public void deleteMessage(String packet_id) {
//        try {
//            database = getWritableDatabase();
//
//            long clearId = database.delete(TBL_CHAT,
//                    CL_PACKET_ID + " =? ",
//                    new String[]{packet_id});
//
//            Logger.d("deleteMessage", "> " + clearId);
//
//            Cursor cursor = database.query(TBL_RECENT, new String[]{CL_BUDDY},
//                    CL_PACKET_ID + " =? ", new String[]{packet_id}, null, null, null);
//
//            if (cursor.moveToFirst()) {
//                String buddy = cursor.getString(0);
//                ContentValues values = new ContentValues();
//                cursor = database.query(TBL_CHAT, new String[]{CL_PACKET_ID, CL_TIMESTAMP, CL_CHAT_TYPE},
//                        CL_BUDDY + " =? ", new String[]{buddy}, null, null, CL_TIMESTAMP + " DESC", "1");
//
//                if (cursor.moveToFirst()) {
//                    values.put(CL_PACKET_ID, cursor.getString(0));
//                    values.put(CL_TIMESTAMP, cursor.getString(1));
//                    values.put(CL_CHAT_TYPE, cursor.getString(2));
//
//                    long id = database.update(TBL_RECENT, values, CL_BUDDY + " =?", new String[]{buddy});
//
//                    Logger.d("deleteMessage", "newMsg> " + id);
//                }
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//    }
//

//
//    public void updateDeliveryTime(String packetId, String value) {
//
//        try {
//            database = getWritableDatabase();
//
//            Cursor cursor = database.query(TBL_CHAT, new String[]{CL_TIME_DELIVERED},
//                    CL_PACKET_ID + " =? ", new String[]{packetId},
//                    null, null, null);
//
//            if (cursor.moveToFirst()) {
//                if (TextUtils.isEmpty(cursor.getString(0))) {
//                    ContentValues values = new ContentValues();
//                    values.put(CL_TIME_DELIVERED, value);
//
//                    long id = database.update(TBL_CHAT, values, CL_PACKET_ID + " =? ", new String[]{packetId});
//
//                    Logger.d("updateChatValue", "< " + "|" + id + " >");
//                }
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//    }
//
//    public void clearChat(String buddy) {
//        try {
//            database = getWritableDatabase();
//
//            long clearId = database.delete(TBL_CHAT,
//                    CL_BUDDY + " =? ",
//                    new String[]{buddy});
//
//            Logger.d("clearChatId", "> " + clearId);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//    }
//
//
//    public void clearAllChat() {
//        try {
//            database = getWritableDatabase();
//
//            long clearId = database.delete(TBL_CHAT,
//                    null,
//                    null);
//
//            Logger.d("clearAllChat", "> " + clearId);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//    }
//
//    public void deleteChat(String buddy) {
//        deleteChat(buddy, false);
//    }
//
//    public void deleteChat(String buddy, boolean isGroup) {
//        try {
//            database = getWritableDatabase();
//
//            long deleteId;
//
//            if (isGroup) {
//                deleteId = database.delete(TBL_RECENT,
//                        CL_BUDDY + " =? AND " + CL_CHAT_TYPE + " =? ",
//                        new String[]{buddy, ChatVo.Util.TYPE_GROUP});
//            } else {
//                deleteId = database.delete(TBL_RECENT,
//                        CL_BUDDY + " =? AND " + CL_CHAT_TYPE + " =? ",
//                        new String[]{buddy, ChatVo.Util.TYPE_CHAT});
//            }
//
//            long clearId = database.delete(TBL_CHAT,
//                    CL_BUDDY + " =? ",
//                    new String[]{buddy});
//
//            Logger.d("clearId|deleteId", "< " + clearId + "|" + deleteId + " >");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//    }
//
//    public void deleteAllChat() {
//        try {
//            database = getWritableDatabase();
//
//            long deleteId = database.delete(TBL_RECENT,
//                    CL_CHAT_TYPE + " =? ",
//                    new String[]{ChatVo.Util.TYPE_CHAT});
//
//            long clearId = database.delete(TBL_CHAT,
//                    null, null);
//
//            Logger.d("deleteAllChat> clearId|deleteId", "< " + clearId + "|" + deleteId + " >");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//    }
//
//    public boolean archiveChat(String buddy, boolean archive) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_ARCHIVE, archive ? 1 : 0);
//
//            long archiveId = database.update(TBL_RECENT, values,
//                    CL_BUDDY + " =? ",
//                    new String[]{buddy});
//
//            Logger.d("archiveId", "< " + archiveId + "|" + archive + " >");
//
//            return archive;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//        return !archive;
//    }
//
//    public void archiveAllChat() {
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_ARCHIVE, 1);
//
//            long archiveId = database.update(TBL_RECENT, values, null, null);
//
//            Logger.d("archiveAllChat", "< " + archiveId + "|" + " >");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public void unarchiveAllChat() {
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_ARCHIVE, 0);
//
//            long archiveId = database.update(TBL_RECENT, values, null, null);
//
//            Logger.d("unarchiveAllChat", "< " + archiveId + "|" + " >");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public boolean isArchive(String buddy) {
//
//        try {
//            database = getReadableDatabase();
//
//            Cursor cursor = database.query(TBL_RECENT, null,
//                    CL_BUDDY + " =? AND " + CL_ARCHIVE + " =?",
//                    new String[]{buddy, "1"}, null, null, null);
//
//            return cursor.getCount() == 1;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//        return false;
//    }
//
//    public ArrayList<String> getUnreadPackets(String buddy) {
//        ArrayList<String> list = new ArrayList<>();
//
//        Cursor cursor = null;
//        try {
//            database = getReadableDatabase();
//
//            String qu = String.format("select %s, %s from %s where %s='%s' AND %s='%s' AND %s=''",
//                    CL_PACKET_ID, CL_TIME_EXPIRE, TBL_CHAT, CL_BUDDY, buddy, CL_FROM, buddy, CL_TIME_READ);
//            cursor = database.rawQuery(qu, null);
//
//
//            if (cursor.moveToFirst()) {
//                do {
//                    list.add(cursor.getString(0));
//                } while (cursor.moveToNext());
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            database.close();
//        }
//        return list;
//    }
//
//    public ArrayList<ChatVo> getExpirePhotoChat() {
//        ArrayList<ChatVo> list = new ArrayList<>();
//
//        try {
//            database = getWritableDatabase();
//
//            String tillTime = String.valueOf(System.currentTimeMillis());
//            Cursor cursor = database.query(TBL_CHAT, new String[]{CL_PACKET_ID, CL_FILE_LOCALE},
//                    CL_MESSAGE_TYPE + " =? AND " + CL_SECURE + " =? AND "
//                            + CL_FILE_PROGRESS + " !=? AND " + CL_TIME_EXPIRE + " <=? ",
//                    new String[]{ChatVo.Util.MSG_TYPE_IMAGE, "1",
//                            String.valueOf(FileManager.FILE_EXPIRE), tillTime},
//                    null, null, null);
//
//            if (cursor.moveToFirst()) {
//                ChatVo chatVo = new ChatVo();
//                chatVo.setPacketId(cursor.getString(0));
//                chatVo.setFileLocale(cursor.getString(1));
//                list.add(chatVo);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//
//        return list;
//    }
//
//    public void checkExpireMessage() {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_MESSAGE, ChatVo.Util.MSG_AFTER_EXPIRE);
//            values.put(CL_PACKET, ChatVo.Util.MSG_AFTER_EXPIRE);
//
//            String tillTime = String.valueOf(System.currentTimeMillis());
//            long expire = database.update(TBL_CHAT, values,
//                    CL_TIME_EXPIRE + " <= ? ",
//                    new String[]{tillTime});
//
//            Logger.d("checkExpireMessage", "< " + expire + "| <= " + tillTime + " >");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//    }
//
//    public void expirePhotoChat(String packetId) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_MESSAGE, ChatVo.Util.MSG_AFTER_EXPIRE);
//            values.put(CL_PACKET, ChatVo.Util.MSG_AFTER_EXPIRE);
//            values.put(CL_FILE_SERVER, "");
//            values.put(CL_FILE_THUMB, "");
//            values.put(CL_FILE_LOCALE, "");
//            values.put(CL_FILE_PROGRESS, FileManager.FILE_EXPIRE);
//
//            long expire = database.update(TBL_CHAT, values,
//                    CL_PACKET_ID + " = ? ",
//                    new String[]{packetId});
//
//            Logger.d("expirePhotoChat", "< " + expire);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//
//    }
//
//    public ArrayList<ChatVo> getChatHistory(String buddy, String before) {
//        checkExpireMessage();
//
//        ArrayList<ChatVo> list = new ArrayList<>();
//
//        Cursor cursor = null;
//        try {
//            database = getReadableDatabase();
//
//            cursor = database.query(TBL_CHAT, null, CL_BUDDY + " =? AND " + CL_TIMESTAMP + " < ?",
//                    new String[]{buddy, before},
//                    null, null, CL_TIME_SENT + " DESC", String.valueOf(ChatVo.Util.PAGE_SIZE + 1));
//
//            if (cursor.moveToFirst()) {
//                do {
//                    list.add(0, wrapChatObj(cursor));
//                } while (cursor.moveToNext());
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            database.close();
//        }
//        return list;
//    }
//
//    private ChatVo wrapChatObj(Cursor cursor) {
//        ChatVo vo = new ChatVo();
//
//        vo.setPacketId(cursor.getString(cursor.getColumnIndex(CL_PACKET_ID)));
//        vo.setBuddy(cursor.getString(cursor.getColumnIndex(CL_BUDDY)));
//        vo.setFrom(cursor.getString(cursor.getColumnIndex(CL_FROM)));
//        vo.setChatType(cursor.getString(cursor.getColumnIndex(CL_CHAT_TYPE)));
//        vo.setPacket(cursor.getString(cursor.getColumnIndex(CL_PACKET)));
//        vo.setMessage(cursor.getString(cursor.getColumnIndex(CL_MESSAGE)));
//        vo.setMsgType(cursor.getString(cursor.getColumnIndex(CL_MESSAGE_TYPE)));
//
//        vo.setTimeSent(cursor.getString(cursor.getColumnIndex(CL_TIME_SENT)));
//        vo.setTimeDelivered(cursor.getString(cursor.getColumnIndex(CL_TIME_DELIVERED)));
//        vo.setTimeRead(cursor.getString(cursor.getColumnIndex(CL_TIME_READ)));
//        vo.setTimeSchedule(cursor.getString(cursor.getColumnIndex(CL_TIME_SCHEDULE)));
//        vo.setTimeExpire(cursor.getString(cursor.getColumnIndex(CL_TIME_EXPIRE)));
//        vo.setTimestamp(cursor.getString(cursor.getColumnIndex(CL_TIMESTAMP)));
//
//        vo.setFileLocale(cursor.getString(cursor.getColumnIndex(CL_FILE_LOCALE)));
//        vo.setFileThumb(cursor.getString(cursor.getColumnIndex(CL_FILE_THUMB)));
//        vo.setFileServer(cursor.getString(cursor.getColumnIndex(CL_FILE_SERVER)));
//
//        vo.setFileProgress(cursor.getInt(cursor.getColumnIndex(CL_FILE_PROGRESS)));
//        vo.setSecure(cursor.getInt(cursor.getColumnIndex(CL_SECURE)) == 1);
//
//        return vo;
//    }
//
//    public ArrayList<ChatVo> getRecentChat(boolean isArchive) {
//        ArrayList<ChatVo> reChatVos = new ArrayList<>();
//        SQLiteDatabase database = null;
//        try {
//            database = getReadableDatabase();
//
//            String sql = "SELECT r.*, c.%s, c.%s, c.%s , c.%s, c.%s, c.%s, c.%s " +
//                    "FROM %s r LEFT OUTER JOIN %s c ON r.%s= c.%s " +
//                    "WHERE %s=%d " +
//                    "ORDER BY r.%s DESC";
//
//            Object[] objects = {CL_MESSAGE, CL_MESSAGE_TYPE, CL_FROM, CL_TIME_SENT, CL_TIME_DELIVERED, CL_TIME_READ, CL_TIME_EXPIRE,
//                    TBL_RECENT, TBL_CHAT, CL_PACKET_ID, CL_PACKET_ID,
//                    CL_ARCHIVE, isArchive ? 1 : 0, //where
//                    CL_TIMESTAMP}; //sort
//
//            String query = String.format(sql, objects);
//            Cursor cursor = database.rawQuery(query, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    ChatVo chatVo = wrapRecentChatObj(cursor, database);
//                    if (chatVo.getContactVo() != null)
//                        reChatVos.add(chatVo);
//
//                } while (cursor.moveToNext());
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//        return reChatVos;
//    }
//
//    private ChatVo wrapRecentChatObj(Cursor cursor, SQLiteDatabase database) {
//        ChatVo vo = new ChatVo();
//
//        vo.setPacketId(cursor.getString(cursor.getColumnIndex(CL_PACKET_ID)));
//        vo.setBuddy(cursor.getString(cursor.getColumnIndex(CL_BUDDY)));
//        vo.setFrom(cursor.getString(cursor.getColumnIndex(CL_FROM)));
//        vo.setChatType(cursor.getString(cursor.getColumnIndex(CL_CHAT_TYPE)));
//        vo.setMessage(cursor.getString(cursor.getColumnIndex(CL_MESSAGE)));
//        vo.setMsgType(cursor.getString(cursor.getColumnIndex(CL_MESSAGE_TYPE)));
//
//        vo.setTimeSent(cursor.getString(cursor.getColumnIndex(CL_TIME_SENT)));
//        vo.setTimeDelivered(cursor.getString(cursor.getColumnIndex(CL_TIME_DELIVERED)));
//        vo.setTimeRead(cursor.getString(cursor.getColumnIndex(CL_TIME_READ)));
//        vo.setTimeExpire(cursor.getString(cursor.getColumnIndex(CL_TIME_EXPIRE)));
//        vo.setTimestamp(cursor.getString(cursor.getColumnIndex(CL_TIMESTAMP)));
//
//        vo.setUnreadCount(cursor.getInt(cursor.getColumnIndex(CL_UNREAD_COUNT)));
//
//        if (ChatVo.Util.TYPE_CHAT.equals(vo.getChatType())) {
//            //people
//            Cursor userCur = getReadableDatabase().query(TBL_CONTACT, null,
//                    CL_CONTACT_NUMBER + " =? AND " + CL_SYNC + " != ?"
//                    , new String[]{vo.getBuddy(), String.valueOf(ContactManager.STATUS_DELETE)}, null, null, null);
//
//            ContactVo user = null;
//            if (userCur.moveToFirst()) {
//                try {
//                    user = wrapContactObj(userCur);
//                    userCur.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            vo.setContactVo(user != null ? user : new ContactVo(vo.getBuddy()));
//
//        } else if (ChatVo.Util.TYPE_GROUP.equals(vo.getChatType())) {
//            //group
//            ContactVo user = ContactVo.createGroup(getChatGroup(vo.getBuddy(), false));
//            if (user.getChatGroupVo() != null)
//                vo.setContactVo(user);
//        }
//
//
//        return vo;
//    }
//
//    public int getArchiveCount() {
//
//        Cursor cursor = null;
//        try {
//            database = getReadableDatabase();
//
//            cursor = database.query(TBL_RECENT, new String[]{CL_BUDDY},
//                    CL_ARCHIVE + " =? ",
//                    new String[]{"1"},
//                    null, null, null);
//
//
//            return cursor.getCount();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            database.close();
//        }
//        return 0;
//    }
//
//    public String addGroup(ChatGroupVo vo, boolean self, boolean invite) {
//        boolean isNew = false;
//        try {
//            database = getWritableDatabase();
//
//            Cursor cursor = database.query(TBL_GROUP, null,
//                    CL_GROUP_ID + " =?", new String[]{vo.getId()},
//                    null, null, null);
//
//            long _id;
//
//
//            ContentValues values = new ContentValues();
//            values.put(CL_GROUP_ID, vo.getId());
//            values.put(CL_GROUP_AVATAR, vo.getAvatar());
//            values.put(CL_GROUP_SUBJECT, vo.getName());
//            values.put(CL_GROUP_CREATED_DATE, vo.getCreatedDate());
//            values.put(CL_DRAW_COLOR, ColorGenerator.MATERIAL.getRandomColor());
//
//            if (cursor.moveToFirst()) {
//                _id = database.update(TBL_GROUP, values, CL_GROUP_ID + " =?", new String[]{vo.getId()});
//            } else {
//                isNew = true;
//                _id = database.insert(TBL_GROUP, null, values);
//            }
//
//            Logger.d("addGroup", "id " + " >" + _id);
//
//            if (vo.getMembers() != null) {
//                database.delete(TBL_GROUP_MEMBER, CL_GROUP_ID + " =?", new String[]{vo.getId()});
//
//                for (ContactVo member : vo.getMembers()) {
//                    values = new ContentValues();
//                    values.put(CL_GROUP_ID, vo.getId());
//                    values.put(CL_GROUP_MEMBER, member.getPhoneNumber());
//                    values.put(CL_GROUP_MEMBER_AFFIL, member.isAdmin() ? 1 : 0);
//
//                    database.insert(TBL_GROUP_MEMBER, null, values);
//                }
//            }
//
//
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//
//        if (isNew || invite) {
//            //add tag message
//            ChatVo chatVo = ChatVo.create();
//
//            String msg = "Group Created";
//            try {
//                if (self || AccountVo.getAccount(mContext).getPhoneNo().equals(vo.getAdmin().getPhoneNumber())) {
//                    msg = "Group Created by you";
//                } else {
//                    msg = "You added by " + getContactName(mContext, vo.getAdmin().getPhoneNumber());
//                }
//            } catch (Exception e) {
//                // e.printStackTrace();
//            }
//            chatVo.setMessage(msg);
//            chatVo.setUnread(invite);
//            chatVo.setChatType(ChatVo.Util.TYPE_GROUP);
//            chatVo.setMsgType(ChatVo.Util.MSG_TYPE_TAG);
//            chatVo.setTimeSent(String.valueOf(System.currentTimeMillis()));
//            chatVo.setTimestamp(chatVo.getTimeSent());
//            chatVo.setBuddy(vo.getId());
//            chatVo.setFrom(vo.getAdmin() != null ? vo.getAdmin().getPhoneNumber() : "");
//
//            addChat(chatVo);
//
//            return msg;
//        }
//
//        return null;
//    }
//
//    public ChatGroupVo getChatGroup(String id, boolean member) {
//
//        Cursor cursor = null;
//        SQLiteDatabase database = null;
//        try {
//            database = getReadableDatabase();
//
//            cursor = database.query(TBL_GROUP, null,
//                    CL_GROUP_ID + " =? ",
//                    new String[]{id},
//                    null, null, null);
//
//            if (!cursor.moveToFirst()) {
//                return null;
//            }
//
//            ChatGroupVo groupVo = new ChatGroupVo();
//            groupVo.setId(id);
//            groupVo.setAvatar(cursor.getString(cursor.getColumnIndex(CL_GROUP_AVATAR)));
//            groupVo.setCreatedDate(cursor.getString(cursor.getColumnIndex(CL_GROUP_CREATED_DATE)));
//            groupVo.setName(cursor.getString(cursor.getColumnIndex(CL_GROUP_SUBJECT)));
//            groupVo.setIsDeleted(cursor.getInt(cursor.getColumnIndex(CL_GROUP_DELETED)));
//            groupVo.setDrawColor(cursor.getInt(cursor.getColumnIndex(CL_DRAW_COLOR)));
//            groupVo.setLastMessage(cursor.getString(cursor.getColumnIndex(CL_GROUP_LAST_MESSAGE)));
//
//
//            if (!member) {
//                return groupVo;
//            }
//
//            String sql = "SELECT * FROM %s m LEFT OUTER JOIN %s c ON m.%s= c.%s " +
//                    "WHERE m.%s='%s' " +
//                    "ORDER BY c.%s ASC";
//
//            Object[] objects = {TBL_GROUP_MEMBER, TBL_CONTACT, CL_GROUP_MEMBER, CL_CONTACT_NUMBER,
//                    CL_GROUP_ID, id,
//                    CL_CONTACT_NAME};
//
//            String query = String.format(sql, objects);
//            cursor = database.rawQuery(query, null);
//
//            if (cursor.moveToFirst()) {
//                ArrayList<ContactVo> members = new ArrayList<>();
//                do {
//                    ContactVo vo = wrapContactObj(cursor);
//                    vo.setAdmin(cursor.getInt(cursor.getColumnIndex(CL_GROUP_MEMBER_AFFIL)) == 1);
//                    vo.setPhoneNumber(cursor.getString(cursor.getColumnIndex(CL_GROUP_MEMBER)));
//                    members.add(vo);
//                } while (cursor.moveToNext());
//
//                groupVo.setMembers(members);
//            }
//
//            return groupVo;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            if (database != null)
//                database.close();
//        }
//        return null;
//    }
//
//    public ArrayList<ChatGroupVo> getGroupIds(String user) {
//        ArrayList<ChatGroupVo> list = new ArrayList<>();
//        Cursor cursor = null;
//        SQLiteDatabase database = null;
//        try {
//            database = getReadableDatabase();
//
//            String sql = "select g.group_id, g.last_message from group_member m left join group_info g on g.group_id= m.group_id where  m.group_member='" + user + "'";
//
//            cursor = database.rawQuery(sql, null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    ChatGroupVo groupVo = new ChatGroupVo();
//                    groupVo.setId(cursor.getString(0));
//                    groupVo.setLastMessage(cursor.getString(1));
//                    list.add(groupVo);
//                } while (cursor.moveToNext());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null)
//                cursor.close();
//            if (database != null)
//                database.close();
//
//        }
//        return list;
//    }
//
//    public ArrayList<ChatGroupVo> getGroupList() {
//        ArrayList<ChatGroupVo> list = new ArrayList<>();
//        Cursor cursor = null;
//        SQLiteDatabase database = null;
//        try {
//            database = getReadableDatabase();
//
//            cursor = database.query(TBL_GROUP, null, null, null, null, null, CL_GROUP_SUBJECT + " ASC");
//
//            if (cursor.moveToFirst()) {
//                do {
//                    ChatGroupVo vo = new ChatGroupVo();
//                    vo.setName(cursor.getString(cursor.getColumnIndex(CL_GROUP_SUBJECT)));
//                    vo.setLastMessage(cursor.getString(cursor.getColumnIndex(CL_GROUP_LAST_MESSAGE)));
//                    vo.setId(cursor.getString(cursor.getColumnIndex(CL_GROUP_ID)));
//                    vo.setCreatedDate(cursor.getString(cursor.getColumnIndex(CL_GROUP_CREATED_DATE)));
//                    vo.setDrawColor(cursor.getInt(cursor.getColumnIndex(CL_DRAW_COLOR)));
//                    list.add(vo);
//                } while (cursor.moveToNext());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null)
//                cursor.close();
//            if (database != null)
//                database.close();
//
//        }
//        return list;
//    }
//
//    public void addNewMember(ContactVo member, String groupId) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_GROUP_ID, groupId);
//            values.put(CL_GROUP_MEMBER, member.getPhoneNumber());
//            values.put(CL_GROUP_MEMBER_AFFIL, member.isAdmin() ? 1 : 0);
//
//            long id = database.insert(TBL_GROUP_MEMBER, null, values);
//
//            Logger.d("addNewMember", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//    }
//
//    public void updateMessageTime(SQLiteDatabase database, String groupId) {
//
//        try {
//
//            ContentValues values = new ContentValues();
//            values.put(CL_GROUP_LAST_MESSAGE, String.valueOf(System.currentTimeMillis()));
//
//            long id = database.update(TBL_GROUP, values, CL_GROUP_ID + " =? ", new String[]{groupId});
//
//            Logger.d("updateMessageTime", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void changeAdmin(String groupId, String newAdmin) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_GROUP_MEMBER_AFFIL, 0);
//
//            long id = database.update(TBL_GROUP_MEMBER, values, CL_GROUP_ID + " =? ", new String[]{groupId});
//
//            values.put(CL_GROUP_MEMBER_AFFIL, 1);
//
//            long id2 = database.update(TBL_GROUP_MEMBER, values,
//                    CL_GROUP_ID + " =? AND " + CL_GROUP_MEMBER + " =?",
//                    new String[]{groupId, newAdmin});
//
//            Logger.d("changeAdmin", "id> " + id + "|" + id2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//    }
//
//    public void updateGroupMemberWithNewNum(String oldNum, String numNum) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_GROUP_MEMBER, numNum);
//
//            long id = database.update(TBL_GROUP_MEMBER, values, CL_GROUP_MEMBER + " =? ", new String[]{oldNum});
//
//            Logger.d("updateGroupMemberWithNewNum", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//    }
//
//    public void updateGroupName(String name, String groupId) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_GROUP_SUBJECT, name);
//
//            long id = database.update(TBL_GROUP, values, CL_GROUP_ID + " =? ", new String[]{groupId});
//
//            Logger.d("updateGroupName", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//    }
//
//    public void removeMember(String groupId, String memberId) {
//
//        try {
//            database = getWritableDatabase();
//
//            long id = database.delete(TBL_GROUP_MEMBER, CL_GROUP_ID + " =? AND " + CL_GROUP_MEMBER + " =? ",
//                    new String[]{groupId, memberId});
//
//            Logger.d("removeMember", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//
//    }
//
//    public void deleteGroupOrMember(String groupId) {
//
//        try {
//            database = getWritableDatabase();
//
//            long id = database.delete(TBL_GROUP, CL_GROUP_ID + " =? ",
//                    new String[]{groupId});
//
//            long id1 = database.delete(TBL_GROUP_MEMBER, CL_GROUP_ID + " =? ",
//                    new String[]{groupId});
//
//            Logger.d("deleteGroupOrMember", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//        deleteChat(groupId, true);
//
//    }
//
//    public void updateFileProgress(String packetId, int status) {
//        updateFileProgress(packetId, status, false);
//    }
//
//    public void updateFileProgress(String packetId, int status, boolean secure) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_FILE_PROGRESS, status);
//
//            if (status == FileManager.FILE_DONE) {
//                values.put(CL_TIME_SENT, String.valueOf(System.currentTimeMillis()));
//                if (secure) {
//                    values.put(CL_TIME_EXPIRE, String.valueOf(System.currentTimeMillis() + ChatVo.getExpireTimeMilli(mContext)));
//                }
//            }
//
//            long id = database.update(TBL_CHAT, values, CL_PACKET_ID + " =? ", new String[]{packetId});
//
//            Logger.d("updateFileProgress", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//
//    }
//
//    public void updateFileServer(String packetId, String serverFile) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_FILE_PROGRESS, FileManager.FILE_DONE);
//            values.put(CL_FILE_SERVER, serverFile);
//
//            long id = database.update(TBL_CHAT, values, CL_PACKET_ID + " =? ", new String[]{packetId});
//
//            Logger.d("updateFileProgress", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//
//    }
//
//    public void updateFileLocale(String packetId, String localeFile) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_FILE_PROGRESS, FileManager.FILE_DONE);
//            values.put(CL_FILE_LOCALE, localeFile);
//
//            long id = database.update(TBL_CHAT, values, CL_PACKET_ID + " =? ", new String[]{packetId});
//
//            Logger.d("updateFileProgress", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//
//    }
//
//
//    public synchronized boolean addPhotoStream(UploadFileVo vo) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_PACKET_ID, vo.getPacketId());
//            values.put(CL_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
//            values.put(CL_BUDDY, vo.getTo());
//            values.put(CL_FROM, vo.getFrom());
//            values.put(CL_FILE_LOCALE, vo.getFilePath());
//            values.put(CL_CHAT_TYPE, vo.getType());
//            values.put(CL_TIME_SCHEDULE, vo.getScheduleAt());
//            values.put(CL_SECURE, vo.isSecure() ? 1 : 0);
//
//            long recentId = database.insert(TBL_PHOTO_STREAM, null, values);
//
//            Logger.d("addPhotoStream", "< " + recentId + " >");
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            database.close();
//        }
//
//        return false;
//    }
//
//    public void updateFileServerPStream(String packetId, String serverFile) {
//
//        try {
//            database = getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(CL_FILE_SERVER, serverFile);
//
//            long id = database.update(TBL_PHOTO_STREAM, values, CL_PACKET_ID + " =? ", new String[]{packetId});
//
//            Logger.d("updateFileServerPStream", "id> " + id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//    }
//
//    public UploadFileVo removeOrSchedulePhotoStream(UploadFileVo vo, long after) {
//
//        try {
//            database = getWritableDatabase();
//
//            long delete = database.delete(TBL_PHOTO_STREAM, CL_PACKET_ID + " =?", new String[]{vo.getPacketId()});
//
//
//            Cursor cursor = database.query(TBL_PHOTO_STREAM, null,
//                    CL_BUDDY + " =? ", new String[]{vo.getTo()}, null, null, null);
//
//
//            if (cursor.moveToFirst()) {
//                UploadFileVo fileVo = wrapStreamObj(cursor);
//
//                long nextRun = System.currentTimeMillis() + after;
//
//                ContentValues values = new ContentValues();
//                values.put(CL_TIME_SCHEDULE, String.valueOf(nextRun));
//
//                long update = database.update(TBL_PHOTO_STREAM, values, CL_PACKET_ID + " =? ", new String[]{fileVo.getPacketId()});
//
//                Logger.d("removeOrSchedulePhotoStream", "update> " + update + "  @ " + new Date(nextRun).toString());
//
//                fileVo.setScheduleAt(String.valueOf(nextRun));
//
//                return fileVo;
//            }
//
//            Logger.d("removeOrSchedulePhotoStream", "delete> " + delete);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//
//        return null;
//
//    }
//
//    public ArrayList<UploadFileVo> getPhotoStreams() {
//        ArrayList<UploadFileVo> fileVos = new ArrayList<>();
//        try {
//            database = getReadableDatabase();
//            String query = "select * from %s where %s IN " +
//                    "(select min(%s) from %s group by %s)";
//
//            String objs[] = {TBL_PHOTO_STREAM, CL_TIMESTAMP, CL_TIMESTAMP, TBL_PHOTO_STREAM, CL_BUDDY};
//
//            Cursor cursor = database.rawQuery(String.format(query, objs), null);
//
//            if (cursor.moveToFirst()) {
//                do {
//                    fileVos.add(wrapStreamObj(cursor));
//                } while (cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null)
//                database.close();
//
//        }
//        return fileVos;
//    }
//
//    private UploadFileVo wrapStreamObj(Cursor cursor) {
//        UploadFileVo vo = new UploadFileVo();
//
//        vo.setPacketId(cursor.getString(cursor.getColumnIndex(CL_PACKET_ID)));
//        vo.setFrom(cursor.getString(cursor.getColumnIndex(CL_FROM)));
//        vo.setTo(cursor.getString(cursor.getColumnIndex(CL_BUDDY)));
//        vo.setType(cursor.getString(cursor.getColumnIndex(CL_CHAT_TYPE)));
//
//        vo.setScheduleAt(cursor.getString(cursor.getColumnIndex(CL_TIME_SCHEDULE)));
//
//        vo.setFilePath(cursor.getString(cursor.getColumnIndex(CL_FILE_LOCALE)));
//        vo.setFileServer(cursor.getString(cursor.getColumnIndex(CL_FILE_SERVER)));
//        vo.setSecure(cursor.getInt(cursor.getColumnIndex(CL_SECURE)) == 1);
//
//
//        return vo;
//    }
}
