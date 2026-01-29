package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_book_section_word_id")
public class WordBookSectionWordIdEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "section_id")
    public int sectionId;

    @ColumnInfo(name = "word_id")
    public int wordId;

    @ColumnInfo(name = "order")
    public int order;
}
