package com.asb.memorizenote.reader;

import com.asb.memorizenote.Constants;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class ReaderFactory {

    public static AbstractReader createReader(int readerType) {
        switch(readerType) {
            case Constants.ReaderType.FILE:
                return new FileReader();
            case Constants.ReaderType.HTTP:
                break;
        }

        return null;
    }
}
