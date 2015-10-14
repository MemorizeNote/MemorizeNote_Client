package com.asb.memorizenote.player;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asb.memorizenote.Constants.*;
import com.asb.memorizenote.MemorizeNoteApplication;
import com.asb.memorizenote.R;
import com.asb.memorizenote.data.SimpleVocaData;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.data.reader.DBReader;
import com.asb.memorizenote.player.adapter.SimpleVocaAdapter;

/**
 * Created by azureskybox on 15. 10. 13.
 */
public class SimpleVocaPlayerActivity extends BasePlayerActivity {

    TextView mWordView;
    TextView mMeaningView;

    SimpleVocaAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout content = (LinearLayout)inflater.inflate(R.layout.activity_simple_voca_player, null);
        setContent(content);

        mWordView = (TextView)content.findViewById(R.id.simple_voca_player_word);
        mMeaningView = (TextView)content.findViewById(R.id.simple_voca_player_meaning);

        mAdapter = (SimpleVocaAdapter)((MemorizeNoteApplication) getApplication()).getDataAdpaterManager().getItemListAdapter(mBookName);

        SimpleVocaData data = mAdapter.first();
        setWordAndMeaning(data.mWord, data.mMeaning);
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
        }
    }

    @Override
    protected void onNextContent() {
        SimpleVocaData data = mAdapter.next();

        if(data != null) {
            setWordAndMeaning(data.mWord, data.mMeaning);
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
