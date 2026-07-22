package com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.lorenj.wordtint.database.vo.TextSegmentationVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextSegmentationViewModel extends ViewModel {

    private final MutableLiveData<List<TextSegmentationVO>> segmentationList = new MutableLiveData<>(new ArrayList<>());

    /**
     * 分段正则表达式：排除常见缩写与数字后面的句号
     */
    private final static String REGEX = "(?<!\\d|Mr|Ms|Dr|Vs|e\\.g|i\\.e)\\.";

    public LiveData<List<TextSegmentationVO>> getSegmentationList() {
        return segmentationList;
    }

    /**
     * 文字分段: 清理 PDF 强制换行，忽略引号内部句号，拆分为 VO 列表
     */
    public void segmentText(String input) {
        if (input == null || input.trim().isEmpty()) {
            segmentationList.setValue(new ArrayList<>());
            return;
        }

        // 1. 清理 PDF 产生的换行符与多余空格
        String cleanText = input.replaceAll("\\r?\\n", " ")
                .replaceAll("[ \\t]+", " ");

        // 2. 抽取并保护成对引号（“” 和 ""）内部的内容
        Pattern quotePattern = Pattern.compile("[\"“](.*?)[\"”]");
        Matcher quoteMatcher = quotePattern.matcher(cleanText);

        List<String> preservedQuotes = new ArrayList<>();
        StringBuffer sb = new StringBuffer();

        while (quoteMatcher.find()) {
            preservedQuotes.add(quoteMatcher.group(0)); // 存下完整的引号内容
            quoteMatcher.appendReplacement(sb, "___QUOTE_PLACEHOLDER_" + (preservedQuotes.size() - 1) + "___");
        }
        quoteMatcher.appendTail(sb);

        String maskedText = sb.toString();

        // 3. 按句号分割（此时引号内的句号已被安全掩码，不会触发 split）
        List<TextSegmentationVO> allSegmentationList = Arrays.stream(maskedText.split(REGEX))
                .map(String::trim)
                .filter(s -> !s.isEmpty()) // 过滤掉空句子
                .map(s -> {
                    // 补充分隔时丢失的句号
                    String segmentWithPeriod = s + ".";

                    // 4. 将该句中的占位符还原为原始的引号内容
                    for (int i = 0; i < preservedQuotes.size(); i++) {
                        String placeholder = "___QUOTE_PLACEHOLDER_" + i + "___";
                        if (segmentWithPeriod.contains(placeholder)) {
                            segmentWithPeriod = segmentWithPeriod.replace(placeholder, preservedQuotes.get(i));
                        }
                    }

                    TextSegmentationVO vo = new TextSegmentationVO();
                    vo.setSegmentationText(segmentWithPeriod);
                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 更新 LiveData
        segmentationList.setValue(allSegmentationList);
    }
}