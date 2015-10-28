package com.asb.memorizenote.widget.libraryseat;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.asb.memorizenote.R;

/**
 * Created by azureskybox on 15. 10. 27.
 */
public class LibrarySeatsViewActivity extends Activity {

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_seats_view);

        mWebView = (WebView)findViewById(R.id.library_seats_view_main);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://222.233.169.126/EZ5500/SEAT/RoomStatus.aspx");
    }
}
