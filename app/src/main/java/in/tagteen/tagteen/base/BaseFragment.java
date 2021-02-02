package in.tagteen.tagteen.base;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import com.afollestad.materialdialogs.MaterialDialog;
import in.tagteen.tagteen.R;
import java.util.Objects;

public class BaseFragment extends Fragment {

  private MaterialDialog mProgressDialog;
  private MaterialDialog mErrorDialog;
  private boolean mInBackground;

  @Override public void onResume() {
    super.onResume();
    mInBackground = false;
  }

  @Override public void onPause() {
    super.onPause();
    mInBackground = true;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mProgressDialog = null;
    mErrorDialog = null;
  }

  protected boolean isInBackground() {
    return mInBackground;
  }

  protected void showProgressDialog(String message) {
    if (mProgressDialog == null) {
      mProgressDialog =
          new MaterialDialog.Builder(Objects.requireNonNull(getContext())).content(message)
              .progress(true, 0)
              .cancelable(false)
              .build();
    } else {
      mProgressDialog.setContent(message);
    }
    if (!isInBackground() && !isRemoving()) {
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
    if (mProgressDialog != null && mProgressDialog.isShowing() && !isRemoving()) {
      mProgressDialog.dismiss();
    }
  }

  protected void showErrorDialog() {
    showErrorDialog(R.string.general_label_error);
  }

  protected void showErrorDialog(String message) {
    if (mErrorDialog == null) {
      mErrorDialog =
          new MaterialDialog.Builder(Objects.requireNonNull(getContext())).title(
              getString(R.string.general_label_error))
              .content(message)
              .positiveText(getString(R.string.general_label_ok))
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
    if (mErrorDialog != null && mErrorDialog.isShowing() && !isRemoving()) {
      mErrorDialog.dismiss();
    }
  }
}
