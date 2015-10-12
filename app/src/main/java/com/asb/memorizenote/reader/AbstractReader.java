package com.asb.memorizenote.reader;

import android.content.Context;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.apater.AbstractAdapter;

/**
 * Created by azureskybox on 15. 10. 12.
 *
 * Read data from extra resources.
 */
public abstract class AbstractReader {
    protected Context mContext;
    protected OnDataReadListener mListener;
    public int mType = Constants.ReaderType.NONE;

    public AbstractReader(Context context) {
        mContext = context;
    }

    public boolean init(OnDataReadListener listener) {
        mListener = listener;

        return true;
    }
    public abstract void readAll();

    public interface OnDataReadListener {
        public void onReadCompleted(AbstractAdapter adapter);
    }

    public void startReadinInThread() {
        ReadThread t = new ReadThread(this);
        t.start();
    }

    private class ReadThread extends Thread {
        private AbstractReader mReader;

        public ReadThread(AbstractReader reader) {
            mReader = reader;
        }

        @Override
        public void run() {
            mReader.readAll();
        }
    }
}
