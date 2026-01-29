package com.github.lorenj.wordtint.database.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.github.lorenj.wordtint.database.entity.WordBookEntity;
import com.github.lorenj.wordtint.database.entity.WordBookSectionEntity;

import java.util.List;

public class WordBookWithSectionEntity {
    @Embedded
    public WordBookEntity wordBookEntity;

    @Relation(
        parentColumn = "id",
        entityColumn = "book_id"
    )
    public List<WordBookSectionEntity> wordBookSectionEntityList;
}
