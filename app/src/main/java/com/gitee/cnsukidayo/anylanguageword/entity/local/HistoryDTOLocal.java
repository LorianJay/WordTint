package com.gitee.cnsukidayo.anylanguageword.entity.local;

import java.io.Serializable;
import java.util.List;

/**
 * @author cnsukidayo
 * @date 2024/7/16 23:05
 */
public class HistoryDTOLocal implements Serializable {
    /**
     * 当前历史记录的名称
     */
    private String name;
    /**
     * 当前历史记录的顺序
     */
    private Long order;
    /**
     * 当前历史的文件路径
     */
    private String path;

    private List<FunctionWordDTOLocal> serializeWordList;

    public HistoryDTOLocal() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FunctionWordDTOLocal> getSerializeWordList() {
        return serializeWordList;
    }

    public void setSerializeWordList(List<FunctionWordDTOLocal> serializeWordList) {
        this.serializeWordList = serializeWordList;
    }
}