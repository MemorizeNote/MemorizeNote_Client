package com.asb.memorizenote.player.data;

import android.content.ContentValues;
import android.text.TextUtils;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.BaseItemData;
import com.asb.memorizenote.data.RawData;

/**
 * Created by azureskybox on 16. 4. 21.
 */
public class SimpleOXQuizData extends BaseItemData {

    public String mQuestion;
    public String mExample1;
    public String mExample2;
    public int mAnswer;
    public String mChapterName;

    public SimpleOXQuizData() {
        super();

        mQuestion = STRING_NULL;
        mExample1 = STRING_NULL;
        mExample2 = STRING_NULL;
        mAnswer = -1;
    }

    @Override
    public RawData toRawData() {
        super.toRawData();

        mRawData.mRawData07 = mQuestion;
        mRawData.mRawData08 = mExample1;
        mRawData.mRawData09 = mExample2;
        mRawData.mRawData10 = mAnswer;

        return mRawData;
    }

    @Override
    public void fromRawData(RawData rawData) {
        super.fromRawData(rawData);

        mQuestion = (String)rawData.mRawData07;
        mExample1 = ((String)rawData.mRawData08);
        mExample2 = ((String)rawData.mRawData09);
        mAnswer = Integer.parseInt((String)rawData.mRawData10);
    }

    @Override
    public void fromRawDataOfFile(RawData rawData) {
        mQuestion = (String)rawData.mRawData01;
        mExample1 = ((String)rawData.mRawData02);
        mExample2 = ((String)rawData.mRawData03);
        mAnswer = Integer.parseInt((String) rawData.mRawData04);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = super.toContentValues();

        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_01, mQuestion);
        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_02, mExample1);
        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_03, mExample2);
        values.put(Constants.DB.ITEM_TABLE.KEY_DATA_04, mAnswer);


        return values;
    }

}
