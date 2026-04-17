package com.github.lorenj.wordtint.context.factory;

import android.content.Context;

import com.github.lorenj.wordtint.handler.WordAudioHandler;
import com.github.lorenj.wordtint.handler.impl.WordAudioHandlerImpl;
import com.github.lorenj.wordtint.ui.markdown.plugin.GlobalMarkwonPlugin;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.CssInlineStyleParser;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;

public class StaticFactory {

    private StaticFactory() {
    }

    private static final class GsonHolder {
        static final Gson GSON = new Gson();
    }

    private static final class ExecutorServiceHolder {
        static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(4,
                8,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    private static final class CssInlineStyleParserHolder {
        static final CssInlineStyleParser CSS_INLINE_STYLE_PARSER = CssInlineStyleParser.create();
    }

    private static final class WordAudioHandlerHolder {
        static final WordAudioHandler WORD_AUDIO_HANDLER = new WordAudioHandlerImpl();
    }

    /**
     * 得到Gson实例
     *
     * @return 返回Gson实例
     */
    public static Gson getGson() {
        return GsonHolder.GSON;
    }

    /**
     * 得到全局的线程池,提高线程复用率
     *
     * @return 返回线程池
     */
    public static ExecutorService getExecutorService() {
        return ExecutorServiceHolder.EXECUTOR_SERVICE;
    }

    /**
     * 得到全局的markdown解析对象
     *
     * @param context 上下文
     * @return 返回全局解析对象
     */
    public static Markwon getGlobalMarkwon(Context context) {
        return Markwon.builder(context)
                .usePlugin(ImagesPlugin.create())
                .usePlugin(HtmlPlugin.create())
                .usePlugin(GlobalMarkwonPlugin.create(context))
                .build();
    }

    /**
     * 得到Css行内解析器
     *
     * @return 返回单利的Css行内解析器对象
     */
    public static CssInlineStyleParser getCssInlineStyleParser() {
        return CssInlineStyleParserHolder.CSS_INLINE_STYLE_PARSER;
    }

    public static WordAudioHandler wordAudioHandler() {
        return WordAudioHandlerHolder.WORD_AUDIO_HANDLER;
    }

}
