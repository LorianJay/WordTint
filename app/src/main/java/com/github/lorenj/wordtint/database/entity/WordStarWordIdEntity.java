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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WordStarWordIdEntity that = (WordStarWordIdEntity) o;

        if (id != that.id) return false;
        if (starId != that.starId) return false;
        if (wordId != that.wordId) return false;
        return order == that.order;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + starId;
        result = 31 * result + wordId;
        result = 31 * result + order;
        return result;
    }
}
