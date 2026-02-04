package com.github.lorenj.wordtint.handler.impl;

import android.content.Context;
import android.text.TextUtils;

import com.github.lorenj.wordtint.database.APPDatabase;
import com.github.lorenj.wordtint.database.entity.WordOriginEntity;
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
import java.util.Objects;
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
     * 存储所有收藏夹的id列表
     */
    private List<WordStarWithWordIdEntity> allStarList = new ArrayList<>();
    /**
     * 字典信息
     */
    private final Map<Integer, FunctionWordVO> dict = new HashMap<>();

    public AbstractStarFunctionHandler(Context context) {
        this.context = context;
        this.appDatabase = APPDatabase.getInstance(context);
        reloadStar();
    }

    @Override
    public Map<Integer, FunctionWordVO> getDict() {
        return dict;
    }

    @Override
    public FunctionWordVO getCurrentFocusWord() {
        Integer currentSelectWordId = getCurrentFocusWordId();
        FunctionWordVO functionWordVO = getDict().get(currentSelectWordId);
        if (functionWordVO != null) return functionWordVO;

        // 如果从字典里面查不出来,则必须从数据库查,此时不能在UI线程中执行!
        List<WordOriginEntity> allOriginWordList = appDatabase.wordOriginDao().findAllOriginWordById(currentSelectWordId);
        functionWordVO = new FunctionWordVO();
        functionWordVO.setWordId(currentSelectWordId);
        for (WordOriginEntity wordOriginEntity : allOriginWordList) {
            functionWordVO.getValue().put(WordStructure.valueOf(wordOriginEntity.key), wordOriginEntity.value);
        }
        getDict().put(currentSelectWordId, functionWordVO);
        return functionWordVO;
    }

    @Override
    public List<WordStarWithWordIdEntity> getAllStarList() {
        return allStarList;
    }

    @Override
    public void createNewStar(WordStarEntity wordStarEntity) {
        if (wordStarEntity.order == 0) wordStarEntity.order = allStarList.size() + 1;
        WordStarWithWordIdEntity wordStarWithWordIdEntity = new WordStarWithWordIdEntity();
        wordStarWithWordIdEntity.wordStarEntity = wordStarEntity;
        wordStarWithWordIdEntity.wordStarWordIdEntityList = new ArrayList<>();
        long starId = appDatabase.wordStarDao().insert(wordStarEntity);
        wordStarEntity.id = (int) starId;
        allStarList.add(wordStarWithWordIdEntity);
    }

    @Override
    public void reloadStar() {
        this.allStarList = appDatabase.wordStarDao()
                .findAllStarAndWordId();
        // 组装出收藏夹的字典
        List<Integer> allWordIdList = allStarList.stream()
                .map(wordStarWithWordIdEntity -> wordStarWithWordIdEntity.wordStarWordIdEntityList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(wordStarWordIdEntity -> wordStarWordIdEntity.wordId)
                .distinct()
                .collect(Collectors.toList());
        List<WordOriginEntity> allOriginWordList = appDatabase.wordOriginDao().findAllOriginWordByIdList(allWordIdList);
        for (WordOriginEntity wordOriginEntity : allOriginWordList) {
            FunctionWordVO functionWordVO = getDict().get(wordOriginEntity.wordId);
            if (functionWordVO == null) {
                functionWordVO = new FunctionWordVO();
                getDict().put(wordOriginEntity.wordId, functionWordVO);
            }
            functionWordVO.setWordId(wordOriginEntity.wordId);
            functionWordVO.getValue().put(WordStructure.valueOf(wordOriginEntity.key), wordOriginEntity.value);
        }
    }

    @Override
    public void updateStar(WordStarWithWordIdEntity wordStarWithWordIdEntity) {
        appDatabase.wordStarDao().update(wordStarWithWordIdEntity.wordStarEntity);
    }

    @Override
    public void batchUpdateCurrentStar() {
        List<WordStarEntity> allWordStarList = this.allStarList
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

    // -----下面是收藏夹内收藏的单词列表-----
    @Override
    public WordStarWithWordIdEntity getStarById(int starId) {
        return allStarList.stream()
                .filter(wordStarWithWordIdEntity -> wordStarWithWordIdEntity.wordStarEntity.id == starId)
                .findFirst()
                .orElse(new WordStarWithWordIdEntity());
    }

    @Override
    public boolean addWordToStar(WordStarWordIdEntity wordStarWordIdEntity) {
        // 得到当前收藏夹
        WordStarWithWordIdEntity wordStarWithWordIdEntity = getStarById(wordStarWordIdEntity.starId);
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
        long insertId = appDatabase.wordStarWordIdDao().insert(wordStarWordIdEntity);
        wordStarWordIdEntity.id = (int) insertId;
        return true;
    }

    @Override
    public void removeWordFromStar(WordStarWordIdEntity wordStarWordIdEntity) {
        // 得到当前收藏夹
        WordStarWithWordIdEntity wordStarWithWordIdEntity = getStarById(wordStarWordIdEntity.starId);
        if (wordStarWithWordIdEntity == null) return;
        wordStarWithWordIdEntity.wordStarWordIdEntityList = wordStarWithWordIdEntity.getWordStarWordIdEntityList()
                .stream()
                .filter(wordStarWordIdEntityTest -> wordStarWordIdEntityTest.id != wordStarWordIdEntity.id)
                .collect(Collectors.toList());
        batchUpdateStarInnerWordList(wordStarWithWordIdEntity.wordStarEntity);
        appDatabase.wordStarWordIdDao().delete(wordStarWordIdEntity);
    }

    @Override
    public FunctionWordVO getWordDetailByWordId(WordStarWordIdEntity wordStarWordIdEntity) {
        return getDict().get(wordStarWordIdEntity.wordId);
    }

    @Override
    public void batchUpdateStarInnerWordList(WordStarEntity wordStarEntity) {
        WordStarWithWordIdEntity wordStarWithWordIdEntity = getStarById(wordStarEntity.id);
        if (wordStarWithWordIdEntity == null) return;
        List<WordStarWordIdEntity> wordStarWordIdEntityList = wordStarWithWordIdEntity.getWordStarWordIdEntityList();
        for (int i = 0; i < wordStarWordIdEntityList.size(); i++) {
            wordStarWordIdEntityList.get(i).order = i + 1;
        }
        appDatabase.wordStarWordIdDao().batchUpdate(wordStarWordIdEntityList);
    }

    @Override
    public void moveStarInnerWord(WordStarWordIdEntity fromWord, WordStarWordIdEntity toWord) {
        int starId = fromWord.starId;
        WordStarWithWordIdEntity wordStarWithWordIdEntity = getStarById(starId);
        int fromIndex = wordStarWithWordIdEntity.wordStarWordIdEntityList.indexOf(fromWord);
        int toIndex = wordStarWithWordIdEntity.wordStarWordIdEntityList.indexOf(toWord);
        Collections.swap(wordStarWithWordIdEntity.wordStarWordIdEntityList, fromIndex, toIndex);
        int fromOrder = fromWord.order;
        fromWord.order = toWord.order;
        toWord.order = fromOrder;
    }

    /**
     * 计算名称
     *
     * @param wordStarEntity 单词实体
     * @return 返回收藏夹名称
     */
    private String calculation(WordStarEntity wordStarEntity) {
        return Optional.ofNullable(getStarById(wordStarEntity.id))
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
