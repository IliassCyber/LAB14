package com.example.securestoragelabjava;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.securestoragelabjava.cache.CacheHelper;
import com.example.securestoragelabjava.external.ExternalFilesManager;
import com.example.securestoragelabjava.files.EntityJsonStore;
import com.example.securestoragelabjava.files.TextFileManager;
import com.example.securestoragelabjava.model.AppEntity;
import com.example.securestoragelabjava.prefs.SettingsManager;
import com.example.securestoragelabjava.prefs.VaultManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Lab14Storage";
    private final List<String> availableLocales = Arrays.asList("fr", "en", "es", "de");

    private EditText inputUser;
    private EditText inputSecret;
    private Spinner spinnerLocale;
    private Switch toggleDarkMode;
    private TextView statusDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupLocaleSelector();
        bindEvents();
        refreshUiFromPrefs();
    }

    private void initViews() {
        inputUser = findViewById(R.id.inputUser);
        inputSecret = findViewById(R.id.inputSecret);
        spinnerLocale = findViewById(R.id.spinnerLocale);
        toggleDarkMode = findViewById(R.id.toggleDarkMode);
        statusDisplay = findViewById(R.id.statusDisplay);
    }

    private void setupLocaleSelector() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_dropdown_item, availableLocales);
        spinnerLocale.setAdapter(adapter);
    }

    private void bindEvents() {
        findViewById(R.id.btnSaveSettings).setOnClickListener(v -> performSaveSettings());
        findViewById(R.id.btnFetchSettings).setOnClickListener(v -> refreshUiFromPrefs());
        findViewById(R.id.btnPersistJson).setOnClickListener(v -> performSaveJson());
        findViewById(R.id.btnRetrieveJson).setOnClickListener(v -> performLoadJson());
        findViewById(R.id.btnWipeAll).setOnClickListener(v -> handleReset());
    }

    private void performSaveSettings() {
        String user = inputUser.getText().toString().trim();
        String locale = availableLocales.get(spinnerLocale.getSelectedItemPosition());
        String mode = toggleDarkMode.isChecked() ? "dark" : "light";

        boolean success = SettingsManager.updateSettings(this, user, locale, mode, false);

        String secret = inputSecret.getText().toString();
        if (!secret.isEmpty()) {
            try {
                VaultManager.saveSecret(this, secret);
            } catch (Exception e) {
                updateStatus("Vault Error: " + e.getMessage());
                return;
            }
        }

        try {
            CacheHelper.saveToCache(this, "session_info.txt", "User: " + user + " | " + mode);
        } catch (Exception ignored) {}

        Log.i(LOG_TAG, "Settings stored: success=" + success + ", user=" + user);
        updateStatus("Paramètres enregistrés.\nUtilisateur: " + user + "\nLocale: " + locale + "\nMode: " + mode);
    }

    private void refreshUiFromPrefs() {
        SettingsManager.UserData data = SettingsManager.fetchSettings(this);

        inputUser.setText(data.user);
        toggleDarkMode.setChecked("dark".equals(data.uiMode));

        int pos = availableLocales.indexOf(data.locale);
        spinnerLocale.setSelection(pos >= 0 ? pos : 0);

        int secretSize = 0;
        try {
            String secret = VaultManager.getSecret(this);
            secretSize = (secret != null) ? secret.length() : 0;
        } catch (Exception ignored) {}

        updateStatus("Données chargées.\nUtilisateur: " + data.user + "\nSecret length: " + secretSize);
        Log.d(LOG_TAG, "UI Refreshed from storage.");
    }

    private void performSaveJson() {
        List<AppEntity> entities = Arrays.asList(
                new AppEntity(101, "Main Engine", 85),
                new AppEntity(102, "Auxiliary Power", 42),
                new AppEntity(103, "Shield Generator", 98)
        );

        try {
            EntityJsonStore.persist(this, entities);
            TextFileManager.saveText(this, "log.txt", "JSON Export successful.");
            updateStatus("Export JSON réussi (" + entities.size() + " éléments)");
        } catch (Exception e) {
            updateStatus("JSON Error: " + e.getMessage());
        }
    }

    private void performLoadJson() {
        List<AppEntity> list = EntityJsonStore.retrieve(this);
        String logInfo;
        try {
            logInfo = TextFileManager.loadText(this, "log.txt");
        } catch (Exception e) {
            logInfo = "No log file found.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Données JSON:\n").append(logInfo).append("\n");
        for (AppEntity entity : list) {
            builder.append(" - [").append(entity.getIdentifier()).append("] ")
                   .append(entity.getLabel()).append(" : ").append(entity.getValue()).append("\n");
        }
        updateStatus(builder.toString());
    }

    private void handleReset() {
        SettingsManager.reset(this);
        try {
            VaultManager.wipe(this);
        } catch (Exception ignored) {}

        EntityJsonStore.delete(this);
        TextFileManager.removeFile(this, "log.txt");
        int deleted = CacheHelper.clearCache(this);

        inputUser.setText("");
        inputSecret.setText("");
        toggleDarkMode.setChecked(false);
        spinnerLocale.setSelection(0);

        updateStatus("Système réinitialisé.\nCache vidé: " + deleted + " fichiers.");
        Log.w(LOG_TAG, "Full data wipe executed.");
    }

    private void updateStatus(String msg) {
        statusDisplay.setText("Statut:\n" + msg);
    }
}