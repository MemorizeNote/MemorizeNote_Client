package com.asb.memorizenote;

import android.app.ProgressDialog;
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
import com.asb.memorizenote.data.apater.NameListAdapter;
import com.asb.memorizenote.player.BasePlayerActivity;
import com.asb.memorizenote.reader.AbstractReader;
import com.asb.memorizenote.reader.ReaderFactory;
import com.asb.memorizenote.ui.BaseActivity;

import java.io.File;


public class MainActivity extends BaseActivity implements AbstractReader.OnDataReadListener, ListView.OnItemClickListener {

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

        mMainListAdapter = new NameListAdapter(getApplicationContext());
        mMainListAdapter.readDataListFromDB();

        Log.d("MN", ""+mMainListAdapter.getCount());

        if(mMainListAdapter.getCount() > 0) {
            mMainList = (ListView) findViewById(R.id.main_name_list);
            mMainList.setAdapter(mMainListAdapter);
            mMainList.setOnItemClickListener(this);

            mMainListAdapter.notifyDataSetChanged();
        }
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

                AbstractReader reader = ReaderFactory.createReader(getApplicationContext(), Constants.ReaderType.FILE);
                reader.init(this);
                reader.startReadinInThread();
                break;
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AbstractData nameData = (AbstractData)mMainListAdapter.getItem(position);
        Intent playerLaunchIntent = BasePlayerActivity.getLaunchingIntent(this, nameData.mDataType, 0, nameData.mDataSetCnt);

        startActivity(playerLaunchIntent);
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {
        switch (msg.what) {
            case HandlerFlags.MainActivity.UPDATE_LIST:
                mMainListAdapter.readDataListFromDB();
                mMainListAdapter.notifyDataSetChanged();
                hideProgress();
                break;
        }
    }

    @Override
    public void onReadCompleted(AbstractAdapter adapter) {
        if(mIsUpdating) {
            if(adapter != null) {
                adapter.writeDataListToDB();
            } else {
                mIsUpdating = false;
                mHandler.sendEmptyMessage(HandlerFlags.MainActivity.UPDATE_LIST);
            }
        }
    }


}
