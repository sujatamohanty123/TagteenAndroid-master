package in.tagteen.tagteen.networkEngine;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.net.URLDecoder;

public class AsyncWorker extends AsyncTask<String, String, String> {
  private ProgressDialog progress;
  private String response;
  private String REQUEST_NUMBER;
  private Context currentContext;
  public AsyncResponse delegate = null;
  String token;

  public AsyncWorker(Context context) {
    currentContext = context;
    SharedPreferenceSingleton.getInstance().init(context);
    token =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    progress = new ProgressDialog(currentContext);
    progress.setMessage("loading...");
    //  progress.show();
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
        HttpRequestWorker mWorker = new HttpRequestWorker();
        //Log.e("url", url);
        response = mWorker.GetRequest(url, token);
      }
      return response;
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  protected void onPostExecute(String result) {
    progress.hide();
    if (delegate == null) {
      return;
    }
    try {
      result = URLDecoder.decode(result, "UTF-8");
      delegate.ReceivedResponseFromServer(result, REQUEST_NUMBER);
    } catch (Exception e) {
      delegate.ReceivedResponseFromServer(result, REQUEST_NUMBER);
    }
  }
}
