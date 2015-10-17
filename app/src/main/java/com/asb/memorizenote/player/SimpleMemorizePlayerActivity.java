package com.asb.memorizenote.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.MemorizeNoteApplication;
import com.asb.memorizenote.R;
import com.asb.memorizenote.data.SimpleMemorizeData;
import com.asb.memorizenote.player.adapter.SimpleMemorizeAdapter;

import java.util.ArrayList;

/**
 * Created by azureskybox on 15. 10. 17.
 */
public class SimpleMemorizePlayerActivity extends BasePlayerActivity {

    TextView mTitleView;
    TextView mContentsView;
    ArrayList<Boolean> mContentsDismissList;

    SimpleMemorizeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout content = (LinearLayout)inflater.inflate(R.layout.activity_simple_memorize_player, null);
        setContent(content);

        mTitleView = (TextView)content.findViewById(R.id.simple_memorize_player_title);
        mTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curPosition = mAdapter.currentPosition();
                boolean itemDismiss = mContentsDismissList.get(curPosition);

                if (itemDismiss) {
                    mContentsView.setTextColor(Color.WHITE);
                } else {
                    mContentsView.setTextColor(Color.BLACK);
                }
                mContentsDismissList.set(curPosition, !itemDismiss);
            }
        });
        mContentsView = (TextView)content.findViewById(R.id.simple_memorize_player_contents);
        mContentsView.setTextColor(Color.BLACK);

        mAdapter = (SimpleMemorizeAdapter)((MemorizeNoteApplication) getApplication()).getDataAdpaterManager().getItemListAdapter(mBookName);

        mContentsDismissList = new ArrayList<>();
        int itemCount = mAdapter.getCount();
        for(int i=0; i<itemCount; i++)
            mContentsDismissList.add(true);

        SimpleMemorizeData data = mAdapter.first();
        setWordAndMeaning(data.mTitle, data.mContents);
        setChapterTitle("Chapter. " + (data.mChapterNum));

        setExtraButton("Clear", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentsView.setTextColor(Color.BLACK);

                int itemCount = mAdapter.getCount();
                for(int i=0; i<itemCount; i++)
                    mContentsDismissList.set(i, true);
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
        SimpleMemorizeData data = mAdapter.previous();

        if(data != null) {
            setWordAndMeaning(data.mTitle, data.mContents);

            if(mContentsDismissList.get(mAdapter.currentPosition()))
                mContentsView.setTextColor(Color.BLACK);
            else
                mContentsView.setTextColor(Color.WHITE);

            setChapterTitle("Chapter. "+(data.mChapterNum));
        }
    }

    @Override
    protected void onNextContent() {
        SimpleMemorizeData data = mAdapter.next();

        if(data != null) {
            setWordAndMeaning(data.mTitle, data.mContents);

            if(mContentsDismissList.get(mAdapter.currentPosition()))
                mContentsView.setTextColor(Color.BLACK);
            else
                mContentsView.setTextColor(Color.WHITE);

            setChapterTitle("Chapter. "+(data.mChapterNum));
        }
    }

    @Override
    protected void onHandleExtraMessage(Message msg) {

    }

    private void setWordAndMeaning(String word, String meaning) {
        mTitleView.setText(word);
        mContentsView.setText(meaning);
    }
}
