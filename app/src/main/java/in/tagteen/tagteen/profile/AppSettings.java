package in.tagteen.tagteen.profile;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONObject;

import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.profile.adapter.AppSettingAdapter;

/**
 * Created by lovekushvishwakarma on 12/10/17.
 */

public class AppSettings extends Activity implements AsyncResponse {

    AppSettingAdapter adapter;
    ArrayList<String> list;
    private  static AsyncWorker mWorker;
    private ImageView settingback;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_settings);
        mWorker = new AsyncWorker(this);
        mWorker.delegate = this;
        mWorker.delegate = this;

        settingback=(ImageView)findViewById(R.id.settingback);
        settingback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        list = new ArrayList<>();
        list.add("Manage account");
        list.add("Rate Us");
        list.add("Notifications");
        //list.add("Privacy");
       // list.add("Chat Settings");
        list.add("FAQ's");
        list.add("Feedback");
        list.add("About");
        list.add("Logout");

        RecyclerView recyviewCatUserInterest = (RecyclerView) findViewById(R.id.settinglist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyviewCatUserInterest.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecorationCate = new DividerItemDecoration(recyviewCatUserInterest.getContext(), layoutManager.getOrientation());
        recyviewCatUserInterest.addItemDecoration(mDividerItemDecorationCate);
        adapter = new AppSettingAdapter(list, this);
        recyviewCatUserInterest.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public static void logout(){
        JSONObject BroadcastObject = new JSONObject();
        mWorker.execute(ServerConnector.REQUEST_LOGOUT, BroadcastObject.toString(), RequestConstants.GET_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_DELETE_YOUTHTUBE_POST);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
            System.out.print(""+output);
    }
}
