package com.github.lorenj.wordtint.handler.impl;

import android.content.Context;
import android.text.TextUtils;

import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.WordStarEntity;
import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;
import com.github.lorenj.wordtint.database.entity.relation.WordStarWithWordIdEntity;
import com.github.lorenj.wordtint.database.vo.FunctionWordVO;
import com.github.lorenj.wordtint.enums.WordStructure;
import com.github.lorenj.wordtint.handler.StarFunctionHandler;

import java.util.ArrayList;
import java.util.Collections;
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
public abstract class AbstractStarFunctionHandler implements StarFunctionHandler {
    /**
     * 上下文
     */
    private final Context context;
    /**
     * 数据库对象
     */
    private final APPDatabase appDatabase;
    /**
     * 用于存储单词分类的列表<br>
     * key:star_id<br>
     * value:star和其下的所有单词列表
     */
    private Map<Integer, WordStarWithWordIdEntity> allStarMap = new HashMap<>();
    /**
     * 存储所有收藏夹的id列表
     */
    private List<WordStarWithWordIdEntity> allStarList = new ArrayList<>();

    public AbstractStarFunctionHandler(Context context) {
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
    public List<WordStarWithWordIdEntity> getAllStarList() {
        return allStarList;
    }

    @Override
    public void createNewStar(WordStarEntity wordStarEntity) {
        if (wordStarEntity.order == 0) wordStarEntity.order = allStarMap.size() + 1;
        WordStarWithWordIdEntity wordStarWithWordIdEntity = new WordStarWithWordIdEntity();
        wordStarWithWordIdEntity.wordStarEntity = wordStarEntity;
        wordStarWithWordIdEntity.wordStarWordIdEntityList = new ArrayList<>();
        long starId = appDatabase.wordStarDao().insert(wordStarEntity);
        wordStarEntity.id = (int) starId;
        allStarMap.put(wordStarEntity.id, wordStarWithWordIdEntity);
        allStarList.add(wordStarWithWordIdEntity);
    }

    @Override
    public void reloadStar() {
        // 加载所有的收藏夹
        this.allStarList = appDatabase.wordStarDao()
                .findAllStarAndWordId();
        this.allStarMap = this.allStarList
                .stream()
                .collect(Collectors.toMap(
                        wordStarWithWordIdEntity -> wordStarWithWordIdEntity.wordStarEntity.id,
                        wordStarWithWordIdEntity -> wordStarWithWordIdEntity));
    }

    @Override
    public void updateStar(WordStarWithWordIdEntity wordStarWithWordIdEntity) {
        appDatabase.wordStarDao().update(wordStarWithWordIdEntity.wordStarEntity);
    }

    @Override
    public void batchUpdateCurrentStar() {
        List<WordStarEntity> allWordStarList = this.allStarMap.values()
                .stream()
                .map(wordStarWithWordIdEntity -> wordStarWithWordIdEntity.wordStarEntity)
                .sorted(Comparator.comparingInt(o -> o.order))
                .collect(Collectors.toList());
        for (int i = 0; i < allWordStarList.size(); i++) {
            allWordStarList.get(i).order = i + 1;
        }
        appDatabase.wordStarDao().batchUpdate(allWordStarList);
    }

    @Override
    public void removeStar(WordStarWithWordIdEntity wordStarWithWordIdEntity) {
        this.allStarMap.remove(wordStarWithWordIdEntity.wordStarEntity.id);
        this.allStarList = this.allStarList.stream()
                .filter(filter -> filter.wordStarEntity.id != wordStarWithWordIdEntity.wordStarEntity.id)
                .collect(Collectors.toList());
        appDatabase.wordStarDao().delete(wordStarWithWordIdEntity.wordStarEntity);
        appDatabase.wordStarWordIdDao().deleteWordIdByStarId(wordStarWithWordIdEntity.wordStarEntity.id);
        batchUpdateCurrentStar();
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
    public void moveStar(WordStarWithWordIdEntity fromStar, WordStarWithWordIdEntity toStar) {
        int fromIndex = allStarList.indexOf(fromStar);
        int toIndex = allStarList.indexOf(toStar);
        Collections.swap(allStarList, fromIndex, toIndex);
        int fromOrder = fromStar.wordStarEntity.order;
        fromStar.wordStarEntity.order = toStar.wordStarEntity.order;
        toStar.wordStarEntity.order = fromOrder;
    }

    @Override
    public int getStarWordCount(WordStarEntity wordStarEntity) {
        return Optional.ofNullable(allStarMap.get(wordStarEntity.id))
                .orElse(new WordStarWithWordIdEntity())
                .wordStarWordIdEntityList
                .size();
    }

    @Override
    public boolean addWordToStar(WordStarWordIdEntity wordStarWordIdEntity) {
        // 得到当前收藏夹
        WordStarWithWordIdEntity wordStarWithWordIdEntity = allStarMap.get(wordStarWordIdEntity.starId);
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
        WordStarWithWordIdEntity wordStarWithWordIdEntity = allStarMap.get(wordStarWordIdEntity.starId);
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
        WordStarWithWordIdEntity wordStarWithWordIdEntity = allStarMap.get(wordStarEntity.id);
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
        return Optional.ofNullable(allStarMap.get(wordStarEntity.id))
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
