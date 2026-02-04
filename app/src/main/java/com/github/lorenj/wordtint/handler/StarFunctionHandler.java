package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;

import java.util.List;
import java.util.Map;


/**
 * @author cnsukidayo
 * @date 2023/1/8 19:48
 */
public interface StarFunctionHandler extends StarSectionFunctionHandler {

    /**
     * 获取单词字典<br>
     * 该方法需要由子类实现
     *
     * @return 返回单词的字典
     */
    Map<Integer, FunctionWordVO> getDict();

    /**
     * 得到当前停留的单词(得到当前正在背诵的单词/或者当前选择的单词的id/当前搜索到的单词的id)<br>
     * 该方法返回的单词是用户可能想要将其收藏到某个收藏夹内的单词.
     *
     * @return 返回当前选中的单词的id
     */
    Integer getCurrentFocusWordId();

    /**
     * 得到当前停留的单词(得到当前选中的单词)<br>
     * 该方法返回的单词是用户可能想要将其收藏到某个收藏夹内的单词<br>
     * 所以有可能是正在背诵的单词/也有可能是当前搜索到的单词<br>
     * 该方法不能在UI线程调用,如果是搜索到的单词,那么此时字典里面很可能并没有该单词的信息<br>
     * 此时就必须从数据库查找单词的详情信息,该方法最好不要在UI线程执行!
     *
     * @return 返回当前正在背诵的单词引用
     */
    FunctionWordVO getCurrentFocusWord();

    /**
     * 添加一个单词分类
     *
     * @param wordStarEntity 单词类别实例对象
     */
    void createNewStar(WordStarEntity wordStarEntity);

    /**
     * 重新加载收藏夹
     */
    void reloadStar();

    /**
     * 得到所有的收藏夹列表
     */
    List<WordStarWithWordIdEntity> getAllStarList();

    /**
     * 更新单词收藏夹信息
     *
     * @param wordStarWithWordIdEntity 单词收藏夹信息
     */
    void updateStar(WordStarWithWordIdEntity wordStarWithWordIdEntity);

    /**
     * 这个方法实际上是一种状态的刷新<br>
     * 因为收藏夹位置不断地变化,为避免频繁更新所以使用该方法统一发送请求更新收藏夹顺序列表
     */
    void batchUpdateCurrentStar();

    /**
     * 删除一个收藏夹
     *
     * @param wordStarEntity 收藏夹对象
     */
    void removeStar(WordStarWithWordIdEntity wordStarWithWordIdEntity);

    /**
     * 根据规则计算出某个WordStarEntity的标题信息.<br>
     * 该方法的返回值会随着对应的WordStarEntity内容改变而改变.
     *
     * @param wordStarEntity 计算哪个收藏夹;单词分类对象在列表中对应的位置
     * @return 返回计算出的标题
     */
    String calculationTitle(WordStarEntity wordStarEntity);

    /**
     * 根据规则计算出某个WordStarEntity的描述信息.<br>
     * 该方法的返回值会随着对应的WordStarEntity内容改变而改变.
     *
     * @param wordStarEntity 计算哪个收藏夹;单词分类对象在列表中对应的位置
     * @return 返回计算出的描述信息
     */
    String calculationDescribe(WordStarEntity wordStarEntity);

    /**
     * 交换收藏夹列表中的两个收藏夹位置<br>
     * 该方法不应该执行持久化
     *
     * @param fromPosition 源收藏夹位置
     * @param toPosition   目标收藏夹位置
     */
    void moveStar(WordStarWithWordIdEntity fromStar, WordStarWithWordIdEntity toStar);

}
