package in.tagteen.tagteen.Fragments;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import in.tagteen.tagteen.Adapters.FriendsListAdapter;
import in.tagteen.tagteen.ChatTreadActivity;
import in.tagteen.tagteen.LocalCasha.DataBaseHelper;
import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.Model.Friend;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;


public class FriendsListFragment extends Fragment implements AdapterView.OnItemSelectedListener ,AsyncResponse{

    List<Friend> friends=new ArrayList<>();
    Dialog dialog;
    String[] country = { "Online","All","BFF"  };
    ArrayAdapter aa;
    FriendsListAdapter adapter;
    GridView listView;
    SQLiteDatabase db;
    DataBaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  rootView = inflater.inflate(R.layout.list_view, container, false);
        listView = (GridView)rootView.findViewById(R.id.listview_con);
        adapter = new FriendsListAdapter(this.getActivity(),friends);
        listView.setAdapter(adapter);
        dbHelper = new DataBaseHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        networkCall();
        // linearLayout =(LinearLayout) rootView.findViewById(R.id.more_button_bac);


        updateUIFromCache();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friendListModel = friends.get(position);
                SharedPreferenceSingleton.getInstance().init(getActivity());

                SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.FRIEND_ID,friendListModel.getId());
                SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.FRIEND_NAME,friendListModel.getName());
                SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.FRIEND_IMAGE,friendListModel.getImg());
                SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.FRIEND_TAG,friendListModel.getTag());
                Intent chatIntent = new Intent(getActivity(), ChatTreadActivity.class);
                startActivity(chatIntent);

            }
        });
     /*   Spinner spin = (Spinner) rootView.findViewById(R.id.number_spinner);
        spin.setOnItemSelectedListener(this);
        aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);*/
        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        switch (REQUEST_NUMBER) {
            case RequestConstants.FRIEND_LIST_REQUEST: {
                friendList(output);
            }
        }
    }

    public void friendList(String output){


        try {
            JSONObject js = new JSONObject(output);
            JSONObject jsObject = js.getJSONObject("data");
            JSONArray jsonArray = jsObject.getJSONArray("friends_user_list");
            for(int i=0; i<jsonArray.length(); i++ ){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Friend friend = new Friend();
                friend.setImg(jsonObject.getString("profile_url"));
                friend.setName(jsonObject.getString("first_name")+" "+jsonObject.getString("last_name"));
                friend.setTag(jsonObject.getString("tagged_number"));
                friend.setId(jsonObject.getString("_id"));
                myDatabase(friend);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public void myDatabase( Friend friend){

        String id;
        String isGroup;
        String userName;
        String userTag;
        String profileImage;
        Cursor res = db.rawQuery("select * from "+DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, null);

        boolean isCacheDataExist = false;
        if(res.getCount() > 0){
            isCacheDataExist = true;
        }
        id=friend.getId();
        userName=friend.getName();
        userTag=friend.getTag();
        profileImage=friend.getImg();
        if (!isCacheDataExist){
            InsertFriendList(id, DatabaseContracts.IS_FALSE, userName, userTag, profileImage );
        }else{
            String[] parameter = {id};
            Cursor friendListInfo = db.rawQuery("select * from "+DatabaseContracts.userLisTable.FRIEND_LIST_TABLE+" WHERE "+DatabaseContracts.userLisTable.USERID+"=? ", parameter);
            if(friendListInfo.getCount() == 0){
                InsertFriendList(id, DatabaseContracts.IS_FALSE, userName, userTag, profileImage );
            }else{
                updateChatData(id, DatabaseContracts.IS_FALSE, userName, userTag, profileImage);
            }
        }
        updateUIFromCache();
    }

    public void networkCall(){
        AsyncWorker mWorker = new AsyncWorker(getActivity());
        mWorker.delegate = this;
        String id = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String url = ServerConnector.FRIEND_LIST + id ;
        Log.e("Friend_list_url"," " + url);
        mWorker.execute(url,"", RequestConstants.GET_REQUEST, RequestConstants.HEADER_YES, RequestConstants.FRIEND_LIST_REQUEST);
    }

    public long InsertFriendList(String id, String isGroup, String userName, String userTag, String profileImage ) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.userLisTable.USERID, id);
        contentValues.put(DatabaseContracts.userLisTable.IS_GROUP, isGroup);
        contentValues.put(DatabaseContracts.userLisTable.USERNAME, userName);
        contentValues.put(DatabaseContracts.userLisTable.TAG_NUMBER, userTag);
        contentValues.put(DatabaseContracts.userLisTable.lAST_MESSAGE, "null");
        contentValues.put(DatabaseContracts.userLisTable.lAST_MESSAGE_DATE,"null");
        contentValues.put(DatabaseContracts.userLisTable.CHAT_COUNT, "0");
        contentValues.put(DatabaseContracts.userLisTable.lAST_MESSAGE_TIME,"null");
        contentValues.put(DatabaseContracts.userLisTable.PROFILE_IMAGE, profileImage);
        contentValues.put(DatabaseContracts.userLisTable.LAST_MESSAGE_STATUS, DatabaseContracts.PENDING);
        contentValues.put(DatabaseContracts.userLisTable.IS_HIDEN, DatabaseContracts.IS_FALSE);
        contentValues.put(DatabaseContracts.userLisTable.IS_BLOCK, DatabaseContracts.IS_FALSE);
        contentValues.put(DatabaseContracts.userLisTable.IS_MUTE, DatabaseContracts.IS_FALSE);
        contentValues.put(DatabaseContracts.userLisTable.IS_CHAT_STARTED, DatabaseContracts.IS_FALSE);
        contentValues.put(DatabaseContracts.userLisTable.IS_LOCKED, DatabaseContracts.IS_FALSE);
        long insertedId = db.insert(DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, null, contentValues);
        return insertedId;
    }

    public void updateChatData(String id, String isGroup, String userName, String userTag, String profileImage ) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.userLisTable.USERID, id);
        contentValues.put(DatabaseContracts.userLisTable.IS_GROUP, isGroup);
        contentValues.put(DatabaseContracts.userLisTable.USERNAME, userName);
        contentValues.put(DatabaseContracts.userLisTable.TAG_NUMBER, userTag);
        contentValues.put(DatabaseContracts.userLisTable.PROFILE_IMAGE, profileImage);
        db.update( DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, contentValues, DatabaseContracts.userLisTable.USERID + "= ? ", new String[]{id});

    }

    public void updateUIFromCache() {
        friends.clear();
        Cursor res = db.rawQuery("SELECT * FROM "+DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {

            Friend friend = new Friend();
            friend.setImg(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.PROFILE_IMAGE)));
            friend.setName(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.USERNAME)));
            friend.setTag(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.TAG_NUMBER)));
            friend.setId(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.USERID)));
            friends.add(friend);
            res.moveToNext();

        }

        res.close();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }
    public void openPassWordDialog() {
        dialog = new Dialog(getActivity(), in.tagteen.tagteen.R.style.MyCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.password_layout_dailog);
        final Window layout = dialog.getWindow();
        layout.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        layout.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
        layout.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        layout.setBackgroundDrawable(new ColorDrawable(getResources().getColor(in.tagteen.tagteen.R.color.pas_transparent)));
        dialog.show();
    }
    public void beginSearch(String query) {
        adapter.getFilter().filter(query);
    }

}
