package com.asb.memorizenote.data.apater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asb.memorizenote.R;
import com.asb.memorizenote.data.NameListData;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class NameListAdapter extends AbstractAdapter {
    public NameListAdapter(Context context) {
        super(context);
    }

    @Override
    public void readDataListFromDB() {
        mDataList.clear();

        mDBHelper.getDataNameList(mDataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_name_list_item, null, false);
        }

        ((TextView)convertView.findViewById(R.id.name_list_name)).setText(((NameListData)mDataList.get(position)).mName);
        ((TextView)convertView.findViewById(R.id.name_list_count)).setText(""+(((NameListData)mDataList.get(position)).mDataSetCnt+1));

        return convertView;
    }

    @Override
    public void readDataListFromFile(ArrayList<String> rawString) {}

    @Override
    public void readDataListFromJSON(ArrayList<JSONObject> rawJSON) {}

    @Override
    public void writeDataListToDB() {}
}
