package com.example.securestoragelabjava.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public final class SettingsManager {

    private static final String FILE_NAME = "user_settings";
    private static final String KEY_USER = "user_name";
    private static final String KEY_LOCALE = "user_locale";
    private static final String KEY_UI_MODE = "ui_mode";

    private SettingsManager() {}

    public static boolean updateSettings(Context context, String user, String locale, String uiMode, boolean urgent) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit()
                .putString(KEY_USER, user)
                .putString(KEY_LOCALE, locale)
                .putString(KEY_UI_MODE, uiMode);

        if (urgent) {
            return editor.commit();
        } else {
            editor.apply();
            return true;
        }
    }

    public static UserData fetchSettings(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return new UserData(
                sharedPrefs.getString(KEY_USER, ""),
                sharedPrefs.getString(KEY_LOCALE, "fr"),
                sharedPrefs.getString(KEY_UI_MODE, "light")
        );
    }

    public static void reset(Context context) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static final class UserData {
        public final String user;
        public final String locale;
        public final String uiMode;

        public UserData(String user, String locale, String uiMode) {
            this.user = user;
            this.locale = locale;
            this.uiMode = uiMode;
        }
    }
}