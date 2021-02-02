package in.tagteen.tagteen.Adapters;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.ChatTreadActivity;
import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.Model.ChatMessage;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.CommonAdapterViewListner;
import in.tagteen.tagteen.TagteenInterface.TimerEventListener;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.emoji.EmojiconTextView;
import in.tagteen.tagteen.util.CountDownManager;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import io.socket.client.Socket;
import tyrantgit.explosionfield.ExplosionField;


public class ChatThreadRecyclerViewAdapter extends RecyclerView.Adapter<ChatThreadRecyclerViewAdapter.ViewHolder> implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private SeekBar playSeekBar;
    private ImageView viewPlay;
    public static List<ChatMessage> chatMessageList = new ArrayList<>();
    private String friendName, friendImage, mSocketSenderId, mSocketReceiverId, senderImage, ownerId;
    private Context context;
    private boolean flag_play;
    private MediaPlayer mPlayer = null;
    ViewHolder holder;
    private Handler seekHandler = new Handler();
    private TextView audioPlayerTimer;
    //    String isSender;
    SQLiteDatabase db;
    int lastPosition;
    File mCapturedImageFile;
    private CommonAdapterViewListner commonAdapterViewListner;
    private Integer[] mThumbIds = {android.R.drawable.ic_media_play, android.R.drawable.ic_media_pause};
    LinearLayout layout;
    private Socket mSocket;
    ExplosionField explosionField;

    public ChatThreadRecyclerViewAdapter(Context context, Socket mSocket, SQLiteDatabase db, CommonAdapterViewListner commonAdapterViewListner, List<ChatMessage> chatMessageList) {
        this.context = context;
        this.mSocket = mSocket;
        this.db = db;
        mPlayer = new MediaPlayer();
        this.commonAdapterViewListner = commonAdapterViewListner;
        ChatThreadRecyclerViewAdapter.chatMessageList = chatMessageList;

        SharedPreferenceSingleton.getInstance().init(context);

        ownerId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        mSocketSenderId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        mSocketReceiverId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_ID);
        senderImage = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.PROFILE_URL);
        friendName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_NAME);
        friendImage = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FRIEND_IMAGE);

    }

    public ChatMessage getItem(int index) {
        return chatMessageList.get(index);
    }

    @Override
    public ChatThreadRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_thread_iteam, null);
        in.tagteen.tagteen.Adapters.ChatThreadRecyclerViewAdapter.ViewHolder mh = new in.tagteen.tagteen.Adapters.ChatThreadRecyclerViewAdapter.ViewHolder(v);
        return mh;
    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }

    @Override
    public void onBindViewHolder(final ChatThreadRecyclerViewAdapter.ViewHolder viewHolder, final int position) {
        final ChatMessage chatMessageObj = getItem(position);

        viewHolder.bindData(chatMessageObj);

        if (chatMessageObj.getCreaterId().equals(ownerId)) {
            viewHolder.senderContainer.setVisibility(View.VISIBLE);
            viewHolder.receiverContainer.setVisibility(View.GONE);
            viewHolder.receiverProgressBar.setMax(100);
            senderView(viewHolder, chatMessageObj);
        } else {
            viewHolder.senderContainer.setVisibility(View.GONE);
            viewHolder.receiverContainer.setVisibility(View.VISIBLE);
            viewHolder.receiverProgressBar.setMax(100);
            receiverView(viewHolder, chatMessageObj);
        }
        if (chatMessageObj.getEventType() == null) {
            viewHolder.eventImageView.setVisibility(View.GONE);
        } else if (chatMessageObj.getEventType().equals(DatabaseContracts.EVENT_LIKE)) {
            viewHolder.eventImageView.setVisibility(View.VISIBLE);
            viewHolder.eventImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cmp_like_icon));
        } else if (chatMessageObj.getEventType().equals(DatabaseContracts.EVENT_AGREE)) {
            viewHolder.eventImageView.setVisibility(View.VISIBLE);
            viewHolder.eventImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cmp_like_icon));
        } else {
            viewHolder.eventImageView.setVisibility(View.GONE);
        }

        // setEvent(chatMessageObj.getEventType(),viewHolder.eventImageView);
        if (chatMessageObj.getIsPrivate().equals(DatabaseContracts.IS_TRUE)) {
            CountDownManager countDownManager = new CountDownManager();
            countDownManager.getTimerCountDown(viewHolder, chatMessageObj, position, new TimerEventListener() {
                @Override
                public void onFinish(ChatThreadRecyclerViewAdapter.ViewHolder holder) {

                    removeData(holder);
                }
            });
        } else {
            viewHolder.privateChatTimer.setVisibility(View.GONE);
        }

        viewHolder.receiverContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                commonAdapterViewListner.getClickedRecyclerView(viewHolder, position, chatMessageObj, 0);
                return true;
            }
        });

        viewHolder.senderContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                commonAdapterViewListner.getClickedRecyclerView(viewHolder, position, chatMessageObj, 0);
                return true;
            }
        });

        viewHolder.receiverContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonAdapterViewListner.getClickedRecyclerView(viewHolder, position, chatMessageObj, 1);
            }
        });

        viewHolder.senderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonAdapterViewListner.getClickedRecyclerView(viewHolder, position, chatMessageObj, 1);
            }
        });
        viewHolder.senderPlayStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!flag_play) {
                    viewHolder.senderPlayStopButton.setImageResource(mThumbIds[1]);
                    startPlaying(chatMessageObj.getLocalPath(), viewHolder.senderSeekbarPlay, viewHolder.senderAudioplayerTimer, viewHolder.senderPlayStopButton);
                    flag_play = true;
                } else {
                    viewHolder.senderPlayStopButton.setImageResource(mThumbIds[0]);
                    mPlayer.stop();
                    viewHolder.senderSeekbarPlay.setProgress(0);
                    flag_play = false;
                }
            }
        });

        viewHolder.receiverPlayStopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!flag_play) {
                    viewHolder.receiverPlayStopButton.setImageResource(mThumbIds[1]);
                    startPlaying(chatMessageObj.getMediaLink(), viewHolder.receiverSeekbarPlay, viewHolder.receiverAudioplayerTimer, viewHolder.receiverPlayStopButton);
                    flag_play = true;
                } else {
                    viewHolder.receiverPlayStopButton.setImageResource(mThumbIds[0]);
                    mPlayer.stop();
                    viewHolder.receiverSeekbarPlay.setProgress(0);
                    flag_play = false;
                }
            }
        });

        String dateLb = Utils.getDateLabel(chatMessageObj.getTime());

        viewHolder.txtDateLabel.setText(dateLb);
        if (position == 0 || !dateLb.equals(Utils.getDateLabel(chatMessageList.get(position - 1).getTime()))) {
            viewHolder.txtDateLabel.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtDateLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ch_container, senderContainer, SenderMsgContainer, senderMsgImage, senderTextContainer, senderAudioLayout, senderTimeContainer, senderPingLayout, receiverPingLayout,
                receiverContainer, receiverMsgContainer, receiverMsgImage, receiverTextContainer, receiverAudioLayout, receiverTimeContainer;
        private ImageView senderVideoIcon, senderSelfreactionImage, senderPlayStopButton, senderChatStatus, receiverSelfreactionImage,
                receiverVideoIcon, receiverPingIcon, senderPingIcon, receiverPlayStopButton, receiverChatStatus, eventImageView;
        private TextView senderAudioplayerTimer, senderChatTime, senderPingText, receiverPingText, receiverAudioplayerTimer, receiverChatTime, receiverDocumentView, senderDocumentView;
        private RoundedImageView receiverImageView, senderImageView;
        private EmojiconTextView senderTextView, receiverTextView;
        private ProgressBar senderProgressBar, receiverProgressBar, senAudioPrbar, recAudioPbar, sendocPbar, recdocPbar;
        private SeekBar senderSeekbarPlay, receiverSeekbarPlay;
        private FrameLayout senderMediaLayout, receiverMediaLayout, senderDocLayer, receiverDocLayer;

        ChatMessage chatMessageIs;
        public TextView privateChatTimer;
        public CountDownTimer timer;
        private TextView txtDateLabel;

        public ViewHolder(View row) {
            super(row);

            txtDateLabel =(TextView) row.findViewById(R.id.date_label);
            ch_container =(LinearLayout) row.findViewById(R.id.chat_th_container);
            senderContainer =(LinearLayout) row.findViewById(R.id.send_container);
            senderAudioLayout =(LinearLayout) row.findViewById(R.id.sender_audio_layout);
            senderTimeContainer =(LinearLayout) row.findViewById(R.id.sender_time_container);
            senderPingLayout = (LinearLayout)row.findViewById(R.id.sender_ping_container);
            receiverPingLayout =(LinearLayout) row.findViewById(R.id.receiver_ping_container);
            receiverContainer = (LinearLayout)row.findViewById(R.id.receiver_container);
            receiverAudioLayout = (LinearLayout)row.findViewById(R.id.receiver_audio_layout);
            receiverTimeContainer =(LinearLayout) row.findViewById(R.id.receiver_time_container);

            senderMediaLayout =(FrameLayout) row.findViewById(R.id.send_media_container);
            receiverMediaLayout = (FrameLayout)row.findViewById(R.id.receiver_media_container);

            privateChatTimer =(TextView) row.findViewById(R.id.private_chat_timer);
            receiverDocumentView =(TextView) row.findViewById(R.id.receiver_documentView);
            senderDocumentView =(TextView) row.findViewById(R.id.sender_documentView);

            eventImageView = (ImageView)row.findViewById(R.id.eventImageView);
            receiverSelfreactionImage =(ImageView) row.findViewById(R.id.receiver_selfReation_imageView);
            senderSelfreactionImage =(ImageView) row.findViewById(R.id.sender_selfReation_imageView);

            senderVideoIcon = (ImageView)row.findViewById(R.id.sender_video_icon);
            senderPlayStopButton = (ImageView)row.findViewById(R.id.sender_play_stop_button);
            senderChatStatus = (ImageView)row.findViewById(R.id.sender_chat_status);

            receiverPingIcon = (ImageView)row.findViewById(R.id.receiver_ping_image);
            senderPingIcon = (ImageView)row.findViewById(R.id.sender_ping_image);
            senderPingText = (TextView)row.findViewById(R.id.sender_ping_text);
            receiverPingText = (TextView)row.findViewById(R.id.receiver_ping_text);

            receiverVideoIcon =(ImageView) row.findViewById(R.id.receiver_video_icon);
            receiverPlayStopButton =(ImageView) row.findViewById(R.id.receiver_play_stop_button);


            senderAudioplayerTimer =(TextView) row.findViewById(R.id.sender_audio_player_timer);
            senderChatTime = (TextView) row.findViewById(R.id.sender_chatTime);


            receiverAudioplayerTimer = (TextView) row.findViewById(R.id.receiver_audio_player_timer);
            receiverChatTime = (TextView) row.findViewById(R.id.receiver_chatTime);


            receiverImageView =(RoundedImageView) row.findViewById(R.id.receiver_image);
            senderImageView = (RoundedImageView)row.findViewById(R.id.sender_image);

            senderTextView = (EmojiconTextView)row.findViewById(R.id.sender_textView);
            receiverTextView =(EmojiconTextView) row.findViewById(R.id.receiver_textView);
            senderDocLayer = (FrameLayout)row.findViewById(R.id.sender_doc_layer);
            receiverDocLayer = (FrameLayout)row.findViewById(R.id.receiver_doc_layer);

            senderProgressBar =(ProgressBar) row.findViewById(R.id.sender_progressBar);
            receiverProgressBar = (ProgressBar)row.findViewById(R.id.receiver_progressBar);

            senAudioPrbar = (ProgressBar)row.findViewById(R.id.sender_audio_progressbar);
            recAudioPbar =(ProgressBar) row.findViewById(R.id.receiver_audio_progressbar);
            sendocPbar = (ProgressBar)row.findViewById(R.id.sender_doc_progressbar);
            recdocPbar = (ProgressBar)row.findViewById(R.id.receiver_doc_progressbar);
            senderSeekbarPlay = (SeekBar)row.findViewById(R.id.sender_seekbar_play);
            receiverSeekbarPlay = (SeekBar)row.findViewById(R.id.receiver_seekbar_play);

        }

        public void bindData(ChatMessage item) {
            chatMessageIs = item;
            privateChatTimer.setText("" + chatMessageIs.getPrivateMessageTime() * 1000);

        }

    }

