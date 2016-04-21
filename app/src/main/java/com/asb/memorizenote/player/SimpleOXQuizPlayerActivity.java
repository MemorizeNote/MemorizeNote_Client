package com.asb.memorizenote.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.MemorizeNoteApplication;
import com.asb.memorizenote.R;
import com.asb.memorizenote.player.adapter.SimpleOXQuizAdapter;
import com.asb.memorizenote.player.data.SimpleOXQuizData;

/**
 * Created by azureskybox on 16. 4. 21.
 */
public class SimpleOXQuizPlayerActivity extends BasePlayerActivity {

    SimpleOXQuizAdapter mAdapter;
    SimpleOXQuizData mCurrentQuestion = null;

    private TextView mQuestionView;
    private TextView mAnswerView;
    private Button mAnswerCorrect;
    private Button mAnswerIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout content = (LinearLayout)inflater.inflate(R.layout.activity_simple_ox_quiz_player, null);
        setContent(content);

        mQuestionView = (TextView)content.findViewById(R.id.simple_ox_quiz_player_question);
        mAnswerView = (TextView)content.findViewById(R.id.simple_ox_quiz_player_answer);
        mAnswerCorrect = (Button)content.findViewById(R.id.simple_ox_quiz_player_answer_o);
        mAnswerIncorrect = (Button)content.findViewById(R.id.simple_ox_quiz_player_answer_x);

        mAnswerCorrect.setTag(true);
        mAnswerIncorrect.setTag(false);
        if(!mIsSmallMode) {
            mAnswerCorrect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mAnswerCorrect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        mAdapter = (SimpleOXQuizAdapter)mAbstractAdapter;

        mCurrentQuestion = (SimpleOXQuizData)mAdapter.first();
        showQuestion();
    }

    private void showQuestion() {
        if(mCurrentQuestion == null)
            return;

        mCurrentQuestion = (SimpleOXQuizData)mAdapter.current();

        //문제 표시
        mQuestionView.setText(mCurrentQuestion.mQuestion);

        //정답 표시
        mAnswerView.setText("정답:" + (mCurrentQuestion.mAnswer == 1 ? mCurrentQuestion.mExample1 : mCurrentQuestion.mExample2));
        mAnswerView.setTextColor(Color.BLACK);

        mAnswerCorrect.setText(mCurrentQuestion.mExample1);
        mAnswerIncorrect.setText(mCurrentQuestion.mExample2);

        setChapterTitle(mCurrentQuestion.mChapterName, mAdapter.getCurrentItemIndex(), mAdapter.getCurrentChapterSize());
    }

    private void checkAnswer(boolean isCorrect) {
        if(isCorrect)
            mAnswerCorrect.setText(mCurrentQuestion.mExample1+"(v)");
        else
            mAnswerIncorrect.setText(mCurrentQuestion.mExample2+"(v)");

        int checkedAnswer = isCorrect?1:2;
        if(mCurrentQuestion.mAnswer == checkedAnswer)
            mAnswerView.setTextColor(Color.BLUE);
        else
            mAnswerView.setTextColor(Color.RED);
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
            case KeyEvent.KEYCODE_1:
                checkAnswer(true);
                break;
            case KeyEvent.KEYCODE_3:
                checkAnswer(false);
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

        showQuestion();
    }

    @Override
    protected void onNextChapter() {
        if(mAdapter.nextChapter() == null) {
            showToast("마지막 챕터 입니다.");
            return;
        }

        showQuestion();
    }

    @Override
    protected void onPreviousContent() {
        if(mAdapter.previous() == null) {
            showToast("첫 문제 입니다.");
            return;
        }

        showQuestion();
    }

    @Override
    protected void onNextContent() {
        if(mAdapter.next() == null) {
            showToast("마지막 문제 입니다.");
            return;
        }

        showQuestion();
    }

    @Override
    protected void onJumpToChapter(int chapter) {
        super.onJumpToChapter(chapter);

        showQuestion();
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {

    }
}
