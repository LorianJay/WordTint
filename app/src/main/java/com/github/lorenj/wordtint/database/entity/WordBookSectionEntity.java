package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_book_section")
public class WordBookSectionEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "book_id")
    public int bookId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "order")
    public int order;

    @ColumnInfo(name = "tagColor")
    public String tagColor;
}
