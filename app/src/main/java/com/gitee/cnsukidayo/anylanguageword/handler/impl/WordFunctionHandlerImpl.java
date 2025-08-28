package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import com.gitee.cnsukidayo.anylanguageword.entity.local.FunctionWordDTOLocal;
import com.gitee.cnsukidayo.anylanguageword.entity.local.ProjectorDTOLocal;
import com.gitee.cnsukidayo.anylanguageword.entity.local.WordDTOLocal;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditState;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;
import com.gitee.cnsukidayo.anylanguageword.enums.WordFunctionState;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.WordFunctionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.cnsukidayo.wword.model.dto.WordCategoryWordDTO;

/**
 * 明确一点,currentIndex是不能随意更改的,每逢currentIndex更改势必是由currentOrder的更改而更改的.<br>
 * 每个单词都是有棕色的,棕色是不可变的颜色,也就是说用户不可以取消单词的棕色标记.<br>
 * 变色龙的每一种状态都是可以进入的,不管当前单词列表中是否有该颜色对应的单词<br>
 */
public class WordFunctionHandlerImpl extends AbstractCategoryFunctionHandler implements WordFunctionHandler, CategoryFunctionHandler {
    /**
     * 单词的摘要信息
     */
    private List<FunctionWordDTOLocal> allFunctionWordList;

    /**
     * 反查单词的Index
     */
    private Map<String, Integer> reverseQueryIndex;

    /**
     * 这是一个临时的集合,它指向allWordList,用于保存由按色打乱、区间重背功能被重置的allWordList引用
     */
    private List<FunctionWordDTOLocal> dummyWordList;

    /**
     * 当前单词背诵的指针,从0开始计数
     */
    private int currentIndex = 0;
    /**
     * 先前的背诵指针,用于恢复背诵进度
     */
    private int preIndex = 0;

    /**
     * 现在正在背诵的区间 start:19 end:29 -> [20,30]
     */
    private int start = 0, end = 0;

    /**
     * 当前变色龙的颜色
     */
    private FlagColor currentChameleon = FlagColor.GREEN;

    /**
     * 默认的单词功能为空
     */
    private WordFunctionState wordFunctionState = WordFunctionState.NONE;

    /**
     * 当前的背诵风格
     */
    private CreditState creditState = CreditState.ENGLISH_TRANSLATION_CHINESE_HEARING;

    /**
     * 当前的放映规则
     */
    private ProjectorDTOLocal projectorDTOLocal;
    private long startTimeMillis;

    /**
     * 是否隐藏介词
     */
    private boolean hideProNoun = false;

    /**
     * @param allFunctionWordList 所有功能性单词
     * @param dict                字典
     */
    public WordFunctionHandlerImpl(List<FunctionWordDTOLocal> allFunctionWordList,
                                   Map<Long, WordDTOLocal> dict) {
        this.allFunctionWordList = allFunctionWordList;
        super.addWordQueryCache(dict);
        initReverseQueryMap();
        this.start = 0;
        this.end = allFunctionWordList.size() - 1;
    }


    @Override
    public WordDTOLocal getWordByIndex(int index) {
        // 扁平化处理所有旗帜的单词
        return queryCache.get(allFunctionWordList.get(currentIndex).getId());
    }

    @Override
    public Set<FlagColor> getCurrentWordFlagColor() {
        return Collections.unmodifiableSet(allFunctionWordList.get(currentIndex).getWordsFlagList());
    }

    @Override
    public WordCategoryWordDTO getCurrentViewWord() {
        WordCategoryWordDTO wordCategoryWordDTO = new WordCategoryWordDTO();
        wordCategoryWordDTO.setWordId(allFunctionWordList.get(currentIndex).getId());
        return wordCategoryWordDTO;
    }

