package in.tagteen.tagteen;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class IdeaBoxInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ideabox_info);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        setContentView(imageView);
    }
}