//}

    public void senderView(ViewHolder viewHolder, ChatMessage chatMessageObject) {

        String messageType = chatMessageObject.getMsgType();
        String isPrivate = chatMessageObject.getIsPrivate();
        viewHolder.senderTextView.setVisibility(View.GONE);
        viewHolder.senderAudioLayout.setVisibility(View.GONE);
        viewHolder.senderMediaLayout.setVisibility(View.GONE);
        viewHolder.senderSelfreactionImage.setVisibility(View.GONE);
        viewHolder.senderDocumentView.setVisibility(View.GONE);
        viewHolder.senderPingLayout.setVisibility(View.GONE);
        viewHolder.senderDocLayer.setVisibility(View.GONE);
        viewHolder.senderChatTime.setText(chatMessageObject.getDate());
        String status = chatMessageObject.getSendStatus();

        setStatus(status, viewHolder);
        if (messageType.equals(DatabaseContracts.ISIMAGE + "")) {
            viewHolder.receiverVideoIcon.setVisibility(View.GONE);
            viewHolder.senderMediaLayout.setVisibility(View.VISIBLE);

            Utils.loadImageUsingGlideCenterCrop(
                    this.context, viewHolder.senderImageView, chatMessageObject.getMediaLink());
            if (chatMessageObject.getUpLoadStatus().equals(DatabaseContracts.IS_FALSE)) {
                viewHolder.senderProgressBar.setVisibility(View.VISIBLE);
                viewHolder.senderVideoIcon.setVisibility(View.GONE);
            } else {
                viewHolder.senderProgressBar.setVisibility(View.GONE);
                viewHolder.senderVideoIcon.setVisibility(View.GONE);
            }

        } else if (messageType.equals(DatabaseContracts.ISAUDIO + "")) {
            viewHolder.senderAudioLayout.setVisibility(View.VISIBLE);
            if (chatMessageObject.getUpLoadStatus().equals(DatabaseContracts.IS_FALSE)) {
                viewHolder.senderPlayStopButton.setVisibility(View.GONE);
                viewHolder.senAudioPrbar.setVisibility(View.VISIBLE);
            } else {
                viewHolder.senAudioPrbar.setVisibility(View.GONE);
                viewHolder.senderPlayStopButton.setVisibility(View.VISIBLE);
            }

        } else if (messageType.equals(DatabaseContracts.ISVEDIO + "")) {
            viewHolder.receiverVideoIcon.setVisibility(View.VISIBLE);
            viewHolder.senderMediaLayout.setVisibility(View.VISIBLE);
            Utils.loadImageUsingGlideCenterCrop(
                    this.context, viewHolder.senderImageView, chatMessageObject.getMediaLink());

            if (chatMessageObject.getUpLoadStatus().equals(DatabaseContracts.IS_FALSE)) {
                viewHolder.senderProgressBar.setVisibility(View.VISIBLE);
                viewHolder.senderVideoIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.senderProgressBar.setVisibility(View.VISIBLE);
                viewHolder.senderVideoIcon.setVisibility(View.VISIBLE);
            }
        } else if (messageType.equals(DatabaseContracts.ISREACTION + "")) {
            viewHolder.senderSelfreactionImage.setVisibility(View.VISIBLE);
            Utils.loadImageUsingGlideCircleTransform(
                    this.context, viewHolder.senderSelfreactionImage, chatMessageObject.getMediaLink());
        } else if (messageType.equals(DatabaseContracts.ISPING + "")) {
            viewHolder.senderPingLayout.setVisibility(View.VISIBLE);
            Utils.loadImageUsingGlideCircleTransform(this.context, viewHolder.senderPingIcon, senderImage);
        } else if (messageType.equals(DatabaseContracts.ISDOC + "")) {
            viewHolder.senderDocLayer.setVisibility(View.VISIBLE);
            viewHolder.senderDocumentView.setVisibility(View.VISIBLE);
            File file = new File(chatMessageObject.getLocalPath());
            String fileName = file.getName();
            if (fileName.length() > 20) {
                fileName = fileName.substring(fileName.length() - 20, fileName.length());
            }
            viewHolder.senderDocumentView.setText(fileName);

            if (chatMessageObject.getUpLoadStatus().equals(DatabaseContracts.IS_FALSE)) {
                viewHolder.sendocPbar.setVisibility(View.VISIBLE);
            } else {
                viewHolder.sendocPbar.setVisibility(View.GONE);
            }

        } else if (messageType.equals(DatabaseContracts.SOUND_EMOJI + "")) {
        } else {
            viewHolder.senderTextView.setVisibility(View.VISIBLE);
            viewHolder.senderTextView.setText(chatMessageObject.getMessage());
        }

    }


    public void receiverView(ViewHolder viewHolder, ChatMessage chatMessageObject) {

        String messageType = chatMessageObject.getMsgType();
        String viewstatus = chatMessageObject.getMsgViewedStatus();
        if (viewstatus.equals("0") || viewstatus == null) {
            try {
                JSONArray messageArray = new JSONArray();
                messageArray.put(chatMessageObject.getServerId());
                updateChatCache(chatMessageObject.getClintId(), DatabaseContracts.IS_TRUE);
                mSocket.emit(RegistrationConstants.SEEN_STATUS, messageArray, mSocketSenderId, mSocketReceiverId);
            } catch (Exception e) {
            }
        } else {
        }
        viewHolder.receiverChatTime.setText(chatMessageObject.getDate());
        String isPrivate = chatMessageObject.getIsPrivate();
        viewHolder.receiverTextView.setVisibility(View.GONE);
        viewHolder.receiverAudioLayout.setVisibility(View.GONE);
        viewHolder.receiverMediaLayout.setVisibility(View.GONE);
        viewHolder.receiverSelfreactionImage.setVisibility(View.GONE);
        viewHolder.receiverDocumentView.setVisibility(View.GONE);
        viewHolder.receiverPingLayout.setVisibility(View.GONE);
        viewHolder.receiverDocLayer.setVisibility(View.GONE);
        if (messageType.equals(DatabaseContracts.ISIMAGE + "")) {
            viewHolder.receiverVideoIcon.setVisibility(View.GONE);
            viewHolder.receiverMediaLayout.setVisibility(View.VISIBLE);
            Utils.loadImageUsingGlideCenterCrop(
                    this.context, viewHolder.receiverImageView, chatMessageObject.getMediaLink());
            if (chatMessageObject.getDownLoadStatus().equals(DatabaseContracts.IS_FALSE)) {
                viewHolder.receiverProgressBar.setVisibility(View.VISIBLE);
                viewHolder.receiverVideoIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.receiverProgressBar.setVisibility(View.GONE);
                viewHolder.receiverVideoIcon.setVisibility(View.GONE);
            }

        } else if (messageType.equals(DatabaseContracts.ISAUDIO + "")) {

            if (chatMessageObject.getDownLoadStatus().equals(DatabaseContracts.IS_FALSE)) {
                viewHolder.receiverPlayStopButton.setVisibility(View.GONE);
                viewHolder.recAudioPbar.setVisibility(View.VISIBLE);
            } else {
                viewHolder.receiverPlayStopButton.setVisibility(View.GONE);
                viewHolder.recAudioPbar.setVisibility(View.VISIBLE);
            }

        } else if (messageType.equals(DatabaseContracts.ISVEDIO + "")) {
            viewHolder.receiverVideoIcon.setVisibility(View.VISIBLE);
            viewHolder.receiverMediaLayout.setVisibility(View.VISIBLE);
            Utils.loadImageUsingGlideCenterCrop(
                    this.context, viewHolder.receiverImageView, chatMessageObject.getMediaLink());
            if (chatMessageObject.getDownLoadStatus().equals(DatabaseContracts.IS_FALSE)) {
                viewHolder.receiverProgressBar.setVisibility(View.VISIBLE);
            } else {
                viewHolder.receiverProgressBar.setVisibility(View.GONE);
            }
        } else if (messageType.equals(DatabaseContracts.ISREACTION + "")) {
            viewHolder.receiverSelfreactionImage.setVisibility(View.VISIBLE);
            Utils.loadImageUsingGlideCircleTransform(
                    this.context, viewHolder.receiverSelfreactionImage, chatMessageObject.getMediaLink());
        } else if (messageType.equals(DatabaseContracts.ISPING + "")) {
            viewHolder.receiverPingText.setText(friendName);
            viewHolder.receiverPingLayout.setVisibility(View.VISIBLE);
            Utils.loadImageUsingGlideCircleTransform(
                    this.context, viewHolder.receiverPingIcon, this.friendImage);
        } else if (messageType.equals(DatabaseContracts.ISDOC + "")) {
            viewHolder.receiverDocumentView.setVisibility(View.VISIBLE);
            viewHolder.receiverDocLayer.setVisibility(View.VISIBLE);

            String urlStr = chatMessageObject.getMediaLink();
            String fileName = urlStr.substring(urlStr.lastIndexOf('/') + 1, urlStr.length());
            viewHolder.senderDocumentView.setText(fileName);
            if (chatMessageObject.getDownLoadStatus().equals(DatabaseContracts.IS_FALSE)) {
                viewHolder.recdocPbar.setVisibility(View.VISIBLE);
            } else {
                viewHolder.recdocPbar.setVisibility(View.GONE);
            }
        } else if (messageType.equals(DatabaseContracts.SOUND_EMOJI + "")) {
        } else {
            viewHolder.receiverTextView.setVisibility(View.VISIBLE);
            viewHolder.receiverTextView.setText(chatMessageObject.getMessage());
        }
    }

    public void removeData(ChatThreadRecyclerViewAdapter.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        if (position > -1) {
            ChatMessage chatMessage = chatMessageList.get(position);
            ChatTreadActivity.modelId.remove(position);
            deleteUserRow(chatMessage.getClintId());
            chatMessageList.remove(position);
            notifyItemRemoved(position);
        }

    }

    private boolean updateChatCache(String _Id, String isViewed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.ISVIEWED, isViewed);
        db.update(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, contentValues, DatabaseContracts.ChatContractDataBase._ID + "= ? ", new String[]{_Id});
        return true;
    }

    public void setStatus(String messageStatus, ChatThreadRecyclerViewAdapter.ViewHolder holder) {
        String status = messageStatus;
        if (messageStatus == null) {
            holder.senderChatStatus.setImageResource(R.drawable.history_icon);
        } else {
            if (messageStatus.equals(DatabaseContracts.SENT)) {
                holder.senderChatStatus.setImageResource(R.drawable.sent_icon);
            } else if (messageStatus.equals(DatabaseContracts.DELIVERED)) {
                holder.senderChatStatus.setImageResource(R.drawable.delevered_icon);
            } else if (messageStatus.equals(DatabaseContracts.SEEN)) {
                holder.senderChatStatus.setImageResource(R.drawable.seen_icon);
            } else {
                holder.senderChatStatus.setImageResource(R.drawable.history_icon);
            }
        }
    }


    private void startPlaying(String path, SeekBar seekbar, TextView AudioTimer, ImageView viewPlay) {
        try {
            mPlayer.reset();
            FileInputStream rawFile = new FileInputStream(path);
            mPlayer.setDataSource(rawFile.getFD());
            mPlayer.prepare();
            mPlayer.start();
            playSeekBar = seekbar;
            playSeekBar.setProgress(0);
            playSeekBar.setMax(100);
            this.viewPlay = viewPlay;
            updateProgressBar(AudioTimer);
        } catch (IOException e) {
        }
    }

    private void updateProgressBar(TextView AudioTimer) {
        audioPlayerTimer = AudioTimer;
        seekHandler.postDelayed(mUpdateTimeTask, 100);

    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mPlayer.getDuration();
            long currentDuration = mPlayer.getCurrentPosition();
            audioPlayerTimer.setText("00:" + currentDuration / 100);
            int progress = getProgressPercentage(currentDuration, totalDuration);
            playSeekBar.setProgress(progress);
            seekHandler.postDelayed(this, 100);
            if (!mPlayer.isPlaying()) {
                viewPlay.setImageResource(mThumbIds[0]);
                flag_play = false;
            }
            if (!flag_play)
                playSeekBar.setProgress(0);
        }
    };

    private int getProgressPercentage(long currentDuration, long totalDuration) {
        // TODO Auto-generated method stub
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;
        // return percentage
        return percentage.intValue();
    }

    public void deleteUserRow(String clientId) {

        db.delete(DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, DatabaseContracts.ChatContractDataBase._ID + "=?", new String[]{clientId});
    }

    public void setEvent(String eventType, ImageView imageView) {
        if (eventType.equals(DatabaseContracts.EVENT_LIKE)) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cmp_like_icon));
        } else if (eventType.equals(DatabaseContracts.EVENT_AGREE)) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.cmp_like_icon));
        } else {
            imageView.setVisibility(View.GONE);
        }

    }
}
