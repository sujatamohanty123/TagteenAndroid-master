package in.tagteen.tagteen.chatting.volley;

/**
 * Created by Tony on 12/25/2017.
 */

public class ResponseNotFoundException extends RuntimeException {

    public ResponseNotFoundException() {
        super("No response listener found in the request queue");
    }

}
