package in.tagteen.tagteen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.PostResponseModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.profile.OtherUserProfileActivity;
import in.tagteen.tagteen.profile.UserProfileFragment;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowsableActivity extends AppCompatActivity {
	private static final String TAG = "BrowsableActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.browsable);
		
		if (getIntent().getData() != null) {
			WebView webview = new WebView(this);
			webview.setWebViewClient(new WebViewClient(){
				/* (non-Javadoc)
				 * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
				 */
				@Override
				public void onPageFinished(WebView view, String url) {
					parseBrowsableUri(url);
				}
			});
			webview.removeJavascriptInterface("searchBoxJavaBridge_");
			webview.removeJavascriptInterface("accessibility");
			webview.removeJavascriptInterface("accessibilityTraversal");
			webview.loadUrl(getIntent().getDataString());
		}
	}
	
	private void parseBrowsableUri(String url) {
		if (url != null && this != null) {
			Uri uri = Uri.parse(url);
			String userId = uri.getQueryParameter(Constants.USER_PARAM);
			String postId = uri.getQueryParameter(Constants.POST_PARAM);
			if (userId != null && userId.trim().isEmpty() == false) {
				this.moveToUserProfile(userId);
			} else if (postId != null && postId.trim().isEmpty() == false) {
				this.loadPost(postId);
			} else {
				Utils.showToast(this, getString(R.string.invalid_hyperlink));
				finish();
			}
		}
	}

	private void moveToUserProfile(String userId) {
		if (this == null) {
			return;
		}
		String loggedInUserId =
				SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
		Intent intent;
		if (userId.equalsIgnoreCase(loggedInUserId)) {
			intent = new Intent(this, UserProfileFragment.class);
			intent.putExtra(Constants.LOAD_FRAGMENT, Constants.MY_PROFILE);
		} else {
			intent = new Intent(this, OtherUserProfileActivity.class);
			intent.putExtra(Constants.USER_ID, userId);
		}
		startActivityForResult(intent, 100);
	}

	private void moveToVideoDetails(GetPostResponseModel.PostDetails data) {
		if (this == null || data == null) {
			return;
		}
		List<GetPostResponseModel.PostDetails> dataList = new ArrayList<>();
		dataList.add(data);
		DataCache.getInstance().setPostlist(dataList, true);

		Intent intent = new Intent(this, VideoPartDetail.class);
		intent.putExtra("post_creator_id", data.getOwner_post_creator_id());
		intent.putExtra("post_vid_id", data.getOwner_post_id());
		intent.putExtra("category_id", data.getCategorie_id());
		intent.putExtra(Constants.SHOWROOM_POST_DATA, data);
		startActivityForResult(intent, 101);
	}

	private void moveToMainScreen() {
		Intent intent = new Intent(this, MainDashboardActivity.class);
		startActivity(intent);
		finish();
	}

	private void loadPost(String postId) {
		String loggedInUserId =
				SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
		String token =
				SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
		Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
		Call<PostResponseModel> call = methods.getPostDetails(postId, loggedInUserId, token);
		call.enqueue(new Callback<PostResponseModel>() {
			@Override
			public void onResponse(Call<PostResponseModel> call, Response<PostResponseModel> response) {
				PostResponseModel model = response.body();
				if (model != null && model.getData() != null) {
					moveToVideoDetails(model.getData());
				}
			}

			@Override
			public void onFailure(Call<PostResponseModel> call, Throwable t) {
				Utils.showToast(BrowsableActivity.this, "Unable to load post");
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		this.moveToMainScreen();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
