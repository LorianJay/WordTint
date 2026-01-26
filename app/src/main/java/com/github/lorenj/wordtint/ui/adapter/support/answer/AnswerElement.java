package com.github.lorenj.wordtint.ui.adapter.support.answer;

import java.util.ArrayList;
import java.util.List;

/**
 * answer-element答案的每个元素
 *
 * @author cnsukidayo
 * @date 2023/11/18 15:34
 */
public class AnswerElement {

    /**
     * 显示的结果key
     */
    private String key;

    /**
     * 显示key的颜色
     */
    private int keyColor;

    /**
     * 显示value的颜色
     */
    private int valueColor;

    /**
     * key和value之间是否有断行,默认是没有断行的
     */
    private boolean hasBreak;

    /**
     * 组标志
     */
    private Long groupFlag;

    /**
     * 组id
     */
    private Long groupId;

    /**
     * 顺序
     */
    private int order;

    /**
     * 组关系
     */
    private List<AnswerElement> answerElementGroup;

    private int weight;

    private AnswerElement(String key,
                          int keyColor,
                          int valueColor,
                          boolean hasBreak,
                          Long groupFlag,
                          Long groupId,
                          Integer order,
                          int weight,
                          List<AnswerElement> answerElementGroup) {
        this.key = key;
        this.keyColor = keyColor;
        this.valueColor = valueColor;
        this.groupFlag = groupFlag;
        this.groupId = groupId;
        this.order = order;
        this.answerElementGroup = answerElementGroup;
        this.weight = weight;
        this.hasBreak = hasBreak;
    }

    /**
     * <strong>警告!</strong><br>
     * 浅克隆方法,本类并没有实现{@link Cloneable}接口,不提供clone的保障
     *
     * @return
     */
    public AnswerElement clone() {
        return new Builder().key(getKey())
                .keyColor(getKeyColor())
                .valueColor(getValueColor())
                .hasBreak(hasBreak())
                .groupFlag(getGroupFlag())
                .groupId(getGroupId())
                .order(getOrder())
                .weight(getWeight())
                .answerElementGroup(getAnswerElementGroup() == null ? getAnswerElementGroup() : new ArrayList<>(getAnswerElementGroup()))
                .build();
    }

    public void addChildElement(AnswerElement answerElement) {
        this.getAnswerElementGroup().add(answerElement);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getKeyColor() {
        return keyColor;
    }

    public void setKeyColor(int keyColor) {
        this.keyColor = keyColor;
    }

    public int getValueColor() {
        return valueColor;
    }

    public void setValueColor(int valueColor) {
        this.valueColor = valueColor;
    }

    public boolean hasBreak() {
        return hasBreak;
    }

    public void setHasBreak(boolean hasBreak) {
        this.hasBreak = hasBreak;
    }

    public List<AnswerElement> getAnswerElementGroup() {
        return answerElementGroup;
    }

    public void setAnswerElementGroup(List<AnswerElement> answerElementGroup) {
        this.answerElementGroup = answerElementGroup;
    }

    public Long getGroupFlag() {
        return groupFlag;
    }

    public void setGroupFlag(Long groupFlag) {
        this.groupFlag = groupFlag;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public static class Builder {

        private String key;
        private int keyColor;
        private int valueColor;
        private boolean hasBreak;
        private Long groupFlag;
        private Long groupId;
        private int order;
        private int weight;
        private List<AnswerElement> answerElementGroup;

        public AnswerElement build() {
            return new AnswerElement(
                    key,
                    keyColor,
                    valueColor,
                    hasBreak,
                    groupFlag,
                    groupId,
                    order,
                    weight,
                    answerElementGroup);
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder keyColor(int keyColor) {
            this.keyColor = keyColor;
            return this;
        }

        public Builder valueColor(int valueColor) {
            this.valueColor = valueColor;
            return this;
        }

        public Builder hasBreak(boolean hasBreak) {
            this.hasBreak = hasBreak;
            return this;
        }

        public Builder answerElementGroup(List<AnswerElement> answerElementGroup) {
            this.answerElementGroup = answerElementGroup;
            return this;
        }

        public Builder groupFlag(Long groupFlag) {
            this.groupFlag = groupFlag;
            return this;
        }

        public Builder groupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder order(Integer order) {
            this.order = order;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

    }

}
