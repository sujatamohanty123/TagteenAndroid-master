package in.tagteen.tagteen;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import in.tagteen.tagteen.Filter.GPUImageFilterTools;
import in.tagteen.tagteen.Filter.GPUImageFilterTools.FilterAdjuster;
import in.tagteen.tagteen.Filter.GPUImageFilterTools.OnGpuImageFilterChosenListener;
import in.tagteen.tagteen.Filter.activity.ExFilterAdapter;
import in.tagteen.tagteen.Filter.activity.FilterAdapter;
import in.tagteen.tagteen.Filter.activity.ModelClass;
import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.GPUImageView.OnPictureSavedListener;

import static android.graphics.BitmapFactory.decodeResource;
import static com.yalantis.ucrop.util.BitmapLoadUtils.calculateInSampleSize;

import in.tagteen.tagteen.emoji.EmojiconEditText;

public class SelfiCameraPreview extends Activity implements OnClickListener, OnPictureSavedListener, AsyncResponse {
    private static final int REQUEST_PICK_IMAGE = 1;
    private GPUImageFilter mFilter;
    private FilterAdjuster mFilterAdjuster;
    private GPUImageView mGPUImageView;
    LinearLayoutManager layoutManager;
    public List<ModelClass> mfilters = new ArrayList<>();
    RecyclerView mRecyclerView, recList;
    Bitmap mMainBitmap, mBitmap;

    String isfromBackCamera = DatabaseContracts.IS_FALSE;
    Uri imageUri;
    ExFilterAdapter exFilterAdapter;
    ImageView imageView;
    String path;
    private ProgressDialog progress;
    FilterAdapter mAdapter;
    Bitmap mThumbNailBitmap;
    private static final String TAG = "SampleActivity";
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "tagteen";
    private ImageView cropImage;
    boolean isfilterShowing;

    private GPUImageFilter selectedFilter;
    private int percent;
    ImageView SendImage,cross;
    ImageView filterButton;

