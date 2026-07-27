package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.WordMarkLogEntity;
import com.github.lorenj.wordtint.database.vo.WordMarkCountVO;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2026/7/26
 */
@Dao
public interface WordMarkLogDao {

    @Insert
    void insert(List<WordMarkLogEntity> wordMarkLogEntityList);

    @Query("SELECT * FROM word_mark_log WHERE mark_word_id = :wordId AND mark_color = :color AND timestamp > :minTime ORDER BY timestamp DESC LIMIT 1")
    WordMarkLogEntity findRecentByWordIdAndColor(int wordId, String color, long minTime);

    @Delete
    void delete(WordMarkLogEntity entity);

    /**
     * 按颜色和时间范围统计单词标记次数，按次数降序排列
     */
    @Query("SELECT mark_word_id AS markWordId, COUNT(*) AS count, AVG(stay_time) AS avgStayTime " +
            "FROM word_mark_log WHERE mark_color = :color AND timestamp BETWEEN :start AND :end " +
            "GROUP BY mark_word_id ORDER BY count DESC limit :limit")
    List<WordMarkCountVO> findWordMarkCounts(String color, long start, long end, int limit);

    /**
     * 获取指定单词在时间范围内的标记记录
     */
    @Query("SELECT * FROM word_mark_log WHERE mark_word_id = :wordId AND mark_color = :color " +
            "AND timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    List<WordMarkLogEntity> findByWordIdAndColor(int wordId, String color, long start, long end);

    /**
     * 按颜色获取时间范围内的所有标记记录
     */
    @Query("SELECT * FROM word_mark_log WHERE mark_color = :color " +
            "AND timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    List<WordMarkLogEntity> findByColorAndTimeRange(String color, long start, long end);

}
