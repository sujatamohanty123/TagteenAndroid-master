package in.tagteen.tagteen.chatting.paging;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ViewGroup;

import java.util.List;

import in.tagteen.tagteen.chatting.emoji.EmojiPlayer;
import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.socket.SocketConstants;

/**
 * Created by tony00 on 11/16/2018.
 */
public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ConversationViewBuilder cBuilder;
    private EmojiPlayer emojiPlayer;
    private int lastEmojiId;

    ConversationAdapter(Context context) {
        cBuilder = new ConversationViewBuilder();
        emojiPlayer = EmojiPlayer.createPlayer(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConversationViewHolder cVHolder = new ConversationViewHolder();
        return cVHolder.new Holders().get(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        if (type == ConversationModel.ConversationItemType.TYPE_TEXT_INCOMING
                || type == ConversationModel.ConversationItemType.TYPE_TEXT_OUTGOING)
            ((ConversationViewHolder.MessageViewHolder) holder)
                    .bind((ConversationModel.Content) cBuilder.getModel().itemAt(position));
        else if (type == ConversationModel.ConversationItemType.TYPE_DAY)
            ((ConversationViewHolder.DayViewHolder) holder)
                    .bind((ConversationModel.Day) cBuilder.getModel().itemAt(position));
        else if (type == ConversationModel.ConversationItemType.TYPE_SOUND_EMOJI_INCOMING
                || type == ConversationModel.ConversationItemType.TYPE_SOUND_EMOJI_OUTGOING) {
            ConversationViewHolder.EmojiViewHolder emojiHolder = ((ConversationViewHolder.EmojiViewHolder) holder);
            ConversationModel.SoundEmoji soundEmoji = (ConversationModel.SoundEmoji) cBuilder.getModel().itemAt(position);
            emojiHolder.bind(soundEmoji);

            int eId = Integer.parseInt(soundEmoji.getMessage().getMessage());
            if (eId == lastEmojiId) playEmoji();

            emojiHolder.setEmojiSelectListener(emojiId -> {
                emojiPlayer.play(emojiId);
            });
        }
    }

    @Override
    public int getItemCount() {
        return cBuilder.getModel().count();
    }

    @Override
    public int getItemViewType(int position) {
        return cBuilder.getModel().type(position);
    }

    private void playEmoji() {
        if (lastEmojiId != 0) {
            emojiPlayer.play(lastEmojiId);
            lastEmojiId = 0;
        }
    }

    private void setSoundEmojiToPlay(int emojiId) {
        lastEmojiId = emojiId;
    }

    private void findSoundEmojiToPlay(List<Message> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);
            if (message.getChatType() == SocketConstants.MessageType.SOUND_EMOJI
                    && message.getMessageType() == Message.MESSAGE_IN
                    && message.getMessageStatus() != Message.SEEN) {
                setSoundEmojiToPlay(Integer.parseInt(messages.get(i).getMessage()));
                break;
            }
        }
    }

    void submitList(List<Message> list, boolean areMoreMessagesAvailable) {
        cBuilder.submitMessages(list, areMoreMessagesAvailable);
        notifyDataSetChanged();
    }

    public void addMessage(Message message, boolean isDrafted) {
        cBuilder.addMessage(message, isDrafted);
        if (!isDrafted
                && message.getChatType() == SocketConstants.MessageType.SOUND_EMOJI
                && message.getMessageType() == Message.MESSAGE_IN)
            setSoundEmojiToPlay(Integer.parseInt(message.getMessage()));
        notifyDataSetChanged();
    }

    public void addMessages(List<Message> messages) {
        cBuilder.addMessages(messages);
        findSoundEmojiToPlay(messages);
        notifyDataSetChanged();
    }

    public void updateMessage(MessageStatus status, String... messageIds) {
        cBuilder.updateMessage(status, messageIds);
        notifyDataSetChanged();
    }

    public boolean contains(Message message) {
        return InterpolationSearch.findMessageAdapterPosition(cBuilder.getMessages(), message.getId()) != -1;
    }

}
