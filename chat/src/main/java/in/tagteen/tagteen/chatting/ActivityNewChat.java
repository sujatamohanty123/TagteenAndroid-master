package in.tagteen.tagteen.chatting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.GridView;

import java.util.List;

import in.tagteen.tagteen.chatting.adapter.NewChatListAdapter;
import in.tagteen.tagteen.chatting.data.Tools;
import in.tagteen.tagteen.chatting.fragment.ChatsFragment;
import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.model.FriendsResponse;
import in.tagteen.tagteen.chatting.utils.ScreenTracker;

/**
 * Created by tony00 on 5/4/2019.
 */
public class ActivityNewChat extends AppCompatActivity {

    public static final String EXTRA_DATA_FRIENDS = "in.tagteen.tagteen.chatting.ActivityNewChat.Friends";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        configAdapter();
        initToolbar();
        Tools.systemBarLolipop(this);
    }

    private void configAdapter() {
        FriendsResponse response = (FriendsResponse) getIntent().getSerializableExtra(EXTRA_DATA_FRIENDS);
        List<Friend> friends = response.getFriends();

        GridView gridView = findViewById(R.id.grid_view);

        NewChatListAdapter adapter = new NewChatListAdapter(friends);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Friend friend = adapter.getItem(position);
            Intent intent = new Intent();
            intent.putExtra(ChatsFragment.EXTRA_DATA_FRIEND, friend);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("New Chat");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ScreenTracker.getInstance().registerScreen(ActivityNewChat.class.getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        ScreenTracker.getInstance().unRegisterScreen(ActivityNewChat.class.getSimpleName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

}
