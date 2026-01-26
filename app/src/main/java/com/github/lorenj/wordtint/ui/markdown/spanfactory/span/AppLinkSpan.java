package com.github.lorenj.wordtint.ui.markdown.spanfactory.span;

import android.text.TextPaint;

import androidx.annotation.NonNull;

import io.noties.markwon.LinkResolver;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.LinkSpan;

/**
 * @author cnsukidayo
 * @date 2023/2/6 19:41
 */
public class AppLinkSpan extends LinkSpan {
    public AppLinkSpan(@NonNull MarkwonTheme theme, @NonNull String link, @NonNull LinkResolver resolver) {
        super(theme, link, resolver);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
    }



}
