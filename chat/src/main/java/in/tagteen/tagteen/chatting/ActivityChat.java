package in.tagteen.tagteen.chatting;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import in.tagteen.tagteen.chatting.data.Tools;
import in.tagteen.tagteen.chatting.fragment.ChatsFragment;
import in.tagteen.tagteen.chatting.utils.ChatFactory;
import in.tagteen.tagteen.chatting.utils.ScreenTracker;
import in.tagteen.tagteen.chatting.viewmodels.ChatListViewModel;

public class ActivityChat extends AppCompatActivity {

    private boolean isSearchEnabled;
    private ChatListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_list);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.chatFragment, ChatsFragment.newInstance()).commit();

        viewModel  = ViewModelProviders.of(this).get(ChatListViewModel.class);

        setSupportActionBar(findViewById(R.id.toolbar));
        configActionBar();
        Tools.systemBarLolipop(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        ScreenTracker.getInstance().registerScreen(ActivityChat.class.getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        ScreenTracker.getInstance().unRegisterScreen(ActivityChat.class.getSimpleName());
    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(isSearchEnabled ? R.menu.menu_search_toolbar : R.menu.menu_main, menu);
        if (isSearchEnabled) {
            final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
            search.setIconified(false);
            search.setQueryHint("Search chats...");
            search.setOnQueryTextListener(searchQueryListener);
            search.setOnCloseListener(() -> {
                closeSearch();
                return true;
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            isSearchEnabled = true;
            supportInvalidateOptionsMenu();
            return true;
        } else if (id == android.R.id.home) {
            if (isSearchEnabled)
                closeSearch();
            else
                finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void closeSearch() {
        ChatFactory.hideKeyboard(this, getCurrentFocus());
        if (isSearchEnabled) {
            isSearchEnabled = false;
            viewModel.getFilterObserver().setValue(null);
            supportInvalidateOptionsMenu();
        }
    }

    private final SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            viewModel.getFilterObserver().setValue(newText);
            return true;
        }
    };

}
