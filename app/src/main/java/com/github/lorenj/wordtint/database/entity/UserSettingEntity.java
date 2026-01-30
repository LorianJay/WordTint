package com.github.lorenj.wordtint.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 用户设置表
 */
@Entity(tableName = "user_setting")
public class UserSettingEntity {

    @PrimaryKey
    @ColumnInfo(name = "setting_key")
    @NonNull
    public String settingKey;

    @ColumnInfo(name = "value_type")
    public String valueType;

    @ColumnInfo(name = "setting_value")
    public String value;
}
