package com.asb.memorizenote.data;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 12. 30.
 */
public class BaseChapterData extends AbstractData {

    public int mID;
    public int mBookID;
    public String mName;
    public int mCount;
    public String mBookName;
    public ArrayList<BaseItemData> mDataList;
    public ArrayList<RawData> mRawDataList;

    public BaseChapterData() {
        mID = ID_NULL;
        mBookID = ID_NULL;
        mName = STRING_NULL;
        mBookName = STRING_NULL;
        mCount = COUNT_NULL;
        mDataList = new ArrayList<>();
        mRawDataList =  new ArrayList<>();
    }

    @Override
    public RawData toRawData() {
        mRawData = new RawData();

        mRawData.mRawData01 = mID;
        mRawData.mRawData02 = mBookID;
        mRawData.mRawData03 = mName;
        mRawData.mRawData04 = mCount;

        return mRawData;
    }

    @Override
    public void fromRawData(RawData rawData) {
        mID = (Integer)rawData.mRawData01;
        mBookID = (Integer)rawData.mRawData02;
        mName = (String)rawData.mRawData03;
        mCount = (Integer)rawData.mRawData04;
    }

    @Override
    public void fromRawDataOfFile(RawData rawData) {

    }
}
