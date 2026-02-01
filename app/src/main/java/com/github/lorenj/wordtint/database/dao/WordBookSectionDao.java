package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.WordBookSectionWordIdEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/1/29 20:24
 */
@Dao
public interface WordBookSectionDao {

    @Query("SELECT COUNT(*) FROM word_book_section_word_id WHERE section_id = :sectionId")
    int countBySectionId(long sectionId);

    @Query("SELECT * FROM word_book_section_word_id WHERE section_id = :sectionId")
    List<WordBookSectionWordIdEntity> findAllBySectionId(Integer sectionId);
}
