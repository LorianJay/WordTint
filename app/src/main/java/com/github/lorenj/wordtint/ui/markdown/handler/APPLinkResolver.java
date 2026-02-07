package com.github.lorenj.wordtint.ui.markdown.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

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
        if (link.startsWith("activity:")) {
            String targetActivity = link.replaceFirst("activity:", "");
            Log.d(String.valueOf(this.getClass()), "跳转的目标页面activity:" + targetActivity);
            Context context = view.getContext();
            String packageName = context.getPackageName();
            Class<?> clazz = null;
            try {
                clazz = Class.forName(packageName + ".ui.activity." + targetActivity);
                Intent intent = new Intent(context, clazz);
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                return;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
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
