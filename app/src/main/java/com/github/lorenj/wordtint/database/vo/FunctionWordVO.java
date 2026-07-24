package com.github.lorenj.wordtint.database.vo;

import com.github.lorenj.wordtint.database.entity.WordNoteEntity;
import com.github.lorenj.wordtint.enums.MarkColor;
import com.github.lorenj.wordtint.enums.WordStructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author cnsukidayo
 * @date 2026/1/31 16:18
 */
public class FunctionWordVO {
    /**
     * 单词的id
     */
    private int wordId;

    /**
     * 单词的来源
     */
    private List<Integer> bookId;

    /**
     * 当前单词对应的标记颜色列表
     */
    private Set<MarkColor> markColorList = new HashSet<>(2);

    /**
     * 当前单词存储的意思
     */
    private Map<WordStructure, WordOriginVO> value = new HashMap<>(2);

    /**
     * 单词注释
     */
    private WordNoteEntity wordNoteEntity;

    public FunctionWordVO() {
        markColorList.add(MarkColor.GREEN);
        markColorList.add(MarkColor.BROWN);
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public List<Integer> getBookId() {
        return bookId;
    }

    public void setBookId(List<Integer> bookId) {
        this.bookId = bookId;
    }

    public Set<MarkColor> getMarkColorList() {
        return markColorList;
    }

    public void setMarkColorList(Set<MarkColor> markColorList) {
        this.markColorList = markColorList;
    }

    public Map<WordStructure, WordOriginVO> getValue() {
        return value;
    }

    public void setValue(Map<WordStructure, WordOriginVO> value) {
        this.value = value;
    }

    public WordNoteEntity getWordNoteEntity() {
        return wordNoteEntity;
    }

    public void setWordNoteEntity(WordNoteEntity wordNoteEntity) {
        this.wordNoteEntity = wordNoteEntity;
    }
}
