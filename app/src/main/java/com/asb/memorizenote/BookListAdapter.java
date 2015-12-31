package com.asb.memorizenote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asb.memorizenote.data.BaseBookData;
import com.asb.memorizenote.data.RawData;
import com.asb.memorizenote.data.apater.AbstractAdapter;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class BookListAdapter extends AbstractAdapter {
    public BookListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_name_list_item, null, false);
        }

        ((TextView)convertView.findViewById(R.id.name_list_name)).setText((mBookList.get(position)).mName);
        ((TextView)convertView.findViewById(R.id.name_list_count)).setText("" + ((mBookList.get(position)).mTotalChapter));

        return convertView;
    }

    @Override
    public void onBookChanged(RawData data) {

    }

    @Override
    public void onChapterChanged(RawData data) {

    }

    @Override
    public void onItemList(ArrayList<RawData> dataList) {
        for(RawData data : dataList) {
            BaseBookData convertedData = new BaseBookData();
            convertedData.fromRawData(data);
            mBookList.add(convertedData);
        }
    }

    @Override
    public void onReadCompleted() {
        if(mListener == null)
            return;

        mListener.onReadCompleted();
    }

    @Override
    public void onWriteCompleted() {

    }
}
