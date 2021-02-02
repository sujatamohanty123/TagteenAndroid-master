package in.tagteen.tagteen.chatting.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.model.FriendsResponse;
import in.tagteen.tagteen.chatting.usecase.ChatListUseCase;

public class ChatListViewModel extends ViewModel implements ChatListUseCase.ChatDataListener {

    private MutableLiveData<List<Friend>> friendsObserver;
    private MutableLiveData<String> errorObserver;
    private MutableLiveData<String> filterObserver;
    private ChatListUseCase useCase;

    public void init() {
        useCase = ChatListUseCase.newInstance(this);
        friendsObserver = new MutableLiveData<>();
        errorObserver = new MutableLiveData<>();
        filterObserver = new MutableLiveData<>();

        useCase.fetchChatHistory();
    }

    @Override
    public void onFriendsLoaded(@NonNull List<Friend> friends) {
        friendsObserver.setValue(friends);
    }

    @Override
    public void onError(@NonNull String error) {
        errorObserver.setValue(error);
    }

    public void fetchRecentMessages() {
        useCase.fetchRecentMessages();
    }

    @NonNull
    public List<Friend> getOnlineFriends(){
        return useCase.getOnlineUsers();
    }

    @Nullable
    public FriendsResponse getFriends(){
        return useCase.getFriends();
    }

    public MutableLiveData<List<Friend>> getFriendsObserver() {
        return friendsObserver;
    }

    public MutableLiveData<String> getErrorObserver() {
        return errorObserver;
    }

    public MutableLiveData<String> getFilterObserver() {
        return filterObserver;
    }
}
