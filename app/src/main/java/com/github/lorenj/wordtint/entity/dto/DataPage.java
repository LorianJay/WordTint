package com.github.lorenj.wordtint.entity.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 该分页对象只能把SpringData的Page对象转为DataPage对象<br>
 * 如果需要转换类型则自主调用{@link DataPage#convertMap(Function)} ()}方法
 *
 * @author sukidayo
 * @date 2024/2/13 9:55
 */
public class DataPage<T> {

    private List<T> content;

    private boolean last;

    private long totalElements;

    private int totalPages;

    private int size;

    private int number;

    private boolean first;

    private int numberOfElements;

    public DataPage() {
    }

    /**
     * DataPage 的泛型转换
     *
     * @param mapper 转换函数
     * @param <R>    转换后的泛型
     * @return 转换泛型后的 IPage
     */
    @SuppressWarnings("unchecked")
    public <R> DataPage<R> convertMap(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.getContent().stream().map(mapper).collect(Collectors.toList());
        this.setContent((List<T>) collect);
        return (DataPage<R>) this;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}
