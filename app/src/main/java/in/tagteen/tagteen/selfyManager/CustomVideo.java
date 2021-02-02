package in.tagteen.tagteen.selfyManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import in.tagteen.tagteen.Filter.activity.ActivityGallery;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.SelfiCameraPreview;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.utils.AndroidPermissionMarshMallo;
import in.tagteen.tagteen.utils.ScalingUtilities;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/*import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;*/

/**
 * Created by BSETEC on 19-04-2016.
 */
public class CustomVideo extends Fragment implements View.OnClickListener {

    final String FileLocation = (Environment.getExternalStorageDirectory() + "/MintShowVideos/");
    int TIME_MAX = 60 * 1000;
    private ImageView capture_video, btn_camera_reverse, closeAcitvity;
    private TextView timer_text;
    private ProgressBar progressBars;
    private RelativeLayout relVideoRec;
    private static Camera mCamera;
    private CameraPreview mPreview;
    private CountDownTimer myCountDown;
    private static long countDown;
    private boolean cameraFront = false, longpress, reversed, videoInPart = false;
    private MediaRecorder mediaRecorder;
    private ArrayList<VideoBean> videoBean;
    String videoPath;
    private boolean isRecording = false;
    String galleryText = "";
    AndroidPermissionMarshMallo marshMallowPermission;
    boolean flag = false;
    Camera.PictureCallback pic_callback;
    private String imageName = "";
    String imagePath = "";
    File mFileTemp;
    Camera.PictureCallback mPicture;
    private boolean isCamera = true;

    private Drawable takePhotoDrawable;
    private Drawable startRecordDrawable;
    private Drawable stopRecordDrawable;
    private int iconPadding = 0;
    private int iconPaddingStop = 18;
    String cameraposition1="";
    Bitmap bm;

    public CustomVideo(){}

    public CustomVideo(String camerapostion) {
        this.cameraposition1=camerapostion;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.videopreview_sec, null);

        takePhotoDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.camera_button);
        /*startRecordDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.start_video_record_button);
        stopRecordDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.stop_button_background);*/
        startRecordDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_start_vid);
        stopRecordDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_stop_vid);

        capture_video = (ImageView) root.findViewById(R.id.capture_video);
        btn_camera_reverse = (ImageView) root.findViewById(R.id.btn_camera_reverse);
        closeAcitvity = (ImageView) root.findViewById(R.id.camera_cross_arrow);
        progressBars = (ProgressBar) root.findViewById(R.id.progressBars);
        relVideoRec = (RelativeLayout) root.findViewById(R.id.imagePreview);

        timer_text = (TextView) root.findViewById(R.id.timer_text);
        timer_text.setVisibility(View.GONE);

        SharedPreferenceSingleton.getInstance().init(getActivity());
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.cameraReotation,RegistrationConstants.ChatBack);


        File dir1 = new File(Environment.getExternalStorageDirectory() + "/MintShowVideos/");
        try {
            if (dir1.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoBean = new ArrayList<VideoBean>();
        //intialization
        mPreview = new CameraPreview(getActivity(), mCamera);
        relVideoRec.addView(mPreview);

        //onclick
        closeAcitvity.setOnClickListener(this);
        btn_camera_reverse.setOnClickListener(this);
        marshMallowPermission = new AndroidPermissionMarshMallo(getActivity());


        capture_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCamera) {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyyhmmss");
                    imageName = sdf.format(date);
                    imageName = imageName + ".png";
                    mCamera.takePicture(shutterCallback, null, pic_callback);
                } else {
                    if (flag == false) {
                        flag = true;
                        videoBean.clear();
                        capture();
                        displayVideoRecordStateInProgress();
                    } else {
                        flag = false;
                        myCountDown.cancel();
                        stopRecording();
                    }
                }


            }
        });

        //new Counter that counts 3000 ms with a tick each 1000 ms
        myCountDown = new CountDownTimer(TIME_MAX, 1000) {
            public void onTick(long millisUntilFinished) {
                //update the UI with the new count
                long progressTime = progressBars.getProgress();
                int timeExit = (int) TimeUnit.MILLISECONDS.toSeconds(progressTime);
                if (cameraposition1.equalsIgnoreCase("selfy")){
                if (timeExit == 11) {
                    stopRecording();
                }
                }
                if (cameraposition1.equalsIgnoreCase("addpost")){
                    if (timeExit >= 60) {
                        stopRecording();
                    }
                }

                if (videoInPart) {
                    progressBars.setProgress(progressBars.getProgress() + 950 - 1000);
                    videoInPart = false;
                }
                if ((progressBars.getProgress() <= TIME_MAX) && (timeExit < 11)) {
                    progressBars.setProgress(progressBars.getProgress() + 950);
                    String aa = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(progressTime), TimeUnit.MILLISECONDS.toSeconds(progressTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progressTime)));
                    timer_text.setText(aa);


                }

            }

            public void onFinish() {
                //start the activity
                countDown = 0;
                stopRecording();
            }
        };


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                changeCamera();
            }
        }, 600);
        return root;
    }

    public void changeAction(String flag) {
        if (flag.equalsIgnoreCase("camera1")) {
            isCamera = true;
            timer_text.setVisibility(View.GONE);
            displayPhotoState();
        } else {
            timer_text.setVisibility(View.VISIBLE);
            isCamera = false;
            displayVideoRecordStateReady();
        }
    }

    private void stopRecording() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                myCountDown.cancel();
                videoBean.add(new VideoBean(progressBars.getProgress(), videoPath));
            }
            displayVideoRecordStateReady();
            releaseMediaRecorder();

            //new MergeVideo().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void onResume() {
        super.onResume();
        if(cameraposition1.equalsIgnoreCase("selfy")) {
            releaseCamera();
        }else{

        }
        if (!hasCamera(getActivity())) {
            Toast toast = Toast.makeText(getActivity(), "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            getActivity().finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(getActivity(), "No front facing camera found.", Toast.LENGTH_LONG).show();
                if (btn_camera_reverse != null) {
                    btn_camera_reverse.setVisibility(View.GONE);
                }

            }
            try {
                mCamera = Camera.open(findBackFacingCamera());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mPreview.refreshCamera(mCamera);
        }
        if(!cameraposition1.equalsIgnoreCase("selfy")){
           btn_camera_reverse.setVisibility(View.VISIBLE);
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_cross_arrow:
                getActivity().finish();
                break;
            case R.id.btn_camera_reverse:
                // chnage camera
                if (isRecording == false) {
                    changeCamera();
                }
                break;

        }
    }


    private void initialStatus() {
        int updatedTime = 0;
        isRecording = false;
        progressBars.setProgress(updatedTime);
        long progressTime = progressBars.getProgress();
        String aa = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(progressTime), TimeUnit.MILLISECONDS.toSeconds(progressTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progressTime)));
        timer_text.setText(aa);
    }

    private void changeCamera() {

        if (reversed) {
            System.out.println("false");
            reversed = false;
        } else {
            System.out.println("true");
            reversed = true;
        }
        // get the number of cameras
        int camerasNumber = Camera.getNumberOfCameras();
        if (camerasNumber > 1) {
            releaseCamera();
            chooseCamera();
        } else {
            Toast toast = Toast.makeText(getActivity(), "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            reversed = false;
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                SharedPreferenceSingleton.getInstance().init(getActivity());
                SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.cameraReotation,RegistrationConstants.ChatFront);

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback2(true);
                mPreview.refreshCamera(mCamera);
            }
        } else {
            reversed = true;
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                SharedPreferenceSingleton.getInstance().init(getActivity());
                SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.cameraReotation,RegistrationConstants.ChatBack);


                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback2(true);
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    void capture() {
        if (!prepareMediaRecorder()) {
            Toast.makeText(getActivity(), "Failed to create directory MyCameraVideo.", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        try {
            isRecording = true;
            mediaRecorder.start();
            myCountDown.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean prepareMediaRecorder() {
        System.out.println("inside prepare media recorder ");
        mediaRecorder = new MediaRecorder();
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(date.getTime());
        try {
            mCamera.unlock();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        mediaRecorder.setCamera(mCamera);
        videoPath = Environment.getExternalStorageDirectory() + "/MintShowVideos/" + timeStamp + ".mp4";
        if (!reversed) {
            mediaRecorder.setOrientationHint(90);
        } else {
            mediaRecorder.setOrientationHint(270);
        }
        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/MintShowVideos/" + timeStamp + ".mp4");
        mediaRecorder.setMaxDuration(10000); // Set max duration 60 sec.
        /////////////////////////////////////////////////////////////////////////////
        Method[] methods = mediaRecorder.getClass().getMethods();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setVideoFrameRate(18);
        mediaRecorder.setVideoSize(320, 240);
        for (Method method : methods) {
            try {
                if (method.getName().equals("setAudioChannels")) {
                    method.invoke(mediaRecorder, String.format("audio-param-number-of-channels=%d", 1));
                } else if (method.getName().equals("setAudioEncodingBitRate")) {
                    method.invoke(mediaRecorder, 12200);
                } else if (method.getName().equals("setVideoEncodingBitRate")) {
                    method.invoke(mediaRecorder, 3000000);
                } else if (method.getName().equals("setAudioSamplingRate")) {
                    method.invoke(mediaRecorder, 8000);
                } else if (method.getName().equals("setVideoFrameRate")) {
                    method.invoke(mediaRecorder, 24);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        try {
            mediaRecorder.setVideoEncodingBitRate(1600000);
            mediaRecorder.prepare();

        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
            // new MergeVide(videos_to_merge).execute();
        }
    }


    // merge class

    /*class MergeVideo extends AsyncTask<String, Integer, String> {
        ProgressDialog dialog;
        String finalVideoPath;
        boolean single_video = false;

        @Override
        protected String doInBackground(String... params) {
            int count = videoBean.size();
            if (count > 1) {

                try {
                    Movie[] inMovies = new Movie[count];
                    for (int i = 0; i < videoBean.size(); i++) {
                        File file = new File(videoBean.get(i).getVideopath());
                        System.out.println("file path " + i + file);
                        inMovies[i] = MovieCreator.build(file.getAbsolutePath());
                    }

                    List<Track> videoTracks = new LinkedList<Track>();
                    List<Track> audioTracks = new LinkedList<Track>();
                    Log.d("Movies length", "isss  " + inMovies.length);
                    if (inMovies.length != 0) {

                        for (Movie m : inMovies) {

                            for (Track t : m.getTracks()) {
                                if (t.getHandler().equals("soun")) {
                                    audioTracks.add(t);
                                }
                                if (t.getHandler().equals("vide")) {
                                    videoTracks.add(t);
                                }
                                if (t.getHandler().equals("")) {

                                }
                            }

                        }
                    }
                    Movie result = new Movie();

                    System.out.println("audio and videoo tracks : " + audioTracks.size() + " , " + videoTracks.size());
                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks
                                .toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks
                                .toArray(new Track[videoTracks.size()])));
                    }
                    final String finlaVideoFile = "/result23.mp4";
                    finalVideoPath = finlaVideoFile;
                    BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);
                    WritableByteChannel fc = new RandomAccessFile(String.format(FileLocation + finlaVideoFile), "rw").getChannel();
                    out.writeContainer(fc);
                    fc.close();
                    single_video = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (count == 1) {
                single_video = true;
                finalVideoPath = videoBean.get(0).getVideopath();
            }

            return finalVideoPath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Processing...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            dialog.dismiss();
            initialStatus();
            releaseCamera();

           *//* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            intent.setDataAndType(Uri.parse(path), "video/mp4");
            startActivity(intent);*//*

            Intent intent = new Intent(getActivity(), AddSelfy.class);
            intent.putExtra("path", path);
            startActivity(intent);
            getActivity().finish();


        }
    }*/


    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    private Camera.PictureCallback getPictureCallback2(final boolean cameraFront2) {

        pic_callback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // TODO Auto-generated method stub
                if(cameraposition1.equalsIgnoreCase("selfy")) {
                    long time = 0;
                    camera.stopPreview();
                    try {
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            //File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "mintshow_images");
                            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Camera");
                            try {
                            if (dir.mkdir()) {
                                System.out.println("Directory created");

                            } else {
                                System.out.println("Directory is not created");
                            }
                                // mFileTemp = new File(dir, "yaac_mint_img.png");
                                mFileTemp = new File(dir, imageName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            mFileTemp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Camera", imageName);
                        }
                        setupImageDisplay(data);
/*
                        int screenWidth = getResources().getDisplayMetrics().widthPixels;
                        int screenHeight = getResources().getDisplayMetrics().heightPixels;


                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);

                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            Bitmap scaled = ScalingUtilities.createScaledBitmap(bm, screenHeight, screenWidth, ScalingUtilities.ScalingLogic.FIT);
                            int w = scaled.getWidth();
                            int h = scaled.getHeight();
                            // Setting post rotate to 90
                            Matrix mtx = new Matrix();
                            mtx.postRotate(90);
                            // Rotating Bitmap

                            if (Build.VERSION.SDK_INT > 13 && cameraFront == true) {
                                float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                                mtx = new Matrix();
                                Matrix matrixMirrorY = new Matrix();
                                matrixMirrorY.setValues(mirrorY);
                                mtx.postConcat(matrixMirrorY);
                                mtx.preRotate(270);

                            }
                            bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                        } else {// LANDSCAPE MODE
                            //No need to reverse width and height
                            Bitmap scaled = ScalingUtilities.createScaledBitmap(bm, screenHeight, screenWidth, ScalingUtilities.ScalingLogic.FIT);
                            bm = scaled;
                        }*/

                        FileOutputStream outStream = new FileOutputStream(mFileTemp);
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.write(data);
                        outStream.close();

                       imagePath = mFileTemp.getAbsolutePath();

                         /*// camera.stopPreview();
                        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(
                                getActivity());
                        alert_dialog.setTitle("Edit");
                        alert_dialog
                                .setMessage("Do you want to crop or rotate the image ?");
                        alert_dialog.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        startCropImage(false);

                                    }
                                });
                        alert_dialog.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub


                                    }
                                });
                        // alert_dialog.show();*/
                        Intent it = new Intent(getActivity(), SelfiCameraPreview.class);
                        SharedPreferenceSingleton.getInstance().init(getActivity());
                        SharedPreferenceSingleton.getInstance().writeStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH, imagePath);
                        startActivity(it);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                    }
                    //mPreview.refreshCamera(camera);
                }else{
                    camera.stopPreview();
                    try {
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            //File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "mintshow_images");
                            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Camera");
                            try {
                            /*if (dir.mkdir()) {
                                System.out.println("Directory created");

                            } else {
                                System.out.println("Directory is not created");
                            }*/
                                // mFileTemp = new File(dir, "yaac_mint_img.png");
                                mFileTemp = new File(dir, imageName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            mFileTemp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Camera", imageName);
                        }

                        int screenWidth = getResources().getDisplayMetrics().widthPixels;
                        int screenHeight = getResources().getDisplayMetrics().heightPixels;

                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);

                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            // Notice that width and height are reversed

                            // Notice that width and height are reversed
                            Bitmap scaled = ScalingUtilities.createScaledBitmap(bm, screenHeight, screenWidth, ScalingUtilities.ScalingLogic.FIT);
                            int w = scaled.getWidth();
                            int h = scaled.getHeight();
                            // Setting post rotate to 90
                            Matrix mtx = new Matrix();
                            mtx.postRotate(90);
                            // Rotating Bitmap

                            if (Build.VERSION.SDK_INT > 13 && cameraFront == true) {
                                float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                                mtx = new Matrix();
                                Matrix matrixMirrorY = new Matrix();
                                matrixMirrorY.setValues(mirrorY);

                                mtx.postConcat(matrixMirrorY);

                                mtx.preRotate(270);

                            }
                            bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                        } else {// LANDSCAPE MODE
                            //No need to reverse width and height
                            Bitmap scaled = ScalingUtilities.createScaledBitmap(bm, screenHeight, screenWidth, ScalingUtilities.ScalingLogic.FIT);
                            bm = scaled;
                        }

                        FileOutputStream outStream = new FileOutputStream(mFileTemp);
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.write(data);
                        outStream.close();

                        imagePath = mFileTemp.getAbsolutePath();

                    Intent it =new Intent(getActivity(),ActivityGallery.class );
                    SharedPreferenceSingleton.getInstance().init(getActivity());
                        it.putExtra("camera","camera");
                    SharedPreferenceSingleton.getInstance().writeStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH,imagePath);
                    startActivityForResult(it, 4);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        return pic_callback;
    }

    private void setupImageDisplay(byte[] data) {
        bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        bm = scaleDown(bm, true);//scaling down bitmap
       // imageview_photo.setImageBitmap(photo);
    }

    public Bitmap scaleDown(Bitmap realImage, boolean filter) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;;

        Bitmap scaled;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Notice that width and height are reversed
            scaled = Bitmap.createScaledBitmap(realImage, screenHeight, screenWidth, filter);
            int w = scaled.getWidth();
            int h = scaled.getHeight();
            // Setting post rotate to 90

            Matrix mtx = new Matrix();

            if (cameraFront == true) {
                float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                Matrix matrixMirrorY = new Matrix();
                matrixMirrorY.setValues(mirrorY);

                mtx.postConcat(matrixMirrorY);
            }
            mtx.postRotate(90);
            // Rotating Bitmap
            realImage = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, filter);
        } else {// LANDSCAPE MODE
            //No need to reverse width and height
            scaled = Bitmap.createScaledBitmap(realImage, screenHeight, screenWidth, filter);
            int w = scaled.getWidth();
            int h = scaled.getHeight();
            // Setting post rotate to 90

            Matrix mtx = new Matrix();

            if (cameraFront==true) {
                float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                Matrix matrixMirrorY = new Matrix();
                matrixMirrorY.setValues(mirrorY);

                mtx.postConcat(matrixMirrorY);
            }
            mtx.postRotate(180);
            // Rotating Bitmap
            realImage = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, filter);
        }
        return realImage;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 4) {
            ArrayList<String> imagePath = new ArrayList<>();
            imagePath = data.getStringArrayListExtra("ImagePath");
            Intent intent = new Intent();
            intent.putStringArrayListExtra("ImagePath", imagePath);
            getActivity().setResult(4, intent);
            getActivity().finish();
        }
    }
    public void displayVideoRecordStateReady() {
        capture_video.setImageDrawable(startRecordDrawable);
        // capture_video.setPadding(iconPadding,iconPadding,iconPadding,iconPadding);
    }

    public void displayVideoRecordStateInProgress() {
        capture_video.setImageDrawable(stopRecordDrawable);
        //capture_video.setPadding(iconPaddingStop,iconPaddingStop,iconPaddingStop,iconPaddingStop);
    }

    public void displayPhotoState() {
        capture_video.setImageDrawable(takePhotoDrawable);
        //capture_video.setPadding(iconPadding,iconPaddingStop,iconPaddingStop,iconPaddingStop);
    }
}

