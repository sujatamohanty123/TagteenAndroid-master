package in.tagteen.tagteen.chatting.volley;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RequestConfiguration<T> {

    private String url;
    private int tag;
    private Object payload;
    private Class<T> dataUnMarshaller;

    public RequestConfiguration(@NonNull String url,
                                int tag) {
        this(url, tag, null, null);
    }

    public RequestConfiguration(@NonNull String url,
                                int tag,
                                @NonNull Class<T> dataUnMarshaller) {
        this(url, tag, null, dataUnMarshaller);
    }

    public RequestConfiguration(@NonNull String url,
                                int tag,
                                @Nullable Object requestBody,
                                @Nullable Class<T> dataUnMarshaller) {
        this.url = url;
        this.tag = tag;
        this.payload = requestBody;
        this.dataUnMarshaller = dataUnMarshaller;
    }

    public String getUrl() {
        return url;
    }

    public int getTag() {
        return tag;
    }

    public Object getPayload() {
        return payload;
    }

    public Class<T> getDataUnMarshaller() {
        return dataUnMarshaller;
    }
}
