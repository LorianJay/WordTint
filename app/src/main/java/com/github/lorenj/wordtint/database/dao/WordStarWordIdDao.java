package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import com.github.lorenj.wordtint.database.entity.WordStarWordIdEntity;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/1/29 20:24
 */
@Dao
public interface WordStarWordIdDao {
    @Insert
    void insert(WordStarWordIdEntity wordStarWordIdEntity);

    @Update
    void batchUpdate(List<WordStarWordIdEntity> wordStarWordIdEntityList);
}
