package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import com.github.lorenj.wordtint.database.entity.WordNoteEntity;

import java.util.List;

/**
 * @author LorianJay
 * @date 2026/5/9 11:33
 */
@Dao
public interface WordNoteDao {

    @Query("select * from word_note where word_id = :wordId ")
    WordNoteEntity findByWordId(int wordId);

    @Query("SELECT * FROM word_note WHERE word_id in(:wordIdList)")
    List<WordNoteEntity> findAllWordNoteByIdList(List<Integer> wordIdList);

    @Upsert
    void upsert(WordNoteEntity wordNoteEntity);

}
