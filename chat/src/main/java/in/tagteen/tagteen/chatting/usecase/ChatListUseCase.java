package in.tagteen.tagteen.chatting.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.chatting.model.ChatHistory;
import in.tagteen.tagteen.chatting.model.ChatHistoryIn;
import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.model.FriendsResponse;
import in.tagteen.tagteen.chatting.room.ChatOfflineMessageExecutor;
import in.tagteen.tagteen.chatting.room.RecentMessageExecutor;
import in.tagteen.tagteen.chatting.socket.SocketConstants;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import in.tagteen.tagteen.chatting.volley.HttpCommunicator;
import in.tagteen.tagteen.chatting.volley.RequestConfiguration;
import in.tagteen.tagteen.chatting.volley.RequestResponse;
import in.tagteen.tagteen.chatting.volley.VolleyResponse;

/**
 * Created by tony00 on 5/24/2019.
 */
public class ChatListUseCase implements VolleyResponse.ResponseListener {

    private static final int REQUEST_FRIENDS = 0x110b;
    private static final int REQUEST_CHAT_HISTORY = 0x110c;

    private HttpCommunicator httpCommunicator;
    private ChatDataListener responseListener;
    private List<Friend> friends;

    private ChatListUseCase(ChatDataListener responseListener) {
        this.responseListener = responseListener;
        httpCommunicator = new HttpCommunicator(this);
    }

    public static ChatListUseCase newInstance(@NonNull ChatDataListener responseListener) {
        return new ChatListUseCase(responseListener);
    }

    public void fetchChatHistory() {
        RequestConfiguration config = new RequestConfiguration(
                SocketConstants.CHAT_HISTORY,
                REQUEST_CHAT_HISTORY,
                new ChatHistoryIn(ChatSessionManager.getInstance().getSenderId()),
                ChatHistory.class);

        httpCommunicator.addRequest(config, ChatSessionManager.getInstance().getToken());
    }

    private void fetchFriends() {
        String url = SocketConstants.CHAT_DETAILS+ChatSessionManager.getInstance().getSenderId();
        RequestConfiguration config = new RequestConfiguration(
                url,
                REQUEST_FRIENDS,
                FriendsResponse.class);

        httpCommunicator.addGetRequest(config, ChatSessionManager.getInstance().getToken());
    }

    @Override
    public void onDataResponse(@NonNull RequestResponse response) {
        if (response.getError() != null) {
            if (responseListener != null)
                responseListener.onError("Error occurred while fetching friends");
            return;
        }

        if (response.getTag() == REQUEST_CHAT_HISTORY) {
            ChatHistory chatHistory = (ChatHistory) response.getData();

            if ((chatHistory.getMessages() != null && chatHistory.getMessages().size() > 0) ||
                    (chatHistory.getDeliveredMessages() != null && chatHistory.getDeliveredMessages().size() > 0) ||
                    (chatHistory.getSeenMessages() != null && chatHistory.getSeenMessages().size() > 0)) {

                ChatOfflineMessageExecutor messageExecutor = new ChatOfflineMessageExecutor();
                messageExecutor.setListener(deleteChatHistoryIn -> {
                    if(deleteChatHistoryIn != null)
                        ChatDeleteUseCase.newInstance().deleteChatHistory(deleteChatHistoryIn);
                    fetchFriends();
                });
                messageExecutor.execute(chatHistory);
            } else {
                fetchFriends();
            }
        } else if (response.getTag() == REQUEST_FRIENDS) {
            FriendsResponse friendsResponse = (FriendsResponse) response.getData();
            friends = friendsResponse.getFriends();
            if (friends != null && friends.size() > 0)
                fetchRecentMessages();
            else if (responseListener != null) responseListener.onError("No friends found");
        }
    }

    public void fetchRecentMessages() {
        if (friends == null) return;

        RecentMessageExecutor recentMessageExecutor = new RecentMessageExecutor(friendWithMessages -> {
            if (friendWithMessages.size() > 0) {
                if (responseListener != null)
                    responseListener.onFriendsLoaded(friendWithMessages);
            } else {
                if (responseListener != null)
                    responseListener.onError("No messages found");
            }
        });
        recentMessageExecutor.execute(friends.toArray(new Friend[friends.size()]));
    }

    @NonNull
    public List<Friend> getOnlineUsers() {
        List<Friend> onlineUsers = new ArrayList<>();
        for (Friend friend : friends) {
            if (friend.isOnline())
                onlineUsers.add(friend);
        }
        return onlineUsers;
    }

    @Nullable
    public FriendsResponse getFriends() {
        return friends == null ? null : new FriendsResponse(friends);
    }

    public interface ChatDataListener {
        void onFriendsLoaded(@NonNull List<Friend> friends);

        void onError(@NonNull String error);
    }
}
