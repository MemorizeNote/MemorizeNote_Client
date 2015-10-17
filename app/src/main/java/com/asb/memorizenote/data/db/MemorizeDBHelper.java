package com.asb.memorizenote.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.asb.memorizenote.Constants.DB;
import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.data.NameListData;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.data.reader.RawData;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class MemorizeDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = DB.VERSION;
    private static final String DB_NAME = DB.NAME;
    private static final String BOOK_TABLE = DB.BOOK_TABLE.NAME;
    private static final String ITEM_TABLE = DB.ITEM_TABLE.NAME;

    public MemorizeDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BOOK_TABLE + "("
                + DB.BOOK_TABLE.KEY_ID + " INTEGER PRIMARY KEY,"
                + DB.BOOK_TABLE.KEY_NAME + " TEXT,"
                + DB.BOOK_TABLE.KEY_COUNT + " INTEGER,"
                + DB.BOOK_TABLE.KEY_TYPE + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + ITEM_TABLE + "("
                + DB.ITEM_TABLE.KEY_ID + " INTEGER PRIMARY KEY, "
                + DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER + " INTEGER, "
                + DB.ITEM_TABLE.KEY_CHAPTER + " INTEGER, "
                + DB.ITEM_TABLE.KEY_BOOK_NAME + " TEXT,"
                + DB.ITEM_TABLE.KEY_DATA_01 + " TEXT, "
                + DB.ITEM_TABLE.KEY_DATA_02 + " TEXT, "
                + DB.ITEM_TABLE.KEY_DATA_03 + " TEXT, "
                + DB.ITEM_TABLE.KEY_DATA_04 + " TEXT, "
                + DB.ITEM_TABLE.KEY_DATA_05 + " TEXT, "
                + DB.ITEM_TABLE.KEY_DATA_06 + " TEXT, "
                + DB.ITEM_TABLE.KEY_DATA_07 + " TEXT, "
                + DB.ITEM_TABLE.KEY_DATA_08 + " TEXT, "
                + DB.ITEM_TABLE.KEY_DATA_09 + " TEXT, "
                + DB.ITEM_TABLE.KEY_DATA_10 + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+BOOK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ITEM_TABLE);

        onCreate(db);
    }

    public int getCurrentChapterOfBook(String bookName) {
        int currentChapter = -1;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(BOOK_TABLE, null, DB.BOOK_TABLE.KEY_NAME + "=?", new String[]{bookName}, null, null, null, null);
        if(cursor.moveToFirst()) {
            currentChapter = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_COUNT));
        }

        cursor.close();
        db.close();

        return currentChapter;
    }

    public int getNextChapterOfBook(String bookName) {
        int lastChapter = 0;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(BOOK_TABLE, null, DB.BOOK_TABLE.KEY_NAME + "=?", new String[]{bookName}, null, null, null, null);
        if(cursor.moveToFirst()) {
            lastChapter = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_COUNT));
            ++lastChapter;
        }

        cursor.close();
        db.close();

        return lastChapter;
    }

    public void getBookList(ArrayList<RawData> dataList) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BOOK_TABLE, null, null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                RawData data = new RawData();
                data.mRawData01 = cursor.getString(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_NAME));
                data.mRawData02 = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_COUNT));
                data.mRawData03 = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_TYPE));
                data.mRawData04 = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_ID));
                dataList.add(data);
            } while (cursor.moveToNext());
        }
    }

    public void updateBookList(BookInfo info) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(BOOK_TABLE, null, DB.BOOK_TABLE.KEY_NAME + "=?", new String[]{info.mBookName}, null, null, null, null);
        if(cursor.moveToFirst()) {
            int dataSetCnt = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_COUNT));
            cursor.close();
            db.close();

            ++dataSetCnt;

            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DB.BOOK_TABLE.KEY_COUNT, dataSetCnt);
            db.update(BOOK_TABLE, values, DB.BOOK_TABLE.KEY_NAME + "=?", new String[]{info.mBookName});

            db.close();

            info.mBookTotalChapter = dataSetCnt;
            return;
        }

        cursor.close();
        db.close();

        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB.BOOK_TABLE.KEY_NAME, info.mBookName);
        values.put(DB.BOOK_TABLE.KEY_COUNT, 0);
        values.put(DB.BOOK_TABLE.KEY_TYPE, info.mBookType);
        db.insert(BOOK_TABLE, null, values);

        db.close();

        info.mBookTotalChapter = 0;
    }

    public void addItem(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();

        db.insert(ITEM_TABLE, null, values);

        db.close();
    }

    public void getItemList(ArrayList<RawData> dataList, String bookame, int first, int last) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ITEM_TABLE, null,
                DB.ITEM_TABLE.KEY_BOOK_NAME + "=?", new String[]{bookame},
                null, null,
                DB.ITEM_TABLE.KEY_CHAPTER+" ASC",
                null);
        if(cursor.moveToFirst()) {
            do {
                RawData data = new RawData();
                data.mRawData01 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_BOOK_NAME));
                data.mRawData02 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_CHAPTER));
                data.mRawData03 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER));
                data.mRawData04 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_01));
                data.mRawData05 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_02));
                data.mRawData06 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_03));
                data.mRawData07 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_04));
                data.mRawData08 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_05));
                data.mRawData09 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_06));
                data.mRawData10 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_07));
                data.mRawData11 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_08));
                data.mRawData12 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_09));
                data.mRawData13 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_10));
                dataList.add(data);
            } while (cursor.moveToNext());
        }
    }

    public void dump() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ITEM_TABLE, null,
                null,
                null, null, null,
                DB.ITEM_TABLE.KEY_CHAPTER+" ASC",
                null);
        if(cursor.moveToFirst()) {
            do {
                RawData data = new RawData();
                data.mRawData01 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_BOOK_NAME));
                data.mRawData02 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_CHAPTER));
                data.mRawData03 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER));
                data.mRawData04 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_01));
                data.mRawData05 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_02));
                data.mRawData06 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_03));
                data.mRawData07 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_04));
                data.mRawData08 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_05));
                data.mRawData09 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_06));
                data.mRawData10 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_07));
                data.mRawData11 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_08));
                data.mRawData12 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_09));
                data.mRawData13 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_10));

                Log.e("MN", "book name="+cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_BOOK_NAME))
                +", chapter="+ cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_CHAPTER))
                +", index in chapter="+ cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER))
                +", data01="+ cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_01)));

            } while (cursor.moveToNext());
        }
    }
}
