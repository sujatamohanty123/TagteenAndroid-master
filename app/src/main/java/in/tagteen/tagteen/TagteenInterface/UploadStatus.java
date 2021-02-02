package in.tagteen.tagteen.TagteenInterface;

/**
 * Created by user on 27-09-2017.
 */

public interface UploadStatus {
   void sendImageMessage(String messageType, String path, String message, String clientId, String isPrivate, String ChatId, boolean isLike, boolean isAgree, boolean isRecall) ;

    }
