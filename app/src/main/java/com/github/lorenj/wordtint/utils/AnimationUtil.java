package com.github.lorenj.wordtint.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Synopsis     动画工具类
 * Author		Mosr
 * version		${VERSION}
 * Create 	    2016/12/22 14:20
 * Email  		intimatestranger@sina.cn
 */
public class AnimationUtil {
    private volatile boolean ismHiddenActionStart = false;
    private static AnimationUtil mInstance;
    private final Set<Integer> hiddenActionStartState = Collections.synchronizedSet(new HashSet<Integer>());

    public static AnimationUtil with() {
        if (mInstance == null) {
            synchronized (AnimationUtil.class) {
                if (mInstance == null) {
                    mInstance = new AnimationUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @param v
     * @param Duration 动画时间
     * @return 返回值为是否执行成功, true为执行成功, false为执行不成功
     */
    public boolean moveToViewBottom(final View v, long Duration) {
        if (v.getVisibility() != View.VISIBLE) return false;
        if (hiddenActionStartState.contains(v.getId())) return false;
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mHiddenAction);
        mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                hiddenActionStartState.add(v.getId());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                hiddenActionStartState.remove(v.getId());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return true;
    }

    /**
     * 从控件的底部移动到控件所在位置
     *
     * @param v
     * @param Duration 动画时间
     * @return 返回值为是否执行成功, true为执行成功, false为执行不成功
     */
    public boolean bottomMoveToViewLocation(View v, long Duration) {
        if (v.getVisibility() == View.VISIBLE)
            return false;
        v.setVisibility(View.VISIBLE);
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mShowAction);
        return true;
    }

    /**
     * 从控件所在位置移动到控件的右侧
     *
     * @param v
     * @param Duration 动画时间
     * @return 返回值为是否执行成功, true为执行成功, false为执行不成功
     */
    public boolean moveToViewEnd(final View v, long Duration) {
        if (v.getVisibility() != View.VISIBLE) return false;
        if (hiddenActionStartState.contains(v.getId())) return false;
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mHiddenAction);
        mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                hiddenActionStartState.add(v.getId());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                hiddenActionStartState.remove(v.getId());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return true;
    }

    /**
     * 从控件的右侧移动到控件所在位置
     *
     * @param v
     * @param Duration 动画时间
     * @return 返回值为是否执行成功, true为执行成功, false为执行不成功
     */
    public boolean endMoveToViewLocation(View v, long Duration) {
        if (v.getVisibility() == View.VISIBLE)
            return false;
        v.setVisibility(View.VISIBLE);
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mShowAction);
        return true;
    }

    /**
     * 从控件所在位置移动到控件的顶部
     *
     * @param v
     * @param Duration 动画时间
     */
    public void moveToViewTop(final View v, long Duration) {
        if (v.getVisibility() != View.VISIBLE)
            return;
        if (ismHiddenActionStart)
            return;
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mHiddenAction);
        mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ismHiddenActionStart = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                ismHiddenActionStart = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 从控件的顶部移动到控件所在位置
     *
     * @param v
     * @param Duration 动画时间
     */
    public void topMoveToViewLocation(View v, long Duration) {
        if (v.getVisibility() == View.VISIBLE)
            return;
        v.setVisibility(View.VISIBLE);
        TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(Duration);
        v.clearAnimation();
        v.setAnimation(mShowAction);
    }

    public boolean isIsmHiddenActionStart() {
        return ismHiddenActionStart;
    }

}