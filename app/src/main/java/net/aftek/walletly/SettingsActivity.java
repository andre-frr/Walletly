package net.aftek.walletly;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity de definições - configurações da aplicação
 */
public class SettingsActivity extends AppCompatActivity {

    public final static String STAMP = "@SettingsActivity";
    private static final String PREFS_NAME = "WalletlyPrefs";
    private static final String PREF_THEME = "theme";

    // Membros de Dados
    Utils mUtils;
    Spinner mSpnTheme;
    SharedPreferences mPreferences;
    boolean isInitializing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        setContentView(R.layout.activity_settings);

        init();
    }

    /**
     * Inicializa os componentes da activity
     */
    void init() {
        mUtils = new Utils(this);

        // Inicializar SharedPreferences
        mPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Associar views
        mSpnTheme = findViewById(R.id.idSpnThemeSelector);

        // Popular spinner com opções de tema
        List<String> themeOptions = new ArrayList<>();
        themeOptions.add(getString(R.string.str_theme_light));
        themeOptions.add(getString(R.string.str_theme_dark));
        themeOptions.add(getString(R.string.str_theme_system));
        mUtils.populateSpinner(mSpnTheme, themeOptions);

        // Carregar tema salvo
        loadSavedTheme();

        // Configurar listener para mudanças de tema
        mSpnTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Evitar aplicar tema durante inicialização
                if (isInitializing) {
                    isInitializing = false;
                    return;
                }

                switch (position) {
                    case 0: // Light
                        applyTheme(AppCompatDelegate.MODE_NIGHT_NO);
                        Log.d(STAMP, "Tema claro selecionado");
                        break;
                    case 1: // Dark
                        applyTheme(AppCompatDelegate.MODE_NIGHT_YES);
                        Log.d(STAMP, "Tema escuro selecionado");
                        break;
                    case 2: // System
                        applyTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        Log.d(STAMP, "Tema sistema selecionado");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não fazer nada
            }
        });

        // Configurar barra de navegação
        mUtils.setupBottomNavigation(R.id.nav_settings);

        Log.d(STAMP, "Activity de definições inicializada");
    }

    /**
     * Carrega o tema salvo nas preferências
     */
    private void loadSavedTheme() {
        int savedTheme = mPreferences.getInt(PREF_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        int spinnerPosition = 2; // Default: System
        if (savedTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            spinnerPosition = 0; // Light
        } else if (savedTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            spinnerPosition = 1; // Dark
        }

        mSpnTheme.setSelection(spinnerPosition);

        Log.d(STAMP, "Tema carregado: " + savedTheme + " (posição spinner: " + spinnerPosition + ")");
    }

    /**
     * Aplica o tema selecionado e guarda nas preferências
     *
     * @param themeMode Modo de tema a aplicar
     */
    private void applyTheme(int themeMode) {
        // Guardar preferência
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(PREF_THEME, themeMode);
        editor.apply();

        // Aplicar tema
        AppCompatDelegate.setDefaultNightMode(themeMode);

        Log.d(STAMP, "Tema aplicado e guardado: " + themeMode);
    }
}