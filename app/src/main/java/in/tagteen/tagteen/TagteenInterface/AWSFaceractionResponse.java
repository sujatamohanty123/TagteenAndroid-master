package in.tagteen.tagteen.TagteenInterface;

/**
 * Created by user on 07-09-2017.
 */

public interface AWSFaceractionResponse {

    void sentReaction(String url, String Reaction);
    void  onUploadReaction(String url,String Reaction, int percent);
    void  onerror(String url, String Reaction);

}
