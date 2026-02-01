package com.github.lorenj.wordtint.handler.impl;

import android.content.Context;

import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.WordBookSectionWordIdEntity;
import com.github.lorenj.wordtint.database.entity.WordOriginEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.database.vo.UserRecitePreference;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.enums.ReciteFilter;
import com.github.lorenj.wordtint.enums.ReciteMode;
import com.github.lorenj.wordtint.enums.ReciteOrder;
import com.github.lorenj.wordtint.enums.ReciteOrigin;
import com.github.lorenj.wordtint.enums.WordFunctionState;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.CategoryFunctionHandler;
import com.github.lorenj.wordtint.handler.WordFunctionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 明确一点,currentIndex是不能随意更改的,每逢currentIndex更改势必是由currentOrder的更改而更改的.<br>
 * 每个单词都是有棕色的,棕色是不可变的颜色,也就是说用户不可以取消单词的棕色标记.<br>
 * 变色龙的每一种状态都是可以进入的,不管当前单词列表中是否有该颜色对应的单词<br>
 */
public class WordFunctionHandlerImpl extends AbstractCategoryFunctionHandler
        implements WordFunctionHandler, CategoryFunctionHandler {
    /**
     * 上下文
     */
    private final Context context;
    /**
     * 用户背诵偏好信息
     */
    private final UserRecitePreference userRecitePreference;
    /**
     * 数据库对象
     */
    private APPDatabase appDatabase;
    /**
     * 所有单词的id列表
     */
    private List<Integer> allWordIdList = new ArrayList<>(100);
    /**
     * 字典信息
     */
    private final Map<Integer, FunctionWordVO> dict = new HashMap<>();
    /**
     * 反查单词的Index(快速定位功能)
     */
    private Map<String, Integer> quickPosition;
    /**
     * 这是一个临时的集合,它指向allWordIdList,用于保存由按色打乱、区间重背功能被重置的allWordIdList引用
     */
    private List<Integer> dummyWordIdList;
    /**
     * 先前的背诵指针,用于恢复背诵进度<br>
     * 主要用于按色打乱、区间重背的功能的快速恢复
     */
    private int dummyIndex = 0;
    /**
     * 当前单词背诵的指针,从0开始计数
     */
    private int currentIndex = 0;
    /**
     * 现在正在背诵的区间 start:19 end:29 -> [20,30]
     */
    private int start = 0, end = 0;
    /**
     * 当前变色龙的颜色
     */
    private MarkColor currentChameleon = MarkColor.GREEN;

    /**
     * 默认的单词功能为空
     */
    private WordFunctionState wordFunctionState = WordFunctionState.NONE;

    public WordFunctionHandlerImpl(Context context,
                                   UserRecitePreference userRecitePreference) {
        super(context);
        this.context = context;
        this.userRecitePreference = userRecitePreference;
        initHandler();
        this.start = 0;
        this.end = allWordIdList.size() - 1;
    }

    @Override
    public Map<Integer, FunctionWordVO> getDict() {
        return dict;
    }

    @Override
    public Integer getCurrentFocusWordId() {
        return this.allWordIdList.get(currentIndex);
    }

    @Override
    public FunctionWordVO getWordByIndex(int index) {
        return dict.get(allWordIdList.get(currentIndex));
    }

    @Override
    public FunctionWordVO gotoPreviousWord() {
        for (int tempIndex = currentIndex - 1; tempIndex != currentIndex; tempIndex--) {
            boolean flag = false;
            if (tempIndex < start) {
                tempIndex = end;
                flag = true;
            }
            if (testWordIsCurrentChameleonWithIndex(tempIndex)) {
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
    public FunctionWordVO gotoNextWord() {
        for (int tempIndex = currentIndex + 1; tempIndex != currentIndex; tempIndex++) {
            boolean flag = false;
            if (tempIndex > end) {
                tempIndex = start;
                flag = true;
            }
            if (testWordIsCurrentChameleonWithIndex(tempIndex)) {
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
    public FunctionWordVO gotoWordWithIndex(int currentIndex) {
        return getWordByIndex(this.currentIndex = findColorCursor(currentIndex));
    }

    @Override
    public FunctionWordVO forceGotoWordWithOutMarkColor(int index) {
        return getWordByIndex(this.currentIndex = index);
    }

    @Override
    public int functionWordSize() {
        return allWordIdList.size();
    }

    @Override
    public int getInnerIndex() {
        return this.currentIndex;
    }

    @Override
    public Set<MarkColor> getCurrentWordMarkColor() {
        return Collections.unmodifiableSet(getCurrentFocusWord().getMarkColorList());
    }

    @Override
    public boolean addMarkColorToCurrentWord(MarkColor markColor) {
        return getCurrentFocusWord().getMarkColorList().add(markColor);
    }

    @Override
    public boolean removeMarkColorToCurrentWord(MarkColor markColor) {
        return getCurrentFocusWord().getMarkColorList().remove(markColor);
    }

    @Override
    public MarkColor getChameleon() {
        return this.currentChameleon;
    }

    @Override
    public void setChameleon(MarkColor chameleonColor) {
        this.currentChameleon = chameleonColor;
    }

    @Override
    public void shuffle() {
        this.wordFunctionState = WordFunctionState.SHUFFLE;
        this.dummyWordIdList = new ArrayList<>(allWordIdList.size());
        for (int i = 0; i < allWordIdList.size(); i++) {
            if (testWordIsCurrentChameleonWithIndex(i)) {
                dummyWordIdList.add(allWordIdList.get(i));
            }
        }
        List<Integer> temp = allWordIdList;
        this.allWordIdList = this.dummyWordIdList;
        this.dummyWordIdList = temp;
        Collections.shuffle(this.allWordIdList);
        Collections.shuffle(this.allWordIdList);
        this.start = 0;
        this.end = allWordIdList.size() - 1;
        this.dummyIndex = currentIndex;
        this.currentIndex = 0;
    }

    @Override
    public void shuffleRange(int start, int end) {
        this.wordFunctionState = WordFunctionState.RANGE;
        this.dummyWordIdList = new ArrayList<>(end - start + 1);
        // 要找到对应颜色的区间
        int realIndex = findColorCursor(start);
        int count = end - start + 1;
        for (int i = realIndex; count > 0; i++) {
            if (testWordIsCurrentChameleonWithIndex(i)) {
                dummyWordIdList.add(allWordIdList.get(i));
                count--;
            }
        }
        List<Integer> temp = allWordIdList;
        this.allWordIdList = this.dummyWordIdList;
        this.dummyWordIdList = temp;
        Collections.shuffle(this.allWordIdList);
        Collections.shuffle(this.allWordIdList);
        this.start = 0;
        this.end = allWordIdList.size() - 1;
        this.dummyIndex = currentIndex;
        this.currentIndex = 0;
    }

    @Override
    public void restoreWordList() {
        this.allWordIdList = this.dummyWordIdList;
        this.wordFunctionState = WordFunctionState.NONE;
        this.start = 0;
        this.end = allWordIdList.size() - 1;
        this.currentIndex = dummyIndex;
    }

    @Override
    public WordFunctionState getWordFunctionState() {
        return this.wordFunctionState;
    }

    @Override
    public void setCurrentReciteMode(ReciteMode reciteMode) {
        this.userRecitePreference.setReciteMode(reciteMode);
    }

    @Override
    public ReciteMode getCurrentReciteMode() {
        return this.userRecitePreference.getReciteMode();
    }

    @Override
    public void setHidePreposition(boolean hide) {
        this.userRecitePreference.setHidePreposition(hide);
    }

    @Override
    public boolean isHidePreposition() {
        return this.userRecitePreference.isHidePreposition();
    }

    @Override
    public void saveProgress() {
        // todo 保存背诵进度
    }

    @Override
    public int getChameleonSize() {
        int result = 0;
        for (int i = start; i <= end; i++) {
            if (testWordIsCurrentChameleonWithIndex(i)) {
                result++;
            }
        }
        return result;
    }

    @Override
    public int getChameleonOrder() {
        int result = 0;
        for (int i = start; i < currentIndex; i++) {
            if (testWordIsCurrentChameleonWithIndex(i)) {
                result++;
            }
        }
        return result + 1;
    }

    @Override
    public int getIndexByWordOrigin(String origin) {
        Integer result = quickPosition.get(origin.toLowerCase());
        return result == null ? -1 : result;
    }

    /**
     * 初始化处理器
     */
    private void initHandler() {
        // 1.加载单词
        if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_LIST) {
            // 来自背诵列表-根据当前选择的章节加载获取所有的单词
            List<Integer> allSectionIdList = userRecitePreference.getAllSectionIdList();
            appDatabase = APPDatabase.getInstance(context);
            for (Integer sectionId : allSectionIdList) {
                List<WordBookSectionWordIdEntity> sectionWordIdEntityList = appDatabase.wordBookSectionDao()
                        .findAllBySectionId(sectionId);
                List<Integer> orderWordIdList = sectionWordIdEntityList.stream()
                        .sorted((o1, o2) -> o1.order - o2.order)
                        .map(wordBookSectionWordIdEntity -> wordBookSectionWordIdEntity.wordId)
                        .collect(Collectors.toList());
                allWordIdList.addAll(orderWordIdList);
            }
        } else if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_REVIEW) {

        } else if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_RECORD) {

        }
        // 2. 根据单词的id组装出所有的单词-字典
        List<WordOriginEntity> allOriginWordList = appDatabase.wordOriginDao().findAllOriginWordByIdList(allWordIdList);
        for (WordOriginEntity wordOriginEntity : allOriginWordList) {
            FunctionWordVO functionWordVO = dict.get(wordOriginEntity.wordId);
            if (functionWordVO == null) {
                functionWordVO = new FunctionWordVO();
                dict.put(wordOriginEntity.wordId, functionWordVO);
            }
            functionWordVO.setWordId(wordOriginEntity.wordId);
            functionWordVO.getValue().put(WordStructure.valueOf(wordOriginEntity.key), wordOriginEntity.value);
        }
        // 3.背诵过滤
        if (userRecitePreference.getReciteFilter() == ReciteFilter.PHRASE) {
            allWordIdList = allWordIdList.stream()
                    .filter(dict::containsKey)
                    .collect(Collectors.toList());
        }
        // 4.背诵的顺序
        if (userRecitePreference.getReciteOrder() == ReciteOrder.DISORDER) {
            Collections.shuffle(allWordIdList);
        } else if (userRecitePreference.getReciteOrder() == ReciteOrder.LEXICOGRAPHIC) {
            allWordIdList = allWordIdList.stream()
                    .sorted((o1, o2) -> dict.get(o1)
                            .getValue()
                            .getOrDefault(WordStructure.WORD_ORIGIN, "")
                            .compareTo(dict.get(o2).getValue().getOrDefault(WordStructure.WORD_ORIGIN, "")))
                    .collect(Collectors.toList());
        }
        // 5.如果是历史记录,则根据历史记录设置MarkColor
        if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_RECORD) {
        }
        // 6.设置当前的背诵模式
        // 5.快速定位(单词反查的初始化)
        quickPosition = new HashMap<>(allWordIdList.size());
        for (int i = 0; i < allWordIdList.size(); i++) {
            FunctionWordVO functionWordVO = dict.get(allWordIdList.get(i));
            if (functionWordVO != null) {
                quickPosition.put(functionWordVO.getValue().get(WordStructure.WORD_ORIGIN), i);
            }
        }
    }

    /**
     * 找出用户输入的颜色索引对应的目标单词index<br>
     * 例如现在的真正的单词区间是[0-99];想象这些单词默认都是棕色标签<br>
     * 现在要找到颜色为红色的,第3个单词,那就不能是简单的[3],而应该遍历整个列表
     *
     * @param currentIndex 用户期待的颜色索引<br>
     *                     用户期待的颜色就是{@link WordFunctionHandler#getChameleon()}方法的返回值
     * @return 返回用户输入的颜色索引所对应的数组元素索引
     */
    private int findColorCursor(int currentIndex) {
        int result = 0;
        for (; result < this.functionWordSize() && currentIndex > -1; result++) {
            if (testWordIsCurrentChameleonWithIndex(result)) {
                currentIndex--;
            }
        }
        return result - 1;
    }

    /**
     * 判断当前索引对应的单词是否和当前选择的变色龙颜色一致
     *
     * @param index 单词索引,索引全局唯一
     * @return boolean
     */
    private boolean testWordIsCurrentChameleonWithIndex(int index) {
        return Optional.ofNullable(dict.get(allWordIdList.get(index)))
                .map(FunctionWordVO::getMarkColorList)
                .map(set -> set.contains(currentChameleon))
                .orElse(false);
    }

}
