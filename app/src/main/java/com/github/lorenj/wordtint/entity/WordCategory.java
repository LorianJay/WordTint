package com.github.lorenj.wordtint.entity;

import com.github.lorenj.wordtint.entity.dto.WordDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 单词的分类实体映射对象
 *
 * @author cnsukidayo
 * @date 2023/1/8 19:32
 */
public class WordCategory {

    private String title;
    private String describe;
    private boolean defaultTitleRule;
    private boolean defaultDescribeRule;
    private int order;

    // todo 这里是模拟测试,暂时使用一个List存储当前分类下的所有单词,集合中单词的顺序就是单词的order
    /**
     * 为了方便实际上存储的是单词结构映射对象<br>
     * Map里存放的就是单词的结构映射;<br>
     * 如果需要修改集合中元素的内容,必须做判断
     */
    private final List<Map<Long, List<WordDTO>>> words = new ArrayList<>();

    /**
     * 用于判断收藏夹中收藏的单词唯一性的集合
     */
    private final Set<Long> uniqueSet = new HashSet<>();

    public WordCategory() {
    }

    public WordCategory(String title, String describe, boolean defaultTitleRule, boolean defaultDescribeRule) {
        this.title = title;
        this.describe = describe;
        this.defaultTitleRule = defaultTitleRule;
        this.defaultDescribeRule = defaultDescribeRule;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public boolean isDefaultTitleRule() {
        return defaultTitleRule;
    }

    public void setDefaultTitleRule(boolean defaultTitleRule) {
        this.defaultTitleRule = defaultTitleRule;
    }

    public boolean isDefaultDescribeRule() {
        return defaultDescribeRule;
    }

    public void setDefaultDescribeRule(boolean defaultDescribeRule) {
        this.defaultDescribeRule = defaultDescribeRule;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<Map<Long, List<WordDTO>>> getWords() {
        return words;
    }

    public Set<Long> getUniqueSet() {
        return uniqueSet;
    }
}
