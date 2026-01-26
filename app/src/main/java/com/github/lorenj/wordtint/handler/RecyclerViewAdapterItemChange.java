package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.entity.dto.DataPage;
import com.github.lorenj.wordtint.ui.adapter.listener.RecycleViewItemClickCallBack;

import java.util.Collection;

/**
 * 如果一个 {@link androidx.recyclerview.widget.RecyclerView.Adapter}实现了该接口,表明拥有该接口实例的引用的对象.<br>
 * 可以动态地调用该接口内的方法,动态地改变当前RecyclerView中的Item内容
 *
 * @author cnsukidayo
 * @date 2023/1/9 15:56
 */
public interface RecyclerViewAdapterItemChange<T> {

    /**
     * 添加一个Item到列表中
     *
     * @param item
     */
    void addItem(T item);

    /**
     * 从列表中删除一个Item
     *
     * @param item
     */
    void removeItem(T item);

    /**
     * 添加一个Item数组到列表中
     *
     * @param items
     */
    default void addAll(T... items) {

    }

    /**
     * 添加一个item集合到列表中
     *
     * @param tCollection
     */
    default void addAll(Collection<T> tCollection) {

    }

    /**
     * 添加所有的item列表到集合中,通过DataPage的方式进行添加
     *
     * @param dataPage 数据页
     */
    default void addAllWithDataPage(DataPage<T> dataPage) {

    }


    /**
     * 替换Item列表
     *
     * @param tCollection
     */
    default void replaceAll(Collection<T> tCollection) {

    }

    /**
     * 替换Item列表通过DataPage实现
     *
     * @param dataPage 分页查询对象
     */
    default void replaceAllWithDataPage(DataPage<T> dataPage) {

    }


    /**
     * 设置Item点击后的回调事件
     *
     * @param recycleViewItemClickCallBack
     */
    default void setRecycleViewItemClickCallBack(RecycleViewItemClickCallBack<T> recycleViewItemClickCallBack) {

    }

}
