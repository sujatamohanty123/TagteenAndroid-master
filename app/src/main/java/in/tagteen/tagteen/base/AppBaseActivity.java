package in.tagteen.tagteen.base;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import com.afollestad.materialdialogs.MaterialDialog;
import in.tagteen.tagteen.R;

public class AppBaseActivity extends AppCompatActivity {
  private MaterialDialog mProgressDialog;
  private MaterialDialog mErrorDialog;
  private MaterialDialog mExtraNullDialog;
  private boolean mInBackground;
  private boolean mViewDestroyed;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mViewDestroyed = false;
  }

  @Override public void onResume() {
    super.onResume();
    mInBackground = false;
  }

  @Override protected void onPause() {
    super.onPause();
    mInBackground = true;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mViewDestroyed = true;
    mProgressDialog = null;
    mErrorDialog = null;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  protected boolean isInBackground() {
    return mInBackground;
  }

  protected void showProgressDialog(String message) {
    if (mProgressDialog == null) {
      mProgressDialog = new MaterialDialog.Builder(this).content(message)
          .progress(true, 0)
          .cancelable(false)
          .build();
    } else {
      mProgressDialog.setContent(message);
    }
    if (!isInBackground() && !isFinishing()) {
      mProgressDialog.show();
    }
  }

  protected void showProgressDialog() {
    showProgressDialog(R.string.general_label_pleasewait);
  }

  protected void showProgressDialog(@StringRes int messageResId) {
    showProgressDialog(getString(messageResId));
  }

  protected void dismissProgressDialog() {
    if (mProgressDialog != null && mProgressDialog.isShowing() && !isFinishing()) {
      mProgressDialog.dismiss();
    }
  }

  public void setMessage(String message) {
    if (this.mProgressDialog == null) {
      return;
    }
    this.mProgressDialog.setContent(message);
  }

  protected void showErrorDialog() {
    showErrorDialog(R.string.general_label_error);
  }

  protected void showErrorDialog(String message) {
    if (mErrorDialog == null) {
      mErrorDialog = new MaterialDialog.Builder(this).title(R.string.general_label_error)
          .content(message)
          .positiveText(R.string.general_label_ok)
          .build();
    } else {
      mErrorDialog.setContent(message);
    }
    if (!isInBackground()) {
      mErrorDialog.show();
    }
  }

  protected void showErrorDialog(@StringRes int messageResId) {
    showErrorDialog(getString(messageResId));
  }

  protected void dismissErrorDialog() {
    if (mErrorDialog != null && mErrorDialog.isShowing() && !isFinishing()) {
      mErrorDialog.dismiss();
    }
  }

  protected void onPositiveExtraNullDialog() {
    //override this if you want to do additional things
    finish();
  }
}
