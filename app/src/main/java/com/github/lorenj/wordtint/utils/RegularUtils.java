package com.github.lorenj.wordtint.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sukidayo
 * @date 2024/7/20 12:39
 */
public class RegularUtils {

    public static List<String> match(String content, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(content);
        List<String> result = new ArrayList<>();
        while (matcher.find()){
            result.add(matcher.group());
        }
        return result;
    }

}
