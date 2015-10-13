package com.asb.memorizenote.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.R;
import com.asb.memorizenote.ui.BaseActivity;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public abstract class BasePlayerActivity extends BaseActivity implements GestureDetector.OnGestureListener {
    //From http://pulsebeat.tistory.com/27
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    protected int mDataType = DataType.NONE;
    protected String mBookName = null;
    protected int mStartChapter = 0;
    protected int mEndChapter = 0;

    protected LinearLayout mChapterWrapper = null;
    protected TextView mChapterTitle = null;

    protected LinearLayout mContentWrapper = null;

    private GestureDetector mGestureDetector;
    private boolean mTouchStartOnChapter;
    private boolean mTouchStartOnContent;

    public static Intent getLaunchingIntent(Context context, String bookName, int dataType, int startChapter, int endChapter) {
        Intent intent = new Intent();
        intent.putExtra(IntentFlags.BasePlayer.DATA_TYPE, dataType);
        intent.putExtra(IntentFlags.BasePlayer.BOOK_NAME, bookName);
        intent.putExtra(IntentFlags.BasePlayer.START_CHAPTER, startChapter);
        intent.putExtra(IntentFlags.BasePlayer.END_CHAPTER, endChapter);

        switch(dataType) {
            case DataType.SIMPLE_VOCA:
                intent.setClass(context, SimpleVocaPlayerActivity.class);
                return intent;
            default:
                return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_player);

        Intent launchIntent = getIntent();

        mDataType = launchIntent.getIntExtra(IntentFlags.BasePlayer.DATA_TYPE, DataType.NONE);
        if(!DataType.isValidType(mDataType)) {
            finish();
        }

        mBookName = launchIntent.getStringExtra(IntentFlags.BasePlayer.BOOK_NAME);
        mStartChapter = launchIntent.getIntExtra(IntentFlags.BasePlayer.START_CHAPTER, 0);
        mEndChapter = launchIntent.getIntExtra(IntentFlags.BasePlayer.START_CHAPTER, 0);

        mGestureDetector = new GestureDetector(getApplicationContext(), this, null);

        mChapterWrapper = (LinearLayout)findViewById(R.id.base_player_chapter_wrapper);
        mChapterTitle = (TextView)findViewById(R.id.base_player_chapter_title);
        mChapterWrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTouchStartOnChapter = true;
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });


        mContentWrapper = (LinearLayout)findViewById(R.id.base_player_content_wrapper);
//        mContentWrapper.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mTouchStartOnContent = true;
//                mGestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //From http://pulsebeat.tistory.com/27
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(mTouchStartOnContent) {
                    onNextContent();
                }
                else {
                    onNextChapter();
                }
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(mTouchStartOnContent) {
                    onPreviousContent();
                }
                else {
                    onPreviousChapter();
                }
            }
            // down to up swipe
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            }
            // up to down swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            }
        } catch (Exception e) {

        }

        mTouchStartOnContent = mTouchStartOnChapter = false;

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    abstract protected void onPreviousChapter();
    abstract protected void onNextChapter();

    abstract protected void onPreviousContent();
    abstract protected void onNextContent();

    protected void setChapterTitle(String chapterTitle) {
        mChapterTitle.setText(chapterTitle);
    }

    protected void setContent(View view) {
        mContentWrapper.addView(view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTouchStartOnContent = true;
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }
}
