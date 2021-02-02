package in.tagteen.tagteen;

import android.app.Activity;
import android.content.Intent;

import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.util.Utils;
import java.util.ArrayList;

import in.tagteen.tagteen.Adapters.MyCustomPagerAdapter1;
import in.tagteen.tagteen.Filter.activity.ActivityGallery;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class ActivityGallery_Viewpager extends Activity {
    ViewPager viewPager;
    MyCustomPagerAdapter1 myCustomPagerAdapter;
    ArrayList<String> pathlist=new ArrayList<>();
    TextView done;
    ArrayList<String> newList = new ArrayList<String>();
    int clickedPosition;
    ImageView edit,cross;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery__viewpager);

        pathlist=getIntent().getStringArrayListExtra(ApplicationConstants.FILTERED_IMAGE_PATH);
        clickedPosition = getIntent().getExtras().getInt("position_id");
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        done = (TextView)findViewById(R.id.done);
         edit = (ImageView)findViewById(R.id.edit);
        cross=(ImageView)findViewById(R.id.cross);
        myCustomPagerAdapter = new MyCustomPagerAdapter1(ActivityGallery_Viewpager.this, pathlist,edit);
        viewPager.setAdapter(myCustomPagerAdapter);
        viewPager.setCurrentItem(clickedPosition);
       /* myCustomPagerAdapter.setOnItemClickListener(new MyCustomPagerAdapter1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String imagePath, int position) {
                Intent intent = new Intent(ActivityGallery_Viewpager.this, ActivityGallery.class);
                intent.putExtra("camera","gallery");
                intent.putStringArrayListExtra("ori_list",pathlist);
                 SharedPreferenceSingleton.getInstance().init(ActivityGallery_Viewpager.this);
                SharedPreferenceSingleton.getInstance().writeStringPreference("image_path",imagePath);
                startActivityForResult(intent,6);
            }
        });*/
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imagePath=viewPager.getCurrentItem();
                Intent intent = new Intent(ActivityGallery_Viewpager.this, ActivityGallery.class);
                intent.putExtra("camera","gallery");
                intent.putStringArrayListExtra("ori_list",pathlist);
                SharedPreferenceSingleton.getInstance().init(ActivityGallery_Viewpager.this);
                SharedPreferenceSingleton.getInstance().writeStringPreference("image_path",pathlist.get(imagePath).toString());
                startActivityForResult(intent,6);
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                if(newList.isEmpty()){
                    intent.putStringArrayListExtra("ImagePath", pathlist);
                }else {
                    intent.putStringArrayListExtra("ImagePath", newList);
                }
                setResult(4, intent);
                finish();
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 6) {
            newList=data.getStringArrayListExtra("filterlistimage");
            myCustomPagerAdapter = new MyCustomPagerAdapter1(ActivityGallery_Viewpager.this, newList, edit);
            viewPager.setAdapter(myCustomPagerAdapter);
            myCustomPagerAdapter.notifyDataSetChanged();
           /* myCustomPagerAdapter.setOnItemClickListener(new MyCustomPagerAdapter1.OnItemClickListener() {
                @Override
                public void onItemClick(View view, String imagePath, int position) {
                    Intent intent = new Intent(ActivityGallery_Viewpager.this, ActivityGallery.class);
                    intent.putExtra("camera","gallery");
                    intent.putStringArrayListExtra("ori_list",newList);
                    SharedPreferenceSingleton.getInstance().init(ActivityGallery_Viewpager.this);
                    SharedPreferenceSingleton.getInstance().writeStringPreference("image_path",imagePath);
                    startActivityForResult(intent,6);
                }
            });*/
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int imagePath=viewPager.getCurrentItem();
                    Intent intent = new Intent(ActivityGallery_Viewpager.this, ActivityGallery.class);
                    intent.putExtra("camera","gallery");
                    intent.putStringArrayListExtra("ori_list",newList);
                    SharedPreferenceSingleton.getInstance().init(ActivityGallery_Viewpager.this);
                    SharedPreferenceSingleton.getInstance().writeStringPreference("image_path",newList.get(imagePath).toString());
                    startActivityForResult(intent,6);
                }
            });
        }
    }
}

