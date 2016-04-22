package com.asb.memorizenote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asb.memorizenote.Constants.HandlerFlags;
import com.asb.memorizenote.Constants.ReaderFlags;
import com.asb.memorizenote.data.BaseBookData;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.data.apater.DataAdapterManager;
import com.asb.memorizenote.data.db.MemorizeDBHelper;
import com.asb.memorizenote.data.reader.DBReader;
import com.asb.memorizenote.player.BasePlayerActivity;
import com.asb.memorizenote.ui.BaseActivity;
import com.asb.memorizenote.ui.update.FileUpdateActivity;
import com.asb.memorizenote.widget.libraryseat.LibrarySeatParser;

import java.io.File;


public class MainActivity extends BaseActivity implements ListView.OnItemClickListener, AbstractAdapter.OnDataLoadListener {

    boolean mIsUpdating = false;

    DataAdapterManager mDataAdapterManager = null;

    ListView mMainList;
    ListView mBookList;
    BookListAdapter mMainListAdapter;

    MemorizeDBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager mgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mgr.getDefaultDisplay().getMetrics(metrics);

        Log.d("TAG", "densityDPI = " + metrics.densityDpi);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check data file directorry
        File dataDir = new File(Constants.FOLDER_PATH);
        if(!dataDir.exists())
            dataDir.mkdir();

        showProgress("Loading...");

        mDataAdapterManager = ((MemorizeNoteApplication)getApplication()).getDataAdpaterManager();
        mDataAdapterManager.initialize(this);

        mDBHelper = new MemorizeDBHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MemorizeDBHelper helper;
        switch(id) {
            case R.id.action_delete_book:
                mMainListAdapter.setDeleteMode(true);
                mMainListAdapter.notifyDataSetChanged();
                break;
            case R.id.action_update_data:
                mIsUpdating = true;
                startActivityForResult(new Intent(this, FileUpdateActivity.class), 0);
                break;
            case R.id.action_dump:
                helper = new MemorizeDBHelper(getApplicationContext());
                helper.dump();
                break;
            case R.id.action_clear:
                helper = new MemorizeDBHelper(getApplicationContext());
                helper.clear();
                break;
            case R.id.action_test_seat:
                showProgress("!!!");
                /*
                LibrarySeatParser parser = new LibrarySeatParser(LibrarySeatParser.LIB_TYPE_PYEONGCHON);
                parser.startParsing(new LibrarySeatParser.OnLibrarySeatsParsedListener() {
                    @Override
                    public void onParsed(String total, String current, String remain, String reserved, String next) {

                    }
                });

                parser = new LibrarySeatParser(LibrarySeatParser.LIB_TYPE_SANBON);
                parser.startParsing(new LibrarySeatParser.OnLibrarySeatsParsedListener() {
                    @Override
                    public void onParsed(String total, String current, String remain, String reserved, String next) {

                    }
                });

                parser = new LibrarySeatParser(LibrarySeatParser.LIB_TYPE_JOONGANG);
                parser.startParsing(new LibrarySeatParser.OnLibrarySeatsParsedListener() {
                    @Override
                    public void onParsed(String total, String current, String remain, String reserved, String next) {

                    }
                });
                */
                //mDBHelper.deleteBook(1);
                break;
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mMainListAdapter.isDeleteMode()) {
            mMainListAdapter.setDeleteMode(false);

            mDBHelper.deleteBook(((BaseBookData)mMainListAdapter.getItem(position)).mID);

            mMainListAdapter.notifyDataSetChanged();
        }
        else {
            BaseBookData bookData = (BaseBookData)mMainListAdapter.getItem(position);
            Intent playerLaunchIntent = BasePlayerActivity.getLaunchingIntent(this, bookData.mName, bookData.mType, 0, bookData.mTotalChapter);

            startActivity(playerLaunchIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mIsUpdating = false;
        mMainListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {
        Log.e("MN", "onHandleExtraMessage");

        switch (msg.what) {
            case HandlerFlags.MainActivity.UPDATE_LIST:
                hideProgress();

                DBReader reader = new DBReader(getApplicationContext(), ReaderFlags.DB.TARGET_BOOK);
                mMainListAdapter.readItems(reader);
                break;
        }
    }

    @Override
    public void onReadCompleted() {
        if(mIsUpdating) {
            mIsUpdating = false;
            mMainListAdapter.notifyDataSetChanged();
        }
        else {
            mMainListAdapter = mDataAdapterManager.getBookListAdapter();

            mMainList = (ListView) findViewById(R.id.main_name_list);
            mMainList.setAdapter(mMainListAdapter);
            mMainList.setOnItemClickListener(this);

            mMainListAdapter.notifyDataSetChanged();
        }

        hideProgress();
    }

    @Override
    public void onWriteCompleted() {

    }

    @Override
    public void onLoadCompleted() {

    }
}
