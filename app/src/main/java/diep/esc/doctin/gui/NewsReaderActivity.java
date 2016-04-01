package diep.esc.doctin.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import diep.esc.doctin.R;

/**
 * Created by Diep on 31/03/2016.
 */
public class NewsReaderActivity extends Activity {
    private WebView mWebView;
    private WebViewClient mWebViewClient;
    private ProgressBar mPBar;
    private static final String TAG = "log_NewsReaderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_reader);
        final String url = getIntent().getStringExtra("url");

        //https://code.google.com/p/android/issues/detail?id=9375
        mWebView=new WebView(getApplicationContext());
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup)findViewById(R.id.webLayout)).addView(mWebView);
        mPBar = (ProgressBar) findViewById(R.id.progressBar);
        ((ViewGroup)findViewById(R.id.webLayout)).removeView(mPBar);
        ((ViewGroup)findViewById(R.id.webLayout)).addView(mPBar);
        mWebViewClient = new WebViewClient() {
//            private static final byte READY=0;
//            private static final byte LOADING=1;
//            private static final byte REDIRECT=2;
//            private static final byte DONE=3;
//
//            private byte state=READY;
//            private boolean pageFinished=false;
//            private String oldUrl=url;
//            private boolean redirected=false;
            private boolean pageLoaded=false;

            @Override
            public void onPageFinished(WebView view, String url) {
//                if(!redirected) {
                    mPBar.setVisibility(View.INVISIBLE);
                    pageLoaded=true;
//                }
//                else{
//                    mWebView.loadUrl(url);
//                }
//                redirected=false;
                Log.d(TAG, "onPageFinished "+url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                showAlertDialog("loading Error", error.toString());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted ");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(pageLoaded){

                    Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
//                Log.d(TAG, "shouldOverrideUrlLoading ");
//                redirected=true;
                return false;
            }
        };
        mWebView.setWebViewClient(mWebViewClient);
        WebSettings settings = mWebView.getSettings();
//        settings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        mWebView.destroyDrawingCache();
        mWebView.destroy();
        super.onDestroy();
    }

    public void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
}
