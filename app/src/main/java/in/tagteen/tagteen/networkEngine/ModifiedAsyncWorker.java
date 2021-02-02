package in.tagteen.tagteen.networkEngine;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.net.URLDecoder;

public class ModifiedAsyncWorker extends AsyncTask<String, String, String> {
  private ProgressDialog progress;
  private String response;
  private String REQUEST_NUMBER;
  public Context currentContext;
  public AsyncResponse delegate = null;
  private String token, loggedInUserId;

  public ModifiedAsyncWorker(Context context) {
    this.currentContext = context;
    SharedPreferenceSingleton.getInstance().init(context);
    token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    loggedInUserId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    progress = new ProgressDialog(currentContext);
    progress.setMessage("loading...");
  }

  @Override
  protected String doInBackground(String... params) {
    try {
      String url = params[0];
      String content = params[1];
      String requestType = params[2];
      String HeaderRequired = params[3];
      this.REQUEST_NUMBER = params[4];
      boolean isHeaderRequired = Boolean.parseBoolean(HeaderRequired);

      if (requestType.equals("POST")) {
        HttpRequestWorker mWorker = new HttpRequestWorker();
        //Log.e("url", url);
        //Log.e("params", content);
        response = mWorker.PostRequest(url, content, REQUEST_NUMBER, token);
      } else if (requestType.equals("GET")) {
        ModifiedHttpRequestWorker mWorker = new ModifiedHttpRequestWorker();
        //Log.e("url", url + loggedInUserId);
        response = mWorker.GetRequest(url, token, loggedInUserId);
      }

      return response;
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  protected void onPostExecute(String result) {
    progress.hide();
    try {
      result = URLDecoder.decode(result, "UTF-8");
      if (this.delegate != null) {
        this.delegate.ReceivedResponseFromServer(result, REQUEST_NUMBER);
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (this.delegate != null) {
        this.delegate.ReceivedResponseFromServer(result, REQUEST_NUMBER);
      }
    }
  }
}
