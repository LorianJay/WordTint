package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.database.vo.FunctionWordVO;

/**
 * @author lorianjay
 * @date 2026/7/25 12:31
 */
public interface WordOriginEditHandler {

    /**
     * 编辑单词词义
     *
     * @param functionWordVO 当前正在浏览的单词
     * @param confirm        同意后的回调函数
     */
    void edit(FunctionWordVO functionWordVO, Runnable confirm);

}
