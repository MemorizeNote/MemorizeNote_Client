package com.asb.memorizenote.ui.update;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.MemorizeNoteApplication;
import com.asb.memorizenote.R;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.ui.BaseActivity;
import com.asb.memorizenote.utils.MNLog;
import com.asb.memorizenote.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by azureskybox on 15. 10. 26.
 */
public class FileUpdateActivity extends BaseActivity implements AbstractAdapter.OnDataLoadListener {

    ArrayList<UpdateFileData> mDataList;
    FileUpdateAdapter mFileListAdapter;
    ListView mFileListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_update);

        mFileListView = (ListView)findViewById(R.id.file_update_main);
        mFileListView.setFocusable(true);
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //((CheckBox)view.findViewById(R.id.file_update_file_list_item_select)).setTag(position);
                mDataList.get(position).mSelected = !mDataList.get(position).mSelected;
                ((CheckBox)view.findViewById(R.id.file_update_file_list_item_select)).setChecked(mDataList.get(position).mSelected);

            }
        });

        showProgress("Loading...");
        Thread t = new Thread(new CheckFileListRunnable());
        t.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        showProgress("Updating...");

        switch(id) {
            case R.id.action_select_files:
                Thread t1 = new Thread(new UpdateFileRunnable((MemorizeNoteApplication)getApplication(), this));
                t1.start();
                break;
            case R.id.action_select_all:
                for(UpdateFileData data : mDataList) {
                    data.mSelected = true;
                }
                mFileListAdapter.notifyDataSetChanged();

                Thread t2 = new Thread(new UpdateFileRunnable((MemorizeNoteApplication)getApplication(), this));
                t2.start();
                break;
        }

        return true;
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {
        switch (msg.what) {
            case Constants.HandlerFlags.FileUpdateActivity.INIT_FILE_LIST:
                mFileListView.setAdapter(mFileListAdapter);
                mFileListAdapter.notifyDataSetChanged();
                hideProgress();
                break;
        }
    }

    @Override
    public void onReadCompleted() {
        hideProgress();
        setResult(0);
        finish();
    }

    @Override
    public void onWriteCompleted() {

    }

    @Override
    public void onLoadCompleted() {

    }

    private class CheckFileListRunnable implements Runnable {

        @Override
        public void run() {
            mDataList = new ArrayList<UpdateFileData>();

            File dataFolder = new File(Constants.FOLDER_PATH);
            File[] dataFileList = dataFolder.listFiles();
            Arrays.sort(dataFileList, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });

            for(File dataFile : dataFileList) {
                Log.d("MN", dataFile.getName());

                if (Utils.isExpectedFileType(dataFile, "txt")) {
                    UpdateFileData data = new UpdateFileData();
                    data.mFileName = dataFile.getName();
                    data.mSelected = false;
                    data.mFile = dataFile;

                    mDataList.add(data);
                }
            }

            mFileListAdapter = new FileUpdateAdapter(getApplicationContext(), mDataList);
            mHandler.sendEmptyMessage(Constants.HandlerFlags.FileUpdateActivity.INIT_FILE_LIST);
        }
    }

    private class UpdateFileRunnable implements Runnable {

        MemorizeNoteApplication mApplication;
        AbstractAdapter.OnDataLoadListener mDataLoadListener;

        UpdateFileRunnable(MemorizeNoteApplication application, AbstractAdapter.OnDataLoadListener listener) {
            mApplication = application;
            mDataLoadListener = listener;
        }

        @Override
        public void run() {
            ArrayList<File> selectedFileList2 = new ArrayList<File>();
            for(UpdateFileData data : mDataList) {
                if(data.mSelected) {
                    MNLog.d(data.mFileName);
                    selectedFileList2.add(data.mFile);
                }
            }

            ((MemorizeNoteApplication)getApplication()).getDataAdpaterManager().update(Constants.AdapterManagerFlags.UPDATE_FROM_FILES, mDataLoadListener, selectedFileList2);
        }
    }
}
