package in.tagteen.tagteen.Adapters.knowledge;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;
import in.tagteen.tagteen.KnowledgeQuestionPreviewActivity;
import in.tagteen.tagteen.Model.knowledge.KnowledgeQuestions;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;

public class MyQuestionsAdapter extends RecyclerView.Adapter<MyQuestionsAdapter.ItemRowHolder> {
    private Context context;
    private List<KnowledgeQuestions.Question> questions;
    private OnPostDeleteListener onPostDeleteListener;

    public MyQuestionsAdapter(Context context, List<KnowledgeQuestions.Question> questions) {
        this.context = context;
        this.questions = questions;
    }

    public void setOnPostDeleteListener(OnPostDeleteListener onPostDeleteListener) {
        this.onPostDeleteListener = onPostDeleteListener;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_question_row, parent, false);
        return new ItemRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, int position) {
        final KnowledgeQuestions.Question question = this.questions.get(position);
        if (question == null) {
            return;
        }

        String questionTitle = question.getTitle();
        if (questionTitle != null && questionTitle.endsWith("?") == false) {
            questionTitle += "?";
        }
        holder.lblQuestionTitle.setText(Utils.getHtmlEscapedString(questionTitle));
        holder.lblViewCount.setText("" + question.getViewCount());

        if (question.getAnswers() == null || question.getAnswers().isEmpty()) {
            holder.lblNoAnswers.setVisibility(View.VISIBLE);
            holder.recyclerAnswers.setVisibility(View.GONE);
        } else {
            holder.lblNoAnswers.setVisibility(View.GONE);
            holder.recyclerAnswers.setVisibility(View.VISIBLE);

            KnowledgeAnswersAdapter adapter =
                    new KnowledgeAnswersAdapter(this.context, question.getAnswers(), question);
            holder.recyclerAnswers.setAdapter(adapter);
        }

        // actions
        holder.lblQuestionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewQuestion(question);
            }
        });
        holder.imgVideoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewQuestion(question);
            }
        });
        holder.layoutDeleteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPostDeleteListener != null) {
                    onPostDeleteListener.onDelete(question.getQuestionId());
                }
            }
        });
    }

    private void previewQuestion(KnowledgeQuestions.Question question) {
        Intent intent = new Intent(this.context, KnowledgeQuestionPreviewActivity.class);
        intent.putExtra(Constants.SHOWROOM_POST_DATA, question);
        this.context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return this.questions.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView lblQuestionTitle;
        private TextView lblViewCount;
        private ImageView imgVideoIcon;
        private TextView lblNoAnswers;
        private RecyclerView recyclerAnswers;
        private LinearLayout layoutDeleteQuestion;

        public ItemRowHolder(View view) {
            super(view);

            this.lblQuestionTitle = view.findViewById(R.id.lblQuestionTitle);
            this.lblViewCount = view.findViewById(R.id.lblViewsCount);
            this.imgVideoIcon = view.findViewById(R.id.imgVideoIcon);
            this.lblNoAnswers = view.findViewById(R.id.lblNoAnswers);
            this.recyclerAnswers = view.findViewById(R.id.recyclerAnswers);
            this.layoutDeleteQuestion = view.findViewById(R.id.layoutDeleteQuestion);

            this.recyclerAnswers.setHasFixedSize(true);
            this.recyclerAnswers.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
    }
}
