package in.tagteen.tagteen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Adapters.CategorySingupAdapter1;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class HobbiesActivity2_new extends Activity implements AsyncResponse {
    private ArrayList<CategoryBean> categoryList;
    private Animation bottomUp;
    private Animation Upbottom;
    private ImageView image;
    Intent it;
    private CategorySingupAdapter1 adapter;
    private RelativeLayout hobby_container;
    private static final String TAG = "HobbySelectionPage";
    private String from = "";
    private ArrayList<String> selectedCategories = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hobbies_layout);
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        if (intent.getStringArrayListExtra(Constants.SELECTED_CATEGORIES) != null) {
            this.selectedCategories = intent.getStringArrayListExtra(Constants.SELECTED_CATEGORIES);
        }
        categoryList = new ArrayList<>();

        ImageView imageView = (ImageView) findViewById(R.id.hobby_imageButton);
        hobby_container = (RelativeLayout) findViewById(R.id.hobby_container);
        RecyclerView recylerview = (RecyclerView) findViewById(R.id.recylerview);

        this.getAllCategories();

        recylerview.setLayoutManager(new GridLayoutManager(HobbiesActivity2_new.this, 3));
        adapter = new CategorySingupAdapter1(this, categoryList, HobbiesActivity2_new.this, this.selectedCategories);
        recylerview.setAdapter(adapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CategoryBean> selectedCategories = adapter.getSelectedCategories();
                if (selectedCategories.size() >= 5) {
                    JSONArray list = new JSONArray();
                    List<String> selectedCtyNames = new ArrayList<String>();
                    for (CategoryBean cty : selectedCategories) {
                        list.put(cty.getId());
                        selectedCtyNames.add(cty.getCategoryName());
                    }
                    List<String> hobbylistarry = new ArrayList<String>();
                    for (int i = 0; i < selectedCtyNames.size(); i++) {
                        hobbylistarry.add(selectedCtyNames.get(i).toString());
                    }
                    SharedPreferenceSingleton.getInstance().init(HobbiesActivity2_new.this);
                    Log.d("hobby", list.toString());
                    SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.HOBBY, list.toString());
                    if (from.equalsIgnoreCase("signup")) {
                        Intent it = new Intent(HobbiesActivity2_new.this, PhotoUpload.class);
                        startActivity(it);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("hobbyliststring", list.toString());
                        intent.putStringArrayListExtra("hobbylist", (ArrayList<String>) hobbylistarry);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    Utils.showAlertDialog(
                            HobbiesActivity2_new.this, "You need to choose minimum of 5 and maximum of 10", "Alert");
                }
            }
        });

        bottomUp = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.slide_up);
        bottomUp.setDuration(1500);

        Upbottom = AnimationUtils.loadAnimation(this, in.tagteen.tagteen.R.anim.slide_down);
        Upbottom.setDuration(1500);
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

    @AntonyChanges
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
                    if (!categoryName.equalsIgnoreCase("All"))
                        categoryList.add(new CategoryBean(categoryId, categoryName));
                }
                adapter.initCategorySelection();
                adapter.notifyDataSetChanged();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllCategories() {
        if (Utils.isNetworkAvailable(this)) {
            AsyncWorker mWorker = new AsyncWorker(this);
            mWorker.delegate = this;
            mWorker.delegate = this;
            JSONObject BroadcastObject = new JSONObject();
            mWorker.execute(ServerConnector.REQUEST_GET_ALL_CATEGORIES, BroadcastObject.toString(), RequestConstants.GET_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_GET_CATEGORIES);
        } else {
            Utils.showShortToast(this, getString(R.string.no_connection));
        }
    }
}
