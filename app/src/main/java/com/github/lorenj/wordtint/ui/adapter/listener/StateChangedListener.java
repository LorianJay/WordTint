package com.github.lorenj.wordtint.ui.adapter.listener;

/**
 * Item状态改变时调用该监听器
 *
 * @author cnsukidayo
 * @date 2023/1/7 21:13
 */
public interface StateChangedListener {
    /**
     * Item被选中时回调该方法
     */
    void onItemSelected();

    /**
     * Item结束移动时回调该方法
     */
    void onItemClear();

}
