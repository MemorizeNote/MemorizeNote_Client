package com.asb.memorizenote.data.writer;

import android.content.Context;
import android.text.TextUtils;

import com.asb.memorizenote.Constants.WriterType;
import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.data.BaseBookData;
import com.asb.memorizenote.data.BaseChapterData;
import com.asb.memorizenote.data.BaseItemData;
import com.asb.memorizenote.data.db.MemorizeDBHelper;

/**
 * Created by azureskybox on 15. 12. 31.
 */
public class DBWriter extends AbstractWriter {

    private MemorizeDBHelper mDBHelper;

    public DBWriter(Context context) {
        super(context);

        mWriterType = WriterType.DB;
        mDBHelper = new MemorizeDBHelper(mContext);
    }

    @Override
    public void flush() {
        //Write new books
        if(mBookList.size() > 0) {
            for(BaseBookData book : mBookList)
                flushBookData(book);
        }
        //Write new chapters

        //Write mew items

        if(mListener != null)
            mListener.onWriteCompleted();
    }

    private void flushBookData(BaseBookData book) {
        if(book.mID == AbstractData.ID_NULL) {
            book.mID = mDBHelper.findBook(book.mName);

            if(book.mID == -1)
                book.mID = mDBHelper.addBook(book.mName, book.mType);
        }

        book.mTotalChapter = mDBHelper.getChaptesInBook(book.mID) + book.mChapterList.size();
        mDBHelper.updateBook(book.mID, book.mName, book.mType, book.mTotalChapter);

        for(BaseChapterData chapter : book.mChapterList) {
            if(chapter.mBookID == AbstractData.ID_NULL)
                chapter.mBookID = book.mID;

            flushChapterData(chapter);
        }
    }

    private void flushChapterData(BaseChapterData chapter) {
        if(chapter.mID == AbstractData.ID_NULL) {
            if(TextUtils.isEmpty(chapter.mName))
                chapter.mName = "Chap."+mDBHelper.getCurrentChapterOfBook(chapter.mBookID);

            chapter.mID = mDBHelper.addChapter(chapter.mBookID, chapter.mName, chapter.mCount);
        }

        for(BaseItemData item : chapter.mDataList) {
            if(item.mBookID == AbstractData.ID_NULL)
                item.mBookID = chapter.mBookID;
            if(item.mChapterID == AbstractData.ID_NULL)
                item.mChapterID = chapter.mID;

            flushItemData(item);
        }
    }

    private void flushItemData(BaseItemData item) {
        if(item.mID == AbstractData.ID_NULL)
            item.mID = mDBHelper.addItem(item.toContentValues());
    }

}
