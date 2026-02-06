package com.github.lorenj.wordtint.context.support.factory;

import android.content.Context;

import androidx.navigation.NavOptions;

import com.github.lorenj.wordtint.R;
import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;
import com.github.lorenj.wordtint.context.pathsystem.document.WordContextPath;
import com.github.lorenj.wordtint.context.support.category.WordMetaInfoFilter;
import com.github.lorenj.wordtint.context.support.category.WordMetaInfoFilterImpl;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.ui.markdown.plugin.GlobalMarkwonPlugin;
import com.github.lorenj.wordtint.utils.JsonUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final class EmptyWordHolder {
        static final WordDTOLocal EMPTY_WORD = new WordDTOLocal();
    }

    private static final class SimpleNavOptionsHolder {
        static final NavOptions NAV_OPTIONS = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.fade_out)
                .setPopExitAnim(R.anim.slide_out_left)
                .build();
    }

    private static final class CssInlineStyleParserHolder {
        static final CssInlineStyleParser CSS_INLINE_STYLE_PARSER = CssInlineStyleParser.create();
    }

    private static final class WordMetaInfoFilterHolder {
        static final WordMetaInfoFilterImpl WORD_META_INFO_FILTER = new WordMetaInfoFilterImpl();
    }

    private static final class WordDictHolder {
        static final Map<Long, WordDTOLocal> ALL_WORD_DICT = new HashMap<>();

        static {
            File file = new File(AnyLanguageWordProperties.getExternalFilesDir(), WordContextPath.WORD_DICT.getPath());
            List<WordDTOLocal> allWordList = new ArrayList<>();
            for (File singleWordFile : file.listFiles()) {
                try {
                    allWordList.addAll(JsonUtils.readJsonArray(singleWordFile.getAbsolutePath().replace(AnyLanguageWordProperties.getExternalFilesDir().getAbsolutePath(), ""),
                            WordDTOLocal.class));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            for (WordDTOLocal wordDTOLocal : allWordList) {
                ALL_WORD_DICT.put(wordDTOLocal.getId(),wordDTOLocal);
            }
        }
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
     * 得到一个空的单词,注意该单词是单利Bean.
     *
     * @return 获取一个空单词
     */
    public static WordDTOLocal getEmptyWord() {
        return EmptyWordHolder.EMPTY_WORD;
    }

    /**
     * 得到navigation控制页面跳转的进入和退出动画参数默认示例对象
     *
     * @return 返回封装动画参数的NavOptions实例
     */
    public static NavOptions getSimpleNavOptions() {
        return SimpleNavOptionsHolder.NAV_OPTIONS;
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

    /**
     * 得到单词元数据过滤Map获取器
     *
     * @return 返回单利的元数据过滤获取器
     */
    public static WordMetaInfoFilter getWordMetaInfoFilter() {
        return WordMetaInfoFilterHolder.WORD_META_INFO_FILTER;
    }


    /**
     * 得到所有单词的字典
     *
     * @return 返回单词的Map
     */
    public static Map<Long, WordDTOLocal> getAllWordDict() {
        return WordDictHolder.ALL_WORD_DICT;
    }


}
