package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.github.lorenj.wordtint.database.entity.WordBookWithSectionEntity;

import java.util.List;

@Dao
public interface WordBookDao {
    /**
     * 查询所有的书以及它对应的章节
     */
    @Transaction
    @Query("SELECT * FROM word_book")
    List<WordBookWithSectionEntity> findAllBookAndSections();
}
