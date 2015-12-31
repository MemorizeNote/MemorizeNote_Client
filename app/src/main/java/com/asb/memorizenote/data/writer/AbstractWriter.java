package com.asb.memorizenote.data.writer;

import android.content.Context;

import com.asb.memorizenote.Constants.WriterType;
import com.asb.memorizenote.data.BaseBookData;
import com.asb.memorizenote.data.BaseChapterData;
import com.asb.memorizenote.data.BaseItemData;
import com.asb.memorizenote.data.RawData;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public abstract class AbstractWriter {

    protected Context mContext;
    WriterType mWriterType;
    OnDataWriteListener mListener;

    ArrayList<BaseBookData> mBookList;
    ArrayList<BaseChapterData> mChapterList;
    ArrayList<BaseItemData> mItemList;

    public AbstractWriter(Context context) {
        mContext = context;

        mBookList = new ArrayList<>();
        mChapterList = new ArrayList<>();
        mItemList = new ArrayList<>();
    }

    public void setListener(OnDataWriteListener listener) {
        mListener = listener;
    }

    public WriterType getWriterType() {
        return mWriterType;
    }

    public void writeBook(BaseBookData book) {
        mBookList.add(book);
    }
    public void writeBooks(ArrayList<BaseBookData> books) {
        mBookList.addAll(books);
    }
    public void writeChapter(BaseChapterData chapter) {
        mChapterList.add(chapter);
    }
    public void writeChapters(ArrayList<BaseChapterData> chapters) {
        mChapterList.addAll(chapters);
    }
    public void writeItem(BaseItemData item) {
        mItemList.add(item);
    }
    public void writeItems(ArrayList<BaseItemData> items) {
        mItemList.addAll(items);
    }

    abstract public void flush();

    public interface OnDataWriteListener {
        void onWriteCompleted();
    }
}
