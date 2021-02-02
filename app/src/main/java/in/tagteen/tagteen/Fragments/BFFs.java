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
public class BFFs extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{


    public BFFs() {
        // Required empty public constructor
    }

    public List<GetAllUserFriendlist.FriendsUserList> bfflist = new ArrayList<>();
    public List<GetAllUserFriendlist.FriendsUserList> friendlist = new ArrayList<>();
    public RecyclerView recyclerView;
    public FriendsAdapter mAdapter;
    int flag = 3;
    TextView no_internet;
    LinearLayout buzz_main_container;
    TextView nofriensdslay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bffs, container, false);

        buzz_main_container=(LinearLayout) view.findViewById(R.id.buzz_main_container);
        no_internet=(TextView) view.findViewById(R.id.no_internet);
        checkConnection();

        Bundle bundle = getArguments();
        bfflist = (List<GetAllUserFriendlist.FriendsUserList>) bundle.getSerializable("bfflist");
        if (bfflist != null && !bfflist.isEmpty()) {
            Collections.sort(bfflist, new Comparator<GetAllUserFriendlist.FriendsUserList>() {
                @Override
                public int compare(GetAllUserFriendlist.FriendsUserList obj1, GetAllUserFriendlist.FriendsUserList obj2) {
                    if (obj1.getFirstName() != null && obj2.getFirstName() != null) {
                        return obj1.getFirstName().toLowerCase().compareTo(obj2.getFirstName().toLowerCase());
                    }
                    return 0;
                }
            });
        }
        friendlist = (List<GetAllUserFriendlist.FriendsUserList>) bundle.getSerializable("allfriends_list");
        nofriensdslay=(TextView) view.findViewById(R.id.nofriendslay);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        if ((bfflist != null) && bfflist.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            nofriensdslay.setVisibility(View.GONE);
            mAdapter = new FriendsAdapter(getContext(), bfflist, flag);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            nofriensdslay.setVisibility(View.VISIBLE);
            nofriensdslay.setText("You have no BFFs");
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
