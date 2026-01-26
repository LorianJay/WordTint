package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.entity.Word;

import java.util.Optional;

/**
 * @author cnsukidayo
 * @date 2023/2/11 17:51
 */
public interface AssociationModeHandler {

    /**
     * 检查单词,传入一个单词,根据单词拼写检查该单词能否在比对库中比对得到,将得到的单词返回到Optional&lt;Word&gt;中.
     * 返回的单词有可能为空,那就需要你自已处理逻辑,为空时实际上就代表无法查询到当前单词.
     *
     * @param wordOrigin 需要比对的单词引用英文
     * @return 返回比对的结果, 注意Optional对象可能封装的是一个null实例
     */
    Optional<Word> checkWord(String wordOrigin);

    /**
     * 返回上一个比对成功的单词
     *
     * @return 返回值用一个Optional对象封装, 注意Optional对象封装的对象引用可能为null
     */
    Optional<Word> getCurrentWord();

}
