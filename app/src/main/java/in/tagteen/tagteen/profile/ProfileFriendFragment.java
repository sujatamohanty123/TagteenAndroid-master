package in.tagteen.tagteen.profile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.profile.adapter.ProfileFriendsListAdapter;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFriendFragment extends Fragment {
    private static final String TAG = "ProfileFriendFragment";

    private ProfileFriendsListAdapter adapter;
    private RecyclerView listView;
    private GetAllUserFriendlist.Data datalist = new GetAllUserFriendlist.Data();
    private ArrayList<GetAllUserFriendlist.FriendsUserList> friendlist = new ArrayList<>();
    private String other_user_id = "";
    private boolean myfriends_privacy = false;
    private boolean is_my_friend=false;
    private String login_usr_id;
    private TextView nofriendslay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  rootView = inflater.inflate(R.layout.list_view, container, false);
        listView = rootView.findViewById(R.id.listview_con);
        nofriendslay = (TextView) rootView.findViewById(R.id.nofriendslay);
        other_user_id = getArguments().getString("user_id");
        is_my_friend=getArguments().getBoolean("is_my_friend");
        myfriends_privacy=getArguments().getBoolean("myfriends_privacy");
        login_usr_id = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        if (login_usr_id.equalsIgnoreCase(other_user_id)){
            callApiFriendsList();
        } else {
            if (is_my_friend && !myfriends_privacy) {
                listView.setVisibility(View.VISIBLE);
                nofriendslay.setVisibility(View.GONE);
                callApiFriendsList();
            } else {
                listView.setVisibility(View.GONE);
                nofriendslay.setVisibility(View.VISIBLE);
                nofriendslay.setText("Friends are hidden");
            }
        }

        this.listView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetAllUserFriendlist.FriendsUserList friendListModel = friendlist.get(position);
                SharedPreferenceSingleton.getInstance().init(getActivity());

                Utils.gotoProfile(getActivity(), friendListModel.getId());
            }
        });*/
        return rootView;

    }

    private void callApiFriendsList() {
        SharedPreferenceSingleton.getInstance().init(getActivity());
        String token= SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);

        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<GetAllUserFriendlist> call =methods.getalluserfriendslist(other_user_id,token);

        call.enqueue(new Callback<GetAllUserFriendlist>() {
            @Override
            public void onResponse(Call<GetAllUserFriendlist> call, Response<GetAllUserFriendlist> response) {
                if(response.code()==200){
                    GetAllUserFriendlist getlistdata=response.body();
                    datalist=getlistdata.getData();

                    friendlist = (ArrayList<GetAllUserFriendlist.FriendsUserList>) datalist.getFriendsUserList();
                    if (friendlist.size()>0) {
                        listView.setVisibility(View.VISIBLE);
                        nofriendslay.setVisibility(View.GONE);
                        adapter = new ProfileFriendsListAdapter(getActivity(), friendlist);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        listView.setVisibility(View.GONE);
                        nofriendslay.setVisibility(View.VISIBLE);
                        if (login_usr_id.equalsIgnoreCase(other_user_id)) {
                            nofriendslay.setText("you have no friends to show.");
                        }else {
                            nofriendslay.setText("No Friends to show.");
                        }
                    }
                } else if(response.code()==401){

                }
            }

            @Override
            public void onFailure(Call<GetAllUserFriendlist> call, Throwable t) {
                Log.d(TAG,"Failed : " + call.request().url().toString());
            }
        });

    }

    /*@Override
    public boolean canScrollVertically(int direction) {
        try {
            return listView != null && listView.canScrollVertically(direction);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onFlingOver(int y, long duration) {
        try {
            if (listView != null) {
                listView.smoothScrollBy(y, (int) duration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
