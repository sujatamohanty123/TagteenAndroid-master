package in.tagteen.tagteen;


import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Adapters.FindFriendsAdapter;
import in.tagteen.tagteen.Model.FriendSeach;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private List<FriendSeach.UserInfo> friendlist = new ArrayList<>();
    private RecyclerView recyclerView;
    private FindFriendsAdapter mAdapter;
    private int flag = 2;
    private LinearLayout nofriendslay;
    private EditText searchkey;
    private ImageView cross, imageBack;
    private SwipeRefreshLayout simpleSwipeRefreshLayout;
    private TextView no_internet;
    private LinearLayout buzz_main_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.buzz_main_container = (LinearLayout) findViewById(R.id.buzz_main_container);
        this.no_internet = (TextView) findViewById(R.id.no_internet);
        checkConnection();

        this.cross = (ImageView) findViewById(R.id.cross_search);
        this.nofriendslay = (LinearLayout) findViewById(R.id.nofriendslay);
        this.searchkey = (EditText) findViewById(R.id.searchtext);
        this.searchkey.requestFocus();

        this.recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        this.recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mLayoutManager.getOrientation());
        this.recyclerView.addItemDecoration(mDividerItemDecoration);

        this.mAdapter = new FindFriendsAdapter(SearchActivity.this, this.friendlist, this.flag);
        this.recyclerView.setAdapter(this.mAdapter);

        this.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.searchkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*String stext = searchkey.getText().toString();
                if (stext.length() > 0) {
                    apicall(stext);
                } else {
                    friendlist.clear();
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    apicall(editable.toString());
                } else {
                    friendlist.clear();
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            buzz_main_container.setVisibility(View.VISIBLE);
            no_internet.setVisibility(View.GONE);
        } else {
            buzz_main_container.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }
    }

    private void apicall(String s) {
        Apimethods methods = API_Call_Retrofit.getretrofit(SearchActivity.this).create(Apimethods.class);
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Call<FriendSeach> call = methods.getFriendsearchlist(s, token);
        call.enqueue(new Callback<FriendSeach>() {
            @Override
            public void onResponse(Call<FriendSeach> call, Response<FriendSeach> response) {
                if (response.code() == 200) {
                    friendlist.clear();
                    FriendSeach searchmodel = response.body();
                    if (searchmodel.getSuccess() == true) {
                        List<FriendSeach.UserInfo> result = searchmodel.getUserInfos();
                        if (result != null && result.isEmpty() == false) {
                            friendlist.addAll(result);
                        }
                    }
                }
                setSearchAdapter();
            }

            @Override
            public void onFailure(Call<FriendSeach> call, Throwable t) {
                Log.e("SearchFriends", "Search fails : " + t.getMessage());
            }
        });

    }

    private void setSearchAdapter() {
        if (this == null) {
            return;
        }

        if (this.friendlist != null && this.friendlist.size() > 0) {
            this.recyclerView.setVisibility(View.VISIBLE);
            this.nofriendslay.setVisibility(View.GONE);
            this.mAdapter.notifyDataSetChanged();
        } else {
            this.recyclerView.setVisibility(View.GONE);
            this.nofriendslay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (this == null) {
            return;
        }
        showSnack(isConnected);
    }

    @Override
    public void onResume() {
        super.onResume();
        TagteenApplication.getInstance().setConnectivityListener(this);
    }
}
