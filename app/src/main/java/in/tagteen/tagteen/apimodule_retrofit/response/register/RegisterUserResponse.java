package in.tagteen.tagteen.apimodule_retrofit.response.register;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class RegisterUserResponse {

  @SerializedName("data")
  private Data data;

  @SerializedName("success")
  private boolean success;

  public Data getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  @NotNull @Override public String toString() {
    return "RegisterUserResponse{" +
        "data=" + data +
        ", success=" + success +
        '}';
  }
}