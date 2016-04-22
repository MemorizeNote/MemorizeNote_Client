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

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_NORMAL = 1;
    public static final int DIFFICULTY_HARD = 2;
    public static final int DIFFICULTY_VERY_HARD = 3;

    public String mWord;
    public String mMeaning;
    public boolean mMarking = false;
    public String mChapterName;
    public int mDifficulty;

    public SimpleVocaData() {
        super();

        mWord = STRING_NULL;
        mMeaning = STRING_NULL;
        mMarking = false;
        mChapterName = STRING_NULL;
        mDifficulty = DIFFICULTY_NORMAL;
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
        mMarking = (Integer)rawData.mRawData17 == 1?true:false;

        if(rawData.mRawData09 == null)
            mDifficulty = DIFFICULTY_NORMAL;
        else
            mDifficulty = Integer.parseInt((String)rawData.mRawData09);
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
        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_03, mDifficulty);

        return values;
    }
}
