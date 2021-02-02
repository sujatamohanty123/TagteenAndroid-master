package in.tagteen.tagteen.VideoEditor.editvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.shortvideo.videoedit.HandleProcessListener;
import com.bokecc.shortvideo.videoedit.ShortVideoHelper;

import java.io.IOException;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.VideoEditor.MainActivity;
import in.tagteen.tagteen.VideoEditor.cutvideo.VideoBean;
import in.tagteen.tagteen.VideoEditor.util.MultiUtils;
import in.tagteen.tagteen.VideoEditor.widget.CustomProgressDialog;
import in.tagteen.tagteen.VideoEditor.widget.CutView;
import in.tagteen.tagteen.VideoEditor.widget.DurView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CutVideoActivity extends Activity implements View.OnClickListener, DurView.IOnRangeChangeListener, TextureView.SurfaceTextureListener {

    private TextureView tv_video;
    private Surface playSurface;
    private String videoPath, originalPath;
    //视频的角度信息
    private int rotation = 0, originalRotation = 0;
    private Activity activity;
    private DurView dv_view;
    private VideoBean videoBean;
    private ImageView iv_back, iv_cut_ratio, iv_edit_speed;
    private TextView tv_next, tv_selected_time, tv_cut_start_time, tv_cut_end_time, tv_original_rate, tv_one_to_one,
            tv_four_to_three, tv_sixteen_to_nine, tv_nine_to_sixteen, tv_extreme_slow, tv_slow, tv_standard,
            tv_high, tv_extreme_high;
    private CutView cv_view;
    private RelativeLayout rl_video;
    private int videoWidth, videoHeight, leftMargin, topMargin, rightMargin, bottomMargin,
            windowWidth, windowHeight, afterChangeVideoHeight, startCutTime, endCutTime,
            standardStartTime, standardEndTime, startCutShowTime, endCutShowTime, selectCutShowTime, afterChangeVideoWidth;

    private int videoDuration, currentRatio = 2;
    private IjkMediaPlayer videoPlayer;
    private LinearLayout ll_cut_and_speed, ll_edit_ratio, ll_edit_speed;
    private boolean isEditVideoSpeed = false, isChangeVideoTime = false, isCutVideoSize = false;
    private float videoSpeed = 1.0f;
    private CustomProgressDialog handleProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_video);
        activity = this;
        initView();
        initPlay();
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        windowWidth = MultiUtils.getScreenWidth(activity);
        windowHeight = MultiUtils.getScreenHeight(activity);

        tv_video = findViewById(R.id.tv_video);
        tv_video.setSurfaceTextureListener(this);
        dv_view = findViewById(R.id.dv_view);
        dv_view.setRangeChangeListener(this);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_next = findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);

        tv_selected_time = findViewById(R.id.tv_selected_time);
        cv_view = findViewById(R.id.cv_view);

        tv_cut_start_time = findViewById(R.id.tv_cut_start_time);
        tv_cut_end_time = findViewById(R.id.tv_cut_end_time);
        ll_cut_and_speed = findViewById(R.id.ll_cut_and_speed);
        ll_edit_ratio = findViewById(R.id.ll_edit_ratio);
        ll_edit_speed = findViewById(R.id.ll_edit_speed);
        rl_video = findViewById(R.id.rl_video);

        iv_cut_ratio = findViewById(R.id.iv_cut_ratio);
        iv_cut_ratio.setOnClickListener(this);
        iv_edit_speed = findViewById(R.id.iv_edit_speed);
        iv_edit_speed.setOnClickListener(this);

        tv_original_rate = findViewById(R.id.tv_original_rate);
        tv_original_rate.setOnClickListener(this);
        tv_one_to_one = findViewById(R.id.tv_one_to_one);
        tv_one_to_one.setOnClickListener(this);
        tv_four_to_three = findViewById(R.id.tv_four_to_three);
        tv_four_to_three.setOnClickListener(this);
        tv_sixteen_to_nine = findViewById(R.id.tv_sixteen_to_nine);
        tv_sixteen_to_nine.setOnClickListener(this);
        tv_nine_to_sixteen = findViewById(R.id.tv_nine_to_sixteen);
        tv_nine_to_sixteen.setOnClickListener(this);
        tv_extreme_slow = findViewById(R.id.tv_extreme_slow);
        tv_extreme_slow.setOnClickListener(this);
        tv_slow = findViewById(R.id.tv_slow);
        tv_slow.setOnClickListener(this);
        tv_standard = findViewById(R.id.tv_standard);
        tv_standard.setOnClickListener(this);
        tv_high = findViewById(R.id.tv_high);
        tv_high.setOnClickListener(this);
        tv_extreme_high = findViewById(R.id.tv_extreme_high);
        tv_extreme_high.setOnClickListener(this);

        cv_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isCutVideoSize = true;
                return false;
            }
        });

    }


    private void initPlay() {
        videoPath = getIntent().getStringExtra("videoPath");
        originalPath = videoPath;
        rotation = getIntent().getIntExtra("rotation", 0);
        originalRotation = rotation;
        videoPlayer = new IjkMediaPlayer();
        try {
            videoPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            videoPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            videoPlayer.setDataSource(videoPath);
            videoPlayer.setLooping(true);
            videoPlayer.setSurface(playSurface);
            videoPlayer.prepareAsync();
            videoPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    videoPlayer.start();
                    videoDuration = (int) videoPlayer.getDuration();

                    videoSpeed = 1.0f;
                    standardStartTime = 0;
                    standardEndTime = (int) videoPlayer.getDuration();
                    setSelectTime();

                    //获取视频的宽和高
                    if (rotation == 90 || rotation == 270) {
                        videoWidth = videoPlayer.getVideoHeight();
                        videoHeight = videoPlayer.getVideoWidth();
                    } else {
                        videoWidth = videoPlayer.getVideoWidth();
                        videoHeight = videoPlayer.getVideoHeight();
                    }

                    afterChangeVideoHeight = videoHeight * windowWidth / videoWidth;

                    //设置视频的宽和高
                    ViewGroup.LayoutParams videoParams = tv_video.getLayoutParams();
                    videoParams.height = afterChangeVideoHeight;
                    videoParams.width = windowWidth;
                    tv_video.setLayoutParams(videoParams);

                    videoBean = MultiUtils.getLocalVideoInfo(videoPath);
                    dv_view.setMediaFileInfo(videoBean);

                    int height = ll_cut_and_speed.getHeight();
                    int maxVideoHeight = windowHeight - height;
                    afterChangeVideoWidth = windowWidth;
                    if (afterChangeVideoHeight > maxVideoHeight) {
                        afterChangeVideoHeight = maxVideoHeight;
                        afterChangeVideoWidth = videoWidth * maxVideoHeight / videoHeight;
                        ViewGroup.LayoutParams layoutParams = rl_video.getLayoutParams();
                        layoutParams.width = afterChangeVideoWidth;
                        layoutParams.height = maxVideoHeight;
                        rl_video.setLayoutParams(layoutParams);

                        ViewGroup.LayoutParams layoutParams1 = cv_view.getLayoutParams();
                        layoutParams1.width = afterChangeVideoWidth;
                        layoutParams1.height = maxVideoHeight;
                        cv_view.setLayoutParams(layoutParams1);

                        ViewGroup.LayoutParams layoutParams2 = tv_video.getLayoutParams();
                        layoutParams2.width = afterChangeVideoWidth;
                        layoutParams2.height = maxVideoHeight;
                        tv_video.setLayoutParams(layoutParams2);

                    }
                    leftMargin = 0;
                    rightMargin = 0;

                    topMargin = (windowHeight - height - afterChangeVideoHeight) / 2;
                    bottomMargin = topMargin;
                    cv_view.setMargin(leftMargin, topMargin, rightMargin, bottomMargin);
                    cv_view.setVisibility(View.VISIBLE);
                    cv_view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setCutRation(4);
                        }
                    }, 1);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                handleProgressDialog = new CustomProgressDialog(activity);
                handleProgressDialog.show();
                if (videoSpeed == 1.0f) {
                    if (isCutVideoSize || isChangeVideoTime) {
                        cutVideo(false);
                    } else {
                        changeVideoSize();
                    }
                } else {
                    changeSpeed();
                }
                break;

            case R.id.iv_back:
                startActivity(new Intent(CutVideoActivity.this, MainActivity.class));
                finish();
                break;

            case R.id.tv_original_rate:
                setCutRation(4);
                break;
            case R.id.tv_one_to_one:
                setCutRation(0);
                break;

            case R.id.tv_four_to_three:
                setCutRation(1);
                break;

            case R.id.tv_sixteen_to_nine:
                setCutRation(2);
                break;

            case R.id.tv_nine_to_sixteen:
                setCutRation(3);
                break;
            case R.id.iv_cut_ratio:
                switchCutRatio();
                break;

            case R.id.iv_edit_speed:
                setVideoSpeed(2);
                isEditVideoSpeed = true;
                ll_edit_ratio.setVisibility(View.GONE);
                ll_edit_speed.setVisibility(View.VISIBLE);
                iv_edit_speed.setImageResource(R.mipmap.iv_speed_on);
                resetEditRatioImage();
                break;
            case R.id.tv_extreme_slow:
                setVideoSpeed(0);
                break;

            case R.id.tv_slow:
                setVideoSpeed(1);
                break;
            case R.id.tv_standard:
                setVideoSpeed(2);
                break;

            case R.id.tv_high:
                setVideoSpeed(3);
                break;

            case R.id.tv_extreme_high:
                setVideoSpeed(4);
                break;

        }
    }

    private void getVideoRotation() {
        VideoBean localVideoInfo = MultiUtils.getLocalVideoInfo(videoPath);
        rotation = localVideoInfo.rotation;
    }


    private void changeSpeed() {
        final String outPutChangeSpeedPath = MultiUtils.getOutPutVideoPath();
        ShortVideoHelper.changeVideoSpeed(activity, videoPath, outPutChangeSpeedPath, videoSpeed, new HandleProcessListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                videoPath = outPutChangeSpeedPath;
                getVideoRotation();
                if (isCutVideoSize || isChangeVideoTime) {
                    cutVideo(true);
                } else {
                    changeVideoSize();
                }
            }

            @Override
            public void onFail(int errorCode, String msg) {
                handleVideoFail();
            }

        });

    }

    private void handleVideoFail() {
        handleProgressDialog.dismiss();
        MultiUtils.showToast(activity, "Processing failed");
    }


    private void switchCutRatio() {
        cv_view.setVisibility(View.VISIBLE);
        isEditVideoSpeed = false;
        ll_edit_ratio.setVisibility(View.VISIBLE);
        ll_edit_speed.setVisibility(View.GONE);
        iv_edit_speed.setImageResource(R.mipmap.iv_speed_off);
        resetEditRatioImage();
    }

    //设置裁剪尺寸比例
    private void setCutRation(int ratioFlag) {
        cv_view.setVisibility(View.VISIBLE);
        currentRatio = ratioFlag;
        isCutVideoSize = true;
        tv_original_rate.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_one_to_one.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_four_to_three.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_sixteen_to_nine.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_nine_to_sixteen.setBackgroundColor(getResources().getColor(R.color.transparent));
        if (ratioFlag == 0) {
            cv_view.setCutRatio(1, 1);
            iv_cut_ratio.setImageResource(R.mipmap.iv_one_one_checked);
            tv_one_to_one.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        } else if (ratioFlag == 1) {
            cv_view.setCutRatio(4, 3);
            iv_cut_ratio.setImageResource(R.mipmap.iv_four_three_checked);
            tv_four_to_three.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        } else if (ratioFlag == 2) {
            cv_view.setCutRatio(16, 9);
            iv_cut_ratio.setImageResource(R.mipmap.iv_sixteen_nine_checked);
            tv_sixteen_to_nine.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        } else if (ratioFlag == 3) {
            cv_view.setCutRatio(9, 16);
            iv_cut_ratio.setImageResource(R.mipmap.iv_nine_sixteen_checked);
            tv_nine_to_sixteen.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        } else if (ratioFlag == 4) {
            isCutVideoSize = false;
            cv_view.setCutRatio(videoWidth, videoHeight);
            iv_cut_ratio.setImageResource(R.mipmap.iv_original_rate_checked);
            tv_original_rate.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        }
    }

    //设置视频速度
    private void setVideoSpeed(int speedFlag) {
        cv_view.setVisibility(View.GONE);
        tv_extreme_slow.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_slow.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_standard.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_high.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_extreme_high.setBackgroundColor(getResources().getColor(R.color.transparent));
        if (speedFlag == 0) {
            videoSpeed = 0.5f;
            tv_extreme_slow.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        } else if (speedFlag == 1) {
            videoSpeed = 0.75f;
            tv_slow.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        } else if (speedFlag == 2) {
            videoSpeed = 1.0f;
            tv_standard.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        } else if (speedFlag == 3) {
            videoSpeed = 1.5f;
            tv_high.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        } else if (speedFlag == 4) {
            videoSpeed = 2.0f;
            tv_extreme_high.setBackgroundResource(R.drawable.orange_fifteen_corner_bac);
        }

        videoPlayer.setSpeed(videoSpeed);
        setSelectTime();

    }

    private void setSelectTime() {
        float timeRatio = MultiUtils.calTimeRatio(2, 1.0f, videoSpeed);
        startCutTime = (int) (timeRatio * standardStartTime);
        endCutTime = (int) (timeRatio * standardEndTime);
        startCutShowTime = startCutTime / 1000;
        endCutShowTime = endCutTime / 1000;
        selectCutShowTime = endCutShowTime - startCutShowTime;
        tv_cut_start_time.setText(startCutShowTime + "s");
        tv_cut_end_time.setText(endCutShowTime + "s");
        tv_selected_time.setText("Selected" + selectCutShowTime + "s");
    }

    private void resetEditRatioImage() {
        if (currentRatio == 0) {
            if (isEditVideoSpeed) {
                iv_cut_ratio.setImageResource(R.mipmap.iv_one_one_unchecked);
            } else {
                iv_cut_ratio.setImageResource(R.mipmap.iv_one_one_checked);
            }
        } else if (currentRatio == 1) {
            if (isEditVideoSpeed) {
                iv_cut_ratio.setImageResource(R.mipmap.iv_four_three_unchecked);
            } else {
                iv_cut_ratio.setImageResource(R.mipmap.iv_four_three_checked);
            }
        } else if (currentRatio == 2) {
            if (isEditVideoSpeed) {
                iv_cut_ratio.setImageResource(R.mipmap.iv_sixteen_nine_unchecked);
            } else {
                iv_cut_ratio.setImageResource(R.mipmap.iv_sixteen_nine_checked);
            }
        } else if (currentRatio == 3) {
            if (isEditVideoSpeed) {
                iv_cut_ratio.setImageResource(R.mipmap.iv_nine_sixteen_unchecked);
            } else {
                iv_cut_ratio.setImageResource(R.mipmap.iv_nine_sixteen_checked);
            }
        } else if (currentRatio == 4) {
            if (isEditVideoSpeed) {
                iv_cut_ratio.setImageResource(R.mipmap.iv_original_rate_unchecked);
            } else {
                iv_cut_ratio.setImageResource(R.mipmap.iv_original_rate_checked);
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        playSurface = new Surface(surface);
        videoPlayer.setSurface(playSurface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    //裁剪视频
    private void cutVideo(boolean isChangeSpeed) {
        //得到裁剪后的margin值
        float[] cutArr = cv_view.getCutArr();
        float left = cutArr[0];
        float top = cutArr[1];
        float right = cutArr[2];
        float bottom = cutArr[3];
        int cutWidth = cv_view.getRectWidth();
        int cutHeight = cv_view.getRectHeight();

        //计算宽高缩放比
        float leftPro = left / cutWidth;
        float topPro = top / cutHeight;
        float rightPro = right / cutWidth;
        float bottomPro = bottom / cutHeight;

        //得到裁剪位置
        int cropWidth = (int) (videoWidth * (rightPro - leftPro));
        int cropHeight = (int) (videoHeight * (bottomPro - topPro));

        int x = (int) (leftPro * videoWidth);
        int y = (int) (topPro * videoHeight);

        videoWidth = cropWidth;
        videoHeight = cropHeight;

        String outPutCutPath = MultiUtils.getOutPutVideoPath();
        ShortVideoHelper.cutVideo(activity, videoPath, outPutCutPath, startCutTime, endCutTime, cropWidth, cropHeight, x, y, new HandleProcessListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                if (isChangeSpeed) {
                    MultiUtils.deleteFile(videoPath);
                }
                videoPath = outPutCutPath;
                getVideoRotation();
                changeVideoSize();
            }

            @Override
            public void onFail(int errorCode, String msg) {
                handleVideoFail();
            }

        });

    }

    private void changeVideoSize() {
        if (videoWidth == 720 && videoHeight == 1280) {
            IntoEdit();
        } else {
            String outPutChangeVideoSizePath = MultiUtils.getOutPutVideoPath();
            ShortVideoHelper.changeVideoSize(activity, videoPath, outPutChangeVideoSizePath, videoWidth, videoHeight, 720, 1280, new HandleProcessListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    if (isEditVideoSpeed || isChangeVideoTime) {
                        MultiUtils.deleteFile(videoPath);
                    }
                    videoPath = outPutChangeVideoSizePath;
                    getVideoRotation();
                    IntoEdit();
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    handleVideoFail();
                }
            });
        }

    }

    private void IntoEdit() {
        handleProgressDialog.dismiss();
        Intent intent = new Intent(activity, EditVideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("rotation", rotation);
        intent.putExtra("isRecord", false);
        intent.putExtra("originalPath", originalPath);
        intent.putExtra("originalRotation", originalRotation);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCutViewDown() {

    }

    @Override
    public void onCutViewUp(int startTime, int endTime) {
        isChangeVideoTime = true;
        standardStartTime = startTime;
        standardEndTime = endTime;
        setSelectTime();
        videoPlayer.seekTo(startTime);
    }

    @Override
    public void onCutViewPreview(int previewTime) {
        videoPlayer.seekTo(previewTime);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (videoPlayer != null) {
            videoPlayer.start();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoPlayer != null) {
            videoPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (videoPlayer != null) {
            videoPlayer.pause();
            videoPlayer.stop();
            videoPlayer.release();
        }

    }

    @Override
    public void onBackPressed() {
        if (videoPlayer != null) {
            videoPlayer.pause();
            videoPlayer.stop();
            videoPlayer.release();
        }
        startActivity(new Intent(CutVideoActivity.this, MainActivity.class));
        super.onBackPressed();
    }
}
