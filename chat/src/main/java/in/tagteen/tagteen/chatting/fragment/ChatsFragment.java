package in.tagteen.tagteen.chatting.fragment;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import in.tagteen.tagteen.chatting.ActivityChatDetails;
import in.tagteen.tagteen.chatting.ActivityNewChat;
import in.tagteen.tagteen.chatting.R;
import in.tagteen.tagteen.chatting.adapter.ChatsListAdapter;
import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.model.FriendsResponse;
import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.socket.SocketConnection;
import in.tagteen.tagteen.chatting.socket.SocketEventListener;
import in.tagteen.tagteen.chatting.viewmodels.ChatListViewModel;
import in.tagteen.tagteen.chatting.widget.CircularImagesList;
import in.tagteen.tagteen.chatting.widget.DividerItemDecoration;

import static in.tagteen.tagteen.chatting.ActivityChatDetails.KEY_FRIEND;
import static in.tagteen.tagteen.chatting.ActivityChatDetails.KEY_NEW_MESSAGES;

public class ChatsFragment extends Fragment implements SocketEventListener {

    public static final String EXTRA_DATA_FRIEND = ChatsFragment.class.getSimpleName() + "Friend";
    public static final int ACTIVITY_REQUEST_SELECT_FRIEND = 0xb51b;

    public RecyclerView recyclerView;
    private ChatsListAdapter mAdapter;
    private ProgressBar progressBar;
    private CircularImagesList onlineUsersList;
    private ChatListViewModel chatViewModel;
    private SocketConnection socketConn;
    private TextView errorTextView;
    private FrameLayout progressLayout;
    private RelativeLayout dataContainer;
    private FloatingActionButton fab;

    public static ChatsFragment newInstance() {

        Bundle args = new Bundle();

        ChatsFragment fragment = new ChatsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socketConn = SocketConnection.getConnection();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View parent = inflater.inflate(R.layout.fragment_chat, container, false);

        setHasOptionsMenu(true);

        dataContainer = parent.findViewById(R.id.dataContainer);
        recyclerView = parent.findViewById(R.id.recyclerView);
        onlineUsersList = parent.findViewById(R.id.onlineUsers);
        progressBar = parent.findViewById(R.id.progressBar);
        progressLayout = parent.findViewById(R.id.progressLayout);
        errorTextView = parent.findViewById(R.id.errorText);
        fab = parent.findViewById(R.id.addNewChat);

        configView();

        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatListViewModel.class);
        chatViewModel.init();

        chatViewModel.getFriendsObserver().observe(getActivity(), friends -> {
            mAdapter.addAll(friends);

            progressLayout.setVisibility(View.GONE);
            dataContainer.setVisibility(View.VISIBLE);

            List<Friend> onlineUsers = chatViewModel.getOnlineFriends();
            if (onlineUsers.size() > 0) {
                onlineUsersList.setVisibility(View.VISIBLE);
                onlineUsersList.setUsers(onlineUsers);
            }
        });

        chatViewModel.getErrorObserver().observe(getActivity(), error -> {
            dataContainer.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(error);
        });

        chatViewModel.getFilterObserver().observe(getActivity(), text -> {
            if (mAdapter != null)
                mAdapter.getFilter().filter(text);
        });

        onlineUsersList.setOnUserClickListener(this::navigateConvView);

        return parent;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null)
            ((AppCompatActivity) getContext()).getSupportActionBar().setTitle("Tagteen Talk");
    }

    @Override
    public void onStart() {
        super.onStart();
        socketConn.registerSocketEventHandler(this);
        chatViewModel.fetchRecentMessages();
    }

    @Override
    public void onStop() {
        super.onStop();
        socketConn.unregisterSocketEventHandler(this);
    }

    private void configView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new ChatsListAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnChatItemClickListener(chatClickListener);

        fab.setOnClickListener(view -> {
            FriendsResponse friends = chatViewModel.getFriends();
            if (friends != null && friends.getFriends().size() > 0
                    && SocketConnection.getConnection().isSocketConnected()) {
                Intent intent = new Intent(getActivity(), ActivityNewChat.class);
                intent.putExtra(ActivityNewChat.EXTRA_DATA_FRIENDS, friends);
                startActivityForResult(intent, ACTIVITY_REQUEST_SELECT_FRIEND);
            }
        });
    }

    private void navigateConvView(Friend friend) {
        Intent intent = new Intent(getActivity(), ActivityChatDetails.class);
        intent.putExtra(KEY_FRIEND, friend);
        intent.putExtra(KEY_NEW_MESSAGES, friend.getUnSeenMessagesCount() > 0);
        startActivity(intent);
    }

    private final ChatsListAdapter.OnChatItemClickListener chatClickListener = (View view,
                                                                                Friend friend,
                                                                                int position) -> {
        navigateConvView(friend);
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_SELECT_FRIEND &&
                resultCode == Activity.RESULT_OK) {
            if (data != null) {
                navigateConvView((Friend) data.getSerializableExtra(EXTRA_DATA_FRIEND));
            }
        }
    }

    @Override
    public void onNewMessageReceived(@NonNull Message message) {
        if (mAdapter.getItemCount() > 0) {
            boolean updated = mAdapter.updateMessage(message);
            if (!updated) chatViewModel.fetchRecentMessages();
        } else {
            chatViewModel.fetchRecentMessages();
        }
    }

    @Override
    public void onMessageStatusChanged(@NonNull MessageStatus msgStatus,
                                       @Nullable String... messageIds) {
        if (messageIds != null)
            mAdapter.updateMessageStatus(msgStatus, messageIds);
    }

    @Override
    public void onOnlineUsersChanged(@Nullable List<Friend> friends) {
        onlineUsersList.setUsers(friends);
    }
}