    File mediaFile;
    String url = "", filepath;
    static String userid = "";
    EmojiconEditText msg_emojicon_edittext;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.selfi_camera_preview);
        filterButton = (ImageView) findViewById(R.id.filterButton);
        SendImage = (ImageView) findViewById(R.id.ImageOperationPostButton);
        cross = (ImageView) findViewById(R.id.cross);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage);
        msg_emojicon_edittext=(EmojiconEditText) findViewById(R.id.msg_emojicon_edittext);
        // cropImage     = (ImageView) findViewById(R.id.crop_image);
        SharedPreferenceSingleton.getInstance().init(this);
        path = SharedPreferenceSingleton.getInstance().getStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH);
        handleImage(path, 0);
        SendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isfilterShowing) {
                    mRecyclerView.setVisibility(View.GONE);
                    isfilterShowing = false;
                    filterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter));
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    filterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_selection));
                    isfilterShowing = true;
                }
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_show();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBitmap = decodeResource(this.getResources(), R.drawable.thumb_nail);

        mAdapter = new FilterAdapter(this, mfilters, new OnGpuImageFilterChosenListener() {

            @Override
            public void onGpuImageFilterChosenListener(GPUImageFilter filter, int per) {
                mFilter = null;
                switchFilterTo(filter, per);
                selectedFilter = filter;
                percent = per;
                mGPUImageView.requestRender();
                mGPUImageView.getGPUImage();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
        isfilterShowing = true;
        // filterButton.setBackground(getResources().getDrawable(R.drawable.di_grayround));
        filterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_selection));
        setmDataset();

        if (userid != null || userid.equalsIgnoreCase("")) {
            userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 69:
                if (resultCode == RESULT_OK) {
                    if (requestCode == UCrop.REQUEST_CROP) {
                        handleCropResult(data);
                    } else {
                        Toast.makeText(this, "Could not fetch selected image", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void switchFilterTo(final GPUImageFilter filter, int per) {

        if (mFilter == null || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImageView.setFilter(mFilter);
            mFilterAdjuster = new FilterAdjuster(mFilter);
            mFilterAdjuster.adjust(per);

        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.button_choose_filter:
                break;

            default:
                break;
        }

    }

    @Override
    public void onPictureSaved(final Uri uri) {
        progress.hide();
        //Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT).show();
        String filepath = getPath(uri);
      /*  Intent intent = new Intent();
        intent.putExtra("ImagePath", path);
        setResult(4, intent);
        finish();*/

        if (filepath != null) {
            File file = new File(filepath);
            uploadVideoThumb(file, SelfiCameraPreview.this);
        }

    }

    private void saveImage() {
        this.hideKeyBoard();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saveImageAfterDelay();
            }
        }, 1000);
    }

    private void saveImageAfterDelay() {
        String fileName = System.currentTimeMillis() + ".jpg";
        mGPUImageView.saveToPictures("tagTeen", fileName, this);
        progress = new ProgressDialog(this);
        progress.setMessage("saving...");
        progress.show();
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private void switchFilterTo(final GPUImageFilter filter, GPUImageView ImageView) {

        if (mFilter == null || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            ImageView.setFilter(mFilter);
            mFilterAdjuster = new FilterAdjuster(mFilter);
            findViewById(R.id.seekBar).setVisibility(mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        }
    }


    private void handleImage(String Url, int isCropImage) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mThumbNailBitmap = BitmapFactory.decodeFile(Url, options);
        mGPUImageView.setImage(mThumbNailBitmap);
    }

    public void setmDataset() {
        mfilters.add(new ModelClass("Normal", GPUImageFilterTools.FilterType.PIXELATION, updateImage(GPUImageFilterTools.FilterType.PIXELATION, 0, 0), 0, false, 0));
        mfilters.add(new ModelClass("Hulk", GPUImageFilterTools.FilterType.HUE, updateImage(GPUImageFilterTools.FilterType.HUE, 0, 23), 23, false, 0));
       // mfilters.add(new ModelClass("Gamma", GPUImageFilterTools.FilterType.GAMMA, updateImage(GPUImageFilterTools.FilterType.GAMMA, 0, 13), 13, false, 0));
        mfilters.add(new ModelClass("Old", GPUImageFilterTools.FilterType.SEPIA, updateImage(GPUImageFilterTools.FilterType.SEPIA, 0, 50), 50, false, 0));
        mfilters.add(new ModelClass("B&W", GPUImageFilterTools.FilterType.GRAYSCALE, updateImage(GPUImageFilterTools.FilterType.GRAYSCALE, 0, 0), 30, false, 0));
        mfilters.add(new ModelClass("Contrast", GPUImageFilterTools.FilterType.SHARPEN, updateImage(GPUImageFilterTools.FilterType.SHARPEN, 0, 62), 62, false, 0));
        buildFilterButton task = new buildFilterButton();
        task.execute(new String[]{"Pixelation"});

    }


    public void startColor(int percent) {
        GPUImageFilter filter = new GPUImagePosterizeFilter();
        switchFilterTo(filter, mGPUImageView);
        mGPUImageView.requestRender();
        GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(filter);
        mFilterAdjuster.adjust(percent);
        imageView.setVisibility(View.GONE);
    }

    public Bitmap updateImage(GPUImageFilterTools.FilterType type, int drawable, int percent) {
        Bitmap filterImageBitmap;
        filterImageBitmap = FilterImage(this, mThumbNailBitmap, GPUImageFilterTools.createFilterForType(this, type, drawable), percent);
        return filterImageBitmap;
    }

    public Bitmap FilterImage(Context context, Bitmap mBitmap, GPUImageFilter filter, int percent) {
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
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                Utils.showShortToast(SelfiCameraPreview.this, "Selfie has been added to moments");
                Intent intent = new Intent(SelfiCameraPreview.this, MainDashboardActivity.class);
                intent.putExtra("fragmentload", "social");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class buildFilterButton extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            mfilters.add(new ModelClass("Sketch", GPUImageFilterTools.FilterType.EMBOSS, updateImage(GPUImageFilterTools.FilterType.EMBOSS, 0, 20), 20, false, 0));
            mfilters.add(new ModelClass("Painting", GPUImageFilterTools.FilterType.POSTERIZE, updateImage(GPUImageFilterTools.FilterType.POSTERIZE, 0, 36), 36, false, 0));
            mfilters.add(new ModelClass("Neon", GPUImageFilterTools.FilterType.WHITE_BALANCE, updateImage(GPUImageFilterTools.FilterType.WHITE_BALANCE, 0, 28), 28, false, 0));
            mfilters.add(new ModelClass("Shine", GPUImageFilterTools.FilterType.TONE_CURVE, updateImage(GPUImageFilterTools.FilterType.TONE_CURVE, 0, 0), 30, false, 0));
            mfilters.add(new ModelClass("Dark", GPUImageFilterTools.FilterType.LOOKUP_AMATORKA, updateImage(GPUImageFilterTools.FilterType.LOOKUP_AMATORKA, 0, 0), 30, false, 0));
           // mfilters.add(new ModelClass("Pixelation", GPUImageFilterTools.FilterType.PIXELATION, updateImage(GPUImageFilterTools.FilterType.PIXELATION, 0, 8), 30, false, 0));
            mfilters.add(new ModelClass("Comic", GPUImageFilterTools.FilterType.TOON, updateImage(GPUImageFilterTools.FilterType.TOON, 0, 0), 30, false, 0));
            mfilters.add(new ModelClass("Funny", GPUImageFilterTools.FilterType.BULGE_DISTORTION, updateImage(GPUImageFilterTools.FilterType.BULGE_DISTORTION, 0, 0), 30, false, 0));
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    public static Bitmap rotate(String url, int degree) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(url, opts);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static Bitmap flip(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            File file = new File(resultUri.getPath());
            String st = file.getPath();
            handleImage(st, 1);

        } else {
            Toast.makeText(this, "Could not fetch selected image", Toast.LENGTH_SHORT).show();
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION},
                null, null, null);

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return -1;
            }
        } finally {
            cursor.close();
        }
    }

    public Bitmap setBitMap(Bitmap decodedBitmap, String previewFilePath) {
        Bitmap resultBitmap = rotateBitmap(decodedBitmap, getExifOrientation(previewFilePath));
        return resultBitmap;
    }

    private Bitmap decodeSampledBitmapFromResource(String url,
                                                   int requestedWidth, int requestedHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);

        options.inSampleSize = calculateInSampleSize(options, requestedWidth, requestedHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(url, options);
    }

    private int getExifOrientation(String previewFilePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(previewFilePath);
        } catch (IOException ignore) {
        }
        return exif == null ? ExifInterface.ORIENTATION_UNDEFINED :
                exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }

        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError ignore) {
            return null;
        }
    }

    public Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap = null;
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        filterButton.setVisibility(View.VISIBLE);
        SendImage.setVisibility(View.VISIBLE);
    }


    //=-------------
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
                /*int per = (int) ((bytesCurrent * 100) / bytesTotal);
                System.out.print("Process " + per + " %");
                progressBar.setProgress(per);
                textPer.setText(per + " %");*/
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
               /* layoutProgress.setVisibility(View.GONE);
                showToast("Uploading fail on AWS " + ex);
                dataSedataSender.setIsCall(false);*/
            }
        });
    }
    private void hideKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void uploadSelfy() {
        AsyncWorker mWorker = new AsyncWorker(this);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(new File(url).getAbsolutePath(), options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;

            JSONObject video = new JSONObject();
            JSONObject image = new JSONObject();
            image.put("url", url);
            image.put("height", imageHeight + "");
            image.put("width", imageWidth + "");

            BroadcastObject.put("postCreatorId", userid);
            BroadcastObject.put("content", "ANCD");
            String title = "-";
            if (this.msg_emojicon_edittext.getText().toString().length() > 0) {
                title = this.msg_emojicon_edittext.getText().toString();
            }
            BroadcastObject.put("title",  title);
            BroadcastObject.put("video", video);
            BroadcastObject.put("image", image);


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

    private void dialog_show() {
        final Dialog d = new Dialog(SelfiCameraPreview.this);
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
        buttonLayout.setVisibility(View.VISIBLE);
        continueorder.setVisibility(View.VISIBLE);
        dismiss.setVisibility(View.VISIBLE);
        name.setVisibility(View.GONE);
        ok.setVisibility(View.GONE);
        msg.setText("Are you sure you want to discard  this moment ?");
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
}
