package com.github.lorenj.wordtint.handler.state;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.enums.ReciteMode;
import com.github.lorenj.wordtint.enums.WordFunctionState;

/**
 * @author cnsukidayo
 * @date 2026/2/1 18:13
 */
public class WordFunctionHandlerState {

    /**
     * 当前的背诵模式
     */
    private ReciteMode currentReciteMode;

    /**
     * 当前单词功能的状态
     */
    private WordFunctionState wordFunctionState;

    /**
     * 功能区是否被折叠
     */
    private boolean functionAreaFold = false;

    private boolean selectChameleon = false;

    /**
     * 当前变色龙的颜色
     */
    private final MutableLiveData<MarkColor> currentChameleon =
            new MutableLiveData<>(MarkColor.GREEN);

    /**
     * 得到当前的背诵模式
     *
     * @return 非空
     */
    public ReciteMode getCurrentReciteMode() {
        return currentReciteMode;
    }

    public void setCurrentReciteMode(ReciteMode currentReciteMode) {
        this.currentReciteMode = currentReciteMode;
    }

    public WordFunctionState getWordFunctionState() {
        return wordFunctionState;
    }

    public void setWordFunctionState(WordFunctionState wordFunctionState) {
        this.wordFunctionState = wordFunctionState;
    }

    public boolean isFunctionAreaFold() {
        return functionAreaFold;
    }

    public void setFunctionAreaFold(boolean functionAreaFold) {
        this.functionAreaFold = functionAreaFold;
    }

    public boolean isSelectChameleon() {
        return selectChameleon;
    }

    public void setSelectChameleon(boolean selectChameleon) {
        this.selectChameleon = selectChameleon;
    }


    /**
     * 得到当前的变色龙状态,默认状态为FlagColor.GREEN
     *
     * @return {@link MarkColor} 返回代表变色龙的颜色.
     * @see MarkColor
     */
    public LiveData<MarkColor> getChameleon() {
        return currentChameleon;
    }

    /**
     * 设置变色龙颜色,此时函数的各个方法的返回值都会因为FlagColor的改变而改变.
     * 每次更改变色龙颜色之后,单词的索引都会从0开始
     *
     * @param chameleonColor FlagColor
     */
    public void setChameleon(MarkColor chameleonColor) {
        this.currentChameleon.setValue(chameleonColor);
    }


}
