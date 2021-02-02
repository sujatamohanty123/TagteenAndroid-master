package in.tagteen.tagteen;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Mathivanan on 02-02-2018.
 */

public abstract class BaseActivity extends AppCompatActivity {



    private static boolean D = BuildConfig.DEBUG;

    protected String TAG = "Toro:AppBaseActivity";



    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        TAG = "Toro:" + getClass().getSimpleName();

        if (D) Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");

    }



    @Override protected void onPostCreate(@Nullable Bundle bundle) {

        super.onPostCreate(bundle);

        if (D) Log.d(TAG, "onPostCreate() called with: bundle = [" + bundle + "]");

    }



    @Override protected void onStart() {

        super.onStart();

        if (D) Log.d(TAG, "onStart() called");

    }



    @Override protected void onRestart() {

        super.onRestart();

        if (D) Log.d(TAG, "onRestart() called");

    }



    @Override protected void onResume() {

        super.onResume();

        if (D) Log.d(TAG, "onResume() called");

    }



    @Override protected void onResumeFragments() {

        super.onResumeFragments();

        if (D) Log.d(TAG, "onResumeFragments() called");

    }



    @Override protected void onPostResume() {

        super.onPostResume();

        if (D) Log.d(TAG, "onPostResume() called");

    }



    @Override protected void onPause() {

        super.onPause();

        if (D) Log.d(TAG, "onPause() called");

    }



    @Override protected void onStop() {

        super.onStop();

        if (D) Log.d(TAG, "onStop() called");

    }



    @Override protected void onDestroy() {

        super.onDestroy();

        if (D) Log.d(TAG, "onDestroy() called");

    }



    @Override protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        if (D) Log.d(TAG, "onSaveInstanceState() called with: outState = [" + outState + "]");

    }



    @Override protected void onRestoreInstanceState(Bundle bundle) {

        super.onRestoreInstanceState(bundle);

        if (D) Log.d(TAG, "onRestoreInstanceState() called with: bundle = [" + bundle + "]");

    }



    @Override public void onAttachedToWindow() {

        super.onAttachedToWindow();

        if (D) Log.d(TAG, "onAttachedToWindow() called");

    }



    @Override public void onDetachedFromWindow() {

        super.onDetachedFromWindow();

        if (D) Log.d(TAG, "onDetachedFromWindow() called");

    }



    @Override public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {

        super.onMultiWindowModeChanged(isInMultiWindowMode);

        if (D) {

            Log.d(TAG, "onMultiWindowModeChanged() called with: isInMultiWindowMode = ["

                    + isInMultiWindowMode

                    + "]");

        }

    }



    @Override public void enterPictureInPictureMode() {

        super.enterPictureInPictureMode();

        if (D) Log.d(TAG, "enterPictureInPictureMode() called");

    }



    @Override public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {

        super.onPictureInPictureModeChanged(isInPictureInPictureMode);

        if (D) {

            Log.d(TAG, "onPictureInPictureModeChanged() called with: isInPictureInPictureMode = ["

                    + isInPictureInPictureMode

                    + "]");

        }

    }



    @Override protected void onUserLeaveHint() {

        super.onUserLeaveHint();

        if (D) Log.d(TAG, "onUserLeaveHint() called");

    }



    @Override public void onUserInteraction() {

        super.onUserInteraction();

        if (D) Log.d(TAG, "onUserInteraction() called");

    }



    @Override public void onVisibleBehindCanceled() {

        super.onVisibleBehindCanceled();

        if (D) Log.d(TAG, "onVisibleBehindCanceled() called");

    }

}
