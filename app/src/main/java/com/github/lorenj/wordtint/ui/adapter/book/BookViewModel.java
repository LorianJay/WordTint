package com.github.lorenj.wordtint.ui.adapter.book;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.lorenj.wordtint.database.entity.WordBookSectionEntity;

import java.util.HashMap;
import java.util.Map;

public class BookViewModel extends ViewModel {

    /**
     * 所有当前用户选择的章节map
     */
    private final MutableLiveData<Map<Integer, WordBookSectionEntity>> selectedSectionList =
            new MutableLiveData<>(new HashMap<>());

    public LiveData<Map<Integer, WordBookSectionEntity>> getSelectedSectionList() {
        return selectedSectionList;
    }

    /**
     * 选择某一个章节/片段
     *
     * @param wordBookSectionEntity 章节实体对象
     */
    public void selectSection(WordBookSectionEntity wordBookSectionEntity) {
        Map<Integer, WordBookSectionEntity> current = selectedSectionList.getValue();
        // 必须创建新的对象
        if (current == null) {
            current = new HashMap<>();
        } else {
            current = new HashMap<>(current);
        }
        if (current.containsKey(wordBookSectionEntity.id)) {
            current.remove(wordBookSectionEntity.id);
        } else {
            current.put(wordBookSectionEntity.id, wordBookSectionEntity);
        }
        selectedSectionList.setValue(current);
    }
}
