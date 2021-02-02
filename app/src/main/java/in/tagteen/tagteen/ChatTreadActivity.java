package in.tagteen.tagteen;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import in.tagteen.tagteen.database.TagTeenEventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import in.tagteen.tagteen.Adapters.ChatEmojiAdapter;
import in.tagteen.tagteen.Adapters.ChatThreadRecyclerViewAdapter;
import in.tagteen.tagteen.Adapters.ForwordAdapter;
import in.tagteen.tagteen.CarouselPicker.CarouselPicker;
import in.tagteen.tagteen.FilePicker.FilePickerActivity;
import in.tagteen.tagteen.Fragments.AudioFileFragment;
import in.tagteen.tagteen.Fragments.AudioRecording;
import in.tagteen.tagteen.Fragments.ChatGalleryFragment;
import in.tagteen.tagteen.Fragments.SelfReactionFragment;
import in.tagteen.tagteen.Fragments.SelfiFragment;
import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.LocalCasha.SaveMediaCash;
import in.tagteen.tagteen.Model.ChatMessage;
import in.tagteen.tagteen.Model.Friend;
import in.tagteen.tagteen.Model.UserModel;
import in.tagteen.tagteen.TagteenInterface.CameraInterface;
import in.tagteen.tagteen.TagteenInterface.ChatDocInterFace;
import in.tagteen.tagteen.TagteenInterface.ChatImageInterface;
import in.tagteen.tagteen.TagteenInterface.ChatScreenCallback;
import in.tagteen.tagteen.TagteenInterface.CommonAdapterViewListner;
import in.tagteen.tagteen.TagteenInterface.DownLoadInterface;
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;
import in.tagteen.tagteen.TagteenInterface.MediaUploadDone;
import in.tagteen.tagteen.TagteenInterface.SendAudioFile;
import in.tagteen.tagteen.TagteenInterface.SendDocument;
import in.tagteen.tagteen.TagteenInterface.UserSelectionList;
import in.tagteen.tagteen.configurations.AppConfigurationSetting;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.SocketContracts;
import in.tagteen.tagteen.database.DBImpl;
import in.tagteen.tagteen.database.DBOpt;
import in.tagteen.tagteen.database.DatabaseHelper;
import in.tagteen.tagteen.emoji.Emojicon;
import in.tagteen.tagteen.emoji.EmojiconEditText;
import in.tagteen.tagteen.emoji.EmojiconGridFragment;
import in.tagteen.tagteen.emoji.EmojiconsFragment;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.utils.DateTimeCalculation;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatTreadActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, GalleryClickedPosition, EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        ChatScreenCallback, ChatImageInterface, ChatDocInterFace, SendDocument, SendAudioFile, CameraInterface {
    List<Friend> friends = new ArrayList<>();
    List<Friend> SelectedFriend;

    String rr = "https://s3.ap-south-1.amazonaws.com/ttprofileurl/Self_Reaction/Happy.png";
    String rv = "https://s3.ap-south-1.amazonaws.com/ttprofileurl/talenthunt_video/tagteen+video+medium+res.mp4";

    CarouselPicker.CarouselViewAdapter textAdapter;
    ForwordAdapter forwordAdapter;
    RecyclerView forwardRecyclerView;
    public static List<String> modelId = new ArrayList<String>();
    List<CarouselPicker.PickerItem> textItems;
    String pageId;
    String isGroup;
    String mIsPrivate;
    String privateMsgTime = "0";
    String mSocketSenderId;
    String mSocketReceiverId;
    String mFriendName, mFriendImage, mFriendTag;
    Dialog forwordDialog;
    private RecyclerView listView;
    ArrayList<String> imagePaths;
    LinearLayout backgroundWallPaper;
    SQLiteDatabase dbase;
    DatabaseHelper dbHelper;
    Dialog dialog;
    Dialog privacyDialog;
    LinearLayoutManager llm;
    FrameLayout cameraLayoutView;
    TextView setPrivacyTimer;
    boolean isPrivateMode = false;
    boolean isSendButton;
    RoundedImageView imageView;
    int STATUS_CHANGE_UPLOADED = 1;
    int STATUS_CHANGE_DOWNLOADED = 2;
    int STATUS_CHANGE_SENT = 3;
    LinearLayout eventLayout, fileOptionList, chatEditBoxLayout;
    //    FrameLayout bottom_area;
    EmojiconEditText mEditEmojicon;
    FrameLayout emojiFrame, chatListLayout, privacyTimerLayout, mainViewFragment;
    SimpleDateFormat simpleDateFormat;
    String time;
    boolean isFragmentOpened = false;
    Calendar calander;
    ImageView gallaryIcon, cameraIcon, pingIcon, selfiIcon, emojiBackButton, fileListOption, fileAttachment, youthTube, privacyButton, audio_mp_File;
    LinearLayout likeChat, agreeChat, forwardChat, replyChat, deleteChat, copyChat, reCallChat;
    int selectedPrivetTimer;
    final Handler handler = new Handler();
    Socket mSocket;
    int selectedPosition;
    private boolean mTyping = false;
    ChatMessage selectedChatMessageObj;
    private boolean isCameraOpened = false;
    CarouselPicker carouselPicker;
    ImageView audioAtt_File;
    boolean isAttachmentCloseButton = false, isPrivacyTimerLayoutVisible = false;
    TextView editTextTimerView, ab_friendName, ab_friendStatus;
    Animation slideLeft, rotationAnimation, antiClockRotation, animation1;
    ChatThreadRecyclerViewAdapter chatAdapter;
    int ImageClicked = 0;
    int opendEvent = 0;
    JSONObject sendIdObject = new JSONObject();
    private Handler mTypingHandler = new Handler();
    TextView textView;
    EditText editPasswordView;
    RoundedImageView myPic, groupPic, addGroupIcon;
    private SQLiteDatabase db;
    private DBOpt dbOpt;
    private ArrayList<ChatMessage> chatMessageList;

    public static String IS_IMAGE = "1";
    public static String IS_VIDEO = "2";
    public static String IS_AUDIO = "3";
    public static String IS_REACTION = "4";
    public static String IS_PING = "5";
    public static String IS_SOUND_EMOJI = "6";
    public static String IS_TEXT_MSG = "7";

    public static String MESSAGE_UNDELIVERED = "0";
    public static int MESSAGE_SENT = 1;
    public static int MESSAGE_DELIVERED = 2;
    public static String MESSAGE_SEEN = "3";

    private final TextWatcher passwordWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String password = editPasswordView.getText().toString();
            if (password.length() == 4) {
                SharedPreferenceSingleton.getInstance().init(ChatTreadActivity.this);
                String passwordValue = SharedPreferenceSingleton.getInstance().getStringPreference(AppConfigurationSetting.LOCK_PASSWORD);
                String textPassword = editPasswordView.getText().toString();
                if (passwordValue.equals(textPassword)) {
                    dialog.dismiss();
                } else {
                    editPasswordView.setText("");
                    textView.setText("Enter Correct Password");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            mSocket.emit(RegistrationConstants.TYPING, sendIdObject);

            try {
                JSONObject js = new JSONObject();
                JSONArray receiverId = new JSONArray();
                receiverId.put(mSocketReceiverId);
                js.put(SocketContracts.CHAT_CREATOR_ID, mSocketSenderId);
                js.put(SocketContracts.CHAT_RECEIVER_ID, receiverId);
                mSocket.emit(RegistrationConstants.TYPING, js);
            } catch (Exception e) {
//
            }

            mTypingHandler.removeCallbacks(onTypingTimeout);
            mTypingHandler.postDelayed(onTypingTimeout, 600);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mEditEmojicon.getText().toString().equals("")) {
                fileListOption.setImageDrawable(ContextCompat.getDrawable(ChatTreadActivity.this, in.tagteen.tagteen.R.drawable.cmp_add_icon));
                fileListOption.setPadding(7, 7, 7, 7);
                isSendButton = false;

            } else {

                fileListOption.setImageDrawable(ContextCompat.getDrawable(ChatTreadActivity.this, in.tagteen.tagteen.R.drawable.right_arrow));
                fileListOption.setPadding(7, 7, 7, 7);
                isSendButton = true;
            }
        }
    };
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;
            mTyping = false;
            mSocket.emit(RegistrationConstants.STOP_TYPING, sendIdObject);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.tagteen.tagteen.R.layout.chat_activity);
        backgroundWallPaper = (LinearLayout) findViewById(R.id.wall_paper_background);
        audio_mp_File = (ImageView) findViewById(in.tagteen.tagteen.R.id.audio_playList_Attachment);
        cameraLayoutView = (FrameLayout) findViewById(in.tagteen.tagteen.R.id.camera_fragment_layout);
        mainViewFragment = (FrameLayout) findViewById(in.tagteen.tagteen.R.id.main_view_fragment);
        fileListOption = (ImageView) findViewById(in.tagteen.tagteen.R.id.down_file_sendOption);
        setPrivacyTimer = (TextView) findViewById(in.tagteen.tagteen.R.id.privacy_ok_button);
        editTextTimerView = (TextView) findViewById(in.tagteen.tagteen.R.id.edittext_timer_view);
        privacyButton = (ImageView) findViewById(in.tagteen.tagteen.R.id.privacy_button);
        audioAtt_File = (ImageView) findViewById(in.tagteen.tagteen.R.id.cmp_voice_Atach);
        chatListLayout = (FrameLayout) findViewById(in.tagteen.tagteen.R.id.chat_listScreen);
        privacyTimerLayout = (FrameLayout) findViewById(in.tagteen.tagteen.R.id.privacy_timer_layout);
        emojiBackButton = (ImageView) findViewById(in.tagteen.tagteen.R.id.emojis_backspace);
        emojiFrame = (FrameLayout) findViewById(in.tagteen.tagteen.R.id.emojicons);
        final ImageView emojiSwitch = (ImageView) findViewById(in.tagteen.tagteen.R.id.emoji_switch);
        gallaryIcon = (ImageView) findViewById(in.tagteen.tagteen.R.id.cmp_gallery_icon);
        cameraIcon = (ImageView) findViewById(in.tagteen.tagteen.R.id.cmp_camera_icon);
        pingIcon = (ImageView) findViewById(in.tagteen.tagteen.R.id.cmp_ping_icon);
        selfiIcon = (ImageView) findViewById(in.tagteen.tagteen.R.id.cmp_selfi_icon);
        youthTube = (ImageView) findViewById(in.tagteen.tagteen.R.id.youthtube_attachment);
        mEditEmojicon = (EmojiconEditText) findViewById(in.tagteen.tagteen.R.id.msg_emojicon_edittext);
