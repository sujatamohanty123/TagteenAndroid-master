package in.tagteen.tagteen;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.profile.ProfilePhotosFragment;
import in.tagteen.tagteen.profile.ProfileFriendFragment;

public class SeeFrndPhotoActivity extends AppCompatActivity {
   String user_id="";
    String user_name="";
    String seeflag="";
    Fragment fragment;
    ImageView imageback;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_frnd_photo);

        user_id=getIntent().getStringExtra("user_id");
        user_name=getIntent().getStringExtra("user_name");
        seeflag=getIntent().getStringExtra("seeflag");
        imageback=(ImageView)findViewById(R.id.imageback) ;
        text=(TextView) findViewById(R.id.text) ;
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        if(seeflag.equalsIgnoreCase("seefriend")) {
            fragment = new ProfileFriendFragment();
            text.setText(user_name+"'s friends");
        }
        if(seeflag.equalsIgnoreCase("seephoto")) {
            fragment = new ProfilePhotosFragment();
            text.setText(user_name+"'s photos");
        }
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fag_see_photo_frnd, fragment);
        fragmentTransaction.commit();

    }
}
