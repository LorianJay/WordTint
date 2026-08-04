package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.WordBookSectionWordIdEntity;

import java.util.List;
import java.util.Optional;

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

    @Insert
    void insert(WordBookSectionWordIdEntity wordBookSectionWordIdEntity);

    @Query("SELECT MAX(`order`) FROM word_book_section_word_id WHERE section_id = :sectionId")
    Optional<Integer> getMaxOrderBySectionId(int sectionId);

    @Query("SELECT * FROM word_book_section_word_id WHERE section_id = :sectionId AND word_id = :wordId")
    WordBookSectionWordIdEntity findBySectionIdAndWordId(int sectionId, int wordId);

    @Query("DELETE FROM word_book_section_word_id WHERE section_id = :sectionId AND word_id = :wordId")
    void deleteBySectionIdAndWordId(int sectionId, int wordId);
}
