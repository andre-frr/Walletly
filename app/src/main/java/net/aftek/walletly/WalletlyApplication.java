package net.aftek.walletly;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Classe Application customizada para inicialização global da app
 * Carrega o tema e idioma salvos nas preferências do utilizador
 * Segue o princípio KISS - configuração simples e centralizada
 */
public class WalletlyApplication extends Application {

    private static final String STAMP = "@WalletlyApplication";
    private static final String PREFS_NAME = "WalletlyPrefs";
    private static final String PREF_THEME = "theme";

    /**
     * Inicialização da aplicação
     * Carrega e aplica o tema salvo nas preferências
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(STAMP, "Aplicação iniciada");

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTheme = preferences.getInt(PREF_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        AppCompatDelegate.setDefaultNightMode(savedTheme);
        Log.d(STAMP, "Tema aplicado: " + savedTheme);
    }

    /**
     * Anexa o contexto base com o locale configurado
     * Chamado antes de onCreate() para garantir que o idioma correto é aplicado
     *
     * @param base Contexto base da aplicação
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.setLocale(base));
        Log.d(STAMP, "Contexto base com locale aplicado");
    }
}