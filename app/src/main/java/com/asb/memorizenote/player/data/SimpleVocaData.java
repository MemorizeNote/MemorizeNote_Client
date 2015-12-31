package com.asb.memorizenote.player.data;

import android.content.ContentValues;
import android.text.TextUtils;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.BaseItemData;
import com.asb.memorizenote.data.RawData;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class SimpleVocaData extends BaseItemData {
    public String mWord;
    public String mMeaning;
    public boolean mMarking = false;
    public String mChapterName;

    public SimpleVocaData() {
        super();

        mWord = STRING_NULL;
        mMeaning = STRING_NULL;
        mMarking = false;
        mChapterName = STRING_NULL;
    }

    @Override
    public RawData toRawData() {
        super.toRawData();

        mRawData.mRawData07 = mWord;
        mRawData.mRawData08 = mMeaning;

        return mRawData;
    }

    @Override
    public void fromRawData(RawData rawData) {
        super.fromRawData(rawData);

        mWord = (String)rawData.mRawData07;
        mMeaning = (String)rawData.mRawData08;
    }

    @Override
    public void fromRawDataOfFile(RawData rawData) {
        mWord = (String)rawData.mRawData01;
        mMeaning = "";
        if(!TextUtils.isEmpty((String)rawData.mRawData02)) {
            mMeaning += rawData.mRawData02;
        }
        if(!TextUtils.isEmpty((String)rawData.mRawData03)) {
            mMeaning += rawData.mRawData03;
        }
        if(!TextUtils.isEmpty((String)rawData.mRawData04)) {
            mMeaning += rawData.mRawData04;
        }
        if(!TextUtils.isEmpty((String)rawData.mRawData05)) {
            mMeaning += rawData.mRawData05;
        }
        if(!TextUtils.isEmpty((String)rawData.mRawData06)) {
            mMeaning += rawData.mRawData06;
        }
        if(!TextUtils.isEmpty((String)rawData.mRawData07)) {
            mMeaning += rawData.mRawData07;
        }
        if(!TextUtils.isEmpty((String)rawData.mRawData08)) {
            mMeaning += rawData.mRawData08;
        }
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = super.toContentValues();

        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_01, mWord);
        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_02, mMeaning);

        return values;
    }
}
