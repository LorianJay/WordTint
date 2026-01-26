package com.github.lorenj.wordtint.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lorenj.wordtint.ui.adapter.listener.MoveAndSwipedListener;
import com.github.lorenj.wordtint.ui.adapter.listener.StateChangedListener;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private MoveAndSwipedListener mAdapter;

    public SimpleItemTouchHelperCallback(MoveAndSwipedListener listener) {
        mAdapter = listener;
    }

    /**
     * 不支持长按拖动
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * 不支持左滑和右划删除
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    /**
     * 指定拖动和滑动支持的方向
     *
     * @param recyclerView 被拖动的RecyclerView引用
     * @param viewHolder   被拖动的viewHolder
     * @return 返回状态
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //设置拖拽方向为上下
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }


    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        // 当某个viewHolder被点击选中时调用该方法
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder instanceof StateChangedListener) {
            StateChangedListener listener = (StateChangedListener) viewHolder;
            listener.onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // 当某个viewHolder停止选中时调用该方法
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof StateChangedListener) {
            StateChangedListener listener = (StateChangedListener) viewHolder;
            listener.onItemClear();
        }
    }


}