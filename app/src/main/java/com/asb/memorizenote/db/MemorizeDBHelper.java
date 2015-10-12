package com.asb.memorizenote.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.asb.memorizenote.Constants.DB;
import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.data.NameListData;
import com.asb.memorizenote.data.apater.AbstractAdapter;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class MemorizeDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = DB.VERSION;
    private static final String DB_NAME = DB.NAME;
    private static final String MEMORIZE_NAME_TABLE = DB.NAME_TABLE.NAME;
    private static final String MEMORIZE_DATA_TABLE = DB.DATA_TABLE.NAME;

    public MemorizeDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MEMORIZE_NAME_TABLE + "("
                + DB.NAME_TABLE.KEY_ID + " INTEGER PRIMARY KEY,"
                + DB.NAME_TABLE.KEY_NAME + " TEXT,"
                + DB.NAME_TABLE.KEY_COUNT + " INTEGER,"
                + DB.NAME_TABLE.KEY_TYPE + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + MEMORIZE_DATA_TABLE + "("
                + DB.DATA_TABLE.KEY_ID + " INTEGER PRIMARY KEY, "
                + DB.DATA_TABLE.KEY_INDEX + " INTEGER, "
                + DB.DATA_TABLE.KEY_DATA_SET_INDEX + " INTEGER, "
                + DB.DATA_TABLE.KEY_NAME_KEY + " INTEGER,"
                + DB.DATA_TABLE.KEY_DATA_01 + " TEXT, "
                + DB.DATA_TABLE.KEY_DATA_02 + " TEXT, "
                + DB.DATA_TABLE.KEY_DATA_03 + " TEXT, "
                + DB.DATA_TABLE.KEY_DATA_04 + " TEXT, "
                + DB.DATA_TABLE.KEY_DATA_05 + " TEXT, "
                + DB.DATA_TABLE.KEY_DATA_06 + " TEXT, "
                + DB.DATA_TABLE.KEY_DATA_07 + " TEXT, "
                + DB.DATA_TABLE.KEY_DATA_08 + " TEXT, "
                + DB.DATA_TABLE.KEY_DATA_09 + " TEXT, "
                + DB.DATA_TABLE.KEY_DATA_10 + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MEMORIZE_NAME_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+MEMORIZE_DATA_TABLE);

        onCreate(db);
    }

    public boolean getDataNameList(ArrayList<AbstractData> dataList) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(MEMORIZE_NAME_TABLE, null, null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                NameListData data = new NameListData();
                data.mName = cursor.getString(cursor.getColumnIndex(DB.NAME_TABLE.KEY_NAME));
                data.mDataSetCnt = cursor.getInt(cursor.getColumnIndex(DB.NAME_TABLE.KEY_COUNT));
                data.mDataType = cursor.getInt(cursor.getColumnIndex(DB.NAME_TABLE.KEY_TYPE));
                data.mNameId = cursor.getInt(cursor.getColumnIndex(DB.NAME_TABLE.KEY_ID));
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        return true;
    }

    public boolean updateDataNameList(AbstractAdapter adapter) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(MEMORIZE_NAME_TABLE, null, DB.NAME_TABLE.KEY_NAME + "=?", new String[]{adapter.getDataName()}, null, null, null, null);
        if(cursor.moveToFirst()) {
            int dataSetCnt = cursor.getInt(cursor.getColumnIndex(DB.NAME_TABLE.KEY_COUNT));
            cursor.close();
            db.close();

            ++dataSetCnt;

            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DB.NAME_TABLE.KEY_COUNT, dataSetCnt);
            db.update(MEMORIZE_NAME_TABLE, values, DB.NAME_TABLE.KEY_NAME + "=?", new String[]{adapter.getDataName()});
            adapter.setmDataSetIndex(dataSetCnt);

            db.close();
            return true;
        }

        cursor.close();
        db.close();

        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB.NAME_TABLE.KEY_NAME, adapter.getDataName());
        values.put(DB.NAME_TABLE.KEY_COUNT, 0);
        values.put(DB.NAME_TABLE.KEY_TYPE, adapter.getDataType());
        db.insert(MEMORIZE_NAME_TABLE, null, values);
        adapter.setmDataSetIndex(0);

        db.close();

        return true;
    }

    public boolean addData(String dataName, int dataNameKey, int index, int dataSetIndex, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();

        values.put(DB.DATA_TABLE.KEY_NAME_KEY, dataName);
        values.put(DB.DATA_TABLE.KEY_INDEX, index);
        values.put(DB.DATA_TABLE.KEY_DATA_SET_INDEX, dataSetIndex);
        values.put(DB.DATA_TABLE.KEY_NAME_KEY, dataNameKey);

        db.insert(MEMORIZE_DATA_TABLE, null, values);

        db.close();

        return true;
    }
}
