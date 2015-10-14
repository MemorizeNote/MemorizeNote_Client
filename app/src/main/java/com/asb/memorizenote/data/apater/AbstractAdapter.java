package com.asb.memorizenote.data.apater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.AbstractData;
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

    protected ArrayList<AbstractData> mItemList;
    protected int mBookType;
    protected String mBookName;
    protected int mBookId;
    protected int mDataSetIndex;

    protected AbstractReader mCurrentReader = null;

    protected boolean mIsUpdating = false;

    public AbstractAdapter(Context context) {
        mContext = context;
        mItemList = new ArrayList<AbstractData>();
        mBookType = Constants.BookType.NONE;
    }

    public void setListener(OnDataLoadListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }
    @Override
    public Object getItem(int position) {
        if(mItemList.size() == 0)
            return null;
        else
            return mItemList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public int getBookType() {
        return mBookType;
    }
    public void setBookType(int dataType) {
        mBookType = dataType;
    }

    public String getBookName() {
        return mBookName;
    }
    public void setBookName(String name) {
        mBookName = name;
    }

    public int getBookId() {
        return mBookId;
    }
    public void setBookId(int dataNameId) {
        this.mBookId = dataNameId;
    }

    public int getmDataSetIndex() {
        return mDataSetIndex;
    }
    public void setmDataSetIndex(int dataSetIndex) {
        this.mDataSetIndex = dataSetIndex;
    }

    public ArrayList<AbstractData> getDataList() {
        return mItemList;
    }

    public void setUpdating(boolean updating) {
        mIsUpdating = updating;
    }

    public void readItems(AbstractReader reader) {
        if(!mIsUpdating)
            mItemList = new ArrayList<>();

        mCurrentReader = reader;
        mCurrentReader.init(this);
        mCurrentReader.readAll();
    }
    abstract public void readItem(RawData data);

    public void writeItems(AbstractWriter writer) {

    }
    abstract public void writeItem(RawData data);

    public interface OnDataLoadListener {
        void onCompleted();
    }
}
