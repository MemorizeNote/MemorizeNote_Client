package com.asb.memorizenote.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.asb.memorizenote.R;
import com.asb.memorizenote.data.BaseItemData;
import com.asb.memorizenote.data.apater.AbstractAdapter;
import com.asb.memorizenote.data.db.MemorizeDBHelper;
import com.asb.memorizenote.player.data.SimpleVocaData;
import com.asb.memorizenote.utils.MNLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by azureskybox on 15. 12. 30.
 */
public abstract class AbstractPlayerAdapter extends AbstractAdapter {

    protected int mTotalItem = 0;
    protected int mTotalUpdatedItem = 0;
    protected int mTotalChapter = 0;
    protected ArrayList<Integer> mTotalItemPerChapter;

    protected int mCurItem = 0;
    protected int mCurChapter = 0;
    protected int mCurItemInChapter = 0;

    protected boolean mIsRandomized;
    protected int[] mRandomizedIdx;

    private LayoutInflater mInflater;

    public AbstractPlayerAdapter(Context context) {
        super(context);

        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTotalItemPerChapter = new ArrayList<>();
        mIsRandomized = false;
    }

    public BaseItemData current() {
        return mItemList.get(mIsRandomized ? mRandomizedIdx[mCurItem] : mCurItem);
    }

    public BaseItemData first() {
        mCurChapter = 0;
        mCurItem = 0;
        mCurItemInChapter = 0;

        return current();
    }

    public BaseItemData next() {
        ++mCurItem;
        ++mCurItemInChapter;

        MNLog.d("next, mCurItem=" + mCurItem + ", mCurItemInChapter=" + mCurItemInChapter + "mCurChapter=" + mCurChapter + ", totalItemInChapter=" + mTotalItemPerChapter.get(mCurChapter));

        if(mCurItemInChapter < mTotalItemPerChapter.get(mCurChapter))
            return current();
        else {
            if(mCurChapter < mTotalChapter-1) {
                ++mCurChapter;
                mCurItemInChapter = 0;

                return current();
            }
            else {
                --mCurItem;
                --mCurItemInChapter;
                return null;
            }
        }
    }

    public BaseItemData previous() {
        --mCurItem;
        --mCurItemInChapter;

        MNLog.d("previous, mCurItem="+mCurItem+", mCurItemInChapter="+mCurItemInChapter+"mCurChapter="+mCurChapter+", totalItemInChapter="+mTotalItemPerChapter.get(mCurChapter));

        if(mCurItemInChapter >= 0)
            return current();
        else {
            if(mCurChapter > 0) {
                --mCurChapter;
                mCurItemInChapter = mTotalItemPerChapter.get(mCurChapter)-1;

                return current();
            }
            else {
                mCurItem = 0;
                mCurItemInChapter = 0;
                return null;
            }
        }
    }

    public BaseItemData nextChapter() {
        MNLog.d("before nextChapter, mCurItem="+mCurItem+", mCurItemInChapter="+mCurItemInChapter+", mCurChapter="+mCurChapter+", totalItemInChapter="+mTotalItemPerChapter.get(mCurChapter));

        //현재 쳅터가 마지막이라면, 이동하지 않는다.
        if(mCurChapter >= mTotalChapter-1)
            return null;

        //현재 쳅터에서 아이템이 몇 개 남았는지 확인한다.
        int totalItemInCurChapter = mTotalItemPerChapter.get(mCurChapter);
        int restItemInCurChapter = totalItemInCurChapter - mCurItemInChapter;

        MNLog.d("In chapter, total="+totalItemInCurChapter+", remain="+restItemInCurChapter);

        //현재 쳅터의 마지막 아이템에서 다음 쳅터의 첫 아이템으로 이동하기 위해 아이템 인덱스를 변경한다.
        mCurItem += restItemInCurChapter;

        //쳅터 이동을 위해 쳅터 인덱스를 변경한다.
        ++mCurChapter;
        mCurItemInChapter = 0;

        MNLog.d("after nextChapter, mCurItem="+mCurItem+", mCurItemInChapter="+mCurItemInChapter+", mCurChapter="+mCurChapter+", totalItemInChapter="+mTotalItemPerChapter.get(mCurChapter));

        return current();
    }

    public BaseItemData previousChapter() {
        MNLog.d("before previousChapter, mCurItem="+mCurItem+", mCurItemInChapter="+mCurItemInChapter+", mCurChapter="+mCurChapter+", totalItemInChapter="+mTotalItemPerChapter.get(mCurChapter));

        //처음 챕터라면 종료한다.
        if(mCurChapter <= 0)
            return null;

        //현재 챕터의 첫 인덱스로 이동한다.
        mCurItem -= mCurItemInChapter;

        //이전 쳅터의 인덱스로 변경한다.
        --mCurChapter;
        mCurItemInChapter = 0;

        //현재 챕터의 첫 아이템 인덱스에서 이전 챕터의 첫 아이템 인덱스로 변경한다.
        mCurItem -= mTotalItemPerChapter.get(mCurChapter);

        MNLog.d("after previousChapter, mCurItem="+mCurItem+", mCurItemInChapter="+mCurItemInChapter+", mCurChapter="+mCurChapter+", totalItemInChapter="+mTotalItemPerChapter.get(mCurChapter));

        return current();
    }

