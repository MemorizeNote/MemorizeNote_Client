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

import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.data.apater.DataUpdateAdapter;
import com.asb.memorizenote.data.apater.NameListAdapter;
import com.asb.memorizenote.data.reader.DBReader;
import com.asb.memorizenote.data.reader.DataFileReader;
import com.asb.memorizenote.player.BasePlayerActivity;
import com.asb.memorizenote.ui.BaseActivity;

import java.io.File;


public class MainActivity extends BaseActivity implements ListView.OnItemClickListener {

    boolean mIsUpdating = false;

    ListView mMainList;
    NameListAdapter mMainListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check data file directorry
        File dataDir = new File(Constants.FOLDER_PATH);
        if(!dataDir.exists())
            dataDir.mkdir();

        DBReader reader = new DBReader(getApplicationContext(), ReaderFlags.DB.TARGET_BOOK);

        mMainListAdapter = new NameListAdapter(getApplicationContext());
        mMainListAdapter.setListener(new AbstractAdapter.OnDataLoadListener() {
            @Override
            public void onCompleted() {
                Log.e("MN", "onCompleted");

                if(mMainListAdapter.getCount() > 0) {
                    mMainListAdapter.notifyDataSetChanged();
                }
            }
        });
        mMainListAdapter.readItems(reader);

        mMainList = (ListView) findViewById(R.id.main_name_list);
        mMainList.setAdapter(mMainListAdapter);
        mMainList.setOnItemClickListener(this);
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
                break;
            case R.id.action_update_data:
                mIsUpdating = true;
                showProgress("Updating...");

                DataFileReader reader = new DataFileReader(getApplicationContext());

                DataUpdateAdapter updateAdpater = new DataUpdateAdapter(getApplicationContext());
                updateAdpater.setListener(new AbstractAdapter.OnDataLoadListener() {
                    @Override
                    public void onCompleted() {
                        mHandler.sendEmptyMessage(HandlerFlags.MainActivity.UPDATE_LIST);
                    }
                });
                updateAdpater.readItems(reader);
                break;
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AbstractData nameData = (AbstractData)mMainListAdapter.getItem(position);
        Intent playerLaunchIntent = BasePlayerActivity.getLaunchingIntent(this, nameData.mDataType, 0, nameData.mChapterNum);

        startActivity(playerLaunchIntent);
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
}
