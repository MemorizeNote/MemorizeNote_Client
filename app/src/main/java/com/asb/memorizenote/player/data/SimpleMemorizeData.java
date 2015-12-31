package com.asb.memorizenote.player.data;

import android.content.ContentValues;
import android.text.TextUtils;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.BaseItemData;
import com.asb.memorizenote.data.RawData;

/**
 * Created by azureskybox on 15. 10. 17.
 */
public class SimpleMemorizeData extends BaseItemData {
    public String mTitle;
    public String mContents;
    public String mChapterName;

    public SimpleMemorizeData() {
        super();

        mTitle = STRING_NULL;
        mContents = STRING_NULL;
        mChapterName = STRING_NULL;
    }

    @Override
    public RawData toRawData() {
        super.toRawData();

        mRawData.mRawData07 = mTitle;
        mRawData.mRawData08 = mContents;

        return mRawData;
    }

    @Override
    public void fromRawData(RawData rawData) {
        super.fromRawData(rawData);

        mTitle = (String)rawData.mRawData07;
        mContents = (String)rawData.mRawData08;
    }

    @Override
    public void fromRawDataOfFile(RawData rawData) {
        mTitle = (String)rawData.mRawData01;
        mContents = "";
        if(!TextUtils.isEmpty((String) rawData.mRawData02)) {
            mContents += rawData.mRawData02;
        }
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = super.toContentValues();

        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_01, mTitle);
        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_02, mContents);

        return values;
    }
}
