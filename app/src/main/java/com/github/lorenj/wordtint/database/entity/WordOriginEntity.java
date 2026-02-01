package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "word_origin",
        indices = {
                @Index(name = "idx_word_kv_word_id", value = "word_id"),
                @Index(name = "idx_word_kv_word_id_key", value = {"word_id", "key"})
        }
)
public class WordOriginEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "word_id")
    public int wordId;

    @ColumnInfo(name = "key")
    public String key;

    @ColumnInfo(name = "value")
    public String value;
}
