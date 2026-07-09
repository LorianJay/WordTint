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

    public CenteringImageSpan(Drawable drawable) {
        super(drawable);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();

        // 核心修正：基于当前文本行的 Baseline (y) 和 FontMetrics 动态计算真正的垂直中心点
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = y + (fm.descent + fm.ascent - drawable.getBounds().bottom) / 2;

        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}