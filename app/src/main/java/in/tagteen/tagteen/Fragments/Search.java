package in.tagteen.tagteen.Fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.tagteen.tagteen.Adapters.FriendsAdapter;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.Model.SearchModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search extends Fragment {

    private List<GetAllUserFriendlist.FriendsUserList> friendlist = new ArrayList<>();
    private RecyclerView recyclerView;
    private FriendsAdapter mAdapter;
    int flag=2;
    LinearLayout search;
    TextView nofriendslay;
    EditText searchkey;
    public Search() {
        // Required empty public constructor
    }
    SwipeRefreshLayout simpleSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        view.clearFocus();

        simpleSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        search=(LinearLayout) view.findViewById(R.id.search);
        nofriendslay=(TextView) view.findViewById(R.id.nofriendslay);
        searchkey=(EditText) view.findViewById(R.id.searchtext);
        searchkey.setCursorVisible(true);
        searchkey.requestFocus();
        search.setVisibility(View.VISIBLE);

        nofriendslay.setVisibility(View.VISIBLE);
        nofriendslay.setText("Search and Connect with new friends");

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        searchkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String stext=searchkey.getText().toString();
                if(stext.length()>0){
                    searchkey.requestFocus();
                    apicall(stext);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    nofriendslay.setVisibility(View.VISIBLE);
                    nofriendslay.setText("Search and Connect with new friends");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nofriendslay.setVisibility(View.VISIBLE);
                nofriendslay.setText("Search and Connect with new friends");
                simpleSwipeRefreshLayout.setRefreshing(true);
                apicall(searchkey.getText().toString());
            }
        });

        return view;
    }

    private void apicall(String s) {
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        String token= SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Call<SearchModel> call =methods.getsearchlist(s,token);
        Log.d("url","url="+call.request().url().toString());
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                simpleSwipeRefreshLayout.setRefreshing(false);
                if(response.code()==200){
                    SearchModel searchmodel=response.body();
                    if(searchmodel.getSuccess()==true){
                        friendlist = searchmodel.getDatalist();
                        if(friendlist.size() > 0){
                            Collections.sort(friendlist, new Comparator<GetAllUserFriendlist.FriendsUserList>() {
                                @Override
                                public int compare(GetAllUserFriendlist.FriendsUserList obj1, GetAllUserFriendlist.FriendsUserList obj2) {
                                    if (obj1.getFirstName() != null && obj2.getFirstName() != null) {
                                        return obj1.getFirstName().toLowerCase().compareTo(obj2.getFirstName().toLowerCase());
                                    }
                                    return 0;
                                }
                            });
                            recyclerView.setVisibility(View.VISIBLE);
                            nofriendslay.setVisibility(View.GONE);
                            mAdapter= new FriendsAdapter(getContext(),friendlist,flag);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();

                        }else {
                            recyclerView.setVisibility(View.GONE);
                            nofriendslay.setVisibility(View.VISIBLE);
                            nofriendslay.setText("No result found.");
                        }
                    }


                }else if(response.code()==401){
                    recyclerView.setVisibility(View.GONE);
                    nofriendslay.setVisibility(View.VISIBLE);
                    nofriendslay.setText("No result found.");
                }
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {

            }
        });

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //Write down your refresh code here, it will call every time user come to this fragment.
            //If you are using listview with custom adapter, just call notifyDataSetChanged().
        }
    }

}
