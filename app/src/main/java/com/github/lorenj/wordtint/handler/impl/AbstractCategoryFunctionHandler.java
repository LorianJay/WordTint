package com.github.lorenj.wordtint.handler.impl;

import android.text.TextUtils;

import com.github.lorenj.wordtint.context.pathsystem.document.WordContextPath;
import com.github.lorenj.wordtint.context.support.factory.StaticFactory;
import com.github.lorenj.wordtint.entity.dto.WordCategoryDTO;
import com.github.lorenj.wordtint.entity.dto.WordCategoryDetailVO;
import com.github.lorenj.wordtint.entity.dto.WordCategoryWordDTO;
import com.github.lorenj.wordtint.entity.local.WordDTOLocal;
import com.github.lorenj.wordtint.enums.structure.EnglishStructure;
import com.github.lorenj.wordtint.handler.CategoryFunctionHandler;
import com.github.lorenj.wordtint.utils.FileUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * @author cnsukidayo
 * @date 2023/2/9 16:26
 */
public abstract class AbstractCategoryFunctionHandler implements CategoryFunctionHandler {

    /**
     * 用于存储单词分类的列表
     */
    private final List<WordCategoryDetailVO> wordCategoryDetailVOList = new ArrayList<>(15);
    /**
     * 当前用户收藏的所有单词列表;第一个Map的Key单词的Id
     * 第二个Map的key是单词的结构id,value就是该结构下面的所有信息
     */
    protected final Map<Long, WordDTOLocal> queryCache = new HashMap<>(30);

    /**
     * 本次使用的语种
     */
    private Long languageId;

    @Override
    public void batchAddCategory(List<WordCategoryDetailVO> wordCategoryDetailVOList) {
        this.wordCategoryDetailVOList.addAll(wordCategoryDetailVOList);
    }

    @Override
    public void replaceAddCategory(List<WordCategoryDetailVO> wordCategoryDetailVOList) {
        this.wordCategoryDetailVOList.clear();
        this.wordCategoryDetailVOList.addAll(wordCategoryDetailVOList);
    }

    @Override
    public void addNewCategory(WordCategoryDTO wordCategoryDTO) {
        wordCategoryDTO.setId((long) wordCategoryDetailVOList.size());
        wordCategoryDTO.setCategoryOrder(wordCategoryDetailVOList.size());
        WordCategoryDetailVO wordCategoryDetailVO = new WordCategoryDetailVO();
        wordCategoryDetailVO.setId(wordCategoryDTO.getId());
        wordCategoryDetailVO.setTitle(wordCategoryDTO.getTitle());
        wordCategoryDetailVO.setDescribeInfo(wordCategoryDTO.getDescribeInfo());
        wordCategoryDetailVO.setCategoryOrder(wordCategoryDTO.getCategoryOrder());
        wordCategoryDetailVO.setWordCategoryWordList(new ArrayList<>());
        wordCategoryDetailVOList.add(wordCategoryDetailVO);
        persist();
    }

    @Override
    public void updateWordCategoryDto(int position, WordCategoryDTO wordCategoryDTO) {
        WordCategoryDetailVO wordCategoryDetailVO = getWordCategoryByPosition(position);
        wordCategoryDetailVO.setTitle(wordCategoryDTO.getTitle());
        wordCategoryDetailVO.setDescribeInfo(wordCategoryDTO.getDescribeInfo());
        persist();
    }

    @Override
    public void updateWordCategoryList() {
        // 移动结束后需要根据List的顺序来设置order值
        persist();
    }

    @Override
    public void removeCategory(int position) {
        wordCategoryDetailVOList.remove(position);
        // 重排序当前的收藏夹
        persist();
    }

    @Override
    public int categoryListSize() {
        return wordCategoryDetailVOList.size();
    }

    @Override
    public WordCategoryDetailVO getWordCategoryByPosition(int position) {
        return wordCategoryDetailVOList.get(position);
    }

    @Override
    public String calculationTitle(int position) {
        return TextUtils.isEmpty(wordCategoryDetailVOList.get(position).getTitle()) ?
                calculation(position, EnglishStructure.WORD_ORIGIN.getWordStructureId()) :
                wordCategoryDetailVOList.get(position).getTitle();
    }

    @Override
    public String calculationDescribe(int position) {
        return TextUtils.isEmpty(wordCategoryDetailVOList.get(position).getDescribeInfo()) ?
                calculation(position, EnglishStructure.UK_PHONETIC.getWordStructureId()) :
                wordCategoryDetailVOList.get(position).getDescribeInfo();
    }

    @Override
    public WordDTOLocal getCurrentStructureWordMap() {
        WordCategoryWordDTO wordCategoryWordDTO = getCurrentViewWord();
        if (wordCategoryWordDTO == null) {
            return null;
        }
        return queryCache.get(wordCategoryWordDTO.getWordId());
    }

