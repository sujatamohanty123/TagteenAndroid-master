package io.agora.openlive.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.phoenixframework.Message;

import java.util.List;
import java.util.Objects;

import io.agora.openlive.R;
import io.agora.openlive.chatlist.ChatAdapter;
import io.agora.openlive.chatlist.ChatModel;
import io.agora.openlive.stats.LocalStatsData;
import io.agora.openlive.stats.RemoteStatsData;
import io.agora.openlive.stats.StatsData;
import io.agora.openlive.ui.VideoGridContainer;
import io.agora.openlive.utils.BroadcastingTrackSocket;
import io.agora.openlive.utils.CircleTransform;
import io.agora.openlive.utils.GeneralUtils;
import io.agora.openlive.utils.ImageUrlUtils;
import io.agora.openlive.utils.IntentUtils;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class LiveBroadcasterActivity extends AppBaseActivity
        implements BroadcastingTrackSocket.TrackEventsCallback, MultiplePermissionsListener {
    private static final String PACKAGE_SCHEME = "package";
    private static final String TAG = LiveBroadcasterActivity.class.getSimpleName();
    public static final String EXTRA_PARCELABLE_DATA = "in.tagteen.tagteen." + "EXTRA_USER_DATA";

    private VideoGridContainer mVideoGridContainer;
    //private ImageView mMuteAudioBtn;
    //private ImageView mMuteVideoBtn;
    private RelativeLayout bottomContainer;

    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    private ImageView imgSend;

    private BroadcastingTrackSocket mTrackingSocket;
    private String userName = "", userId = "";
    private TextView lblViewsCount;

    // Chat controlls
    private LinearLayout commentsView;
    private RecyclerView recyclerlistComments;
    private EditText editComment;
    //private ImageView imageSend;

    private ChatAdapter chatAdapter;
    private LiveActivityUserDM mUserDM;
    ImageView imgProfilePic;
    private MaterialDialog mPermissionDeniedDialog;
    private FloatingActionButton fabShareButton;
    private String hostedBy = "";
    private String uid = "";
    private String token = "";
    String channel = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_broadcaster_room);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initUI();
        initData();
        initPermissionDialog();
        initClickListeners();
        checkPermisssion();
        chatAdapter = new ChatAdapter();
        recyclerlistComments.setAdapter(chatAdapter);
    }

    private void initAgoraMethods() {
        config().setChannelName(channel);
        config().setToken(token);
        config().setUid(uid);
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
    }

    private void initUI() {
        if (getIntent().getExtras() != null && getIntent().getExtras()
                .getParcelable(EXTRA_PARCELABLE_DATA) != null) {
            mUserDM = getIntent().getExtras().getParcelable(EXTRA_PARCELABLE_DATA);
            if (mUserDM != null) {
                userId = mUserDM.getUserId();
                userName = mUserDM.getUserName();
            }
        }
        //Chat Controlls
        commentsView = (LinearLayout) findViewById(R.id.commentsView);
        imgSend = findViewById(R.id.img_send);
        fabShareButton = findViewById(R.id.btn_share);
        recyclerlistComments = (RecyclerView) findViewById(R.id.recyclerlistComments);
        editComment = (EditText) findViewById(R.id.editComment);
        //imageSend = (ImageView) findViewById(R.id.imageSend);
        recyclerlistComments.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerlistComments.setLayoutManager(layoutManager);
        // set profile pic
        imgProfilePic = findViewById(R.id.live_name_board_icon);

        lblViewsCount = findViewById(R.id.lblViewsCount);
        //mMuteVideoBtn = findViewById(R.id.live_btn_mute_video);
        //mMuteVideoBtn.setActivated(isBroadcaster);

        //mMuteAudioBtn = findViewById(R.id.live_btn_mute_audio);
        //mMuteAudioBtn.setActivated(isBroadcaster);

        ImageView beautyBtn = findViewById(R.id.live_btn_beautification);
        beautyBtn.setActivated(true);
        rtcEngine().setBeautyEffectOptions(beautyBtn.isActivated(),
                io.agora.openlive.Constants.DEFAULT_BEAUTY_OPTIONS);

        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(statsManager());
        mVideoDimension = io.agora.openlive.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
        token = getIntent().getStringExtra(io.agora.openlive.Constants.TOKEN);
        uid = getIntent().getStringExtra(io.agora.openlive.Constants.UID);
        channel = getIntent().getStringExtra(io.agora.openlive.Constants.CHANNEL_NAME);
        hostedBy = getIntent().getStringExtra(io.agora.openlive.Constants.HOSTED_BY);
        String profilePic = getIntent().getStringExtra(io.agora.openlive.Constants.PROFILE_PIC_URL);
        //this.userName = getIntent().getStringExtra(io.agora.openlive.Constants.LOGIN_USERNAME);
        //this.userId = getIntent().getStringExtra(io.agora.openlive.Constants.LOGIN_ID);

        TextView roomName = findViewById(R.id.live_room_name);
        if (hostedBy != null) {
            roomName.setText(hostedBy);
        } else {
            roomName.setText(config().getChannelName());
        }
        roomName.setSelected(true);

        if (mUserDM != null && mUserDM.getBrodcasterProfileImage() != null) {
            loadProfilePic(imgProfilePic,
                    ImageUrlUtils.getUpdatedImageUrl(mUserDM.getBrodcasterProfileImage(), "large"));
        }

        this.bottomContainer = findViewById(R.id.bottom_container);
        commentsView.setVisibility(View.VISIBLE);
        this.bottomContainer.setVisibility(View.VISIBLE);
    }

    private void initClickListeners() {
        editComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                imgSend.setVisibility(
                        charSequence.toString().trim().length() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment(editComment.getText().toString().trim());
            }
        });
    }

    private void sendComment(String comment) {
        if (!comment.isEmpty() && !GeneralUtils.isStringEmpty(channel) && mTrackingSocket != null) {
            mTrackingSocket.pushComment(userId, userName, comment, channel);
            chatAdapter.addComment(new ChatModel(userId, userName, comment));
            if (chatAdapter.getItemCount() > 0) {
                recyclerlistComments.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }
            editComment.setText("");
        } else {
            Toast.makeText(LiveBroadcasterActivity.this, "Please enter comment", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void loadProfilePic(ImageView imageView, String url) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            //Picasso.with(this)
            //    .load(url) // Your image source.
            //    .fit()  // Fix centerCrop issue: http://stackoverflow.com/a/20824141/1936697
            //    .centerCrop()
            //    .placeholder(R.color.gray)
            //    .into(imageView);
            Glide.with(this)
                    .load(url) // add your image url
                    .transform(new CircleTransform(this)) // applying the image transformer
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //private void initUserIcon() {
    //  Bitmap origin = BitmapFactory.decodeResource(getResources(), R.drawable.fake_user_icon);
    //  RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), origin);
    //  drawable.setCircular(true);
    //  ImageView iconView = findViewById(R.id.live_name_board_icon);
    //  iconView.setImageDrawable(drawable);
    //}

    private void initData() {
        fabShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //IntentUtils.shareWebShowAppDownloadLink(LiveBroadcasterActivity.this,
                //    mUserDM.getBroadcasterImage(),
                //    mUserDM.getUserName());
                if (mUserDM != null && mUserDM.getBroadcasterImage() != null) {
                    shareImage(mUserDM.getBroadcasterImage());
                }
            }
        });
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        RelativeLayout topLayout = findViewById(R.id.live_room_top_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) topLayout.getLayoutParams();
        params.height = mStatusBarHeight + topLayout.getMeasuredHeight();
        topLayout.setLayoutParams(params);
        topLayout.setPadding(0, mStatusBarHeight, 0, 0);
    }

    private void startBroadcast() {
        initAgoraMethods();
        joinChannel();
        SurfaceView surface = prepareRtcVideo(0, true);
        mVideoGridContainer.addUserVideoSurface(0, surface, true);

        //mMuteAudioBtn.setActivated(true);
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        removeRtcVideo(0, true);
        mVideoGridContainer.removeUserVideo(0, true);
        //mMuteAudioBtn.setActivated(false);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }

    public void onLeaveClicked(View view) {
        finish();
    }

    public void onSwitchCameraClicked(View view) {
        rtcEngine().switchCamera();
    }

    public void onBeautyClicked(View view) {
        view.setActivated(!view.isActivated());
        rtcEngine().setBeautyEffectOptions(view.isActivated(),
                io.agora.openlive.Constants.DEFAULT_BEAUTY_OPTIONS);
    }

    public void onMoreClicked(View view) {
        // Do nothing at the moment
    }

    public void onPushStreamClicked(View view) {
        // Do nothing at the moment
    }

    //public void onMuteAudioClicked(View view) {
    //  if (!mMuteVideoBtn.isActivated()) return;
    //
    //  rtcEngine().muteLocalAudioStream(view.isActivated());
    //  view.setActivated(!view.isActivated());
    //}

    public void onMuteVideoClicked(View view) {
        if (view.isActivated()) {
            stopBroadcast();
        } else {
            startBroadcast();
        }
        view.setActivated(!view.isActivated());
    }

    @Override
    public void onReceiveComment(Message envelope) {
        String mUserId = Objects.requireNonNull(envelope.getPayload().get("id")).toString();
        String userName = Objects.requireNonNull(envelope.getPayload().get("name")).toString();
        String recieveComment = Objects.requireNonNull(envelope.getPayload().get("comment")).toString();
        final ChatModel chatModel = new ChatModel(userId, userName, recieveComment);
        if (!mUserId.equals(userId)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatAdapter.addComment(chatModel);
                    if (chatAdapter.getItemCount() > 0) {
                        recyclerlistComments.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                }
            });
        }
    }

    @Override
    public void onRecieveUserCount(Message envelope) {
        String visitorCount =
                        Objects.requireNonNull(envelope.getPayload().get("count")).toString();
        if (!GeneralUtils.isStringEmpty(visitorCount)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lblViewsCount.setText(visitorCount);
                }
            });
        }
    }

    @Override
    public void onJoinChannelSuccess(@NonNull Message envelope) {
    }

    @Override
    public void onJoinChannelIgnored(@NonNull Message envelope) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTrackingSocket != null) {
            mTrackingSocket.disconnectAllSockets();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!GeneralUtils.isStringEmpty(channel)) {
            if (mTrackingSocket == null) {
                initTrackingSocket();
            }
            mTrackingSocket.initSocketConnection(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTrackingSocket.disconnectAllSockets();
    }

    private void initTrackingSocket() {
        if (!GeneralUtils.isStringEmpty(userId)) {
            mTrackingSocket = new BroadcastingTrackSocket("Tagteen", userId);
            mTrackingSocket.setCallback(this);
        }
    }

    private void checkPermisssion() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(this).check();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list,
                                                   final PermissionToken token) {
        new MaterialDialog.Builder(this).title("Permission ")
                .positiveText("Okay")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        //should open settings page to enable permission
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@androidx.annotation.NonNull MaterialDialog dialog,
                                        @androidx.annotation.NonNull DialogAction which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .build().show();
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        if (report.areAllPermissionsGranted()) {
            startBroadcast();
        } else if (report.isAnyPermissionPermanentlyDenied()) {
            mPermissionDeniedDialog.show();
        } else {
            Toast.makeText(this, "Enable All Permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private void initPermissionDialog() {
        mPermissionDeniedDialog =
                new MaterialDialog.Builder(this).title("Permission Denied")
                        .positiveText("Go To Settings")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(
                                    @NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //should open settings page to enable permission
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts(PACKAGE_SCHEME, getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .build();
    }

    private void shareImage(String imageUrl) {
        Log.e("url", ImageUrlUtils.getUpdatedImageUrl(imageUrl, "large"));
        IntentUtils.removeShareLinkTempImage();
        Picasso.with(this).load(ImageUrlUtils.getUpdatedImageUrl(imageUrl, "large")).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                IntentUtils.shareBrodcasterWebShowAppDownloadLink(LiveBroadcasterActivity.this, bitmap,
                        mUserDM.getUserName());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    @Override
    public void onUserLeavesChannel(@NotNull Message message) {
        String leavesValue = Objects.requireNonNull(message.getPayload().get("leaves")).toString();
        if (!leavesValue.trim().equals("{}")
                && !leavesValue.trim().isEmpty()
                && !GeneralUtils.isStringEmpty(channel)
                && mTrackingSocket != null) {
            mTrackingSocket.pushOnline(channel);
        }
    }
}
