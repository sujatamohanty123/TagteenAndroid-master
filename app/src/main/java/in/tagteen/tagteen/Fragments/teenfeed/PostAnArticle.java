package in.tagteen.tagteen.Fragments.teenfeed;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AndroidPermissionMarshMallo;
import in.tagteen.tagteen.utils.ChooseImageDialog;
import in.tagteen.tagteen.utils.TeenFeedUpload;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import life.knowledge4.videotrimmer.utils.AbsoluteFilePath;

import static in.tagteen.tagteen.utils.AndroidPermissionMarshMallo.CAMERA_PERMISSION_REQUEST_CODE;
import static in.tagteen.tagteen.utils.AndroidPermissionMarshMallo.READ_EXTERNAL_STORAGE_CODE;

/**
 * Created by lovekushvishwakarma on 29/10/17.
 */

public class PostAnArticle extends AppCompatActivity implements View.OnClickListener {

    public final static String ARTICLE_TITLE = "ARTICLE_TITLE";
    public final static String ARTICLE_DESC = "ARTICLE_DESC";
    public final static String ARTICLE_CATEGORY = "ARTICLE_CATEGORY";
    public final static String ARTICLE_IMAGE = "ARTICLE_IMAGE";
    public final static String IS_ARTICLE_DRAFT = "IS_ARTICLE_DRAFT";
    public final static String ARTICLE_CATEGORY_ID = "ARTICLE_CATEGORY_ID";

    private static final int CAMERA_PIC_REQUEST = 1;
    private static final int GALLERY_PIC_REQUEST = 2;

    private AndroidPermissionMarshMallo permissionMarshMallo;

    private ImageView imageBack, imagePostAticle;
    private LinearLayout linCamera, linGallery, linLearALl, linSaveaDraft;
    private LinearLayout layoutImage;
    private EditText edtTitle, edtDesc;
    private TextView txtSelectCat;
    private Spinner spinShareTo;
    private Uri uri;
    private List<String> imagePaths;

    public static String selected_cat;
    private String option[]={"public","private","friends","BFF","Fans"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_an_article);
        imageBack = (ImageView) findViewById(R.id.imageback);
        imagePostAticle = (ImageView) findViewById(R.id.imagerUplaod);
        linCamera = (LinearLayout) findViewById(R.id.layoutCamera);
        linGallery = (LinearLayout) findViewById(R.id.layoutGallery);
        linLearALl = (LinearLayout) findViewById(R.id.layoutClearAll);
        linSaveaDraft = (LinearLayout) findViewById(R.id.layoutSaveDarft);
        layoutImage = (LinearLayout) findViewById(R.id.layoutImage);
        layoutImage.setVisibility(View.GONE);
        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        txtSelectCat = (TextView) findViewById(R.id.SelectCategory);
        spinShareTo = (Spinner) findViewById(R.id.share_to);
        permissionMarshMallo = new AndroidPermissionMarshMallo(this);

        imageBack.setOnClickListener(this);
        imagePostAticle.setOnClickListener(this);
        txtSelectCat.setOnClickListener(this);
        linCamera.setOnClickListener(this);
        linGallery.setOnClickListener(this);
        linLearALl.setOnClickListener(this);
        linSaveaDraft.setOnClickListener(this);

        ArrayAdapter<String> shareAdapter = new ArrayAdapter<String>(PostAnArticle.this,android.R.layout.simple_spinner_item, option);
        shareAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinShareTo.setAdapter(shareAdapter);

