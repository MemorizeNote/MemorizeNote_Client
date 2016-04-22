package com.asb.memorizenote.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.asb.memorizenote.Constants.*;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDlg = null;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("MN", "handleMessage, type="+msg.what);
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
        mProgressDlg.setCancelable(false);
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showProgress(String msg) {
        mHandler.sendMessage(mHandler.obtainMessage(HandlerFlags.SHOW_PROGRESS, msg));
    }

    protected void hideProgress() {
        mHandler.sendEmptyMessage(HandlerFlags.HIDE_PROGRESS);
    }

    abstract protected void onHandleExtraMessage(Message msg);
}
