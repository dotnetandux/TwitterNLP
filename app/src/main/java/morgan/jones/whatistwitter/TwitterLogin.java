package morgan.jones.whatistwitter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created on 19/03/2019.
 *
 * @author Morgan Eifion Jones
 * @version 1.0
 */
public class TwitterLogin extends Activity
{
    private WebView webView;

    public static String EXTRA_URL = "extra_url";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Super Code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_login);
        setTitle("Login to Twitter");

        String url = this.getIntent().getStringExtra(EXTRA_URL);
        if (url == null)
        {
            Log.e("Twitter", "URL NULL");
            finish();
        }

        webView = findViewById(R.id.webview_login);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    public class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            Uri uri = Uri.parse(url);

            String verify = uri.getQueryParameter("oAuth_verifier");
            Intent result = new Intent();
            result.putExtra("oAuth_verifier", verify);
            setResult(RESULT_OK, result);

            finish();
            return true;
        }
    }
}
