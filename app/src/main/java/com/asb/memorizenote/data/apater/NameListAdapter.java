package com.asb.memorizenote.data.apater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asb.memorizenote.R;
import com.asb.memorizenote.data.NameListData;
import com.asb.memorizenote.data.reader.AbstractReader;
import com.asb.memorizenote.data.reader.RawData;
import com.asb.memorizenote.data.writer.AbstractWriter;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class NameListAdapter extends AbstractAdapter {
    public NameListAdapter(Context context) {
        super(context);
    }

    public void readItems(AbstractReader reader) {
        mDataList.clear();
        super.readItems(reader);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_name_list_item, null, false);
        }

        ((TextView)convertView.findViewById(R.id.name_list_name)).setText(((NameListData) mDataList.get(position)).mName);
        ((TextView)convertView.findViewById(R.id.name_list_count)).setText("" + (((NameListData) mDataList.get(position)).mChapterNum + 1));

        return convertView;
    }

    @Override
    public void onItemSetChanged(String itemSetName, int itemSetType, int itemNum) {

    }

    @Override
    public void onItem(RawData data) {

    }

    @Override
    public void onItemList(ArrayList<RawData> dataList) {
        for(RawData data : dataList) {
            NameListData convertedData = new NameListData();
            convertedData.mName = (String)data.mRawData01;
            convertedData.mChapterNum = (int)data.mRawData02;
            convertedData.mDataType = (int)data.mRawData03;
            convertedData.mBookId = (int)data.mRawData04;
            mDataList.add(convertedData);
        }
    }

    @Override
    public void onCompleted() {
        if(mListener == null)
            return;

        mListener.onCompleted();
    }
}
