package in.tagteen.tagteen.chatting.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.chatting.R;
import in.tagteen.tagteen.chatting.emoji.Emoji;
import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.socket.SocketConstants;
import in.tagteen.tagteen.chatting.utils.ChatFactory;
import in.tagteen.tagteen.chatting.utils.ChatListDiffUtilsCallback;
import in.tagteen.tagteen.chatting.utils.UrlUtils;
import java.util.ArrayList;
import java.util.List;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder>
    implements Filterable {

  private SparseBooleanArray selectedItems;
  private List<Friend> chatList;
  private List<Friend> filteredChatList;
  private ChatItemFilter chatFilter;
  private Context context;
  private OnChatItemClickListener onItemClickListener;
  private OnChatItemLongClickListener onItemLongClickListener;

  public ChatsListAdapter(Context ctx) {
    this.context = ctx;
    chatList = new ArrayList<>();
    filteredChatList = new ArrayList<>();
    selectedItems = new SparseBooleanArray();
    chatFilter = new ChatItemFilter();
  }

  public void addAll(List<Friend> chatList) {
    this.chatList.clear();
    this.chatList.addAll(chatList);
    this.filteredChatList = chatList;
    notifyDataSetChanged();
  }

  @Override
  @NonNull
  public ChatsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chats, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
    Friend f = filteredChatList.get(position);
    holder.bind(f);
    holder.lyt_parent.setOnClickListener((View v) -> {
      if (onItemClickListener != null) {
        f.setUnSeenMessagesCount(0);
        onItemClickListener.onChatItemClick(v, f, position);
      }
    });

    holder.lyt_parent.setOnLongClickListener((View v) -> {
      if (onItemLongClickListener != null) {
        onItemLongClickListener.onChatItemLongClick(v, f, position);
      }
      return false;
    });

    holder.lyt_parent.setActivated(selectedItems.get(position, false));
  }

  @Override
  public int getItemCount() {
    return filteredChatList.size();
  }

  public void setOnChatItemClickListener(final OnChatItemClickListener mItemClickListener) {
    this.onItemClickListener = mItemClickListener;
  }

  @Override
  public Filter getFilter() {
    return chatFilter;
  }

  private class ChatItemFilter extends Filter {
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
      if (TextUtils.isEmpty(constraint)) {
        filteredChatList = chatList;
      } else {
        String query = constraint.toString().toLowerCase();
        final List<Friend> filtered = new ArrayList<>();
        for (Friend chat : chatList) {
          String str_title = chat.getName();
          if (str_title.toLowerCase().contains(query)) {
            filtered.add(chat);
          }
        }
        filteredChatList = filtered;
      }
      FilterResults results = new FilterResults();
      results.values = filteredChatList;
      results.count = filteredChatList.size();
      return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
      filteredChatList = (List<Friend>) results.values;
      notifyDataSetChanged();
    }
  }

  //tony14
  public boolean updateMessage(@NonNull Message message) {
    List<Friend> friends = new ArrayList<>(filteredChatList);
    int index = findFriend(friends, message.getClientId());
    if (index != -1) {
      Friend friendToUpdate = friends.get(index);
      friends.remove(index);
      friendToUpdate.setUnSeenMessagesCount(friendToUpdate.getUnSeenMessagesCount() + 1);
      friendToUpdate.setLastMessage(message);
      friends.add(0, friendToUpdate);
      changeDataSet(friends);
      return true;
    } else {
      index = findFriend(chatList, message.getClientId());
      if (index != -1) {
        Friend friendToUpdate = chatList.get(index);
        friendToUpdate.setUnSeenMessagesCount(friendToUpdate.getUnSeenMessagesCount() + 1);
        friendToUpdate.setLastMessage(message);
        return true;
      }
    }
    return false;
  }

  public void updateMessageStatus(@NonNull MessageStatus status,
      @NonNull String... messageIds) {
    List<Friend> friends = new ArrayList<>(filteredChatList);
    int index = findFriend(friends, status.getReceiverId());
    if (index != -1) {
      for (String id : messageIds) {
        Message message = friends.get(index).getLastMessage();
        if (message != null && message.getServerMsgId() != null && message.getServerMsgId()
            .equals(id)) {
          message.setMessageStatus(status.getStatus());
          changeDataSet(friends);
          return;
        }
      }
    }
  }

  //tony14
  private int findFriend(@NonNull List<Friend> list,
      @NonNull String clientId) {
    for (Friend friend : list) {
      if (friend.getId().equals(clientId)) {
        return list.indexOf(friend);
      }
    }
    return -1;
  }

  private void changeDataSet(List<Friend> friends) {
    ChatListDiffUtilsCallback diffUtilsCallback =
        new ChatListDiffUtilsCallback(filteredChatList, friends);
    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilsCallback);
    filteredChatList.clear();
    filteredChatList.addAll(friends);
    diffResult.dispatchUpdatesTo(this);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView content;
    public TextView time;
    public TextView msgCount;
    public ImageView image;
    public LinearLayout lyt_parent;

    ViewHolder(View v) {
      super(v);
      title = v.findViewById(R.id.title);
      content = v.findViewById(R.id.content);
      time = v.findViewById(R.id.time);
      image = v.findViewById(R.id.image);
      lyt_parent = v.findViewById(R.id.lyt_parent);
      msgCount = v.findViewById(R.id.message_count);
    }

    void bind(Friend frd) {
      if (frd != null) {
        if (!TextUtils.isEmpty(frd.getName())) {
          title.setText(frd.getName());
        }
        ChatFactory.loadCircleImage(context,
            UrlUtils.getUpdatedImageUrl(frd.getProfileImage(), "large"), image);

        if (frd.getUnSeenMessagesCount() != 0) {
          msgCount.setVisibility(View.VISIBLE);
          msgCount.setText(Integer.toString(frd.getUnSeenMessagesCount()));
        } else {
          msgCount.setVisibility(View.GONE);
        }
      }

      Message msg = frd.getLastMessage();
      if (msg != null) {
        long date =
            msg.getMessageType() == Message.MESSAGE_OUT ? msg.getDate() : msg.getServerDate();
        time.setText(ChatFactory.getFormattedChatListDate(date));

        if (msg.getChatType() == SocketConstants.MessageType.TEXT) {
          content.setText(msg.getMessage().trim());
        } else if (msg.getChatType() == SocketConstants.MessageType.SOUND_EMOJI) {
          int emojiId = Integer.parseInt(msg.getMessage());
          ImageSpan imageSpan = new ImageSpan(itemView.getContext(), Emoji.iconOf(emojiId));
          SpannableStringBuilder sb = new SpannableStringBuilder("Emoji");
          sb.setSpan(imageSpan, 0, sb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
          content.setText(sb);
        }

        int res = 0;
        if (msg.getMessageType() == Message.MESSAGE_OUT) {
          if (msg.getMessageStatus() == Message.SEEN) {
            res = R.drawable.ic_seen;
          } else if (msg.getMessageStatus() == Message.SENT) {
            res = R.drawable.ic_sent;
          } else if (msg.getMessageStatus() == Message.DELIVERED) {
            res = R.drawable.ic_delivered;
          } else {
            res = R.drawable.ic_not_sent;
          }
        }
        content.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0);
      }
    }
  }

  public interface OnChatItemClickListener {
    void onChatItemClick(View view, Friend chat, int position);
  }

  public interface OnChatItemLongClickListener {
    void onChatItemLongClick(View view, Friend chat, int position);
  }
}