package in.tagteen.tagteen.VideoEditor.editvideo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bokecc.shortvideo.combineimages.model.SelectImageInfo;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.VideoEditor.MainActivity;
import in.tagteen.tagteen.VideoEditor.adapter.VideoAndImagePagerAdapter;
import in.tagteen.tagteen.VideoEditor.fragment.SelectImageFragment;
import in.tagteen.tagteen.VideoEditor.fragment.SelectVideoFragment;
import in.tagteen.tagteen.VideoEditor.util.MultiUtils;
import in.tagteen.tagteen.VideoEditor.widget.NoScrollViewPager;

public class SelectVideoOrImageActivity extends FragmentActivity implements View.OnClickListener {
    private Activity activity;
    private NoScrollViewPager vp;
    private SelectVideoFragment selectVideoFragment;
    private SelectImageFragment selectImageFragment;
    private List<Fragment> datas;
    private VideoAndImagePagerAdapter adapter;
    private TextView tv_selct_video, tv_selct_image, tv_complete;
    private View view_selct_video, view_selct_image;
    private ImageView iv_back;
    private boolean isComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video_or_image);
        activity = this;
        MultiUtils.setFullScreen(this);

        initView();

        datas = new ArrayList<>();
        if (selectVideoFragment == null) {
            selectVideoFragment = new SelectVideoFragment();
        }
        datas.add(selectVideoFragment);

        if (selectImageFragment == null) {
            selectImageFragment = new SelectImageFragment();
        }
        datas.add(selectImageFragment);

        adapter = new VideoAndImagePagerAdapter(getSupportFragmentManager(), datas);
        vp.setOffscreenPageLimit(2);
        vp.setAdapter(adapter);
    }

    private void initView() {
        vp = findViewById(R.id.vp);
        tv_selct_video = findViewById(R.id.tv_selct_video);
        tv_selct_image = findViewById(R.id.tv_selct_image);
        view_selct_video = findViewById(R.id.view_selct_video);
        view_selct_image = findViewById(R.id.view_selct_image);
        iv_back = findViewById(R.id.iv_back);
        tv_complete = findViewById(R.id.tv_complete);

        tv_selct_video.setOnClickListener(this);
        tv_selct_image.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_selct_video:
                tv_selct_video.setTextColor(activity.getResources().getColor(R.color.white));
                tv_selct_image.setTextColor(activity.getResources().getColor(R.color.fiftyWhite));
                view_selct_video.setVisibility(View.VISIBLE);
                view_selct_image.setVisibility(View.INVISIBLE);
                vp.setCurrentItem(0);
                break;

            case R.id.tv_selct_image:
                tv_selct_image.setTextColor(activity.getResources().getColor(R.color.white));
                tv_selct_video.setTextColor(activity.getResources().getColor(R.color.fiftyWhite));
                view_selct_image.setVisibility(View.VISIBLE);
                view_selct_video.setVisibility(View.INVISIBLE);
                vp.setCurrentItem(1);
                break;

            case R.id.iv_back:
                back();
                break;

            case R.id.tv_complete:
                int currentItem = vp.getCurrentItem();
                Fragment fragment = datas.get(currentItem);
                if (currentItem == 1) {
                    ArrayList<SelectImageInfo> selectedImages = (ArrayList<SelectImageInfo>) ((SelectImageFragment) fragment).getSelectedImages();
                    if (selectedImages.size() < 3) {
                        MultiUtils.showToast(activity, "Choose at least 3 pictures");
                        return;
                    }

                    Intent intent = new Intent(activity, CombineImagesActivity.class);
                    intent.putParcelableArrayListExtra("images", selectedImages);
                    startActivity(intent);
                    isComplete = true;
                    sendBroadcast(new Intent("in.tagteen.tagteen..CLOSE_MAIN"));

                }
                break;
        }
    }

    private void back() {
        int currentFragment = vp.getCurrentItem();
        if (currentFragment==1 && isComplete){
            startActivity(new Intent(activity, MainActivity.class));
        }
        finish();
    }

    @Override
    public Resources getResources() {
        Resources resources =  super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
        return resources;
    }

    @Override
    public void onBackPressed() {
        back();
//        startActivity(new Intent(activity, MainActivity.class));
//        super.onBackPressed();
    }
}
