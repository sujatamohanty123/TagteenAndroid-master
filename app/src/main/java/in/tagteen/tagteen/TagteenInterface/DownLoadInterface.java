package in.tagteen.tagteen.TagteenInterface;

import java.util.Locale;

import in.tagteen.tagteen.Adapters.ChatThreadRecyclerViewAdapter;
import in.tagteen.tagteen.Model.ChatMessage;



public interface DownLoadInterface {

    void mediaDownLoadProgress(int persent);

    void mediaDownloadDone(String id, String localePath);

}
