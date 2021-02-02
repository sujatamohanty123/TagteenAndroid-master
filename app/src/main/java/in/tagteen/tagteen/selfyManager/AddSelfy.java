package in.tagteen.tagteen.selfyManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import in.tagteen.tagteen.BuildConfig;
import in.tagteen.tagteen.MainDashboardActivity;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.emoji.EmojiconEditText;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.AbsoluteFilePath;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/**
 * Created by lovekushvishwakarma on 02/12/17.
 */

public class AddSelfy extends Activity implements View.OnClickListener, AsyncResponse {

    ImageView imageback, imagePost;
    SimpleExoPlayerView videoview;
    SimpleExoPlayer exoPlayer;
    private static final int VIDEO_CAPTURE = 101;
    public static final int CAMERA_REQUEST = 100;
    File mediaFile;
    String url = "", filepath, desc = "";
    static String userid = "";

    String videoPath = "";
    EmojiconEditText msg_emojicon_edittext;
    ImageView video_play,video_pause;
    VideoDataSender_selfy videoDataSender_selfy=new VideoDataSender_selfy();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_add_selfy);

        try {
            videoPath = getIntent().getStringExtra("path");
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageback = (ImageView) findViewById(R.id.imageback);
        imagePost = (ImageView) findViewById(R.id.imagePost);
        videoview = (SimpleExoPlayerView) findViewById(R.id.videoview);
        msg_emojicon_edittext = (EmojiconEditText) findViewById(R.id.msg_emojicon_edittext);
        video_play = (ImageView) findViewById(R.id.video_play);
        video_pause = (ImageView) findViewById(R.id.video_pause);

        imageback.setOnClickListener(this);
        imagePost.setOnClickListener(this);


        if (userid != null || userid.equalsIgnoreCase("")) {
            userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        }

        try {
            video_pause.setVisibility(View.VISIBLE);

            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
            Uri videoURI = Uri.parse(videoPath);
            DataSpec dataSpec = new DataSpec(videoURI);
            final FileDataSource fileDataSource = new FileDataSource();
            fileDataSource.open(dataSpec);
            DataSource.Factory factory = new DataSource.Factory() {
                @Override
                public DataSource createDataSource() {
                    return fileDataSource;
                }
            };
            MediaSource mediaSource = new ExtractorMediaSource(
                    fileDataSource.getUri(), factory, new DefaultExtractorsFactory(), null, null);

            videoview.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
            this.exoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onSeekProcessed() {}

                @Override
                public void onPositionDiscontinuity(int reason) {}

                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {}

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {}

                @Override
                public void onRepeatModeChanged(int repeatMode) {}

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}
                @Override
                public void onLoadingChanged(boolean isLoading) {}
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (ExoPlayer.STATE_ENDED == playbackState) {
                        video_play.setVisibility(View.VISIBLE);
                        video_pause.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onPlayerError(ExoPlaybackException error) {}

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        video_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_play.setVisibility(View.GONE);
                video_pause.setVisibility(View.VISIBLE);
                //videoview.setVideoURI(Uri.parse(filePath));
                /*Uri videoURI = Uri.parse(videoPath);
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);*/
                exoPlayer.seekTo(0);
                exoPlayer.setPlayWhenReady(true);
            }
        });
        video_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_pause.setVisibility(View.GONE);
                video_play.setVisibility(View.VISIBLE);
                exoPlayer.setPlayWhenReady(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageback:
                dialog_show();
                break;
            case R.id.imagePost:
                if (videoPath != null) {
                    hideKeyBoard();
                    desc = "-";
                    if (msg_emojicon_edittext.getText().toString().length() > 0) {
                        desc = msg_emojicon_edittext.getText().toString();
                    }
                    //File file = new File(videoPath);
                    //uploadVideoThumb(file, AddSelfy.this);
                    videoDataSender_selfy.setVideoPath(videoPath);
                    videoDataSender_selfy.setVideoDesc(desc);
                    videoDataSender_selfy.setIsCall(true);
                    Intent intent = new Intent(AddSelfy.this, MainDashboardActivity.class);
                    intent.putExtra("fragmentload", "social");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
        }
    }

    private void dialog_show() {
        final Dialog d = new Dialog(AddSelfy.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.confirmationpopup);
        final TextView msg = (TextView) d.findViewById(R.id.message);
        TextView name = (TextView) d.findViewById(R.id.yourname);
        TextView continueorder = (TextView) d.findViewById(R.id.confirm);
        TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
        TextView ok = (TextView) d.findViewById(R.id.confirm_ok_btn);
        LinearLayout buttonLayout = (LinearLayout) d.findViewById(R.id.button_layout);
        name.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.VISIBLE);
        continueorder.setVisibility(View.VISIBLE);
        dismiss.setVisibility(View.VISIBLE);
        ok.setVisibility(View.GONE);
        msg.setText("Are you sure you want to delete this moment ?");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        continueorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        d.show();
    }

    private void hideKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void openCaera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void openVide() {
        mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri videoUri = FileProvider.getUriForFile(AddSelfy.this, BuildConfig.APPLICATION_ID + ".provider", mediaFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                videoview.setVisibility(View.VISIBLE);
                filepath = data.getDataString();
                Uri videoURI = Uri.parse(videoPath);
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
                //Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                videoview.setVisibility(View.GONE);
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                final Uri filePath = Utils.getImageUri(AddSelfy.this, imageBitmap);
                filepath = AbsoluteFilePath.getPath(AddSelfy.this, filePath);
            }

        }
    }


    void uploadVideoThumb(final File file, Context context) {

        final String S3Folder = RegistrationConstants.POST_IMAGE;
        String bucket = AWSUtility.getBucketName();
        String fileName = file.getName();
        if (fileName.length() > 20) {
            fileName = fileName.substring(fileName.length() - 20, fileName.length());
        }

        final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
        url = "https://" + bucket + ".s3.amazonaws.com/" + OBJECT_KEY;

        TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
                bucket,     // The bucket to upload to
                OBJECT_KEY,   //  The key for the uploaded object
                file       //  The file where the data to upload exists
        );

        final String finalFileName = fileName;
        mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    uploadSelfy();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {

            }
        });
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                Utils.showToast(AddSelfy.this, "Clippie has been added to moments");
                Intent intent = new Intent(AddSelfy.this, MainDashboardActivity.class);
                intent.putExtra("fragmentload", "");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadSelfy() {
        AsyncWorker mWorker = new AsyncWorker(this);
        mWorker.delegate = this;
        mWorker.delegate = AddSelfy.this;
        JSONObject BroadcastObject = new JSONObject();
        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        try {

            JSONObject video = new JSONObject();
            JSONObject image = new JSONObject();

            video.put("url", url);
            video.put("height", "521");
            video.put("width", "512");

            BroadcastObject.put("postCreatorId", userid);
            BroadcastObject.put("content", desc);
            BroadcastObject.put("title", desc);
            BroadcastObject.put("video", video);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWorker.execute(ServerConnector.REQUEST_ADD_SELFY, BroadcastObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_NO, RequestConstants.REQUEST_FOR_LOGIN);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialog_show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.exoPlayer != null) this.exoPlayer.release();
    }
}
