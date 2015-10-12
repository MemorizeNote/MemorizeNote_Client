package com.asb.memorizenote.data.apater;

import android.content.Context;
import android.widget.BaseAdapter;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.db.MemorizeDBHelper;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public abstract class AbstractAdapter extends BaseAdapter {
    protected Context mContext;
    protected MemorizeDBHelper mDBHelper;

    protected ArrayList<AbstractData> mDataList;
    protected int mDataType;
    protected String mDataName;
    protected int mDataNameId;
    protected int mDataSetIndex;

    public AbstractAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<AbstractData>();
        mDataType = Constants.DataType.NONE;
        mDBHelper = new MemorizeDBHelper(mContext);
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

    abstract public void readDataListFromFile(ArrayList<String> rawString);
    abstract public void readDataListFromJSON(ArrayList<JSONObject> rawJSON);

    abstract public void readDataListFromDB();
    abstract public void writeDataListToDB();
}
