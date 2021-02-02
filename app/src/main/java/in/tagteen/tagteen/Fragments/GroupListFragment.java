package in.tagteen.tagteen.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import in.tagteen.tagteen.Adapters.GroupListAdapter;
import in.tagteen.tagteen.Adapters.RecyclerViewGroupAdapter;
import in.tagteen.tagteen.Adapters.SelectedGroupListAdapter;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.Model.SingleItemModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.CustomLayoutManager;


public class GroupListFragment extends Fragment {

//    private Toolbar toolbar;
    ArrayList<SectionDataModel> allSampleData;
    LinearLayout linearLayout;
    Dialog dialog;
    Snackbar snackbar;
    CoordinatorLayout chatlist;
    GroupListAdapter concernStudentListAdapter;
    SelectedGroupListAdapter concernedSelectedStudentListAdapter;
    LinearLayout layoutSelectedStudent;
    private TextView txtErrorMessage;
    private CustomLayoutManager linearLayoutManager;
    SectionDataModel sectionDataModel;
    private RecyclerView selectedGroupRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_chat_list_view, container, false);


//        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        allSampleData = new ArrayList<SectionDataModel>();

//        createDummyData();

        selectedGroupRecyclerView = (RecyclerView) rootView.findViewById(R.id.selected_group_list);
        RecyclerView groupRecyclerView = (RecyclerView) rootView.findViewById(R.id.group_list);
        txtErrorMessage = (TextView) rootView.findViewById(R.id.txt_error_message);

        linearLayoutManager = new CustomLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL);

        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        selectedGroupRecyclerView.setLayoutManager(linearLayoutManager);

        layoutSelectedStudent = (LinearLayout) rootView.findViewById(R.id.layout_selected_student);


        RecyclerViewGroupAdapter adapter = new RecyclerViewGroupAdapter(getActivity(), allSampleData);

        concernStudentListAdapter = new GroupListAdapter(this);
        concernedSelectedStudentListAdapter = new SelectedGroupListAdapter(this, allSampleData);

        groupRecyclerView.setAdapter(concernStudentListAdapter);
        selectedGroupRecyclerView.setAdapter(concernedSelectedStudentListAdapter);

        return rootView;
    }

    public void addStudent(String name) {

//        if (searchView.getQuery() != null && searchView.getQuery().toString().length() > 0)
//            searchView.setQuery("", false);

        sectionDataModel = new SectionDataModel();
        sectionDataModel.setHeaderTitle(name);
        allSampleData.add(sectionDataModel);

        layoutSelectedStudent.setVisibility(View.VISIBLE);
//        fab.setVisibility(View.VISIBLE);


        concernedSelectedStudentListAdapter.notifyDataSetChanged();
        selectedGroupRecyclerView.smoothScrollToPosition(concernedSelectedStudentListAdapter.getItemCount() - 1);

    }


    public void removeStudent(String name) {

        int pos = -1;
        concernStudentListAdapter.refreshAdapterAfterRemovingItem(name);
        Iterator<SectionDataModel> it = allSampleData.iterator();
        while (it.hasNext()) {
            pos++;
            SectionDataModel data = it.next();
            if (data.getHeaderTitle().equals(name)) {

                it.remove();
                break;
            }
        }
        concernedSelectedStudentListAdapter.decreasePos();
//        smoothScroller.setTargetPosition(pos);
//        linearLayoutManager.startSmoothScroll(smoothScroller);

        Handler handler = new Handler();

        final int finalPos = pos;
        final Runnable r = new Runnable() {
            public void run() {
                concernedSelectedStudentListAdapter.notifyDataSetChanged();
                concernedSelectedStudentListAdapter.notifyItemRemoved(finalPos);
                concernedSelectedStudentListAdapter.notifyItemRangeRemoved(finalPos, allSampleData.size());
            }
        };

        handler.post(r);


        if (allSampleData.size() == 0) {
            layoutSelectedStudent.setVisibility(View.GONE);
//            fab.setVisibility(View.GONE);
        }
    }

    public void removeStudentFromVerticalList(String name) {

        int pos = -1;
        concernStudentListAdapter.refreshAdapterAfterRemovingItem(name);
        for (int i = 0; i < allSampleData.size(); i++) {

            if (name.equalsIgnoreCase(allSampleData.get(i).getHeaderTitle())) {
                pos = i;
                break;
            }
        }
        concernedSelectedStudentListAdapter.decreasePos(pos, allSampleData.size());
    }

    public void removeSelected(String name) {

        Iterator<SectionDataModel> it = allSampleData.iterator();
        while (it.hasNext()) {
            SectionDataModel data = it.next();
            if (data.getHeaderTitle().equals(name)) {

                it.remove();
                break;
            }
        }


        concernedSelectedStudentListAdapter.decreasePos();
        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                concernedSelectedStudentListAdapter.notifyDataSetChanged();
            }
        };

        handler.post(r);


        if (allSampleData.size() == 0) {
            layoutSelectedStudent.setVisibility(View.GONE);
//            fab.setVisibility(View.GONE);
        }
    }

    public void createDummyData() {
        for (int i = 1; i <= 10; i++) {

            SectionDataModel dm = new SectionDataModel();
            dm.setHeaderTitle("Section " + i);
            ArrayList<SingleItemModel> singleItem = new ArrayList<SingleItemModel>();
            for (int j = 0; j <= 5; j++) {
                singleItem.add(new SingleItemModel("Item " + j, "URL " + j));
            }

            dm.setAllItemsInSection(singleItem);
            allSampleData.add(dm);

        }
    }


    public void beginSearch(String query) {
        concernStudentListAdapter.getFilter().filter(query);
    }
}