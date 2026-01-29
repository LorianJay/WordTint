package com.github.lorenj.wordtint.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Set;

public class BookSectionViewModel extends ViewModel {

    private final MutableLiveData<Set<Long>> selectedSectionList =
            new MutableLiveData<>(new HashSet<>());

    public LiveData<Set<Long>> getSelectedSectionList() {
        return selectedSectionList;
    }

    public void toggleSection(long sectionId) {
        Set<Long> current = selectedSectionList.getValue();
        // 必须创建新的对象
        if (current == null) {
            current = new HashSet<>();
        } else {
            current = new HashSet<>(current);
        }

        if (current.contains(sectionId)) {
            current.remove(sectionId);
        } else {
            current.add(sectionId);
        }

        selectedSectionList.setValue(current);
    }
}
