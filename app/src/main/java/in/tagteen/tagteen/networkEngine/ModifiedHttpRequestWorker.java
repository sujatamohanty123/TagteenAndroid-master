package in.tagteen.tagteen.networkEngine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;


public class ModifiedHttpRequestWorker {
	public ModifiedHttpRequestWorker() {
		super();
	}

    /*
	 * Method: GetRequest
	 * @param: url:String, isHeaderRequired:boolean
	 * @Desc : url: URL to connect the server,
	 * isHeaderRequired	: Set dbName as Header for every request after Registration.
	 */

	public String GetRequest(String urlString, String token, String loggedInUserId) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setDoOutput(false);
			connection.setRequestProperty("token", token);
			connection.setRequestProperty("login_user_id", loggedInUserId);

			InputStream in = connection.getInputStream();
			StringBuffer sb = new StringBuffer();
			int chr;
			while ((chr = in.read()) != -1) {
				sb.append((char) chr);
			}
			String result = sb.toString();
			/*return result;

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("token",token);
			httpGet.setHeader("login_user_id",logedn_user_id);
		    HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 5000);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String result = httpClient.execute(httpGet, responseHandler);*/
			if (result != null) {
				result = URLDecoder.decode(result, "UTF-8");
			}
			return result;
		} catch (Exception ex) {
			return "Failed " + ex;
		}
	}
}
