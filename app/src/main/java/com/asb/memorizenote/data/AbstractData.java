package com.asb.memorizenote.data;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public abstract class AbstractData {
    public String mName;
    public String mPlayer;

    public abstract boolean generateDataFromFileString(String rawString);
    public abstract boolean generateDataFromJSONString(String rawString);
}
