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
import com.asb.memorizenote.R;
import com.asb.memorizenote.player.adapter.SimpleVocaAdapter;
import com.asb.memorizenote.player.data.SimplePhraseData;
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
    TextView mStatusDifficulty;

    ArrayList<Boolean> mMeaningDismissList;

    SimpleVocaAdapter mAdapter;
    SimpleVocaData mCurrentVoca;

    boolean mIsAllDismissed = false;
    boolean mIsRandomized = false;
    boolean mIsShowDifficulty = false;
    int mShowingDifficulty = SimpleVocaData.DIFFICULTY_NORMAL;

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
        mStatusDifficulty = (TextView)content.findViewById(R.id.simple_voca_player_status_marking);
        mStatusDifficulty.setTag(false);

        mAdapter = (SimpleVocaAdapter)mAbstractAdapter;

        mMeaningDismissList = new ArrayList<>();
        int itemCount = mAdapter.getCount();
        for(int i=0; i<itemCount; i++)
            mMeaningDismissList.add(true);

        mCurrentVoca = (SimpleVocaData)mAdapter.first();
        setWordAndMeaning();

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
        mIsShowDifficulty = false;
        mIsRandomized = false;

        mAdapter.unrandomize();

        finish();
    }

    @Override
    protected void onPreviousChapter() {
        mCurrentVoca = (SimpleVocaData)mAdapter.previousChapter();

        if(mCurrentVoca != null)
            setWordAndMeaning();
        else
            showToast("첫 챕터 입니다.");
    }

    @Override
    protected void onNextChapter() {
        mCurrentVoca = (SimpleVocaData)mAdapter.nextChapter();

        if(mCurrentVoca != null)
            setWordAndMeaning();
        else
            showToast("마지막 챕터 입니다.");
    }

    @Override
    protected void onPreviousContent() {
        mAdapter.mark();

        do {
            mCurrentVoca = (SimpleVocaData)mAdapter.previous();
        } while(mCurrentVoca != null && mCurrentVoca.mDifficulty < mShowingDifficulty);

        if(mCurrentVoca != null)
            setWordAndMeaning();
        else {
            mAdapter.recovery();
            showToast("첫 단어 입니다.");
        }
    }

    @Override
    protected void onNextContent() {
        mAdapter.mark();

        do {
            mCurrentVoca = (SimpleVocaData)mAdapter.next();
        } while(mCurrentVoca != null && mCurrentVoca.mDifficulty < mShowingDifficulty);

        if(mCurrentVoca != null)
            setWordAndMeaning();
        else {
            mAdapter.recovery();
            showToast("마지막 단어 입니다.");
        }
    }

    @Override
    protected void onJumpToChapter(int chapter) {
        super.onJumpToChapter(chapter);

        mCurrentVoca = (SimpleVocaData)mAdapter.current();
        setWordAndMeaning();
    }

    private void setWordAndMeaning() {
        mWordView.setText(mCurrentVoca.mWord);
        mMeaningView.setText(mCurrentVoca.mMeaning);

        mWordView.setTextColor(getDifficultyColor(mCurrentVoca.mDifficulty));

        if(mMeaningDismissList.get(mAdapter.currentPosition()))
            mMeaningView.setTextColor(Color.BLACK);
        else {
            mMeaningView.setTextColor(Color.WHITE);
        }

        setChapterTitle(mCurrentVoca.mChapterName, mAdapter.getCurrentItemIndex(), mAdapter.getCurrentChapterSize());
    }

    private int getDifficultyColor(int difficulty) {
        switch (difficulty) {
            case SimplePhraseData.DIFFICULTY_EASY:
                return Color.GRAY;
            case SimplePhraseData.DIFFICULTY_NORMAL:
                return Color.WHITE;
            case SimplePhraseData.DIFFICULTY_HARD:
                return Color.YELLOW;
            case SimplePhraseData.DIFFICULTY_VERY_HARD:
                return Color.RED;
            default:
                return Color.WHITE;
        }
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

        setWordAndMeaning();
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
        if (mIsShowDifficulty) {
            mStatusDifficulty.setText("Marking:OFF");
        } else {
            mStatusDifficulty.setText("Marking:ON");
        }

        mIsShowDifficulty = !mIsShowDifficulty;
    }

    private void showDictionary() {
        SimpleVocaData data = (SimpleVocaData)mAdapter.current();

        Intent intent = WebViewActivity.getLaunchingIntent(this, Constants.IntentFlags.WebViewActivity.PageType.DIC, new String[]{data.mWord});
        startActivity(intent);
    }

    private void increasePhraseDifficulty() {
        if(mCurrentVoca.mDifficulty >= SimplePhraseData.DIFFICULTY_VERY_HARD)
            return ;

        ++mCurrentVoca.mDifficulty;

        mAdapter.write(mCurrentVoca);

        setWordAndMeaning();
    }

    private void decreasePhraseDifficulty() {
        if(mCurrentVoca.mDifficulty <= SimplePhraseData.DIFFICULTY_EASY)
            return ;

        --mCurrentVoca.mDifficulty;

        mAdapter.write(mCurrentVoca);

        setWordAndMeaning();
    }

    private void increaseDifficultyFilter() {
        if(mShowingDifficulty >= SimpleVocaData.DIFFICULTY_VERY_HARD)
            return ;

        ++mShowingDifficulty;

        mStatusDifficulty.setText("Diff:" + getDifficultyPhrase());
    }

    private void decreaseDifficultyFilter() {
        if(mShowingDifficulty <= SimpleVocaData.DIFFICULTY_EASY)
            return ;

        --mShowingDifficulty;

        mStatusDifficulty.setText("Diff:" + getDifficultyPhrase());
    }

    private String getDifficultyPhrase() {
        switch(mShowingDifficulty) {
            case SimpleVocaData.DIFFICULTY_EASY:
                return "EASY";
            case SimpleVocaData.DIFFICULTY_NORMAL:
                return "NORMAL";
            case SimpleVocaData.DIFFICULTY_HARD:
                return "HARD";
            case SimpleVocaData.DIFFICULTY_VERY_HARD:
                return "VHARD";
            default:
                return "NORMAL";
        }
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
            case KeyEvent.KEYCODE_7:
                decreaseDifficultyFilter();
                break;
            case KeyEvent.KEYCODE_9:
                increaseDifficultyFilter();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                increasePhraseDifficulty();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                decreasePhraseDifficulty();
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
