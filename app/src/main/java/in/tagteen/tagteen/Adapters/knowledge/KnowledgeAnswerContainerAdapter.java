package in.tagteen.tagteen.Adapters.knowledge;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswer;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.viewholder.AnswerViewHolder;

public class KnowledgeAnswerContainerAdapter extends RecyclerView.Adapter<AnswerViewHolder> {
    private Context context;
    private List<KnowledgeAnswer> answersList;

    public KnowledgeAnswerContainerAdapter(Context context, List<KnowledgeAnswer> answersList) {
        this.context = context;
        this.answersList = answersList;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(
                R.layout.view_holder_knowledge_answer, parent, false);
        AnswerViewHolder holder = new AnswerViewHolder(view, this.context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        KnowledgeAnswer answer = this.answersList.get(position);
        Uri videoUri = Uri.parse(answer.getVideoUrl());
        holder.bind(answer, videoUri);

        if (answer.getVideoThumbnail() != null &&
                !answer.getVideoThumbnail().equals(Constants.DUMMY_THUMBNAIL_URL)) {
            Utils.loadImageUsingGlide(
                    this.context, holder.getPosterView(), answer.getVideoThumbnail());
        } else {
            Utils.loadImageUsingGlide(
                    this.context, holder.getPosterView(), answer.getVideoUrl());
        }
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }
}
