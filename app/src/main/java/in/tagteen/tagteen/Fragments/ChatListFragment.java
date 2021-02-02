package in.tagteen.tagteen.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.tagteen.tagteen.database.TagTeenEventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Adapters.RecyclerViewChatFriendsAdapter;
import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.Model.ChatMessage;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.Model.SingleItemModel;
import in.tagteen.tagteen.PinLockActivity;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.CommonAdapterLongPress;
import in.tagteen.tagteen.configurations.AppConfigurationSetting;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.database.DBOpt;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.DateTimeCalculation;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;


public class ChatListFragment extends Fragment {

    private Toolbar toolbar;
    ArrayList<SectionDataModel> allSampleData;
    List<ChatMessage> friends = new ArrayList<>();
    LinearLayout linearLayout;
    Dialog dialog;
    Dialog onLongPressdialog;
    ChatMessage selectedFriend;
    int selectedPosition;
    String mSocketSenderId;
    SQLiteDatabase db;;
    //    DataBaseHelper dbHelper;
    RecyclerView my_recycler_view;
    RecyclerViewChatFriendsAdapter adapter;
    TextView textView;
    EditText editPasswordView;
    boolean isConfirmingPassword = false;
    private final TextWatcher watcher1 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String password = editPasswordView.getText().toString();
            if (password.length() == 4) {
                if (!isConfirmingPassword) {
                    SharedPreferenceSingleton.getInstance().init(getActivity());
                    SharedPreferenceSingleton.getInstance().writeStringPreference(AppConfigurationSetting.LOCK_PASSWORD, editPasswordView.getText().toString());
                    textView.setText("Confirm the Password");
                    editPasswordView.setText("");
                    isConfirmingPassword = true;

                } else {

                    SharedPreferenceSingleton.getInstance().init(getActivity());
                    String passwordValue = SharedPreferenceSingleton.getInstance().getStringPreference(AppConfigurationSetting.LOCK_PASSWORD);
                    String textPassword = editPasswordView.getText().toString();
                    if (passwordValue.equals(textPassword)) {
                        lockTheList(selectedFriend.getFriendId(), DatabaseContracts.IS_TRUE);
                        SharedPreferenceSingleton.getInstance().writeStringPreference(ApplicationConstants.PASSWORD_STATUS, AppConfigurationSetting.PASSWORD_SETTING_STATUS[1]);
                        dialog.dismiss();
                        onLongPressdialog.dismiss();
                    } else {
                        editPasswordView.setText("");
                        isConfirmingPassword = false;
                    }

                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.chat_listview, container, false);
        SharedPreferenceSingleton.getInstance().init(getActivity());
        mSocketSenderId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
//        dbHelper = new DataBaseHelper(getActivity());
//        db = dbHelper.getWritableDatabase();
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        allSampleData = new ArrayList<SectionDataModel>();
        createDummyData();
        call_online_friend_api();
        my_recycler_view = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        my_recycler_view.setHasFixedSize(true);
        beginSearch(null,false);

//        friends = DBOpt.getInstance(getActivity()).getChatList(null);
//        adapter = new RecyclerViewChatFriendsAdapter(getActivity(), allSampleData, friends, new CommonAdapterLongPress() {
//            @Override
//            public void getClickedRecyclerView(int position) {
//                openDialog(position - 1);
//                selectedPosition = position - 1;
//            }
//        });
//        my_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        my_recycler_view.setAdapter(adapter);

        updateUIFromCache();
        return rootView;
    }

