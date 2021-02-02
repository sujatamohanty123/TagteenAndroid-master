package in.tagteen.tagteen.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.regex.Pattern;

import in.tagteen.tagteen.Adapters.FriendsAdapter;
import in.tagteen.tagteen.ConnectivityReceiver;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.utils.TagteenApplication;
import in.tagteen.tagteen.utils.Commons;


public class FriendsAllFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    public  List<GetAllUserFriendlist.FriendsUserList> friendlist = new ArrayList<>();
    public  List<GetAllUserFriendlist.FriendsUserList> bfflist = new ArrayList<>();
    private RecyclerView recyclerView;
    private FriendsAdapter mAdapter;
    int flag=1;
    LinearLayout search;
    TextView nofriensdslay;
    EditText searchkey;
    SwipeRefreshLayout simpleSwipeRefreshLayout;
    TextView no_internet;
    LinearLayout buzz_main_container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_friends__all, container, false);

        buzz_main_container=(LinearLayout) view.findViewById(R.id.buzz_main_container);
        no_internet=(TextView) view.findViewById(R.id.no_internet);
        checkConnection();

        simpleSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        search=(LinearLayout) view.findViewById(R.id.search);
        nofriensdslay=(TextView) view.findViewById(R.id.nofriendslay);
        searchkey=(EditText) view.findViewById(R.id.searchtext);
        search.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Bundle bundle = getArguments();
        friendlist= (List<GetAllUserFriendlist.FriendsUserList>) bundle.getSerializable("allfriends_list");
        bfflist= (List<GetAllUserFriendlist.FriendsUserList>) bundle.getSerializable("bfflist");
        mAdapter= new FriendsAdapter(getContext(),friendlist,flag);
        recyclerView.setAdapter(mAdapter);

        if((friendlist!=null)&&friendlist.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            nofriensdslay.setVisibility(View.GONE);
            Commons.staticfriendlist= (ArrayList<GetAllUserFriendlist.FriendsUserList>) friendlist;
            mAdapter= new FriendsAdapter(getContext(),friendlist,flag);
            recyclerView.setAdapter(mAdapter);
        }else{
            recyclerView.setVisibility(View.GONE);
            nofriensdslay.setVisibility(View.VISIBLE);
            nofriensdslay.setText("No friends to show");
        }


        searchkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key=searchkey.getText().toString();

                if(key.length()>0){
                    friendlist=getsearchFriendlist(key);
                    if(friendlist.size()>0){
                        recyclerView.setVisibility(View.VISIBLE);
                        nofriensdslay.setVisibility(View.GONE);
                        mAdapter= new FriendsAdapter(getContext(),friendlist,flag);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else {
                        recyclerView.setVisibility(View.GONE);
                        nofriensdslay.setVisibility(View.VISIBLE);
                        nofriensdslay.setText("No result found.");
                    }

                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    nofriensdslay.setVisibility(View.GONE);
                    friendlist= Commons.staticfriendlist;
                    mAdapter= new FriendsAdapter(getContext(),friendlist,flag);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
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
                friendlist=getsearchFriendlist(searchkey.getText().toString());
            }
        });
        return view;
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

    private ArrayList<GetAllUserFriendlist.FriendsUserList> getsearchFriendlist(String s) {

        ArrayList<GetAllUserFriendlist.FriendsUserList> friendlistnew = new ArrayList<>();
        System.out.print("restcharacter="+s);
        if(!(Commons.staticfriendlist==null)){
            if (!Commons.staticfriendlist.isEmpty()) {
                Collections.sort(Commons.staticfriendlist, new Comparator<GetAllUserFriendlist.FriendsUserList>() {
                    @Override
                    public int compare(GetAllUserFriendlist.FriendsUserList obj1, GetAllUserFriendlist.FriendsUserList obj2) {
                        if (obj1.getFirstName() != null && obj2.getFirstName() != null) {
                            return obj1.getFirstName().toLowerCase().compareTo(obj2.getFirstName().toLowerCase());
                        }
                        return 0;
                    }
                });
            }
            for(int i = 0; i< Commons.staticfriendlist.size(); i++) {
                String name= Commons.staticfriendlist.get(i).getFirstName()+" "+ Commons.staticfriendlist.get(i).getLastName();
                if( (Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(Commons.staticfriendlist.get(i).getFirstName()).find())||(Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(Commons.staticfriendlist.get(i).getLastName()).find())||(Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(name).find())){
                    friendlistnew.add(Commons.staticfriendlist.get(i));

                }
                simpleSwipeRefreshLayout.setRefreshing(false);
            }

        }
        return friendlistnew;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    public void onResume() {
        super.onResume();
        TagteenApplication.getInstance().setConnectivityListener(this);
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
