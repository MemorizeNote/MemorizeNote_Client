package com.asb.memorizenote.data.reader;

import android.content.Context;

import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.data.db.MemorizeDBHelper;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public class DBReader extends AbstractReader {

    private int mReadTarget = ReaderFlags.NONE;

    protected MemorizeDBHelper mDBHelper;

    public DBReader(Context context, int target) {
        super(context);
        mReadTarget = target;
        mDBHelper = new MemorizeDBHelper(mContext);
    }

    @Override
    public void readAll() {
        if(mListener == null)
            return;

        if(mReadTarget == ReaderFlags.DB.TARGET_ITEM) {
            ArrayList<RawData> dataList = new ArrayList<RawData>();
            mDBHelper.getItemList(dataList, mTargetBooksName, mStartChapter, mLastChpter);

            mListener.onItemList(dataList);
        }
        else {
            ArrayList<RawData> dataList = new ArrayList<RawData>();
            mDBHelper.getBookList(dataList);

            mListener.onItemList(dataList);
        }

        mListener.onCompleted();
    }
}
