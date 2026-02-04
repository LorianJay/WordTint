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
        tableName = "recite_record_word_mark",
        indices = {
                @Index(name = "idx_record_word_id", value = {"record_word_id"}),
        }
)
public class ReciteRecordWordMarkEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "record_word_id")
    public int recordWordId;

    @ColumnInfo(name = "mark_color")
    public String markColor;

}
