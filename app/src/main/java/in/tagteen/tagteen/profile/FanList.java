package in.tagteen.tagteen.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetFanList;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.profile.adapter.FanListAdapter;
import in.tagteen.tagteen.utils.Commons;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovekushvishwakarma on 12/10/17.
 */

public class FanList extends BaseFragment {

  private FanListAdapter adapter;
  private String ApiURL = "";
  private int pageLimint = 20, pageNumber = 1;
  private SwipeRefreshLayout simpleSwipeRefreshLayout;
  private LinearLayout linerLoadmore;
  private boolean apiCallBlocler;
  private ArrayList<GetFanList.UserData> fanlist;
  private TextView textNofans;
  private RecyclerView recylerUserFans;
  private String otheruser_id;
  private String flag = "";
  private String tittle = "";
  private String position = "";
  private RelativeLayout layouttop;
  private LinearLayout fanList_linear;
  private EditText searchkey;
  private LinearLayout layoutProgress;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fan_list, container, false);
    fanlist = new ArrayList<>();
    layouttop = (RelativeLayout) view.findViewById(R.id.layouttop);
    simpleSwipeRefreshLayout =
        (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
    simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
    linerLoadmore = (LinearLayout) view.findViewById(R.id.linerLoadmore);
    textNofans = (TextView) view.findViewById(R.id.textNofans);
    textNofans.setVisibility(View.GONE);
    this.layoutProgress = (LinearLayout) view.findViewById(R.id.layoutProgress);

    this.bundle_data();

    if (position.equalsIgnoreCase("profile")) {
      layouttop.setVisibility(View.VISIBLE);
    } else {
      layouttop.setVisibility(View.GONE);
    }

    this.pageNumber = 1;
    if (flag.equalsIgnoreCase("1")) {
      this.getFanList();
    } else {
      this.getMyfans();
    }

    TextView txttitle = (TextView) view.findViewById(R.id.title);
    txttitle.setText(tittle);

    recylerUserFans = (RecyclerView) view.findViewById(R.id.fanList);
    fanList_linear = (LinearLayout) view.findViewById(R.id.fanList_linear);
    searchkey = (EditText) view.findViewById(R.id.searchtext);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    recylerUserFans.setLayoutManager(layoutManager);
        /*DividerItemDecoration mDividerItemDecorationCate = new DividerItemDecoration(recylerUserFans.getContext(), layoutManager.getOrientation());
        recylerUserFans.addItemDecoration(mDividerItemDecorationCate);*/

    searchkey.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String key = searchkey.getText().toString();
        ArrayList<GetFanList.UserData> friendlistnew = getsearchFriendlist(key);
        fanlist.clear();
        if (friendlistnew != null && !friendlistnew.isEmpty()) {
          fanlist.addAll(friendlistnew);
        }
        if (fanlist.size() > 0) {
          recylerUserFans.setVisibility(View.VISIBLE);
          textNofans.setVisibility(View.GONE);
          setAdapter();
        } else {
          recylerUserFans.setVisibility(View.GONE);
          textNofans.setVisibility(View.VISIBLE);
          textNofans.setText("No result found.");
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        simpleSwipeRefreshLayout.setRefreshing(true);
        pageNumber = 1;
        if (flag.equalsIgnoreCase("1")) {
          getFanList();
        } else {
          getMyfans();
        }
      }
    });

    view.findViewById(R.id.imageback).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getActivity().finish();
      }
    });
    return view;
  }

  private void setAdapter() {
    if (this.fanlist == null) {
      return;
    }
    if (adapter == null) {
      adapter = new FanListAdapter(this.fanlist, getContext(), this.flag, this.otheruser_id,
          this.recylerUserFans);
      recylerUserFans.setAdapter(adapter);
      adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
          if (apiCallBlocler == true) {
            fanlist.add(null);
            adapter.notifyItemInserted(fanlist.size() - 1);
            apiCallBlocler = false;
            pageNumber++;
            if (flag.equalsIgnoreCase("1")) {
              getFanList();
            } else {
              getMyfans();
            }
          }
        }
      });
    } else {
      adapter.notifyDataSetChanged();
      adapter.setLoaded();
    }
  }

  private ArrayList<GetFanList.UserData> getsearchFriendlist(String searchStr) {
    ArrayList<GetFanList.UserData> friendlistnew = new ArrayList<>();
    if (Commons.staticSupporterslist != null) {
      if (searchStr.length() == 0) {
        friendlistnew.addAll(Commons.staticSupporterslist);
        return friendlistnew;
      }
      searchStr = searchStr.toLowerCase();
      for (int i = 0; i < Commons.staticSupporterslist.size(); i++) {
        String name =
            Commons.staticSupporterslist.get(i).getFirst_name() + " " + Commons.staticSupporterslist
                .get(i)
                .getLast_name();
        if (name.toLowerCase().contains(searchStr)) {
          friendlistnew.add(Commons.staticSupporterslist.get(i));
        }
        simpleSwipeRefreshLayout.setRefreshing(false);
      }
    }
    return friendlistnew;
  }

  private void bundle_data() {
    Bundle bundle = getArguments();
    position = bundle.getString("position");
    flag = bundle.getString("flag");
    tittle = bundle.getString("tittle");
    otheruser_id = bundle.getString("user_id");
  }

  private void getMyfans() {
    String token =
        SharedPreferenceSingleton.getInstance()
            .getStringPreference(getContext(), RegistrationConstants.TOKEN);
    Apimethods methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    Call<GetFanList> call = methods.getMyFanList(otheruser_id, pageNumber, pageLimint, token);

    call.enqueue(new Callback<GetFanList>() {
      @Override
      public void onResponse(Call<GetFanList> call, Response<GetFanList> response) {
        int statuscode = response.code();
        apiCallBlocler = true;
        if (pageNumber > 1 && fanlist != null && fanlist.isEmpty() == false) {
          fanlist.remove(fanlist.size() - 1);
          adapter.notifyItemRemoved(fanlist.size());
        }
        if (pageNumber == 1) {
          fanlist.clear();
        }
        simpleSwipeRefreshLayout.setRefreshing(false);
        if (statuscode == 200) {
          layoutProgress.setVisibility(View.GONE);
          GetFanList getresponsemodel = response.body();
          ArrayList<GetFanList.UserData> dataList =
              (ArrayList<GetFanList.UserData>) getresponsemodel.getData();
          if (dataList == null || dataList.isEmpty()) {
            apiCallBlocler = false;
          } else {
            fanlist.addAll(dataList);
            // sort the list
                        /*Collections.sort(fanlist, new Comparator<GetFanList.UserData>() {
                            @Override
                            public int compare(GetFanList.UserData obj1, GetFanList.UserData obj2) {
                                if (obj1.getFirst_name() != null && obj2.getFirst_name() != null) {
                                    return obj1.getFirst_name().toLowerCase().compareTo(obj2.getFirst_name().toLowerCase());
                                }
                                return 0;
                            }
                        });*/
          }
          Commons.staticSupporterslist = new ArrayList<GetFanList.UserData>();
          if (fanlist != null && !fanlist.isEmpty()) {
            Commons.staticSupporterslist.addAll(fanlist);
          }
          if (fanlist.size() > 0) {
            fanList_linear.setVisibility(View.VISIBLE);
            textNofans.setVisibility(View.GONE);
          } else {
            fanList_linear.setVisibility(View.GONE);
            textNofans.setVisibility(View.VISIBLE);
            textNofans.setText("You are not supporting anyone.");
          }
          setAdapter();
        }
        if (statuscode == 401) {
          Log.d("url", "url=" + call.request().url().toString());
        }
      }

      @Override
      public void onFailure(Call<GetFanList> call, Throwable t) {

      }
    });
  }

  private void getFanList() {
    String token =
        SharedPreferenceSingleton.getInstance()
            .getStringPreference(getContext(), RegistrationConstants.TOKEN);
    Apimethods methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    Call<GetFanList> call =
        methods.getUserFanList(otheruser_id, this.pageNumber, pageLimint, token);
    call.enqueue(new Callback<GetFanList>() {
      @Override
      public void onResponse(Call<GetFanList> call, Response<GetFanList> response) {
        int statuscode = response.code();
        apiCallBlocler = true;
        if (pageNumber > 1 && fanlist != null && fanlist.isEmpty() == false) {
          fanlist.remove(fanlist.size() - 1);
          adapter.notifyItemRemoved(fanlist.size());
        }
        if (pageNumber == 1) {
          fanlist.clear();
        }
        simpleSwipeRefreshLayout.setRefreshing(false);
        if (statuscode == 200) {
          layoutProgress.setVisibility(View.GONE);
          GetFanList getresponsemodel = response.body();
          ArrayList<GetFanList.UserData> dataList =
              (ArrayList<GetFanList.UserData>) getresponsemodel.getData();
          if (dataList == null || dataList.isEmpty()) {
            apiCallBlocler = false;
          } else {
            fanlist.addAll(dataList);
            // sort the list
                        /*Collections.sort(fanlist, new Comparator<GetFanList.UserData>() {
                            @Override
                            public int compare(GetFanList.UserData obj1, GetFanList.UserData obj2) {
                                if (obj1.getFirst_name() != null && obj2.getFirst_name() != null) {
                                    return obj1.getFirst_name().toLowerCase().compareTo(obj2.getFirst_name().toLowerCase());
                                }
                                return 0;
                            }
                        });*/
          }
          Commons.staticSupporterslist = new ArrayList<GetFanList.UserData>();
          if (fanlist != null && !fanlist.isEmpty()) {
            Commons.staticSupporterslist.addAll(fanlist);
          }
          if (fanlist.size() > 0) {
            fanList_linear.setVisibility(View.VISIBLE);
            textNofans.setVisibility(View.GONE);
          } else {
            fanList_linear.setVisibility(View.GONE);
            textNofans.setVisibility(View.VISIBLE);
            textNofans.setText("You don't have any supporters.");
          }
          setAdapter();
        }
        if (statuscode == 401) {

        }
      }

      @Override
      public void onFailure(Call<GetFanList> call, Throwable t) {

      }
    });
  }
}

