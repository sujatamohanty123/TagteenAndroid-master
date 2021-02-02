package in.tagteen.tagteen.teenfeed;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.teenfeed.adapter.SearchArticleAdapter;
import in.tagteen.tagteen.teenfeed.model.SearchArticleModel;

/**
 * Created by lovekushvishwakarma on 10/12/17.
 */

public class SearchArticleActivity extends AppCompatActivity implements AsyncResponse {

    private ArrayList<SearchArticleModel> searchList;
    private SearchArticleAdapter adapter;
    private boolean disableSearch = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_article_layot);
        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        EditText edtbox = (EditText) findViewById(R.id.edtbox);
        ImageView cross_search=(ImageView)findViewById(R.id.cross_search);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);

        edtbox.setHint("Search Teenfeed...");

        searchList = new ArrayList<>();
        adapter = new SearchArticleAdapter(this, searchList);
        list.setAdapter(adapter);
        getSearchData("");

        edtbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getSearchData(editable.toString());
            }
        });

        cross_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getSearchData(String searchtext) {
        if (this.disableSearch) {
            // FIXME: Blocking search functionality for now.
            return;
        }
        AsyncWorker mWorker = new AsyncWorker(this);
        mWorker.delegate = this;
        mWorker.delegate = SearchArticleActivity.this;
        JSONObject BroadcastObject = new JSONObject();
        try {
            BroadcastObject.put("search_data", searchtext);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWorker.execute(
                ServerConnector.REQUEST_SEARCH_ARTICLE,
                BroadcastObject.toString(),
                RequestConstants.POST_REQUEST,
                RequestConstants.HEADER_NO,
                RequestConstants.REQUEST_SEARCH_ARTICLE);
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        System.out.print("" + output);
        try {
            searchList.clear();
            JSONObject jsonObject = new JSONObject(output);
            boolean flag = jsonObject.getBoolean("success");
            JSONArray arrayJson = jsonObject.getJSONArray("data");
            for (int i = 0; i < arrayJson.length(); i++) {
                JSONObject data = arrayJson.getJSONObject(i);
                String categorie_id = data.getString("categorie_id");
                String categorie_name = data.getString("categorie_name");
                String content = data.getString("content");
                int conversation_count = data.getInt("conversation_count");
                int cool_count = data.getInt("cool_count");
                String date_created = data.getString("date_created");
                String email = data.getString("email");
                String first_name = data.getString("first_name");
                boolean is_selfie = data.getBoolean("is_selfie");
                String last_name = data.getString("last_name");
                int like_count = data.getInt("like_count");
                String pincode = data.getString("pincode");
                String post_creator_id = data.getString("post_creator_id");
                String post_type_id = data.getString("post_type_id");
                String profile_url = data.getString("profile_url");
                String school_name = data.getString("school_name");
                String tagged_number = data.getString("tagged_number");
                String user_cool = data.getString("user_cool");
                boolean user_like = data.getBoolean("user_like");
                String _id = data.getString("_id");
                JSONArray images = data.getJSONArray("image");
                JSONObject imagedata = images.getJSONObject(0);
                String image = imagedata.getString("url");

                searchList.add(new SearchArticleModel(categorie_id, categorie_name, content, date_created, email, first_name, image, last_name,
                        pincode, post_creator_id, post_type_id, profile_url, school_name, tagged_number, user_cool, _id, is_selfie, user_like,
                        conversation_count, cool_count, like_count));
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
