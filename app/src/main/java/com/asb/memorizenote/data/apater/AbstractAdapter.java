package com.asb.memorizenote.data.apater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.asb.memorizenote.Constants.AdapterDataType;
import com.asb.memorizenote.data.BaseBookData;
import com.asb.memorizenote.data.BaseChapterData;
import com.asb.memorizenote.data.BaseItemData;
import com.asb.memorizenote.data.reader.AbstractReader;
import com.asb.memorizenote.data.writer.AbstractWriter;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public abstract class AbstractAdapter extends BaseAdapter implements AbstractReader.OnDataReadListener, AbstractWriter.OnDataWriteListener {
    protected Context mContext;

    protected OnDataLoadListener mListener;

    AdapterDataType mDataType;
    protected ArrayList<BaseBookData> mBookList;
    protected ArrayList<BaseChapterData> mChapterList;
    protected ArrayList<BaseItemData> mItemList;

    protected AbstractReader mCurrentReader = null;
    protected AbstractWriter mCurrentWriter = null;

    protected boolean mIsUpdating = false;

    public AbstractAdapter(Context context) {
        mContext = context;

        mDataType = AdapterDataType.ITEM;
        mChapterList = new ArrayList<>();
        mItemList = new ArrayList<>();
        mBookList = new ArrayList<>();
    }

    public void setListener(OnDataLoadListener listener) {
        mListener = listener;
    }

    public void setDataType(AdapterDataType dataType) {
        mDataType = dataType;
    }

    @Override
    public int getCount() {
        switch(mDataType) {
            case BOOK:
                return mBookList.size();
            case CHAPTER:
                return mChapterList.size();
            case ITEM:
                return mItemList.size();
            default:
                return 0;
        }
    }
    @Override
    public Object getItem(int position) {
        switch(mDataType) {
            case BOOK:
                return mBookList.get(position);
            case CHAPTER:
                return mChapterList.get(position);
            case ITEM:
                return mItemList.get(position);
            default:
                return null;
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void setUpdating(boolean updating) {
        mIsUpdating = updating;
    }

    public void setReader(AbstractReader reader) {
        mCurrentReader = reader;
    }
    public void setWriter(AbstractWriter writer) {
        mCurrentWriter = writer;
    }


    public void readItems(AbstractReader reader) {
        if(!mIsUpdating) {
            switch (mDataType) {
                case BOOK:
                    mBookList = new ArrayList<>();
                    break;
                case CHAPTER:
                    mChapterList = new ArrayList<>();
                    break;
                case ITEM:
                    mItemList = new ArrayList<>();
                    break;

            }
        }

        if(reader != null)
            mCurrentReader = reader;

        mCurrentReader.init(this);
        mCurrentReader.readAll();
    }

    public void writeItems(AbstractWriter writer) {
        if(writer != null)
            mCurrentWriter = writer;

        mCurrentWriter.setListener(this);
        mCurrentWriter.flush();
    }

    public interface OnDataLoadListener {
        void onReadCompleted();
        void onWriteCompleted();
        void onLoadCompleted();
    }
}
