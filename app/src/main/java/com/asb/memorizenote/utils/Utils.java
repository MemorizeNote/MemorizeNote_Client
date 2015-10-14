package com.asb.memorizenote.utils;

import android.content.Context;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.player.adapter.SimpleVocaAdapter;

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

    public static AbstractAdapter getAdapter(Context context, int bookType) {
        switch(bookType) {
            case Constants.BookType.SIMPLE_VOCA:
                return new SimpleVocaAdapter(context);
            default:
                return null;
        }
    }

}
