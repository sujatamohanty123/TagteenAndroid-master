package in.tagteen.tagteen.networkEngine;

public interface AsyncResponse {
    void onRefresh();
    void ReceivedResponseFromServer(String output, String REQUEST_NUMBER);

}