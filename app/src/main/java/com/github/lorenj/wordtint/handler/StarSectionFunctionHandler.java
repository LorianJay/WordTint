package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;


/**
 * 单个分类单词功能的接口
 *
 * @author cnsukidayo
 * @date 2023/1/9 16:45
 */
public interface StarSectionFunctionHandler {

    /**
     * 得到某个具体分类(收藏夹)中单词的数量
     *
     * @return 返回int类型
     */
    int getStarWordCount(WordStarEntity wordStarEntity);

    /**
     * 将某个单词添加到某个分类中
     *
     * @param wordStarEntity       目标收藏夹
     * @param wordStarWordIdEntity 待添加的单词
     * @return 返回是否添加成功
     */
    boolean addWordToStar(WordStarWordIdEntity wordStarWordIdEntity);

    /**
     * 将某个单词从某个分类中移除
     *
     * @param wordStarWordIdEntity 收藏夹内的单词实体
     */
    void removeWordFromStar(WordStarWordIdEntity wordStarWordIdEntity);

    /**
     * 从某个分类中获取某个单词
     *
     * @param wordStarWordIdEntity 单词id
     * @return 返回Word引用
     */
    FunctionWordVO getWordDetailByWordId(WordStarWordIdEntity wordStarWordIdEntity);

    /**
     * 批量更新某一个收藏夹内的单词<br>
     * 因为收藏夹内单词的位置位置不断地变化,为避免频繁更新所以使用该方法统一发送请求更新收藏夹单词顺序<br>
     */
    void batchUpdateStarInnerWordList(WordStarEntity wordStarEntity);

    /**
     * 交换收藏夹中两个单词的位置
     *
     * @param fromPosition     源单词的位置
     * @param toPosition       目标单词的位置
     */
    void moveStarInnerWord(WordStarWordIdEntity fromPosition, WordStarWordIdEntity toPosition);

}
