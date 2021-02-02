package in.tagteen.tagteen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.tagteen.tagteen.utils.DateTimeCalculation;

public class ImagePrivewActivity extends Activity {
    String sele = "selectedPath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_privew);
        Intent intent = getIntent();
        String imagepath = intent.getStringExtra(sele);
        String name = intent.getStringExtra("userName");
        String date = intent.getStringExtra("uploadDate");
        ImageView imag = (ImageView) findViewById(R.id.privew_image);
        ImageView back = (ImageView) findViewById(R.id.backButton);

        TextView userName   = (TextView) findViewById(R.id.userName);
        TextView uploadDate = (TextView) findViewById(R.id.upload_date);
        userName.setText(name);
        uploadDate.setText(DateTimeCalculation.getActualDateFrom(date));
        Glide.with(this).load(imagepath).fitCenter().centerCrop().placeholder(R.drawable.placeholder).into(imag);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