//        bottom_area = (FrameLayout) findViewById(in.tagteen.tagteen.R.id.cpm_bottom_area);
        listView = (RecyclerView) findViewById(in.tagteen.tagteen.R.id.msgview);
        animation1 = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.rotate_normal);
        eventLayout = (LinearLayout) findViewById(in.tagteen.tagteen.R.id.cmp_event_layout);
        fileOptionList = (LinearLayout) findViewById(in.tagteen.tagteen.R.id.file_optionList);
        chatEditBoxLayout = (LinearLayout) findViewById(in.tagteen.tagteen.R.id.chat_editText_layout);

        /*chattopIcon  recall*/

        likeChat = (LinearLayout) findViewById(R.id.like_Image);
        agreeChat = (LinearLayout) findViewById(R.id.agree_Image);
        reCallChat = (LinearLayout) findViewById(R.id.recall);
        forwardChat = (LinearLayout) findViewById(R.id.forward_Image);
        replyChat = (LinearLayout) findViewById(R.id.reply_Image);
        deleteChat = (LinearLayout) findViewById(R.id.delete_image);
        copyChat = (LinearLayout) findViewById(R.id.copy_image);

        likeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = selectedChatMessageObj.getServerId();
                String event = selectedChatMessageObj.getEventType();
                if (event.equals(DatabaseContracts.EVENT_LIKE)) {
                    eventMessage(DatabaseContracts.EVENT_UNlIKE, id);
                } else {
                    eventMessage(DatabaseContracts.EVENT_LIKE, id);
                }
            }
        });

        agreeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = selectedChatMessageObj.getServerId();
                String event = selectedChatMessageObj.getEventType();
                if (event.equals(DatabaseContracts.EVENT_AGREE)) {
                    eventMessage(DatabaseContracts.EVENT_DISAGREE, id);
                } else {
                    eventMessage(DatabaseContracts.EVENT_AGREE, id);
                }
            }
        });

        reCallChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = selectedChatMessageObj.getServerId();
                eventMessage(DatabaseContracts.EVENT_RECALL, id);
            }
        });

        forwardChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForwordDialog();
            }
        });

        replyChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSingleRow(selectedChatMessageObj.getClintId());
//                chatAdapter.chatMessageList.remove(selectedPosition);
                chatAdapter.notifyDataSetChanged();
            }
        });

        copyChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) ChatTreadActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(selectedChatMessageObj.getMessage());
            }
        });

        mEditEmojicon.addTextChangedListener(watcher1);

//        DatabasehelperOld myDbHelper = new DatabasehelperOld(this);
//        try {
//
//            myDbHelper.createDatabase();
//
//        } catch (IOException ioe) {
//
//            throw new Error("Unable to create database");
//
//        }
//        try {
//
//            dbase = myDbHelper.openDatabase();
//
//        } catch (SQLException sqle) {
//
//            sqle.printStackTrace();
//
//        }

//        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
//        try {
//            dbHelper.prepareDatabase();
//        } catch (IOException e) {
//            Log.e("database", e.getMessage());
//        }

        dbOpt = DBOpt.getInstance(this);


        eventLayout.setVisibility(View.GONE);
        SharedPreferenceSingleton.getInstance().init(this);


        mSocketSenderId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        mSocketReceiverId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_ID);

//        mSocketSenderId = "59ce05bad37c6073d27ea575";
//        mSocketReceiverId = "59c8d15ad37c6073d27ea336";

        int isLocked = SharedPreferenceSingleton.getInstance().getIntPreference(RegistrationConstants.IS_LOCKED_FRIEND);
        if (isLocked == 1) {
            openPassWordDialog();
        }
        try {
            sendIdObject.put("chat_creator_id", mSocketSenderId);
            sendIdObject.put("chat_receiver_id", mSocketReceiverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pageId = mSocketSenderId + mSocketReceiverId;
        isGroup = DatabaseContracts.IS_FALSE;
        mIsPrivate = DatabaseContracts.IS_FALSE;

        // callChatHistory(mSocketReceiverId);

        carouselPicker = (CarouselPicker) findViewById(in.tagteen.tagteen.R.id.carousel);
        fileAttachment = (ImageView) findViewById(in.tagteen.tagteen.R.id.file_Attachment);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View actionBarView = mInflater.inflate(in.tagteen.tagteen.R.layout.chat_actionbar, null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(actionBarView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        ab_friendName = (TextView) actionBarView.findViewById(R.id.chatreceiver_name);
        ab_friendStatus = (TextView) actionBarView.findViewById(R.id.chatSubtitle);
        mFriendName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_NAME);
        mFriendImage = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_IMAGE);
        mFriendTag = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_TAG);
        ab_friendName.setText(mFriendName);
        mEditEmojicon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setEventLayout(RegistrationConstants.SOFT_KEY);
            }
        });

