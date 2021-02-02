package in.tagteen.tagteen.chatting.volley;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VolleyResponse<T> {

    public interface JsonResponseHandler<T> {

        void OnResponseParsed(int requestTag,
                              @Nullable T unMarshalledObject);
    }

    public interface ResponseListener<T> {

        void onDataResponse(@NonNull RequestResponse<T> response);

    }

}
