package in.tagteen.tagteen.profile;

import android.content.Context;
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
import java.util.Collections;
import java.util.List;

import in.tagteen.tagteen.Adapters.Profile_photos_Adapter;
import in.tagteen.tagteen.Model.UserPhotosModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePhotosFragment extends /*FragmentPagerFragment*/Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerView_Adapter;
    private Context context;
    public List<String> imaglist = new ArrayList<>();
    private String other_user_id="";
    private boolean is_my_friend=false;
    private boolean myphotos_privacy=false;
    private String login_usr_id;
    private TextView nofriendslay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile__photos, container, false);
        context = getActivity().getApplicationContext();
        other_user_id = getArguments().getString("user_id");
        is_my_friend=getArguments().getBoolean("is_my_friend");
        myphotos_privacy=getArguments().getBoolean("myphotos_privacy");
        login_usr_id = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

        recyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        nofriendslay = (TextView) v.findViewById(R.id.nofriendslay);

        recyclerViewLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        if (login_usr_id.equalsIgnoreCase(other_user_id)) {
            prepareList();
        } else {
            if (is_my_friend && !myphotos_privacy) {
                recyclerView.setVisibility(View.VISIBLE);
                nofriendslay.setVisibility(View.GONE);
                prepareList();
            } else {
                recyclerView.setVisibility(View.GONE);
                nofriendslay.setVisibility(View.VISIBLE);
                nofriendslay.setText("Photos are hidden");
            }
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            this.prepareList();
        }
    }

    public void notifyPostDeleted(String postImageUrl) {
        if (postImageUrl != null && this.imaglist != null) {
            this.imaglist.remove(postImageUrl);
            if (recyclerView_Adapter != null) {
                recyclerView_Adapter.notifyDataSetChanged();
            }
        }
    }

    private void prepareList() {
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        String userid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        Call<UserPhotosModel> call=null;
        if (userid.equalsIgnoreCase(other_user_id)){
            call = methods.getUserPhotos_loginuser(other_user_id, 1, 100,token);
        } else {
            call = methods.getUserPhotos(other_user_id,userid,1, 100);
        }
        call.enqueue(new Callback<UserPhotosModel>() {
            @Override
            public void onResponse(Call<UserPhotosModel> call, Response<UserPhotosModel> response) {
                if (response.code() == 200) {
                    UserPhotosModel searchmodel = response.body();
                    if (searchmodel.getSuccess() == true) {
                        List<UserPhotosModel.Data> list = searchmodel.getData();
                        imaglist.clear();
                        if (list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                for (int j = 0; j < list.get(i).getImage().size(); j++) {
                                    if (!list.get(i).getImage().isEmpty()) {
                                        imaglist.add(searchmodel.getData().get(i).getImage().get(j).getUrl());
                                        recyclerView.setVisibility(View.VISIBLE);
                                        nofriendslay.setVisibility(View.GONE);
                                    }
                                }
                            }
                            if (imaglist != null && imaglist.isEmpty() == false) {
                                Collections.reverse(imaglist);
                            }
                            recyclerView_Adapter = new Profile_photos_Adapter(context, imaglist);
                            recyclerView.setAdapter(recyclerView_Adapter);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nofriendslay.setVisibility(View.VISIBLE);
                            if (login_usr_id.equalsIgnoreCase(other_user_id)) {
                                nofriendslay.setText("you have no photos to show");
                            } else {
                                nofriendslay.setText("No photos to show");
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserPhotosModel> call, Throwable t) {
                Log.d("ProfilePhotos","Failed : "+call.request().url().toString());
            }
        });
    }
}
