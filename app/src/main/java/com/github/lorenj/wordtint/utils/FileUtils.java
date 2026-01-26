package com.github.lorenj.wordtint.utils;

import com.github.lorenj.wordtint.context.AnyLanguageWordProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author cnsukidayo
 * @date 2023/2/6 12:56
 */
public class FileUtils {


    public static String readWithExternal(File target) throws IOException {
        return readWithExternal(target.getAbsolutePath());
    }

    public static String readWithExternal(String target) throws IOException {
        return readAll(AnyLanguageWordProperties.getExternalFilesDir(), target);
    }

    /**
     * 读取一个文件,如果不存在就自动创建该文件
     *
     * @param target 目标文件路径
     * @return 返回读取到的文件内容, 如果文件不存在则会返回一个空白串""
     * @throws IOException IO异常
     */
    public static String readWithExternalExist(File target) throws IOException {
        return readWithExternalExist(target.getAbsolutePath());
    }

    /**
     * 读取一个文件,如果不存在就自动创建该文件
     *
     * @param target 目标文件路径
     * @return 返回读取到的文件内容, 如果文件不存在则会返回一个空白串""
     * @throws IOException IO异常
     */
    public static String readWithExternalExist(String target) throws IOException {
        File file = new File(AnyLanguageWordProperties.getExternalFilesDir(), target);
        if (file.exists()) {
            return readAll(AnyLanguageWordProperties.getExternalFilesDir(), target);
        } else {
            file.getParentFile().mkdirs();
            file.createNewFile();
            return "";
        }
    }

    public static String readAll(File parent, String child) throws IOException {
        return readAll(parent.getAbsolutePath(), child);
    }

    public static String readAll(String parent, String child) throws IOException {
        return new String(Files.readAllBytes(Paths.get(parent, child)), StandardCharsets.UTF_8);
    }

    public static String readAll(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 读取一个文件,如果不存在就自动创建该文件
     *
     * @param target 目标文件路径
     * @return 返回读取到的文件内容, 如果文件不存在则会返回一个空白串""
     * @throws IOException IO异常
     */
    public static void writeWithExternalExist(String target, String str) throws IOException {
        File file = new File(AnyLanguageWordProperties.getExternalFilesDir(), target);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            outputStreamWriter.write(str);
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeWithExternalExist(File target, String str) throws IOException {
        writeWithExternalExist(target.getAbsolutePath(), str);
    }

}
