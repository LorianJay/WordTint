package com.github.lorenj.wordtint.handler.impl;

import android.content.Context;

import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.dao.ReciteRecordDao;
import com.github.lorenj.wordtint.database.dao.ReciteRecordMarkDao;
import com.github.lorenj.wordtint.database.dao.ReciteRecordWordDao;
import com.github.lorenj.wordtint.database.entity.ReciteRecordEntity;
import com.github.lorenj.wordtint.database.entity.ReciteRecordWordEntity;
import com.github.lorenj.wordtint.database.entity.ReciteRecordWordMarkEntity;
import com.github.lorenj.wordtint.database.entity.WordBookSectionWordIdEntity;
import com.github.lorenj.wordtint.database.entity.WordNoteEntity;
import com.github.lorenj.wordtint.database.entity.WordOriginEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.database.vo.UserRecitePreference;
import com.github.lorenj.wordtint.database.vo.WordOriginVO;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.enums.ReciteFilter;
import com.github.lorenj.wordtint.enums.ReciteOrder;
import com.github.lorenj.wordtint.enums.ReciteOrigin;
import com.github.lorenj.wordtint.enums.WordFunctionState;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;
import com.github.lorenj.wordtint.handler.WordFunctionHandler;
import com.github.lorenj.wordtint.handler.state.WordFunctionHandlerState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 明确一点,currentIndex是不能随意更改的,每逢currentIndex更改势必是由currentOrder的更改而更改的.<br>
 * 每个单词都是有棕色的,棕色是不可变的颜色,也就是说用户不可以取消单词的棕色标记.<br>
 * 变色龙的每一种状态都是可以进入的,不管当前单词列表中是否有该颜色对应的单词<br>
 */
