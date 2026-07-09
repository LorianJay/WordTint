package com.github.lorenj.wordtint.ui.adapter.customview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

/**
 * 主要用在文字分段页面,行内文字最后的复制图表的大小和位置的更改
 *
 * @author lorianjay
 * @date 2026/7/9 20:04
 */
public class CenteringImageSpan extends ImageSpan {

    private boolean isPressed = false;

    public CenteringImageSpan(Drawable drawable) {
        super(drawable);
    }

    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();

        // 完美居中计算
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = y + (fm.descent + fm.ascent - drawable.getBounds().bottom) / 2;
        canvas.translate(x, transY);

        // 核心反馈：如果处于被点击状态，降低图标透明度（呈现半透明变暗反馈），松开还原
        if (isPressed) {
            drawable.setAlpha(120); // 0-255，120 大概是接近半透明
        } else {
            drawable.setAlpha(255); // 恢复全透明
        }

        drawable.draw(canvas);
        canvas.restore();
    }
}