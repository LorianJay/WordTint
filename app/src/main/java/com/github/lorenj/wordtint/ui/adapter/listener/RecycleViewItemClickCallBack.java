package com.github.lorenj.wordtint.ui.adapter.listener;

/**
 * RecycleView中item点击的回调事件<br>
 * 该接口的用法不应该是实现而应该是组合的方式
 *
 * @author cnsukidayo
 * @date 2023/2/11 16:34
 */
@FunctionalInterface
public interface RecycleViewItemClickCallBack<T> {
    void viewClickCallBack(T recycleViewOnClick);
}
