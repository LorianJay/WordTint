package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * @author cnsukidayo
 * @date 2026/2/4 14:18
 */
@Entity(
        tableName = "word_search",
        indices = {
                @Index(name = "idx_word_origin", value = "word_origin")
        }
)
public class WordSearchEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "word_id")
    public int wordId;

    @ColumnInfo(name = "word_origin")
    public String wordOrigin;

}
