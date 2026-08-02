package com.github.lorenj.wordtint.handler.state;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.enums.ReciteMode;
import com.github.lorenj.wordtint.enums.RecitePreposition;
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
     * 是否隐藏介词
     */
    private RecitePreposition recitePreposition = RecitePreposition.VISIBLE;
    /**
     * 当前单词功能的状态
     */
    private WordFunctionState wordFunctionState;

    /**
     * 功能区(右侧标记区域)是否被折叠
     */
    private boolean functionAreaFold = false;

    /**
     * 是否开启蓝牙
     */
    private boolean enableBlueTooth = false;

    /**
     * 是否开启手写拼写
     */
    private boolean enableHandwriting = false;

    /**
     * 当前是否正在选择标记颜色
     */
    private boolean selectChameleon = false;

    /**
     * 当前变色龙的颜色
     */
    private final MutableLiveData<MarkColor> currentChameleon =
            new MutableLiveData<>(MarkColor.BROWN);

    /**
     * 当前是否锁定了light的位置
     */
    private boolean lockLight = true;

    /**
     * 是否开启滑动切换
     */
    private boolean enableSwitch = false;
    /**
     * 当前是否正在滑动切换
     */
    private boolean switching = false;
    /**
     * 第一次滑动切换时的默认位置
     */
    private int previousFocusSwitchPosition = 4;
    /**
     * 当前正聚焦的选择位置
     */
    private int currentFocusSwitchPosition = previousFocusSwitchPosition;

    /**
     * 第一次蓝牙选择的默认位置
     */
    private int previousFocusBlueToothPosition = 4;
    /**
     * 当前蓝牙选择的默认位置
     */
    private int currentFocusBlueToothPosition = previousFocusBlueToothPosition;

    /**
     * 是否正在排序收藏夹
     */
    private boolean sortStar = false;

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

    public MutableLiveData<MarkColor> getCurrentChameleon() {
        return currentChameleon;
    }

    public boolean isLockLight() {
        return lockLight;
    }

    public void setLockLight(boolean lockLight) {
        this.lockLight = lockLight;
    }

    public boolean isEnableSwitch() {
        return enableSwitch;
    }

    public void setEnableSwitch(boolean enableSwitch) {
        this.enableSwitch = enableSwitch;
    }

    public boolean isSwitching() {
        return switching;
    }

    public void setSwitching(boolean switching) {
        this.switching = switching;
    }

    public int getPreviousFocusSwitchPosition() {
        return previousFocusSwitchPosition;
    }

    public void setPreviousFocusSwitchPosition(int previousFocusSwitchPosition) {
        this.previousFocusSwitchPosition = previousFocusSwitchPosition;
    }

    public int getCurrentFocusSwitchPosition() {
        return currentFocusSwitchPosition;
    }

    public void setCurrentFocusSwitchPosition(int currentFocusSwitchPosition) {
        this.currentFocusSwitchPosition = currentFocusSwitchPosition;
    }

    public RecitePreposition getRecitePreposition() {
        return recitePreposition;
    }

    public void setRecitePreposition(RecitePreposition recitePreposition) {
        this.recitePreposition = recitePreposition;
    }

    public boolean isSortStar() {
        return sortStar;
    }

    public void setSortStar(boolean sortStar) {
        this.sortStar = sortStar;
    }

    public boolean isEnableBlueTooth() {
        return enableBlueTooth;
    }

    public void setEnableBlueTooth(boolean enableBlueTooth) {
        this.enableBlueTooth = enableBlueTooth;
    }

    public boolean isEnableHandwriting() {
        return enableHandwriting;
    }

    public void setEnableHandwriting(boolean enableHandwriting) {
        this.enableHandwriting = enableHandwriting;
    }

    public int getPreviousFocusBlueToothPosition() {
        return previousFocusBlueToothPosition;
    }

    public void setPreviousFocusBlueToothPosition(int previousFocusBlueToothPosition) {
        this.previousFocusBlueToothPosition = previousFocusBlueToothPosition;
    }

    public int getCurrentFocusBlueToothPosition() {
        return currentFocusBlueToothPosition;
    }

    public void setCurrentFocusBlueToothPosition(int currentFocusBlueToothPosition) {
        this.currentFocusBlueToothPosition = currentFocusBlueToothPosition;
    }
}
