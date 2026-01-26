package com.github.lorenj.wordtint.ui.adapter.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;

/**
 * @Created by xww.
 * @Creation time 2018/8/21.
 */

public class ScrollerLinearLayout extends LinearLayout {

    private View menuView;

    private Scroller mScroller;
    private int measuredWidth;

    private float startX;
    private float startY;

    public ScrollerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 获取到索引为1的布局,也就是说在布局文件中,必须是索引为1的布局充当菜单布局
        menuView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = menuView.getMeasuredWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float nowX = event.getX();
        final float nowY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 如果是按下动作,则记录按下动作时手指所在的位置
                startX = nowX;
                startY = nowY;
                break;
            case MotionEvent.ACTION_MOVE:
                // 本次滑动的偏移量(滑动的速度、滑动的距离)
                final float distanceX = nowX - startX;
                // 计算出本次要移动到的目标X位置,getScrollX()得到当前已经移动的距离
                int targetX = (int) (getScrollX() - distanceX);
                if (targetX <= 0) {
                    targetX = 0;
                } else if (targetX >= measuredWidth) {
                    targetX = measuredWidth;
                }
                /*
                这个方法是让item滑动到某个位置,相当于是set的作用没有渐变的效果.
                而scrollBy方法是移动方法,是根据当前位置进行移动的.
                */
                scrollTo(targetX, 0);
                // 只要当前滑动的距离不为0,就不允许父容器滑动了
                if (Math.abs(distanceX) > 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                startX = nowX;
                startY = nowY;
                break;
            case MotionEvent.ACTION_UP:
                if (getScrollX() > measuredWidth / 2) {
                    mScroller.startScroll(getScrollX(), getScrollY(), measuredWidth - getScrollX(), 0);
                    invalidate();
                } else {
                    mScroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), 0);
                    invalidate();
                }
                break;
        }
        return true;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;
        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final float moveX = Math.abs(x - startX);
                if (moveX > 10f) {
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return intercept;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

}