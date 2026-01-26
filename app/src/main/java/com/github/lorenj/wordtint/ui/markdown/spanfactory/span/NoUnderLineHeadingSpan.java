package com.github.lorenj.wordtint.ui.markdown.spanfactory.span;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.LineHeightSpan;

import androidx.annotation.NonNull;

import com.github.lorenj.wordtint.utils.DPUtils;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.HeadingSpan;

/**
 * 没有下划线的标题栏HeadingSpan
 *
 * @author cnsukidayo
 * @date 2023/1/12 14:40
 */
public class NoUnderLineHeadingSpan extends HeadingSpan implements LineHeightSpan {
    private final Rect rect = new Rect();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int level;

    public NoUnderLineHeadingSpan(@NonNull MarkwonTheme theme, int level) {
        super(theme, level);
        this.level = level;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {

        paint.getTextBounds(text.toString(), 0, text.length(), rect);
        int textHeight = Math.max((int) paint.getTextSize(), rect.bottom - rect.top);
        // 根据当前不同的等级,拥有不同的行距
        int paddingTop = 0;
        switch (level) {
            case 1:
                paddingTop = DPUtils.dp2px(6);
                break;
            case 2:
                paddingTop = DPUtils.dp2px(5);
                break;
            case 3:
                paddingTop = DPUtils.dp2px(4);
                break;
            case 4:
                paddingTop = DPUtils.dp2px(3);
                break;
            case 5:
                paddingTop = DPUtils.dp2px(2);
                break;
            case 6:
                paddingTop = DPUtils.dp2px(1);
                break;
        }
        fm.descent -= 40 - textHeight * 2;
    }
}
