package in.tagteen.tagteen.chatting.paging;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import in.tagteen.tagteen.chatting.R;
import in.tagteen.tagteen.chatting.emoji.Emoji;
import in.tagteen.tagteen.chatting.emoji.EmojiPlayer;
import in.tagteen.tagteen.chatting.emoji.EmojiSelectListener;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.utils.ChatFactory;

/**
 * Created by tony00 on 11/16/2018.
 */
class ConversationViewHolder {

    class Holders {

        RecyclerView.ViewHolder get(ViewGroup viewGroup, int type) {
            switch (
                    type
                    ) {
                case ConversationModel.ConversationItemType.TYPE_TEXT_INCOMING:
                case ConversationModel.ConversationItemType.TYPE_TEXT_OUTGOING:
                    View v = LayoutInflater.from(viewGroup.getContext()).
                            inflate(R.layout.row_chat_details,
                                    viewGroup,
                                    false);
                    return new MessageViewHolder(v, type);

                case ConversationModel.ConversationItemType.TYPE_PROGRESS:
                    View v1 = LayoutInflater.from(viewGroup.getContext()).
                            inflate(R.layout.list_loading_item,
                                    viewGroup,
                                    false);
                    return new ProgressViewHolder(v1);

                case ConversationModel.ConversationItemType.TYPE_DAY:
                    View v2 = LayoutInflater.from(viewGroup.getContext()).
                            inflate(R.layout.list_day_item,
                                    viewGroup,
                                    false);
                    return new DayViewHolder(v2);
                case ConversationModel.ConversationItemType.TYPE_SOUND_EMOJI_INCOMING:
                case ConversationModel.ConversationItemType.TYPE_SOUND_EMOJI_OUTGOING:
                    View v3 = LayoutInflater.from(viewGroup.getContext()).
                            inflate(R.layout.row_sound_emoji,
                                    viewGroup,
                                    false);
                    return new EmojiViewHolder(v3, type);
            }
            return null;
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder implements DataBinder<ConversationModel.Content> {

        private TextView timeView;
        private TextView messageView;
        private ImageView imageStatus;

        MessageViewHolder(View itemView, int type) {
            super(itemView);
            timeView = itemView.findViewById(R.id.text_time);
            messageView = itemView.findViewById(R.id.text_content);
            imageStatus = itemView.findViewById(R.id.image_status);
            LinearLayout layParent = itemView.findViewById(R.id.lyt_parent);
            CardView cardView = itemView.findViewById(R.id.lyt_thread);

            if (type == ConversationModel.ConversationItemType.TYPE_TEXT_OUTGOING) {
                layParent.setPadding(100, 0, 20, 0);
                layParent.setGravity(Gravity.RIGHT);
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.me_chat_bg));
            } else if (type == ConversationModel.ConversationItemType.TYPE_TEXT_INCOMING) {
                layParent.setPadding(20, 0, 100, 0);
                layParent.setGravity(Gravity.LEFT);
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.grey_bg));
            }
        }

        void clear() {
            timeView.setText("");
            messageView.setText("");
        }

        @Override
        public void bind(ConversationModel.Content data) {
            Message message = data.getMessage();

            long date = message.getMessageType() == Message.MESSAGE_OUT ?
                    message.getDate() : message.getServerDate();
            timeView.setText(ChatFactory.getFormattedTime(date));
            messageView.setText(message.getMessage());

            if (message.getMessageType() == Message.MESSAGE_OUT) {
                int res = 0;
                if (message.getMessageStatus() == Message.SEEN)
                    res = R.drawable.ic_seen;
                else if (message.getMessageStatus() == Message.SENT)
                    res = R.drawable.ic_sent;
                else if (message.getMessageStatus() == Message.DELIVERED)
                    res = R.drawable.ic_delivered;
                else
                    res = R.drawable.ic_not_sent;
                imageStatus.setImageResource(res);
                imageStatus.setVisibility(View.VISIBLE);
            } else {
                imageStatus.setVisibility(View.GONE);
            }
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressViewHolder(View itemView) {
            super(itemView);
        }

    }

    class DayViewHolder extends RecyclerView.ViewHolder implements DataBinder<ConversationModel.Day> {

        DayViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(ConversationModel.Day data) {
            ((TextView) itemView.findViewById(R.id.textView))
                    .setText(ChatFactory.getFormattedItemDate(data.getDate()));
        }
    }

    class EmojiViewHolder extends RecyclerView.ViewHolder implements DataBinder<ConversationModel.SoundEmoji> {

        private TextView timeView;
        private ImageView emojiView;
        private ImageView imageStatus;
        private EmojiPlayer player;
        private EmojiSelectListener emojiSelectListener;

        EmojiViewHolder(View itemView, int type) {
            super(itemView);

            timeView = itemView.findViewById(R.id.text_time);
            emojiView = itemView.findViewById(R.id.emoji);
            imageStatus = itemView.findViewById(R.id.image_status);
            LinearLayout layParent = itemView.findViewById(R.id.lyt_parent);
            RelativeLayout content = itemView.findViewById(R.id.lyt_thread);

            if (type == ConversationModel.ConversationItemType.TYPE_SOUND_EMOJI_OUTGOING) {
                layParent.setPadding(100, 0, 20, 0);
                layParent.setGravity(Gravity.RIGHT);
                content.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.transparent));
            } else if (type == ConversationModel.ConversationItemType.TYPE_SOUND_EMOJI_INCOMING) {
                layParent.setPadding(20, 0, 100, 0);
                layParent.setGravity(Gravity.LEFT);
                content.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.transparent));
            }
        }

        @Override
        public void bind(ConversationModel.SoundEmoji data) {
            Message message = data.getMessage();

            long date = message.getMessageType() == Message.MESSAGE_OUT ?
                    message.getDate() : message.getServerDate();
            timeView.setText(ChatFactory.getFormattedTime(date));

            if (message.getMessageType() == Message.MESSAGE_OUT) {
                int res = 0;
                if (message.getMessageStatus() == Message.SEEN)
                    res = R.drawable.ic_seen;
                else if (message.getMessageStatus() == Message.SENT)
                    res = R.drawable.ic_sent;
                else if (message.getMessageStatus() == Message.DELIVERED)
                    res = R.drawable.ic_delivered;
                else
                    res = R.drawable.ic_not_sent;
                imageStatus.setImageResource(res);
                imageStatus.setVisibility(View.VISIBLE);
            } else {
                imageStatus.setVisibility(View.GONE);
            }

            int emojiId = Integer.parseInt(message.getMessage());
            emojiView.setImageResource(Emoji.iconOf(emojiId));
            emojiView.setTag(emojiId);
            emojiView.setOnClickListener(v -> {
                if (emojiSelectListener != null)
                    this.emojiSelectListener.onEmojiSelect(emojiId);
            });
        }

        public void setEmojiSelectListener(EmojiSelectListener emojiSelectListener) {
            this.emojiSelectListener = emojiSelectListener;
        }
    }

    interface DataBinder<T> {
        void bind(T data);
    }
}
