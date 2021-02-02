package in.tagteen.tagteen.Fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.tagteen.tagteen.Adapters.FriendsAdapter;
import in.tagteen.tagteen.ConnectivityReceiver;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.utils.TagteenApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFrament extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    private List<GetAllUserFriendlist.FriendsUserList> requestfriendlist = new ArrayList<>();
    private List<GetAllUserFriendlist.FriendsUserList> friendlist = new ArrayList<>();
    private RecyclerView recyclerView;
    private FriendsAdapter mAdapter;
    private TextView nofriensdslay;
    private int flag=6;
    private TextView no_internet;
    private LinearLayout buzz_main_container;

    public RequestFrament() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_request_frament, container, false);

        buzz_main_container=(LinearLayout) view.findViewById(R.id.buzz_main_container);
        no_internet=(TextView) view.findViewById(R.id.no_internet);
        checkConnection();

        Bundle bundle=getArguments();
        requestfriendlist= (List<GetAllUserFriendlist.FriendsUserList>) bundle.getSerializable("requestlist");
        //friendlist= (List<GetAllUserFriendlist.FriendsUserList>) bundle.getSerializable("allfriends_list");

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        nofriensdslay=(TextView) view.findViewById(R.id.nofriendslay);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if((requestfriendlist!=null)&&requestfriendlist.size()>0){
            Collections.sort(requestfriendlist, new Comparator<GetAllUserFriendlist.FriendsUserList>() {
                @Override
                public int compare(GetAllUserFriendlist.FriendsUserList obj1, GetAllUserFriendlist.FriendsUserList obj2) {
                    if (obj1.getFirstName() != null && obj2.getFirstName() != null) {
                        return obj1.getFirstName().toLowerCase().compareTo(obj2.getFirstName().toLowerCase());
                    }
                    return 0;
                }
            });
            recyclerView.setVisibility(View.VISIBLE);
            nofriensdslay.setVisibility(View.GONE);
            mAdapter= new FriendsAdapter(getContext(),requestfriendlist,flag);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }else{
            recyclerView.setVisibility(View.GONE);
            nofriensdslay.setVisibility(View.VISIBLE);
            nofriensdslay.setText("You have no friend requests pending.");
        }
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
