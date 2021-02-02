package in.tagteen.tagteen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import in.tagteen.tagteen.util.Utils;

public class GamingActivity extends AppCompatActivity {
    private WebView webView;
    private LinearLayout layoutProgress;
    private LinearLayout layoutRefresh;
    private Toolbar toolbar;

    private static final String GAMING_URL = "https://www.gamezop.com/?id=cpZ8-f6C5";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gaming);

        this.initWidgets();
        this.bindEvents();
    }

    private void initWidgets() {
        this.webView = findViewById(R.id.webview);
        this.layoutProgress = findViewById(R.id.layoutProgress);
        this.layoutRefresh = findViewById(R.id.layoutRefresh);
        this.toolbar = findViewById(R.id.toolbar);

        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.setSoundEffectsEnabled(true);
        this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.webView.getSettings().setGeolocationEnabled(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setAllowContentAccess(true);
        this.webView.getSettings().setDatabaseEnabled(true);
        this.webView.getSettings().setLoadsImagesAutomatically(true);
        CookieManager.getInstance().setAcceptCookie(true);
        this.webView.setBackgroundColor(Color.argb(1,0,0,0));
        // Enable database and localstorage in webview
        this.webView.getSettings().setDatabaseEnabled(true);
        this.webView.getSettings().setAllowFileAccess(true);
        this.webView.setHapticFeedbackEnabled(false);

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String domain = "";
                try {
                    URL gameUrl = new URL(request.getUrl().toString());
                    domain = gameUrl.getHost();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                if (domain.contains(getString(R.string.gamezop_host_name))
                        || domain.contains(getString(R.string.gamezop_paytm_transaction_url))
                        || domain.contains(getString(R.string.gamezop_hdfcbank_url))) {
                    view.loadUrl(request.getUrl().toString());
                } else {
                    loadOutsideWebview(view, request.getUrl());
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                layoutProgress.setVisibility(View.GONE);
            }
        });
        this.webView.loadUrl(GAMING_URL);
    }

    private void loadOutsideWebview(WebView webView, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (activities != null && !activities.isEmpty()) {
            startActivity(intent);
        } else {
            Utils.showToast(this, "No application found to load");
        }
    }

    private void bindEvents() {
        this.layoutRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(GAMING_URL);
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.toolbar.setVisibility(View.GONE);
        } else {
            this.toolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.webView.destroy();
    }
}
