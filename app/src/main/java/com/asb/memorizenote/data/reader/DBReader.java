package com.asb.memorizenote.data.reader;

import android.content.Context;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.data.BaseBookData;
import com.asb.memorizenote.data.BaseChapterData;
import com.asb.memorizenote.data.RawData;
import com.asb.memorizenote.data.db.MemorizeDBHelper;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public class DBReader extends AbstractReader {

    private int mReadTarget = ReaderFlags.NONE;

    protected MemorizeDBHelper mDBHelper;

    public DBReader(Context context, int target) {
        super(context);

        mType = Constants.ReaderType.DB;

        mReadTarget = target;
        mDBHelper = new MemorizeDBHelper(mContext);
    }

    @Override
    public void readAll() {
        if(mListener == null)
            return;

        if(mReadTarget == ReaderFlags.DB.TARGET_ITEM) {
            ArrayList<RawData> bookList = new ArrayList<>();
            ArrayList<RawData> chapterList = new ArrayList<>();
            bookList = mDBHelper.getBookList(mTargetBookID);

            for(RawData book : bookList) {
                BaseBookData bookData = new BaseBookData();
                bookData.fromRawData(book);

                mListener.onBookChanged(book);

                chapterList = mDBHelper.getChapterList(bookData.mID);

                for(RawData chapter : chapterList) {
                    BaseChapterData chapterData = new BaseChapterData();
                    chapterData.fromRawData(chapter);

                    mListener.onChapterChanged(chapter);

                    mListener.onItemList(mDBHelper.getItemList(chapterData.mBookID, chapterData.mID));
                }
            }
        }
        else
            mListener.onItemList(mDBHelper.getBookList());

        mListener.onReadCompleted();
    }
}
