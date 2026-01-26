package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.entity.dto.DataPage;
import com.github.lorenj.wordtint.entity.dto.PageQueryParam;
import com.github.lorenj.wordtint.entity.local.AddWordAnalysisParamLocal;
import com.github.lorenj.wordtint.entity.local.WordAnalysisLocal;
import com.github.lorenj.wordtint.entity.local.WordFlagRankLocal;
import com.github.lorenj.wordtint.enums.FlagColor;

import java.util.ArrayList;


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

    /**
     * 分页查询单词排行榜
     *
     * @param flagColor      目标单词标记
     * @param pageQueryParam 分页查询参数
     * @return 返回分页查询结果
     */
    DataPage<WordFlagRankLocal> pageQueryFlagRankByFlagColor(FlagColor flagColor, PageQueryParam pageQueryParam);

    /**
     * 查询某个标记下的所有单词
     *
     * @param flagColor 目标单词标记
     * @return 返回分页查询结果
     */
    ArrayList<Long> queryFlagRankByFlagColor(FlagColor flagColor);

    /**
     * 统计某个标记单词有多少种颜色
     *
     * @param flagColor 目标单词的标记
     * @return 返回目标单词标记的总数
     */
    int countFlagRankByFlagColor(FlagColor flagColor);

}
