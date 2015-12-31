package com.asb.memorizenote.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.Constants;
import com.asb.memorizenote.MemorizeNoteApplication;
import com.asb.memorizenote.R;
import com.asb.memorizenote.player.adapter.SimpleVocaAdapter;
import com.asb.memorizenote.player.data.SimpleVocaData;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public class SimpleVocaPlayerActivity extends BasePlayerActivity {

    TextView mWordView;
    TextView mMeaningView;
    TextView mStatusRandom;
    TextView mStatusAuto;

    ArrayList<Boolean> mMeaningDismissList;

    SimpleVocaAdapter mAdapter;

    AutoPlayingRunnable mAutoPlayer;

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
        mStatusAuto = (TextView)content.findViewById(R.id.simple_voca_player_status_auto);
        mStatusAuto.setTag(false);

        mAdapter = (SimpleVocaAdapter)((MemorizeNoteApplication) getApplication()).getDataAdpaterManager().getItemListAdapter(mBookName);

        mMeaningDismissList = new ArrayList<>();
        int itemCount = mAdapter.getCount();
        for(int i=0; i<itemCount; i++)
            mMeaningDismissList.add(true);

        SimpleVocaData data = (SimpleVocaData)mAdapter.first();
        setWordAndMeaning(data);

        setExtraButton("Rnd", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRandomized) {
                    mAdapter.unrandomize();
                    mStatusRandom.setText("Random:OFF");
                } else {
                    mAdapter.randomize();
                    mStatusRandom.setText("Random:ON");
                }

                mIsRandomized = !mIsRandomized;
            }
        });
        setExtraButton("Auto", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((Boolean)mStatusAuto.getTag()) {
                    mAutoPlayer.stop();

                    mStatusAuto.setText("Auto:OFF");
                    mStatusAuto.setTag(false);
                }
                else {
                    mAutoPlayer = new AutoPlayingRunnable(getApplicationContext(), mHandler);
                    Thread t = new Thread(mAutoPlayer);
                    t.start();

                    mStatusAuto.setText("Auto:ON");
                    mStatusAuto.setTag(true);
                }
            }
        });
        setExtraButton("Mark", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsRandomized) {
                    showToast("무작위보기 해제 후 사용하세요.");
                    return;
                }

                mIsShowMarking = !mIsShowMarking;
            }
        });
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
    protected void onSingleTap() {
        toggleMeaningVisibility();

        super.onSingleTap();
    }

    @Override
    protected void onDoubleTap() {
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

        super.onDoubleTap();
    }

    @Override
    protected void onFlingUp() {
        super.onFlingUp();
        SimpleVocaData data = (SimpleVocaData)mAdapter.current();
        data.mMarking = true;

        setWordAndMeaning(data);
    }

    @Override
    protected void onFlingDown() {
        super.onFlingDown();
        SimpleVocaData data = (SimpleVocaData)mAdapter.current();
        data.mMarking = false;

        setWordAndMeaning(data);
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {
        switch (msg.what) {
            case Constants.HandlerFlags.SimpleVocalPlayerActivity.AUTO_PLAYING_INIT:
                setMeaningVisibility(false);
                break;
            case Constants.HandlerFlags.SimpleVocalPlayerActivity.SHOW_MEANING:
                setMeaningVisibility(true);
                break;
            case Constants.HandlerFlags.SimpleVocalPlayerActivity.HIDE_MEANING:
                setMeaningVisibility(false);
                break;
            case Constants.HandlerFlags.SimpleVocalPlayerActivity.SHOW_NEXT_WORD:
                SimpleVocaData data = (SimpleVocaData)mAdapter.next();
                if(data != null) {
                    setWordAndMeaning(data);
                }
                else {
                    mAutoPlayer.stop();
                }
                break;
        }
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

    private void setMeaningVisibility(boolean visibility){
        int curPosition = mAdapter.currentPosition();

        if (visibility) {
            mMeaningView.setTextColor(Color.WHITE);
        } else {
            mMeaningView.setTextColor(Color.BLACK);
        }
        mMeaningDismissList.set(curPosition, visibility);
    }

    private void toggleMeaningVisibility(){
        int curPosition = mAdapter.currentPosition();
        boolean itemDismiss = mMeaningDismissList.get(curPosition);

        if (itemDismiss) {
            mMeaningView.setTextColor(Color.WHITE);
        } else {
            mMeaningView.setTextColor(Color.BLACK);
        }
        mMeaningDismissList.set(curPosition, !itemDismiss);
    }

    private class AutoPlayingRunnable implements Runnable {

        private Context mContext;
        private Handler mHandler;
        private boolean mStopFlag;

        public AutoPlayingRunnable(Context context, Handler handler) {
            mContext = context;
            mHandler = handler;
            mStopFlag = false;
        }

        @Override
        public void run() {

            mHandler.sendEmptyMessage(Constants.HandlerFlags.SimpleVocalPlayerActivity.AUTO_PLAYING_INIT);

            while(!mStopFlag) {
                try {
                    Thread.sleep(1500);
                    mHandler.sendEmptyMessage(Constants.HandlerFlags.SimpleVocalPlayerActivity.SHOW_MEANING);

                    Thread.sleep(1500);
                    mHandler.sendEmptyMessage(Constants.HandlerFlags.SimpleVocalPlayerActivity.HIDE_MEANING);

                    Thread.sleep(1500);
                    mHandler.sendEmptyMessage(Constants.HandlerFlags.SimpleVocalPlayerActivity.SHOW_MEANING);

                    Thread.sleep(1500);
                    mHandler.sendEmptyMessage(Constants.HandlerFlags.SimpleVocalPlayerActivity.HIDE_MEANING);

                    Thread.sleep(3000);
                    mHandler.sendEmptyMessage(Constants.HandlerFlags.SimpleVocalPlayerActivity.SHOW_NEXT_WORD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            mStopFlag = true;
        }
    }
}
