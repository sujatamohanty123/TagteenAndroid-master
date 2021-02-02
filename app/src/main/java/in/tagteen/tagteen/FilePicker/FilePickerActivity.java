package in.tagteen.tagteen.FilePicker;

import android.Manifest;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.tagteen.tagteen.FilePicker.adapter.FilePickerAdapter;
import in.tagteen.tagteen.FilePicker.cursors.loadercallback.FileResultCallback;
import in.tagteen.tagteen.FilePicker.item.Document;
import in.tagteen.tagteen.FilePicker.utils.MediaStoreHelper;
import in.tagteen.tagteen.FilePicker.utils.PermissionUtils;
import in.tagteen.tagteen.FilePicker.view.FilterFragment;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.FilePickerInterFace;
import in.tagteen.tagteen.TagteenInterface.SendDocument;

import java.util.ArrayList;
import java.util.List;



public class FilePickerActivity extends Fragment {
    private RecyclerView recyclerView;
    private FilePickerAdapter adapter;
    private Toolbar toolbar;
    private TextView txtNull;
    private FloatingActionButton btnFilter;
    private List<String> itemfilter;
    View rootView;
   SendDocument sendDocument;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
       rootView = inflater.inflate(R.layout.filepicker_activitymain, container, false);
        sendDocument = (SendDocument) getActivity();
        toolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
       /* txtNull = (TextView)rootView.findViewById(R.id.txtNull);*/
        btnFilter = (FloatingActionButton)rootView.findViewById(R.id.btnFilter);
        itemfilter = new ArrayList<>();
        itemfilter.add(".pdf");
        itemfilter.add(".txt");
        itemfilter.add(".docx");
        itemfilter.add(".doc");

        if (PermissionUtils.requestPermission(getActivity(), 1, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            getDocument();
        }

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = FilterFragment.newInstance((ArrayList<String>) itemfilter);
                dialogFragment.show(getActivity().getFragmentManager(), "filter");
            }
        });
        return rootView;
    }

/*    public void finishData(String path) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "ok");
        returnIntent.putExtra("path", path);
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }*/

    public void getDocument() {
        MediaStoreHelper mediaStoreHelper = new MediaStoreHelper();
        mediaStoreHelper.getDocs(getActivity(),itemfilter, new FileResultCallback<Document>() {
            @Override
            public void onResultCallback(List<Document> files) {
                recyclerView = (RecyclerView)rootView.findViewById(R.id.recycleMain);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                adapter = new FilePickerAdapter(getActivity().getApplicationContext(), FilePickerActivity.this, files, new FilePickerInterFace() {
                    @Override
                    public void clickedDoc(String path, String name, String size) {
                        sendDocument.sendDoc(path,name,size);
                    }
                });
                recyclerView.setAdapter(adapter);
                if (files.isEmpty()) {
                    txtNull.setVisibility(View.VISIBLE);
                }
                Log.e("data","loadagain");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(requestCode, 1, grantResults)) {
            getDocument();
        }
    }


}
