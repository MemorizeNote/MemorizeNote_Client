package com.asb.memorizenote.data.reader;

import android.content.Context;
import android.util.Log;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class DataFileReader extends AbstractReader {

    ArrayList<File> mDataFileList;

    public DataFileReader(Context context) {
        super(context);
    }

    @Override
    public boolean init(OnDataReadListener listener) {
        super.init(listener);

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
    public void readAll() {
        boolean sendHeader = false;
        String dataName = null;
        int dataType = Constants.DataType.NONE;

        for(File dataFile : mDataFileList) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(dataFile));

                ArrayList<String> dataList = new ArrayList<String>();
                String rawData = null;
                while((rawData = reader.readLine()) != null) {
                    Log.d("MN", rawData);

                    if(rawData.startsWith("##")) {
                        rawData = rawData.substring(2, rawData.length());
                        String[] metaDatas = rawData.split("=");

                        if(metaDatas[0].equals(Constants.MetaData.KEY_NAME)) {
                            dataName = metaDatas[1];
                        }
                        else if(metaDatas[0].equals(Constants.MetaData.KEY_TYPE)) {
                            dataType = Constants.DataType.getType(metaDatas[1]);
                        }
                    }
                    else {
                        if(!sendHeader) {
                            sendHeader = true;
                            mListener.onItemSetChanged(dataName, dataType, 0);
                        }

                        String[] splitted = rawData.split(";");

                        RawData convertedRawData = new RawData();
                        convertedRawData.mRawData01 = splitted[0];
                        convertedRawData.mRawData02 = splitted[1];

                        mListener.onItem(convertedRawData);
                    }
                }

                sendHeader = false;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mListener.onCompleted();
    }
}
