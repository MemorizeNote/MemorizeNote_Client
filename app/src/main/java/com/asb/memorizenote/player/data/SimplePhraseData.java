package com.asb.memorizenote.player.data;

import android.content.ContentValues;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.BaseItemData;
import com.asb.memorizenote.data.RawData;

/**
 * Created by azureskybox on 16. 4. 21.
 */
public class SimplePhraseData extends BaseItemData {

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_NORMAL = 1;
    public static final int DIFFICULTY_HARD = 2;
    public static final int DIFFICULTY_VERY_HARD = 3;

    public String mPhrase;
    public String mSpelling;
    public String mMeaning;
    public String mChapterName;
    public int mDifficulty;

    public SimplePhraseData() {
        super();

        mPhrase = STRING_NULL;
        mSpelling = STRING_NULL;
        mMeaning = STRING_NULL;
        mDifficulty = DIFFICULTY_NORMAL;
    }

    @Override
    public RawData toRawData() {
        super.toRawData();

        mRawData.mRawData07 = mPhrase;
        mRawData.mRawData08 = mSpelling;
        mRawData.mRawData09 = mMeaning;

        return mRawData;
    }

    @Override
    public void fromRawData(RawData rawData) {
        super.fromRawData(rawData);

        mPhrase = (String)rawData.mRawData07;
        mSpelling = ((String)rawData.mRawData08);
        mMeaning = ((String)rawData.mRawData09);

        if(rawData.mRawData10 == null)
            mDifficulty = DIFFICULTY_NORMAL;
        else
            mDifficulty = ((Integer)rawData.mRawData10);
    }

    @Override
    public void fromRawDataOfFile(RawData rawData) {
        mPhrase = (String)rawData.mRawData01;
        mSpelling = ((String)rawData.mRawData03)
                + "/" + ((String)rawData.mRawData04)
                + "/" + ((String)rawData.mRawData05)
                + "/" + ((String)rawData.mRawData06);

        mMeaning = ((String)rawData.mRawData02) + "\n" + ((String)rawData.mRawData07);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = super.toContentValues();

        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_01, mPhrase);
        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_02, mSpelling);
        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_03, mMeaning);

        return values;
    }
}
