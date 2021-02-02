package in.tagteen.tagteen.selfyManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.SelfiCameraPreview;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.util.CustomFrontBackCameraPreview;
import in.tagteen.tagteen.utils.AndroidPermissionMarshMallo;
import in.tagteen.tagteen.utils.CommanFun;
import in.tagteen.tagteen.utils.CommonConstants;
import in.tagteen.tagteen.utils.ScalingUtilities;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class CustomCamera extends Fragment implements SurfaceHolder.Callback {
    AndroidPermissionMarshMallo marshMallowPermission;
    private static String TAG = "";
    private static Camera camera;
    public static int f = 0;
    SurfaceView surfaceView;
    int cameraId;
    CustomFrontBackCameraPreview mPreview;
    PictureCallback mPicture;
    CameraInfo info;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    public static ImageView    video_rec_btn;
    ImageButton capture_old;
    ImageView back_btn;
    File mFileTemp;
    Bitmap bitPicFinal;
    String imagePath = "";
    private LinearLayout cameraPreview;
    ImageView switchCamera;
    private String selectedImagePath = "";
    Activity ll;
    //String write_something,category,location;
    PictureCallback pic_callback;
    public static boolean you_tube_selected;
    public static boolean video_selected, videoMint_selected, imageMint_selected;
    TextView gallery_text;
    private boolean cameraFront = false;
    public static boolean image_selected, image_from_gallery;
    private String imageName="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TAG = getClass().getName();
        Log.d(TAG, "on create view");
        ll = (Activity) getActivity();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final double density = getResources().getDisplayMetrics().density;
        marshMallowPermission = new AndroidPermissionMarshMallo(getActivity());
        View rootView = inflater.inflate(R.layout.custom_camera_temp, container, false);
        RelativeLayout rv = (RelativeLayout) rootView.findViewById(R.id.container);

        capture_old = (ImageButton) rootView.findViewById(R.id.capture_old);
        back_btn = (ImageView) rootView.findViewById(R.id.camera_cross_arrow);
        // video_rec_btn =
        // (ImageButton)rootView.findViewById(R.id.video_rec_btn);

        gallery_text = (TextView) rootView.findViewById(R.id.gallery_text);
        mPreview = new CustomFrontBackCameraPreview(getActivity(), camera,
                cameraId);
        cameraPreview = (LinearLayout) rootView.findViewById(R.id.imagePreview);
        cameraPreview.addView(mPreview);
        rootView.setOnClickListener(null);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("edit_type")) {
                if (bundle.getString("edit_type").equalsIgnoreCase("image")) {

                } else if (bundle.getString("edit_type").equalsIgnoreCase(
                        "video")) {

                }
            }

		/*	write_something = bundle.getString("write_something");
            category = bundle.getString("category");
			location = bundle.getString("location");*/
        }

        // create dynamic image name
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyyhmmss");
        imageName = sdf.format(date);
        imageName=imageName+".png";

        info = new CameraInfo();

        switchCamera = (ImageView) rootView.findViewById(R.id.btn_camera_reverse);
        switchCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa
                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Sorry, your phone has only one camera!",
                            Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });


        capture_old.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // TODO Auto-generated method stub

                image_selected = true;
                video_selected = false;
                you_tube_selected = false;
                image_from_gallery = false;
                try {
                    camera.takePicture(shutterCallback, null, pic_callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        back_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });




        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i(TAG, "keyCode: " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    Log.i("back key", "onKey Back listener is working!!!");
                    getActivity().finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
        return rootView;
    }



    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    private PictureCallback getPictureCallback(final boolean cameraFront2) {
        pic_callback = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // TODO Auto-generated method stub
                Log.d(TAG, "PIC CALLBACK>>");

                camera.stopPreview();
                try {
                    Log.d(TAG, "Data: " + "[" + data + "]");
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        Log.d(TAG, "MEdia mounted");
                        //File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "mintshow_images");
                        File dir =new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ File.separator +"Camera");
                        try {
                            /*if (dir.isDirectory()) {
                                dir.delete();
                            }
                            if (dir.mkdir()) {
                                System.out.println("Directory created");

                            } else {
                                System.out.println("Directory is not created");
                            }*/
                           // mFileTemp = new File(dir, "yaac_mint_img.png");
                            mFileTemp=new File(dir,imageName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "MEdia NOT mounted" + getActivity().getFilesDir().getAbsolutePath());
                        mFileTemp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ File.separator +"Camera", imageName);
                    }

                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;


                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // Notice that width and height are reversed
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        // Setting post rotate to 90
                        Matrix mtx = new Matrix();
                        mtx.postRotate(90);
                        // Rotating Bitmap
                        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, -1};
                            Matrix matrixMirrorY = new Matrix();
                            matrixMirrorY.setValues(mirrorY);
                            mtx.postConcat(matrixMirrorY);
                        }

                        bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                    } else {// LANDSCAPE MODE
                        //No need to reverse width and height
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenWidth, screenHeight, true);
                        bm = scaled;
                    }
                    FileOutputStream outStream = new FileOutputStream(mFileTemp);
                    bm.compress(CompressFormat.JPEG, 100, outStream);
                    outStream.write(data);
                    outStream.close();
                    //new FileOperation(data,mFileTemp).execute(bm);
                    imagePath = mFileTemp.getAbsolutePath();
                    Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
                    //camera.stopPreview();

                    startCropImage(cameraFront2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onPictureTaken - jpeg");
                //mPreview.refreshCamera(camera);
            }

        };
        return pic_callback;
    }


    private PictureCallback getPictureCallback2(final boolean cameraFront2) {
        pic_callback = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // TODO Auto-generated method stub

                Log.d(TAG, "PIC CALLBACK>>");
                long time = 0;
                camera.stopPreview();
                try {
                    Log.d(TAG, "Data: " + "[" + data + "]");
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        Log.d(TAG, "MEdia mounted");
                        //File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "mintshow_images");
                        File dir =new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ File.separator +"Camera");
                        try {
                            /*if (dir.mkdir()) {
                                System.out.println("Directory created");

                            } else {
                                System.out.println("Directory is not created");
                            }*/
                           // mFileTemp = new File(dir, "yaac_mint_img.png");
                            mFileTemp=new File(dir,imageName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "MEdia NOT mounted" + getActivity().getFilesDir().getAbsolutePath());
                        mFileTemp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ File.separator +"Camera", imageName);
                    }

                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;


                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // Notice that width and height are reversed
                       /* Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        // Setting post rotate to 90
                        Matrix rotateRight = new Matrix();
                        rotateRight.preRotate(90);

                        if (Build.VERSION.SDK_INT > 13 && cameraFront == true) {
                            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                            rotateRight = new Matrix();
                            Matrix matrixMirrorY = new Matrix();
                            matrixMirrorY.setValues(mirrorY);

                            rotateRight.postConcat(matrixMirrorY);

                            rotateRight.preRotate(270);

                        }                        // Rotating Bitmap
                        bm = Bitmap.createBitmap(scaled, 0, 0, w, h, rotateRight, true);*/



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
                        bm=scaled;
                    }

                    FileOutputStream outStream = new FileOutputStream(mFileTemp);
                    bm.compress(CompressFormat.JPEG, 100, outStream);
                    outStream.write(data);
                    outStream.close();

                    imagePath = mFileTemp.getAbsolutePath();
                    Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);

                    // camera.stopPreview();
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
                    // alert_dialog.show();

                    startCropImage(cameraFront2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }
                Log.d(TAG, "onPictureTaken - jpeg");
                //mPreview.refreshCamera(camera);
            }

        };
        return pic_callback;
    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
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

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                f = 1;
                camera = Camera.open(cameraId);
                mPicture = getPictureCallback(cameraFront);
                mPreview.refreshCamera(camera);
            }
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                f = 2;
                camera = Camera.open(cameraId);
                System.out.println("Front facing camera : " + cameraFront);
                mPicture = getPictureCallback2(cameraFront);
                mPreview.refreshCamera(camera);
            }
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
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
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        releaseCamera();
        if (camera == null)
            Log.d(TAG, "CustomCam - Resume - Camera is NULL");
        if (!hasCamera(getActivity())) {
            Toast toast = Toast.makeText(getActivity(),
                    "Sorry, your phone does not have a camera!",
                    Toast.LENGTH_LONG);
            toast.show();
            // finish();
        }
        if (camera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                // Toast.makeText(this, "No front facing camera found.",
                // Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }
            camera = Camera.open(findBackFacingCamera());
            mPicture = getPictureCallback(cameraFront);
            mPreview.refreshCamera(camera);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.d("TAG", "SurfaceCreated!!!");

        if (camera != null) {
            Log.e("Inside the Surfac",
                    "Inside the SurfaceCreated of CustomCameraFragment");
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            previewing = false;
        }

        try {
            camera = Camera.open();
            camera.startPreview();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Please try again after sometime.",
                    Toast.LENGTH_LONG).show();
        }

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // TODO Auto-generated method stub
        if(holder.getSurface() == null)//check if the surface is ready to receive camera data
            return;

        try{
            camera.stopPreview();
        } catch (Exception e){
            //this will happen when you are trying the camera if it's not running
        }

        //now, recreate the camera preview
        try{
            //set the focusable true
            mPreview.setFocusable(true);
            //set the touch able true
            mPreview.setFocusableInTouchMode(true);
            //set the camera display orientation lock
            camera.setDisplayOrientation(90);

            Camera.Parameters params = camera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            Camera.Size optimalSize = getOptimalPreviewSize(sizes,width,height);
            params.setPreviewSize(optimalSize.width,optimalSize.height);
            params.setPictureSize(width,height);
            camera.setParameters(params);

            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch(Exception exp){
            Log.i("","FROM surfaceChanged: "+exp.toString());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.d("SurfaceDestroyed called", "SurfaceDestroyed called");

        if (camera != null) {
            Log.d("SurfaceDestroyed called",
                    "camera not null in surface destroy");
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            previewing = false;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // surfaceView.setVisibility(8);
            // Data1.loading_box(getActivity(),"Loading..");

        } catch (Exception e) {
            Log.e("FB onactiv", "" + e.toString());
        }
        Log.e("22", "22");
        if (resultCode != getActivity().RESULT_OK) {

            return;
        }

        Bitmap bitmap;

        Log.d(TAG, "Data: " + data.getData());

        switch (requestCode) {
            case CommonConstants.REQUEST_CODE_GALLERY:

                Log.i("Inside the ",
                        "Inside the openGallery - REQUEST_CODE_GALLERY");

                if (data.getData().toString().contains("images")) {
                    try {
                        video_selected = false;
                        image_selected = true;
                        you_tube_selected = false;
                        image_from_gallery = true;
                        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ File.separator +"Camera");
                        try {
                            /*if (dir.mkdir()) {
                                System.out.println("Directory created");

                            } else {
                                System.out.println("Directory is not created");
                            }*/
                            mFileTemp = new File(dir, imageName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final Uri selectedImageUri = data.getData();
                        InputStream inputStream = getActivity()
                                .getContentResolver().openInputStream(
                                        data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                        CommanFun.copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        inputStream.close();
                        imagePath = mFileTemp.getAbsolutePath();

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
                                        dialog.dismiss();
                                        Log.d(TAG, "Call Crop");
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
                        // alert_dialog.show();

                        Bitmap thumbnail = (BitmapFactory.decodeFile(imagePath));

                        if (thumbnail.getHeight() >= 200
                                && thumbnail.getWidth() >= 200) {
                            startCropImage(false);
                        } else {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated
                                    CommanFun.getAlertDialogWithbothMessage(ll, "Upload Error!", "Try uploading a different photo. Photos must be at least 200 x 200 pixels.");
                                    Log.i("test", "afterbitpic");
                                }
                            });

                        }

                    } catch (Exception e) {

                        Log.e("Exception", "Error while creating temp file", e);
                    }
                } else if (data.getData().toString().contains("video")) {
                    try {
                        video_selected = true;
                        image_selected = false;
                        you_tube_selected = false;
                        System.out.println("SELECT_VIDEO");
                        Uri selectedImageUri = data.getData();
                        String selectedPath = getPath(selectedImageUri);
                    /* Restrict users to upload one minute videos only */
                        Cursor cursor = MediaStore.Video
                                .query(getActivity().getContentResolver

                                                (),
                                        data.getData(),
                                        new String[]{

                                                MediaStore.Video.VideoColumns.DURATION});
                        System.out.println("Video cursor count: "
                                + cursor.getCount());
                        cursor.moveToFirst();
                        long video_duration = TimeUnit.MILLISECONDS
                                .toSeconds(cursor.getInt(cursor


                                        .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));

                        Log.d(TAG, "Video duration: " + video_duration);
                        // new VideoUploader().execute();
                        if (video_duration > 60) {
                            CommanFun.getAlertDialogWithbothMessage(getActivity(),
                                    "Video Upload", "You can upload maximum of 60 sec videos only.");
                        } else {
                        }

                    } catch (Exception e) {
                        Log.e("Exception", "Error while creating temp file", e);
                    }

                }
                break;
            case CommonConstants.REQUEST_CODE_CROP_IMAGE:

                // String path = data.getStringExtra(CropImage.IMAGE_PATH);
                String path = mFileTemp.getAbsolutePath();
                if (path == null) {
                    return;
                } else {
                    bitmap = BitmapFactory.decodeFile(path);

                    if (bitmap.getHeight() >= 200 & bitmap.getWidth() >= 200) {
                        saveBitmap(bitmap);
                    } else {
                        Log.i("test2", "inside bitmap");
                        CommanFun.getAlertDialogWithbothMessage(ll, "Upload Error!",
                                "Try uploading a different photo. Photos must be at least 200 x 200 pixels.");
                    }

                }
                break;

        }
    }

    private String getPath(Uri uri) {

        String[] projection = {MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};

        Cursor cursor = getActivity().getContentResolver().query(uri,
                projection, null, null, null);
        cursor.moveToFirst();
        String filePath = cursor.getString(cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
        int fileSize = cursor.getInt(cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
        long duration = TimeUnit.MILLISECONDS.toSeconds(cursor.getInt(cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));

        System.out.println("size: " + fileSize);
        System.out.println("path: " + filePath);
        System.out.println("duration: " + duration);

        return filePath;
    }


    public void startCropImage(boolean camfront) {


       //Utils.myToast(getActivity(),"taken");
        /*Intent upload = new Intent(getActivity(), NewCropImage.class);
        upload.putExtra("pageflag", "Camera");
        upload.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        upload.putExtra("front", camfront);
        upload.putExtra(CropImage.SCALE, true);
        upload.putExtra(CropImage.ASPECT_X, 2);
        upload.putExtra(CropImage.ASPECT_Y, 2);
        upload.putExtra("cam_type", camfront);
        startActivity(upload);
        getActivity().finish();*/
       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mFileTemp.getPath())));

        Intent it = new Intent(getActivity(), SelfiCameraPreview.class);
        SharedPreferenceSingleton.getInstance().init(getActivity());
        SharedPreferenceSingleton.getInstance().writeStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH, imagePath);
        startActivity(it);



    }

    public void saveBitmap(Bitmap bm) {

        video_selected = false;
        image_selected = true;
        you_tube_selected = false;
        try {
            String mBaseFolderPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/";
            String name = "";
            for (int i = 0; i < 15; i++) {
                String albhabets = "abcdefghijklmnopqrstuvwxyz0123456789";
                Random random = new Random();

                name = name + albhabets.charAt(random.nextInt(35 - 1) + 1);
            }

            selectedImagePath = mBaseFolderPath + name + ".png";
            FileOutputStream stream = new FileOutputStream(selectedImagePath);

            bm.compress(CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

        } catch (Exception e) {
            Log.e("Could not save", e.toString());
        }
    }


}