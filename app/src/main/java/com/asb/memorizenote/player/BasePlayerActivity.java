package com.asb.memorizenote.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.R;
import com.asb.memorizenote.ui.BaseActivity;
import com.asb.memorizenote.utils.MNLog;

import java.util.HashMap;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public abstract class BasePlayerActivity extends BaseActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    //From http://pulsebeat.tistory.com/27
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    protected enum TouchType {
        ONE_POINT,
        TWO_POINT,
        THREE_POINT;
    }
    private TouchType mTouchType;
    private HashMap<Integer, Integer> mPointerMap = new HashMap<>();

    protected int mDataType = BookType.NONE;
    protected String mBookName = null;
    protected int mStartChapter = 0;
    protected int mEndChapter = 0;

    protected LinearLayout mChapterWrapper = null;
    protected TextView mChapterTitle = null;

    protected LinearLayout mContentWrapper = null;

    protected LinearLayout mExtraButtonsWrapper = null;
    private static int MAX_EXTRA_BTN_NUM = 4;
    private int mCurrentExtraBtnNum = 0;
    protected Button mExtraButton01 = null;
    protected Button mExtraButton02 = null;
    protected Button mExtraButton03 = null;
    protected Button mExtraButton04 = null;
    private Button[] mExtraButtons = new Button[4];

    private GestureDetector mGestureDetector;

    public static Intent getLaunchingIntent(Context context, String bookName, int dataType, int startChapter, int endChapter) {
        Intent intent = new Intent();
        intent.putExtra(IntentFlags.BasePlayer.DATA_TYPE, dataType);
        intent.putExtra(IntentFlags.BasePlayer.BOOK_NAME, bookName);
        intent.putExtra(IntentFlags.BasePlayer.START_CHAPTER, startChapter);
        intent.putExtra(IntentFlags.BasePlayer.END_CHAPTER, endChapter);

        switch(dataType) {
            case BookType.SIMPLE_VOCA:
                intent.setClass(context, SimpleVocaPlayerActivity.class);
                return intent;
            case BookType.SIMPLE_MEMORIZE:
                intent.setClass(context, SimpleMemorizePlayerActivity.class);
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

        mDataType = launchIntent.getIntExtra(IntentFlags.BasePlayer.DATA_TYPE, BookType.NONE);
        if(!BookType.isValidType(mDataType)) {
            finish();
        }

        mBookName = launchIntent.getStringExtra(IntentFlags.BasePlayer.BOOK_NAME);
        mStartChapter = launchIntent.getIntExtra(IntentFlags.BasePlayer.START_CHAPTER, 0);
        mEndChapter = launchIntent.getIntExtra(IntentFlags.BasePlayer.START_CHAPTER, 0);

        mGestureDetector = new GestureDetector(getApplicationContext(), this, null);

        mChapterWrapper = (LinearLayout)findViewById(R.id.base_player_chapter_wrapper);

        mChapterTitle = (TextView)findViewById(R.id.base_player_chapter_title);
        mChapterTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MNLog.d("!@#!@#!@#!@#");
            }
        });


        mContentWrapper = (LinearLayout)findViewById(R.id.base_player_content_wrapper);

        mExtraButtonsWrapper = (LinearLayout)findViewById(R.id.base_player_extra_buttons_wrapper);
        mExtraButton01 = (Button)findViewById(R.id.base_player_extra_btn_1);
        mExtraButton01.setVisibility(View.INVISIBLE);
        mExtraButton02 = (Button)findViewById(R.id.base_player_extra_btn_2);
        mExtraButton02.setVisibility(View.INVISIBLE);
        mExtraButton03 = (Button)findViewById(R.id.base_player_extra_btn_3);
        mExtraButton03.setVisibility(View.INVISIBLE);
        mExtraButton04 = (Button)findViewById(R.id.base_player_extra_btn_4);
        mExtraButton04.setVisibility(View.INVISIBLE);

        mExtraButtons[0] = mExtraButton01;
        mExtraButtons[1] = mExtraButton02;
        mExtraButtons[2] = mExtraButton03;
        mExtraButtons[3] = mExtraButton04;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //From http://pulsebeat.tistory.com/27
        setTouchType();

        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(mTouchType == TouchType.ONE_POINT)
                    onNextContent();
                else if(mTouchType == TouchType.TWO_POINT)
                    onNextChapter();
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(mTouchType == TouchType.ONE_POINT)
                    onPreviousContent();
                else if(mTouchType == TouchType.TWO_POINT)
                    onPreviousChapter();
            }
            // down to up swipe
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            }
            // up to down swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            }
        } catch (Exception e) {

        }

        clearTouchType();

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        onSingleTap();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        onDoubleTap();
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private void setTouchType() {
        MNLog.d(""+mPointerMap.size());
        switch (mPointerMap.size()) {
            case 1:
                mTouchType = TouchType.TWO_POINT;
                break;
            case 2:
                mTouchType = TouchType.THREE_POINT;
                break;
            default:
                mTouchType = TouchType.ONE_POINT;
                break;
        }
    }

    private void clearTouchType() {
        mPointerMap.clear();
    }

    abstract protected void onPreviousChapter();
    abstract protected void onNextChapter();

    abstract protected void onPreviousContent();
    abstract protected void onNextContent();

    protected void onSingleTap() {
        MNLog.d("onSingleTap");
    }
    protected void onDoubleTap() {
        MNLog.d("onDoubleTap");
    }

    protected void setChapterTitle(String chapterTitle) {
        mChapterTitle.setText(chapterTitle);
    }

    protected void setContent(View view) {
        mContentWrapper.addView(view);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int pointerIndex = event.getActionIndex();
                int pointerID = event.getPointerId(pointerIndex);

                if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                    mPointerMap.put(pointerID, pointerIndex);
                }

                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    protected void setExtraButton(String name, View.OnClickListener listener) {
        if(mCurrentExtraBtnNum >= MAX_EXTRA_BTN_NUM)
            return;

        Button targetExtraButton = mExtraButtons[mCurrentExtraBtnNum];
        targetExtraButton.setText(name);
        targetExtraButton.setOnClickListener(listener);
        targetExtraButton.setVisibility(View.VISIBLE);

        ++mCurrentExtraBtnNum;
    }


}
