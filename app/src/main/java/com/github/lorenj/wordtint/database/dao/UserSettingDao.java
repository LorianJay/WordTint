package com.github.lorenj.wordtint.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.lorenj.wordtint.database.entity.UserSettingEntity;

@Dao
public interface UserSettingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserSettingEntity entity);

    @Query("UPDATE user_setting SET setting_value = :settingValue WHERE setting_key = :settingKey")
    void updateValue(String settingKey, String settingValue);

    @Query("SELECT * FROM user_setting WHERE setting_key = :settingKey LIMIT 1")
    UserSettingEntity findByKey(String settingKey);

}
