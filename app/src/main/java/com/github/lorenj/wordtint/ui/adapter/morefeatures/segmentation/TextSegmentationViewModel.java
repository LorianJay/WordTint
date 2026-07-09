package com.github.lorenj.wordtint.ui.adapter.morefeatures.segmentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.lorenj.wordtint.database.vo.TextSegmentationVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextSegmentationViewModel extends ViewModel {

    private final MutableLiveData<List<TextSegmentationVO>> segmentationList = new MutableLiveData<>(new ArrayList<>());
    /**
     * 分段正则表达式
     */
    private final static String REGEX = "(?<!\\d|Mr|Ms|Dr|Vs|e\\.g|i\\.e)\\.(?!\\d|[a-zA-Z0-9-]+\\.[a-zA-Z])";

    public LiveData<List<TextSegmentationVO>> getSegmentationList() {
        return segmentationList;
    }

    /**
     * 文字分段: 删除所有换行符, 在句号结尾的句子后添加两个换行符
     *
     */
    public void segmentText(String input) {
        if (input == null || input.isEmpty()) {
            return;
        }
        String processed = input.replaceAll("\\r?\\n", " ");
        StringBuilder format = new StringBuilder();

        List<TextSegmentationVO> allSegmentationList = Arrays.stream(processed.split(REGEX))
                .map(s -> {
                    format.setLength(0);
                    TextSegmentationVO vo = new TextSegmentationVO();
                    vo.setSegmentationText(format.append(s.trim()).append(".").toString());
                    return vo;
                })
                .collect(Collectors.toList());

        segmentationList.setValue(allSegmentationList);
    }
}