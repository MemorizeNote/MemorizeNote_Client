package com.asb.memorizenote.data.apater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import com.asb.memorizenote.Constants.DB;
import com.asb.memorizenote.data.SimpleVocaData;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class SimpleVocaAdapter extends AbstractAdapter {

    private static final String KEY_WORD = DB.DATA_TABLE.KEY_DATA_01;
    private static final String KEY_MEANING = DB.DATA_TABLE.KEY_DATA_02;

    public SimpleVocaAdapter(Context context) {
        super(context);
    }

    @Override
    public void readDataListFromFile(ArrayList<String> rawString) {
        for(String rawData : rawString) {
            String[] splittedData = rawData.split(";");

            SimpleVocaData data = new SimpleVocaData();
            data.mWord = splittedData[0];
            data.mMeaning = splittedData[1];

            mDataList.add(data);
        }
    }

    @Override
    public void readDataListFromJSON(ArrayList<JSONObject> rawJSON) {

    }

    @Override
    public void readDataListFromDB() {

    }

    @Override
    public void writeDataListToDB() {
        mDBHelper.updateDataNameList(this);

        for(int i=0; i<mDataList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_WORD, ((SimpleVocaData)mDataList.get(i)).mWord);
            values.put(KEY_MEANING, ((SimpleVocaData)mDataList.get(i)).mMeaning);
            mDBHelper.addData(mDataName, mDataNameId, i, mDataSetIndex, values);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
