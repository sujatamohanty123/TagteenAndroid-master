package in.tagteen.tagteen.TagteenInterface;

import java.io.File;

import in.tagteen.tagteen.Adapters.ChatThreadRecyclerViewAdapter;
import in.tagteen.tagteen.Model.ChatMessage;

/**
 * Created by user on 06-09-2017.
 */

public interface MediaUploadDone {

    void mediaUploadonProgress(String clientdbId, int persent);

    void mediaUploadDone(String msgType , String localPath, String AWSPath, String id);

    void mediaUploadError(String clientdbId);

}
