package com.asb.memorizenote.utils;

import java.io.File;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class Utils {

    public static boolean isExpectedFileType(File file, String ext) {
        String name = file.getName();

        int idx = name.lastIndexOf(".");
        String fileExt = name.substring(idx+1, name.length());
        if(fileExt.equals(ext))
            return true;
        return false;
    }

}
