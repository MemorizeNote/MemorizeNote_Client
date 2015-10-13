package com.asb.memorizenote.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;

import com.asb.memorizenote.Constants.*;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public abstract class BaseActivity extends ActionBarActivity {

    private ProgressDialog mProgressDlg = null;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case HandlerFlags.SHOW_PROGRESS:
                    mProgressDlg.setMessage((String)msg.obj);
                    mProgressDlg.show();
                    break;
                case HandlerFlags.HIDE_PROGRESS:
                    mProgressDlg.dismiss();
                    break;
                default:
                    onHandleExtraMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDlg = new ProgressDialog(BaseActivity.this);
        mProgressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    protected void showProgress(String msg) {
        mHandler.sendMessage(mHandler.obtainMessage(HandlerFlags.SHOW_PROGRESS, msg));
    }

    protected void hideProgress() {
        mHandler.sendEmptyMessage(HandlerFlags.HIDE_PROGRESS);
    }

    abstract protected void onHandleExtraMessage(Message msg);
}
