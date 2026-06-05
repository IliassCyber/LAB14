package com.example.securestoragelabjava.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public final class VaultManager {

    private static final String VAULT_NAME = "secure_vault";
    private static final String KEY_SECRET_TOKEN = "vault_secret_token";

    private VaultManager() {}

    private static SharedPreferences getSecureInstance(Context context) throws Exception {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                VAULT_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public static void saveSecret(Context context, String token) throws Exception {
        getSecureInstance(context).edit().putString(KEY_SECRET_TOKEN, token).apply();
    }

    public static String getSecret(Context context) throws Exception {
        return getSecureInstance(context).getString(KEY_SECRET_TOKEN, "");
    }

    public static void wipe(Context context) throws Exception {
        getSecureInstance(context).edit().clear().apply();
    }
}