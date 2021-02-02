package in.tagteen.tagteen;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import in.tagteen.tagteen.Adapters.NewGroupSelectionAdapter;
import in.tagteen.tagteen.Adapters.New_Group_Adapter;
import in.tagteen.tagteen.Model.Friend;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.Model.SingleItemModel;
import in.tagteen.tagteen.Model.UserModel;
import in.tagteen.tagteen.TagteenInterface.UserSelectionList;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class BroadcastListActivity extends AppCompatActivity {

    EditText txtOutput;
    ImageView imageView, cross_view,backbtn;
    Animation alpha;
    LinearLayout linearLayout,linear1;
    private final int SPEECH_RECOGNITION_CODE = 1;
    private ImageView btnMicrophone;
    private List<Friend> friendlist = new ArrayList<>();
    private RecyclerView mUserRecyclerView ,mUserRecyclerSelected;
    private New_Group_Adapter adapter;
    ArrayList<SectionDataModel> allSampleData;
    NewGroupSelectionAdapter mSelectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(in.tagteen.tagteen.R.layout.activity_new_group);
        allSampleData = new ArrayList<SectionDataModel>();
        backbtn=(ImageView) findViewById(in.tagteen.tagteen.R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView = (ImageView) findViewById(in.tagteen.tagteen.R.id.search_tab_button);
        cross_view = (ImageView) findViewById(in.tagteen.tagteen.R.id.cross_tab);
        txtOutput = (EditText) findViewById(in.tagteen.tagteen.R.id.search_edittext);
        linearLayout = (LinearLayout) findViewById(in.tagteen.tagteen.R.id.search_layout);
        linear1=(LinearLayout) findViewById(in.tagteen.tagteen.R.id.linear1);
        mUserRecyclerSelected = (RecyclerView)findViewById(in.tagteen.tagteen.R.id.my_recycler_view12); //mUserRecyclerSelected
        alpha = AnimationUtils.loadAnimation(BroadcastListActivity.this, in.tagteen.tagteen.R.anim.tab_fab_out);
        alpha.setDuration(10);
        imageView.startAnimation(alpha);

        btnMicrophone = (ImageView) findViewById(in.tagteen.tagteen.R.id.btn_mic);
        btnMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
                linear1.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
            }
        });
        cross_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.INVISIBLE);
                linear1.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }
        });

        prepareDummyDta();
        createDummyData();

        mUserRecyclerView = (RecyclerView) findViewById(in.tagteen.tagteen.R.id.my_recycler_view11);
        adapter = new New_Group_Adapter(BroadcastListActivity.this,friendlist,allSampleData, new UserSelectionList() {
            @Override
            public void selectionList(List<Friend> userList) {

            }

            @Override
            public void selectionList(ArrayList<UserModel> userList) {

                mUserRecyclerSelected.setLayoutManager(new LinearLayoutManager(BroadcastListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                mSelectionAdapter = new NewGroupSelectionAdapter(BroadcastListActivity.this,userList,1);
                mUserRecyclerSelected.setAdapter(mSelectionAdapter);
                mSelectionAdapter.notifyDataSetChanged();

            }
        });
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(BroadcastListActivity.this, LinearLayoutManager.VERTICAL, false));

        mUserRecyclerView.setAdapter(adapter);


    }

    private void prepareDummyDta() {
        Friend friend = new Friend("Ajit Kumar", "ABSD431", in.tagteen.tagteen.R.drawable.pr_pic);
        friendlist.add(friend);

        friend = new Friend("sujata", "ABSD432", in.tagteen.tagteen.R.drawable.girl);
        friendlist.add(friend);

        friend = new Friend("preeti", "ABSD433", in.tagteen.tagteen.R.drawable.pr_pic);
        friendlist.add(friend);
    }
    public void createDummyData() {
        for (int i = 1; i <= 3; i++) {

            SectionDataModel dm = new SectionDataModel();
            dm.setHeaderTitle("Section " + i);
            ArrayList<SingleItemModel> singleItem = new ArrayList<>();
            for (int j = 0; j <= 2; j++) {
                singleItem.add(new SingleItemModel("Item " + j, "URL " + j));
            }
            dm.setAllItemsInSection(singleItem);
            allSampleData.add(dm);

        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(BroadcastListActivity.this,
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}