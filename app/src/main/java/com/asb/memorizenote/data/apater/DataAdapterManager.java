package com.asb.memorizenote.data.apater;

import android.content.Context;
import android.util.Log;

import com.asb.memorizenote.BookListAdapter;
import com.asb.memorizenote.Constants;
import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.data.BaseBookData;
import com.asb.memorizenote.data.NameListData;
import com.asb.memorizenote.data.reader.DBReader;
import com.asb.memorizenote.data.reader.DataFileReader;
import com.asb.memorizenote.data.writer.DBWriter;
import com.asb.memorizenote.utils.MNLog;
import com.asb.memorizenote.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by azureskybox on 15. 10. 14.
 */
public class DataAdapterManager implements AbstractAdapter.OnDataLoadListener{

    private static final int INIT_START = 10;
    private static final int INIT_BOOK_LIST_FINISHED = 11;
    private static final int INIT_ITEM_LIST_FINISHED = 12;

    private static final int UPDATE_START = 20;
    private static final int IN_UPDATE_FROM_FILES = 21;
    private static final int IN_UPDATE_WRITING = 22;
    private static final int UPDATE_FINISHED = 23;

    private Context mContext = null;
    private AbstractAdapter.OnDataLoadListener mListener = null;

    private int mCurrentState = INIT_START;

    //init target data
    private int mCurInitBookIdx = 0;
    private BaseBookData mCurInitBook = null;

    //update target data
    private int mCurUpdateIdx = 0;
    private ArrayList<File> mUpdateFileList;
    private AbstractAdapter mCurUpdatedAdapter = null;

    BookListAdapter mBookListAdapter = null;
    HashMap<String, AbstractAdapter> mItemListAdapterMap;

    public DataAdapterManager(Context context) {
        mContext = context;

        mItemListAdapterMap = new HashMap<String, AbstractAdapter>();
    }

    public void initialize(AbstractAdapter.OnDataLoadListener listener) {
        mListener = listener;

        //First, create book list adapter from DB.
        DBReader dbReader = new DBReader(mContext, ReaderFlags.DB.TARGET_BOOK);
        mBookListAdapter = new BookListAdapter(mContext);
        mBookListAdapter.setListener(this);
        mBookListAdapter.setDataType(AdapterDataType.BOOK);
        mBookListAdapter.setReader(dbReader);
        mBookListAdapter.setWriter(new DBWriter(mContext));
        mBookListAdapter.readItems(null);
    }

    private void initializeItemListAdapter() {
        if(mBookListAdapter.getCount() <= mCurInitBookIdx) {
            mCurrentState = INIT_ITEM_LIST_FINISHED;
            this.onReadCompleted();
            return;
        }

        mCurInitBook = (BaseBookData)mBookListAdapter.getItem(mCurInitBookIdx);
        ++mCurInitBookIdx;

        DBReader reader = new DBReader(mContext, ReaderFlags.DB.TARGET_ITEM);
        reader.setTargetBookName(mCurInitBook.mName);
        reader.setTargetBookID(mCurInitBook.mID);

        AbstractAdapter adapter = Utils.getAdapter(mContext, mCurInitBook.mType);
        adapter.setListener(this);
        adapter.setReader(reader);
        mItemListAdapterMap.put(mCurInitBook.mName, adapter);

        adapter.readItems(null);
    }

    public void update(int updateFrom, AbstractAdapter.OnDataLoadListener listener) {
        update(updateFrom, listener, null);
    }

    public void update(int updateFrom, AbstractAdapter.OnDataLoadListener listener, ArrayList<File> fileList) {
        MNLog.d("update");

        mListener = listener;

        switch(updateFrom) {
            case AdapterManagerFlags.UPDATE_FROM_FILES:
                mCurrentState = IN_UPDATE_FROM_FILES;

                if(fileList == null) {
                    mUpdateFileList = new ArrayList<File>();

                    File dataFolder = new File(Constants.FOLDER_PATH);
                    File[] dataFileList = dataFolder.listFiles();

                    for (File dataFile : dataFileList) {
                        Log.d("MN", dataFile.getName());

                        if (Utils.isExpectedFileType(dataFile, "txt")) {
                            mUpdateFileList.add(dataFile);
                        }
                    }
                }
                else
                    mUpdateFileList = fileList;

                updateFromFiles();
                break;
            case AdapterManagerFlags.UPDATE_FROM_HTTP:
                updateFromHTTP();
                break;
        }
    }

    private void updateFromFiles() {
        Log.e("MN", "updateFromFiles, "+mUpdateFileList.size());

        if(mCurUpdateIdx >= mUpdateFileList.size()) {
            mCurrentState = UPDATE_FINISHED;

            mBookListAdapter.setUpdating(false);
            mBookListAdapter.readItems(null);

            return;
        }

        File currentFile = mUpdateFileList.get(mCurUpdateIdx);
        ++mCurUpdateIdx;

        try {
            int bookType = BookType.NONE;
            String bookName = null;
            BufferedReader reader = new BufferedReader(new FileReader(currentFile));

            String rawData;
            while((rawData = reader.readLine()) != null) {
                if(rawData.startsWith("##")) {
                    rawData = rawData.substring(2, rawData.length());
                    String[] metaDatas = rawData.split("=");

                    if(metaDatas[0].equals(Constants.MetaData.KEY_NAME)) {
                        bookName = metaDatas[1];
                    }
                    else if(metaDatas[0].equals(Constants.MetaData.KEY_TYPE)) {
                        bookType = Constants.BookType.getType(metaDatas[1]);
                    }

                    if(bookName != null && bookType != BookType.NONE)
                        break;
                }
            }

            reader.close();

            AbstractAdapter adapter = mItemListAdapterMap.get(bookName);
            if(adapter == null) {
                adapter = Utils.getAdapter(mContext, bookType);
                mItemListAdapterMap.put(bookName, adapter);
            }

            adapter.setUpdating(true);
            adapter.setListener(this);

            adapter.setReader(new DataFileReader(mContext, currentFile.getName()));
            adapter.setWriter(new DBWriter(mContext));

            mCurUpdatedAdapter = adapter;

            adapter.readItems(null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateFromHTTP() {

    }

    public BookListAdapter getBookListAdapter() {
        return mBookListAdapter;
    }

    public AbstractAdapter getItemListAdapter(String bookName) {
        return mItemListAdapterMap.get(bookName);
    }

    @Override
    public void onReadCompleted() {
        switch(mCurrentState) {
            case INIT_START:
                mCurrentState = INIT_BOOK_LIST_FINISHED;
                initializeItemListAdapter();
                break;
            case INIT_BOOK_LIST_FINISHED:
                initializeItemListAdapter();
                break;
            case IN_UPDATE_FROM_FILES:
                mCurrentState = IN_UPDATE_WRITING;
                mCurUpdatedAdapter.writeItems(null);
                break;
            case UPDATE_FINISHED:
                mCurUpdateIdx = 0;
                mUpdateFileList = new ArrayList<>();
            case INIT_ITEM_LIST_FINISHED:
                mListener.onReadCompleted();
                break;
        }
    }

    @Override
    public void onWriteCompleted() {
        switch(mCurrentState) {
            case IN_UPDATE_WRITING:
                mCurrentState = IN_UPDATE_FROM_FILES;
                updateFromFiles();
                break;
        }
    }

    @Override
    public void onLoadCompleted() {

    }
}
