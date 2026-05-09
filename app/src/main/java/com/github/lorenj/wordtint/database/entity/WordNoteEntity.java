package com.github.lorenj.wordtint.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author LorianJay
 * @date 2026/5/9 11:31
 */
@Entity(tableName = "word_note")
public class WordNoteEntity {

    @PrimaryKey
    @ColumnInfo(name = "word_id")
    public int wordId;

    @ColumnInfo(name = "word_note")
    public String wordNote;

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getWordNote() {
        return wordNote;
    }

    public void setWordNote(String wordNote) {
        this.wordNote = wordNote;
    }
}
