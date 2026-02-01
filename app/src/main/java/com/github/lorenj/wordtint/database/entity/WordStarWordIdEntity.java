package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "word_star_word_id")
public class WordStarWordIdEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "star_id")
    public int starId;

    @ColumnInfo(name = "word_id")
    public int wordId;

    @ColumnInfo(name = "order")
    public int order;
}
