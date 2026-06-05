package com.example.securestoragelabjava.external;

import android.content.Context;
import com.example.securestoragelabjava.files.TextFileManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public final class ExternalFilesManager {

    private ExternalFilesManager() {}

    public static String export(Context context, String name, String data) throws Exception {
        File folder = context.getExternalFilesDir(null);
        if (folder == null) return null;

        File targetFile = new File(folder, name);
        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(data.getBytes(StandardCharsets.UTF_8));
        }
        return targetFile.getAbsolutePath();
    }

    public static String importFile(Context context, String name) throws Exception {
        File folder = context.getExternalFilesDir(null);
        if (folder == null) return null;

        File targetFile = new File(folder, name);
        if (!targetFile.exists()) return null;
        try (FileInputStream fis = new FileInputStream(targetFile)) {
            return TextFileManager.readFromStream(fis);
        }
    }

    public static boolean deleteExport(Context context, String name) {
        File folder = context.getExternalFilesDir(null);
        if (folder == null) return false;

        File targetFile = new File(folder, name);
        return targetFile.delete();
    }
}