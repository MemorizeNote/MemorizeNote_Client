package com.asb.memorizenote;

import android.os.Environment;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class Constants {
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String FOLDER_PATH = SD_CARD_PATH +"/MemorizeNote";

    public static class ReaderType {
        public static final int NONE = -1;
        public static final int FILE = 0;
        public static final int HTTP = 1;
    }

}
