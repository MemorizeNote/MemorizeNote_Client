package com.asb.memorizenote.reader;

import android.util.Log;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.AbstractData;
import com.asb.memorizenote.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class FileReader extends AbstractReader {

    ArrayList<File> mDataFileList;

    @Override
    public boolean init() {
        mDataFileList = new ArrayList<File>();

        File dataFolder = new File(Constants.FOLDER_PATH);
        File[] dataFileList = dataFolder.listFiles();

        for(File dataFile : dataFileList) {
            Log.d("MN", dataFile.getName());

            if(Utils.isExpectedFileType(dataFile, "txt"))
                mDataFileList.add(dataFile);
        }

        return mDataFileList.size() > 0 ? true:false;
    }

    @Override
    public boolean readAll() {
        return false;
    }

    @Override
    public boolean nextDataSet() {
        return false;
    }

    @Override
    public AbstractData nextData() {
        return null;
    }
}
