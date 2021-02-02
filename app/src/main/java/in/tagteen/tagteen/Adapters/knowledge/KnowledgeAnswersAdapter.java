package in.tagteen.tagteen.Adapters.knowledge;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tagteen.tagteen.KnowledgeAnswerPreviewActivity;
import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswer;
import in.tagteen.tagteen.Model.knowledge.KnowledgeQuestions;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;

public class KnowledgeAnswersAdapter extends RecyclerView.Adapter<KnowledgeAnswersAdapter.ItemRowHolder> {
    private Context context;
    private List<KnowledgeAnswer> answers;
    private KnowledgeQuestions.Question question;

    public KnowledgeAnswersAdapter(
            Context context,
            List<KnowledgeAnswer> answers,
            KnowledgeQuestions.Question question) {
        this.context = context;
        this.answers = answers;
        this.question = question;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.knowledge_answer_row, parent, false);
        return new ItemRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, int position) {
        KnowledgeAnswer answer = this.answers.get(position);
        if (answer == null) {
            return;
        }

        if (answer.getTitle() != null) {
            holder.lblDescription.setText(Utils.getHtmlEscapedString(answer.getTitle()));
        }
        holder.lblViewsCount.setText("" + answer.getViewCount());

        if (answer.getVideoThumbnail() != null) {
            if (answer.getVideoThumbnail().equals(Constants.DUMMY_THUMBNAIL_URL)) {
                Utils.loadImageUsingGlide(
                        this.context, holder.imgAnswerThumbnail, answer.getVideoUrl());
            } else {
                Utils.loadImageUsingGlide(
                        this.context, holder.imgAnswerThumbnail, answer.getVideoThumbnail());
            }
        } else {
            Utils.loadImageUsingGlide(
                    this.context, holder.imgAnswerThumbnail, answer.getVideoUrl());
        }

        holder.imgAnswerThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewAnswer(answer);
            }
        });
    }

    private void previewAnswer(KnowledgeAnswer answer) {
        Intent intent = new Intent(this.context, KnowledgeAnswerPreviewActivity.class);
        intent.putExtra(Constants.SHOWROOM_POST_DATA, answer);
        intent.putExtra(Constants.QUESTION_TITLE, this.question.getTitle());
        intent.putExtra(Constants.QUESTION_ID, this.question.getQuestionId());
        intent.putExtra(Constants.PROFILE_PIC_URL, this.question.getProfilePic());

        String userName = "";
        if (this.question.getFirstName() != null) {
            userName += this.question.getFirstName();
        }
        if (this.question.getLastName() != null) {
            userName += " " + this.question.getLastName();
        }
        intent.putExtra(Constants.POSTED_BY, userName);
        intent.putExtra(Constants.VIEW_COUNT, this.question.getViewCount());
        this.context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return this.answers.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private ImageView imgAnswerThumbnail;
        private TextView lblViewsCount;
        private TextView lblDescription;

        public ItemRowHolder(View view) {
            super(view);

            this.imgAnswerThumbnail = view.findViewById(R.id.imgAnswerThumbnail);
            this.lblViewsCount = view.findViewById(R.id.lblViewsCount);
            this.lblDescription = view.findViewById(R.id.lblDescription);
        }
    }
}