    @Override
    public WordDTOLocal jumpPreviousWord() {
        for (int tempIndex = currentIndex - 1; tempIndex != currentIndex; tempIndex--) {
            boolean flag = false;
            if (tempIndex < start) {
                tempIndex = end;
                flag = true;
            }
            if (allFunctionWordList.get(tempIndex).getWordsFlagList().contains(currentChameleon)) {
                currentIndex = tempIndex;
                break;
            }
            if (flag) {
                tempIndex++;
            }
        }
        return getWordByIndex(currentIndex);
    }

    @Override
    public WordDTOLocal jumpNextWord() {
        for (int tempIndex = currentIndex + 1; tempIndex != currentIndex; tempIndex++) {
            boolean flag = false;
            if (tempIndex > end) {
                tempIndex = start;
                flag = true;
            }
            if (allFunctionWordList.get(tempIndex).getWordsFlagList().contains(currentChameleon)) {
                currentIndex = tempIndex;
                break;
            }
            if (flag) {
                tempIndex--;
            }
        }
        return getWordByIndex(currentIndex);
    }

    @Override
    public WordDTOLocal jumpToWord(int currentIndex) {
        return getWordByIndex(this.currentIndex = findColorCursor(currentIndex));
    }

    @Override
    public WordDTOLocal jumpToWordWithOutFlag(int index) {
        return getWordByIndex(this.currentIndex = index);
    }

    @Override
    public int getCurrentIndex() {
        return this.currentIndex;
    }

    @Override
    public int size() {
        return allFunctionWordList.size();
    }


    @Override
    public boolean addFlagToCurrentWord(FlagColor tobeAddFlag) {
        return allFunctionWordList.get(currentIndex).getWordsFlagList().add(tobeAddFlag);
    }

    @Override
    public boolean removeFlagToCurrentWord(FlagColor tobeAddFlag) {
        return allFunctionWordList.get(currentIndex).getWordsFlagList().remove(tobeAddFlag);
    }

    @Override
    public FlagColor getChameleon() {
        return this.currentChameleon;
    }

    @Override
    public void setChameleon(FlagColor chameleonColor) {
        this.currentChameleon = chameleonColor;
    }

    @Override
    public void shuffle() {
        this.wordFunctionState = WordFunctionState.SHUFFLE;
        this.dummyWordList = new ArrayList<>(allFunctionWordList.size());
        for (int i = 0; i < allFunctionWordList.size(); i++) {
            if (allFunctionWordList.get(i).getWordsFlagList().contains(currentChameleon)) {
                dummyWordList.add(allFunctionWordList.get(i));
            }
        }
        List<FunctionWordDTOLocal> temp = allFunctionWordList;
        this.allFunctionWordList = this.dummyWordList;
        this.dummyWordList = temp;
        Collections.shuffle(this.allFunctionWordList);
        Collections.shuffle(this.allFunctionWordList);
        this.start = 0;
        this.end = allFunctionWordList.size() - 1;
        this.preIndex = currentIndex;
        this.currentIndex = 0;
    }

    @Override
    public void shuffleRange(int start, int end) {
        this.wordFunctionState = WordFunctionState.RANGE;
        this.dummyWordList = new ArrayList<>(end - start + 1);
        // 要找到对应颜色的区间
        int realIndex = findColorCursor(start);
        int count = end - start + 1;
        FlagColor currentFlagColor = getChameleon();
        for (int i = realIndex; count > 0; i++) {
            FunctionWordDTOLocal tempWord = allFunctionWordList.get(i);
            if (tempWord.getWordsFlagList().contains(currentFlagColor)) {
                dummyWordList.add(tempWord);
                count--;
            }
        }
        List<FunctionWordDTOLocal> temp = allFunctionWordList;
        this.allFunctionWordList = this.dummyWordList;
        this.dummyWordList = temp;
        Collections.shuffle(this.allFunctionWordList);
        Collections.shuffle(this.allFunctionWordList);
        this.start = 0;
        this.end = allFunctionWordList.size() - 1;
        this.preIndex = currentIndex;
        this.currentIndex = 0;
    }

