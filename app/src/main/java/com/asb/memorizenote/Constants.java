package com.asb.memorizenote;

import android.content.Context;
import android.os.Environment;

import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.player.adapter.SimpleVocaAdapter;

/**
 * Created by azureskybox on 15. 10. 12.
 */
public class Constants {
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String FOLDER_PATH = SD_CARD_PATH +"/MemorizeNote";

    public static class MetaData {
        public static final String KEY_NAME = "name";
        public static final String KEY_TYPE = "type";
        public static final String KEY_CHAPTER_NAME = "chapterName";
        public static final String KEY_MULTIPLE_LINE_START = "multipleLineStart";
        public static final String KEY_MULTIPLE_LINE_END = "multipleLineEnd";
    }

    public static class BookType {
        public static final int NONE = -1;
        public static final int SIMPLE_VOCA = 0;
        public static final int SIMPLE_MEMORIZE = 1;
        public static final int NAME_LIST_DATA = 9999;

        public static int getType(String typeName) {
            if(typeName.equals("SimpleVoca"))
                return SIMPLE_VOCA;
            else if(typeName.equals("SimpleMemorize"))
                return SIMPLE_MEMORIZE;
            else
                return NONE;
        }

        public static boolean isValidType(int dataType) {
            switch(dataType) {
                case SIMPLE_VOCA:
                case SIMPLE_MEMORIZE:
                case NAME_LIST_DATA:
                    return true;
                default:
                    return false;
            }
        }

        public static AbstractAdapter getAdpter(Context context, int type) {
            switch(type) {
                case SIMPLE_VOCA:
                    return new SimpleVocaAdapter(context);
            }

            return null;
        }
    }

    public static class ReaderType {
        public static final int NONE = -1;
        public static final int FILE = 0;
        public static final int HTTP = 1;
        public static final int DB = 2;
    }

    public static class ReaderFlags {
        public static final int NONE = -1;

        public static class DB {
            public static final int TARGET_BOOK = 0;
            public static final int TARGET_ITEM = 1;
        }
    }

    public enum WriterType {
        NONE,
        DB,
        FILE,
        HTTP
    }

    public enum AdapterDataType {
        BOOK,
        CHAPTER,
        ITEM;
    }

    public static class AdapterManagerFlags {
        public static final int UPDATE_FROM_FILES = 0;
        public static final int UPDATE_FROM_HTTP = 1;
        public static final int UPDATE_FROM_LIST = 2;
    }

    public static class DB {
        public static final int VERSION = 2;
        public static final String NAME = "memorizedb";

        public static class BOOK_TABLE {
            public static final String NAME = "memorize_books";

            public static final String KEY_ID = "_id";
            public static final String KEY_NAME = "name";
            public static final String KEY_TYPE = "type";
            public static final String KEY_CHAPTER_COUNT = "chapter_count";
        }

        public static class CHAPTER_TABLE {
            public static final String NAME = "memorize_chapters";

            public static final String KEY_ID = "_id";
            public static final String KEY_BOOK_ID = "book_id";
            public static final String KEY_NAME = "name";
            public static final String KEY_COUNT = "count";
        }

        public static class ITEM_TABLE {
            public static final String NAME = "memorize_items";

            public static final String KEY_ID = "_id";
            public static final String KEY_BOOK_ID = "book_id";
            public static final String KEY_CHAPTER_ID = "chapter_id";
            public static final String KEY_BOOK_NAME = "book_name";
            public static final String KEY_INDEX_IN_CHAPTER = "index_in_chapter";
            public static final String KEY_CHAPTER = "chapter";
            public static final String KEY_MARKING = "marking";
            public static final String KEY_DATA_01 = "data_01";
            public static final String KEY_DATA_02 = "data_02";
            public static final String KEY_DATA_03 = "data_03";
            public static final String KEY_DATA_04 = "data_04";
            public static final String KEY_DATA_05 = "data_05";
            public static final String KEY_DATA_06 = "data_06";
            public static final String KEY_DATA_07 = "data_07";
            public static final String KEY_DATA_08 = "data_08";
            public static final String KEY_DATA_09 = "data_09";
            public static final String KEY_DATA_10 = "data_10";
        }
    }

    public static class HandlerFlags {
        public static final int SHOW_PROGRESS = 0;
        public static final int HIDE_PROGRESS = SHOW_PROGRESS+1;

        public static class MainActivity {
            public static final int UPDATE_LIST = 10;
        }

        public static class FileUpdateActivity {
            public static final int INIT_FILE_LIST = 20;
        }

        public static class SimpleVocalPlayerActivity {
            public static final int AUTO_PLAYING_INIT = 10;
            public static final int SHOW_NEXT_WORD = 11;
            public static final int SHOW_MEANING = 12;
            public static final int HIDE_MEANING = 13;
        }
    }

    public static class IntentFlags {

        public static final class BasePlayer {
            public static final String DATA_TYPE = "data_type";
            public static final String BOOK_NAME = "book_name";
            public static final String START_CHAPTER = "start_chapter";
            public static final String END_CHAPTER = "end_chapter";
        }
    }
}
