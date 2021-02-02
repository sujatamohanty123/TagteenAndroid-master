package in.tagteen.tagteen.LocalCasha;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.tagteen.tagteen.Model.GetAllUserFriendlist;


public class DataBaseHelper extends SQLiteOpenHelper {

    Context mCtx;
    private SQLiteDatabase mDB;

    public static final String TABLE_CHAT_LIST = "CREATE TABLE IF NOT EXISTS "

            + DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE + "(" +
            "" + DatabaseContracts.ChatContractDataBase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "" + DatabaseContracts.ChatContractDataBase.CHAT_ID + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.CREATOR_ID + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.RECEIVER_USER_ID + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.CHAT_PAGE_ID + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.DATE + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.TIME + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.MESSAGE + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.MEDIA_LINK+ " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.LOCATPATH+ " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.ISPRIVATE_MSG + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.THUMB_NAIL + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.MESSAGE_TYPE + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.IS_GROUP + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.IS_FROM_ME + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.PRIVATE_MSG_TIME + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.MEDIA_HEIGHT + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.MEDIA_WIDTH + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS +" VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.EVENT_TYPE + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.EVENT_STATUS+ " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.IS_REPLY + " VARCHAR,"+
         //   "" + DatabaseContracts.ChatContractDataBase.REPLY_TO_ID+ " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.REPLY_TO_MSG_TYPE + " VARCHAR,"+
         //   "" + DatabaseContracts.ChatContractDataBase.REPLY_TO_CONTENT+ " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.REPLY_TO_MEDIA + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.DOWNLOAD_STATUS + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS + " VARCHAR,"+
            "" + DatabaseContracts.ChatContractDataBase.ISVIEWED + " VARCHAR )";

    public static final String FRIEND_LIST = "CREATE TABLE IF NOT EXISTS "
            + DatabaseContracts.userLisTable.FRIEND_LIST_TABLE + "(" +
            "" + DatabaseContracts.userLisTable.USERID + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.IS_GROUP + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.USERNAME + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.lAST_MESSAGE_DATE + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.lAST_MESSAGE_TIME + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.CHAT_COUNT + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.TAG_NUMBER + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.PROFILE_IMAGE + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.LAST_MESSAGE_STATUS + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.IS_HIDEN + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.IS_LOCKED + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.IS_CHAT_STARTED + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.IS_MUTE + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.IS_BLOCK + " VARCHAR,"+
            "" + DatabaseContracts.userLisTable.lAST_MESSAGE + " VARCHAR )";


    public static final String TABLE_REACTION_LIST = "CREATE TABLE IF NOT EXISTS "
            + DatabaseContracts.FaceReactionDataBase.REACTION_TABLE + "(" +
            "" + DatabaseContracts.FaceReactionDataBase.ID + " VARCHAR,"+
            "" + DatabaseContracts.FaceReactionDataBase.REACTION_NAME + " VARCHAR,"+
            "" + DatabaseContracts.FaceReactionDataBase.REACTON_AWS_URL + " VARCHAR,"+
            "" + DatabaseContracts.FaceReactionDataBase.REACTION_lOCAL_PATH + " VARCHAR )";


    public DataBaseHelper(Context context) {
        super(context, DatabaseContracts.TAGTEEN_DATABASE, null, DatabaseContracts.DATABASE_VERSION);
        this.mCtx = context;
        mDB = this.getWritableDatabase();
    }

    public DataBaseHelper open() throws SQLException {
        mDB = this.getWritableDatabase();
        return this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CHAT_LIST);
        db.execSQL(TABLE_REACTION_LIST);
        db.execSQL(FRIEND_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContracts.FaceReactionDataBase.REACTION_TABLE);
        onCreate(db);
    }




    public boolean deleteUserRow(String chatPage) {
    return mDB.delete(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, DatabaseContracts.ChatContractDataBase.CHAT_PAGE_ID + "=" + chatPage, null) > 0;
    }

    public void deleteAllTableData() {
         mDB.delete(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE , null, null);
    }

    private boolean updateChatCache(String _Id, String chatId, String isViewed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.CHAT_ID, chatId);
        contentValues.put(DatabaseContracts.ChatContractDataBase.ISVIEWED, isViewed);
    /*  contentValues.put(DatabaseContracts.ChatContractDataBase.IS_AGREE,isAgree);
        contentValues.put(DatabaseContracts.ChatContractDataBase.IS_LIKED,isLiked);*/
        mDB.update( DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, contentValues, DatabaseContracts.ChatContractDataBase._ID+ "= ? ", new String[]{_Id});
        return true;
    }

}
