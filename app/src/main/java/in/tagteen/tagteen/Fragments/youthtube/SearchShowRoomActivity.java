package in.tagteen.tagteen.Fragments.youthtube;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import in.tagteen.tagteen.Fragments.beans.SearchInputJson;
import in.tagteen.tagteen.Fragments.beans.SearchShowRoomBean;
import in.tagteen.tagteen.Fragments.beans.ShowRooms;
import in.tagteen.tagteen.Fragments.youthtube.adapter.SearchShowroomAdapter;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovekushvishwakarma on 10/12/17.
 */

public class SearchShowRoomActivity extends AppCompatActivity implements AsyncResponse {

    private ArrayList<ShowRooms> searchList;
    private SearchShowroomAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_article_layot);
        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        EditText edtbox = (EditText) findViewById(R.id.edtbox);
        ImageView cross_search = (ImageView) findViewById(R.id.cross_search);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);

        edtbox.setHint("Search Showroom...");

        searchList = new ArrayList<>();
        adapter = new SearchShowroomAdapter(this, searchList);
        list.setAdapter(adapter);

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
        SearchInputJson inputJson = new SearchInputJson();
        inputJson.setSearch_data(searchtext);
        SharedPreferenceSingleton.getInstance().init(SearchShowRoomActivity.this);
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(SearchShowRoomActivity.this).create(Apimethods.class);
        Call<SearchShowRoomBean> call = methods.searchShowrooms(inputJson, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<SearchShowRoomBean>() {
            @Override
            public void onResponse(Call<SearchShowRoomBean> call, Response<SearchShowRoomBean> response) {
                int statuscode = response.code();
                if (statuscode == 200) {
                    searchList.clear();
                    SearchShowRoomBean getresponsemodel = response.body();
                    ArrayList<ShowRooms> getdatalist = (ArrayList<ShowRooms>) getresponsemodel.getData();
                    for (int i = 0; i < getdatalist.size(); i++) {
                        searchList.add(getdatalist.get(i));
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<SearchShowRoomBean> call, Throwable t) {
                //Toast.makeText(getActivity(),"fail",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        System.out.print("" + output);

    }
}
