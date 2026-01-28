package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.WordBook;

import java.util.List;

@Dao
public interface WordBookDao {
    @Query("SELECT * FROM word_book")
    List<WordBook> findAll();
}
