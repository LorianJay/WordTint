package com.github.lorenj.wordtint.ui.markdown.spanfactory.span;

import android.graphics.Paint;
import android.text.Spanned;
import android.text.style.LineHeightSpan;

import androidx.annotation.NonNull;

import com.github.lorenj.wordtint.ui.markdown.plugin.MarkwonThemeAdapter;

import io.noties.markwon.core.spans.HeadingSpan;

/**
 * @author cnsukidayo
 * @date 2023/2/7 10:40
 */
public class AppHeadingSpan extends HeadingSpan implements LineHeightSpan {
    private final MarkwonThemeAdapter markwonThemeAdapter;

    public AppHeadingSpan(@NonNull MarkwonThemeAdapter theme, int level) {
        super(theme, level);
        this.markwonThemeAdapter = theme;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        if (selfEnd(end, text, this)) {
            // 可以通过这种方式动态地设置每个Heading与Heading之间的行距
            // 也可以设置一个Heading中每行之间的行距
            fm.descent += markwonThemeAdapter.getBetweenHeadingHeight();
            fm.bottom += markwonThemeAdapter.getBetweenHeadingHeight();
        }
    }

    private boolean selfEnd(int end, CharSequence text, Object span) {
        final int spanEnd = ((Spanned) text).getSpanEnd(span);
        return spanEnd == end || spanEnd == end - 1;
    }
}