//        updateUIFromCache(DatabaseContracts.IS_FALSE);
//        bindAdapter(chatMessageList);

        ImageView moreImage = (ImageView) actionBarView.findViewById(in.tagteen.tagteen.R.id.chat_more_button);
        moreImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_draware_menu));
        // ImageView eventImage = (ImageView) actionBarView.findViewById(in.tagteen.tagteen.R.id.imageView_event);
        imageView = (RoundedImageView) actionBarView.findViewById(in.tagteen.tagteen.R.id.chatViewImage_ab);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setCornerRadius(60);
        setTimerPicker();

        Glide.with(this).load(mFriendImage).fitCenter().into(imageView);
        slideLeft = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.anim_slide_in_left);
        rotationAnimation = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.rotate_normal);
        antiClockRotation = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.anti_clock_rottion);
        listView.setHasFixedSize(true);

        llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        listView.setLayoutManager(llm);

        listView.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                Log.e("inside_Scroll", "isLastPage");

            }
        });

        youthTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventLayout(RegistrationConstants.YOUTHTUBE);
            }
        });

        fileAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventLayout(RegistrationConstants.DOCUMENT);
            }
        });

        audio_mp_File.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEventLayout(RegistrationConstants.AUDIO_FILES);
            }
        });

        setPrivacyTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPrivetTimer == 0) {
                    setIsNotPrivateMode();
                    privateMsgTime = "0";
                    editTextTimerView.setVisibility(View.GONE);
                    privacyButton.setVisibility(View.VISIBLE);
                } else {
                    editTextTimerView.setVisibility(View.VISIBLE);
                    privacyButton.setVisibility(View.GONE);
                    privacyTimerLayout.setVisibility(View.GONE);
                }
            }
        });

        editTextTimerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPrivacyTimerLayoutVisible == false) {
                    privacyTimerLayout.setVisibility(View.VISIBLE);
                    isPrivacyTimerLayoutVisible = true;

                } else {
                    privacyTimerLayout.setVisibility(View.GONE);
                    isPrivacyTimerLayoutVisible = false;
                }
            }
        });

        emojiSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent it =new Intent(ChatTreadActivity.this,CustomCameraActivity.class);
                startActivity(it);*/
                setEventLayout(RegistrationConstants.EMOJI);
            }
        });

        audioAtt_File.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEventLayout(RegistrationConstants.AUDIO_RECODER);
            }
        });


        fileListOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSendButton == true) {
                    String msg = mEditEmojicon.getText().toString();
                    sendMessage(msg, "" + DatabaseContracts.ISTEXTMSG, null, mSocketReceiverId);
                    isSendButton = false;
                } else {
                    if (isAttachmentCloseButton == true) {
                        fileOptionList.setVisibility(View.GONE);
                        chatEditBoxLayout.setVisibility(View.VISIBLE);
                        isAttachmentCloseButton = false;
                        fileListOption.setRotation(0);
                        antiClockRotation.setDuration(200);
                        fileListOption.startAnimation(antiClockRotation);
                        mainViewFragment.setVisibility(View.GONE);
                    } else {
                        fileOptionList.setVisibility(View.VISIBLE);
                        chatEditBoxLayout.setVisibility(View.GONE);
                        hideSoftKeyboard();
                        slideLeft.setDuration(200);
                        fileOptionList.startAnimation(slideLeft);
                        isAttachmentCloseButton = true;
                        fileListOption.setRotation(135);
                        rotationAnimation.setDuration(200);
                        fileListOption.startAnimation(rotationAnimation);

                    }
                }
            }
        });

        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEventLayout(RegistrationConstants.PRIVATE);
            }
        });


        animation1.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                if (isPrivateMode == false) {
                    privacyButton.setImageResource(in.tagteen.tagteen.R.drawable.cmp_sandclock_gray);
                } else {
                    privacyButton.setImageResource(in.tagteen.tagteen.R.drawable.cmp_sandclock_blue);
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
            }
        });


        gallaryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventLayout(RegistrationConstants.IMAGE);
            }
        });

        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventLayout(RegistrationConstants.CAMERA);

            }
        });

        pingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendReactionAndPing(true, true, false, null, "10:30");
                String msg = mEditEmojicon.getText().toString();
                sendMessage(msg, "" + DatabaseContracts.ISPING, null, mSocketReceiverId);

            }
        });

        selfiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventLayout(RegistrationConstants.REACTION);

            }
        });


        moreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
                //backgroundWallPaper.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_draware)));
            }
        });