        boolean isAritleDraft = SharedPreferenceSingleton.getInstance().getBoolPreference(IS_ARTICLE_DRAFT);
        if (isAritleDraft) {
            String artTitle = SharedPreferenceSingleton.getInstance().getStringPreference(ARTICLE_TITLE);
            String artDesc = SharedPreferenceSingleton.getInstance().getStringPreference(ARTICLE_DESC);
            String artCat = SharedPreferenceSingleton.getInstance().getStringPreference(ARTICLE_CATEGORY);
            this.imagePaths = SharedPreferenceSingleton.getInstance().getStringListPreference(ARTICLE_IMAGE);

            if (artTitle == null) {
                artTitle = "";
            }
            edtTitle.setText(artTitle + "");
            edtTitle.setSelection(artTitle.length());

            if (artDesc == null) {
                artDesc = "";
            }
            edtDesc.setText(artDesc + "");
            edtDesc.setSelection(artDesc.length());

            txtSelectCat.setText(artCat);
            if (this.imagePaths == null || this.imagePaths.isEmpty()) {
                layoutImage.setVisibility(View.GONE);
            } else {
                layoutImage.setVisibility(View.VISIBLE);
                // TODO: loop the images and set
                //Glide.with(this).load(path).error(R.mipmap.logo_small).into(AricleImage);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageback:
                saveAsDrast();
                finish();
                break;
            case R.id.imagerUplaod:
                if (validation()) {
                    uploadArticle();
                }
                break;

            case R.id.SelectCategory:
                Fragment catFrag = new CategoryFragment(txtSelectCat);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.framCateg, catFrag);
                fragmentTransaction.addToBackStack("cat");
                fragmentTransaction.commit();
                break;

            case R.id.layoutCamera:
                if (permissionMarshMallo.checkPermissionForCamera()) {
                    gotoCamera();
                } else {
                    permissionMarshMallo.requestPermissionForCamera();
                }
                break;

            case R.id.layoutGallery:
                if (permissionMarshMallo.checkPermissionForReadExternal()) {
                    gotoGallery();
                } else {
                    permissionMarshMallo.requestPermissionForReadExternal();
                }
                break;

            case R.id.layoutClearAll:
                clearDraft();
                break;

            case R.id.layoutSaveDarft:
                saveAsDrast();
                break;
        }
    }

    private boolean validation() {
        //String feedTitle = edtTitle.getText().toString();
        String category = txtSelectCat.getText().toString();
        if (!category.equalsIgnoreCase("Select Category")) {
            return true;
        } else {
            Utils.showToast(PostAnArticle.this, "please select category");
        }
        return false;
    }

    private void uploadArticle() {
        TeenFeedUpload.feedCat = "0";
        TeenFeedUpload.feedDesc = edtTitle.getText().toString() + "|" + edtDesc.getText().toString();
        String title = edtTitle.getText().toString();
        if (title.length() == 0) {
            title = " ";
        }
        TeenFeedUpload.feedTitle = title;
        TeenFeedUpload.iscall = true;
        TeenFeedUpload.view_to = spinShareTo.getSelectedItem().toString();

        if (imagePaths == null || imagePaths.isEmpty()) {
            TeenFeedUpload.selectedPathList.clear();
        } else {
            TeenFeedUpload.selectedPathList = new ArrayList<>(this.imagePaths);
        }
        finish();
    }

    private void saveAsDrast() {
        String title = edtTitle.getText().toString();
        String desc = edtDesc.getText().toString();
        String cat = txtSelectCat.getText().toString();
        SharedPreferenceSingleton.getInstance().writeStringPreference(ARTICLE_TITLE, title);
        SharedPreferenceSingleton.getInstance().writeStringPreference(ARTICLE_DESC, desc);
        SharedPreferenceSingleton.getInstance().writeStringPreference(ARTICLE_CATEGORY, cat);
        SharedPreferenceSingleton.getInstance().writeStringListPreference(ARTICLE_IMAGE, this.imagePaths);
        SharedPreferenceSingleton.getInstance().writeBoolPreference(IS_ARTICLE_DRAFT, true);
        SharedPreferenceSingleton.getInstance().writeStringPreference(ARTICLE_CATEGORY_ID, selected_cat);
        if (!title.equalsIgnoreCase("") || !desc.equalsIgnoreCase("")) {
            Utils.showToast(this, "Draft saved...");
        }
    }

    public static void clearDraft() {
        SharedPreferenceSingleton.getInstance().writeStringPreference(ARTICLE_TITLE, "");
        SharedPreferenceSingleton.getInstance().writeStringPreference(ARTICLE_DESC, "");
        SharedPreferenceSingleton.getInstance().writeStringPreference(ARTICLE_CATEGORY, "");
        SharedPreferenceSingleton.getInstance().writeStringPreference(ARTICLE_IMAGE, "");
        SharedPreferenceSingleton.getInstance().writeBoolPreference(IS_ARTICLE_DRAFT, false);
        SharedPreferenceSingleton.getInstance().writeStringPreference(ARTICLE_CATEGORY_ID, "");
        selected_cat = "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveAsDrast();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
            case READ_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
    }

    public void gotoCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    public void gotoGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), GALLERY_PIC_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_PIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    layoutImage.setVisibility(View.VISIBLE);
                    Uri imageUri = data.getData();
                    String path = AbsoluteFilePath.getPath(PostAnArticle.this, imageUri);
                    this.addImagePath(path, imageUri);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        } else if (requestCode == ChooseImageDialog.CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri picUri = Utils.getImageUri(PostAnArticle.this, imageBitmap);
            String path = AbsoluteFilePath.getPath(PostAnArticle.this, picUri);
            this.addImagePath(path, picUri);
        } else if (requestCode == ChooseImageDialog.CROP_PIC_REQUEST && resultCode == RESULT_OK) {
            /*layoutImage.setVisibility(View.VISIBLE);
            final Uri picUri = data.getData();
            path = AbsoluteFilePath.getPath(PostAnArticle.this, picUri);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(PostAnArticle.this)
                            .load(picUri) // add your image url
                            .into(AricleImage);
                }
            }, 100);*/
        }
    }

    private void addImagePath(String path, final Uri imageUri) {
        if (this.imagePaths == null) {
            this.imagePaths = new ArrayList<>();
        }
        this.imagePaths.add(path);

        ImageView imageView = new ImageView(this);
        int imageSize = Utils.getPxFromDp(this, 100);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
        this.layoutImage.addView(imageView);
        Glide.with(PostAnArticle.this)
                .load(imageUri)
                .into(imageView);
    }

    /*private void gotoCrop(Uri picUri) {
        File file = new File(Environment.getExternalStorageDirectory(),
                "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setDataAndType(picUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        cropIntent.putExtra("output", uri);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, ChooseImageDialog.CROP_PIC_REQUEST);
    }*/
}
