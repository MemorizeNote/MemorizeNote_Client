package com.asb.memorizenote.data.reader;

import android.content.Context;
import android.util.Log;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.utils.MNLog;
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

        boolean multipleLineStart = false;
        boolean multipleLineEnd = false;
        int multipleLineStartIdx = 0;
        String mergedMultipleLine = "";

        RawData convertedRawData = null;
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

                        if(multipleLineStart) {
                            if(rawData.startsWith(";") && split_string.length > 0 && split_string[1].equals("##" + Constants.MetaData.KEY_MULTIPLE_LINE_END))
                                multipleLineEnd = true;
                            else {
                                mergedMultipleLine += "\n" + rawData;
                                continue;
                            }
                        }

                        if(convertedRawData == null) {
                            convertedRawData = new RawData();

                            for (String string : split_string) {
                                if (string.equals("##" + Constants.MetaData.KEY_MULTIPLE_LINE_START)) {
                                    multipleLineStart = true;
                                }
                            }
                        }

                        int dataStartIdx = 0;
                        int dataEndIdx = split_string.length;
                        int convertedIdx = 0;

                        if(multipleLineStart && !multipleLineEnd) {
                            multipleLineStartIdx = dataEndIdx-1;
                            dataEndIdx -= 1;
                        }
                        else if(multipleLineStart && multipleLineEnd) {
                            convertedRawData.mRawDataArr[multipleLineStartIdx] = mergedMultipleLine;
                            multipleLineStart = false;
                            dataStartIdx += 1;
                            convertedIdx = multipleLineStartIdx + 1;
                        }

                        for(int i=dataStartIdx; i<dataEndIdx; i++) {
                            convertedRawData.mRawDataArr[convertedIdx] = split_string[i];
                            ++convertedIdx;
                        }

                        if(multipleLineStart)
                            continue;

                        convertedRawData.flattenData();

                        MNLog.d(">>"+(String)convertedRawData.mRawData01+(String)convertedRawData.mRawData02+(String)convertedRawData.mRawData03);

                        rawDataList.add(convertedRawData);

                        mListener.onItem(convertedRawData);

                        if(multipleLineEnd){
                            multipleLineStart = false;
                            multipleLineEnd = false;
                            multipleLineStartIdx = 0;
                            mergedMultipleLine = "";
                        }

                        convertedRawData = null;
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
