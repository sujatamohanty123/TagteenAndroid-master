package in.tagteen.tagteen.chatting.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

public interface BaseRequest<T> {

    @NonNull
    String serializedJson(@NonNull T input);

    default void putIfNotNull(
            @NonNull Map<String, String> params,
            @NonNull String key,
            @Nullable String value) {
        if (value != null) {
            params.put(key, value);
        }
    }
}
