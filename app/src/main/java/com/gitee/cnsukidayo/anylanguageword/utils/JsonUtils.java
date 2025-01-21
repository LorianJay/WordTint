package com.gitee.cnsukidayo.anylanguageword.utils;

import androidx.annotation.NonNull;

import com.gitee.cnsukidayo.anylanguageword.context.support.factory.StaticFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cnsukidayo
 * @date 2023/2/10 13:46
 */
public class JsonUtils {

    public static <T> T readJson(@NonNull String externalPath, Class<T> classOfT) throws IOException {
        T result = StaticFactory.getGson().fromJson(FileUtils.readWithExternalExist(externalPath), classOfT);
        if (result == null) {
            try {
                result = classOfT.newInstance();
                writeJson(externalPath, result);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }

    public static <T> List<T> readJsonArray(@NonNull String externalPath, Class<T> tClass) throws IOException {
        List<T> result = StaticFactory.getGson().fromJson(FileUtils.readWithExternalExist(externalPath), new ParameterizedTypeImpl(tClass));
        writeJson(externalPath, Optional.ofNullable(result).orElse(new ArrayList<>()));
        return result;
    }

    public static <T> void writeJson(String externalPath, @NonNull T targetObject) throws IOException {
        FileUtils.writeWithExternalExist(externalPath, StaticFactory.getGson().toJson(targetObject, targetObject.getClass()));
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class<?> clazz;

        public ParameterizedTypeImpl(Class<?> clz) {
            clazz = clz;
        }

        @NonNull
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @NonNull
        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

}
