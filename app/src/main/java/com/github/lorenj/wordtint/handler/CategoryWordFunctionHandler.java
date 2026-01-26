package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.entity.dto.WordCategoryWordDTO;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;


/**
 * 单个分类单词功能的接口
 *
 * @author cnsukidayo
 * @date 2023/1/9 16:45
 */
public interface CategoryWordFunctionHandler extends WordFunctionContextHandler {

    /**
     * 得到某个具体分类(收藏夹)中单词的数量
     *
     * @return 返回int类型
     */
    int currentCategorySize(int categoryPosition);

    /**
     * 将某个单词添加到某个分类中
     *
     * @param categoryPosition    分类的位置
     * @param wordCategoryWordDTO 待添加的单词
     * @return 返回是否添加成功
     */
    boolean addWordToCategory(int categoryPosition, WordCategoryWordDTO wordCategoryWordDTO);

    /**
     * 将某个单词从某个分类中移除
     *
     * @param categoryPosition 分类ID
     * @param position         单词在category中的位置
     */
    void removeWordFromCategory(int categoryPosition, int position);

    /**
     * 从某个分类中获取某个单词
     *
     * @param categoryPosition 分类在列表中的位置
     * @param position         单词在category中的位置
     * @return 返回Word引用
     */
    WordDTOLocal getWordFromCategory(int categoryPosition, int position);

    /**
     * 这个方法实际上是一种状态的刷新
     * 因为收藏夹内单词的位置位置不断地变化,为避免频繁更新所以使用该方法统一发送请求更新收藏夹单词顺序<br>
     * 本方法不负责维护单词的Order值;而是直接更新;如果需要维护某收藏夹中单词的Order值则通过{@link CategoryWordFunctionHandler#moveCategoryWord(int, int, int)}方法来维护
     *
     * @param categoryPosition 收藏夹的位置
     */
    void updateWordCategoryWordOrder(int categoryPosition);

    /**
     * 交换收藏夹中两个单词的位置
     *
     * @param categoryPosition 收藏夹的位置
     * @param fromPosition     源单词的位置
     * @param toPosition       目标单词的位置
     */
    void moveCategoryWord(int categoryPosition, int fromPosition, int toPosition);

}
