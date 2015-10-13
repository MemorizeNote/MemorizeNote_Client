package com.asb.memorizenote.player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.R;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public abstract class BasePlayerActivity extends Activity {

    protected int mDataType = DataType.NONE;
    protected int mStartChapter = 0;
    protected int mEndChapter = 0;

    protected LinearLayout mChapterWrapper = null;
    protected TextView mChapterTitle = null;

    protected LinearLayout mContentWrapper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_player);

        Intent launchIntent = getIntent();

        mDataType = launchIntent.getIntExtra(IntentFlags.BasePlayer.DATA_TYPE, DataType.NONE);
        if(!DataType.isValidType(mDataType)) {
            finish();
        }

        mStartChapter = launchIntent.getIntExtra(IntentFlags.BasePlayer.START_CHAPTER, 0);
        mEndChapter = launchIntent.getIntExtra(IntentFlags.BasePlayer.START_CHAPTER, 0);

        mChapterWrapper = (LinearLayout)findViewById(R.id.base_player_chapter_wrapper);
        mChapterTitle = (TextView)findViewById(R.id.base_player_chapter_title);

        mContentWrapper = (LinearLayout)findViewById(R.id.base_player_content_wrapper);
    }

    abstract protected void onPreviousChapter();
    abstract protected void onNextChapter();

    abstract protected void onPreviousContent();
    abstract protected void onNextContent();

    protected void setChapterTitle(String chapterTitle) {
        mChapterTitle.setText(chapterTitle);
    }
}
