package in.tagteen.tagteen.networkEngine;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequestWorker {
	public AsyncResponse delegate = null;
	public HttpRequestWorker() {
		super();
	}

    /*
	 * Method: GetRequest
	 * @param: url:String,isHeaderRequired:boolean
	 * @Desc : url				: URL to connect the server,
	 * isHeaderRequired	: Set dbName as Header for every request after Registration.
	 */

	public String GetRequest(String urlString, String token) {

		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setDoOutput(false);
			connection.setRequestProperty("token", token);

			InputStream in = connection.getInputStream();
			StringBuffer sb = new StringBuffer();
			int chr;
			while ((chr = in.read()) != -1) {
				sb.append((char) chr);
			}
			String result = sb.toString();
			return result;
		} catch (Exception ex) {
			return "Failed " + ex;
		}
	}

    /*
     * Method: POSTRequest
     * @param: url:String, content:String, isHeaderRequired:boolean
     * @Desc : url				: URL to connect the server,
     *         content			: JSON Content to send,
     *         isHeaderRequired	: Set dbName as Header for every request after Registration.
     */
	public String PostRequest(String urlString, String content, String requestNumber, String token) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("token", token);
			connection.setInstanceFollowRedirects(false);

			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
			writer.write(content);

			writer.flush();
			writer.close();
			os.close();

			int code =connection.getResponseCode();
			System.out.println("code"+code);
			InputStream in = connection.getInputStream();
			StringBuffer sb = new StringBuffer();
			int chr;
			while ((chr = in.read()) != -1) {
				sb.append((char) chr);
			}
			return sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Failed " + ex;
		}
	}


	public void registrationCall(){
		try{
			OkHttpClient client = new OkHttpClient();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\n  \"profile_url\": \"https://ttprofileurl.s3.amazonaws.com/inputs/1593854853569_010101_0_07_04_14_57_30.jpg\",\n  \"device_token\": \"dtAzjt96260:APA91bF6SGa7oTYFbYOppkdXNu3Vkje8pooRXzjSPM3qyw8KzCMRtQyWuBOrjoJLqSlO2whiaGUBNfIl1QxaeXiU1so3abX8Lct3X_7tXlFkYxjatZGPG9tB2NiBTbJfF08aC2N7ueLr\",\n  \"country_code\": \"+91\",\n  \"password\": \"123456\",\n  \"first_name\": \"Lovekush\",\n  \"last_name\": \"Vishwa\",\n  \"dob\": \"4-07-2000\",\n  \"mobile\": \"8237509284\",\n  \"email\": \"viswakarmalovekush6@gmail.com\",\n  \"device_id\": \"2d864b7bfedfd32\",\n  \"verification_code\": \"123456\"\n}");
			Request request = new Request.Builder()
					.url("https://pro-api.tagteen.in/write/api/v1.0/user/register/")
					.post(body)
					.addHeader("content-type", "application/json")
					.addHeader("cache-control", "no-cache")
					.addHeader("postman-token", "56e415dd-b74c-4746-ad37-f83a289dff64")
					.build();

			Response response = client.newCall(request).execute();
			String res=response.body().toString();
			System.out.println(("response----"+res));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
