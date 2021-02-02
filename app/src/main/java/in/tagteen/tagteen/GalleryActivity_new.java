package in.tagteen.tagteen;

import android.graphics.Bitmap;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import in.tagteen.tagteen.Adapters.MyCustomPagerAdapter2;
import in.tagteen.tagteen.util.Constants;

public class GalleryActivity_new extends AppCompatActivity {
    private ViewPager viewPager;
    private MyCustomPagerAdapter2 myCustomPagerAdapter;
    private ArrayList<String> pathlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_new);
        this.pathlist = getIntent().getStringArrayListExtra(GalleryActivity.EXTRA_NAME);
        this.viewPager = (ViewPager)findViewById(R.id.viewPager);

        Bitmap bitmap = null;
        if (getIntent().getParcelableExtra(Constants.IMAGE_BITMAP) != null) {
            bitmap = getIntent().getParcelableExtra(Constants.IMAGE_BITMAP);
        }

        int currentImageIndex = getIntent().getIntExtra(Constants.SELETED_IMAGE_INDEX, 0);
        this.myCustomPagerAdapter = new MyCustomPagerAdapter2(GalleryActivity_new.this, bitmap, this.pathlist,1);
        this.viewPager.setAdapter(this.myCustomPagerAdapter);
        this.viewPager.setCurrentItem(currentImageIndex);
    }
}
