package in.tagteen.tagteen.chatting.volley;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;

public class RequestResponse<T> {

    private int tag;
    private T data;
    private VolleyError error;

    public RequestResponse(int tag,
                           @Nullable T data,
                           @Nullable VolleyError error) {
        this.tag = tag;
        this.data = data;
        this.error = error;
    }

    public int getTag() {
        return tag;
    }

    public T getData() {
        return data;
    }

    public VolleyError getError() {
        return error;
    }
}
