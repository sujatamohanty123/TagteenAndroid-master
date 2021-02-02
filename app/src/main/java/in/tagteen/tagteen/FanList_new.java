package in.tagteen.tagteen;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.tagteen.tagteen.profile.FanList;
import in.tagteen.tagteen.utils.TagteenApplication;

public class FanList_new extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    private TextView no_internet;
    private LinearLayout buzz_main_container;
    private String otheruser_id;
    private String flag = "";
    private String tittle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_with_framonly);

        buzz_main_container=(LinearLayout) findViewById(R.id.buzz_main_container);
        no_internet=(TextView) findViewById(R.id.no_internet);
        checkConnection();

        Intent intent = getIntent();
        if (intent.hasExtra("flag")) {
            flag = intent.getStringExtra("flag");
            tittle = intent.getStringExtra("tittle");
            otheruser_id = intent.getStringExtra("user_id");
        }
        Bundle bundle = new Bundle();
        bundle.putString("position", "profile");
        bundle.putString("flag", flag);
        bundle.putString("tittle", tittle);
        bundle.putString("user_id", otheruser_id);
        Fragment profile = new FanList();
        profile.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.framMain, profile);
        fragmentTransaction.commit();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {

        if (isConnected) {
            buzz_main_container.setVisibility(View.VISIBLE);
            no_internet.setVisibility(View.GONE);
        } else {
            buzz_main_container.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
    @Override
    public void onResume() {
        super.onResume();
        TagteenApplication.getInstance().setConnectivityListener(this);
    }
}
