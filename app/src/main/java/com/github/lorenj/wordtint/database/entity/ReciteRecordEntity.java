package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * @author cnsukidayo
 * @date 2026/2/4 21:30
 */
@Entity(tableName = "recite_record")
public class ReciteRecordEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "create_time")
    public long createTime;

    @ColumnInfo(name = "word_count")
    public int wordCount;

    @ColumnInfo(name = "recite_mode")
    public String reciteMode;

    @ColumnInfo(name = "recite_order")
    public String reciteOrder;

    @ColumnInfo(name = "recite_filer")
    public String reciteFiler;

    @ColumnInfo(name = "hide_preposition")
    public boolean hidePreposition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReciteRecordEntity that = (ReciteRecordEntity) o;
        return id == that.id
                && createTime == that.createTime
                && wordCount == that.wordCount
                && hidePreposition == that.hidePreposition
                && Objects.equals(reciteMode, that.reciteMode)
                && Objects.equals(reciteOrder, that.reciteOrder)
                && Objects.equals(reciteFiler, that.reciteFiler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createTime, wordCount, reciteMode, reciteOrder, reciteFiler, hidePreposition);
    }
}
