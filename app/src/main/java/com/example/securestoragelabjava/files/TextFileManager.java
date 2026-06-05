package com.example.securestoragelabjava.files;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class TextFileManager {

    private TextFileManager() {}

    public static void saveText(Context context, String filename, String data) throws Exception {
        try (FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            outputStream.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static String loadText(Context context, String filename) throws Exception {
        try (FileInputStream inputStream = context.openFileInput(filename)) {
            return readFromStream(inputStream);
        }
    }

    public static String readFromStream(InputStream is) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }

    public static boolean removeFile(Context context, String filename) {
        return context.deleteFile(filename);
    }
}