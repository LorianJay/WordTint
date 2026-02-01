package com.github.lorenj.wordtint.handler.impl;

import android.content.Context;
import android.text.TextUtils;

import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.CategoryFunctionHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author cnsukidayo
 * @date 2023/2/9 16:26
 */
public abstract class AbstractCategoryFunctionHandler implements CategoryFunctionHandler {
    /**
     * 上下文
     */
    private final Context context;
    /**
     * 数据库对象
     */
    private final APPDatabase appDatabase;
    /**
     * 用于存储单词分类的列表
     */
    private Map<Integer, WordStarWithWordIdEntity> allStarList = new HashMap<>();

    public AbstractCategoryFunctionHandler(Context context) {
        this.context = context;
        this.appDatabase = APPDatabase.getInstance(context);
        initHandler();
    }

    @Override
    public FunctionWordVO getCurrentFocusWord() {
        Integer currentSelectWordId = getCurrentFocusWordId();
        return currentSelectWordId == null ? null :
                getDict().get(currentSelectWordId);
    }

    @Override
    public void createNewStar(WordStarEntity wordStarEntity) {
        if (wordStarEntity.order == -1) {
            wordStarEntity.order = allStarList.size() + 1;
        }
        appDatabase.wordStarDao().insert(wordStarEntity);
    }

    @Override
    public void reloadStar() {
        // 加载所有的收藏夹
        this.allStarList = appDatabase.wordStarDao()
                .findAllStarAndWordId()
                .stream()
                .collect(Collectors.toMap(
                        wordStarWithWordIdEntity -> wordStarWithWordIdEntity.wordStarEntity.id,
                        wordStarWithWordIdEntity -> wordStarWithWordIdEntity));
    }

    @Override
    public void updateWordStar(WordStarEntity wordStarEntity) {
        appDatabase.wordStarDao().update(wordStarEntity);
        Optional.ofNullable(allStarList.get(wordStarEntity.id))
                .ifPresent(wordStarWithWordIdEntity -> wordStarWithWordIdEntity.setWordStarEntity(wordStarEntity));
    }

    @Override
    public void batchUpdateCurrentStar() {
        List<WordStarEntity> allWordStarList = this.allStarList.values()
                .stream()
                .map(wordStarWithWordIdEntity -> wordStarWithWordIdEntity.wordStarEntity)
                .sorted(Comparator.comparingInt(o -> o.order))
                .collect(Collectors.toList());
        for (int i = 0; i < allWordStarList.size(); i++) {
            allWordStarList.get(i).order = i;
        }
        appDatabase.wordStarDao().batchUpdate(allWordStarList);
    }

    @Override
    public void removeStar(WordStarEntity wordStarEntity) {
        this.allStarList.remove(wordStarEntity.id);
        batchUpdateCurrentStar();
    }

    @Override
    public int starListSize() {
        return allStarList.size();
    }

    @Override
    public String calculationTitle(WordStarEntity wordStarEntity) {
        return TextUtils.isEmpty(wordStarEntity.title) ?
                calculation(wordStarEntity) :
                wordStarEntity.title;
    }

    @Override
    public String calculationDescribe(WordStarEntity wordStarEntity) {
        return TextUtils.isEmpty(wordStarEntity.describeInfo) ?
                calculation(wordStarEntity) :
                wordStarEntity.describeInfo;
    }

    @Override
    public void moveStar(WordStarEntity fromPosition, WordStarEntity toPosition) {
        // 这个方法不需要持久化,因为频繁地移动会调用该方法
    }

    @Override
    public int getStarWordCount(WordStarEntity wordStarEntity) {
        return Optional.ofNullable(allStarList.get(wordStarEntity.id))
                .orElse(new WordStarWithWordIdEntity())
                .wordStarWordIdEntityList
                .size();
    }

    @Override
    public boolean addWordToStar(WordStarWordIdEntity wordStarWordIdEntity) {
        // 得到当前收藏夹
        WordStarWithWordIdEntity wordStarWithWordIdEntity = allStarList.get(wordStarWordIdEntity.starId);
        if (wordStarWithWordIdEntity == null) return false;
        if (wordStarWithWordIdEntity.getWordStarWordIdEntityList() == null)
            wordStarWithWordIdEntity.setWordStarWordIdEntityList(new ArrayList<>());
        List<WordStarWordIdEntity> wordStarWordIdEntityList = wordStarWithWordIdEntity.getWordStarWordIdEntityList();
        // 当前收藏夹不能已经存在当前单词
        for (WordStarWordIdEntity wordStarWordId : wordStarWordIdEntityList) {
            if (wordStarWordId.wordId == wordStarWordIdEntity.wordId) {
                return false;
            }
        }
        wordStarWordIdEntityList.add(wordStarWordIdEntity);
        wordStarWordIdEntity.order = wordStarWordIdEntityList.size();
        appDatabase.wordStarWordIdDao().insert(wordStarWordIdEntity);
        return true;
    }

    @Override
    public void removeWordFromStar(WordStarWordIdEntity wordStarWordIdEntity) {
        // 得到当前收藏夹
        WordStarWithWordIdEntity wordStarWithWordIdEntity = allStarList.get(wordStarWordIdEntity.starId);
        if (wordStarWithWordIdEntity == null) return;
        wordStarWithWordIdEntity.wordStarWordIdEntityList = wordStarWithWordIdEntity.getWordStarWordIdEntityList()
                .stream()
                .filter(wordStarWordIdEntityTest -> wordStarWordIdEntityTest.id != wordStarWordIdEntity.id)
                .collect(Collectors.toList());
        batchUpdateStarInnerWordList(wordStarWithWordIdEntity.wordStarEntity);
    }

    @Override
    public FunctionWordVO getWordDetailByWordId(WordStarWordIdEntity wordStarWordIdEntity) {
        return getDict().get(wordStarWordIdEntity.wordId);
    }

    @Override
    public void batchUpdateStarInnerWordList(WordStarEntity wordStarEntity) {
        WordStarWithWordIdEntity wordStarWithWordIdEntity = allStarList.get(wordStarEntity.id);
        if (wordStarWithWordIdEntity == null) return;
        List<WordStarWordIdEntity> wordStarWordIdEntityList = wordStarWithWordIdEntity.getWordStarWordIdEntityList();
        for (int i = 0; i < wordStarWordIdEntityList.size(); i++) {
            wordStarWordIdEntityList.get(i).order = i + 1;
        }
        appDatabase.wordStarWordIdDao().batchUpdate(wordStarWordIdEntityList);
    }

    @Override
    public void moveStarInnerWord(WordStarWordIdEntity fromPosition, WordStarWordIdEntity toPosition) {
    }

    private void initHandler() {
        this.reloadStar();
    }

    /**
     * 计算名称
     *
     * @param wordStarEntity 单词实体
     * @return 返回收藏夹名称
     */
    private String calculation(WordStarEntity wordStarEntity) {
        return Optional.ofNullable(allStarList.get(wordStarEntity.id))
                .orElse(new WordStarWithWordIdEntity())
                .wordStarWordIdEntityList
                .stream()
                .limit(3)
                .map(wordStarWordIdEntity -> Optional.ofNullable(getDict().get(wordStarWordIdEntity.wordId))
                        .orElse(new FunctionWordVO())
                        .getValue()
                        .get(WordStructure.WORD_ORIGIN))
                .collect(Collectors.joining("、"));
    }

}
