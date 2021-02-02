package in.tagteen.tagteen.apimodule_retrofit;

/**
 * Created by Jayattama Prusty on 30-Aug-17.
 */

public interface ApiRetrofitResponse {
    void ReceivedResponseFromRetrofit_with200(String output);
    void ReceivedResponseFromRetrofit_with401(String output);
    void ReceivedResponseFromRetrofit_failure(String output);

}