/*
        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacySettingDialog();

            }
        });
*/

        emojiBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = mEditEmojicon.getText().toString();
                if (str.length() >= 1) {
                    str = str.substring(0, str.length() - 2);
                    mEditEmojicon.setText(str);
                } else if (str.length() <= 1) {
                    mEditEmojicon.setText("0");
                }
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(in.tagteen.tagteen.R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Emojis"));
        tabLayout.addTab(tabLayout.newTab().setText("Stickers"));
        tabLayout.addTab(tabLayout.newTab().setText("Sound Emojis"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(in.tagteen.tagteen.R.id.pager);
        ChatEmojiAdapter adapter = new ChatEmojiAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        socketInit();
        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //position of the selected item
                privateMsgTime = "" + position;
                selectedPrivetTimer = position;
                editTextTimerView.setText(position + "");
                editTextTimerView.setVisibility(View.VISIBLE);
                privacyButton.setVisibility(View.GONE);
                hideSoftKeyboard();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        DBOpt.getInstance(ChatTreadActivity.this).resetUnreadCount(mSocketReceiverId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            TagTeenEventBus.registerEventBus(this, receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIFromCache(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TagTeenEventBus.removeEventBus(this, receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = TagTeenEventBus.getEventData(intent);
            ChatMessage chatMessage = TagTeenEventBus.getDataObj(intent);
            int eType = TagTeenEventBus.getEventType(intent);

            switch (eType) {

                case TagTeenEventBus.TYPE_TYPING:

                    ab_friendStatus.setText("typing..");
                    ab_friendStatus.setTextColor(Color.GREEN);

                    break;
                case TagTeenEventBus.TYPE_NEW_MESSAGE:

                    DBOpt.getInstance(ChatTreadActivity.this).resetUnreadCount(chatMessage.getFriendId());

                    chatMessageList.add(chatMessage);
                    chatAdapter.notifyDataSetChanged();

                    listView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    break;
                case TagTeenEventBus.TYPE_MESSAGE_SENT:
                    updateUIFromCache("");
                    break;
                case TagTeenEventBus.TYPE_MESSAGE_SEEN:
                    updateUIFromCache("");
                    break;

                case TagTeenEventBus.TYPE_MESSAGE_DELIVERED:

                    updateUIFromCache("");
                    break;

            }
        }
    };

    private boolean sendReadReport(JSONObject js, DBOpt dbOpt) {

        try {
            mSocket.emit(RegistrationConstants.SEEN_STATUS, js);
            dbOpt.updateChatValue(DBImpl.CL_TIME_READ, String.valueOf(System.currentTimeMillis()),
                    js.optString(SocketContracts.SERVER_CHAT_ID));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("requestCode", "" + requestCode);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedFileURI = data.getData();
                File file = new File(selectedFileURI.getPath().toString());
                Log.d("", "File : " + file.getName());
                String filePath = file.getName().toString();
                StringTokenizer tokens = new StringTokenizer(filePath, ":");
                Log.e("filed_dsk", "File : " + " " + file + "  " + tokens);
            }
        } else if (resultCode == 4) {

            String result = data.getStringExtra("ImagePath");
            sendMessage("", "" + DatabaseContracts.ISIMAGE, result, mSocketReceiverId);
            setEventLayout(RegistrationConstants.DEFAULT);

            // some stuff that will happen if there's no result
        }
    }


    @Override
    public void finish() {
        super.finish();
    }


    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEditEmojicon, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEditEmojicon);
    }


    public void openEmojiconsActivity(View view) {
    }


    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(in.tagteen.tagteen.R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();

    }

    public void openDialog() {
        /*dialog = new Dialog(ChatTreadActivity.this, R.style.MyCustomDrawer);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_chat_navigation);//R.layout.floating_dialog
        final Window layout = dialog.getWindow();
        layout.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        layout.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
        layout.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        layout.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_draware)));
       *//* myPic=(RoundedImageView) dialog.findViewById(R.id.fL_di_profile_pic);
        groupPic=(RoundedImageView) dialog.findViewById(R.id.fL_di_add_pic);
        addGroupIcon=(RoundedImageView) dialog.findViewById(R.id.fL_di_add_icon);
        TextView userName=(TextView) dialog.findViewById(R.id.fL_di_name);
        TextView usertag=(TextView) dialog.findViewById(R.id.fL_di_tag);

        fL_di_name*//*
        dialog.show();*/

        FullScreenDialog dialog = new FullScreenDialog();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialog.show(ft, FullScreenDialog.TAG);

    }

    public void openPassWordDialog() {
        dialog = new Dialog(ChatTreadActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.password_layout_dailog);
        final Window layout = dialog.getWindow();
        layout.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        layout.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
        layout.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        layout.setBackgroundDrawable(new ColorDrawable(getResources().getColor(in.tagteen.tagteen.R.color.pas_transparent)));
        textView = (TextView) dialog.findViewById(R.id.password_text);
        editPasswordView = (EditText) dialog.findViewById(R.id.password_edittext);
        editPasswordView.addTextChangedListener(passwordWatcher);
        editPasswordView.requestFocus();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }

    public void openForwordDialog() {
        forwordDialog = new Dialog(ChatTreadActivity.this);
        forwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        forwordDialog.setContentView(R.layout.forword_layout);
        final Window layout = forwordDialog.getWindow();
        layout.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        layout.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
        layout.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        layout.setBackgroundDrawable(new ColorDrawable(getResources().getColor(in.tagteen.tagteen.R.color.pas_transparent)));
        TextView sendForwordMsg = (TextView) forwordDialog.findViewById(R.id.send_forword_button);
        forwardRecyclerView = (RecyclerView) forwordDialog.findViewById(R.id.my_forword_chat_re);
        forwordAdapter = new ForwordAdapter(this, friends, new UserSelectionList() {
            @Override
            public void selectionList(List<Friend> userList) {
                SelectedFriend = userList;
            }

            @Override
            public void selectionList(ArrayList<UserModel> userList) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        forwardRecyclerView.setLayoutManager(linearLayoutManager);
        forwardRecyclerView.setAdapter(chatAdapter);
        updateUIFromCache();
        sendForwordMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedFriend.size() > 0)
                    sendForwordMessage();
            }
        });
        forwordDialog.show();
    }

    @Override
    public void clicked(ArrayList<String> imagePath, int requestNumber) {
        this.imagePaths = imagePath;
    }

    @Override
    public void imageList(List<String> image) {
        for (int i = 0; i < image.size(); i++) {
            String path = image.get(i);
            String msg = mEditEmojicon.getText().toString();
            sendMessage(msg, "" + DatabaseContracts.ISIMAGE, path, mSocketReceiverId);
            setEventLayout(RegistrationConstants.DEFAULT);
        }
    }

    @Override
    public void selfReaction(String image) {
        String msg = mEditEmojicon.getText().toString();
        sendMessage(msg, "" + DatabaseContracts.ISREACTION, image, mSocketReceiverId);
        setEventLayout(RegistrationConstants.DEFAULT);
    }

    @Override
    public void DocList(List<String> imageList) {

    }

    @Override
    public void AudioList(List<String> AudioList) {

    }

    @Override
    public void VideoList(List<String> videoList) {

    }

    @Override
    public void sendDoc(String path, String name, String size) {
        String msg = mEditEmojicon.getText().toString();
        sendMessage(msg, "" + DatabaseContracts.ISDOC, path, mSocketReceiverId);
    }


    @Override
    public void sendAudio(String path) {
        String msg = mEditEmojicon.getText().toString();
        sendMessage(msg, "" + DatabaseContracts.ISAUDIO, path, mSocketReceiverId);
        setEventLayout(RegistrationConstants.DEFAULT);
    }


    @Override
    public void clickedPick(String Image, boolean isClosed) {
        setEventLayout(RegistrationConstants.DEFAULT);
    }


    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }


    public void setTimerPicker() {

        textItems = new ArrayList<>();
        textItems.add(new CarouselPicker.TextItem("Off", 20));
        textItems.add(new CarouselPicker.TextItem("1s", 20));
        textItems.add(new CarouselPicker.TextItem("2s", 20));
        textItems.add(new CarouselPicker.TextItem("3s", 20));
        textItems.add(new CarouselPicker.TextItem("4s", 20));
        textItems.add(new CarouselPicker.TextItem("5s", 20));
        textItems.add(new CarouselPicker.TextItem("6s", 20));
        textItems.add(new CarouselPicker.TextItem("7s", 20));
        textItems.add(new CarouselPicker.TextItem("8s", 20));
        textItems.add(new CarouselPicker.TextItem("9s", 20));
        textItems.add(new CarouselPicker.TextItem("10s", 20));
        textAdapter = new CarouselPicker.CarouselViewAdapter(this, textItems, 0);
        carouselPicker.setAdapter(textAdapter);
        textAdapter.notifyDataSetChanged();
    }


    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = this.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {

            return contentUri.getPath();

        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }


    public void privacySettingDialog() {
        privacyDialog = new Dialog(this);
        privacyDialog.setContentView(in.tagteen.tagteen.R.layout.pricacy_custom_dialog);
        Switch privacyScreenShot = (Switch) privacyDialog.findViewById(in.tagteen.tagteen.R.id.ScreenShortSwitch);
        Switch privacyCopyAndPast = (Switch) privacyDialog.findViewById(in.tagteen.tagteen.R.id.noCopyPastSwitch);
        Switch privacyForward = (Switch) privacyDialog.findViewById(in.tagteen.tagteen.R.id.ScreenShortSwitch);
        Switch privacyPassword = (Switch) privacyDialog.findViewById(in.tagteen.tagteen.R.id.ScreenShortSwitch);

        privacyDialog.show();
        privacyScreenShot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                    } else {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    }
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });
    }

    @Override
    public void isPasswordMatched(boolean isPasswordMatched) {
        if (isPasswordMatched) {
            cameraLayoutView.setVisibility(View.GONE);
        }
    }

    @Override
    public void gallerySelectedImage(boolean send) {
        if (send && imagePaths.size() > 0) {
            for (int i = 0; i < imagePaths.size(); i++) {
                String path = imagePaths.get(i);
                sendMessage("", "" + DatabaseContracts.ISIMAGE, path, mSocketReceiverId);
                setEventLayout(RegistrationConstants.DEFAULT);
                // sendReactionAndPing(boolean isSender, boolean isPing,boolean isSelfReaction,String path,String time)
            }
        }
    }


    @Override
    public void cameraClickedImage(String imagePath) {
    }

    @Override
    public void audioMusicFile(String imagePath) {
    }

    @Override
    public void cameraClose(boolean isClicked) {
    }

    private void socketInit() {

        TagteenApplication tagteenApplication = (TagteenApplication) getApplication();
        mSocket = tagteenApplication.getSocket();
        mSocket.emit(RegistrationConstants.ADD_CHAT_USER, mSocketSenderId);
//        mSocket.on(RegistrationConstants.NEW_CHAT_MESSAGE, onNewMessage);
//        mSocket.on(RegistrationConstants.TYPING, onTyping);
        mSocket.on(RegistrationConstants.STOP_TYPING, onStopTyping);
        mSocket.on(RegistrationConstants.DISCONNECT, onDisconnect);
        mSocket.on(RegistrationConstants.USER_LEFT, onLeft);
        mSocket.on(RegistrationConstants.REMOVE_USRER, onRemoveUser);
        mSocket.on(RegistrationConstants.MSG_RECEIVE_SUCCESS, onReceiveSuccess);
//        mSocket.on(RegistrationConstants.SERVER_SENDER, onSendStatus);
//        mSocket.on(RegistrationConstants.SEEN_STATUS, onSeenStatus);
        mSocket.on(RegistrationConstants.NEW_EVENT, onNewEvent);
        mSocket.on(RegistrationConstants.EVENT_DELIVERED_STATUS, onEventDeleveredStatus);
    }

    private Emitter.Listener onNewEvent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject data = (JSONObject) args[0];
                        eventResponse(data.toString());
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
            runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONObject data = (JSONObject) args[0];
                        Log.e("Server_sender", data + "");
                        msgResponse(data.toString(), 1);

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONObject data = (JSONObject) args[0];
                        String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");

                       /* if (mSocketSenderId.equals(TypedReceiverId)) {*/
                        ab_friendStatus.setText("onLine");
                        ab_friendStatus.setTextColor(Color.GREEN);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");
                     /*   Log.e("Add_user",""+data);
                        if (mSocketSenderId.equals(TypedReceiverId)) {*/
                        ab_friendStatus.setText("onLine");
                        ab_friendStatus.setTextColor(Color.GREEN);
                   /*     }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


//    private Emitter.Listener onTyping = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//
//                        JSONObject data = (JSONObject) args[0];
//                        Log.e("onTyping", "" + data);
//                     /*   String TypedSenderId = data.getString("senderId");
//                        String TypedReceiverId = data.getString("mReceiverId");
//                        if (mSocketSenderId.equals(TypedReceiverId)) {*/
//                        ab_friendStatus.setText("typing..");
//                        ab_friendStatus.setTextColor(Color.GREEN);
//                        Log.e("onTyping", "" + data);
//                  /*      }*/
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//    };


    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                       /* String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");
                        if (mSocketSenderId.equals(TypedReceiverId)) {*/
                        ab_friendStatus.setText("onLine");
                        ab_friendStatus.setTextColor(Color.DKGRAY);
                        Log.e("onStopTyping", "" + data);
                        Toast.makeText(ChatTreadActivity.this, "onTyping : " + data, Toast.LENGTH_SHORT).show();
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
//                        msgResponse(data.toString(), 0);


                        try {
                            JSONObject js = new JSONObject();
                            JSONArray receiverId = new JSONArray();
                            receiverId.put(mSocketReceiverId);
                            js.put(SocketContracts.CHAT_CREATOR_ID, mSocketSenderId);
                            js.put(SocketContracts.CHAT_RECEIVER_ID, receiverId);
                            js.put(SocketContracts.SERVER_CHAT_ID, data.get("_id"));
                            mSocket.emit(RegistrationConstants.MSG_RECEIVE_SUCCESS, js);
//                            mSocket.emit(RegistrationConstants.SEEN_STATUS, js);

                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                        insertFriendMessageToDB(data.toString());

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];

                        updateDeliveredSeenStatus(data, DatabaseContracts.DELIVERED);
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");
                        if (mSocketSenderId.equals(TypedReceiverId)) {
                            ab_friendStatus.setText("offLine");
                            ab_friendStatus.setTextColor(Color.DKGRAY);
                        }
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String TypedSenderId = data.getString("senderId");
                        String TypedReceiverId = data.getString("mReceiverId");

                        if (mSocketSenderId.equals(TypedReceiverId)) {
                            ab_friendStatus.setText("offLine");
                            ab_friendStatus.setTextColor(Color.DKGRAY);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private boolean updateChatCache(String _Id, String chatId, String isViewed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.CHAT_ID, chatId);
        contentValues.put(DatabaseContracts.ChatContractDataBase.ISVIEWED, isViewed);
        dbase.update(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, contentValues, DatabaseContracts.ChatContractDataBase.CHAT_ID + "= ? ", new String[]{chatId});
        return true;
    }

    private boolean updateMyChat(String chatId, String serverChatId, String messageStatus, String date, String uploadStatus) {

        long id = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS, messageStatus);

        if (messageStatus.equals(DatabaseContracts.SENT)) {
            contentValues.put(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID, serverChatId);
            id = dbOpt.updateSendStatus(contentValues, chatId);
        } else {

            id = dbOpt.updateDeliveredStatus(contentValues, serverChatId);
        }
        return id > 0;

    }

    private boolean updateChat(String serverChatId, String chatId, String messageStatus, String date, String uploadStatus) {
        ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseContracts.ChatContractDataBase.CHAT_ID, chatId);
        contentValues.put(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID, serverChatId);
