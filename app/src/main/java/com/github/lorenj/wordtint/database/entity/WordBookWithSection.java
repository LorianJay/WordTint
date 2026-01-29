package com.github.lorenj.wordtint.database.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class WordBookWithSection {
    @Embedded
    public WordBookEntity wordBookEntity;

    @Relation(
        parentColumn = "id",
        entityColumn = "book_id"
    )
    public List<WordBookSectionEntity> wordBookSectionEntityList;
}
