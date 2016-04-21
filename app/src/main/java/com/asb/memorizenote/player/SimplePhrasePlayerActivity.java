package com.asb.memorizenote.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.MemorizeNoteApplication;
import com.asb.memorizenote.R;
import com.asb.memorizenote.player.adapter.SimplePhraseAdapter;
import com.asb.memorizenote.player.data.SimplePhraseData;

import java.util.ArrayList;

/**
 * Created by azureskybox on 16. 4. 21.
 */
public class SimplePhrasePlayerActivity extends BasePlayerActivity {

    SimplePhraseAdapter mAdapter;
    SimplePhraseData mCurrentPhrase;

    TextView mPhraseView;
    TextView mSpellingView;
    TextView mMeaningView;

    ArrayList<Boolean> mSpellingVisibleList;
    ArrayList<Boolean> mMeaningVisibleList;
    boolean mIsSpellingShown = false;
    boolean mIsMeaningShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout content = (LinearLayout)inflater.inflate(R.layout.activity_simple_phrase_player, null);
        setContent(content);

        mPhraseView = (TextView)findViewById(R.id.simple_phrase_player_phrase);
        mSpellingView = (TextView)findViewById(R.id.simple_phrase_player_spelling);
        mMeaningView = (TextView)findViewById(R.id.simple_phrase_player_meaning);

        mAdapter = (SimplePhraseAdapter)((MemorizeNoteApplication) getApplication()).getDataAdpaterManager().getItemListAdapter(mBookName);

        mSpellingVisibleList = new ArrayList<>();
        mMeaningVisibleList = new ArrayList<>();

        int itemCount = mAdapter.getCount();
        for(int i=0; i<itemCount; i++) {
            mSpellingVisibleList.add(false);
            mMeaningVisibleList.add(false);
        }

        mAdapter.first();
        showPhrase();
    }

    private void showPhrase() {
        mCurrentPhrase = (SimplePhraseData)mAdapter.current();

        mPhraseView.setText(mCurrentPhrase.mPhrase);
        mPhraseView.setTextColor(getDifficultyColor(mCurrentPhrase.mDifficulty));

        mSpellingView.setText(mCurrentPhrase.mSpelling);
        mMeaningView.setText(mCurrentPhrase.mMeaning);
        setViewVisibility(mSpellingVisibleList.get(mAdapter.currentPosition()), mMeaningVisibleList.get(mAdapter.currentPosition()));
    }

    private void toggleViewVisibility() {
        if(!mIsSpellingShown && !mIsMeaningShown)
            setViewVisibility(true, false);
        else if(mIsSpellingShown && !mIsMeaningShown)
            setViewVisibility(true, true);
        else
            setViewVisibility(false, false);
    }

    private void setViewVisibility(boolean isSpellingVisible, boolean isMeaningVisible) {
        if(isSpellingVisible) {
            mIsSpellingShown = true;
            mSpellingVisibleList.set(mAdapter.currentPosition(), true);

            mSpellingView.setTextColor(Color.WHITE);
        }
        else {
            mIsSpellingShown = false;
            mSpellingVisibleList.set(mAdapter.currentPosition(), false);

            mSpellingView.setTextColor(Color.BLACK);
        }

        if(isMeaningVisible) {
            mIsMeaningShown = true;
            mMeaningVisibleList.set(mAdapter.currentPosition(), true);

            mMeaningView.setTextColor(Color.WHITE);
        }
        else {
            mIsMeaningShown = false;
            mMeaningVisibleList.set(mAdapter.currentPosition(), false);

            mMeaningView.setTextColor(Color.BLACK);
        }
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

    private void increasePhraseDifficulty() {
        if(mCurrentPhrase.mDifficulty >= SimplePhraseData.DIFFICULTY_VERY_HARD)
            return ;

        ++mCurrentPhrase.mDifficulty;

        mAdapter.write(mCurrentPhrase);

        showPhrase();
    }

    private void decreasePhraseDifficulty() {
        if(mCurrentPhrase.mDifficulty <= SimplePhraseData.DIFFICULTY_EASY)
            return ;

        --mCurrentPhrase.mDifficulty;

        mAdapter.write(mCurrentPhrase);

        showPhrase();
    }

    @Override
    protected void onMenuButtonPressed() {

    }

    @Override
    protected void onBackButtonPressed() {
        finish();
    }

    @Override
    protected void onExtraKeyUp(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                increasePhraseDifficulty();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                decreasePhraseDifficulty();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                toggleViewVisibility();
                break;
            case KeyEvent.KEYCODE_4:
                onPreviousContent();
                break;
            case KeyEvent.KEYCODE_6:
                onNextContent();
                break;
        }
    }

    @Override
    protected void onPreviousChapter() {
        if(mAdapter.previousChapter() == null) {
            showToast("첫 챕터 입니다.");
            return;
        }

        showPhrase();
    }

    @Override
    protected void onNextChapter() {
        if(mAdapter.nextChapter() == null) {
            showToast("마지막 챕터 입니다.");
            return;
        }

        showPhrase();
    }

    @Override
    protected void onPreviousContent() {
        if(mAdapter.previous() == null) {
            showToast("처음 입니다.");
            return;
        }

        showPhrase();
    }

    @Override
    protected void onNextContent() {
        if(mAdapter.next() == null) {
            showToast("마지막 입니다.");
            return;
        }

        showPhrase();
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {

    }
}
