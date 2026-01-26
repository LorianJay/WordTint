package com.github.lorenj.wordtint.utils;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanUtils {

    private static final Map<Class<?>, SoftReference<Map<String, Field>>> resolvedClassCache = new ConcurrentHashMap<>();


    /**
     * 获取一个对象里面字段为null的字段名称集合
     */
    public static String[] getNullValueFieldNames(Object source) {

        //非空校验：NullPointerException
        Objects.requireNonNull(source);

        Class<?> sourceClass = source.getClass();

        //从缓存里面获取，如果缓存里面没有就会进行第一次反射解析
        Map<String, Field> classFieldMap = getClassFieldMapWithCache(sourceClass);

        List<String> nullValueFieldNames = new ArrayList<>();

        classFieldMap.forEach(
                (fieldName, field) -> {
                    try {
                        //挑选出值为null的字段名称
                        if (field.get(source) == null) {
                            nullValueFieldNames.add(fieldName);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );
        return nullValueFieldNames.toArray(new String[]{});
    }


    /**
     * 获取一个对象里面字段不为null的字段名称集合
     */
    public static String[] getNonNullValueFieldNames(Object source) {

        //非空校验
        Objects.requireNonNull(source);

        //获取空值字段名称
        String[] nullValueFieldNames = getNullValueFieldNames(source);

        Map<String, Field> classFieldMap = getClassFieldMapWithCache(source.getClass());

        //获取全部的字段名称，因为原数据没办法修改，所以需要重新建立一个集合来进行判断
        Set<String> allFieldNames = new HashSet<>(classFieldMap.keySet());

        //移除掉值为null的字段名称
        allFieldNames.removeAll(Arrays.asList(nullValueFieldNames));

        return allFieldNames.toArray(new String[]{});
    }


    public static Map<String, Field> resolveClassFieldMap(final Class<?> sourceClass) {

        SoftReference<Map<String, Field>> softReference = resolvedClassCache.get(sourceClass);

        //判断是否已经被初始化
        if (softReference == null || softReference.get() == null) {

            //对同一个字节码对象的解析是同步的，但是不同字节码对象的解析是并发的
            synchronized (sourceClass) {

                softReference = resolvedClassCache.get(sourceClass);

                if (softReference == null || softReference.get() == null) {

                    Map<String, Field> fieldMap = new HashMap<>();

                    /*
                    Returns an array of Field objects reflecting all the fields declared by the class or interface represented by this
                    Class object. This includes public, protected, default access, and private fields, but excludes inherited fields
                    */
                    Field[] declaredFields = sourceClass.getDeclaredFields();
                    Class<?> superclass = sourceClass;
                    while (declaredFields != null && declaredFields.length > 0) {
                        for (Field field : declaredFields) {

                            /*
                            Set the accessible flag for this object to the indicated boolean value.
                            */
                            field.setAccessible(true);

                            fieldMap.put(field.getName(), field);
                        }
                        superclass = superclass.getSuperclass();
                        if (superclass != null && !superclass.equals(Object.class)) {
                            declaredFields = superclass.getDeclaredFields();
                        } else {
                            declaredFields = null;
                        }
                    }

                    //设置为不变Map
                    fieldMap = Collections.unmodifiableMap(fieldMap);

                    softReference = new SoftReference<>(fieldMap);

                    /*
                    更新缓存，将解析后的数据加入到缓存里面去
                     */
                    resolvedClassCache.put(sourceClass, softReference);

                    return fieldMap;
                }
            }
        }

        /*
        运行到这里来的时候要么早就存在，要么就是已经被其他的线程给初始化了
         */
        return softReference.get();
    }

    public static Map<String, Field> getClassFieldMapWithCache(Class<?> sourceClass) {

        //查看缓存里面有没有已经解析完毕的现成的数据
        SoftReference<Map<String, Field>> softReference = resolvedClassCache.get(sourceClass);

        //确保classFieldMap的正确初始化和缓存
        if (softReference == null || softReference.get() == null) {

            //解析字节码对象
            return resolveClassFieldMap(sourceClass);
        } else {

            //从缓存里面正确的取出数据
            return softReference.get();
        }
    }

    public static void copyObjectProperties(Object source, Map<String, Field> sourceFieldMap, Object target, Map<String, Field> targetFieldMap) {

        //进行属性值复制
        sourceFieldMap.forEach(
                (fieldName, sourceField) -> {

                    //查看目标对象是否存在这个字段
                    Field targetField = targetFieldMap.get(fieldName);

                    if (targetField != null) {

                        try {
                            //对目标字段进行赋值操作
                            targetField.set(target, sourceField.get(source));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public static void filterByFieldName(Map<String, Field> fieldMap, String... ignoreFieldNames) {
        for (String ignoreFieldName : ignoreFieldNames) {
            fieldMap.remove(ignoreFieldName);
        }
    }

    /**
     * 将一个对象里面字段相同、类型兼容的数据复制到另外一个对象去
     * 原始功能
     *
     * @param source：从这个对象复制
     * @param target：复制到这个对象来
     */
    public static void copyPropertiesSimple(Object source, Object target) {

        copyObjectProperties(
                source, new HashMap<>(getClassFieldMapWithCache(source.getClass())),
                target, new HashMap<>(getClassFieldMapWithCache(target.getClass())));
    }

    /**
     * 忽略掉非空的字段或者空的字段
     */
    public static void filterByFieldValue(Object object, Map<String, Field> fieldMap, boolean filterNullAble) {

        Iterator<String> iterator = fieldMap.keySet().iterator();

        if (filterNullAble) {

            while (iterator.hasNext()) {

                try {
                    //移除值为null的字段
                    if (fieldMap.get(iterator.next()).get(object) == null) {
                        iterator.remove();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {

            while (iterator.hasNext()) {

                try {
                    //移除字段不为null的字段
                    if (fieldMap.get(iterator.next()).get(object) != null) {
                        iterator.remove();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T> List<T> copyArrayProperties(Class<T> targetType, List<?> sources) {
        List<T> result = new ArrayList<>(sources.size());
        for (Object source : sources) {
            T target = null;
            try {
                target = targetType.getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            copyPropertiesWithNonNullSourceFields(source, target);
            result.add(target);
        }
        return result;
    }

    /**
     * 忽略掉原对象字段值为null的字段
     */
    public static void copyPropertiesWithNonNullSourceFields(Object source, Object target) {

        Map<String, Field> sourceFieldMap = new HashMap<>(getClassFieldMapWithCache(source.getClass()));

        filterByFieldValue(source, sourceFieldMap, true);

        copyObjectProperties(source, sourceFieldMap, target, new HashMap<>(getClassFieldMapWithCache(target.getClass())));
    }

    /**
     * 忽略掉原对象的指定字段的复制
     *
     * @param ignoreFieldNames：需要忽略的原对象字段名称集合
     */
    public static void copyPropertiesWithIgnoreSourceFields(Object source, Object target, String... ignoreFieldNames) {

        Map<String, Field> sourceFieldMap = new HashMap<>(getClassFieldMapWithCache(source.getClass()));

        filterByFieldName(sourceFieldMap, ignoreFieldNames);

        copyObjectProperties(source, sourceFieldMap, target, new HashMap<>(getClassFieldMapWithCache(target.getClass())));
    }

    /**
     * 忽略掉目标对象的指定字段的复制
     *
     * @param ignoreFieldNames：需要忽略的目标对象字段名称集合
     */
    public static void copyPropertiesWithIgnoreTargetFields(Object source, Object target, String... ignoreFieldNames) {

        Map<String, Field> targetFieldMap = new HashMap<>(getClassFieldMapWithCache(target.getClass()));

        filterByFieldName(targetFieldMap, ignoreFieldNames);

        copyObjectProperties(source, new HashMap<>(getClassFieldMapWithCache(source.getClass())), target, targetFieldMap);
    }

    /**
     * 除实现 copyPropertiesSimple 的功能外，如果目标对象的属性值不为null将不进行覆盖
     */
    public static void copyPropertiesWithTargetFieldNonOverwrite(Object source, Object target) {

        Map<String, Field> targetFieldMap = new HashMap<>(getClassFieldMapWithCache(target.getClass()));

        filterByFieldValue(target, targetFieldMap, false);

        copyObjectProperties(source, new HashMap<>(getClassFieldMapWithCache(source.getClass())), target, targetFieldMap);
    }

}

