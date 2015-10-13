package com.asb.memorizenote.player.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.asb.memorizenote.data.SimpleVocaData;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.data.reader.RawData;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class SimpleVocaAdapter extends AbstractAdapter {

    private int mTotalItem = 0;
    private int mTotalCahpter = 0;
    private int[] mTotalItemPerChapter;

    private int mCurItem = 0;
    private int mCurChapter = 0;
    private int mCurItemInChapter = 0;

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
        mTotalItem = dataList.size();
        mTotalCahpter = (int)dataList.get(dataList.size()-1).mRawData02+1;
        mTotalItemPerChapter = new int[mTotalCahpter];
        for(int i=0; i<mTotalCahpter; i++)
            mTotalItemPerChapter[i] = 0;

        int curChapter = 0;
        for(RawData data : dataList) {
            if(data.mRawData02 != curChapter)
                ++curChapter;

            ++mTotalItemPerChapter[curChapter];

            SimpleVocaData convertedData = new SimpleVocaData();
            convertedData.mWord = (String)data.mRawData04;
            convertedData.mMeaning = (String)data.mRawData05;

            mDataList.add(convertedData);
        }
    }

    @Override
    public void onCompleted() {
        if(mListener == null)
            return;

        mListener.onCompleted();
    }

    public SimpleVocaData first() {
        mCurChapter = 0;
        mCurItem = 0;
        mCurItemInChapter = 0;
        
        return (SimpleVocaData)mDataList.get(mCurItem);
    }

    public SimpleVocaData next() {
        ++mCurItem;

        if(mCurItem < mTotalItem)
            return (SimpleVocaData)mDataList.get(mCurItem);
        else {
            --mCurItem;
            return null;
        }
    }

    public SimpleVocaData previous() {
        --mCurItem;

        if(mCurItem >= 0)
            return (SimpleVocaData)mDataList.get(mCurItem);
        else {
            mCurItem = 0;
            return null;
        }
    }
}
