package com.github.lorenj.wordtint.context;

import java.io.File;

public class AnyLanguageWordProperties {

    private static File externalFilesDir = null;

    public static final String androidPackageName = "android";

    public static final String ip = "119.45.248.231";
    public static final String port = "8200";
    public static final String imagePrefix = "https://cnsukidayo.oss-cn-shanghai.aliyuncs.com/";

    public static File getExternalFilesDir() {
        return AnyLanguageWordProperties.externalFilesDir;
    }

    public static void setExternalFilesDir(File externalFilesDir) {
        AnyLanguageWordProperties.externalFilesDir = externalFilesDir;
    }

    private AnyLanguageWordProperties() {

    }
}
