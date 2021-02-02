package in.tagteen.tagteen;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.LinearLayout;

import in.tagteen.tagteen.util.Constants;


public class AlbumPhotosActivity extends AppCompatActivity  {
    private int int_position;
    private GridLayoutManager lLayout;
    private AlbumGridViewAdapter adapter;
    private RecyclerView image_List;
    private LinearLayout ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallary_main_activity);
        int_position = getIntent().getIntExtra("value", 0);
        boolean isSingleSelect = getIntent().getBooleanExtra(Constants.IS_SINGLE_SELECTION, false);

        image_List = (RecyclerView) findViewById(R.id.gv_folder);
        lLayout = new GridLayoutManager(AlbumPhotosActivity.this, 3);
        image_List.setHasFixedSize(true);
        image_List.setLayoutManager(lLayout);
        ok=(LinearLayout) findViewById(R.id.ok);

        adapter =
                new AlbumGridViewAdapter(
                        AlbumPhotosActivity.this, BrowsePicture2.al_images, int_position,ok, isSingleSelect);
        image_List.setAdapter(adapter);
    }
}