    private void call_online_friend_api() {
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = TagTeenEventBus.getEventData(intent);
            ChatMessage chatMessage = TagTeenEventBus.getDataObj(intent);
            int eType = TagTeenEventBus.getEventType(intent);

            switch (eType) {

                case TagTeenEventBus.TYPE_NEW_MESSAGE:

//                    chatMessageList.add(chatMessage);
//                    chatAdapter.notifyDataSetChanged();
//
//                    listView.scrollToPosition(chatAdapter.getItemCount() - 1);

                    onResume();
                    break;
                case TagTeenEventBus.TYPE_MESSAGE_SENT:
                    updateUIFromCache();
                    break;
                case TagTeenEventBus.TYPE_MESSAGE_SEEN:
                    updateUIFromCache();
                    break;

                case TagTeenEventBus.TYPE_MESSAGE_DELIVERED:

                    updateUIFromCache();
                    break;

            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        try {
            TagTeenEventBus.registerEventBus(getActivity(), receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        TagTeenEventBus.removeEventBus(getActivity(), receiver);
    }



    @Override
    public void onResume() {
        super.onResume();

        beginSearch(null, false);
    }

    public void createDummyData() {
        for (int i = 1; i <= 10; i++) {

            SectionDataModel dm = new SectionDataModel();
            dm.setHeaderTitle("Section " + i);
            ArrayList<SingleItemModel> singleItem = new ArrayList<>();
            for (int j = 0; j <= 5; j++) {
                singleItem.add(new SingleItemModel("Item " + j, "URL " + j));
            }

            dm.setAllItemsInSection(singleItem);
            allSampleData.add(dm);

        }
    }


    public void updateUIFromCache() {
//        String[] parameter = {DatabaseContracts.IS_FALSE, DatabaseContracts.IS_FALSE, DatabaseContracts.IS_TRUE};
//        Cursor res = db.rawQuery("select * from " + DatabaseContracts.userLisTable.FRIEND_LIST_TABLE + " WHERE " + DatabaseContracts.userLisTable.IS_HIDEN + "=? AND " + DatabaseContracts.userLisTable.IS_BLOCK + "=? AND " + DatabaseContracts.userLisTable.IS_CHAT_STARTED + "=?" + " ORDER BY " + DatabaseContracts.userLisTable.lAST_MESSAGE_DATE + " DESC", parameter);
//        res.moveToFirst();
//        while (!res.isAfterLast()) {
//            Friend friend = new Friend();
//            friend.setImg(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.PROFILE_IMAGE)));
//            friend.setName(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.USERNAME)));
//            friend.setTag(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.TAG_NUMBER)));
//            friend.setId(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.USERID)));
//            friend.setIsGroup(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.IS_GROUP)));
//            friend.setLastMessage(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.lAST_MESSAGE)));
//            String date = res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.lAST_MESSAGE_DATE));
//            if (date == null || date == "null" || date == "") {
//                friend.setLastMsgDate("");
//            } else {
//                friend.setLastMsgDate(setTime(date));
//            }
//
//            friend.setLastMsgTime(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.lAST_MESSAGE_TIME)));
//            friend.setChatCount(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.CHAT_COUNT)));
//            friend.setMsgStatus(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.LAST_MESSAGE_STATUS)));
//            friend.setIsLocked(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.IS_LOCKED)));
//            friend.setIsMute(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.IS_MUTE)));
//            friends.add(friend);
//            res.moveToNext();

//        }
//        res.close();

//        friends = DBOpt.getInstance(getActivity()).getChatList(null);
//        adapter.notifyDataSetChanged();
    }

    public void openDialog(final int position) {
        onLongPressdialog = new Dialog(getActivity());
        onLongPressdialog.setContentView(R.layout.chat_friend_list_dialog);
        LinearLayout hideLinearLayout = (LinearLayout) onLongPressdialog.findViewById(R.id.hide_container);
        LinearLayout lockLinearLayout = (LinearLayout) onLongPressdialog.findViewById(R.id.lock_container);
        LinearLayout deleteLinearLayout = (LinearLayout) onLongPressdialog.findViewById(R.id.delete_container);
        LinearLayout muteLinearLayout = (LinearLayout) onLongPressdialog.findViewById(R.id.mute_container);
        LinearLayout blockLinearLayout = (LinearLayout) onLongPressdialog.findViewById(R.id.block_container);
        LinearLayout viewProfileLinearLayout = (LinearLayout) onLongPressdialog.findViewById(R.id.viewProfile_container);
        TextView muteText = (TextView) onLongPressdialog.findViewById(R.id.mute_text);
        TextView lockText = (TextView) onLongPressdialog.findViewById(R.id.lock_text);
        TextView nameText = (TextView) onLongPressdialog.findViewById(R.id.name_text);
        final ChatMessage friendlist = friends.get(position);
        nameText.setText(friendlist.getFriendName());
        if (friendlist.isLocked() == 1) {
            lockText.setText("Lock Chat");
        } else {
            lockText.setText("Unlock Chat");
        }

//        if (friendlist.getIsMute().equals(DatabaseContracts.IS_FALSE)) {
//            muteText.setText("Mute");
//        } else {
//            muteText.setText("Unmute");
//        }
        hideLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openAlertDialog(1, position);
                Intent intent=new Intent(getActivity(), PinLockActivity.class);
                intent.putExtra("position",position);
                startActivityForResult(intent,1);
            }
        });
        lockLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlertDialog(2, position);
            }
        });
        deleteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlertDialog(3, position);
            }
        });
        muteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlertDialog(4, position);
            }
        });
        blockLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlertDialog(5, position);
            }
        });
        viewProfileLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoProfile(getActivity(),friendlist.getFriendId());
            }
        });
        onLongPressdialog.show();
    }

    public void deleteUserRow(String chatPage) {
        Log.e("chatPage_delete", chatPage);
        db.delete(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, DatabaseContracts.ChatContractDataBase.CHAT_PAGE_ID + "=?", new String[]{chatPage});
    }

    private boolean HideTheList(String userId, String isHide) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.userLisTable.IS_HIDEN, isHide);
        db.update(DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, contentValues, DatabaseContracts.userLisTable.USERID + "= ? ", new String[]{userId});
        return true;
    }

    private boolean lockTheList(String userId, String isLock) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.userLisTable.IS_LOCKED, isLock);
        db.update(DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, contentValues, DatabaseContracts.userLisTable.USERID + "= ? ", new String[]{userId});
        return true;
    }

    private boolean muteList(String userId, String isMute) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.userLisTable.IS_LOCKED, isMute);
        db.update(DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, contentValues, DatabaseContracts.userLisTable.USERID + "= ? ", new String[]{userId});
        return true;
    }

    private boolean BlockTheList(String userId, String isBlock) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.userLisTable.IS_BLOCK, isBlock);
        db.update(DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, contentValues, DatabaseContracts.userLisTable.USERID + "= ? ", new String[]{userId});
        return true;
    }

    public void openAlertDialog(final int type, final int position) {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        String title = null;
        String message = null;
        // set title
        if (type == 1) {
            title = "HIDE FRIEND";
            message = "Do you want to hide the friend !";
        } else if (type == 2) {
            title = "LOCK FRIEND";
            message = "Do you want to lock the friend's chat !";
        } else if (type == 3) {
            title = "DELETE FRIEND";
            message = "Do you want to delete the friend's chat !";
        } else if (type == 4) {
            title = "MUTE FRIEND";
            message = "Do you want to mute the friend's chat !";
        } else if (type == 5) {
            title = "BLOCK FRIEND";
            message = "Do you want to block the friend's chat !";
        }

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ChatMessage friend = friends.get(position);
                        if (type == 1) {
                            HideTheList(friend.getFriendId(), DatabaseContracts.IS_TRUE);
                            removeList();
                            onLongPressdialog.dismiss();
                        } else if (type == 2) {
                            passwordStatusSetting(friend);
                            selectedFriend = friend;
                        } else if (type == 3) {
                            String pageId = mSocketSenderId + friend.getFriendId();
                            deleteUserRow(pageId);
                            onLongPressdialog.dismiss();
                        } else if (type == 4) {
                            if (friend.isLocked() == 1) {
                                updateUIMessageStatus(4, DatabaseContracts.IS_TRUE);
                                muteList(friend.getFriendId(), DatabaseContracts.IS_TRUE);
                            } else {
                                updateUIMessageStatus(4, DatabaseContracts.IS_FALSE);
                                muteList(friend.getFriendId(), DatabaseContracts.IS_FALSE);
                            }
                            onLongPressdialog.dismiss();
                        } else if (type == 5) {
                            BlockTheList(friend.getFriendId(), DatabaseContracts.IS_TRUE);
                            removeList();
                            onLongPressdialog.dismiss();
                        }
                        onLongPressdialog.cancel();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

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

    public void passwordStatusSetting(ChatMessage friend) {
        SharedPreferenceSingleton.getInstance().init(getActivity());
        String passwordStatus = SharedPreferenceSingleton.getInstance().getStringPreference(ApplicationConstants.PASSWORD_STATUS);
        if (passwordStatus.equals(AppConfigurationSetting.PASSWORD_SETTING_STATUS[0])) {
            openPassWordDialog();
        } else {
            if (friend.isLocked() == 1) {
                updateUIMessageStatus(2, DatabaseContracts.IS_TRUE);
                lockTheList(friend.getFriendId(), DatabaseContracts.IS_TRUE);
            } else {
                updateUIMessageStatus(2, DatabaseContracts.IS_FALSE);
                lockTheList(friend.getFriendId(), DatabaseContracts.IS_FALSE);
            }
            onLongPressdialog.dismiss();
        }
    }

    public void openPassWordDialog() {

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.password_layout_dailog);
        final Window layout = dialog.getWindow();
        layout.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        layout.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
        layout.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        layout.setBackgroundDrawable(new ColorDrawable(getResources().getColor(in.tagteen.tagteen.R.color.pas_transparent)));
        textView = (TextView) dialog.findViewById(R.id.password_text);
        editPasswordView = (EditText) dialog.findViewById(R.id.password_edittext);
        editPasswordView.requestFocus();
        textView.setText("Set the Password");
        editPasswordView.addTextChangedListener(watcher1);
        dialog.show();
    }

    private void updateUIMessageStatus(int type, String operation) {

        ChatMessage friendlist = friends.get(selectedPosition);
        boolean action = Boolean.parseBoolean(operation);
        if (type == 2) {

            int lockValue = 0;
            if (action)
                lockValue = 1;
            else
                lockValue = 0;
            friendlist.setLocked(lockValue);

        } else if (type == 4) {
//            friendlist.setIsMute(operation);
        }
        friends.set(selectedPosition, friendlist);
        my_recycler_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void removeList() {
        friends.remove(selectedPosition);
        my_recycler_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String Pin=data.getStringExtra("Pin");
                int position=data.getIntExtra("position",0);
                selectedPosition=position;
                ChatMessage friend1 = friends.get(position);
                SharedPreferenceSingleton.getInstance().init(getActivity());
                SharedPreferenceSingleton.getInstance().writeStringPreference(AppConfigurationSetting.HIDE_FRIEND_PIN,Pin);
                //HideTheList(friend1.getFriendId(), DatabaseContracts.IS_TRUE);
                DBOpt.getInstance(getActivity()).hideFriend(friend1.getFriendId());
                removeList();
                onLongPressdialog.dismiss();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void beginSearch(String query, boolean issearch) {
        friends.clear();
        friends.addAll(DBOpt.getInstance(getActivity()).getChatList(null,query,issearch));

        if (adapter == null) {
            adapter = new RecyclerViewChatFriendsAdapter(getActivity(), allSampleData, friends, new CommonAdapterLongPress() {
                @Override
                public void getClickedRecyclerView(int position) {
                    openDialog(position - 1);
                    selectedPosition = position - 1;
                }
            });
            my_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            my_recycler_view.setAdapter(adapter);
        } else {

            adapter.notifyDataSetChanged();
        }
    }
}

