package io.agora.openlive.chatlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import io.agora.openlive.R;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
  private ArrayList<ChatModel> chatlist;

  public ChatAdapter() {
    this.chatlist = new ArrayList<>();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View listItem = layoutInflater.inflate(R.layout.chat_item, parent, false);
    return new ViewHolder(listItem);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final ChatModel model = chatlist.get(position);
    holder.userName.setText(model.getUserName().concat(" : "));
    holder.userComment.setText(model.getComment());
  }

  @Override
  public int getItemCount() {
    return chatlist.size();
  }

  public void addComment(ChatModel data) {
    chatlist.add(data);
    notifyDataSetChanged();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView userName, userComment;

    public ViewHolder(View itemView) {
      super(itemView);
      this.userName = (TextView) itemView.findViewById(R.id.userName);
      this.userComment = (TextView) itemView.findViewById(R.id.userComment);
    }
  }
}
