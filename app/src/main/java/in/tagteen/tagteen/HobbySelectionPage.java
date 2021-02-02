package in.tagteen.tagteen;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;


public class HobbySelectionPage extends AppCompatActivity {
    List<String> selectedHobby = new ArrayList<>();
    ImageView image;
    Intent it;


    private static final String TAG = "HobbySelectionPage";
    String a[] = {"Architecture", "Dance", "Music", "Fashion",
            "Finance", "Indian Air Force", "Indian Army", "Lawyer",
            "Movies", "Marketing", "Lifestyle", "Gaming",
            "Arts", "Health & Fitness", "Entrepreneurs",
            "Air Hostess", "Banking", "Celebrities"
            , "Architecture", "Dance", "Music", "Fashion",
            "Finance", "Indian Air Force", "Indian Army", "Lawyer",
            "Movies", "Marketing", "Lifestyle", "Gaming",
            "Arts", "Health & Fitness", "Entrepreneurs",
            "Air Hostess", "Banking", "Celebrities",
            "Architecture", "Dance", "Music", "Fashion",
            "Finance", "Indian Air Force", "Indian Army", "Lawyer",
            "Movies", "Marketing", "Lifestyle", "Gaming",
            "Arts", "Health & Fitness", "Entrepreneurs",
            "Air Hostess", "Banking", "Celebrities"
            , "Architecture", "Dance", "Music", "Fashion",
            "Finance", "Indian Air Force", "Indian Army", "Lawyer",
            "Movies", "Marketing", "Lifestyle", "Gaming",
            "Arts", "Health & Fitness", "Entrepreneurs",
            "Air Hostess", "Banking", "Celebrities"};

    String b[] = {"#B71C1C", "#3F51B5", "#03A9F4", "#1B5E20",
            "#F57F17", "#FFD600", "#BF360C", "#880E4F",
            "#1A237E", "#006064", "#33691E", "#FF6F00",
            "#E91E63", "#9C27B0", "#0D47A1", "#004D40",
            "#827717", "#E65100", "#B71C1C", "#3F51B5",
            "#03A9F4", "#1B5E20", "#F57F17", "#FFD600",
            "#BF360C", "#880E4F", "#1A237E", "#006064",
            "#33691E", "#FF6F00", "#E91E63", "#9C27B0",
            "#0D47A1", "#004D40", "#827717", "#E65100",
            "#B71C1C", "#3F51B5", "#03A9F4", "#1B5E20",
            "#F57F17", "#FFD600", "#BF360C", "#880E4F",
            "#1A237E", "#006064", "#33691E", "#FF6F00",
            "#E91E63", "#9C27B0", "#0D47A1", "#004D40",
            "#827717", "#E65100", "#B71C1C", "#3F51B5",
            "#03A9F4", "#1B5E20", "#F57F17", "#FFD600",
            "#BF360C", "#880E4F", "#1A237E", "#006064",
            "#33691E", "#FF6F00", "#E91E63", "#9C27B0",
            "#0D47A1", "#004D40", "#827717", "#E65100"};


    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobby_selection_page);
        imageView = (ImageView) findViewById(R.id.hobby_imageButton);

        FlexboxLayout flexbox = (FlexboxLayout) findViewById(R.id.flexbox);
        for (int i = 0; i < a.length; i++) {
           /* ChipCloudConfig config = new ChipCloudConfig()
                    .selectMode(ChipCloud.SelectMode.multi)
                    .checkedChipColor(Color.parseColor("#000000"))
                    .checkedTextColor(Color.parseColor("#ffffff"))
                    .uncheckedChipColor(Color.parseColor(b[i]))
                    .useInsetPadding(true)
                    .uncheckedTextColor(Color.parseColor("#ffffff"));
            chipCloud = new ChipCloud(this, flexbox, config);
            chipCloud.addChip(a[i]);
            chipCloud.setListener(new ChipListener() {
                @Override
                public void chipCheckedChange(int index, boolean checked, boolean userClick) {
                    if (checked) {
                        if (selectedHobby.size() < 10) {

                            selectedHobby.add(a[index]);

                        } else {
                            chipCloud.deselectIndex(index);
                            in.tagteen.tagteen.util.Utils.showAlertDialog(HobbySelectionPage.this, "You can select only upto 10", "Alert");
                        }
                    } else {
                        selectedHobby.remove(a[index]);
                    }
                }
            });*/
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedHobby.size() < 11 && selectedHobby.size() > 4) {
                    JSONArray list = new JSONArray();
                    for (int i = 0; i < selectedHobby.size(); i++) {
                        list.put(selectedHobby.get(i));
                    }

                    SharedPreferenceSingleton.getInstance().init(HobbySelectionPage.this);
                    SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.HOBBY, list.toString());
                    Intent it = new Intent(HobbySelectionPage.this, PhotoUpload.class);
                    startActivity(it);
                } else {
                    in.tagteen.tagteen.util.Utils.showAlertDialog(HobbySelectionPage.this, "You need to choose minimum of 5 and maximum of 10", "Alert");
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }


}
