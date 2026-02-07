package com.github.lorenj.wordtint.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author cnsukidayo
 * @date 2023/2/6 12:56
 */
public class FileUtils {

    public static String inputStreamToString(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
