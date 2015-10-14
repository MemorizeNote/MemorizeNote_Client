package com.asb.memorizenote;

import android.app.Application;

import com.asb.memorizenote.data.apater.DataAdapterManager;

/**
 * Created by azureskybox on 15. 10. 14.
 */
public class MemorizeNoteApplication extends Application {

    private DataAdapterManager mDataAdapterManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mDataAdapterManager = new DataAdapterManager(getApplicationContext());
    }

    public DataAdapterManager getDataAdpaterManager() {
        return mDataAdapterManager;
    }
}
