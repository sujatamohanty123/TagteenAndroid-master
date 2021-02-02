package in.tagteen.tagteen.Adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import in.tagteen.tagteen.Fragments.GroupListFragment;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;


public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> implements Filterable{

    private SectionDataModel sectionDataModel;
    private ArrayList<SectionDataModel> studentNameList = new ArrayList<>();
    private ArrayList<SectionDataModel> studentFilteredList;
    private GroupListFragment selectConcernStudentActivity;

    public GroupListAdapter(GroupListFragment selectConcernStudentActivity) {

        this.selectConcernStudentActivity = selectConcernStudentActivity;
        for (int i = 1; i <= 10; i++) {

            sectionDataModel = new SectionDataModel();
            sectionDataModel.setHeaderTitle("Student " + i);
            studentNameList.add(sectionDataModel);

        }
        studentFilteredList = studentNameList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.concern_student_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        if (studentFilteredList.get(position).isSelected()) {
            holder.imgSelected.setVisibility(View.VISIBLE);
        } else {
            holder.imgSelected.setVisibility(View.GONE);
        }
        holder.txtStudentName.setText(studentFilteredList.get(position).getHeaderTitle());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (studentFilteredList.get(position).isSelected()) {

                    selectConcernStudentActivity.removeStudentFromVerticalList(studentFilteredList.get(position).getHeaderTitle());
                    studentFilteredList.get(position).setSelected(false);
                    notifyDataSetChanged();
                } else {

                    getFilter().filter("");
                    selectConcernStudentActivity.addStudent(studentFilteredList.get(position).getHeaderTitle());
                    studentFilteredList.get(position).setSelected(true);
                    notifyDataSetChanged();
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return studentFilteredList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtStudentName;
        ImageView imgSelected;
        LinearLayout layout;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            txtStudentName = (TextView) itemView.findViewById(R.id.student_name);
            imgSelected = (ImageView) itemView.findViewById(R.id.imgSelected);
            view = itemView;

        }
    }

    public void refreshAdapterAfterRemovingItem(String name) {

        Iterator<SectionDataModel> it = studentFilteredList.iterator();
        while (it.hasNext()) {
            SectionDataModel data = it.next();
            if (data.getHeaderTitle().equals(name)) {
                data.setSelected(false);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void refreshAdapterAfterAddingItem(ArrayList<SectionDataModel> studentDummie) {

        for (int i = 0; i < studentDummie.size(); i++) {
            Iterator<SectionDataModel> it = studentFilteredList.iterator();
            while (it.hasNext()) {
                SectionDataModel data = it.next();
                if (data.getHeaderTitle().equals(studentDummie.get(i).getHeaderTitle())) {
                    data.setSelected(true);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (constraint.toString().length() > 0) {
                    List<SectionDataModel> founded = new ArrayList<>();
                    for (SectionDataModel item : studentNameList) {
                        if (item.getHeaderTitle().toLowerCase().contains(constraint)) {
                            founded.add(item);
                        }
                    }

                    result.values = founded;
                    result.count = founded.size();

                } else {
                    result.values = studentNameList;
                    result.count = studentNameList.size();
                }
                return result;


            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                clear();
//                for (StudentDummy item : (List<StudentDummy>) results.values) {
//                    add(item);
//                }

                studentFilteredList = (ArrayList<SectionDataModel>) results.values;

                if (studentFilteredList.size() == 0) {

//                    selectConcernStudentActivity.setErrorMessage("No result found for '"+constraint+"'");
                } else {
//                    selectConcernStudentActivity.setErrorMessage("");
                }
                notifyDataSetChanged();

            }

        };

    }

}
