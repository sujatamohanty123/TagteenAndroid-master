package in.tagteen.tagteen.Fragments.youthtube;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import in.tagteen.tagteen.R;


public class VideoDetailListActivity extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_animal_detail);
        int flag=getIntent().getIntExtra("videoplay",0);
        int select_caregory_id=getIntent().getIntExtra("category_id",0);
        //changeFragment(new Basic4ListFragment());
        changeFragment(new YouthTubeVideoList(VideoDetailListActivity.this,flag,select_caregory_id));
    }

    public void changeFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.auto_play_video, targetFragment);
        transaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

   /* public void myFancyMethod(View v) {
        Intent it =new Intent (this, MediaPlayerActivity.class);
        startActivity(it);
    }*/

    public void changeOrientationClick() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
    }
}