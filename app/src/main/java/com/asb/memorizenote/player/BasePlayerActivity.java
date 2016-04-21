package com.asb.memorizenote.player;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
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
    private static final int SWIPE_MIN_DISTANCE_X = 100;
    private static final int SWIPE_MIN_DISTANCE_Y = 40;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private enum TouchDirection {
        NONE,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
    protected enum TouchType {
        ONE_POINT,
        TWO_POINT,
        THREE_POINT;
    }
    private TouchType mTouchType;
    private HashMap<Integer, Integer> mPointerMap = new HashMap<>();

    protected boolean mSmallMode = false;

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

    private GestureDetector mGestureDetector = null;

    public static Intent getLaunchingIntent(Context context, String bookName, int dataType, int startChapter, int endChapter) {
        MNLog.d("getLaunchingIntent, book name=" + bookName);

        Intent intent = new Intent();
        intent.putExtra(IntentFlags.BasePlayer.DATA_TYPE, dataType);
        intent.putExtra(IntentFlags.BasePlayer.BOOK_NAME, bookName);
        intent.putExtra(IntentFlags.BasePlayer.START_CHAPTER, startChapter);
        intent.putExtra(IntentFlags.BasePlayer.END_CHAPTER, endChapter);
        intent.putExtra(IntentFlags.BasePlayer.DEVICE_MODEL, Build.MODEL.toUpperCase());

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

        /*
         * 전체적인 동작을 터치로 할지 키패드로 할지 결정하는 변수
         * F440L만 지원함
         */
        mSmallMode = launchIntent.getStringExtra(IntentFlags.BasePlayer.DEVICE_MODEL).contains("F440L");

        mDataType = launchIntent.getIntExtra(IntentFlags.BasePlayer.DATA_TYPE, BookType.NONE);
        if(!BookType.isValidType(mDataType)) {
            finish();
        }

        mBookName = launchIntent.getStringExtra(IntentFlags.BasePlayer.BOOK_NAME);
        mStartChapter = launchIntent.getIntExtra(IntentFlags.BasePlayer.START_CHAPTER, 0);
        mEndChapter = launchIntent.getIntExtra(IntentFlags.BasePlayer.START_CHAPTER, 0);

        mChapterWrapper = (LinearLayout)findViewById(R.id.base_player_chapter_wrapper);
        mChapterTitle = (TextView)findViewById(R.id.base_player_chapter_title);
        /*
        mChapterTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        */

        mContentWrapper = (LinearLayout)findViewById(R.id.base_player_content_wrapper);

        if(mSmallMode) {

        }
        else {
            mGestureDetector = new GestureDetector(getApplicationContext(), this, null);

            mExtraButtonsWrapper = (LinearLayout) findViewById(R.id.base_player_extra_buttons_wrapper);
            mExtraButton01 = (Button) findViewById(R.id.base_player_extra_btn_1);
            mExtraButton01.setVisibility(View.INVISIBLE);
            mExtraButton02 = (Button) findViewById(R.id.base_player_extra_btn_2);
            mExtraButton02.setVisibility(View.INVISIBLE);
            mExtraButton03 = (Button) findViewById(R.id.base_player_extra_btn_3);
            mExtraButton03.setVisibility(View.INVISIBLE);
            mExtraButton04 = (Button) findViewById(R.id.base_player_extra_btn_4);
            mExtraButton04.setVisibility(View.INVISIBLE);

            mExtraButtons[0] = mExtraButton01;
            mExtraButtons[1] = mExtraButton02;
            mExtraButtons[2] = mExtraButton03;
            mExtraButtons[3] = mExtraButton04;
        }
    }

    protected void setChapterTitle(String chapterTitle) {
        mChapterTitle.setText(chapterTitle);
    }

    protected void setContent(View view) {
        mContentWrapper.addView(view);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if(mSmallMode) {

        }
        else {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int pointerIndex = event.getActionIndex();
                    int pointerID = event.getPointerId(pointerIndex);

                    if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                        mPointerMap.put(pointerID, pointerIndex);
                    }

                    mGestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(mSmallMode) {
            MNLog.d("key down:" + keyCode);

            switch(keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    onPreviousContent();
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    onNextContent();
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    onBackButtonPressed();
                    return true;
                case KeyEvent.KEYCODE_MENU:
                    onMenuButtonPressed();
                    return true;
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_CAMERA:
                case KeyEvent.KEYCODE_CONTACTS:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_STAR:
                case KeyEvent.KEYCODE_POUND:
                    onExtraKeyUp(keyCode);
                    return true;
                default:
                    return false;
            }
        }
        else
            return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        MNLog.d("onFling");
        MNLog.d("e1.x="+e1.getX()+", e1.y"+e1.getY());
        MNLog.d("e2.x="+e2.getX()+", e2.y"+e2.getY());
        MNLog.d("vx="+velocityX+", vy="+velocityY);
        //From http://pulsebeat.tistory.com/27
        setTouchType();

        try {
            TouchDirection direction = TouchDirection.NONE;

            float distanceX = Math.abs(e1.getX() - e2.getX());
            float distanceY = Math.abs(e1.getY() - e2.getY());

            //left or right
            if(distanceX >= distanceY*2) {
                if(velocityX > 0 && distanceX > SWIPE_MIN_DISTANCE_X && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    direction = TouchDirection.RIGHT;
                }
                else if(velocityX < 0 && distanceX > SWIPE_MIN_DISTANCE_X && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    direction = TouchDirection.LEFT;
                }
            }
            //up or down
            else {
                if(velocityY > 0 && distanceX > SWIPE_MIN_DISTANCE_Y && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    direction = TouchDirection.DOWN;
                }
                else if(velocityY < 0 && distanceX > SWIPE_MIN_DISTANCE_Y && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    direction = TouchDirection.UP;
                }
            }

            MNLog.d(""+direction);

            switch (direction) {
                case RIGHT:
                    if(mTouchType == TouchType.ONE_POINT)
                        onPreviousContent();
                    else if (mTouchType == TouchType.TWO_POINT)
                        onPreviousChapter();
                    break;
                case LEFT:
                    if(mTouchType == TouchType.ONE_POINT)
                        onNextContent();
                    else if (mTouchType == TouchType.TWO_POINT)
                        onNextChapter();
                    break;
                case UP:
                    onFlingUp();
                    break;
                case DOWN:
                    onFlingDown();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
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

    abstract protected void onMenuButtonPressed();
    abstract protected void onBackButtonPressed();

    abstract protected void onPreviousChapter();
    abstract protected void onNextChapter();

    abstract protected void onPreviousContent();
    abstract protected void onNextContent();

    protected void onExtraKeyUp(int keyCode) {
        MNLog.d("extra key="+keyCode+", but no handle in BasePlayerActivity...");
    }

    protected void onFlingUp() {
        MNLog.d("onFlingUp");
    }
    protected void onFlingDown(){
        MNLog.d("onFlingDown");
    }

    protected void onSingleTap() {
        MNLog.d("onSingleTap");
    }
    protected void onDoubleTap() {
        MNLog.d("onDoubleTap");
    }
}