    public BaseItemData jumpToChapter(int chapter) {
        MNLog.d("jumpToChapter, chapter="+chapter);

        //nextChapter/previousChapter를 몇 회 실행할지 결정한다.
        int loopCount = chapter - mCurChapter;
        MNLog.d("loop count="+loopCount);

        BaseItemData firstItemDataInChapter = null;
        BaseItemData tempItem;
        //현재 챕터보다 이후의 챕터로 이동한다.
        if(loopCount > 0) {
            for(int i=0; i<loopCount; i++) {
                if((tempItem = nextChapter()) == null)
                    break;

                firstItemDataInChapter = tempItem;
            }
        }
        //현재 챕터보다 이전의 챕터로 이동한다.
        else if(loopCount < 0) {
            loopCount = Math.abs(loopCount);

            for(int i=0; i<loopCount; i++) {
                if((tempItem = previousChapter()) == null)
                    break;

                firstItemDataInChapter = tempItem;
            }
        }

        return firstItemDataInChapter;
    }

    public int currentPosition() {
        return mCurItem;
    }

    /**
     * 원래 데이터 배열은 변경하지 않고, 챕터 내에서의 출력 순서만 무작위로 참조하는
     * 순서 배열을 내부적으로 생성한다.
     * 만약 전체 데이터의 범위가 0~9(10개의 데이터)이고,
     * 총 챕터는 3개, 각 챕터별 아이템 개수가 다음과 같다고 가정하자.
     * chap[0] = 3;
     * chap[1] = 4;
     * chap[2] = 3;
     *
     * 이 때 해당 함수의 목적은 mCurItem로 참조할 수 있는, 각 챕터별 무작위 index 배열 생성이다.
     * - 원본 : {0,1,2,3,4,5,6,7,8,9}
     * - 생성 : {
     *   2,1,0,     //챕터 0에 대한 무작위 인덱스
     *   6,3,4,2,   //챕터 1에 대한 무작위 인덱스
     *   8,9,7      //챕터 2에 대한 무작위 인덱스
     * }
     * 위와 같이 생성했을 때 기존 아이템 이동 코드를 수정하지 않고 무작위 출력 기능을 바로 적용할 수 있는 장점이 있다.]ㅎ
     *
     * 위와 같은 배열 생성을 위 다음과 같은 알고리즘을 적용 하였다.
     * 1) mRandomizedIdx를 전체 아이템 개수만큼의 크기로 생성한다.
     * 2) 기본 루프는 mTotalItemPerChapter의 개수만큼 동작한다.
     * 3) 각 챕터 별 아이템 개수를 최대값으로 하는 무작위 정수를 생성한다.
     * 4) 생성한 정수는 임의의 값이므로 순서대로 배열에 저장한다.
     * 5) 이 때 무작위 정수에 totalItemNum를 더하여 적절한 인덱스로 보정한다.
     * 6) 생성되는 배열에 대한 for문의 index 범위는 수
     *      현재까지의 총 아이템 개수 <= index < 현재까지의 총 아이템 개수 + 다음 챕터 아이템 개수
     *  로 설정하여 최상위 for문 한 번에 해당 챕터 아이템 개수만큼 무작위 인덱스 생성을 하도록 한다.
     */
    public void randomize() {
        mIsRandomized = true;

        mRandomizedIdx = new int[mItemList.size()];

        int totalItemNum = 0;
        Random rand = new Random();
        HashMap<Integer, Integer> checker = new HashMap<>();
        for(int chapterNum : mTotalItemPerChapter) {
            int seed = chapterNum;

            for(int i=totalItemNum; i<totalItemNum+chapterNum; i++) {
                int tmp = rand.nextInt(seed);
                while(true) {
                    if(checker.get(tmp) == null) {
                        checker.put(tmp, tmp);
                        break;
                    }

                    tmp = rand.nextInt(seed);
                }

                mRandomizedIdx[i] = tmp+totalItemNum;
            }

            checker.clear();
            totalItemNum += chapterNum;
        }

    }

    public void unrandomize() {
        mIsRandomized = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.base_player_chapter_list_item, null, false);
        }

        ((TextView)convertView.findViewById(R.id.base_player_chapter_list_name)).setText(mChapterList.get(position).mName);

        /*
        ((CheckBox)convertView.findViewById(R.id.file_update_file_list_item_select)).setTag(position);
        ((CheckBox)convertView.findViewById(R.id.file_update_file_list_item_select)).setFocusable(false);
        ((CheckBox)convertView.findViewById(R.id.file_update_file_list_item_select)).setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mDataList.get((Integer)buttonView.getTag()).mSelected = true;
                else
                    mDataList.get((Integer)buttonView.getTag()).mSelected = false;
            }
        });
        */
        return convertView;
    }

    public int getCurrentChapterSize() {
        return mTotalItemPerChapter.get(mCurChapter);
    }

    public int getCurrentItemIndex() {
        return mCurItemInChapter+1;
    }
}
