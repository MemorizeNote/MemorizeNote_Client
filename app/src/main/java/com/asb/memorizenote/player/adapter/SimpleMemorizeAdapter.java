package com.asb.memorizenote.player.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.SimpleMemorizeData;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.data.db.BookInfo;
import com.asb.memorizenote.data.db.MemorizeDBHelper;
import com.asb.memorizenote.data.reader.RawData;
import com.asb.memorizenote.data.writer.AbstractWriter;
import com.asb.memorizenote.utils.MNLog;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 17.
 */
public class SimpleMemorizeAdapter extends AbstractAdapter {

    private MemorizeDBHelper mDBHelper;

    private int mTotalItem = 0;
    private int mTotalUpdatedItem = 0;
    private int mTotalChapter = 0;
    private ArrayList<Integer> mTotalItemPerChapter;

    private int mCurItem = 0;
    private int mCurChapter = 0;
    private int mCurItemInChapter = 0;

    public SimpleMemorizeAdapter(Context context) {
        super(context);

        mDBHelper = new MemorizeDBHelper(mContext);
        mTotalItemPerChapter = new ArrayList<>();
    }

    @Override
    public void readItem(RawData data) {

    }

    @Override
    public void writeItem(RawData data) {

    }

    @Override
    public void writeItems(AbstractWriter writer) {
        Log.d("MN", "writeItems, " + mTotalItem + ", " + mTotalUpdatedItem);

        int startIdx = mTotalItem - mTotalUpdatedItem;

        for(int i=startIdx; i<mTotalItem; i++) {
            SimpleMemorizeData data = (SimpleMemorizeData)mItemList.get(i);

            ContentValues values = new ContentValues();
            values.put(Constants.DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER, data.mIndexInChapter);
            values.put(Constants.DB.ITEM_TABLE.KEY_BOOK_NAME, data.mName);
            values.put(Constants.DB.ITEM_TABLE.KEY_CHAPTER, data.mChapterNum);
            values.put(Constants.DB.ITEM_TABLE.KEY_DATA_01, data.mTitle);
            values.put(Constants.DB.ITEM_TABLE.KEY_DATA_02, data.mContents);

            mDBHelper.addItem(values);
        }

        mTotalUpdatedItem = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void onBookChanged(String bookName, int bookType, int chapter) {
        mBookName = bookName;
        mBookType = bookType;

        BookInfo info = new BookInfo();
        info.mBookName = mBookName;
        info.mBookType = mBookType;
        mDBHelper.updateBookList(info);
        mCurChapter = info.mBookTotalChapter;

        mCurItem = 0;
    }

    @Override
    public void onItem(RawData data) {
    }

    @Override
    public void onItemList(ArrayList<RawData> dataList) {
        if(mCurrentReader.getType() == Constants.ReaderType.DB) {
            readItemsWithDBReader(dataList);
        }
        else {
            readItemsWithFileReader(dataList);
        }
    }

    @Override
    public void onCompleted() {
        if(mListener == null)
            return;

        mListener.onCompleted();
    }

    public SimpleMemorizeData first() {
        mCurChapter = 0;
        mCurItem = 0;
        mCurItemInChapter = 0;

        return (SimpleMemorizeData) mItemList.get(mCurItem);
    }

    public SimpleMemorizeData next() {
        ++mCurItem;
        ++mCurItemInChapter;

        MNLog.d("next, mCurItem=" + mCurItem + ", mCurItemInChapter=" + mCurItemInChapter + "mCurChapter=" + mCurChapter + ", totalItemInChapter=" + mTotalItemPerChapter.get(mCurChapter));

        if(mCurItemInChapter < mTotalItemPerChapter.get(mCurChapter))
            return (SimpleMemorizeData) mItemList.get(mCurItem);
        else {
            if(mCurChapter < mTotalChapter-1) {
                ++mCurChapter;
                mCurItemInChapter = 0;

                return (SimpleMemorizeData) mItemList.get(mCurItem);
            }
            else {
                --mCurItem;
                --mCurItemInChapter;
                return null;
            }
        }
    }

    public SimpleMemorizeData previous() {
        --mCurItem;
        --mCurItemInChapter;

        MNLog.d("previous, mCurItem="+mCurItem+", mCurItemInChapter="+mCurItemInChapter+"mCurChapter="+mCurChapter+", totalItemInChapter="+mTotalItemPerChapter.get(mCurChapter));

        if(mCurItemInChapter >= 0)
            return (SimpleMemorizeData) mItemList.get(mCurItem);
        else {
            if(mCurChapter > 0) {
                --mCurChapter;
                mCurItemInChapter = mTotalItemPerChapter.get(mCurChapter)-1;

                return (SimpleMemorizeData) mItemList.get(mCurItem);
            }
            else {
                mCurItem = 0;
                mCurItemInChapter = 0;
                return null;
            }
        }
    }

    public int currentPosition() {
        return mCurItem;
    }

    private void readItemsWithDBReader(ArrayList<RawData> dataList) {
        mTotalItem = dataList.size();
        mTotalChapter = (int)dataList.get(dataList.size()-1).mRawData02;

        MNLog.d("readItemsWithDBReader, mTotalItem="+mTotalItem+", mTotalChapter="+mTotalChapter);

        int curChapter = 1;
        int itemsInChapter = 0;
        for(RawData data : dataList) {
            if(data.mRawData02 != curChapter) {
                mTotalItemPerChapter.add(itemsInChapter);
                itemsInChapter = 0;
                ++curChapter;
            }

            ++itemsInChapter;

            SimpleMemorizeData convertedData = new SimpleMemorizeData();
            convertedData.mName = mBookName;
            convertedData.mChapterNum = (int)data.mRawData02;
            convertedData.mIndexInChapter = (int)data.mRawData03;
            convertedData.mTitle = (String)data.mRawData04;
            convertedData.mContents = (String)data.mRawData05;

            mItemList.add(convertedData);
        }

        //Set item count of last chapter in list.
        mTotalItemPerChapter.add(itemsInChapter);

        for(int items : mTotalItemPerChapter)
            MNLog.d(""+items);
    }

    private void readItemsWithFileReader(ArrayList<RawData> dataList) {
        mTotalItem += dataList.size();
        mTotalUpdatedItem += dataList.size();
        ++mTotalChapter;

        int indexInChapter = 0;
        for(RawData data : dataList) {

            SimpleMemorizeData convertedData = new SimpleMemorizeData();
            convertedData.mTitle = (String)data.mRawData01;

            convertedData.mContents = "";
            String[] splitString = ((String)data.mRawData02).split("@");
            int listSize = splitString.length;
            int idx;
            for(idx=0; idx<listSize-1; idx++)
                convertedData.mContents += splitString[idx] + "\n";

            convertedData.mContents += splitString[idx];

            convertedData.mName = mBookName;
            convertedData.mChapterNum = mTotalChapter;
            convertedData.mIndexInChapter = indexInChapter++;

            mItemList.add(convertedData);
        }
    }
}
