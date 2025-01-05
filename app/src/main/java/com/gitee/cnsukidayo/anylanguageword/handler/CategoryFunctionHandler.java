package com.gitee.cnsukidayo.anylanguageword.handler;

import com.gitee.cnsukidayo.anylanguageword.entity.local.WordDTOLocal;

import java.util.List;

import io.github.cnsukidayo.wword.model.dto.WordCategoryDTO;
import io.github.cnsukidayo.wword.model.dto.WordCategoryWordDTO;
import io.github.cnsukidayo.wword.model.vo.WordCategoryDetailVO;

/**
 * @author cnsukidayo
 * @date 2023/1/8 19:48
 */
public interface CategoryFunctionHandler extends CategoryWordFunctionHandler {
    /**
     * 得到当前停留的单词(得到当前正在背诵的单词)<br>
     * 该方法返回的单词是用户可能想要将其收藏到某个收藏夹内的单词.
     *
     * @return 返回当前正在背诵的单词引用
     */
    WordCategoryWordDTO getCurrentViewWord();

    /**
     * 添加一个单词分类
     *
     * @param wordCategoryDTO 单词类别实例对象
     */
    void addNewCategory(WordCategoryDTO wordCategoryDTO);

    /**
     * 批量添加单词收藏夹
     *
     * @param wordCategoryDetailVOList 单词分类列表
     */
    void batchAddCategory(List<WordCategoryDetailVO> wordCategoryDetailVOList);

    /**
     * 批量替换单词收藏夹
     *
     * @param wordCategoryDetailVOList 单词分类列表
     */
    void replaceAddCategory(List<WordCategoryDetailVO> wordCategoryDetailVOList);

    /**
     * 得到当前停留的单词(得到当前正在背诵的单词)<br>
     * 该方法返回的单词是用户可能想要将其收藏到某个收藏夹内的单词.<br>
     * 并转换成以单词结构id为Key的集合
     *
     * @return 返回当前正在背诵的单词引用
     */
    WordDTOLocal getCurrentStructureWordMap();

    /**
     * 更新单词收藏夹信息WordCategoryDTO
     *
     * @param position        收藏夹的position
     * @param wordCategoryDTO 单词收藏夹信息
     */
    void updateWordCategoryDto(int position, WordCategoryDTO wordCategoryDTO);

    /**
     * 这个方法实际上是一种状态的刷新<br>
     * 因为收藏夹位置不断地变化,为避免频繁更新所以使用该方法统一发送请求更新收藏夹顺序列表
     */
    void updateWordCategoryList();

    /**
     * 删除一个单词分类
     *
     * @param position 单词分类对象在列表中对应的位置
     */
    void removeCategory(int position);

    /**
     * 得到当前分类列表的长度
     *
     * @return 返回int值
     */
    int categoryListSize();

    /**
     * 根据单词分类在列表中的位置得到对应的WordCategory
     *
     * @param position 单词分类对象在列表中对应的位置
     * @return 返回WordCategory引用
     */
    WordCategoryDetailVO getWordCategoryByPosition(int position);

    /**
     * 根据规则计算出某个WordCategory的标题信息.<br>
     * 该方法的返回值会随着对应的WordCategory内容改变而改变.
     *
     * @param position 计算哪个收藏夹;单词分类对象在列表中对应的位置
     * @return 返回计算出的标题
     */
    String calculationTitle(int position);

    /**
     * 根据规则计算出某个WordCategory的描述信息.<br>
     * 该方法的返回值会随着对应的WordCategory内容改变而改变.
     *
     * @param position 计算哪个收藏夹;单词分类对象在列表中对应的位置
     * @return 返回计算出的描述信息
     */
    String calculationDescribe(int position);

    /**
     * 交换收藏夹列表中的两个收藏夹位置
     *
     * @param fromPosition 源收藏夹位置
     * @param toPosition   目标收藏夹位置
     */
    void moveCategory(int fromPosition, int toPosition);

}
