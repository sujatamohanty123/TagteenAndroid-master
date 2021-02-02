package in.tagteen.tagteen.Filter.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import in.tagteen.tagteen.Filter.GPUImageFilterTools;
import in.tagteen.tagteen.Filter.GPUImageFilterTools.FilterAdjuster;
import in.tagteen.tagteen.Filter.utils.CameraHelper;
import in.tagteen.tagteen.Filter.utils.CameraHelper.CameraInfo2;
import in.tagteen.tagteen.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage.OnPictureSavedListener;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

import static android.graphics.BitmapFactory.decodeResource;

public class ActivityCamera extends Activity implements OnSeekBarChangeListener, OnClickListener {

    private GPUImage mGPUImage;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;
    private GPUImageFilter mFilter;
    private FilterAdjuster mFilterAdjuster;
    LinearLayoutManager layoutManager;
    private Bitmap mBitmap;
    GPUImageFilterTools gpu1;
    public List<ModelClass> mfilters = new ArrayList<>();
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);
        findViewById(R.id.button_choose_filter).setOnClickListener(this);
        findViewById(R.id.button_capture).setOnClickListener(this);

        setmDataset();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        mGPUImage = new GPUImage(this);
        mGPUImage.setGLSurfaceView((GLSurfaceView) findViewById(R.id.surfaceView));
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBitmap = decodeResource(this.getResources(), R.drawable.thumb_nail);

        FilterAdapter mAdapter = new FilterAdapter(this, mfilters, new GPUImageFilterTools.OnGpuImageFilterChosenListener() {
            @Override
            public void onGpuImageFilterChosenListener( GPUImageFilter filter,int per) {

                    switchFilterTo(filter);

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mCameraHelper = new CameraHelper(this);
        mCamera = new CameraLoader();
       /* View cameraSwitchView = findViewById(R.id.img_switch_camera);
        cameraSwitchView.setOnClickListener(this);
        if (!mCameraHelper.hasFrontCamera() || !mCameraHelper.hasBackCamera()) {
            cameraSwitchView.setVisibility(View.GONE);
        }*/
    }

    public void setmDataset() {

        mfilters.add(new ModelClass("Pixelation", GPUImageFilterTools.FilterType.PIXELATION, updateImage(GPUImageFilterTools.FilterType.PIXELATION,0), 30, false,0));
        mfilters.add(new ModelClass("Hue",GPUImageFilterTools.FilterType.HUE, updateImage(GPUImageFilterTools.FilterType.HUE,0), 30, false,0));
        mfilters.add(new ModelClass("Gamma",GPUImageFilterTools.FilterType.GAMMA, updateImage(GPUImageFilterTools.FilterType.GAMMA,0), 30, false,0));
        mfilters.add(new ModelClass("Sepia",GPUImageFilterTools.FilterType.SEPIA, updateImage(GPUImageFilterTools.FilterType.SEPIA,0), 30, false,0));
        mfilters.add(new ModelClass("Grayscale",GPUImageFilterTools.FilterType.GRAYSCALE, updateImage(GPUImageFilterTools.FilterType.GRAYSCALE,0), 30, false,0));
        mfilters.add(new ModelClass("Sharpness",GPUImageFilterTools.FilterType.SHARPEN, updateImage(GPUImageFilterTools.FilterType.SHARPEN,0), 30, false,0));
        mfilters.add(new ModelClass("Emboss",GPUImageFilterTools.FilterType.EMBOSS, updateImage(GPUImageFilterTools.FilterType.EMBOSS,0), 30, false,0));
        mfilters.add(new ModelClass("Posterize",GPUImageFilterTools.FilterType.POSTERIZE, updateImage(GPUImageFilterTools.FilterType.POSTERIZE,0), 30, false,0));
        mfilters.add(new ModelClass("White Balance",GPUImageFilterTools.FilterType.WHITE_BALANCE, updateImage(GPUImageFilterTools.FilterType.WHITE_BALANCE,0), 30, false,0));
        mfilters.add(new ModelClass("ToneCurve",GPUImageFilterTools.FilterType.TONE_CURVE, updateImage(GPUImageFilterTools.FilterType.TONE_CURVE,0), 30, false,0));
        mfilters.add(new ModelClass("Lookup (Amatorka)",GPUImageFilterTools.FilterType.LOOKUP_AMATORKA, updateImage(GPUImageFilterTools.FilterType.LOOKUP_AMATORKA,0), 30, false,0));
        mfilters.add(new ModelClass("Toon",GPUImageFilterTools.FilterType.TOON, updateImage(GPUImageFilterTools.FilterType.TOON,0), 30, false,0));
        mfilters.add(new ModelClass("Bulge Distortion",GPUImageFilterTools.FilterType.BULGE_DISTORTION, updateImage(GPUImageFilterTools.FilterType.BULGE_DISTORTION,0), 30, false,0));


/*FILTER1*/
        mfilters.add(new ModelClass("1aBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50amber), 30, false,0));
        mfilters.add(new ModelClass("3aBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50amber), 30, false,0));
        mfilters.add(new ModelClass("12aBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50amber), 30, false,0));
        mfilters.add(new ModelClass("20aBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50amber), 30, false,0));
/*FILTER1*/
        mfilters.add(new ModelClass("1bBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50blue), 30, false,0));
        mfilters.add(new ModelClass("3bBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50blue), 30, false,0));
        mfilters.add(new ModelClass("12bBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50blue), 30, false,0));
        mfilters.add(new ModelClass("20bBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50blue), 30, false,0));
/*FILTER1*/
        mfilters.add(new ModelClass("1cBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50cyan), 30, false,0));
        mfilters.add(new ModelClass("3cBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50cyan), 30, false,0));
        mfilters.add(new ModelClass("12cBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50cyan), 30, false,0));
        mfilters.add(new ModelClass("20cBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50cyan), 30, false,0));
/*FILTER1*/
        mfilters.add(new ModelClass("1dBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50deep_orange), 30, false,0));
        mfilters.add(new ModelClass("3dBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50deep_orange), 30, false,0));
        mfilters.add(new ModelClass("12dBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50deep_orange), 30, false,0));
        mfilters.add(new ModelClass("20dBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50deep_orange), 30, false,0));
/*FILTER1*/
        mfilters.add(new ModelClass("1eBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50deep_purple), 30, false,0));
        mfilters.add(new ModelClass("3eBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50deep_purple), 30, false,0));
        mfilters.add(new ModelClass("12eBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50deep_purple), 30, false,0));
        mfilters.add(new ModelClass("20eBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50deep_purple), 30, false,0));
        /*FILTER1*/
        mfilters.add(new ModelClass("1fBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50green), 30, false,0));
        mfilters.add(new ModelClass("3fBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50green), 30, false,0));
        mfilters.add(new ModelClass("12fBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50green), 30, false,0));
        mfilters.add(new ModelClass("20fBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50green), 30, false,0));
        /*FILTER1*/
        mfilters.add(new ModelClass("1gBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50indigo), 30, false,0));
        mfilters.add(new ModelClass("3gBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50indigo), 30, false,0));
        mfilters.add(new ModelClass("12gBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50indigo), 30, false,0));
        mfilters.add(new ModelClass("20gBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50indigo), 30, false,0));
        /*FILTER1*/
        mfilters.add(new ModelClass("1hBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50light_blue), 30, false,0));
        mfilters.add(new ModelClass("3hBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50light_blue), 30, false,0));
        mfilters.add(new ModelClass("12hBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50light_blue), 30, false,0));
        mfilters.add(new ModelClass("20hBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50light_blue), 30, false,0));
        /*FILTER1*/
        mfilters.add(new ModelClass("1iBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50light_green), 30, false,0));
        mfilters.add(new ModelClass("3iBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50light_green), 30, false,0));
        mfilters.add(new ModelClass("12iBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50light_green), 30, false,0));
        mfilters.add(new ModelClass("20iBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50light_green), 30, false,0));
        /*FILTER1*/
        mfilters.add(new ModelClass("1jBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50lime), 30, false,0));
        mfilters.add(new ModelClass("3jBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50lime), 30, false,0));
        mfilters.add(new ModelClass("12jBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50lime), 30, false,0));
        mfilters.add(new ModelClass("20jBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50lime), 30, false,0));

        /*FILTER1*/
        mfilters.add(new ModelClass("1kBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50orange), 30, false,0));
        mfilters.add(new ModelClass("3kBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50orange), 30, false,0));
        mfilters.add(new ModelClass("12kBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50orange), 30, false,0));
        mfilters.add(new ModelClass("20kBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50orange), 30, false,0));
                /*FILTER1*/
        mfilters.add(new ModelClass("1lBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50pink), 30, false,0));
        mfilters.add(new ModelClass("3lBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50pink), 30, false,0));
        mfilters.add(new ModelClass("12lBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50pink), 30, false,0));
        mfilters.add(new ModelClass("20lBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50pink), 30, false,0));
                /*FILTER1*/
        mfilters.add(new ModelClass("1mBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50purple), 30, false,0));
        mfilters.add(new ModelClass("3mBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50purple), 30, false,0));
        mfilters.add(new ModelClass("12mBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50purple), 30, false,0));
        mfilters.add(new ModelClass("20mBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50purple), 30, false,0));

                /*FILTER1*/
        mfilters.add(new ModelClass("1nBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50red), 30, false,0));
        mfilters.add(new ModelClass("3nBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50red), 30, false,0));
        mfilters.add(new ModelClass("12nBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50red), 30, false,0));
        mfilters.add(new ModelClass("20nBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50red), 30, false,0));

                /*FILTER1*/
        mfilters.add(new ModelClass("1oBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50teal), 30, false,0));
        mfilters.add(new ModelClass("3oBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50teal), 30, false,0));
        mfilters.add(new ModelClass("12oBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50teal), 30, false,0));
        mfilters.add(new ModelClass("20oBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50teal), 30, false,0));

                /*FILTER1*/
        mfilters.add(new ModelClass("1pBlend (Difference)",GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, updateImage(GPUImageFilterTools.FilterType.BLEND_DIFFERENCE,R.drawable.a50yellow), 30, false,0));
        mfilters.add(new ModelClass("3pBlend (Color Burn)",GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_COLOR_BURN,R.drawable.a50yellow), 30, false,0));
        mfilters.add(new ModelClass("12pBlend (Multiply)",GPUImageFilterTools.FilterType.BLEND_MULTIPLY, updateImage(GPUImageFilterTools.FilterType.BLEND_MULTIPLY,R.drawable.a50yellow), 30, false,0));
        mfilters.add(new ModelClass("20pBlend (Linear Burn)",GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, updateImage(GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN,R.drawable.a50yellow), 30, false,0));

    }

    public Bitmap updateImage(GPUImageFilterTools.FilterType type,int drawable) {
        Bitmap mBitmap, filterImageBitmap;
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_nail);
        gpu1=new GPUImageFilterTools();
        filterImageBitmap = FilterImage(this, mBitmap,gpu1.createFilterForType(this, type,drawable), 20);
        return filterImageBitmap;
    }
    
    public  Bitmap FilterImage(Context context, Bitmap mBitmap, GPUImageFilter filter, int percent) {
        GPUImage gpuImage = new GPUImage(context);
        GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(filter);
        mFilterAdjuster.adjust(percent);
        gpuImage.setImage(mBitmap);
        gpuImage.setFilter(filter);
        gpuImage.requestRender();
        Bitmap bitmapAfterFilter = gpuImage.getBitmapWithFilterApplied();
        gpuImage.deleteImage();
        return bitmapAfterFilter;
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        mCamera.onResume();
    }

    @Override
    protected void onPause() {
        mCamera.onPause();
        super.onPause();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.button_choose_filter:

                break;

            case R.id.button_capture:
                if (mCamera.mCameraInstance.getParameters().getFocusMode().equals(
                        Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    takePicture();
                } else {
                    mCamera.mCameraInstance.autoFocus(new Camera.AutoFocusCallback() {

                        @Override
                        public void onAutoFocus(final boolean success, final Camera camera) {
                            takePicture();
                        }
                    });
                }
                break;

          /*  case R.id.img_switch_camera:
                mCamera.switchCamera();
                break;*/
        }
    }

    private void takePicture() {
        // TODO get a size that is about the size of the screen
        Parameters params = mCamera.mCameraInstance.getParameters();
        params.setRotation(90);
        mCamera.mCameraInstance.setParameters(params);
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            Log.i("ASDF", "Supported: " + size.width + "x" + size.height);
        }
        mCamera.mCameraInstance.takePicture(null, null,
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {

                        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d("ASDF",
                                    "Error creating media file, check storage permissions");
                            return;
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("ASDF", "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d("ASDF", "Error accessing file: " + e.getMessage());
                        }

                        data = null;
                        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                        // mGPUImage.setImage(bitmap);
                        final GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
                        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                        mGPUImage.saveToPictures(bitmap, "GPUImage",
                                System.currentTimeMillis() + ".jpg",
                                new OnPictureSavedListener() {

                                    @Override
                                    public void onPictureSaved(final Uri
                                                                       uri) {
                                        pictureFile.delete();
                                        camera.startPreview();
                                        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                                    }
                                });
                    }
                });
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImage.setFilter(mFilter);
            mFilterAdjuster = new FilterAdjuster(mFilter);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress,
                                  final boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    private class CameraLoader {

        private int mCurrentCameraId = 0;
        private Camera mCameraInstance;

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            Parameters parameters = mCameraInstance.getParameters();
            // TODO adjust by getting supportedPreviewSizes and then choosing
            // the best one for screen size (best fill screen)
            if (parameters.getSupportedFocusModes().contains(
                    Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCameraInstance.setParameters(parameters);

            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    ActivityCamera.this, mCurrentCameraId);
            CameraInfo2 cameraInfo = new CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
        }

        /** A safe way to get an instance of the Camera object. */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        private void releaseCamera() {
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.release();
            mCameraInstance = null;
        }
    }
}
