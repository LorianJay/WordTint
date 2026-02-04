package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.WordSearchEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/2/4 14:19
 */
@Dao
public interface WordSearchDao {

    @Query("SELECT * FROM word_search WHERE word_origin like '%' || :wordOrigin || '%' LIMIT :arg0,:arg1")
    List<WordSearchEntity> searchWordLikeOrigin(String wordOrigin, int arg0, int arg1);

    @Query("SELECT count(1) FROM word_search WHERE word_origin like '%' || :wordOrigin || '%'")
    Integer countWordLikeOrigin(String wordOrigin);

}
