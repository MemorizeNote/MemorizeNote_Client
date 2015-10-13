package com.asb.memorizenote.data.apater;

import android.content.ContentValues;
import android.content.Context;

import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.data.db.BookInfo;
import com.asb.memorizenote.data.db.MemorizeDBHelper;
import com.asb.memorizenote.data.reader.RawData;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public class DataUpdateAdapter extends AbstractAdapter {

    private int mCurrentItemIndex = 0;
    BookInfo mInfo;

    private MemorizeDBHelper mDBHelper;

    public DataUpdateAdapter(Context context) {
        super(context);

        mInfo = new BookInfo();
        mDBHelper = new MemorizeDBHelper(mContext);
    }

    @Override
    public void onItemSetChanged(String itemSetName, int itemSetType, int itemNum) {
        mInfo.mBookName = itemSetName;
        mInfo.mBookType = itemSetType;

        mDBHelper.updateBookList(mInfo);

        mCurrentItemIndex = 0;
    }

    @Override
    public void onItem(RawData data) {
        ContentValues values = new ContentValues();
        values.put(DB.ITEM_TABLE.KEY_BOOK_NAME, mInfo.mBookName);
        values.put(DB.ITEM_TABLE.KEY_CHAPTER, mInfo.mBookTotalChapter);
        values.put(DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER, mCurrentItemIndex);

        switch(mInfo.mBookType) {
            case DataType.SIMPLE_VOCA:
                values.put(DB.ITEM_TABLE.KEY_DATA_01, (String)data.mRawData01);
                values.put(DB.ITEM_TABLE.KEY_DATA_02, (String)data.mRawData02);
                break;
        }

        mDBHelper.addItem(values);
        ++mCurrentItemIndex;
    }

    @Override
    public void onItemList(ArrayList<RawData> dataList) {

    }

    @Override
    public void onCompleted() {
        if(mListener == null)
            return;

        mListener.onCompleted();
    }
}
