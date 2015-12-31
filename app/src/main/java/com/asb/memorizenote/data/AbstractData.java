package com.asb.memorizenote.data;

/**
 * Created by azureskybox on 15. 12. 31.
 */
public abstract class AbstractData {

    public static int ID_NULL = -1;
    public static String STRING_NULL = "";
    public static int COUNT_NULL = 0;
    public static int INT_NULL = -1;

    protected RawData mRawData = null;

    public abstract RawData toRawData();
    public abstract void fromRawDataOfFile(RawData rawData);
    public abstract void fromRawData(RawData rawData);
}
