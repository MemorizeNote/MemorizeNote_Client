package com.asb.memorizenote.ui.update;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.asb.memorizenote.R;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 26.
 */
public class FileUpdateAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UpdateFileData> mDataList;

    private LayoutInflater mInflater;

    public FileUpdateAdapter(Context context, ArrayList<UpdateFileData> dataList) {
        mContext = context;
        mDataList = dataList;

        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.file_update_file_list_item, null, false);
        }

        ((TextView)convertView.findViewById(R.id.file_update_file_list_item_name)).setText(mDataList.get(position).mFileName);

        ((CheckBox)convertView.findViewById(R.id.file_update_file_list_item_select)).setTag(position);
        ((CheckBox)convertView.findViewById(R.id.file_update_file_list_item_select)).setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mDataList.get((Integer)buttonView.getTag()).mSelected = true;
                else
                    mDataList.get((Integer)buttonView.getTag()).mSelected = false;
            }
        });

        return convertView;
    }
}
