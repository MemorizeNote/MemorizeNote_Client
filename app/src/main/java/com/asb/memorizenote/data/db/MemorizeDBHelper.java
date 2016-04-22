package com.asb.memorizenote.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.asb.memorizenote.Constants.DB;
import com.asb.memorizenote.data.BaseChapterData;
import com.asb.memorizenote.data.BaseItemData;
import com.asb.memorizenote.data.RawData;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class MemorizeDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = DB.VERSION;
    private static final String DB_NAME = DB.NAME;
    private static final String BOOK_TABLE = DB.BOOK_TABLE.NAME;
    private static final String CHAPTER_TABLE = DB.CHAPTER_TABLE.NAME;
    private static final String ITEM_TABLE = DB.ITEM_TABLE.NAME;

    public MemorizeDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BOOK_TABLE + "("
                + DB.BOOK_TABLE.KEY_ID + " INTEGER PRIMARY KEY,"
                + DB.BOOK_TABLE.KEY_NAME + " TEXT,"
                + DB.BOOK_TABLE.KEY_CHAPTER_COUNT + " INTEGER,"
                + DB.BOOK_TABLE.KEY_TYPE + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + CHAPTER_TABLE + "("
                + DB.CHAPTER_TABLE.KEY_ID + " INTEGER PRIMARY KEY,"
                + DB.CHAPTER_TABLE.KEY_BOOK_ID + " INTEGER,"
                + DB.CHAPTER_TABLE.KEY_NAME + " TEXT,"
                + DB.CHAPTER_TABLE.KEY_COUNT + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + ITEM_TABLE + "("
                + DB.ITEM_TABLE.KEY_ID + " INTEGER PRIMARY KEY, "
                + DB.ITEM_TABLE.KEY_BOOK_ID + " TEXT,"
                + DB.ITEM_TABLE.KEY_CHAPTER_ID + " TEXT,"
                + DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER + " INTEGER, "
                + DB.ITEM_TABLE.KEY_CHAPTER + " INTEGER, "
                + DB.ITEM_TABLE.KEY_MARKING + " INTEGER, "
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
        //db.execSQL("DROP TABLE IF EXISTS "+BOOK_TABLE);
        //db.execSQL("DROP TABLE IF EXISTS "+CHAPTER_TABLE);
        //db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);

        if(newVersion > oldVersion)
            db.execSQL("ALTER TABLE "+ITEM_TABLE+" ADD marking INTEGER");

        onCreate(db);
    }

    public int getCurrentChapterOfBook(int bookID) {
        int currentChapter = -1;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(BOOK_TABLE, null, DB.BOOK_TABLE.KEY_ID + "=?", new String[]{""+bookID}, null, null, null, null);
        if(cursor.moveToFirst()) {
            currentChapter = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_CHAPTER_COUNT));
        }

        cursor.close();
        db.close();

        return currentChapter;
    }

    public int getNextChapterOfBook(int bookID) {
        int lastChapter = 0;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(BOOK_TABLE, null, DB.BOOK_TABLE.KEY_ID + "=?", new String[]{""+bookID}, null, null, null, null);

        if(cursor.moveToFirst()) {
            lastChapter = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_CHAPTER_COUNT));
            ++lastChapter;
        }

        cursor.close();
        db.close();

        return lastChapter;
    }

    public int getChaptesInBook(int bookID) {
        int chapters = 0;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(BOOK_TABLE, null, DB.BOOK_TABLE.KEY_ID + "=?", new String[]{"" + bookID}, null, null, null, null);

        if(cursor.moveToFirst())
            chapters = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_CHAPTER_COUNT));

        cursor.close();
        db.close();

        return chapters;
    }

    public int findBook(String name) {
        int id = -1;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(BOOK_TABLE, null, DB.BOOK_TABLE.KEY_NAME + "=?", new String[]{name}, null, null, null, null);

        if(cursor.moveToFirst())
            id = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_ID));

        cursor.close();
        db.close();

        return id;
    }

    public int addBook(String name, int type) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB.BOOK_TABLE.KEY_NAME, name);
        values.put(DB.BOOK_TABLE.KEY_CHAPTER_COUNT, 0);
        values.put(DB.BOOK_TABLE.KEY_TYPE, type);
        int bookId = (int)db.insert(BOOK_TABLE, null, values);

        db.close();

        return bookId;
    }

    public void deleteBook(int bookId) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DB.BOOK_TABLE.NAME, DB.BOOK_TABLE.KEY_ID + "=?", new String[]{"" + bookId});
        db.delete(DB.CHAPTER_TABLE.NAME, DB.CHAPTER_TABLE.KEY_BOOK_ID + "=?", new String[]{"" + bookId});
        db.delete(DB.ITEM_TABLE.NAME, DB.ITEM_TABLE.KEY_BOOK_ID + "=?", new String[]{"" + bookId});

        db.close();
    }

    public void updateBook(int id, String name, int type, int chaptersInBook) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB.BOOK_TABLE.KEY_NAME, name);
        values.put(DB.BOOK_TABLE.KEY_CHAPTER_COUNT, chaptersInBook);
        values.put(DB.BOOK_TABLE.KEY_TYPE, type);
        db.update(BOOK_TABLE, values, DB.BOOK_TABLE.KEY_ID + "=?", new String[]{"" + id});

        db.close();
    }

    public ArrayList<RawData> getBookList() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BOOK_TABLE, null, null, null, null, null, null, null);

        ArrayList<RawData> dataList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                RawData data = new RawData();
                data.mRawData01 = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_ID));
                data.mRawData02 = cursor.getString(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_NAME));
                data.mRawData03 = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_CHAPTER_COUNT));
                data.mRawData04 = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_TYPE));
                dataList.add(data);
            } while (cursor.moveToNext());
        }

        return dataList;
    }

    public ArrayList<RawData> getBookList(int bookID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BOOK_TABLE, null, DB.BOOK_TABLE.KEY_ID + "=?", new String[]{""+bookID}, null, null, null, null);

        ArrayList<RawData> dataList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                RawData data = new RawData();
                data.mRawData01 = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_ID));
                data.mRawData02 = cursor.getString(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_NAME));
                data.mRawData03 = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_CHAPTER_COUNT));
                data.mRawData04 = cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_TYPE));
                dataList.add(data);
            } while (cursor.moveToNext());
        }

        return dataList;
    }

    public int getChapterCountOnBook(int bookID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(BOOK_TABLE, null, DB.BOOK_TABLE.KEY_ID + "=?", new String[]{""+bookID}, null, null, null, null);
        if(cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_ID));
        }
        else
            return 0;
    }

    public int addChapter(int bookID, String name, int count) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB.CHAPTER_TABLE.KEY_BOOK_ID, bookID);
        values.put(DB.CHAPTER_TABLE.KEY_NAME, name);
        values.put(DB.CHAPTER_TABLE.KEY_COUNT, count);
        int id = (int)db.insert(CHAPTER_TABLE, null, values);

        db.close();

        return id;
    }

    public ArrayList<RawData> getChapterList(int bookID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CHAPTER_TABLE, null, DB.CHAPTER_TABLE.KEY_BOOK_ID + "=?", new String[]{"" + bookID}, null, null,  DB.CHAPTER_TABLE.KEY_ID + " ASC", null);

        ArrayList<RawData> dataList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                RawData data = new RawData();
                data.mRawData01 = cursor.getInt(cursor.getColumnIndex(DB.CHAPTER_TABLE.KEY_ID));
                data.mRawData02 = cursor.getInt(cursor.getColumnIndex(DB.CHAPTER_TABLE.KEY_BOOK_ID));
                data.mRawData03 = cursor.getString(cursor.getColumnIndex(DB.CHAPTER_TABLE.KEY_NAME));
                data.mRawData04 = cursor.getInt(cursor.getColumnIndex(DB.CHAPTER_TABLE.KEY_COUNT));
                dataList.add(data);
            } while (cursor.moveToNext());
        }

        return dataList;
    }

    public int addItem(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();

        int id = (int)db.insert(ITEM_TABLE, null, values);

        db.close();

        return id;
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
                data.mRawData01 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_ID));
                data.mRawData02 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_BOOK_NAME));
                data.mRawData03 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_BOOK_ID));
                data.mRawData04 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_CHAPTER_ID));
                data.mRawData05 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER));
                data.mRawData06 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_CHAPTER));
                data.mRawData07 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_01));
                data.mRawData08 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_02));
                data.mRawData09 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_03));
                data.mRawData10 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_04));
                data.mRawData11 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_05));
                data.mRawData12 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_06));
                data.mRawData13 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_07));
                data.mRawData14 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_08));
                data.mRawData15 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_09));
                data.mRawData16 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_10));
                data.mRawData17 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_MARKING));
                dataList.add(data);
            } while (cursor.moveToNext());
        }
    }

    public ArrayList<RawData> getItemList(int bookID, int chapterID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ITEM_TABLE, null,
                DB.ITEM_TABLE.KEY_BOOK_ID + "=? AND " + DB.ITEM_TABLE.KEY_CHAPTER_ID + "=?",
                new String[]{"" + bookID, "" + chapterID},
                null, null,
                DB.ITEM_TABLE.KEY_CHAPTER + " ASC",
                null);

        ArrayList<RawData> dataList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                RawData data = new RawData();
                data.mRawData01 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_ID));
                data.mRawData02 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_BOOK_NAME));
                data.mRawData03 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_BOOK_ID));
                data.mRawData04 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_CHAPTER_ID));
                data.mRawData05 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER));
                data.mRawData06 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_CHAPTER));
                data.mRawData07 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_01));
                data.mRawData08 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_02));
                data.mRawData09 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_03));
                data.mRawData10 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_04));
                data.mRawData11 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_05));
                data.mRawData12 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_06));
                data.mRawData13 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_07));
                data.mRawData14 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_08));
                data.mRawData15 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_09));
                data.mRawData16 = cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_10));
                data.mRawData17 = cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_MARKING));
                dataList.add(data);
            } while (cursor.moveToNext());
        }

        return dataList;
    }

    public void setItemMarking(int itemId, boolean isMarked) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DB.ITEM_TABLE.KEY_MARKING, isMarked?1:0);
        db.update(ITEM_TABLE, values, DB.BOOK_TABLE.KEY_ID + "=?", new String[]{"" + itemId});

        db.close();
    }

    public void updateItem(BaseItemData item) {
        SQLiteDatabase db = getWritableDatabase();

        db.update(ITEM_TABLE, item.toContentValues(), DB.BOOK_TABLE.KEY_ID + "=?", new String[]{"" + item.mID});

        db.close();
    }

    public void clear() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS "+BOOK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CHAPTER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);

        onCreate(db);
    }

    public void dump() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ITEM_TABLE, null,
                null,
                null, null, null,
                DB.ITEM_TABLE.KEY_BOOK_NAME+" ASC, " + DB.ITEM_TABLE.KEY_CHAPTER+" ASC",
                null);
        if(cursor.moveToFirst()) {
            do {
                Log.e("MN", "book name="+cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_BOOK_NAME))
                +", chapter="+ cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_CHAPTER))
                +", index in chapter="+ cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_INDEX_IN_CHAPTER))
                +", book id="+ cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_BOOK_ID))
                +", chapter id="+ cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_CHAPTER_ID))
                +", marking="+ cursor.getInt(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_MARKING))
                +", data01="+ cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_01))
                +", data02="+ cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_02))
                +", data03="+ cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_03))
                +", data04="+ cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_04))
                +", data05="+ cursor.getString(cursor.getColumnIndex(DB.ITEM_TABLE.KEY_DATA_05)));

            } while (cursor.moveToNext());
        }
        cursor.close();

        cursor = db.query(BOOK_TABLE, null,
                null,
                null, null, null,
                null,
                null);
        if(cursor.moveToFirst()) {
            do {
                Log.e("MN", "book id="+cursor.getString(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_ID))
                        +", book name="+ cursor.getString(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_NAME))
                        +", chapters in book="+ cursor.getString(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_CHAPTER_COUNT))
                        +", book type="+ cursor.getInt(cursor.getColumnIndex(DB.BOOK_TABLE.KEY_TYPE)));

            } while (cursor.moveToNext());
        }
        cursor.close();

        cursor = db.query(CHAPTER_TABLE, null,
                null,
                null, null, null,
                null,
                null);
        if(cursor.moveToFirst()) {
            do {
                Log.e("MN", "chapter id="+cursor.getString(cursor.getColumnIndex(DB.CHAPTER_TABLE.KEY_ID))
                        +", book id="+cursor.getString(cursor.getColumnIndex(DB.CHAPTER_TABLE.KEY_BOOK_ID))
                        +", item count="+cursor.getString(cursor.getColumnIndex(DB.CHAPTER_TABLE.KEY_COUNT))
                        +", chapter name="+ cursor.getString(cursor.getColumnIndex(DB.CHAPTER_TABLE.KEY_NAME)));

            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
    }
}
