package net.aftek.walletly;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Classe Application customizada para inicialização global da app
 * Carrega o tema salvo nas preferências do utilizador
 */
public class WalletlyApplication extends Application {

    private static final String PREFS_NAME = "WalletlyPrefs";
    private static final String PREF_THEME = "theme";

    @Override
    public void onCreate() {
        super.onCreate();

        // Carregar e aplicar o tema salvo
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTheme = preferences.getInt(PREF_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }
}