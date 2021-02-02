package in.tagteen.tagteen.Fragments.teenfeed;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Constants;

/**
 * Created by lovekushvishwakarma on 29/10/17.
 */

public class CategoryFragment extends Fragment implements AsyncResponse {

    ImageView imageback;
    RecyclerView recycler_view;
    private ArrayList<CategoryBean> categoryList;
    private CategoryAdapter adapter;
    TextView textCatSelct;

    public CategoryFragment(TextView textCatSelct) {
        this.textCatSelct = textCatSelct;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageback = (ImageView) view.findViewById(R.id.imageback);
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        categoryList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(linearLayoutManager);
        DividerItemDecoration mDividerItemDecorationCate = new DividerItemDecoration(recycler_view.getContext(), linearLayoutManager.getOrientation());
        recycler_view.addItemDecoration(mDividerItemDecorationCate);

        adapter = new CategoryAdapter(getActivity(), categoryList, textCatSelct,CategoryFragment.this);
        recycler_view.setAdapter(adapter);
        this.getAllCategories();

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
    }


    private void getAllCategories() {
        AsyncWorker mWorker = new AsyncWorker(getActivity());
        mWorker.delegate = this;
        mWorker.delegate = CategoryFragment.this;
        JSONObject BroadcastObject = new JSONObject();
        mWorker.execute(ServerConnector.REQUEST_GET_ALL_CATEGORIES, BroadcastObject.toString(), RequestConstants.GET_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_GET_CATEGORIES);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        try {
            JSONObject data = new JSONObject(output);
            if (data.getBoolean("success")) {
                JSONArray dataArr = data.getJSONArray("data");
                categoryList.add(new CategoryBean(0, "All"));
                for (int i = 0; i < dataArr.length(); i++) {
                    JSONObject object = dataArr.getJSONObject(i);
                    String categoryName = object.getString(Constants.CATEGORY_NAME_PARAM);
                    int categoryId = object.getInt(Constants.CATEGORY_ID_PARAM);
                    categoryList.add(new CategoryBean(categoryId, categoryName));
                }
                adapter.notifyDataSetChanged();
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(output);
    }

    public void closeFragment(){
        getFragmentManager().popBackStack();
    }
}
