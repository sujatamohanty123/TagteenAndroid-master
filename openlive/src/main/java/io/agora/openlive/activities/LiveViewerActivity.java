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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.phoenixframework.Message;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
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
import jp.wasabeef.glide.transformations.BlurTransformation;

public class LiveViewerActivity extends AppBaseActivity
        implements BroadcastingTrackSocket.TrackEventsCallback, MultiplePermissionsListener {
    private static final String PACKAGE_SCHEME = "package";
    private static final String TAG = LiveViewerActivity.class.getSimpleName();
    public static final String EXTRA_PARCELABLE_DATA = "in.tagteen.tagteen." + "EXTRA_USER_DATA";

    private VideoGridContainer mVideoGridContainer;
    //private ImageView mMuteAudioBtn;
    //private ImageView mMuteVideoBtn;
    private RelativeLayout bottomContainer;

    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    private FloatingActionButton fabShareButton;
    private MaterialDialog mPermissionDeniedDialog;

    private BroadcastingTrackSocket mTrackingSocket;
    private String userName = "", userId = "";
    private TextView lblViewsCount;

    // Chat controlls
    private LinearLayout commentsView;
    private RecyclerView recyclerlistComments;
    private EditText editComment;
    private CircleImageView imgBroadcaster;
    private AppCompatImageView imgBrodcasterBackground;
    private RippleBackground rippleImgBrodcaster;
    private FrameLayout containerBroadcasterView;
    private RelativeLayout containerViewersView;
    private AppCompatImageView imgProfilePic;

    private ChatAdapter chatAdapter;
    private LiveActivityUserDM mUserDM;
    private boolean isBrodcasterOnline;
    private String hostedBy = "";
    private String uid = "";
    private String token = "";
    private String channel = "";
    private String hostUserId;
    private ImageView imgSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_viewer_room);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initComponent();
        initClickListeners();
        initPermissionDialog();
        checkPermisssion();
        chatAdapter = new ChatAdapter();
        recyclerlistComments.setAdapter(chatAdapter);
    }

    private void initComponent() {
        if (getIntent().getExtras() != null && getIntent().getExtras()
                .getParcelable(EXTRA_PARCELABLE_DATA) != null) {
            mUserDM = getIntent().getExtras().getParcelable(EXTRA_PARCELABLE_DATA);
            if (mUserDM != null) {
                userId = mUserDM.getUserId();
                userName = mUserDM.getUserName();
            }
        }
        fabShareButton = findViewById(R.id.btn_share);
        imgSend = findViewById(R.id.img_send);
        containerBroadcasterView = findViewById(R.id.container_brodcaster_view);
        containerViewersView = findViewById(R.id.container_viewer_view);
        rippleImgBrodcaster = findViewById(R.id.ripple_img_broadcaster);
        imgBroadcaster = findViewById(R.id.img_broadcaster);
        imgBrodcasterBackground = findViewById(R.id.img_brodaster_background);
        imgProfilePic = findViewById(R.id.live_name_board_icon);
        token = getIntent().getStringExtra(io.agora.openlive.Constants.TOKEN);
        uid = getIntent().getStringExtra(io.agora.openlive.Constants.UID);
        channel = getIntent().getStringExtra(io.agora.openlive.Constants.CHANNEL_NAME);
        hostedBy = getIntent().getStringExtra(io.agora.openlive.Constants.HOSTED_BY);
        this.hostUserId = getIntent().getStringExtra(io.agora.openlive.Constants.HOST_USER_ID);
        String profilePic = getIntent().getStringExtra(io.agora.openlive.Constants.PROFILE_PIC_URL);
        //this.userName = getIntent().getStringExtra(io.agora.openlive.Constants.LOGIN_USERNAME);
        //this.userId = getIntent().getStringExtra(io.agora.openlive.Constants.LOGIN_ID);

        if (mUserDM != null && mUserDM.getBroadcasterImage() != null) {
            Glide.with(this).load(mUserDM.getBroadcasterImage())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                    .into(imgBrodcasterBackground);
            Glide.with(this).load(mUserDM.getBroadcasterImage()).into(imgBroadcaster);
            rippleImgBrodcaster.startRippleAnimation();
        }

        if (mUserDM != null && mUserDM.getBrodcasterProfileImage() != null) {
            loadProfilePic(imgProfilePic,
                    ImageUrlUtils.getUpdatedImageUrl(mUserDM.getBrodcasterProfileImage(), "large"));
        }

        // set profile pic

        //if (profilePic != null) {
        //  this.loadProfilePic(imgProfilePic, profilePic);
        //}

        TextView roomName = findViewById(R.id.live_room_name);
        if (hostedBy != null) {
            roomName.setText(hostedBy);
            roomName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToHostProfile();
                }
            });
        } else {
            roomName.setText(config().getChannelName());
        }
        roomName.setSelected(true);

        //initUserIcon();

        //int role = getIntent().getIntExtra(
        //    io.agora.openlive.Constants.KEY_CLIENT_ROLE,
        //    Constants.CLIENT_ROLE_AUDIENCE);

        //Chat Controlls
        commentsView = (LinearLayout) findViewById(R.id.commentsView);
        recyclerlistComments = (RecyclerView) findViewById(R.id.recyclerlistComments);
        editComment = (EditText) findViewById(R.id.editComment);
        //imageSend = (ImageView) findViewById(R.id.imageSend);
        recyclerlistComments.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerlistComments.setLayoutManager(layoutManager);

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

        this.bottomContainer = findViewById(R.id.bottom_container);

        initData();
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
        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToHostProfile();
            }
        });
    }

    private void moveToHostProfile() {
        try {
            Intent intent = new Intent(this,
                    Class.forName("in.tagteen.tagteen.profile.OtherUserProfileActivity"));
            intent.putExtra(io.agora.openlive.Constants.USER_ID, this.hostUserId);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initAgoraMethods() {
        config().setChannelName(channel);
        config().setToken(token);
        config().setUid(uid);
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
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

    private void checkPermisssion() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(this).check();
    }

    private void initUI() {

        //if (isBroadcaster) startBroadcast();

        //imageSend.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //  public void onClick(View v) {
        //    String comment = editComment.getText().toString().trim();
        //    if (!comment.isEmpty()) {
        //      mTrackingSocket.pushComment(userId, userName, comment);
        //      chatAdapter.addComment(new ChatModel(userId, userName, comment));
        //      if (chatAdapter.getItemCount() > 0) {
        //        recyclerlistComments.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        //      }
        //      editComment.setText("");
        //    } else {
        //      Toast.makeText(LiveViewerActivity.this, "Please enter comment", Toast.LENGTH_LONG).show();
        //    }
        //  }
        //});
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
            Toast.makeText(LiveViewerActivity.this, "Please enter comment", Toast.LENGTH_LONG).show();
        }
    }

    private void loadProfilePic(ImageView imageView, String url) {
        if (imageView == null || url == null || url.trim().length() == 0) {
            return;
        }
        try {
            Glide.with(this)
                    .load(url) // add your image url
                    .transform(new CircleTransform(this)) // applying the image transformer
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        mVideoDimension = io.agora.openlive.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
        fabShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUserDM != null && mUserDM.getBroadcasterImage() != null) {
                    downloadUsingFetch(mUserDM.getBroadcasterImage());
                }
            }
        });
    }

    private void downloadUsingFetch(String imageUrl) {
        IntentUtils.removeShareLinkTempImage();
        Picasso.with(this).load(ImageUrlUtils.getUpdatedImageUrl(imageUrl, "large")).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                IntentUtils.shareBrodcasterWebShowAppDownloadLink(LiveViewerActivity.this, bitmap,
                        mUserDM.getBroadcasterName());
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
    protected void onGlobalLayoutCompleted() {
        RelativeLayout topLayout = findViewById(R.id.live_room_top_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) topLayout.getLayoutParams();
        params.height = mStatusBarHeight + topLayout.getMeasuredHeight();
        topLayout.setLayoutParams(params);
        topLayout.setPadding(0, mStatusBarHeight, 0, 0);
    }

    private void joinBroadcast() {
        initAgoraMethods();
        joinChannel();
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
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
        if (!isBrodcasterOnline) {
            containerBroadcasterView.setVisibility(View.GONE);
            containerViewersView.setVisibility(View.VISIBLE);
            isBrodcasterOnline = true;
            rippleImgBrodcaster.stopRippleAnimation();
        }
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
            joinBroadcast();
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
        if (mTrackingSocket != null) {
            mTrackingSocket.disconnectAllSockets();
        }
    }

    private void initTrackingSocket() {
        mTrackingSocket = new BroadcastingTrackSocket("Tagteen", userId);
        mTrackingSocket.setCallback(this);
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        if (report.areAllPermissionsGranted()) {
            joinBroadcast();
        } else if (report.isAnyPermissionPermanentlyDenied()) {
            mPermissionDeniedDialog.show();
        } else {
            Toast.makeText(this, "Enable All Permissions", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list,
                                                   final PermissionToken token) {
        new MaterialDialog.Builder(this).title("Permission ")
                .positiveText("Okay")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
    public void onUserLeavesChannel(@NotNull Message message) {
        String leavesValue = Objects.requireNonNull(message.getPayload().get("leaves")).toString();
        if (!leavesValue.trim().equals("{}")
                && !leavesValue.trim().isEmpty() && !GeneralUtils.isStringEmpty(channel) && mTrackingSocket != null) {
            mTrackingSocket.pushOnline(channel);
        }
    }
}
