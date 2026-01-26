package com.github.lorenj.wordtint.ui.markdown.spanfactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.lorenj.wordtint.ui.markdown.spanfactory.span.AppLinkSpan;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.core.CoreProps;

/**
 * @author cnsukidayo
 * @date 2023/2/6 19:41
 */
public class AppLinkSpanFactory implements SpanFactory {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return new AppLinkSpan(configuration.theme(),
                CoreProps.LINK_DESTINATION.require(props),
                configuration.linkResolver());
    }
}
