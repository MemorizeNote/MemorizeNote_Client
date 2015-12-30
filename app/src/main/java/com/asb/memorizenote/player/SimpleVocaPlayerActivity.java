package com.asb.memorizenote.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.MemorizeNoteApplication;
import com.asb.memorizenote.R;
import com.asb.memorizenote.data.SimpleVocaData;
import com.asb.memorizenote.player.adapter.SimpleVocaAdapter;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public class SimpleVocaPlayerActivity extends BasePlayerActivity {

    TextView mWordView;
    TextView mMeaningView;
    ArrayList<Boolean> mMeaningDismissList;

    SimpleVocaAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout content = (LinearLayout)inflater.inflate(R.layout.activity_simple_voca_player, null);
        setContent(content);

        mWordView = (TextView)content.findViewById(R.id.simple_voca_player_word);
        mWordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curPosition = mAdapter.currentPosition();
                boolean itemDismiss = mMeaningDismissList.get(curPosition);

                if (itemDismiss) {
                    mMeaningView.setTextColor(Color.WHITE);
                } else {
                    mMeaningView.setTextColor(Color.BLACK);
                }
                mMeaningDismissList.set(curPosition, !itemDismiss);
            }
        });
        mMeaningView = (TextView)content.findViewById(R.id.simple_voca_player_meaning);
        mMeaningView.setTextColor(Color.BLACK);

        mAdapter = (SimpleVocaAdapter)((MemorizeNoteApplication) getApplication()).getDataAdpaterManager().getItemListAdapter(mBookName);

        mMeaningDismissList = new ArrayList<>();
        int itemCount = mAdapter.getCount();
        for(int i=0; i<itemCount; i++)
            mMeaningDismissList.add(true);

        SimpleVocaData data = mAdapter.first();
        setWordAndMeaning(data.mWord, data.mMeaning);
        setChapterTitle("" + (data.mChapterNum));

        setExtraButton("C", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeaningView.setTextColor(Color.BLACK);

                int itemCount = mAdapter.getCount();
                for (int i = 0; i < itemCount; i++)
                    mMeaningDismissList.set(i, true);

                if(mMeaningDismissList.get(mAdapter.currentPosition()))
                    mMeaningView.setTextColor(Color.BLACK);
                else
                    mMeaningView.setTextColor(Color.WHITE);
            }
        });
        setExtraButton("B", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeaningView.setTextColor(Color.BLACK);

                int itemCount = mAdapter.getCount();
                for (int i = 0; i < itemCount; i++)
                    mMeaningDismissList.set(i, false);

                if(mMeaningDismissList.get(mAdapter.currentPosition()))
                    mMeaningView.setTextColor(Color.BLACK);
                else
                    mMeaningView.setTextColor(Color.WHITE);
            }
        });
        setExtraButton(">>", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleVocaData data = mAdapter.nextChapter();

                if(data != null) {
                    setWordAndMeaning(data.mWord, data.mMeaning);

                    if(mMeaningDismissList.get(mAdapter.currentPosition()))
                        mMeaningView.setTextColor(Color.BLACK);
                    else
                        mMeaningView.setTextColor(Color.WHITE);

                    setChapterTitle(""+(data.mChapterNum));
                }
                else
                    showToast("마지막 챕터 입니다.");
            }
        });
        setExtraButton("<<", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleVocaData data = mAdapter.previousChapter();

                if(data != null) {
                    setWordAndMeaning(data.mWord, data.mMeaning);

                    if(mMeaningDismissList.get(mAdapter.currentPosition()))
                        mMeaningView.setTextColor(Color.BLACK);
                    else
                        mMeaningView.setTextColor(Color.WHITE);

                    setChapterTitle(""+(data.mChapterNum));
                }
                else
                    showToast("첫 챕터 입니다.");
            }
        });
    }

    @Override
    protected void onPreviousChapter() {
        Log.e("MN", "onPreviousChapter");
    }

    @Override
    protected void onNextChapter() {
        Log.e("MN", "onNextChapter");
    }

    @Override
    protected void onPreviousContent() {
        SimpleVocaData data = mAdapter.previous();

        if(data != null) {
            setWordAndMeaning(data.mWord, data.mMeaning);

            if(mMeaningDismissList.get(mAdapter.currentPosition()))
                mMeaningView.setTextColor(Color.BLACK);
            else
                mMeaningView.setTextColor(Color.WHITE);

            setChapterTitle(""+(data.mChapterNum));
        }
    }

    @Override
    protected void onNextContent() {
        SimpleVocaData data = mAdapter.next();

        if(data != null) {
            setWordAndMeaning(data.mWord, data.mMeaning);

            if(mMeaningDismissList.get(mAdapter.currentPosition()))
                mMeaningView.setTextColor(Color.BLACK);
            else
                mMeaningView.setTextColor(Color.WHITE);

            setChapterTitle(""+(data.mChapterNum));
        }
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {

    }

    private void setWordAndMeaning(String word, String meaning) {
        mWordView.setText(word);
        mMeaningView.setText(meaning);
    }
}
