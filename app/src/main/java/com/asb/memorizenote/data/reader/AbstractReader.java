package com.asb.memorizenote.data.reader;

import android.content.Context;

import com.asb.memorizenote.Constants;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 *
 * Read data from extra resources.
 */
public abstract class AbstractReader {
    protected Context mContext;
    protected OnDataReadListener mListener;
    public int mType = Constants.ReaderType.NONE;

    protected String mTargetBooksName = null;
    protected int mStartChapter = 0;
    protected int mLastChpter = 0;

    public AbstractReader(Context context) {
        mContext = context;
    }

    public boolean init(OnDataReadListener listener) {
        mListener = listener;
        return true;
    }

    public void setTargetBookName(String bookName) {
        mTargetBooksName = bookName;
    }

    public void setDataSetRange(int start, int last) {
        mStartChapter = start;
        mLastChpter = last;
    }

    public abstract void readAll();

    public interface OnDataReadListener {
        void onItemSetChanged(String itemSetName, int itemSetType, int itemNum);
        void onItem(RawData data);
        void onItemList(ArrayList<RawData> dataList);
        void onCompleted();
    }
}
