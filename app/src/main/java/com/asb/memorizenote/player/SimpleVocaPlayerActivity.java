package com.asb.memorizenote.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.MemorizeNoteApplication;
import com.asb.memorizenote.R;
import com.asb.memorizenote.player.adapter.SimpleVocaAdapter;
import com.asb.memorizenote.player.data.SimpleVocaData;
import com.asb.memorizenote.ui.WebViewActivity;
import com.asb.memorizenote.utils.MNLog;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public class SimpleVocaPlayerActivity extends BasePlayerActivity {

    TextView mWordView;
    TextView mMeaningView;
    TextView mStatusRandom;
    TextView mStatusMarking;

    ArrayList<Boolean> mMeaningDismissList;

    SimpleVocaAdapter mAdapter;

    boolean mIsAllDismissed = false;
    boolean mIsRandomized = false;
    boolean mIsShowMarking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout content = (LinearLayout)inflater.inflate(R.layout.activity_simple_voca_player, null);
        setContent(content);

        mWordView = (TextView)content.findViewById(R.id.simple_voca_player_word);

        mMeaningView = (TextView)content.findViewById(R.id.simple_voca_player_meaning);
        mMeaningView.setTextColor(Color.BLACK);

        mStatusRandom = (TextView)content.findViewById(R.id.simple_voca_player_status_random);
        mStatusRandom.setTag(false);
        mStatusMarking = (TextView)content.findViewById(R.id.simple_voca_player_status_marking);
        mStatusMarking.setTag(false);

        mAdapter = (SimpleVocaAdapter)mAbstractAdapter;

        mMeaningDismissList = new ArrayList<>();
        int itemCount = mAdapter.getCount();
        for(int i=0; i<itemCount; i++)
            mMeaningDismissList.add(true);

        SimpleVocaData data = (SimpleVocaData)mAdapter.first();
        setWordAndMeaning(data);

        if(mIsSmallMode) {

        }
        else {
            setExtraButton("Rnd", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleRandomMode();
                }
            });
            setExtraButton("Mark", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleMarkingMode();
                }
            });
        }
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        mAdapter.save();
    }

    @Override
    protected void onMenuButtonPressed() {
        showToast("** 버튼 안내 **\n"
                        + "1번 : 랜던 on/off\n"
                        + "2번 : 마킹 on/off\n"
                        + "3번 : 해당단어 사전 찾아보기"
        );
    }

    @Override
    protected void onBackButtonPressed() {
        //기타 설정된 값들을 초기화 한다.
        mIsAllDismissed = false;
        mIsShowMarking = false;
        mIsRandomized = false;

        mAdapter.unrandomize();

        finish();
    }

    @Override
    protected void onPreviousChapter() {
        SimpleVocaData data = (SimpleVocaData)mAdapter.previousChapter();

        if(data != null)
            setWordAndMeaning(data);
        else
            showToast("첫 챕터 입니다.");
    }

    @Override
    protected void onNextChapter() {
        SimpleVocaData data = (SimpleVocaData)mAdapter.nextChapter();

        if(data != null)
            setWordAndMeaning(data);
        else
            showToast("마지막 챕터 입니다.");
    }

    @Override
    protected void onPreviousContent() {
        SimpleVocaData data = null;
        do {
            data = (SimpleVocaData)mAdapter.previous();
        } while(data != null && mIsShowMarking && !data.mMarking);

        if(data != null)
            setWordAndMeaning(data);
        else
            showToast("첫 단어 입니다.");
    }

    @Override
    protected void onNextContent() {
        SimpleVocaData data = null;
        do {
            data = (SimpleVocaData)mAdapter.next();
        } while(data != null && mIsShowMarking && !data.mMarking);

        if(data != null)
            setWordAndMeaning(data);
        else
            showToast("마지막 단어 입니다.");
    }

    @Override
    protected void onJumpToChapter(int chapter) {
        super.onJumpToChapter(chapter);

        setWordAndMeaning((SimpleVocaData)mAdapter.current());
    }

    private void setWordAndMeaning(SimpleVocaData data) {
        mWordView.setText(data.mWord);
        mMeaningView.setText(data.mMeaning);

        if(data.mMarking)
            mWordView.setTextColor(Color.YELLOW);
        else
            mWordView.setTextColor(Color.WHITE);

        if(mMeaningDismissList.get(mAdapter.currentPosition()))
            mMeaningView.setTextColor(Color.BLACK);
        else {
            mMeaningView.setTextColor(Color.WHITE);
        }

        setChapterTitle(data.mChapterName);
    }

    private void toggleMeaningVisibility() {
        int curPosition = mAdapter.currentPosition();
        boolean itemDismiss = mMeaningDismissList.get(curPosition);

        if (itemDismiss) {
            mMeaningView.setTextColor(Color.WHITE);
        } else {
            mMeaningView.setTextColor(Color.BLACK);
        }
        mMeaningDismissList.set(curPosition, !itemDismiss);
    }

    private void toggleMeaningVisibilityAll() {
        if(mIsAllDismissed) {
            mMeaningView.setTextColor(Color.BLACK);

            int itemCount = mAdapter.getCount();
            for (int i = 0; i < itemCount; i++)
                mMeaningDismissList.set(i, false);

            if(mMeaningDismissList.get(mAdapter.currentPosition()))
                mMeaningView.setTextColor(Color.BLACK);
            else
                mMeaningView.setTextColor(Color.WHITE);
        }
        else {
            mMeaningView.setTextColor(Color.BLACK);

            int itemCount = mAdapter.getCount();
            for (int i = 0; i < itemCount; i++)
                mMeaningDismissList.set(i, true);

            if(mMeaningDismissList.get(mAdapter.currentPosition()))
                mMeaningView.setTextColor(Color.BLACK);
            else
                mMeaningView.setTextColor(Color.WHITE);
        }

        mIsAllDismissed = !mIsAllDismissed;
    }

    private void setWordMarking(boolean isMarked) {
        super.onFlingUp();
        SimpleVocaData data = (SimpleVocaData)mAdapter.current();
        data.mMarking = isMarked;

        setWordAndMeaning(data);
    }

    private void toggleRandomMode() {
        if (mIsRandomized) {
            mAdapter.unrandomize();
            mStatusRandom.setText("Random:OFF");
        } else {
            mAdapter.randomize();
            mStatusRandom.setText("Random:ON");
        }

        mIsRandomized = !mIsRandomized;
    }

    private void toggleMarkingMode() {
        if (mIsShowMarking) {
            mStatusMarking.setText("Marking:OFF");
        } else {
            mStatusMarking.setText("Marking:ON");
        }

        mIsShowMarking = !mIsShowMarking;
    }

    private void showDictionary() {
        SimpleVocaData data = (SimpleVocaData)mAdapter.current();

        Intent intent = WebViewActivity.getLaunchingIntent(this, Constants.IntentFlags.WebViewActivity.PageType.DIC, new String[]{data.mWord});
        startActivity(intent);
    }

    /*
     * Touch and Keypad Event
     */
    //For Keypad event listener
    @Override
    protected void onExtraKeyUp(int keyCode) {
        MNLog.d("key code=" + keyCode + ", on SimpleVocaPlayer");
        switch(keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                toggleMeaningVisibility();
                break;
            case KeyEvent.KEYCODE_1:
                toggleRandomMode();
                break;
            case KeyEvent.KEYCODE_2:
                toggleMarkingMode();
                break;
            case KeyEvent.KEYCODE_3:
                showDictionary();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                setWordMarking(true);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                setWordMarking(false);
                break;
        }
    }

    //For touch event listener
    @Override
    protected void onSingleTap() {
        toggleMeaningVisibility();

        super.onSingleTap();
    }

    @Override
    protected void onDoubleTap() {
        toggleMeaningVisibilityAll();

        super.onDoubleTap();
    }

    @Override
    protected void onFlingUp() {
        setWordMarking(true);
    }

    @Override
    protected void onFlingDown() {
        setWordMarking(false);
    }
}
