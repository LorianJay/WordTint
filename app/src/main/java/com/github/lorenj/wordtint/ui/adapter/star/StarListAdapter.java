package com.github.lorenj.wordtint.ui.adapter.star;

import android.content.Context;

import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.handler.RecyclerViewAdapterItemChange;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.ui.adapter.listener.MoveAndSwipedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 每个分类的Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class StarListAdapter implements RecyclerViewAdapterItemChange<WordStarWithWordIdEntity>,
        MoveAndSwipedListener {

    private final Context context;
    private final ConcatAdapter globalAdapter;
    private final StarFunctionHandler startFunctionHandler;
    public static final int STAR = 3;
    public static final int BOOK_SECTION = 4;
    /**
     * 所有单词中文意思的缓存
     */
    private final RecyclerView.RecycledViewPool resultPool = new RecyclerView.RecycledViewPool();
    private final ItemTouchHelper touchHelper;

    public StarListAdapter(Context context, StarFunctionHandler startFunctionHandler, ItemTouchHelper touchHelper) {
        this.context = context;
        this.startFunctionHandler = startFunctionHandler;
        this.touchHelper = touchHelper;
        ConcatAdapter.Config config = new ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build();
        this.globalAdapter = new ConcatAdapter(config);
    }

    /**
     * 得到ConcatAdapter
     */
    public void refreshConcatAdapter() {
        List<? extends RecyclerView.Adapter<? extends RecyclerView.ViewHolder>> currentAdapters =
                new ArrayList<>(globalAdapter.getAdapters());
        for (RecyclerView.Adapter<?> adapter : currentAdapters) {
            globalAdapter.removeAdapter(adapter);
        }
        List<WordStarWithWordIdEntity> allStarList = startFunctionHandler.getAllStarList();
        for (WordStarWithWordIdEntity wordStarWithWordIdEntity : allStarList) {
            StarHeaderAdapter headerAdapter = new StarHeaderAdapter(
                    context,
                    wordStarWithWordIdEntity,
                    startFunctionHandler,
                    globalAdapter,
                    resultPool,
                    touchHelper);
            this.globalAdapter.addAdapter(headerAdapter);
        }
    }

    public ConcatAdapter getGlobalAdapter() {
        return globalAdapter;
    }

    @Override
    public void addItem(WordStarWithWordIdEntity item) {
        StarHeaderAdapter headerAdapter = new StarHeaderAdapter(
                context,
                item,
                startFunctionHandler,
                globalAdapter,
                resultPool,
                touchHelper);
        this.globalAdapter.addAdapter(headerAdapter);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        WordStarWithWordIdEntity fromStar = startFunctionHandler.getAllStarList().get(fromPosition);
        WordStarWithWordIdEntity toStar = startFunctionHandler.getAllStarList().get(toPosition);
        startFunctionHandler.moveStar(fromStar, toStar);
    }

    @Override
    public void onItemDismiss(int position) {

    }
}
