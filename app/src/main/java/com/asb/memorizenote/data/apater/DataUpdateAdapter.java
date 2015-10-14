package com.asb.memorizenote.data.apater;

import android.content.ContentValues;
import android.content.Context;

import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.data.db.BookInfo;
import com.asb.memorizenote.data.db.MemorizeDBHelper;
import com.asb.memorizenote.data.reader.RawData;
import com.asb.memorizenote.player.adapter.SimpleVocaAdapter;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public class DataUpdateAdapter extends AbstractAdapter {

    private int mCurrentItemIndex = 0;
    BookInfo mInfo;

    private MemorizeDBHelper mDBHelper;

    private SimpleVocaAdapter mSimpleVocaAdpater;

    public DataUpdateAdapter(Context context) {
        super(context);

        mInfo = new BookInfo();
        mDBHelper = new MemorizeDBHelper(mContext);

        mSimpleVocaAdpater = new SimpleVocaAdapter(mContext);
    }

    @Override
    public void readItem(RawData data) {

    }

    @Override
    public void writeItem(RawData data) {

    }

    @Override
    public void onBookChanged(String bookName, int bookType, int chapter) {
        mInfo.mBookName = bookName;
        mInfo.mBookType = bookType;

        mDBHelper.updateBookList(mInfo);
        mCurrentItemIndex = 0;

        switch(mInfo.mBookType) {
            case BookType.SIMPLE_VOCA:
                mSimpleVocaAdpater.onBookChanged(mInfo.mBookName, mInfo.mBookType, mDBHelper.getCurrentChapterOfBook(mInfo.mBookName));
                break;
        }
    }

    @Override
    public void onItem(RawData data) {
        ContentValues values = new ContentValues();
        values.put(DB.ITEM_TABLE.KEY_BOOK_NAME, mInfo.mBookName);
        values.put(DB.ITEM_TABLE.KEY_CHAPTER, mInfo.mBookTotalChapter);
        values.put(DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER, mCurrentItemIndex);

        switch(mInfo.mBookType) {
            case BookType.SIMPLE_VOCA:
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
