package in.tagteen.tagteen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Adapters.CategorySingupAdapter;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

import static in.tagteen.tagteen.Adapters.CategorySingupAdapter.hobbyList;
import static in.tagteen.tagteen.Adapters.CategorySingupAdapter.selectedList;


public class HobbiesActivity2 extends Activity implements AsyncResponse {
    private ArrayList<CategoryBean> categoryList;
    private Animation bottomUp;
    private Animation Upbottom;
    private ImageView image;
    Intent it;
    private CategorySingupAdapter adapter;
    private RelativeLayout hobby_container;
    private static final String TAG = "HobbySelectionPage";
    private String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hobbies_layout);
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        categoryList = new ArrayList<>();

        ImageView imageView = (ImageView) findViewById(R.id.hobby_imageButton);
        hobby_container = (RelativeLayout) findViewById(R.id.hobby_container);

        adapter = new CategorySingupAdapter(this, categoryList, HobbiesActivity2.this);
        RecyclerView recylerview = (RecyclerView) findViewById(R.id.recylerview);
        // recylerview.setLayoutManager(new GridLayoutManager(HobbiesActivity2.this, 3));
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recylerview.setLayoutManager(mLayoutManager);
        recylerview.setAdapter(adapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> hobbylistarry=new ArrayList<String>();
                if (selectedList.size() >= 5) {
                    JSONArray list = new JSONArray();
                    for (int i = 0; i < selectedList.size(); i++) {
                        list.put(selectedList.get(i));
                    }
                    for (int i = 0; i < hobbyList.size(); i++) {
                        hobbylistarry.add(hobbyList.get(i).toString());
                    }
                    SharedPreferenceSingleton.getInstance().init(HobbiesActivity2.this);
                    SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.HOBBY, list.toString());
                    if (from.equalsIgnoreCase("signup")) {
                        Intent it = new Intent(HobbiesActivity2.this, PhotoUpload.class);
                        startActivity(it);
                    } else {
                        //EditProfileActivity.addUpdateHobbies(list.toString());
                        Intent intent = new Intent();
                        intent.putExtra("hobbyliststring",list.toString());
                        intent.putStringArrayListExtra("hobbylist", (ArrayList<String>) hobbylistarry);
                        setResult(1, intent);
                        finish();
                    }
                } else {
                    Utils.showAlertDialog(HobbiesActivity2.this, "You need to choose minimum of 5 and maximum of 10", "Alert");
                }
            }
        });


        bottomUp = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.slide_up);
        bottomUp.setDuration(1500);

        Upbottom = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.slide_down);
        Upbottom.setDuration(1500);


        getAllCategories();
    }

    public void startAnim() {
        hobby_container.setVisibility(View.VISIBLE);
        hobby_container.startAnimation(bottomUp);
    }
    public void endAnim() {
        hobby_container.setVisibility(View.GONE);
        hobby_container.startAnimation(Upbottom);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        try {
            JSONObject data = new JSONObject(output);
            if (data.getBoolean("success")) {
                JSONArray dataArr = data.getJSONArray("data");
                for (int i = 0; i < dataArr.length(); i++) {
                    JSONObject object = dataArr.getJSONObject(i);
                    String categoryName = object.getString(Constants.CATEGORY_NAME_PARAM);
                    int categoryId = object.getInt(Constants.CATEGORY_ID_PARAM);
                    categoryList.add(new CategoryBean(categoryId, categoryName));
                }
                adapter.notifyDataSetChanged();
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getAllCategories() {
        AsyncWorker mWorker = new AsyncWorker(this);
        mWorker.delegate = this;
        mWorker.delegate = HobbiesActivity2.this;
        JSONObject BroadcastObject = new JSONObject();
        mWorker.execute(ServerConnector.REQUEST_GET_ALL_CATEGORIES, BroadcastObject.toString(), RequestConstants.GET_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_GET_CATEGORIES);
    }

}
