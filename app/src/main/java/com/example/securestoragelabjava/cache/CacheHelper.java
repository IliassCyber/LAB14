package com.example.securestoragelabjava.cache;

import android.content.Context;
import com.example.securestoragelabjava.files.TextFileManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public final class CacheHelper {

    private CacheHelper() {}

    public static void saveToCache(Context context, String name, String data) throws Exception {
        File cacheFile = new File(context.getCacheDir(), name);
        try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
            fos.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static String readFromCache(Context context, String name) throws Exception {
        File cacheFile = new File(context.getCacheDir(), name);
        if (!cacheFile.exists()) return null;
        try (FileInputStream fis = new FileInputStream(cacheFile)) {
            return TextFileManager.readFromStream(fis);
        }
    }

    public static int clearCache(Context context) {
        File[] files = context.getCacheDir().listFiles();
        if (files == null) return 0;
        int count = 0;
        for (File f : files) {
            if (f.delete()) count++;
        }
        return count;
    }
}