//        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS, uploadStatus);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS, messageStatus);


//        dbOpt.updateChat(contentValues, chatId);
        return true;
    }

    public void updateUIFromCache(String isPrivateMode) {

        chatMessageList = dbOpt.getChatDetail(mSocketReceiverId);
        bindAdapter(chatMessageList);
    }

    private void bindAdapter(ArrayList<ChatMessage> chatMessageList) {

        chatAdapter = new ChatThreadRecyclerViewAdapter(getApplicationContext(), mSocket, dbase, new CommonAdapterViewListner() {
            @Override
            public void getClickedRecyclerView(ChatThreadRecyclerViewAdapter.ViewHolder viewHolder, int position, ChatMessage chatMessage, int type) {
                if (type == 0) {
                    String isFromeMe = chatMessage.getIsFromMe();
                    if (isFromeMe.equals(DatabaseContracts.IS_TRUE)) {
                        likeChat.setVisibility(View.GONE);
                        agreeChat.setVisibility(View.GONE);
                        forwardChat.setVisibility(View.VISIBLE);
                        replyChat.setVisibility(View.GONE);
                        deleteChat.setVisibility(View.VISIBLE);
                        copyChat.setVisibility(View.VISIBLE);
                        reCallChat.setVisibility(View.VISIBLE);

                    } else {

                        likeChat.setVisibility(View.VISIBLE);
                        agreeChat.setVisibility(View.VISIBLE);
                        forwardChat.setVisibility(View.VISIBLE);
                        replyChat.setVisibility(View.VISIBLE);
                        deleteChat.setVisibility(View.VISIBLE);
                        copyChat.setVisibility(View.VISIBLE);
                        reCallChat.setVisibility(View.GONE);

                    }

                    eventLayout.setVisibility(View.VISIBLE);
                    selectedPosition = position;
                    selectedChatMessageObj = chatMessage;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            eventLayout.setVisibility(View.GONE);
                        }
                    }, 2000);

                } else {
                    String pathFile = chatMessage.getLocalPath();
                    String typeofMsg = chatMessage.getMsgType();
                    if (typeofMsg.equals(DatabaseContracts.ISIMAGE + "")) {
                        Intent it = new Intent(ChatTreadActivity.this, ImagePrivewActivity.class);
                        it.putExtra("selectedPath", chatMessage.getLocalPath());
                        if (chatMessage.getIsFromMe().equals(DatabaseContracts.IS_TRUE)) {
                            it.putExtra("userName", "you");

                        } else {

                            SharedPreferenceSingleton.getInstance().init(ChatTreadActivity.this);
                            String name = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_NAME);
                            it.putExtra("userName", name);

                        }
                        it.putExtra("uploadDate", chatMessage.getTime());
                        startActivity(it);
                    } else if (typeofMsg.equals(DatabaseContracts.ISVEDIO + "")) {
                        Intent it = new Intent(ChatTreadActivity.this, VideoPreviewActivity.class);
                        it.putExtra("selectedPath", chatMessage.getLocalPath());
                        startActivity(it);
                    } else if (typeofMsg.equals(DatabaseContracts.ISDOC + "")) {
                        if (pathFile != null || pathFile != "null" || pathFile != "") {
                            openFile(pathFile);
                        } else {

                        }
                    }


                }

            }
        }, chatMessageList);

        listView.scrollToPosition(chatAdapter.getItemCount() - 1);
        listView.setAdapter(chatAdapter);
    }

    public void sendMessage(String msg, String MsgType, String LocalPath, String receiverId) {

        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.UK);
        time = String.valueOf(System.currentTimeMillis());
        Log.e("oncklkd", "" + pageId);
        String chatId = null;
        String page = mSocketSenderId + receiverId;

        long id = InsertChatData(mSocketSenderId, mSocketReceiverId, "", msg, LocalPath, null,
                MsgType, time, isGroup, mIsPrivate, privateMsgTime, DatabaseContracts.PENDING);
        int message = Integer.parseInt(privateMsgTime);
        modelId.add("" + id);

        if (MsgType.equals("" + DatabaseContracts.ISTEXTMSG) || MsgType.equals("" + DatabaseContracts.ISREACTION) || MsgType.equals("" + DatabaseContracts.ISPING) || MsgType.equals("" + DatabaseContracts.SOUND_EMOJI)) {
            senderMessage(MsgType, LocalPath, msg, "" + id, mIsPrivate);
        } else {
            File file = new File(LocalPath);
            MediaUpload(file, this, MsgType, mSocketSenderId, "" + id);
        }
        mEditEmojicon.setText("");
        llm.scrollToPositionWithOffset(0, 0);
        listView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }


    public long InsertChatData(String senderId, String receiverId, String serverChatId, String msg, String localPath, String awsUrl,
                               String MsgType, String Time, String isGroup, String isPrivate, String privateMessageTime, String msgStatus) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.SENDER_ID, senderId);
        contentValues.put(DatabaseContracts.ChatContractDataBase.FRIEND_ID, receiverId);
        contentValues.put(DatabaseContracts.ChatContractDataBase.FRIEND_NAME, mFriendName);
        contentValues.put(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID, serverChatId);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE, msg);
        contentValues.put(DatabaseContracts.ChatContractDataBase.LOCATPATH, localPath);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_LINK, awsUrl);
        contentValues.put(DatabaseContracts.ChatContractDataBase.TIME, Time);
        contentValues.put(DatabaseContracts.ChatContractDataBase.ISPRIVATE_MSG, isPrivate);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS, DatabaseContracts.PENDING);
        contentValues.put(DatabaseContracts.ChatContractDataBase.THUMB_NAIL, "");
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_TYPE, MsgType);
        contentValues.put(DatabaseContracts.ChatContractDataBase.IS_GROUP, isGroup);
        contentValues.put(DatabaseContracts.ChatContractDataBase.DOWNLOAD_STATUS, DatabaseContracts.PENDING);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS, msgStatus);
        contentValues.put(DatabaseContracts.ChatContractDataBase.PRIVATE_MSG_TIME, privateMessageTime);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_HEIGHT, "");
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_WIDTH, "");
        contentValues.put(DatabaseContracts.ChatContractDataBase.EVENT_TYPE, DatabaseContracts.EVENT_NULL);

        addChatDataToList(contentValues);

        return dbOpt.insertChatData(contentValues);

    }


    private void senderMessage(String messageType, String path, String message, String clientId, String isPrivate) {
        String dd = null;

        try {
            if (isPrivate.equals(DatabaseContracts.IS_TRUE)) {
                isPrivate = "true";
            } else {
                isPrivate = "false";
            }

            JSONObject js = new JSONObject();
            JSONArray receiverId = new JSONArray();
            receiverId.put(mSocketReceiverId);
            js.put(SocketContracts.CHAT_CREATOR_ID, mSocketSenderId);
            js.put(SocketContracts.CHAT_RECEIVER_ID, receiverId);
            js.put(SocketContracts.CHAT_TYPE, messageType);
            js.put(SocketContracts.CLIENT_CHAT_ID, clientId);
            js.put(SocketContracts.IS_PRIVATE, isPrivate);
            js.put(SocketContracts.PRIVATE_MSG_TIMER, privateMsgTime);
            js.put(SocketContracts.CONTENT, message);
            js.put(SocketContracts.LOCATION, dd);
            js.put(SocketContracts.MEDIA_LINK, path);
            js.put(SocketContracts.DEVICE_TYPE, "android");
            js.put(SocketContracts.Chat_thumb, dd);
            js.put(SocketContracts.IS_GROUP, isGroup);
            js.put(SocketContracts.GROUP_ID, dd);
            Log.e("sendmessage", "" + js);
            mSocket.emit(RegistrationConstants.NEW_CHAT_MESSAGE, js, mSocketSenderId, mSocketReceiverId, "1");
//            if (messageType == "7") {
//                updateFriendList(mSocketReceiverId, "0", message);
//            } else {
//                updateFriendList(mSocketReceiverId, "0", path);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void insertFriendMessageToDB(String response) {


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
            contentValues.put(DatabaseContracts.ChatContractDataBase.SENDER_ID, creatorId);
            contentValues.put(DatabaseContracts.ChatContractDataBase.FRIEND_ID, creatorId);
            contentValues.put(DatabaseContracts.ChatContractDataBase.FRIEND_NAME, mFriendName);
            contentValues.put(DatabaseContracts.ChatContractDataBase.SERVER_CHAT_ID, serverChatId);
            contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE, message);
            contentValues.put(DatabaseContracts.ChatContractDataBase.LOCATPATH, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_LINK, mediaUrl);
            contentValues.put(DatabaseContracts.ChatContractDataBase.DATE, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.TIME, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.ISPRIVATE_MSG, isPrivate);
            contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS, DatabaseContracts.PENDING);
            contentValues.put(DatabaseContracts.ChatContractDataBase.THUMB_NAIL, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_TYPE, messageType);
            contentValues.put(DatabaseContracts.ChatContractDataBase.IS_GROUP, isGroup);
            contentValues.put(DatabaseContracts.ChatContractDataBase.DOWNLOAD_STATUS, DatabaseContracts.PENDING);
            contentValues.put(DatabaseContracts.ChatContractDataBase.MESSAGE_STATUS, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.PRIVATE_MSG_TIME, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_HEIGHT, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_WIDTH, "");
            contentValues.put(DatabaseContracts.ChatContractDataBase.EVENT_TYPE, DatabaseContracts.EVENT_NULL);

            addChatDataToList(contentValues);

            dbOpt.insertChatData(contentValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void msgResponse(String Response, int type) {
        try {
            JSONObject data = new JSONObject(Response);
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
            if (type == 0) {
                SharedPreferenceSingleton.getInstance().init(this);
                if (pageId != chatPageId) {
                    int i = SharedPreferenceSingleton.getInstance().getIntPreference(DatabaseContracts.CHAT_OF + chatPageId);
                    SharedPreferenceSingleton.getInstance().writeIntPreference(DatabaseContracts.CHAT_OF + chatPageId, i + 1);
                }

                if (mSocketReceiverId.equals(creatorId)) {

                    String time = String.valueOf(System.currentTimeMillis());
                    long id = InsertChatData(creatorId, receiverId, message, null, mediaUrl, messageType, DatabaseContracts.IS_FALSE, time, isGroup, isPrivate, privateMessageTimer, DatabaseContracts.DELIVERED);
                    modelId.add("" + id);
                    if (isPrivate.equals(mIsPrivate)) {
                        chatAdapter.add(new ChatMessage(isFromMe, "" + id, creatorId, receiverId, messageType, message, mediaUrl, time,
                                chatPageId, isGroup, isPrivate, msgTimer, DatabaseContracts.DELIVERED, serverChatId, "", setTime(date)));
                        llm.scrollToPositionWithOffset(0, 0);
                        listView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    }

                    mSocket.emit(RegistrationConstants.MSG_RECEIVE_SUCCESS, messageArray, mSocketSenderId);

                    if (messageType.equals(DatabaseContracts.ISIMAGE + "") || messageType.equals(DatabaseContracts.ISVEDIO + "") || messageType.equals(DatabaseContracts.ISDOC + "") || messageType.equals(DatabaseContracts.ISAUDIO + "")) {
                        MediaDownload(mediaUrl, "" + id);

                        if (messageType.equals(IS_TEXT_MSG)) {
                            updateFriendList(receiverId, date, message);
                        } else {
                            updateFriendList(receiverId, date, mediaUrl);
                        }
                    }
                }
            } else if (type == MESSAGE_SENT) {

                updateMyChat(clientChatId, serverChatId, DatabaseContracts.SENT, date, DatabaseContracts.IS_TRUE);
                updateUIFromCache(null);
                chatAdapter.notifyDataSetChanged();


//                if (mSocketReceiverId.equals(receiverId) && isPrivate.equals(mIsPrivate)) {
//                    updateUIMessageStatus(clientChatId, DatabaseContracts.SENT, date, STATUS_CHANGE_SENT, "");
//                }
//                if (messageType == IS_TEXT_MSG && isPrivate.equals(DatabaseContracts.IS_FALSE)) {
//                    updateFriendList(mSocketReceiverId, date, message);
//                } else {
//                    updateFriendList(mSocketReceiverId, date, mediaUrl);
//                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void updateDeliveredSeenStatus(JSONObject data, String messageStatus) {

        String creatorId = data.optString(SocketContracts.CHAT_CREATOR_ID);
        String receiverId = data.optString(SocketContracts.CHAT_RECEIVER_ID);
        String serverChatId = data.optString(SocketContracts.SERVER_CHAT_ID);

        updateMyChat("", serverChatId, messageStatus, "", DatabaseContracts.IS_TRUE);


        updateUIFromCache(null);
        chatAdapter.notifyDataSetChanged();
    }

    public void setEventLayout(int event) {

        if (event == RegistrationConstants.DEFAULT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().show();
            mainViewFragment.setVisibility(View.GONE);
            opendEvent = RegistrationConstants.DEFAULT;
            cameraLayoutView.setVisibility(View.GONE);
            mainViewFragment.setVisibility(View.GONE);
            hideSoftKeyboard();
            privacyTimerLayout.setVisibility(View.GONE);
        } else if (event == RegistrationConstants.IMAGE) {
            getFragment(new ChatGalleryFragment());
            opendEvent = RegistrationConstants.IMAGE;
            hideSoftKeyboard();
            privacyTimerLayout.setVisibility(View.GONE);
            isFragmentOpened = true;
        } else if (event == RegistrationConstants.CAMERA) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            cameraLayoutView.setVisibility(View.VISIBLE);
            getCameraFragment(new SelfiFragment());
            opendEvent = RegistrationConstants.CAMERA;
            hideSoftKeyboard();
            privacyTimerLayout.setVisibility(View.GONE);
        } else if (event == RegistrationConstants.REACTION) {
            getFragment(new SelfReactionFragment());
            opendEvent = RegistrationConstants.REACTION;
            hideSoftKeyboard();
            privacyTimerLayout.setVisibility(View.GONE);
            isFragmentOpened = true;
        } else if (event == RegistrationConstants.AUDIO_RECODER) {

            getFragment(new AudioRecording());
            opendEvent = RegistrationConstants.AUDIO_RECODER;
            hideSoftKeyboard();
            privacyTimerLayout.setVisibility(View.GONE);
            isFragmentOpened = true;

        } else if (event == RegistrationConstants.AUDIO_FILES) {

            getFragment(new AudioFileFragment());
            opendEvent = RegistrationConstants.AUDIO_FILES;
            hideSoftKeyboard();
            privacyTimerLayout.setVisibility(View.GONE);
            isFragmentOpened = true;

        } else if (event == RegistrationConstants.DOCUMENT) {

            getFragment(new FilePickerActivity());
            opendEvent = RegistrationConstants.DOCUMENT;
            hideSoftKeyboard();
            privacyTimerLayout.setVisibility(View.GONE);
            isFragmentOpened = true;

        } else if (event == RegistrationConstants.YOUTHTUBE) {

            Intent it = new Intent(ChatTreadActivity.this, DetailActivity.class);
            startActivity(it);
            opendEvent = RegistrationConstants.YOUTHTUBE;
            hideSoftKeyboard();
            privacyTimerLayout.setVisibility(View.GONE);

        } else if (event == RegistrationConstants.EMOJI) {
            hideSoftKeyboard();
            getFragment(new EmojiconGridFragment());
            opendEvent = RegistrationConstants.EMOJI;
            privacyTimerLayout.setVisibility(View.GONE);
            isFragmentOpened = true;
        } else if (event == RegistrationConstants.PRIVATE) {
            onPrivateMode();
            opendEvent = RegistrationConstants.PRIVATE;
            mainViewFragment.setVisibility(View.GONE);
            hideSoftKeyboard();
        } else if (event == RegistrationConstants.SOFT_KEY) {

            opendEvent = RegistrationConstants.SOFT_KEY;
            mainViewFragment.setVisibility(View.GONE);
            privacyTimerLayout.setVisibility(View.GONE);
            showSoftKeyboard(mainViewFragment);

        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    private void getFragment(Fragment targetFragment) {
        mainViewFragment.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(in.tagteen.tagteen.R.id.main_view_fragment, targetFragment);
        transaction.commit();
    }

    public void getCameraFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(in.tagteen.tagteen.R.id.camera_fragment_layout, targetFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (opendEvent == RegistrationConstants.DEFAULT) {

            mSocket.emit(RegistrationConstants.REMOVE_USRER, sendIdObject);
            mSocket.emit(RegistrationConstants.DISCONNECT, sendIdObject);
        } else {
            setEventLayout(RegistrationConstants.DEFAULT);
        }

        super.onBackPressed();
    }

    public void onPrivateMode() {
        if (isPrivateMode == false) {
            setIsPrivateMode();
        } else {

            setIsNotPrivateMode();
        }
    }

    public void setIsNotPrivateMode() {
        backgroundWallPaper.setBackgroundResource(0);
        // bottom_area.setBackgroundColor(Color.WHITE);
        animation1.setDuration(100);
        privacyButton.startAnimation(animation1);
        isPrivateMode = false;
        mIsPrivate = DatabaseContracts.IS_FALSE;
        privacyTimerLayout.setVisibility(View.GONE);
        isPrivacyTimerLayoutVisible = false;
        updateUIFromCache(DatabaseContracts.IS_FALSE);
        chatAdapter.notifyDataSetChanged();
    }

    public void setIsPrivateMode() {
        setTimerPicker();
        backgroundWallPaper.setBackgroundResource(R.drawable.private_chat_wallpaper);
        //   bottom_area.setBackgroundColor(Color.BLACK);
        animation1.setDuration(100);
        privacyButton.startAnimation(animation1);
        mIsPrivate = DatabaseContracts.IS_TRUE;
        isPrivateMode = true;
        privacyTimerLayout.setVisibility(View.VISIBLE);
        isPrivacyTimerLayoutVisible = true;
        updateUIFromCache(DatabaseContracts.IS_TRUE);
        chatAdapter.notifyDataSetChanged();
    }

    public void deleteSingleRow(String rowId) {
        dbase.delete(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, DatabaseContracts.ChatContractDataBase._ID + "=?", new String[]{rowId});
    }

    public void deleteUserRow(String chatPage) {
        dbase.delete(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, DatabaseContracts.ChatContractDataBase.CHAT_PAGE_ID + "=?", new String[]{chatPage});
    }

    public void eventMessage(String EventType, String chatId) {
        try {
            JSONObject js = new JSONObject();
            JSONArray receiverId = new JSONArray();
            receiverId.put(mSocketReceiverId);
            js.put(SocketContracts.CHAT_CREATOR_ID, mSocketSenderId);
            js.put(SocketContracts.CHAT_RECEIVER_ID, receiverId);
            js.put(SocketContracts.EVENT_TYPE, "" + EventType);
            js.put(SocketContracts.SERVER_CHAT_ID, chatId);
            js.put(SocketContracts.MEDIA_LINK, "");
            js.put(SocketContracts.CONTENT, "Hello for Check");
            js.put(SocketContracts.CHAT_TYPE, DatabaseContracts.ISTEXTMSG);
            mSocket.emit(RegistrationConstants.NEW_EVENT, js);
            updateEventType(chatId, EventType);
            updateEventStatus(chatId, EventType);
        } catch (Exception e) {
        }
    }

    private void updateUIMessageStatus(String id, String sendStatus, String date, int typeStatus, String imagePath) {
        int retval = modelId.indexOf(id);
        ChatMessage chatMessage = chatAdapter.chatMessageList.get(retval);
        if (typeStatus == STATUS_CHANGE_UPLOADED) {
            chatMessage.setUpLoadStatus(DatabaseContracts.IS_TRUE);
            senderMessage(chatMessage.getMsgType(), imagePath, chatMessage.getMessage(), chatMessage.getClintId(), chatMessage.getIsPrivate());
        } else if (typeStatus == STATUS_CHANGE_DOWNLOADED) {
            chatMessage.setSendStatus(sendStatus);
            chatMessage.setDate(setTime(date));
            chatMessage.setDownLoadStatus(DatabaseContracts.IS_TRUE);
        } else {
            chatMessage.setSendStatus(sendStatus);
            chatMessage.setDate(setTime(date));
            chatMessage.setUpLoadStatus(DatabaseContracts.IS_TRUE);
            chatMessage.setMsgViewedStatus(DatabaseContracts.IS_TRUE);
        }
        chatAdapter.chatMessageList.set(retval, chatMessage);
        chatAdapter.notifyDataSetChanged();
        listView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    public void MediaUpload(File file, Context context, final String msgType, String userId, String dbId) {

        UpLoadFile upload = new UpLoadFile();
        upload.uploadChatFiles(file, dbId, this, msgType, userId, new MediaUploadDone() {
            @Override
            public void mediaUploadonProgress(String clientdbId, int persent) {

            }

            @Override
            public void mediaUploadDone(String msgType, String localPath, String AWSPath, String id) {
                updateUIMessageStatus(id, "", "", STATUS_CHANGE_UPLOADED, AWSPath);
            }

            @Override
            public void mediaUploadError(String clientdbId) {

            }
        });
    }


    public void MediaDownload(String path, String Id) {

        SaveMediaCash saveMediaCash = new SaveMediaCash(this, new DownLoadInterface() {
            @Override
            public void mediaDownLoadProgress(int persent) {
            }

            @Override
            public void mediaDownloadDone(String id, String localePath) {
                updateUIMessageStatus(id, "", "", STATUS_CHANGE_DOWNLOADED, localePath);
            }
        }, dbase);
        saveMediaCash.execute(path, Id);
    }

    public void openFile(String url) {

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (url.contains(".doc") || url.contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.contains(".ppt") || url.contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.contains(".xls") || url.contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.contains(".zip") || url.contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.contains(".wav") || url.contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {

        } else if (url.contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {


        } else {
            intent.setDataAndType(uri, "*/*");
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }

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

    private boolean updateFriendList(String userId, String date, String lastMessage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.userLisTable.lAST_MESSAGE_DATE, date);
        contentValues.put(DatabaseContracts.userLisTable.lAST_MESSAGE, lastMessage);
        contentValues.put(DatabaseContracts.userLisTable.IS_CHAT_STARTED, DatabaseContracts.IS_TRUE);
        dbase.update(DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, contentValues, DatabaseContracts.userLisTable.USERID + "= ? ", new String[]{userId});
        return true;
    }

    public void updateUIFromCache() {
        friends.clear();
        String[] parameter = {};
        Cursor res = dbase.rawQuery("select * from " + DatabaseContracts.userLisTable.FRIEND_LIST_TABLE, parameter);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            Friend friend = new Friend();
            friend.setImg(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.PROFILE_IMAGE)));
            friend.setName(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.USERNAME)));
            friend.setTag(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.TAG_NUMBER)));
            friend.setId(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.USERID)));
            friend.setIsGroup(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.IS_GROUP)));
            friend.setLastMessage(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.lAST_MESSAGE)));
            String date = res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.lAST_MESSAGE_DATE));
            if (date == null || date == "null" || date == "") {
                friend.setLastMsgDate("");
            } else {
                friend.setLastMsgDate(setTime(date));
            }
            friend.setLastMsgTime(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.lAST_MESSAGE_TIME)));
            friend.setChatCount(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.CHAT_COUNT)));
            friend.setMsgStatus(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.LAST_MESSAGE_STATUS)));
            friend.setIsLocked(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.IS_LOCKED)));
            friend.setIsMute(res.getString(res.getColumnIndex(DatabaseContracts.userLisTable.IS_MUTE)));
            friends.add(friend);
            res.moveToNext();
        }
        res.close();
        forwardRecyclerView.setAdapter(forwordAdapter);
        forwordAdapter.notifyDataSetChanged();
    }

    private void sendForwordMessage() {
        String id = null;

        for (int i = 0; i < SelectedFriend.size(); i++) {
            Friend userModel = SelectedFriend.get(i);
            sendMessage(selectedChatMessageObj.getMessage(), selectedChatMessageObj.getMsgType(), selectedChatMessageObj.getLocalPath(), userModel.getId());
        }
        forwordDialog.dismiss();
    }

    public void eventResponse(String Response) {
        JSONObject data = null;
        try {
            data = new JSONObject(Response);
            String creatorId = data.getString(SocketContracts.CHAT_CREATOR_ID);
            String receiverId = data.getString(SocketContracts.CHAT_RECEIVER_ID);
            String chatId = data.getString(SocketContracts.SERVER_CHAT_ID);
            String eventTypeId = data.getString(SocketContracts.EVENT_TYPE);
            String mediaLink = data.getString(SocketContracts.MEDIA_LINK);
            String content = data.getString(SocketContracts.CONTENT);
            String chatTypeId = data.getString(SocketContracts.CHAT_TYPE);
            String id = data.getString(SocketContracts._ID);
            if (eventTypeId.equals(DatabaseContracts.REPLY_EVENT)) {

            } else if (eventTypeId.equals(DatabaseContracts.EVENT_RECALL)) {
                dbase.delete(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, DatabaseContracts.ChatContractDataBase.CHAT_ID + "=?", new String[]{chatId});
                updateEventStatus(chatId, eventTypeId);
            } else {
                updateEventStatus(chatId, eventTypeId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateEventStatus(String chatId, String eventTypeId) {
        int j = chatAdapter.chatMessageList.size();
        int pos;
        for (int i = 0; i < j; i++) {
            ChatMessage chatMessage = chatAdapter.chatMessageList.get(i);
            String id = chatMessage.getServerId();
            if (chatId.equals(id)) {
                if (eventTypeId.equals(DatabaseContracts.EVENT_RECALL)) {
                    chatAdapter.chatMessageList.remove(i);
                    chatAdapter.notifyDataSetChanged();
                } else {
                    chatMessage.setEventType(eventTypeId);
                    chatAdapter.chatMessageList.set(i, chatMessage);
                    chatAdapter.notifyDataSetChanged();
                    listView.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }
        }
    }

    private boolean updateEventType(String chatId, String eventType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.EVENT_TYPE, eventType);
        dbase.update(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, contentValues, DatabaseContracts.ChatContractDataBase.CHAT_ID + "= ? ", new String[]{chatId});
        return true;
    }

    private void addChatDataToList(ContentValues contentValues) {

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

        chatMessageList.add(chatMessage);
        chatAdapter.notifyDataSetChanged();

        Log.d("chat size", "" + chatMessageList.size());
    }
}