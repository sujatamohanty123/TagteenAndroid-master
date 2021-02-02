package in.tagteen.tagteen.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import in.tagteen.tagteen.R;

public class AboutActivity extends AppCompatActivity {
    private ListView listItemView;
    private ImageView aboutback1;

    // Define string array.
    private String[] listItemsValue = new String[] {"Website","Licence Information","Terms of Use","Privacy policy","Community Policy"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        listItemView = (ListView)findViewById(R.id.listabout);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simple_list11, R.id.text12, listItemsValue);

        listItemView.setAdapter(adapter);

        listItemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    Uri uri = Uri.parse("http://tagteen.in/"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

                if (position == 2) {
                    Intent myIntent = new Intent(view.getContext(), TermsOfUseActivity.class);
                    startActivityForResult(myIntent, 0);
                }
                if (position == 3) {
                    Intent myIntent = new Intent(view.getContext(), PrivacyPolicyActivity.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 4) {
                    Intent myIntent = new Intent(view.getContext(), CommunityPolicyActivity.class);
                    startActivityForResult(myIntent, 0);
                }


            }
        });

        aboutback1=(ImageView)findViewById(R.id.aboutback);
        aboutback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AboutActivity.this,AppSettings.class);
                startActivity(intent);

            }
        });
    }

}

