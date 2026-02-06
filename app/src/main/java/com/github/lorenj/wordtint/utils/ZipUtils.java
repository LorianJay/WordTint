package com.github.lorenj.wordtint.utils;

import android.content.Context;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    /**
     * 将 assets 目录下的压缩包解压到指定目录
     * @param context    上下文
     * @param assetName  assets 中的文件名 (如 "audio.zip")
     * @param targetDir  解压到的目标 File 对象
     */
    public static void unzipFromAssets(Context context, String assetName, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // 关键：通过 context.getAssets().open() 获取输入流
        try (InputStream is = context.getAssets().open(assetName);
             ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is))) {
            
            ZipEntry ze;
            byte[] buffer = new byte[1024 * 8];

            while ((ze = zis.getNextEntry()) != null) {
                File outputFile = new File(targetDir, ze.getName());

                // 安全校验：防止 Zip Slip 漏洞
                if (!outputFile.getCanonicalPath().startsWith(targetDir.getCanonicalPath())) {
                    throw new IOException("非法解压路径: " + ze.getName());
                }

                if (ze.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    File parent = outputFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }

                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        int count;
                        while ((count = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, count);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}