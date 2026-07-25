package com.github.lorenj.wordtint.ui.adapter.wordsearch;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.lorenj.wordtint.context.factory.StaticFactory;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.utils.FileUtils;
import com.github.lorenj.wordtint.utils.RegularUtils;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author cnsukidayo
 * @date 2024/7/20 14:02
 */
public class ResultWebViewHandler {
    private final Context context;
    private final WebView resultWebView;
    private final String template;

    public ResultWebViewHandler(Context context, WebView resultWebView) {
        this.context = context;
        this.resultWebView = resultWebView;
        WebSettings webSettings = this.resultWebView.getSettings();
        // 支持javascript
        webSettings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        try (InputStream welcomeInputStream = context.getAssets().open("template/english.html");) {
            template = FileUtils.inputStreamToString(welcomeInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void displayWordResult(FunctionWordVO functionWordVO) {
        String jsonMessage = StaticFactory.getGson().toJson(functionWordVO);
        DocumentContext documentContext = JsonPath.parse(jsonMessage);
        List<String> htmlRegexList = RegularUtils.match(template, "\\{\\{.+\\}\\}");
        String renderHtml = template;
        for (String htmlRegex : htmlRegexList) {
            String jsonPath = htmlRegex.replace("{{", "")
                    .replace("}}", "");
            String readValue = "";
            try {
                Object rawValue = documentContext.read(jsonPath);
                if (rawValue instanceof Map) {
                    // WordOriginVO 序列化为嵌套对象，提取有效值
                    Map<?, ?> map = (Map<?, ?>) rawValue;
                    Object customValue = map.get("customValue");
                    readValue = (customValue != null && !customValue.toString().isEmpty())
                            ? customValue.toString()
                            : String.valueOf(map.get("value"));
                } else if (rawValue != null) {
                    readValue = rawValue.toString();
                }
            } catch (Exception e) {
                Log.e("ResultWebViewHandler", e.getMessage(), e);
            }
            if (readValue != null) {
                readValue = readValue.replace("\\n", "\n");
            }
            renderHtml = renderHtml.replace(htmlRegex, readValue);
        }
        resultWebView.loadDataWithBaseURL("file:///android_asset/", renderHtml, "text/html", "utf-8", null);
        this.resultWebView.setVisibility(View.VISIBLE);
    }

    public void gone() {
        this.resultWebView.setVisibility(View.GONE);
    }

}
