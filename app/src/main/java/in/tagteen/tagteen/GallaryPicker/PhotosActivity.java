package in.tagteen.tagteen.GallaryPicker;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import in.tagteen.tagteen.R;


public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.priview_activity);

      /*  ImageView imageView= (ImageView)findViewById(R.id.image_iv);
        SharedPreferenceSingleton.getInstance().init(this);*/
      /* String url=SharedPreferenceSingleton.getInstance().getStringPreference("photo");*/
       /* Glide.with(this).load("file:/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20170514-WA0000.jpg")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);*/
        changeFragment(new EffectsFilterFragment());

    }


    public void changeFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, targetFragment);
        transaction.commit();
    }



}
