package in.tagteen.tagteen.chatting;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import in.tagteen.tagteen.chatting.data.Tools;
import in.tagteen.tagteen.chatting.emoji.EmojiKeyboardManager;
import in.tagteen.tagteen.chatting.emoji.EmojiKeyboardManagerEventListener;
import in.tagteen.tagteen.chatting.emoji.EmojiSelectListener;
import in.tagteen.tagteen.chatting.emoji.SoftKeyboardChangedListener;
import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.model.NewMessage;
import in.tagteen.tagteen.chatting.model.TypingStatus;
import in.tagteen.tagteen.chatting.paging.MessageDataSource;
import in.tagteen.tagteen.chatting.paging.MessageLoadedListener;
import in.tagteen.tagteen.chatting.paging.PagingRecyclerView;
import in.tagteen.tagteen.chatting.room.DBQueryHandler;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.socket.SocketConnection;
import in.tagteen.tagteen.chatting.socket.SocketConstants;
import in.tagteen.tagteen.chatting.socket.SocketEventListener;
import in.tagteen.tagteen.chatting.utils.ChatFactory;
import in.tagteen.tagteen.chatting.utils.ScreenTracker;
import in.tagteen.tagteen.chatting.widget.TypingEditText;

public class ActivityChatDetails extends AppCompatActivity
    implements SocketEventListener,
    EmojiKeyboardManagerEventListener,
    EmojiSelectListener,
    SoftKeyboardChangedListener,
    MessageLoadedListener {

  public static final String KEY_FRIEND = ActivityChatDetails.class.getCanonicalName() + ".FRIEND";
  public static final String KEY_NEW_MESSAGES =
      ActivityChatDetails.class.getCanonicalName() + ".NEW_MESSAGES";

  private ImageButton send;
  private PagingRecyclerView recyclerView;
  private TypingEditText txtEditor;
  private SocketConnection socketConn;
  private Friend friend;
  private RelativeLayout newMessageIndicator;
  private TextView messageCount;
  private int mCount = 0;
  private EmojiKeyboardManager emojiKeyboardManager;
  private MessageDataSource mMessageDataSource;
  private LiveData<Message> mMessageLiveData;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    socketConn = SocketConnection.getConnection();

    LinearLayout parent =
        (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_chat_details, null);

    txtEditor = parent.findViewById(R.id.text_content);
    emojiKeyboardManager = EmojiKeyboardManager.from(this,
        parent,
        (ImageView) parent.findViewById(R.id.soundEmoji),
        txtEditor);
    emojiKeyboardManager.setEmojiKeyboardManagerEventListener(this);
    emojiKeyboardManager.setEmojiSelectListener(this);
    emojiKeyboardManager.setSoftKeyboardChangedListener(this);

    setContentView(emojiKeyboardManager.getRootView());

    //        View parent_view = findViewById(android.R.id.content);

    //        ViewCompat.setTransitionName(parent_view, KEY_FRIEND);

    configToolbar();
    initComponent();
    configRecycleView();

    initConversation((Friend) getIntent().getSerializableExtra(KEY_FRIEND));

    Tools.systemBarLolipop(this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (intent != null) {
      Friend newFriend = (Friend) intent.getSerializableExtra(KEY_FRIEND);
      if (!friend.getId().equals(newFriend.getId())) {
        recyclerView.reset();
        initConversation(newFriend);
        bindReceiver();
        socketConn.makeAllMessagesSeen();
      }
    }
  }

  private void initConversation(Friend friend) {
    this.friend = friend;
    configTitle();
    initRecycleView();
    //        socketConn.bindReceiver(friend.getId());
  }

  private void configRecycleView() {
    recyclerView.requestFocus();
    recyclerView.setOnScrollDownListener(this::onScrollToEnd);
    recyclerView.setOnMessageLoadedListener(this);
    registerForContextMenu(recyclerView);
  }

  private void initRecycleView() {
    mMessageDataSource = MessageDataSource.createInstanceOf(this, friend.getId());
    mMessageLiveData = DBQueryHandler.getMessagesObserver();
    mMessageLiveData.observe(this, message -> {
      mMessageDataSource.onMessageTableUpdated(message);
    });
    recyclerView.setDataSource(mMessageDataSource);
    recyclerView.requestFocus();
  }

  private void configToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }
  }

  private void configTitle() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle("");
      ((TextView) findViewById(R.id.toolbarTitleText)).setText(friend.getName());
      //            ((TextView) findViewById(R.id.toolbarTitle)).setText(friend.getName());
    }
  }

  public void initComponent() {
    recyclerView = findViewById(R.id.listview);
    send = findViewById(R.id.btn_send);
    send.setOnClickListener(this::onSendClicked);
    newMessageIndicator = findViewById(R.id.new_message_indicator);
    messageCount = findViewById(R.id.message_count);
    txtEditor.setOnTypingAnalyser(this::onTypingStatusChanged);
    ChatFactory.hideKeyboard(this, getCurrentFocus());

    newMessageIndicator.setOnClickListener(listenerNewMessage);
  }

  @Override
  protected void onStart() {
    super.onStart();
    bindReceiver();
    socketConn.registerSocketEventHandler(this);
    ScreenTracker.getInstance().registerScreen(ActivityChatDetails.class.getSimpleName());
  }

  private void bindReceiver() {
    socketConn.bindReceiver(friend.getId());
  }

  @Override
  protected void onResume() {
    super.onResume();
    socketConn.makeAllMessagesSeen();
    ScreenTracker.getInstance().unRegisterScreen(ActivityChatDetails.class.getSimpleName());
  }

  @Override
  protected void onStop() {
    super.onStop();
    socketConn.unbindReceiver();
    socketConn.emitTypingStatus(false);
    socketConn.unregisterSocketEventHandler(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //        socketConn.unbindReceiver();
  }

  //    @Override
  //    public boolean onCreateOptionsMenu(Menu menu) {
  //        getMenuInflater().inflate(R.menu.menu_chat_details, menu);
  //        return true;
  //    }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      //            case R.id.action_sample:
      //                Snackbar.make(parent_view, item.getTitle() + " Clicked ", Snackbar.LENGTH_SHORT).show();
      //                return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void onSendClicked(View view) {
    if (!SocketConnection.getConnection().isSocketConnected()) {
      View errorStrip = findViewById(R.id.networkStatusBackground);
      if (errorStrip.getVisibility() != View.VISIBLE) {
        errorStrip.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> errorStrip.setVisibility(View.GONE), 1000);
      }
      return;
    }

    if (!TextUtils.isEmpty(txtEditor.getText())) {
      NewMessage newMessage = new NewMessage();
      newMessage.setReceiverId(friend.getId());
      newMessage.setMessage(txtEditor.getText().toString().trim());
      newMessage.setDate(ChatFactory.getFormattedDate(System.currentTimeMillis()));
      newMessage.setChatType(SocketConstants.MessageType.TEXT);
      //            newMessage.setLocation("13.452746,80.373463");
      socketConn.emitMessage(newMessage);
      txtEditor.setText("");
    }
  }

  public void onTypingStatusChanged(boolean isTyping) {
    socketConn.emitTypingStatus(isTyping);
  }

  @Override
  public void onSocketConnectionChanged(boolean isConnected) {
    if (isConnected) {
      bindReceiver();
      socketConn.makeAllMessagesSeen();
    }
  }

  @Override
  public void onTypingStatusChanged(@NonNull TypingStatus typing) {
    if (typing.isTyping()) {
      ((TextView) findViewById(R.id.toolbarTitleText)).setText(getModifiedTitle("typing..."));
    } else {
      ((TextView) findViewById(R.id.toolbarTitleText)).setText(friend.getName());
    }
  }

  @Override
  public void onMessageStatusChanged(@NonNull MessageStatus msgStatus,
      @Nullable String... messageIds) {
    if (messageIds != null && messageIds.length > 1) {
      recyclerView.getAdapter().updateMessage(msgStatus, messageIds);
    }
  }

  public void onScrollToEnd() {
    if (newMessageIndicator.getVisibility() == View.VISIBLE) {
      newMessageIndicator.setVisibility(View.GONE);
      messageCount.setText("0");
      mCount = 0;
    }
  }

  private View.OnClickListener listenerNewMessage = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      recyclerView.scrollToEnd();
      newMessageIndicator.setVisibility(View.GONE);
      messageCount.setText("0");
      mCount = 0;
    }
  };

  @Override
  public void onEmojiIconClick(View view) {
    //TODO : No Implementation
  }

  @Override
  public void onEditTextClick(View view) {
    //        int scrollPos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
    //        recyclerView.addOnLayoutChangeListener((v1, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
    //            if (bottom < oldBottom) {
    //                recyclerView.postDelayed(() ->
    //                        recyclerView.scrollToPosition(scrollPos), 100);
    //            }
    //        });
  }

  @Override
  public void onEmojiSelect(int emojiId) {
    NewMessage newMessage = new NewMessage();
    newMessage.setReceiverId(friend.getId());
    newMessage.setMessage(Integer.toString(emojiId));
    newMessage.setDate(ChatFactory.getFormattedDate(System.currentTimeMillis()));
    newMessage.setChatType(SocketConstants.MessageType.SOUND_EMOJI);
    //        newMessage.setLocation("13.452746,80.373463");
    socketConn.emitMessage(newMessage);
  }

  @Override
  public void onKeyboardOpen(int keyboardHeight) {
    //        int scrollPos = recyclerView.getLastVisibleItemPosition();
    //        recyclerView.addOnLayoutChangeListener((v1, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
    //            if (bottom < oldBottom) {
    //                recyclerView.postDelayed(() ->
    //                        recyclerView.scrollToPosition(scrollPos), 100);
    //            }
    //        });
    //        InputMethodManager imm = (InputMethodManager) getApplicationContext()
    //                .getSystemService(Context.INPUT_METHOD_SERVICE);
    //        if (imm.isActive()) {
    //                    RelativeLayout.LayoutParams pms = new RelativeLayout.LayoutParams(
    //                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    //                    pms.addRule(RelativeLayout.ABOVE, findViewById(R.id.input_bar).getId());
    //                    recyclerView.setLayoutParams(pms);
    //        }
  }

  @Override
  public void onKeyboardClose() {

  }

  @Override
  public void onBackPressed() {
    if (emojiKeyboardManager.isEmojiPoppedUp()) {
      emojiKeyboardManager.hideEmojiKeyboard();
    } else {
      super.onBackPressed();
    }

    //        else if (ChatFactory.isActivityAlive( this)) {
    //            super.onBackPressed();
    //        } else {
    //            Intent intent = new Intent(this, ActivityChat.class);
    //            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    //            startActivity(intent);
    //        }
  }

  @Override
  public void onNewMessage(@NonNull Message message, boolean isDrafted) {
    if (message.getMessageType() == Message.MESSAGE_IN
        && !recyclerView.isLastItemVisible()
        && !isDrafted) {
      newMessageIndicator.setVisibility(View.VISIBLE);
      messageCount.setText(Integer.toString(++mCount));
    }
  }

  private CharSequence getModifiedTitle(String subText) {
    int pos = 0;
    SpannableStringBuilder builder = new SpannableStringBuilder(friend.getName());
    pos = builder.length();

    builder.setSpan(new AbsoluteSizeSpan((int) ChatFactory.toDp(this, 20)),
        0,
        pos,
        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);

    builder.setSpan(new StyleSpan(Typeface.BOLD),
        0,
        pos,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    builder.append("\n").append(subText);

    builder.setSpan(new AbsoluteSizeSpan((int) ChatFactory.toDp(this, 14)),
        pos,
        builder.length(),
        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);

    builder.setSpan(new StyleSpan(Typeface.BOLD),
        pos,
        builder.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    return builder;
  }
}