public class WordFunctionHandlerImpl extends AbstractStarFunctionHandler
        implements WordFunctionHandler, StarFunctionHandler {
    /**
     * 上下文
     */
    private final Context context;
    /**
     * 用户背诵偏好信息
     */
    private final UserRecitePreference userRecitePreference;
    /**
     * 功能区的状态标记
     */
    private final WordFunctionHandlerState wordFunctionHandlerState;
    /**
     * 数据库对象
     */
    private APPDatabase appDatabase;
    /**
     * 所有单词的id列表
     */
    private List<Integer> allWordIdList = new ArrayList<>(100);
    /**
     * 仅用于保存背诵记录的引用<br>
     * 只在初始化时赋值,后续不再更改,用于本次背诵的所有单词的记录保存
     */
    private final List<Integer> saveList;
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

    public WordFunctionHandlerImpl(Context context,
                                   UserRecitePreference userRecitePreference) {
        super(context);
        this.context = context;
        this.userRecitePreference = userRecitePreference;
        this.wordFunctionHandlerState = new WordFunctionHandlerState();
        initHandler();
        this.saveList = allWordIdList;
        this.start = 0;
        this.end = allWordIdList.size() - 1;
    }

    @Override
    public Integer getCurrentFocusWordId() {
        return this.allWordIdList.get(currentIndex);
    }

    @Override
    public FunctionWordVO getWordByIndex(int index) {
        return super.getDict().get(allWordIdList.get(currentIndex));
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
    public void shuffle() {
        getWordFunctionHandlerState().setWordFunctionState(WordFunctionState.SHUFFLE);
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
        getWordFunctionHandlerState().setWordFunctionState(WordFunctionState.RANGE);
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
        getWordFunctionHandlerState().setWordFunctionState(WordFunctionState.NONE);
        this.start = 0;
        this.end = allWordIdList.size() - 1;
        this.currentIndex = dummyIndex;
    }

    @Override
    public WordFunctionHandlerState getWordFunctionHandlerState() {
        return this.wordFunctionHandlerState;
    }

    @Override
    public void saveProgress() {
        ReciteRecordDao reciteRecordDao = appDatabase.reciteRecordDao();
        ReciteRecordWordDao reciteRecordWordDao = appDatabase.reciteRecordWordDao();
        ReciteRecordMarkDao reciteRecordMarkDao = appDatabase.reciteRecordMarkDao();
        appDatabase.runInTransaction(() -> {
            ReciteRecordEntity reciteRecordEntity = new ReciteRecordEntity();
            reciteRecordEntity.createTime = System.currentTimeMillis();
            reciteRecordEntity.wordCount = saveList.size();
            reciteRecordEntity.reciteMode = this.getWordFunctionHandlerState().getCurrentReciteMode().name();
            reciteRecordEntity.reciteOrder = userRecitePreference.getReciteOrder().name();
            reciteRecordEntity.reciteFiler = userRecitePreference.getReciteFilter().name();
            reciteRecordEntity.recitePreposition = this.getWordFunctionHandlerState().getRecitePreposition().name();
            long reciteRecordId = reciteRecordDao.insertReciteRecord(reciteRecordEntity);
            List<ReciteRecordWordEntity> recordWordEntityList = new ArrayList<>();
            for (int i = 0; i < saveList.size(); i++) {
                ReciteRecordWordEntity recordWordEntity = new ReciteRecordWordEntity();
                recordWordEntity.recordId = (int) reciteRecordId;
                recordWordEntity.wordId = saveList.get(i);
                recordWordEntity.order = i + 1;
                recordWordEntityList.add(recordWordEntity);
            }
            List<Long> recordWordIdList = reciteRecordWordDao.batchInsertReciteRecordWord(recordWordEntityList);
            List<ReciteRecordWordMarkEntity> wordMarkEntityList = new ArrayList<>();
            for (int i = 0; i < saveList.size(); i++) {
                Long recordWordId = recordWordIdList.get(i);
                Integer wordId = saveList.get(i);
                FunctionWordVO functionWordVO = getDict().get(wordId);
                if (functionWordVO == null) continue;
                for (MarkColor markColor : functionWordVO.getMarkColorList()) {
                    if (markColor == MarkColor.BROWN) continue;
                    ReciteRecordWordMarkEntity wordMarkEntity = new ReciteRecordWordMarkEntity();
                    wordMarkEntity.recordWordId = Math.toIntExact(recordWordId);
                    wordMarkEntity.markColor = markColor.name();
                    wordMarkEntityList.add(wordMarkEntity);
                }
            }
            reciteRecordMarkDao.batchInsertReciteRecordWordMark(wordMarkEntityList);
        });
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
        appDatabase = APPDatabase.getInstance(context);
        // 1.加载单词
        if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_LIST) {
            // 来自背诵列表-根据当前选择的章节加载获取所有的单词
            List<Integer> allSectionIdList = userRecitePreference.getAllSectionIdList();
            for (Integer sectionId : allSectionIdList) {
                List<WordBookSectionWordIdEntity> sectionWordIdEntityList = appDatabase.wordBookSectionDao()
                        .findAllBySectionId(sectionId);
                List<Integer> orderWordIdList = sectionWordIdEntityList.stream()
                        .sorted((o1, o2) -> o1.order - o2.order)
                        .map(wordBookSectionWordIdEntity -> wordBookSectionWordIdEntity.wordId)
                        .collect(Collectors.toList());
                allWordIdList.addAll(orderWordIdList);
            }
        }
        List<ReciteRecordWordEntity> recordWordEntityList = new ArrayList<>();
        if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_RECORD) {
            Integer recordId = userRecitePreference.getAllSectionIdList()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("not found"));
            recordWordEntityList = appDatabase.reciteRecordWordDao()
                    .findByRecordId(recordId);
            List<Integer> orderWordIdList = recordWordEntityList
                    .stream()
                    .sorted(Comparator.comparingInt(value -> value.order))
                    .map(reciteRecordWordEntity -> reciteRecordWordEntity.wordId)
                    .collect(Collectors.toList());
            allWordIdList.addAll(orderWordIdList);
        }
        if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_REVIEW) {

        }
        // 2. 根据单词的id组装出所有的单词-字典
        List<WordOriginEntity> allOriginWordList = appDatabase.wordOriginDao()
                .findAllOriginWordByIdList(allWordIdList);
        for (WordOriginEntity wordOriginEntity : allOriginWordList) {
            FunctionWordVO functionWordVO = super.getDict().get(wordOriginEntity.wordId);
            if (functionWordVO == null) {
                functionWordVO = new FunctionWordVO();
                super.getDict().put(wordOriginEntity.wordId, functionWordVO);
            }
            functionWordVO.setWordId(wordOriginEntity.wordId);
            WordOriginVO wordOriginVO = new WordOriginVO();
            wordOriginVO.setValue(wordOriginEntity.value);
            wordOriginVO.setCustomValue(wordOriginEntity.customValue);
            functionWordVO.getValue().put(WordStructure.valueOf(wordOriginEntity.key), wordOriginVO);
        }
        // 3.背诵过滤
        if (userRecitePreference.getReciteFilter() == ReciteFilter.PHRASE) {
            allWordIdList = allWordIdList.stream()
                    .filter(super.getDict()::containsKey)
                    .collect(Collectors.toList());
        }
        // 4.背诵的顺序
        if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_LIST
                && userRecitePreference.getReciteOrder() == ReciteOrder.DISORDER) {
            Collections.shuffle(allWordIdList);
        } else if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_LIST
                && userRecitePreference.getReciteOrder() == ReciteOrder.LEXICOGRAPHIC) {

            allWordIdList = allWordIdList.stream()
                    .sorted((o1, o2) -> super.getDict().get(o1)
                            .getValue()
                            .getOrDefault(WordStructure.WORD_ORIGIN, new WordOriginVO())
                            .getValue()
                            .compareTo(String.valueOf(super.getDict().get(o2).getValue().getOrDefault(WordStructure.WORD_ORIGIN, new WordOriginVO()))))
                    .collect(Collectors.toList());
        }
        // 5.如果是历史记录,则根据历史记录设置MarkColor
        if (userRecitePreference.getReciteOrigin() == ReciteOrigin.RECITE_RECORD) {
            List<Integer> recordWordIdList = recordWordEntityList.stream()
                    .map(reciteRecordWordEntity -> reciteRecordWordEntity.id)
                    .collect(Collectors.toList());
            Map<Integer, List<MarkColor>> markMap = appDatabase.reciteRecordMarkDao()
                    .findAllByRecordWordId(recordWordIdList)
                    .stream()
                    .collect(Collectors.groupingBy(
                            entity -> entity.recordWordId,
                            Collectors.mapping(entity -> MarkColor.valueOfName(entity.markColor), Collectors.toList())
                    ));
            recordWordEntityList.forEach(entity -> {
                FunctionWordVO functionWordVO = getDict().get(entity.wordId);
                List<MarkColor> markColor = markMap.get(entity.id);
                if (functionWordVO == null) return;
                functionWordVO.getMarkColorList().remove(MarkColor.GREEN);
                if (markColor != null) functionWordVO.getMarkColorList().addAll(markColor);
            });
        }
        // 6.设置当前的背诵模式
        wordFunctionHandlerState.setCurrentReciteMode(userRecitePreference.getReciteMode());
        wordFunctionHandlerState.setRecitePreposition(userRecitePreference.getRecitePreposition());
        wordFunctionHandlerState.setWordFunctionState(WordFunctionState.NONE);
        // 7.单词去重
        allWordIdList = allWordIdList.stream()
                .distinct()
                .collect(Collectors.toList());
        // 8.快速定位(单词反查的初始化)
        quickPosition = new HashMap<>(allWordIdList.size());
        for (int i = 0; i < allWordIdList.size(); i++) {
            FunctionWordVO functionWordVO = super.getDict().get(allWordIdList.get(i));
            if (functionWordVO != null) {
                quickPosition.put(functionWordVO.getValue().get(WordStructure.WORD_ORIGIN).getValue(), i);
            }
        }
        // 9.单词的注释初始化
        List<WordNoteEntity> allWordNoteList = appDatabase.wordNoteDao()
                .findAllWordNoteByIdList(allWordIdList);
        for (WordNoteEntity wordNoteEntity : allWordNoteList) {
            FunctionWordVO functionWordVO = super.getDict().get(wordNoteEntity.wordId);
            if (functionWordVO == null) continue;
            functionWordVO.setWordNoteEntity(wordNoteEntity);
        }
    }

    /**
     * 找出用户输入的颜色索引对应的目标单词index<br>
     * 例如现在的真正的单词区间是[0-99];想象这些单词默认都是棕色标签<br>
     * 现在要找到颜色为红色的,第3个单词,那就不能是简单的[3],而应该遍历整个列表
     *
     * @param currentIndex 用户期待的颜色索引<br>
     *                     用户期待的颜色就是{@link WordFunctionHandlerState#getChameleon()}方法的返回值
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
        return Optional.ofNullable(super.getDict().get(allWordIdList.get(index)))
                .map(FunctionWordVO::getMarkColorList)
                .map(set -> set.contains(getWordFunctionHandlerState().getChameleon().getValue()))
                .orElse(false);
    }

}
