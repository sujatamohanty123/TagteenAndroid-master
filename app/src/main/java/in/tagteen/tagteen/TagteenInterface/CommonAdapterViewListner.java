package in.tagteen.tagteen.TagteenInterface;

import android.view.View;

import in.tagteen.tagteen.Adapters.ChatThreadRecyclerViewAdapter;
import in.tagteen.tagteen.Model.ChatMessage;

/**
 * Created by user on 03-10-2017.
 */

public interface CommonAdapterViewListner {
    void getClickedRecyclerView(ChatThreadRecyclerViewAdapter.ViewHolder viewHolder, int position,ChatMessage chatMessage,int type);


}
