package in.tagteen.tagteen.Adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.tagteen.tagteen.Fragments.GroupListFragment;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;

public class SelectedGroupListAdapter extends RecyclerView.Adapter<SelectedGroupListAdapter.ViewHolder> {

    private ArrayList<SectionDataModel> studentNameList = new ArrayList<>();
    private GroupListFragment selectConcernStudentActivity;
    private int lastPosition = -1;
    private int selectedPositionFromActivity = -1;
    private int totalCount = 0;
    SectionDataModel sectionDataModel;

    public SelectedGroupListAdapter(GroupListFragment selectConcernStudentActivity, ArrayList<SectionDataModel> studentNameList) {

        this.selectConcernStudentActivity = selectConcernStudentActivity;

        this.studentNameList = studentNameList;
    }

    @Override
    public SelectedGroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_group_row, parent, false);
        return new SelectedGroupListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SelectedGroupListAdapter.ViewHolder holder, final int position) {

        if (totalCount >= 1) {

            if (position == selectedPositionFromActivity) {
                setAnimationFadeOut1(holder.layout, selectedPositionFromActivity);
                totalCount = 0;
            }
        }

        if (position < studentNameList.size())
            holder.txtStudentName.setText(studentNameList.get(position).getHeaderTitle());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (studentNameList.size() > 1) {

                    setAnimationFadeOut(view, position);
                } else if (studentNameList.size() == 1) {
                    selectConcernStudentActivity.removeStudent(studentNameList.get(position).getHeaderTitle());
                }

            }
        });

        setAnimationFadeIn(holder.view, position);
    }

    @Override
    public int getItemCount() {
        return studentNameList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtStudentName;
        LinearLayout layout;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);

            txtStudentName = (TextView) itemView.findViewById(R.id.student_name);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            view = itemView;

        }
    }

    private void setAnimationFadeIn(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(300);//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    private void setAnimationFadeOut(View viewToAnimate, final int position) {

        ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(300);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                selectConcernStudentActivity.removeStudent(studentNameList.get(position).getHeaderTitle());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewToAnimate.startAnimation(anim);


    }

    private void setAnimationFadeOut1(View viewToAnimate, final int pos) {

        if (totalCount == 1) {


            selectConcernStudentActivity.removeSelected(studentNameList.get(pos).getHeaderTitle());
        } else {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(300);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    selectConcernStudentActivity.removeSelected(studentNameList.get(pos).getHeaderTitle());
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            viewToAnimate.startAnimation(anim);
        }

    }

    public void decreasePos(int pos, int totalCount) {

        this.totalCount = totalCount;
        selectedPositionFromActivity = pos;
//        lastPosition = lastPosition - 1;

        notifyDataSetChanged();
//        notifyItemRemoved(pos);
//        notifyItemRangeRemoved(pos, studentNameList.size());
    }

    public void decreasePos() {

        lastPosition = lastPosition - 1;
    }

}