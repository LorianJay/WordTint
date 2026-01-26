package com.github.lorenj.wordtint.ui.adapter.listener;

import android.view.MenuItem;

import androidx.annotation.NonNull;

/**
 * 当父级bottomNavigationItem点击当前页面对应的Item时会回调的监听器
 *
 * @author cnsukidayo
 * @date 2023/2/2 18:45
 */
public interface NavigationItemSelectListener {
    /**
     * 当父级bottomNavigationItem点击当前页面对应的Item时会回调的监听器
     *
     * @param item 当前点击的Item
     */
    void onClickCurrentPage(@NonNull MenuItem item);
}
