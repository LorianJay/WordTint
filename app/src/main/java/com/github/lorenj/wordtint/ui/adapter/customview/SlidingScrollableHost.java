package com.github.lorenj.wordtint.ui.adapter.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 此类用于解决 ViewPager2  嵌套 ViewPager2 或者 RecyclerView 等相互嵌套的冲突问题，
 */
public class SlidingScrollableHost extends FrameLayout {
    private float initialX;
    private float initialY;

    private View getChild() {
        return this.getChildCount() > 0 ? this.getChildAt(0) : null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        this.handleInterceptTouchEvent(e);
        return super.onInterceptTouchEvent(e);
    }

    private final void handleInterceptTouchEvent(MotionEvent e) {
        ViewPager viewPager = (ViewPager) this.getChild();
        if (viewPager != null) {
            int currentItem = viewPager.getCurrentItem();
            int childCount = viewPager.getAdapter().getCount();
            if (e.getAction() == 0) {
                this.initialX = e.getX();
                this.initialY = e.getY();
                this.getParent().requestDisallowInterceptTouchEvent(true);
            } else if (e.getAction() == 2) {
                float dx = e.getX() - this.initialX;
                float dy = e.getY() - this.initialY;
                if (currentItem == 0 && dx > 0) {
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    return;
                }
                if (currentItem == childCount - 1 && dx < 0) {
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    return;
                }
                this.getParent().requestDisallowInterceptTouchEvent(true);
            }

        }
    }

    public SlidingScrollableHost(Context context) {
        super(context);
    }

    public SlidingScrollableHost(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

}
