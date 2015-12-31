package com.asb.memorizenote.data;

import android.content.ContentValues;

import com.asb.memorizenote.Constants;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class BaseItemData extends AbstractData {
    public int mID;
    public int mBookID;
    public int mChapterID;
    public String mBookName;
    public int mIndexInChapter;
    public int mChapterNum;
    public int mDataType;
    public String mName;

    public BaseItemData() {
        mID = ID_NULL;
        mBookName = STRING_NULL;
        mBookID = ID_NULL;
        mChapterID = ID_NULL;
        mChapterNum = COUNT_NULL;
        mDataType = INT_NULL;
        mIndexInChapter = INT_NULL;
        mName = "";
    }

    @Override
    public RawData toRawData() {
        mRawData = new RawData();

        mRawData.mRawData01 = mID;
        mRawData.mRawData02 = mBookName;
        mRawData.mRawData03 = mBookID;
        mRawData.mRawData04 = mChapterID;
        mRawData.mRawData05 = mIndexInChapter;
        mRawData.mRawData06 = mChapterNum;

        return mRawData;
    }

    @Override
    public void fromRawData(RawData rawData) {
        mID = (Integer)rawData.mRawData01;
        mBookName = (String)rawData.mRawData02;
        mBookID = (Integer)rawData.mRawData03;
        mChapterID = (Integer)rawData.mRawData04;
        mIndexInChapter = (Integer)rawData.mRawData05;
        mChapterNum = (Integer)rawData.mRawData06;
    }

    @Override
    public void fromRawDataOfFile(RawData rawData) {
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(Constants.DB.ITEM_TABLE.KEY_BOOK_ID, mBookID);
        values.put(Constants.DB.ITEM_TABLE.KEY_CHAPTER_ID, mChapterID);
        values.put(Constants.DB.ITEM_TABLE.KEY_BOOK_NAME, mBookName);
        values.put(Constants.DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER, mIndexInChapter);
        values.put(Constants.DB.ITEM_TABLE.KEY_CHAPTER, mChapterNum);

        return values;
    }
}
