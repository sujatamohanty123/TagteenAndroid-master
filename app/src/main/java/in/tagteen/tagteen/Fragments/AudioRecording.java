package in.tagteen.tagteen.Fragments;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.SendAudioFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioRecording extends Fragment implements View.OnClickListener {

        private TextView mTimerTextView;
        private MediaRecorder mRecorder;
        private long mStartTime = 0;
        private ImageView mediaImageView;
        private ImageView sendImage;
        private ImageView deleteImage;
        private int[] amplitudes = new int[100];
        private int i = 0;
        private Handler mHandler = new Handler();

    private int StateOfPlayer =0;
    private int START_RECORDING =0;
    private int STOP_RECORDING  = 1;
    private int PLAY_RECORDING  = 2;
    private int PAUSE_RECORDING =3;
    private MediaPlayer mPlayer = null;

        private Runnable mTickExecutor = new Runnable() {
            @Override
            public void run() {
                tick();
                mHandler.postDelayed(mTickExecutor,100);
            }
        };
        private File mOutputFile;
    SendAudioFile sendAudioFile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View  rootView = inflater.inflate(R.layout.fragment_audio_recording, container, false);
            sendAudioFile=(SendAudioFile) getActivity();
            this.mTimerTextView = (TextView) rootView.findViewById(R.id.timer);
            this.mediaImageView=(ImageView) rootView.findViewById(R.id.audi_recoarder_Imag);
            this.sendImage=(ImageView)rootView.findViewById(R.id.send_image);
            this.deleteImage=(ImageView)rootView.findViewById(R.id.deleteImage);
            this.mediaImageView.setOnClickListener(this);
            this.deleteImage.setOnClickListener(this);
            this.sendImage.setOnClickListener(this);
            mPlayer = new MediaPlayer();
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mRecorder != null) {
                stopRecording(false);
            }
        }

        private void startRecording() {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
                mRecorder.setAudioEncodingBitRate(48000);
            } else {
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mRecorder.setAudioEncodingBitRate(64000);
            }
            mRecorder.setAudioSamplingRate(16000);
            mOutputFile = getOutputFile();
            mOutputFile.getParentFile().mkdirs();
            mRecorder.setOutputFile(mOutputFile.getAbsolutePath());

            try {
                mRecorder.prepare();
                mRecorder.start();
                mStartTime = SystemClock.elapsedRealtime();
                mHandler.postDelayed(mTickExecutor, 100);
                Log.d("Voice Recorder","started recording to "+mOutputFile.getAbsolutePath());
            } catch (IOException e) {
                Log.e("Voice Recorder", "prepare() failed "+e.getMessage());
            }
        }

        protected  void stopRecording(boolean saveFile) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mStartTime = 0;
            mHandler.removeCallbacks(mTickExecutor);
            if (!saveFile && mOutputFile != null) {
                mOutputFile.delete();
            }

        }

        private File getOutputFile() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
            return new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                    + "/Voice Recorder/RECORDING_"
                    + dateFormat.format(new Date())
                    + ".mp3");

        }

        private void tick() {
            long time = (mStartTime < 0) ? 0 : (SystemClock.elapsedRealtime() - mStartTime);
            int minutes = (int) (time / 60000);
            int seconds = (int) (time / 1000) % 60;
            int milliseconds = (int) (time / 100) % 10;
            mTimerTextView.setText(minutes+":"+(seconds < 10 ? "0"+seconds : seconds)+"."+milliseconds);
            if (mRecorder != null) {
                amplitudes[i] = mRecorder.getMaxAmplitude();
                if (i >= amplitudes.length -1) {
                    i = 0;
                } else {
                    ++i;
                }
            }
        }

    private void startPlaying() {
       /* String path=mOutputFile.getPath().toString();*/
        try {
            mPlayer.reset();
            FileInputStream rawFile = new FileInputStream(mOutputFile);
            mPlayer.setDataSource(rawFile.getFD());
            mPlayer.prepare();
            mPlayer.start();

        } catch (IOException e) {
            Log.e("preparefailed", "prepare() failed");
        }
    }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.deleteImage:
                    stopRecording(false);
                    break;

                case R.id.send_image:
                    sendAudioFile.sendAudio(mOutputFile.getAbsolutePath());
                    break;

                case R.id.audi_recoarder_Imag:
                    playerState();
                    break;
            }
        }
        public void playerState(){
            if(StateOfPlayer==START_RECORDING){
                mediaImageView.setImageDrawable(getResources().getDrawable(R.drawable.stop_button_background));
                startRecording();
                StateOfPlayer=STOP_RECORDING;

            }else if(StateOfPlayer==STOP_RECORDING){
                StateOfPlayer=PLAY_RECORDING;
                mediaImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_play));
                stopRecording(true);

            }else if(StateOfPlayer==PLAY_RECORDING){
                startPlaying();
                StateOfPlayer=PAUSE_RECORDING;
                mediaImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_pause));

            }else if(StateOfPlayer==PAUSE_RECORDING){
                mPlayer.reset();
            }else {
                mediaImageView.setImageDrawable(getResources().getDrawable(R.drawable.microphone));

            }
        }
    }