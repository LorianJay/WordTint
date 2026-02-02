package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "word_star")
public class WordStarEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id ;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "describe_info")
    public String describeInfo;

    @ColumnInfo(name = "order")
    public int order;

}
