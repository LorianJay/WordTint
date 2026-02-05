package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * @author cnsukidayo
 * @date 2026/2/4 21:30
 */
@Entity(
        tableName = "recite_record_word",
        indices = {
                @Index(name = "idx_record_id", value = "record_id"),
        }
)
public class ReciteRecordWordEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "record_id")
    public int recordId;

    @ColumnInfo(name = "word_id")
    public int wordId;

    @ColumnInfo(name = "order")
    public int order;

}
