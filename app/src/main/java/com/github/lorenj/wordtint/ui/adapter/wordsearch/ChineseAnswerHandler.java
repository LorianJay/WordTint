package com.github.lorenj.wordtint.ui.adapter.wordsearch;

import android.content.Context;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.utils.FileUtils;
import com.github.lorenj.wordtint.utils.RegularUtils;
import com.github.lorenj.wordtint.R;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author cnsukidayo
 * @date 2024/7/20 14:02
 */
public class ChineseAnswerHandler {
    private final View rootView;
    private final Context context;
    private final WebView chineseAnswer;
    private final String template;

    public ChineseAnswerHandler(View rootView) {
        this.rootView = rootView;
        this.context = rootView.getContext();
        this.chineseAnswer = rootView.findViewById(R.id.fragment_word_credit_chinese_answer);
        WebSettings webSettings = this.chineseAnswer.getSettings();
        // 支持javascript
        webSettings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        try (InputStream welcomeInputStream = context.getAssets().open("template/english.html");) {
            template = FileUtils.readAll(welcomeInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showWordChineseMessage(WordDTOLocal wordDTOLocal) {
        String jsonMessage = StaticFactory.getGson().toJson(wordDTOLocal);
        DocumentContext documentContext = JsonPath.parse(jsonMessage);
        List<String> htmlRegexList = RegularUtils.match(template, "\\{\\{.+\\}\\}");
        String renderHtml = template;
        for (String htmlRegex : htmlRegexList) {
            String jsonPath = htmlRegex.replace("{{", "")
                    .replace("}}", "");
            String readValue = "";
            try {
                readValue = documentContext.read(jsonPath);
            } catch (Exception e) {

            }
            renderHtml = renderHtml.replace(htmlRegex, readValue);
        }
        chineseAnswer.loadData(renderHtml, "text/html", StandardCharsets.UTF_8.name());
        this.chineseAnswer.setVisibility(View.VISIBLE);
    }

    public void gone() {
        this.chineseAnswer.setVisibility(View.GONE);
    }

}