    @Override
    public void restoreWordList() {
        this.allFunctionWordList = this.dummyWordList;
        this.wordFunctionState = WordFunctionState.NONE;
        this.start = 0;
        this.end = allFunctionWordList.size() - 1;
        this.currentIndex = preIndex;
    }

    @Override
    public WordFunctionState getWordFunctionState() {
        return this.wordFunctionState;
    }


    @Override
    public void setCurrentCreditState(CreditState creditState) {
        this.creditState = creditState;
    }

    @Override
    public void setHidePronoun(boolean hide) {
        this.hideProNoun = hide;
    }

    @Override
    public boolean isHidePronoun() {
        return this.hideProNoun;
    }

    @Override
    public CreditState getCurrentCreditState() {
        return creditState;
    }

    @Override
    public List<FunctionWordDTOLocal> getAllFunctionWordList() {
        return this.allFunctionWordList;
    }

    @Override
    public int getChameleonSize() {
        int result = 0;
        for (int i = start; i <= end; i++) {
            if (allFunctionWordList.get(i).getWordsFlagList().contains(getChameleon())) {
                result++;
            }
        }
        return result;
    }

    @Override
    public int getChameleonOrder() {
        int result = 0;
        for (int i = start; i < currentIndex; i++) {
            if (allFunctionWordList.get(i).getWordsFlagList().contains(getChameleon())) {
                result++;
            }
        }
        return result + 1;
    }

    @Override
    public synchronized void startProjector(ProjectorDTOLocal projectorDTOLocal) {
        if (projectorDTOLocal == null) {
            this.projectorDTOLocal = null;
            return;
        }
        this.projectorDTOLocal = projectorDTOLocal;
        this.projectorDTOLocal.setMilliseconds((long) this.projectorDTOLocal.getMinute() * 60 * 1000);
        this.startTimeMillis = 0;
    }

    @Override
    public ProjectorDTOLocal calculateCountdown() {
        if (this.projectorDTOLocal == null) {
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();

        if (this.projectorDTOLocal.isRunning()) {
            this.startTimeMillis = startTimeMillis == 0 ? currentTimeMillis : startTimeMillis;
            long result = this.projectorDTOLocal.getMilliseconds() - (currentTimeMillis - this.startTimeMillis);
            this.projectorDTOLocal.setRemainingTime(result < 0 ? 0 : result);
        }

        if (!this.projectorDTOLocal.isRunning() && this.startTimeMillis != 0) {
            long result = this.projectorDTOLocal.getMilliseconds() - (currentTimeMillis - this.startTimeMillis);
            this.projectorDTOLocal.setRemainingTime(result < 0 ? 0 : result);
            this.projectorDTOLocal.setMilliseconds(this.projectorDTOLocal.getRemainingTime());
            this.startTimeMillis = 0;
        }
        return this.projectorDTOLocal;
    }

    @Override
    public int getIndexByWordOrigin(String origin) {
        Integer result = reverseQueryIndex.get(origin);
        return result == null ? -1 : result;
    }

    /**
     * 找出用户输入的颜色索引对应的目标单词index
     *
     * @param currentIndex 用户期待的颜色索引
     * @return 返回用户输入的颜色索引所对应的数组元素索引
     */
    private int findColorCursor(int currentIndex) {
        FlagColor currentFlagColor = getChameleon();
        int result = 0;
        for (; result < this.size() && currentIndex > -1; result++) {
            if (allFunctionWordList.get(result).getWordsFlagList().contains(currentFlagColor)) {
                currentIndex--;
            }
        }
        return result - 1;
    }

    private void initReverseQueryMap() {
        reverseQueryIndex = new HashMap<>(allFunctionWordList.size());
        for (int i = 0; i < allFunctionWordList.size(); i++) {
            WordDTOLocal tempWord = queryCache.getOrDefault(allFunctionWordList.get(i).getId(), new WordDTOLocal());
            if (tempWord != null) {
                reverseQueryIndex.put(tempWord.getOrigin(), i);
            }
        }
    }

}
