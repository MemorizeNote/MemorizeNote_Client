package com.asb.memorizenote.player;

import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public class TestPlayerActivity extends BasePlayerActivity {
    @Override
    protected void onPreviousChapter() {
        Log.e("MN", "p chapter");
    }

    @Override
    protected void onNextChapter() {
        Log.e("MN", "n chapter");
    }

    @Override
    protected void onPreviousContent() {
        Log.e("MN", "p content");
    }

    @Override
    protected void onNextContent() {
        Log.e("MN", "n content");
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {
    }
}
