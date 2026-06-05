package com.example.securestoragelabjava.files;

import android.content.Context;
import com.example.securestoragelabjava.model.AppEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EntityJsonStore {

    public static final String DATA_FILE = "entities.json";

    private EntityJsonStore() {}

    public static void persist(Context context, List<AppEntity> entities) throws Exception {
        JSONArray array = new JSONArray();
        for (AppEntity e : entities) {
            JSONObject obj = new JSONObject();
            obj.put("id", e.getIdentifier());
            obj.put("label", e.getLabel());
            obj.put("value", e.getValue());
            array.put(obj);
        }
        String json = array.toString();
        try (FileOutputStream fos = context.openFileOutput(DATA_FILE, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static List<AppEntity> retrieve(Context context) {
        try (FileInputStream fis = context.openFileInput(DATA_FILE)) {
            String json = TextFileManager.readFromStream(fis);
            JSONArray array = new JSONArray(json);
            List<AppEntity> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new AppEntity(
                        obj.getInt("id"),
                        obj.getString("label"),
                        obj.getInt("value")
                ));
            }
            return list;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static void delete(Context context) {
        context.deleteFile(DATA_FILE);
    }
}