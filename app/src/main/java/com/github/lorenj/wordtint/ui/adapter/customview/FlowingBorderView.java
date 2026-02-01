package com.github.lorenj.wordtint.ui.adapter.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * 优化后的直角流光边框
 */
public class FlowingBorderView extends View {

    private Paint mPaint;
    private float mRotateAngle = 0f;
    private ValueAnimator mAnimator;
    private final Matrix mMatrix = new Matrix();
    private SweepGradient mShader;

    // 定义颜色，方便后续修改
    private final int[] mColors = {
            Color.parseColor("#2E8B57"),
            Color.parseColor("#98FB98"),
            Color.parseColor("#AFEEEE"),
            Color.parseColor("#2E8B57")
    };

    public FlowingBorderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(12f); // 边框宽度

        mAnimator = ValueAnimator.ofFloat(0f, 360f);
        mAnimator.setDuration(1500);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(animation -> {
            mRotateAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 尺寸确定后初始化渐变器
        if (w > 0 && h > 0) {
            mShader = new SweepGradient(w / 2f, h / 2f, mColors, null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mShader == null) return;

        float width = getWidth();
        float height = getHeight();
        float stroke = mPaint.getStrokeWidth();

        // 1. 设置流光旋转矩阵
        mMatrix.setRotate(mRotateAngle, width / 2f, height / 2f);
        mShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mShader);

        // 2. 绘制直角矩形
        // 使用 stroke/2 进行向内偏移，防止边框的一半被 View 边界裁剪
        canvas.drawRect(
                stroke / 2,
                stroke / 2,
                width - stroke / 2,
                height - stroke / 2,
                mPaint
        );
    }

    public void start() {
        if (mAnimator != null && !mAnimator.isRunning()) {
            mAnimator.start();
        }
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }
}