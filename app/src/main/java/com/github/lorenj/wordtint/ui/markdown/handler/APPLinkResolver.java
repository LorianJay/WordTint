package com.github.lorenj.wordtint.ui.markdown.handler;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;

import io.noties.markwon.LinkResolver;
import io.noties.markwon.LinkResolverDef;

/**
 * @author cnsukidayo
 * @date 2023/2/6 17:22
 */
public class APPLinkResolver extends LinkResolverDef {
    private static LinkResolver linkResolver;

    @Override
    public void resolve(@NonNull View view, @NonNull String link) {
        // 如果是APP内自定义的语法,则跳转到目标页面
        if (link.startsWith("navigation:")) {
            String navigationAction = link.replaceFirst("navigation:", "");
            Log.d(String.valueOf(this.getClass()), "跳转的目标页面navigationAction:" + navigationAction);
            int id = view.getContext().getResources().getIdentifier(navigationAction, "id", view.getContext().getPackageName());
            Navigation.findNavController(view).navigate(id, null, StaticFactory.getSimpleNavOptions());
            return;
        }
        super.resolve(view, link);
    }

    public static LinkResolver getInstance() {
        if (linkResolver == null) {
            return new APPLinkResolver();
        }
        return linkResolver;
    }

}
