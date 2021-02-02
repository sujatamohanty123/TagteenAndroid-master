package in.tagteen.tagteen.selfyManager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageView;

import in.tagteen.tagteen.R;

//@ant22
public class StatusPlaceholderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.selfie_status);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(imageView);
    }
}
