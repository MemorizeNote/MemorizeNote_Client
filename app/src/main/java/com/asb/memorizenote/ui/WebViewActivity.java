package com.asb.memorizenote.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.R;
import com.asb.memorizenote.utils.MNLog;

/**
 * Created by azureskybox on 16. 4. 21.
 */
public class WebViewActivity extends Activity {

    private static final String BASE_URL_DICTIONARY = "https://m.search.naver.com/search.naver?query=%s&sm=msv_hty";

    public static Intent getLaunchingIntent(Context context, int pageType, String... args) {
        MNLog.d("getLaunchingIntent, pageType=" + pageType);

        Intent intent = new Intent();
        intent.putExtra(Constants.IntentFlags.WebViewActivity.PAGE_TYPE, pageType);
        intent.setClass(context, WebViewActivity.class);

        switch(pageType) {
            case Constants.IntentFlags.WebViewActivity.PageType.DIC:
                intent.putExtra(Constants.IntentFlags.WebViewActivity.PAGE_URL, String.format(BASE_URL_DICTIONARY, args[0]));
                return intent;
            default:
                return null;
        }
    }

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mWebView = (WebView)findViewById(R.id.webview_activity_main);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(getIntent().getStringExtra(Constants.IntentFlags.WebViewActivity.PAGE_URL));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
