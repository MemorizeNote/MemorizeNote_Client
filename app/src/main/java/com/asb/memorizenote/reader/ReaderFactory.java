package com.asb.memorizenote.reader;

import android.content.Context;

import com.asb.memorizenote.Constants;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class ReaderFactory {

    public static AbstractReader createReader(Context context, int readerType) {
        switch(readerType) {
            case Constants.ReaderType.FILE:
                return new DataFileReader(context);
            case Constants.ReaderType.HTTP:
                break;
        }

        return null;
    }
}
