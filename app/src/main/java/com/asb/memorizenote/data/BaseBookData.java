package com.asb.memorizenote.data;

import com.asb.memorizenote.Constants;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 12. 31.
 */
public class BaseBookData extends AbstractData {
    public int mID;
    public String mName;
    public int mTotalChapter;
    public int mType;
    public ArrayList<BaseChapterData> mChapterList;

    public BaseBookData() {
        mID = ID_NULL;
        mName = STRING_NULL;
        mTotalChapter  = COUNT_NULL;
        mType = Constants.BookType.NONE;
        mChapterList = new ArrayList<>();
    }

    @Override
    public RawData toRawData() {
        mRawData = new RawData();

        mRawData.mRawData01 = mID;
        mRawData.mRawData02 = mName;
        mRawData.mRawData03 = mTotalChapter;
        mRawData.mRawData04 = mType;

        return mRawData;
    }

    @Override
    public void fromRawData(RawData rawData) {
        mID = (Integer)rawData.mRawData01;
        mName = (String)rawData.mRawData02;
        mTotalChapter = (Integer)rawData.mRawData03;
        mType = (Integer)rawData.mRawData04;
    }

    @Override
    public void fromRawDataOfFile(RawData rawData) {
        mName = (String)rawData.mRawData01;
    }
}
