package com.github.lorenj.wordtint.ui.adapter.listener;

/**
 * Item被移动或者滑动时的监听器
 */
public interface MoveAndSwipedListener {
    /**
     * 拖动Item时回调该方法
     *
     * @param fromPosition 源Item的Position
     * @param toPosition   目标Item的Position
     */
    void onItemMove(int fromPosition, int toPosition);

    /**
     * 删除Item时回调该方法
     *
     * @param position 删除的目标Item的position
     */
    void onItemDismiss(int position);

}