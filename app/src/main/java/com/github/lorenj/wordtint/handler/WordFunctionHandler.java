package com.github.lorenj.wordtint.handler;

import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordFunctionState;
import com.github.lorenj.wordtint.handler.state.WordFunctionHandlerState;

public interface WordFunctionHandler extends CategoryFunctionHandler {
    /**
     * 得到单词列表长度
     *
     * @return 返回长度
     */
    int functionWordSize();

    /**
     * 得到当前的内部的index<br>
     * 区别{@link WordFunctionHandler#getChameleonOrder()}
     *
     * @return 返回index
     */
    int getInnerIndex();

    /**
     * 根据单词在列表中的位序获取单词,该方法的返回值会受到Chameleon的改变而改变.
     *
     * @param index 单词在列表中的位序,注意order的顺序是从0开始的.
     * @return 返回单词的引用(单词的信息是一个集合)
     */
    FunctionWordVO getWordByIndex(int index);

    /**
     * 跳转到上一个单词,调用该方法会将指针指向传入的索引位置
     *
     * @return 返回单词引用
     */
    FunctionWordVO gotoPreviousWord();

    /**
     * 跳转到下一个单词,调用该方法会将指针指向传入的索引位置
     *
     * @return 返回单词引用
     */
    FunctionWordVO gotoNextWord();

    /**
     * 跳转到某个单词,调用该方法会将指针指向传入的索引位置<br>
     * 该方法会考虑单词标记<br>
     * 当前方法是跳转,而 {@link WordFunctionHandler#getWordByIndex(int)} 仅仅是得到单词的信息
     *
     * @param index 跳转的目标位序
     * @return 返回单词引用
     */
    FunctionWordVO gotoWordWithIndex(int index);

    /**
     * 强制跳转到索引指向的单词,不会考虑单词标记
     *
     * @param index 单词索引
     * @return 返回单词引用
     * @see WordFunctionHandler#gotoWordWithIndex(int)
     */
    FunctionWordVO forceGotoWordWithOutMarkColor(int index);

    /**
     * 根据当前的chameleonColor进行打乱,也就是按颜色打乱.
     * 当前在打乱的状态下是不能使用变色龙模式的,要想使用变色龙模式必须先退出打乱模式.
     */
    void shuffle();

    /**
     * 根据左右区间打乱列表,注意这里的左右区间是闭区间.<br>
     * 一旦打乱则当前单词功能的状态会切换为{@link WordFunctionState#RANGE}
     *
     * @param start 左区间的值,下标从0开始
     * @param end   右区间的值,该值不应该超过列表的{@link WordFunctionHandler#functionWordSize()}-1
     */
    void shuffleRange(int start, int end);

    /**
     * 还原单词列表为初始列表,该方法可以还原由 {@link WordFunctionHandler#shuffle()}方法和<br>
     * {@link WordFunctionHandler#shuffleRange(int, int)}方法改变的单词列表顺序.
     */
    void restoreWordList();

    /**
     * 得到当前功能区域的所有涉及状态
     * @return 返回功能区状态实例
     */
    WordFunctionHandlerState getWordFunctionHandlerState();

    /**
     * 保存背诵进度
     */
    void saveProgress();

    /**
     * 得到当前变色龙的单词的数量
     *
     * @return 返回单词数量
     */
    int getChameleonSize();

    /**
     * 得到当前单词在当前变色龙列表中的顺序
     *
     * @return 返回单词顺序
     */
    int getChameleonOrder();

    /**
     * 根据单词查找该单词在列表中的索引位置
     *
     * @param origin 源单词内容
     * @return 单词索引, 若没找到返回-1
     */
    int getIndexByWordOrigin(String origin);

}
