package com.gitee.cnsukidayo.anylanguageword.handler;

import com.gitee.cnsukidayo.anylanguageword.entity.local.AddWordAnalysisParamLocal;
import com.gitee.cnsukidayo.anylanguageword.entity.local.WordAnalysisLocal;
import com.gitee.cnsukidayo.anylanguageword.entity.local.WordFlagRankLocal;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;

import io.github.cnsukidayo.wword.model.dto.support.DataPage;
import io.github.cnsukidayo.wword.model.params.PageQueryParam;

/**
 * @author cnsukidayo
 * @date 2024/7/21 10:38
 */
public interface WordAnalysisHandler {

    /**
     * 根据单词的id查询单词的分析信息
     *
     * @param id 单词id
     * @return
     */
    WordAnalysisLocal queryWordAnalysis(int id);

    /**
     * 添加一个单词记录
     *
     * @param addWordAnalysisParamLocal 单词添加参数
     */
    void insertWordAnalysis(AddWordAnalysisParamLocal addWordAnalysisParamLocal);

    DataPage<WordFlagRankLocal> pageQueryFlagRankByFlagColor(FlagColor flagColor, PageQueryParam pageQueryParam);
}
