package com.asb.memorizenote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asb.memorizenote.Constants.AdapterManagerFlags;
import com.asb.memorizenote.Constants.HandlerFlags;
import com.asb.memorizenote.Constants.ReaderFlags;
import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.data.apater.BookListAdapter;
import com.asb.memorizenote.data.apater.DataAdapterManager;
import com.asb.memorizenote.data.db.MemorizeDBHelper;
import com.asb.memorizenote.data.reader.DBReader;
import com.asb.memorizenote.player.BasePlayerActivity;
import com.asb.memorizenote.ui.BaseActivity;
import com.asb.memorizenote.ui.update.FileUpdateActivity;
import com.asb.memorizenote.widget.libraryseat.LibrarySeatParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class MainActivity extends BaseActivity implements ListView.OnItemClickListener, AbstractAdapter.OnDataLoadListener {

    boolean mIsUpdating = false;

    DataAdapterManager mDataAdapterManager = null;

    ListView mMainList;
    BookListAdapter mMainListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check data file directorry
        File dataDir = new File(Constants.FOLDER_PATH);
        if(!dataDir.exists())
            dataDir.mkdir();

        showProgress("Loading...");

        mDataAdapterManager = ((MemorizeNoteApplication)getApplication()).getDataAdpaterManager();
        mDataAdapterManager.initialize(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                testRandIdx();
                break;
            case R.id.action_update_data:
                mIsUpdating = true;
//                showProgress("Updating...");
//
//                mDataAdapterManager.update(AdapterManagerFlags.UPDATE_FROM_FILES, this);
                startActivityForResult(new Intent(this, FileUpdateActivity.class), 0);
                break;
            case R.id.action_dump:
                MemorizeDBHelper helper = new MemorizeDBHelper(getApplicationContext());
                helper.dump();
                break;
            case R.id.action_test_seat:
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
                break;
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AbstractData nameData = (AbstractData)mMainListAdapter.getItem(position);
        Intent playerLaunchIntent = BasePlayerActivity.getLaunchingIntent(this, nameData.mName, nameData.mDataType, 0, nameData.mChapterNum);

        startActivity(playerLaunchIntent);
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
    public void onCompleted() {
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

    private void testRandIdx() {
        int MAX_ITEM_SIZE = 10;
        int MAX_BOOK_SIZE = 3;

        ArrayList<Integer> itemNumPerChapter = new ArrayList<>();
        itemNumPerChapter.add(3);
        itemNumPerChapter.add(4);
        itemNumPerChapter.add(3);

        int[] results = new int[MAX_ITEM_SIZE];
        for(int i=0; i<MAX_ITEM_SIZE; i++)
            results[i] = -1;

        int totalItemNum = 0;
        Random rand = new Random();
        HashMap<Integer, Integer> checker = new HashMap<>();
        for(int chapterNum : itemNumPerChapter) {
            int seed = chapterNum;

            for(int i=totalItemNum; i<totalItemNum+chapterNum; i++) {
                int tmp = rand.nextInt(seed);
                while(true) {
                    if(checker.get(tmp) == null) {
                        checker.put(tmp, tmp);
                        break;
                    }

                    tmp = rand.nextInt(seed);
                }

                results[i] = tmp+totalItemNum;
            }

            checker.clear();
            totalItemNum += chapterNum;
        }

        for(int i=0; i<MAX_ITEM_SIZE; i++)
            Log.d("", i+":"+results[i]);
    }
}
