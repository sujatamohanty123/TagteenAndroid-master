package in.tagteen.tagteen.VideoEditor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bokecc.camerafilter.camera.engine.CameraEngine;
import com.bokecc.camerafilter.camera.engine.CameraParam;
import com.bokecc.camerafilter.camera.recordervideo.PreviewRecorder;
import com.bokecc.camerafilter.camera.render.PreviewRenderer;
import com.bokecc.camerafilter.camera.widget.AspectFrameLayout;
import com.bokecc.camerafilter.camera.widget.CameraTextureView;
import com.bokecc.camerafilter.glfilter.color.bean.DynamicColor;
import com.bokecc.camerafilter.glfilter.resource.FilterHelper;
import com.bokecc.camerafilter.multimedia.VideoCombiner;
import com.bokecc.shortvideo.model.MusicSet;
import com.bokecc.shortvideo.videoedit.HandleProcessListener;
import com.bokecc.shortvideo.videoedit.ShortVideoHelper;
import com.github.guilhe.views.SeekBarRangedView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.VideoEditor.cutvideo.VideoBean;
import in.tagteen.tagteen.VideoEditor.editvideo.CutVideoActivity;
import in.tagteen.tagteen.VideoEditor.editvideo.EditVideoActivity;
import in.tagteen.tagteen.VideoEditor.editvideo.SelectVideoOrImageActivity;
import in.tagteen.tagteen.VideoEditor.presenter.CameraPreviewPresenter;
import in.tagteen.tagteen.VideoEditor.util.FileUtils;
import in.tagteen.tagteen.VideoEditor.util.MultiUtils;
import in.tagteen.tagteen.VideoEditor.widget.BeautyDialog;
import in.tagteen.tagteen.VideoEditor.widget.CustomProgressDialog;
import in.tagteen.tagteen.VideoEditor.widget.FilterDialog;
import in.tagteen.tagteen.VideoEditor.widget.MusicDialog;
import in.tagteen.tagteen.VideoEditor.widget.ProgressView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_flash_light, switchCameraView, play_pause, iv_complete_record,
            iv_delete_last, iv_beauty,iv_filter, iv_countdown_time, iv_countdown, iv_close,ll_select_video;
    RelativeLayout iv_record_video;
    private ProgressView progressView;
    private String videoPath, dirName = "AHuodeShortVideo";
    private int SELECTVIDEOCODE = 1, currentBeautyWhiteValue = 0, currentBeautySkinValue = 0, currentFilter = 0,
            delayRecordTime = 3;
    private Activity activity;
    private LinearLayout  ll_camera_control;
    private TextView tv_record_time;
    private AspectFrameLayout afl_layout;
    private CameraTextureView cameraTextureView;
    private CameraPreviewPresenter mPreviewPresenter;
    // 预览参数
    private CameraParam mCameraParam;
    //是否开启了闪光灯
    private boolean flashOn = false;
    //是否可以启动倒计时拍摄任务
    private boolean isCanStartDelayRecord = true, isOpenDelayRecord = false;
    private Timer delayTimer;
    private DelayRecordTask delayRecordTask;
    private boolean isCanChangeSize = false;
    private int focusSize = 120;

    private IsCanCombineTask isCanCombineTask;
    private Timer isCanCombineTimer;
    private CustomProgressDialog customProgressDialog;
    private boolean isCombineVideo = false;
    private CloseReceiver closeReceiver;
    RelativeLayout img_sound;
    String bacMusicPath;
    private MediaPlayer musicPlayer;
    MusicDialog musicDialog;
    int media_length=0;
    private boolean isMusicPrepared = false;
    int musicDuration;
    boolean music=false;

    TextView tv_cut_music,tv_music_volume,tv_origin_volume_value,tv_music_volume_value,tv_music_start_time,tv_music_time;
    TextView apply;
    ImageView close;
    LinearLayout ll_origin_volume,ll_music_volume,ll_cut_music;
    SeekBar sb_origin_volume,sb_music_volume;
    SeekBarRangedView sb_cut_music;

    Runnable mRunnable;
    Handler mHandler;
    int maxValue,minValue=0;
    int defaultOriginVolume = 50, defaultMusicVolume = 50,startCutMusicPosition = 0,
            tempOriginVolume, tempMusicVolume, tempStartCutMusicPosition;
    private float musicVolume, originVolume,tempMusicValue,tempOriginValue;
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        activity = this;
        MultiUtils.setFullScreen(this);
        //修改录制时长 单位：毫秒
        CameraParam.DEFAULT_RECORD_TIME = 50 * 1000;

        initView();
        resetViewStatus();
        initResources();

        mCameraParam = CameraParam.getInstance();
        mPreviewPresenter = new CameraPreviewPresenter(this);
        mPreviewPresenter.onAttach(activity);

        //注册广播
        closeReceiver = new CloseReceiver();
        IntentFilter intentFilter = new IntentFilter("in.tagteen.tagteen.VideoEditor.CLOSE_MAIN");
        registerReceiver(closeReceiver, intentFilter);
        switchCamera();

        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slow_rotate);
        img_sound.startAnimation(animation);
        img_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicPlayer != null && musicPlayer.isPlaying()) {
                    musicPlayer.stop();
                }
                editMusic();
            }
        });
        beauty_on_off(1);
    }

    private void editMusic() {
        musicDialog = new MusicDialog(activity, bacMusicPath, new MusicDialog.OnSelectMusic() {
            @Override
            public void selectMusic(String musicPath) {
                bacMusicPath = musicPath;
                musicDialog.dismiss();
                dialog_show();
            }

            @Override
            public void cancelMusic() {
                bacMusicPath = null;
                isMusicPrepared = false;
                if (musicPlayer != null) {
                    musicPlayer.pause();
                }
            }
        });
        musicDialog.show();

        musicDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                musicDialog.dismiss();
            }
        });
    }

    private void dialog_show() {
        BottomSheetDialog dialog_payment = new BottomSheetDialog(this, R.style.SheetDialog);
        dialog_payment.setContentView(R.layout.edit_music);
        dialog_payment.setCanceledOnTouchOutside(false);
        tv_cut_music = dialog_payment.findViewById(R.id.tv_cut_music);
        tv_music_volume = dialog_payment.findViewById(R.id.tv_music_volume);
        apply= dialog_payment.findViewById(R.id.apply);
        close= dialog_payment.findViewById(R.id.close);

        ll_origin_volume=dialog_payment.findViewById(R.id.ll_origin_volume);
        tv_origin_volume_value=dialog_payment.findViewById(R.id.tv_origin_volume_value);
        sb_origin_volume=dialog_payment.findViewById(R.id.sb_origin_volume);

        ll_music_volume=dialog_payment.findViewById(R.id.ll_music_volume);
        tv_music_volume_value=dialog_payment.findViewById(R.id.tv_music_volume_value);
        sb_music_volume=dialog_payment.findViewById(R.id.sb_music_volume);

        ll_cut_music=dialog_payment.findViewById(R.id.ll_cut_music);
        tv_music_start_time=dialog_payment.findViewById(R.id.tv_music_start_time);
        sb_cut_music=dialog_payment.findViewById(R.id.sb_cut_music);
        tv_music_time=dialog_payment.findViewById(R.id.tv_music_time);

        tv_cut_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_cut_music.setTextColor(getResources().getColor(R.color.white));
                tv_music_volume.setTextColor(getResources().getColor(R.color.sixtyWhite));
                ll_origin_volume.setVisibility(View.GONE);
                ll_music_volume.setVisibility(View.GONE);
                ll_cut_music.setVisibility(View.VISIBLE);
            }
        });
        tv_music_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_music_volume.setTextColor(getResources().getColor(R.color.white));
                tv_cut_music.setTextColor(getResources().getColor(R.color.sixtyWhite));
                ll_origin_volume.setVisibility(View.VISIBLE);
                ll_music_volume.setVisibility(View.VISIBLE);
                ll_cut_music.setVisibility(View.GONE);
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultOriginVolume = tempOriginVolume;
                setOriginVolume();
                defaultMusicVolume = tempMusicVolume;
                setMusicVolume();
                startCutMusicPosition = tempStartCutMusicPosition;
                musicPlayer.seekTo(startCutMusicPosition);
                musicPlayer.start();
                playProgress();
                dialog_payment.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_payment.dismiss();
                musicPlayer.stop();
            }
        });
        sb_music_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_music_volume_value.setText(progress + "");
                tempMusicVolume = progress;
                tempMusicValue = MultiUtils.calFloat(1, tempMusicVolume, 100);
                musicPlayer.setVolume(tempMusicValue, tempMusicValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_origin_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_origin_volume_value.setText(progress + "");
                tempOriginVolume = progress;
                tempOriginValue = MultiUtils.calFloat(1, tempOriginVolume, 100);
                musicPlayer.setVolume(tempOriginValue, tempOriginValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mHandler = new Handler();
        sb_cut_music.setOnSeekBarRangedChangeListener(new SeekBarRangedView.OnSeekBarRangedChangeListener() {
            @Override
            public void onChanged(SeekBarRangedView view, float min, float max) {
                // musicPlayer.seekTo((int) minValue);

                minValue = (int) min;
                maxValue = (int) max;


                //   it will elminate millisecond part keeping music from exact second to exact second
                minValue = (int) (min/1000) * 1000;
                maxValue = (int) (max/1000) * 1000;

                tempStartCutMusicPosition=minValue;

                tv_music_start_time.setText(MultiUtils.millsecondsToMinuteSecondStr(minValue));
                tv_music_time.setText(MultiUtils.millsecondsToMinuteSecondStr(maxValue));

                if (musicPlayer!=null) {
                    musicPlayer.seekTo(minValue);
                    musicPlayer.start();
                    playProgress();
                }


            }

            @Override
            public void onChanging(SeekBarRangedView view, float min, float max) {
                minValue = (int) min;
                maxValue = (int) max;

                //   it will elminate millisecond part keeping music from exact second to exact second
                minValue = (int) (min/1000) * 1000;
                maxValue = (int) (max/1000) * 1000;

             //   Log.d("max_Value_set",maxValue+" "+minValue+" "+min+" "+max);
             //   tempStartCutMusicPosition=minValue;

                tv_music_start_time.setText(MultiUtils.millsecondsToMinuteSecondStr(minValue));
                tv_music_time.setText(MultiUtils.millsecondsToMinuteSecondStr(maxValue));

              /*  if (musicPlayer!=null) {
                    musicPlayer.seekTo(minValue);
                    musicPlayer.start();
                    playProgress();
                }*/
            }
        });
        audio_range(1,minValue);
        dialog_payment.show();
    }
    private void setMusicVolume() {
        musicVolume = MultiUtils.calFloat(1, defaultMusicVolume, 100);
        musicPlayer.setVolume(musicVolume, musicVolume);
    }

    //设置视频原声音量
    private void setOriginVolume() {
        originVolume = MultiUtils.calFloat(1, defaultOriginVolume, 100);
        musicPlayer.setVolume(originVolume, originVolume);
    }

    private int music_start_point = 0;
    private void audio_range(int flag, int music_start_point) {
        this.music_start_point = music_start_point;
        if (musicPlayer != null && flag==2) {
            music=true;
            Log.d("start_point",music_start_point+"");
           // minValue=music_start_point;
            start_record();
        }else {
            if (musicPlayer == null) {
                musicPlayer = new MediaPlayer();
            }

            try {

                musicPlayer.reset();
                musicPlayer.setDataSource(bacMusicPath);
                musicPlayer.setLooping(true);
                musicPlayer.prepareAsync();
                musicPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        isMusicPrepared = true;
                        musicPlayer.start();

                        tv_music_volume_value.setText(defaultMusicVolume + "");
                        sb_music_volume.setProgress(defaultMusicVolume);
                        tv_origin_volume_value.setText(defaultOriginVolume + "");
                        sb_origin_volume.setProgress(defaultOriginVolume);
                        setMusicVolume();
                        musicDuration = musicPlayer.getDuration();
                        tv_music_start_time.setText(MultiUtils.millsecondsToMinuteSecondStr(0));
                        tv_music_time.setText(MultiUtils.millsecondsToMinuteSecondStr(musicDuration));
                        startCutMusicPosition = 0;
                        sb_cut_music.setMaxValue(musicDuration);
                        maxValue=musicDuration;
                        minValue =0;
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isMusicPrepared && !TextUtils.isEmpty(bacMusicPath)) {
                musicPlayer.start();
            }
        }
    }
    private void playProgress() {
        if(musicPlayer.getCurrentPosition() >= maxValue) {
            musicPlayer.seekTo(minValue);
            musicPlayer.start();
        }
        if (musicPlayer.isPlaying()) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    playProgress();
                }
            };
            mHandler.postDelayed(mRunnable, 0);
        }
    }


    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //预览画面
        afl_layout = findViewById(R.id.afl_layout);
        cameraTextureView = new CameraTextureView(activity);
        cameraTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        cameraTextureView.addCameraViewClickListener(onCameraViewClickListener);
        afl_layout.addView(cameraTextureView);

        img_sound = findViewById(R.id.img_sound);
        iv_beauty = findViewById(R.id.iv_beauty);
        iv_filter = findViewById(R.id.iv_filter);

        play_pause=findViewById(R.id.play_pause);
        iv_record_video = findViewById(R.id.iv_record_video);
        iv_record_video.setOnClickListener(this);

        iv_countdown_time = findViewById(R.id.iv_countdown_time);
        iv_countdown_time.setOnClickListener(this);

        iv_flash_light = findViewById(R.id.iv_flash_light);
        iv_flash_light.setOnClickListener(this);

        switchCameraView = findViewById(R.id.iv_switch_camera);
        switchCameraView.setOnClickListener(this);


        iv_countdown = findViewById(R.id.iv_countdown);

        iv_complete_record = findViewById(R.id.iv_complete_record);
        iv_complete_record.setOnClickListener(this);

        iv_delete_last = findViewById(R.id.iv_delete_last);
        iv_delete_last.setOnClickListener(this);

        iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        iv_beauty.setOnClickListener(this);
        iv_filter.setOnClickListener(this);

        ll_select_video = findViewById(R.id.ll_select_video);
        ll_select_video.setOnClickListener(this);

        tv_record_time = findViewById(R.id.tv_record_time);

        progressView = findViewById(R.id.progress_view);
        ll_camera_control = findViewById(R.id.ll_camera_control);
        progressView.setData();
    }

    /**
     * 初始化滤镜资源
     */
    private void initResources() {
        new Thread(() -> {
            FilterHelper.initAssetsFilter(activity);
        }).start();
    }



    private String getOutPutPath() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + dirName, "RecordVideo");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".mp4";
    }


    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mPreviewPresenter.bindSurface(surface);
            mPreviewPresenter.changePreviewSize(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mPreviewPresenter.changePreviewSize(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    TimerTask captureTimerTask;

    public void completeRecordProgress()
    {
        //Log.d("FINLdurationcompleted","dd");


       /* activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startIsCanCombineTimer();
                tv_record_time.setVisibility(View.GONE);
            }});*/
    }

    //更新录制进度
    public void updateRecordProgress(long duration) {


        progressView.setCurrentDuration((int) duration);
        Log.d("duration",duration+" "+(int) (duration / 1000));
        if ((maxValue!=0 && duration >= (maxValue-minValue+200) )|| (musicPlayer==null && duration >= CameraParam.DEFAULT_RECORD_TIME) || (musicPlayer!=null && !musicPlayer.isPlaying() &&duration >= CameraParam.DEFAULT_RECORD_TIME)) {
            Log.d("FINLduration",duration+" "+(int) (duration / 1000));
            mPreviewPresenter.stopRecord();
            startIsCanCombineTimer();
            tv_record_time.setVisibility(View.GONE);
        } else {
            int recordTime = (int) (duration / 1000);
            tv_record_time.setText(recordTime + "s");
            tv_record_time.setVisibility(View.VISIBLE);
        }

    }

    private void stopCaptureTimer() {
        if (captureTimerTask != null) {
            captureTimerTask.cancel();
        }
    }

    private void resetViewStatus() {
        used = false;
        stopCaptureTimer();
        resetFinishCaptureView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_record_video:
                if (bacMusicPath!=null && !bacMusicPath.equalsIgnoreCase("")) {
                    if (flag==0){
                        audio_range(2, minValue);
                    }else {
                        audio_range(2, musicPlayer.getCurrentPosition());
                    }
                }else {
                    start_record();
                }
                break;
            case R.id.iv_delete_last:
                deleteLastVideo();
                break;
            case R.id.iv_complete_record:
                completeRecord();
                break;
            case R.id.iv_flash_light:
                switchFlash();
                break;
            case R.id.iv_switch_camera:
                switchCamera();
                break;

            case R.id.iv_filter:
                FilterDialog filterDialog = new FilterDialog(activity, currentFilter, new FilterDialog.OnSelectFilter() {
                    @Override
                    public void selectFilter(int filterPos, DynamicColor color) {
                        currentFilter = filterPos;
                        mPreviewPresenter.changeDynamicFilter(color);
                    }
                });
                filterDialog.show();
                break;

            case R.id.iv_beauty:
               beauty_on_off(0);

                BeautyDialog beautyDialog = new BeautyDialog(activity, currentBeautyWhiteValue, currentBeautySkinValue, new BeautyDialog.OnBeauty() {
                    @Override
                    public void getBeautyWhiteValue(int value) {
                        currentBeautyWhiteValue = value;
                    }

                    @Override
                    public void getBeautySkinValue(int value) {
                        currentBeautySkinValue = value;
                    }
                });
                beautyDialog.show();
                break;
            case R.id.ll_select_video:
                startActivityForResult(new Intent(activity, SelectVideoOrImageActivity.class), SELECTVIDEOCODE);
                break;
            case R.id.iv_countdown_time:
                if (!isCanStartDelayRecord) {
                    return;
                }
                isOpenDelayRecord = !isOpenDelayRecord;
                if (isOpenDelayRecord) {
                    iv_countdown_time.setImageResource(R.mipmap.iv_countdown_on);
                } else {
                    iv_countdown_time.setImageResource(R.mipmap.iv_countdown_off);
                }
                break;

            case R.id.iv_close:
                if (mPreviewPresenter.isRecording()) {
                    MultiUtils.showToast(activity, "请稍候");
                } else {
                    mPreviewPresenter.removeAllSubVideo();
                    resetView();
                }

                break;

        }
    }

    private void beauty_on_off(int flag) {
        if (flag==1) {
            iv_beauty.setImageResource(R.mipmap.iv_beauty_on);
            if (currentBeautySkinValue == 0) {
                currentBeautySkinValue = 15;
                currentBeautyWhiteValue =30;
                mCameraParam.beauty.beautyIntensity = 0.5f;
                mCameraParam.beauty.complexionIntensity = 0.5f;
            }
        }else {
            iv_beauty.setImageResource(R.mipmap.iv_beauty_on);
                currentBeautySkinValue = 0;
                currentBeautyWhiteValue = 0;
                mCameraParam.beauty.beautyIntensity = 0.0f;
                mCameraParam.beauty.complexionIntensity = 0.0f;
        }
    }

    private void completeRecord() {
        if (!(iv_delete_last.getVisibility() == View.VISIBLE)) {
            mPreviewPresenter.stopRecord();
        }
        startIsCanCombineTimer();
    }

    private void start_record() {
        if (music && !used){
            progressView.setMaxDuration(maxValue-minValue+200);
            PreviewRecorder.getInstance().setRecordTime(maxValue-minValue+200);
        }else if(!used){
            progressView.setMaxDuration(CameraParam.DEFAULT_RECORD_TIME);
            PreviewRecorder.getInstance().setRecordTime(CameraParam.DEFAULT_RECORD_TIME);
        }

        if (isOpenDelayRecord) {
            if (isCanStartDelayRecord) {
                startDelayRecordTask();
            }
        } else {
            switchRecordStatus();
        }
    }

    //预览画面点击事件
    private CameraTextureView.OnCameraViewClickListener onCameraViewClickListener = new CameraTextureView.OnCameraViewClickListener() {
        @Override
        public void onCameraViewClick(float x, float y) {
            CameraEngine.getInstance().setFocusArea(CameraEngine.getFocusArea((int) x, (int) y,
                    cameraTextureView.getWidth(), cameraTextureView.getHeight(), focusSize));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cameraTextureView.showFocusAnimation();
                }
            });
        }
    };

    private void startDelayRecordTask() {
        isCanStartDelayRecord = false;
        cancelDelayRecordTask();
        delayRecordTime = 3;
        iv_countdown.setImageResource(R.mipmap.iv_countdown_three);
        iv_countdown.setVisibility(View.VISIBLE);
        iv_countdown_time.setImageResource(R.mipmap.iv_countdown_on);
        delayTimer = new Timer();
        delayRecordTask = new DelayRecordTask();
        delayTimer.schedule(delayRecordTask, 1000, 1000);
    }

    private void cancelDelayRecordTask() {
        if (delayTimer != null) {
            delayTimer.cancel();
        }
        if (delayRecordTask != null) {
            delayRecordTask.cancel();
        }
    }

    // 倒计时拍摄任务
    class DelayRecordTask extends TimerTask {
        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    delayRecordTime = delayRecordTime - 1;
                    if (delayRecordTime == 2) {
                        iv_countdown.setImageResource(R.mipmap.iv_countdown_two);
                    } else if (delayRecordTime == 1) {
                        iv_countdown.setImageResource(R.mipmap.iv_countdown_one);
                    } else if (delayRecordTime == 0) {
                        iv_countdown.setVisibility(View.GONE);
                        switchRecordStatus();
                    } else {
                        isOpenDelayRecord = false;
                        isCanStartDelayRecord = true;
                        iv_countdown_time.setImageResource(R.mipmap.iv_countdown_off);
                        cancelDelayRecordTask();
                    }

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SELECTVIDEOCODE) {
            videoPath = data.getStringExtra("path");

            if (TextUtils.isEmpty(videoPath)) {
                MultiUtils.showToast(activity, "The file is wrong, please select again");
                return;
            }
            long videoSize = FileUtils.getFileSize(videoPath);
            if (videoSize > 100 * 1024 * 1024) {
                MultiUtils.showToast(activity, "Video size cannot exceed 100MB");
                return;
            }
            VideoBean localVideoInfo = MultiUtils.getLocalVideoInfo(videoPath);
            int rotation = localVideoInfo.rotation;
            long duration = localVideoInfo.duration;
            if (duration > 3 * 60 * 1000) {
                MultiUtils.showToast(activity, "Video duration cannot exceed 3 minutes");
                return;
            }
            startActivity(new Intent(activity, CutVideoActivity.class).putExtra("videoPath", videoPath).putExtra("rotation", rotation));
            finish();
        }

    }

    private void deleteLastVideo() {
        if (PreviewRecorder.getInstance().getNumberOfSubVideo() < 1) {
            MultiUtils.showToast(activity, "Please record a video first");
        }

        if (mPreviewPresenter.isRecording()) {
            MultiUtils.showToast(activity, "During recording, please stop recording and delete");
            return;
        }


        PreviewRecorder.getInstance().removeLastSubVideo();
        resetFinishCaptureView();

        int currentDuration = PreviewRecorder.getInstance().getDuration();
        int recordTime = currentDuration / 1000;
       // int time=recordTime+1;
        tv_record_time.setText(recordTime + "s");
    }

    private void resetFinishCaptureView() {
        if (PreviewRecorder.getInstance().getNumberOfSubVideo() > 0) {
            iv_complete_record.setVisibility(View.VISIBLE);
            iv_delete_last.setVisibility(View.VISIBLE);
            iv_close.setVisibility(View.VISIBLE);
        } else {
            resetView();
        }
    }

    private void startIsCanCombineTimer() {
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.stop();
        }
        isCombineVideo = false;
        cancelIsCanCombineTimer();
        customProgressDialog = new CustomProgressDialog(activity);
        customProgressDialog.setCanceledOnTouchOutside(false);
        customProgressDialog.show();

        isCanCombineTimer = new Timer();
        isCanCombineTask = new IsCanCombineTask();
        isCanCombineTimer.schedule(isCanCombineTask, 0, 30);
    }


    private void cancelIsCanCombineTimer() {

        if (isCanCombineTimer != null) {
            isCanCombineTimer.cancel();
        }
        if (isCanCombineTask != null) {
            isCanCombineTask.cancel();
        }
    }


    class IsCanCombineTask extends TimerTask {
        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mPreviewPresenter.isRecording() && !isCombineVideo) {
                        cancelIsCanCombineTimer();
                        isCombineVideo = true;
                        combineVideo();
                    }
                }
            });
        }
    }


    //合成视频
    public void combineVideo() {
        mPreviewPresenter.destroyRecorder();
        //分段视频的数量
        int numberOfSubVideo = mPreviewPresenter.getNumberOfSubVideo();
        if (numberOfSubVideo > 1) {
            videoPath = getOutPutPath();
            PreviewRecorder.getInstance().combineVideo(videoPath, mCombineListener);
        } else {
            List<String> subVideoPathList = PreviewRecorder.getInstance().getSubVideoPathList();
            if (subVideoPathList != null && subVideoPathList.size() > 0) {
                videoPath = subVideoPathList.get(0);
                startEdit();
            } else {
                startEdit();
            }
        }
    }

    // 合成监听器
    private VideoCombiner.CombineListener mCombineListener = new VideoCombiner.CombineListener() {
        @Override
        public void onCombineStart() {

        }

        @Override
        public void onCombineProcessing(final int current, final int sum) {

        }

        @Override
        public void onCombineFinished(final boolean success) {
            startEdit();
        }
    };

    private void startEdit() {
        mPreviewPresenter.removeAllSubVideo();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (music) {
                    addMusic();
                }else {
                    intoEditActivity();
                    resetView();
                }
            }
        });
    }
    private void addMusic() {
        String outPutAddStickerPath = MultiUtils.getOutPutVideoPath();
        MusicSet musicSet = null;
            musicSet = new MusicSet();
            int startMusicPos = startCutMusicPosition / 1000;
            musicSet.setMusicPath(bacMusicPath);
            musicSet.setStartMusicPos(startMusicPos);
            musicSet.setOriginVolume(0);
            musicSet.setMusicVolume(musicVolume);
        ShortVideoHelper.AddMusicAndSticker(activity, videoPath, outPutAddStickerPath, musicSet, null, new HandleProcessListener() {
            @Override
            public void onStart() {

            }


            @Override
            public void onFinish() {

                MultiUtils.deleteFile(videoPath);
                videoPath = outPutAddStickerPath;
                handleVideoSuccess();
            }

            @Override
            public void onFail(int errorCode, String msg) {
                handleVideoFail();
            }
        });
    }
    private void handleVideoFail() {
        MultiUtils.showToast(activity, "Processing failed");
    }

    private void handleVideoSuccess() {
        MultiUtils.showToast(activity, "Processed successfully");
        intoEditActivity();
        resetView();
    }

    private void resetView() {
        iv_beauty.setVisibility(View.VISIBLE);
        ll_camera_control.setVisibility(View.VISIBLE);
        iv_filter.setVisibility(View.VISIBLE);
        ll_select_video.setVisibility(View.VISIBLE);
        tv_record_time.setVisibility(View.GONE);
        iv_delete_last.setVisibility(View.GONE);
        iv_close.setVisibility(View.GONE);
        iv_complete_record.setVisibility(View.GONE);
        used=false;
    }

    private void intoEditActivity() {

        if (customProgressDialog != null && customProgressDialog.isShowing()) {
            customProgressDialog.dismiss();
            customProgressDialog = null;
        }

        if (mCameraParam.supportFlash) {
            CameraEngine.getInstance().setFlashLight(false);
        }
        iv_flash_light.setImageResource(R.mipmap.iv_flash_off);
        Intent intent = new Intent(activity, EditVideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("isRecord", true);
        intent.putExtra("AudioPlay", music);
        intent.putExtra("AudioPath", bacMusicPath);
        intent.putExtra("maxValue",maxValue);
        intent.putExtra("minValue",minValue);
        startActivity(intent);
        finish();

    }

    private void switchFlash() {
        if (mCameraParam.supportFlash) {
            boolean flashLightOn = CameraEngine.getInstance().isFlashLightOn();
            flashOn = !flashLightOn;
            CameraEngine.getInstance().setFlashLight(flashOn);
            if (flashOn) {
                iv_flash_light.setImageResource(R.mipmap.iv_flash_on);
            } else {
                iv_flash_light.setImageResource(R.mipmap.iv_flash_off);
            }
        }
    }

    private void switchCamera() {
        mPreviewPresenter.switchCamera();
        iv_flash_light.setImageResource(R.mipmap.iv_flash_off);
    }

    boolean used = false;
    private void switchRecordStatus() {
        iv_filter.setVisibility(View.GONE);
        iv_beauty.setVisibility(View.GONE);
        ll_select_video.setVisibility(View.GONE);
        ll_camera_control.setVisibility(View.GONE);
        if (mPreviewPresenter.isRecording()) {
            //暂停录制
            // pause
         //   flag = 0;
            iv_delete_last.setVisibility(View.VISIBLE);
            iv_close.setVisibility(View.VISIBLE);
            iv_complete_record.setVisibility(View.GONE);
            //iv_record_video.setImageResource(R.mipmap.iv_record_video);
            play_pause.setImageResource(R.drawable.ic_play);
            mPreviewPresenter.stopRecord();
            progressView.stop();
            if (musicPlayer != null) {
                musicPlayer.pause();
            }
        } else {
            used = true;
            // playing
            //开始录制
            flag=1;
            iv_delete_last.setVisibility(View.GONE);
            iv_close.setVisibility(View.GONE);
            iv_complete_record.setVisibility(View.VISIBLE);
            //iv_record_video.setImageResource(R.mipmap.iv_recording_video);
            play_pause.setImageResource(R.drawable.ic_pause);
            int width = mCameraParam.DEFAULT_9_16_WIDTH;
            int height = mCameraParam.DEFAULT_9_16_HEIGHT;
            videoPath = getOutPutPath();
            progressView.start();
            mPreviewPresenter.startRecord(width, height, videoPath);
            if (music) {
                musicPlayer.seekTo(music_start_point);
                musicPlayer.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        resetViewStatus();
        if (musicPlayer!=null) {
            musicPlayer.pause();
            media_length = musicPlayer.getCurrentPosition();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (flashOn) {
            CameraEngine.getInstance().setFlashLight(!flashOn);
            iv_flash_light.setImageResource(R.mipmap.iv_flash_off);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isCanChangeSize) {
            mCameraParam.backCamera = !mCameraParam.backCamera;
            mPreviewPresenter.switchCamera();

            if (flashOn) {
                iv_flash_light.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CameraEngine.getInstance().setFlashLight(flashOn);
                        iv_flash_light.setImageResource(R.mipmap.iv_flash_on);
                    }
                }, 500);
            }
        }
        isCanChangeSize = true;

        if (musicPlayer!=null && media_length>0/*musicPlayer.isPlaying()*/) {
            musicPlayer.seekTo(media_length);
            musicPlayer.start();
        }
    }

    class CloseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreviewPresenter.removeAllSubVideo();
        PreviewRenderer.getInstance().stopRecording();
        CameraEngine.getInstance().releaseCamera();

        mPreviewPresenter.onDestroy();
        mPreviewPresenter.onDetach();
        mPreviewPresenter = null;
        cancelDelayRecordTask();
        cancelIsCanCombineTimer();

        if (closeReceiver != null) {
            unregisterReceiver(closeReceiver);
        }
    }
}