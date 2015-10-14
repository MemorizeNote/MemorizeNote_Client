package com.asb.memorizenote.data.reader;

import android.content.Context;
import android.util.Log;

import com.asb.memorizenote.Constants;
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

        mType = Constants.ReaderType.FILE;

        mDataFileList = new ArrayList<File>();

        File dataFolder = new File(Constants.FOLDER_PATH);
        File[] dataFileList = dataFolder.listFiles();

        for(File dataFile : dataFileList) {
            Log.d("MN", dataFile.getName());

            if(Utils.isExpectedFileType(dataFile, "txt"))
                mDataFileList.add(dataFile);
        }

        return;
    }

    public DataFileReader(Context context, String fileName) {
        super(context);

        mType = Constants.ReaderType.FILE;

        mDataFileList = new ArrayList<File>();

        File targetFile = new File(Constants.FOLDER_PATH + "/" + fileName);
        if(!targetFile.exists())
            return;

        if(Utils.isExpectedFileType(targetFile, "txt"))
            mDataFileList.add(targetFile);
        else
            return;
    }

    @Override
    public void readAll() {
        boolean sendHeader = false;
        String dataName = null;
        int dataType = Constants.BookType.NONE;

        for(File dataFile : mDataFileList) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(dataFile));

                ArrayList<RawData> rawDataList = new ArrayList<RawData>();

                String rawData;
                while((rawData = reader.readLine()) != null) {
                    Log.d("MN", rawData);

                    if(rawData.startsWith("##")) {
                        rawData = rawData.substring(2, rawData.length());
                        String[] metaDatas = rawData.split("=");

                        if(metaDatas[0].equals(Constants.MetaData.KEY_NAME)) {
                            dataName = metaDatas[1];
                        }
                        else if(metaDatas[0].equals(Constants.MetaData.KEY_TYPE)) {
                            dataType = Constants.BookType.getType(metaDatas[1]);
                        }
                    }
                    else {
                        if(!sendHeader) {
                            sendHeader = true;
                            mListener.onBookChanged(dataName, dataType, 0);
                        }

                        String[] split_string = rawData.split(";");

                        RawData convertedRawData = new RawData();

                        int rawDataLength = split_string.length;
                        switch(rawDataLength) {
                            case 15:
                                convertedRawData.mRawData15 = split_string[14];
                            case 14:
                                convertedRawData.mRawData14 = split_string[13];
                            case 13:
                                convertedRawData.mRawData13 = split_string[12];
                            case 12:
                                convertedRawData.mRawData12 = split_string[11];
                            case 11:
                                convertedRawData.mRawData11 = split_string[10];
                            case 10:
                                convertedRawData.mRawData10 = split_string[9];
                            case 9:
                                convertedRawData.mRawData09 = split_string[8];
                            case 8:
                                convertedRawData.mRawData08 = split_string[7];
                            case 7:
                                convertedRawData.mRawData07 = split_string[6];
                            case 6:
                                convertedRawData.mRawData06 = split_string[5];
                            case 5:
                                convertedRawData.mRawData05 = split_string[4];
                            case 4:
                                convertedRawData.mRawData04 = split_string[3];
                            case 3:
                                convertedRawData.mRawData03 = split_string[2];
                            case 2:
                                convertedRawData.mRawData02 = split_string[1];
                            case 1:
                                convertedRawData.mRawData01 = split_string[0];
                                break;
                        }

                        rawDataList.add(convertedRawData);

                        mListener.onItem(convertedRawData);
                    }
                }

                mListener.onItemList(rawDataList);
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
