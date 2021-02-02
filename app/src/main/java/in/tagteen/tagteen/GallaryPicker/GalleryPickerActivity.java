package in.tagteen.tagteen.GallaryPicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import in.tagteen.tagteen.BrowsePicture2;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;
import in.tagteen.tagteen.util.Constants;

public class GalleryPickerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GalleryClickedPosition {
    private RelativeLayout layoutTop;
    private ImageView imgBack;
    private GridViewAdapter adapter;
    private ArrayList<Model_images> allImages = new ArrayList<>();
    private RecyclerView recyclerView;

    private boolean isFolder;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int IMAGE_PICK_CODE = 212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gallary_main_activity);

        this.initWidgets();
        this.bindEvents();
    }

    private void initWidgets() {
        this.layoutTop = (RelativeLayout) findViewById(R.id.layoutTop);
        this.layoutTop.setVisibility(View.VISIBLE);
        this.imgBack = (ImageView) findViewById(R.id.imgBack);

        this.recyclerView = (RecyclerView) findViewById(R.id.gv_folder);
        GridLayoutManager lLayout = new GridLayoutManager(this, 3);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(lLayout);

        this.adapter = new GridViewAdapter(this, allImages, 0, null);
        this.recyclerView.setAdapter(this.adapter);

        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            this.loadImages();
        }
    }

    private void bindEvents() {
        this.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void loadImages() {
        this.allImages.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        ArrayList<String> al_path1 = new ArrayList<>();
        Model_images obj_model1 = new Model_images();
        obj_model1.setStr_folder("All");

        al_path1.add("All");
        obj_model1.setAl_imagepath(al_path1);
        this.allImages.add(obj_model1);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            for (int i = 0; i < this.allImages.size(); i++) {
                if (this.allImages.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    isFolder = true;
                    int_position = i;
                    break;
                } else {
                    isFolder = false;
                }
            }

            if (isFolder) {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(this.allImages.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                this.allImages.get(int_position).setAl_imagepath(al_path);

                ArrayList<String> al_path2 = new ArrayList<>();
                al_path2.addAll(this.allImages.get(0).getAl_imagepath());
                al_path2.add(absolutePathOfImage);
                this.allImages.get(0).setAl_imagepath(al_path2);
            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setAl_imagepath(al_path);
                this.allImages.add(obj_model);
            }
        }

        this.adapter = new GridViewAdapter(this, this.allImages, 0, null);
        this.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Model_images selectedItem = (Model_images) adapterView.getItemAtPosition(position);
        if (selectedItem != null &&
                selectedItem.getAl_imagepath() != null &&
                selectedItem.getAl_imagepath().size() > 0) {
            this.sendResult(selectedItem.getAl_imagepath().get(0));
        }
    }

    private void sendResult(String imagePath) {
        Intent intent = new Intent();
        intent.putExtra(Constants.SELECTED_IMAGE_PATHS, imagePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // do nothing
    }

    @Override
    public void clicked(ArrayList<String> imagePath, int requestNumber) {
        if (requestNumber == 1) {
            Intent intent = new Intent(this, BrowsePicture2.class);
            intent.putExtra(Constants.IS_SINGLE_SELECTION, true);
            startActivityForResult(intent, IMAGE_PICK_CODE);
        } else if (imagePath != null && imagePath.size() > 0) {
            this.sendResult(imagePath.get(0));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE) {
            if (data != null && data.getStringExtra(Constants.SELECTED_IMAGE_PATHS) != null) {
                this.sendResult(data.getStringExtra(Constants.SELECTED_IMAGE_PATHS));
            }
        }
    }
}
