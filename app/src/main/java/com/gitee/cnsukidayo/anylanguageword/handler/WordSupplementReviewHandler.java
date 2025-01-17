package com.gitee.cnsukidayo.anylanguageword.handler;

import com.gitee.cnsukidayo.anylanguageword.entity.local.AddWordReViewParamLocal;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;

import java.util.ArrayList;

/**
 * @author cnsukidayo
 * @date 2024/7/21 10:38
 */
public interface WordSupplementReviewHandler {

    /**
     * 添加一个增量背诵单词记录
     *
     * @param addWordAnalysisParamLocal 单词添加参数
     */
    void insertWordReView(AddWordReViewParamLocal addWordAnalysisParamLocal);

    /**
     * 删除一个单词背诵复习
     *
     * @param id 记录id
     */
    void deleteWordReView(int id);

    /**
     * 查询某个标记下的所有增量单词
     *
     * @param flagColor 目标单词标记
     * @return 返回单词的所有id
     */
    ArrayList<Long> querySupplementByFlagColor(FlagColor flagColor);

    /**
     * 统计某个标记单词当前的增量单词是多少
     *
     * @param flagColor 目标单词的标记
     * @return 返回增量总数
     */
    int countSupplementFlagColor(FlagColor flagColor);

}
