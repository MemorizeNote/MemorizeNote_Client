package com.asb.memorizenote.data.apater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import com.asb.memorizenote.Constants.DB;
import com.asb.memorizenote.data.SimpleVocaData;
import com.asb.memorizenote.data.reader.RawData;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class SimpleVocaAdapter extends AbstractAdapter {

    public SimpleVocaAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void onItemSetChanged(String itemSetName, int itemSetType, int itemNum) {

    }

    @Override
    public void onItem(RawData data) {

    }

    @Override
    public void onItemList(ArrayList<RawData> dataList) {

    }

    @Override
    public void onCompleted() {

    }
}
