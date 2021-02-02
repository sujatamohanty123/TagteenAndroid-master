package in.tagteen.tagteen.chatting.model;

import in.tagteen.tagteen.chatting.socket.SocketConstants;

public class TypingStatus extends ChatId {

    private transient boolean typing;

    public TypingStatus(boolean isTyping) {
        super(isTyping ?
                SocketConstants.Event.TYPING : SocketConstants.Event.STOP_TYPING);
        this.typing = isTyping;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

}
