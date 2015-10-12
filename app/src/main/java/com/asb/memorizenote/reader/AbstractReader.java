package com.asb.memorizenote.reader;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.data.AbstractData;

/**
 * Created by azureskybox on 15. 10. 12.
 *
 * Read data from extra resources.
 */
public abstract class AbstractReader {
    public int mType = Constants.ReaderType.NONE;

    public abstract boolean init();
    public abstract boolean readAll();

    public abstract boolean nextDataSet();
    public abstract AbstractData nextData();

}
