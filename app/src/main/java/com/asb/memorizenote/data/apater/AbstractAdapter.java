package com.asb.memorizenote.data.apater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.data.db.MemorizeDBHelper;
import com.asb.memorizenote.data.reader.AbstractReader;
import com.asb.memorizenote.data.reader.RawData;
import com.asb.memorizenote.data.writer.AbstractWriter;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public abstract class AbstractAdapter extends BaseAdapter implements AbstractReader.OnDataReadListener {
    protected Context mContext;

    protected OnDataLoadListener mListener;

    protected ArrayList<AbstractData> mDataList;
    protected int mDataType;
    protected String mDataName;
    protected int mDataNameId;
    protected int mDataSetIndex;

    public AbstractAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<AbstractData>();
        mDataType = Constants.DataType.NONE;
    }

    public void setListener(OnDataLoadListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }
    @Override
    public Object getItem(int position) {
        if(mDataList.size() == 0)
            return null;
        else
            return mDataList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public int getDataType() {
        return mDataType;
    }
    public void setDataType(int dataType) {
        mDataType = dataType;
    }

    public String getDataName() {
        return mDataName;
    }
    public void setDataName(String name) {
        mDataName = name;
    }

    public int getmDataNameId() {
        return mDataNameId;
    }
    public void setmDataNameId(int dataNameId) {
        this.mDataNameId = dataNameId;
    }

    public int getmDataSetIndex() {
        return mDataSetIndex;
    }
    public void setmDataSetIndex(int dataSetIndex) {
        this.mDataSetIndex = dataSetIndex;
    }

    public ArrayList<AbstractData> getDataList() {
        return mDataList;
    }

    public void readItems(AbstractReader reader) {
        reader.init(this);
        reader.readAll();
    }
    abstract public void readItem(RawData data);

    public void writeItems(AbstractWriter writer) {

    }
    abstract public void writeItem(RawData data);

    public interface OnDataLoadListener {
        void onCompleted();
    }
}