    @Override
    public void moveCategory(int fromPosition, int toPosition) {
        Collections.swap(wordCategoryDetailVOList, fromPosition, toPosition);
    }

    @Override
    public int currentCategorySize(int categoryPosition) {
        return getWordCategoryByPosition(categoryPosition).getWordCategoryWordList().size();
    }

    @Override
    public boolean addWordToCategory(int categoryPosition, WordCategoryWordDTO addWordCategoryWordDTO) {
        // 得到当前收藏夹
        WordCategoryDetailVO wordCategoryDetailVO = getWordCategoryByPosition(categoryPosition);
        List<WordCategoryWordDTO> wordCategoryWordList = wordCategoryDetailVO.getWordCategoryWordList();
        // 当前收藏夹不能已经存在当前单词
        for (WordCategoryWordDTO wordCategoryWordDTO : wordCategoryWordList) {
            if (wordCategoryWordDTO.getWordId().equals(addWordCategoryWordDTO.getWordId())) {
                return false;
            }
        }
        addWordCategoryWordDTO.setWordOrder(wordCategoryWordList.size());
        addWordCategoryWordDTO.setWordCategoryId(wordCategoryDetailVO.getId());
        wordCategoryWordList.add(addWordCategoryWordDTO);
        persist();
        return true;
    }

    @Override
    public void removeWordFromCategory(int categoryPosition, int position) {
        WordCategoryDetailVO wordCategoryDetailVO = getWordCategoryByPosition(categoryPosition);
        // 重排序收藏夹内的单词顺序
        wordCategoryDetailVO.getWordCategoryWordList().sort((o1, o2) -> o1.getWordOrder() - o2.getWordOrder());
        wordCategoryDetailVO.getWordCategoryWordList().forEach(new Consumer<>() {

            int order = 0;

            @Override
            public void accept(WordCategoryWordDTO wordCategoryWordDTO) {
                wordCategoryWordDTO.setWordOrder(order++);
            }
        });
        persist();
    }

    @Override
    public WordDTOLocal getWordFromCategory(int categoryID, int position) {
        return queryCache.get(getWordCategoryByPosition(categoryID).getWordCategoryWordList()
                .get(position)
                .getWordId());
    }

    @Override
    public void addWordQueryCache(Map<Long, WordDTOLocal> dict) {
        queryCache.putAll(dict);
    }

    @Override
    public void updateWordCategoryWordOrder(int categoryPosition) {
        List<WordCategoryWordDTO> wordCategoryWordList = wordCategoryDetailVOList.get(categoryPosition).getWordCategoryWordList();
        // 移动结束后需要根据List的顺序来设置order值
        // 这里是更新收藏夹内的单词顺序,不是收藏夹顺序
        int order = 0;
        for (WordCategoryWordDTO wordCategoryWordDTO : wordCategoryWordList) {
            wordCategoryWordDTO.setWordOrder(order++);
        }
        persist();
    }

    @Override
    public void moveCategoryWord(int categoryPosition, int fromPosition, int toPosition) {
        Collections.swap(wordCategoryDetailVOList.get(categoryPosition).getWordCategoryWordList(), fromPosition, toPosition);
    }

    @Override
    public void setCurrentLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    @Override
    public Long getCurrentLanguageId() {
        return this.languageId;
    }

    /**
     * 计算标题和计算描述信息的方法复用
     *
     * @param position        单词分类对象在列表中对应的位置
     * @param wordStructureId 要对那个结构字段进行计算(传入结构字段的id)
     * @return 返回计算出的描述信息, 返回值不为null
     * @see AbstractCategoryFunctionHandler#calculationDescribe(int)
     * @see AbstractCategoryFunctionHandler#calculationTitle(int)
     */
    private String calculation(int position, Long wordStructureId) {
        WordCategoryDetailVO wordCategoryDetailVO = getWordCategoryByPosition(position);
        return wordCategoryDetailVO.getWordCategoryWordList()
                .stream()
                .limit(3)
                .map(wordCategoryWordDTO -> queryCache.get(wordCategoryWordDTO.getWordId()).getOrigin())
                .collect(Collectors.joining("、"));
    }

    /**
     * 持久化收藏夹信息
     */
    private void persist() {
        StaticFactory.getExecutorService().submit(() -> {
            // 重新排序wordCategoryDetailVOList
            int order = 0;
            for (WordCategoryDetailVO wordCategoryDetailVO : wordCategoryDetailVOList) {
                wordCategoryDetailVO.setCategoryOrder(order++);
            }
            Gson gson = StaticFactory.getGson();
            String persist = gson.toJson(wordCategoryDetailVOList);
            try {
                FileUtils.writeWithExternalExist(WordContextPath.WORD_STAR.getPath(), persist);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
