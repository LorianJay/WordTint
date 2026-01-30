package com.github.lorenj.wordtint.database.rep;

import com.github.lorenj.wordtint.database.dao.UserSettingDao;
import com.github.lorenj.wordtint.database.entity.UserSettingEntity;
import com.github.lorenj.wordtint.enums.UserSettingKeyEnums;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author cnsukidayo
 * @date 2026/1/30 13:29
 */
public class UserSettingRepository {
    private static volatile UserSettingRepository INSTANCE;
    private final UserSettingDao dao;

    private UserSettingRepository(UserSettingDao dao) {
        this.dao = dao;
    }

    public static UserSettingRepository getInstance(UserSettingDao dao) {
        if (INSTANCE == null) {
            synchronized (UserSettingRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserSettingRepository(dao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取用户的设置配置
     *
     * @param userSettingKey 设置的key
     * @param <T>
     * @return
     */
    public <T> T getUserSettingValue(UserSettingKeyEnums userSettingKey) {
        return parseByValueType(getUserSettingEntity(userSettingKey));
    }

    /**
     * 获取用户的设置实体类
     *
     * @param userSettingKey 设置的key
     * @return
     */
    public UserSettingEntity getUserSettingEntity(UserSettingKeyEnums userSettingKey) {
        UserSettingEntity byKey = dao.findByKey(userSettingKey.name());
        return Optional.ofNullable(byKey)
                .orElseGet(() -> {
                    UserSettingEntity insert = new UserSettingEntity();
                    insert.settingKey = userSettingKey.name();
                    insert.valueType = userSettingKey.type.getName();
                    insert.value = String.valueOf(userSettingKey.defaultValue);
                    dao.insert(insert);
                    return insert;
                });
    }

    public <T> void update(UserSettingKeyEnums userSettingKeyEnums, T value) {
        dao.updateValue(userSettingKeyEnums.name(), String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    private <T> T parseByValueType(UserSettingEntity entity) {
        try {
            Class<?> clazz = Class.forName(entity.valueType);
            if (clazz == String.class) {
                return (T) entity.value;
            }
            Method valueOf = clazz.getMethod("valueOf", String.class);
            return (T) valueOf.invoke(null, entity.value);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unknown valueType: " + entity.valueType, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Type " + entity.valueType + " must have static valueOf(String)", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
