package com.asb.memorizenote.player.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.data.BaseBookData;
import com.asb.memorizenote.data.BaseChapterData;
import com.asb.memorizenote.data.RawData;
import com.asb.memorizenote.data.db.MemorizeDBHelper;
import com.asb.memorizenote.player.data.SimpleVocaData;
import com.asb.memorizenote.utils.MNLog;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class SimpleVocaAdapter extends AbstractPlayerAdapter {

    BaseBookData mUpdatedBook;
    MemorizeDBHelper mDBHelper;

    public SimpleVocaAdapter(Context context) {
        super(context);
        mDBHelper = new MemorizeDBHelper(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void onBookChanged(RawData data) {
        MNLog.d("onBookChanged");

        BaseBookData book = new BaseBookData();
        if(mIsUpdating) {
            book.fromRawData(data);

            mUpdatedBook = new BaseBookData();
            mUpdatedBook.mName = book.mName;
            mUpdatedBook.mType = book.mType;

            mCurrentWriter.writeBook(mUpdatedBook);
        }
        else {
            book.fromRawData(data);

            mBookList.add(book);
        }
    }

    @Override
    public void onChapterChanged(RawData data) {
        MNLog.d("onChapterChanged");

        ++mTotalChapter;

        BaseChapterData chapter = new BaseChapterData();
        chapter.fromRawData(data);

        if(mIsUpdating) {
            ++mUpdatedBook.mTotalChapter;
            mUpdatedBook.mChapterList.add(chapter);
        }
        else {
            mBookList.get(mBookList.size() - 1).mChapterList.add(chapter);
            mChapterList.add(chapter);

            mTotalItemPerChapter.add(chapter.mCount);
        }
    }

    @Override
    public void onItemList(ArrayList<RawData> dataList) {
        MNLog.d("onItemList, data size=" + dataList.size() + ", reader type=" + mCurrentReader.getReaderType());

        //DB로 부터 데이터를 읽어들이는 과정
        if(mCurrentReader.getReaderType() == Constants.ReaderType.DB) {
            mTotalItem += dataList.size();

            for(RawData data : dataList) {
                SimpleVocaData convertedData = new SimpleVocaData();
                convertedData.fromRawData(data);
                convertedData.mChapterName = mChapterList.get(mChapterList.size()-1).mName;

                mItemList.add(convertedData);

                mChapterList.get(mChapterList.size()-1).mDataList.add(convertedData);
            }
        }
        //파일 및 네트워크로부터 데이터를 업데이트 하는 과정
        else if(mCurrentReader.getReaderType() == Constants.ReaderType.FILE){
            mTotalItem += dataList.size();
            mTotalUpdatedItem += dataList.size();

            int indexInChapter = 0;
            for(RawData data : dataList) {

                SimpleVocaData convertedData = new SimpleVocaData();
                convertedData.mIndexInChapter = indexInChapter;
                convertedData.fromRawDataOfFile(data);
                convertedData.mChapterName = mUpdatedBook.mChapterList.get(mUpdatedBook.mChapterList.size()-1).mName;

                mItemList.add(convertedData);

                mUpdatedBook.mChapterList.get(mUpdatedBook.mChapterList.size()-1).mDataList.add(convertedData);
                ++mUpdatedBook.mChapterList.get(mUpdatedBook.mChapterList.size()-1).mCount;

                ++indexInChapter;
            }
        }
    }

    public void save() {
        for(AbstractData data  : mItemList) {
            mDBHelper.setItemMarking(((SimpleVocaData)data).mID, ((SimpleVocaData)data).mMarking);
        }
    }

    public void write(SimpleVocaData data) {
        mDBHelper.updateItem(data);
    }

    @Override
    public void onReadCompleted() {
        if(mListener != null)
            mListener.onReadCompleted();
    }

    @Override
    public void onWriteCompleted() {
        if(mListener != null)
            mListener.onWriteCompleted();

        mBookList.add(mUpdatedBook);
    }
}
