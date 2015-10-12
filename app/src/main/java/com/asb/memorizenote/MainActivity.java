package com.asb.memorizenote;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.data.SimpleVocaData;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.data.apater.NameListAdapter;
import com.asb.memorizenote.reader.AbstractReader;
import com.asb.memorizenote.reader.ReaderFactory;

import java.io.File;


public class MainActivity extends ActionBarActivity implements AbstractReader.OnDataReadListener {

    boolean mIsUpdating = false;
    ProgressDialog mProgressDialog;

    ListView mMainList;
    NameListAdapter mMainListAdapter;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    mProgressDialog = new ProgressDialog(MainActivity.this);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setMessage("Updating...");
                    mProgressDialog.show();
                    break;
                case 1:
                    mProgressDialog.dismiss();
                    break;
                case 2:
                    mMainListAdapter.readDataListFromDB();
                    mMainListAdapter.notifyDataSetChanged();
                    mProgressDialog.dismiss();
                    break;
            }
        }
    };

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

            mMainListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

                mHandler.sendEmptyMessage(0);

                AbstractReader reader = ReaderFactory.createReader(getApplicationContext(), Constants.ReaderType.FILE);
                reader.init(this);
                reader.startReadinInThread();
                break;
        }

        return true;
    }

    @Override
    public void onReadCompleted(AbstractAdapter adapter) {
        if(mIsUpdating) {
            if(adapter != null) {
                adapter.writeDataListToDB();
            } else {
                mIsUpdating = false;
                mHandler.sendEmptyMessage(2);


            }
        }
    }
}
