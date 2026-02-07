package com.github.lorenj.wordtint.ui.markdown.handler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.lorenj.wordtint.context.factory.StaticFactory;

import java.util.Collection;
import java.util.List;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.html.CssProperty;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.tag.SimpleTagHandler;

/**
 * @author cnsukidayo
 * @date 2023/2/6 15:59
 */
public class SpanHandler extends SimpleTagHandler {
    private final Context context;

    public SpanHandler(Context context) {
        this.context = context;
    }

    @SuppressLint("DiscouragedApi")
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps, @NonNull HtmlTag tag) {
        int colorID = 0;
        String app = tag.attributes().get("app");
        String style = tag.attributes().get("style");
        if (!TextUtils.isEmpty(app)) {
            // 先解析自定义的app标签,先从项目路径下寻找颜色,如果不能找到则再从Android下获取颜色
            for (CssProperty property : StaticFactory.getCssInlineStyleParser().parse(app)) {
                switch (property.key()) {
                    case "color":
                        colorID = context.getResources().getIdentifier(property.value(), "color", context.getPackageName());
                        if (colorID == 0) {
                            colorID = context.getResources().getIdentifier(property.value(), "color", "android");
                        }
                        if (colorID == 0) {
                            return null;
                        }
                        break;
                }
            }
            return new ForegroundColorSpan(context.getResources().getColor(colorID, null));
        }
        // 如果没有定义app级别的标签,则尝试解析原生HTML标签
        if (!TextUtils.isEmpty(style)) {
            for (CssProperty property : StaticFactory.getCssInlineStyleParser().parse(style)) {
                switch (property.key()) {
                    case "color":
                        colorID = Color.parseColor(property.value());
                        if (colorID == 0) {
                            return null;
                        }
                        break;
                }
                return new ForegroundColorSpan(colorID);
            }
        }
        return null;
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return List.of("span");
    }
}
