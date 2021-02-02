package in.tagteen.tagteen.chatting.usecase;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.model.DeleteChatHistoryIn;
import in.tagteen.tagteen.chatting.socket.SocketConstants;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import in.tagteen.tagteen.chatting.volley.HttpCommunicator;
import in.tagteen.tagteen.chatting.volley.RequestConfiguration;

/**
 * Created by tony00 on 6/22/2019.
 */
public class ChatDeleteUseCase {

    private static final int REQUEST_CHAT_DELETE = 0x110d;

    private HttpCommunicator httpCommunicator;

    private ChatDeleteUseCase() {
        httpCommunicator = new HttpCommunicator(null);
    }

    public static ChatDeleteUseCase newInstance() {
        return new ChatDeleteUseCase();
    }

    public void deleteChatHistory(@NonNull DeleteChatHistoryIn deleteChatHistoryIn) {
        RequestConfiguration config = new RequestConfiguration(
                SocketConstants.CHAT_DELETE,
                REQUEST_CHAT_DELETE,
                deleteChatHistoryIn,
                null);

        httpCommunicator.addRequest(config, ChatSessionManager.getInstance().getToken());
    }

}
