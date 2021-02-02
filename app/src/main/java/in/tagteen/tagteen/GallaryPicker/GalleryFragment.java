package in.tagteen.tagteen.GallaryPicker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;


import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static ArrayList<Model_images> al_images = new ArrayList<>();
    private boolean boolean_folder;
    private Spinner gv_folder;
    private GridLayoutManager lLayout;
    private GridViewAdapter adapter;
    private RecyclerView image_List;
    private static final int REQUEST_PERMISSIONS = 100;
    private MyGallaryAdapter myGallaryAdapter;
    private List<String> selectedImages = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.gallary_main_activity, container, false);

        gv_folder = (Spinner) rootView.findViewById(R.id.spinner);
        gv_folder.setOnItemSelectedListener(this);

        image_List = (RecyclerView) rootView.findViewById(R.id.gv_folder);
        lLayout = new GridLayoutManager(getActivity(), 3);
        image_List.setHasFixedSize(true);
        image_List.setLayoutManager(lLayout);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.selectedImages = bundle.getStringArrayList(Constants.SELECTED_IMAGE_PATHS);
        }

        this.adapter = new GridViewAdapter(getActivity(), al_images, 0, this.selectedImages);
        image_List.setAdapter(this.adapter);

        if ((ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            fn_imagespath();
        }

        return rootView;
    }

    public ArrayList<Model_images> fn_imagespath() {
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getActivity().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);


        ArrayList<String> al_path1 = new ArrayList<>();
        Model_images obj_model1 = new Model_images();
        obj_model1.setStr_folder("All");

        al_path1.add("All");
        obj_model1.setAl_imagepath(al_path1);
        al_images.add(obj_model1);


        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            if (boolean_folder) {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);

                ArrayList<String> al_path2 = new ArrayList<>();
                al_path2.addAll(al_images.get(0).getAl_imagepath());
                al_path2.add(absolutePathOfImage);
                al_images.get(0).setAl_imagepath(al_path2);

            } else {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setAl_imagepath(al_path);
                al_images.add(obj_model);
            }
        }

        myGallaryAdapter = new MyGallaryAdapter(getActivity(), al_images);
        gv_folder.setAdapter(myGallaryAdapter);
        adapter = new GridViewAdapter(getActivity(), al_images, 0, this.selectedImages);
        image_List.setAdapter(adapter);
        return al_images;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        fn_imagespath();
                    } else {
                        String msg = "The app was not allowed to read or write to your storage. " +
                                "Hence, it cannot function properly. Please consider granting it this permission";
                        Utils.showShortToast(getActivity(), msg);
                    }
                }
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        adapter = new GridViewAdapter(getActivity(), al_images, position, this.selectedImages);
        image_List.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
