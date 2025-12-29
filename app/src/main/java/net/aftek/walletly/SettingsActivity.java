package net.aftek.walletly;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import net.aftek.walletly.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity de definições - configurações da aplicação
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String STAMP = "@SettingsActivity";
    private static final String PREFS_NAME = "WalletlyPrefs";
    private static final String PREF_THEME = "theme";

    // Membros de Dados
    Utils mUtils;
    Spinner mSpnTheme;
    Spinner mSpnLanguage;
    Button mBtnExport;
    Button mBtnImport;
    SharedPreferences mPreferences;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;
    ActivityResultLauncher<String> mFilePickerLauncher;
    boolean isInitializingTheme = true;
    boolean isInitializingLanguage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        setContentView(R.layout.activity_settings);

        init();
    }

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    /**
     * Inicializa os componentes da activity
     */
    void init() {
        mUtils = new Utils(this);

        // Inicializar banco de dados e executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        // Inicializar SharedPreferences
        mPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize file picker launcher (Activity Result API)
        mFilePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Log.d(STAMP, "Arquivo selecionado para importar: " + uri);
                        mUtils.importTransactions(uri, mDatabase, mExecutorService);
                    }
                });

        // Associar views
        mSpnTheme = findViewById(R.id.idSpnThemeSelector);
        mBtnExport = findViewById(R.id.idBtnExport);
        mBtnImport = findViewById(R.id.idBtnImport);

        // Configurar botões de export/import
        mBtnExport.setOnClickListener(v -> {
            Log.d(STAMP, "Botão exportar clicado");
            checkStoragePermissionAndExport();
        });

        mBtnImport.setOnClickListener(v -> {
            Log.d(STAMP, "Botão importar clicado");
            checkStoragePermissionAndImport();
        });

        // Popular spinner com opções de tema
        List<String> themeOptions = new ArrayList<>();
        themeOptions.add(getString(R.string.str_theme_light));
        themeOptions.add(getString(R.string.str_theme_dark));
        themeOptions.add(getString(R.string.str_theme_system));
        mUtils.populateSpinner(mSpnTheme, themeOptions);

        // Configurar listener para mudanças de tema (antes de carregar)
        mSpnTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Evitar aplicar tema durante inicialização
                if (isInitializingTheme) {
                    isInitializingTheme = false;
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
                    default:
                        applyTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        Log.d(STAMP, "Posição inválida, usando tema sistema");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não fazer nada
            }
        });

        // Carregar tema salvo (após configurar listener)
        loadSavedTheme();

        // Associar spinner de idioma
        mSpnLanguage = findViewById(R.id.idSpnLanguageSelector);

        // Popular spinner com opções de idioma
        List<String> languageOptions = new ArrayList<>();
        languageOptions.add(getString(R.string.str_language_system));
        languageOptions.add(getString(R.string.str_language_portuguese));
        languageOptions.add(getString(R.string.str_language_english));
        languageOptions.add(getString(R.string.str_language_spanish));
        languageOptions.add(getString(R.string.str_language_german));
        languageOptions.add(getString(R.string.str_language_french));
        mUtils.populateSpinner(mSpnLanguage, languageOptions);

        // Configurar listener para mudanças de idioma (antes de carregar)
        mSpnLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Evitar aplicar idioma durante inicialização
                if (isInitializingLanguage) {
                    isInitializingLanguage = false;
                    return;
                }

                String languageCode;
                switch (position) {
                    case 0: // System
                        languageCode = LocaleHelper.LANGUAGE_SYSTEM;
                        Log.d(STAMP, "Idioma sistema selecionado");
                        break;
                    case 1: // Portuguese
                        languageCode = LocaleHelper.LANGUAGE_PORTUGUESE;
                        Log.d(STAMP, "Português selecionado");
                        break;
                    case 2: // English
                        languageCode = LocaleHelper.LANGUAGE_ENGLISH;
                        Log.d(STAMP, "English selecionado");
                        break;
                    case 3: // Spanish
                        languageCode = LocaleHelper.LANGUAGE_SPANISH;
                        Log.d(STAMP, "Español selecionado");
                        break;
                    case 4: // German
                        languageCode = LocaleHelper.LANGUAGE_GERMAN;
                        Log.d(STAMP, "Deutsch selecionado");
                        break;
                    case 5: // French
                        languageCode = LocaleHelper.LANGUAGE_FRENCH;
                        Log.d(STAMP, "Français selecionado");
                        break;
                    default:
                        languageCode = LocaleHelper.LANGUAGE_SYSTEM;
                        break;
                }

                applyLanguage(languageCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não fazer nada
            }
        });

        // Carregar idioma salvo (após configurar listener)
        loadSavedLanguage();

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

        // Set selection (listener will skip due to initialization flag)
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

    /**
     * Carrega o idioma salvo nas preferências
     */
    private void loadSavedLanguage() {
        String savedLanguage = LocaleHelper.getSavedLanguage(this);

        int spinnerPosition = 0; // Default: System
        switch (savedLanguage) {
            case LocaleHelper.LANGUAGE_SYSTEM:
                break;
            case LocaleHelper.LANGUAGE_PORTUGUESE:
                spinnerPosition = 1;
                break;
            case LocaleHelper.LANGUAGE_ENGLISH:
                spinnerPosition = 2;
                break;
            case LocaleHelper.LANGUAGE_SPANISH:
                spinnerPosition = 3;
                break;
            case LocaleHelper.LANGUAGE_GERMAN:
                spinnerPosition = 4;
                break;
            case LocaleHelper.LANGUAGE_FRENCH:
                spinnerPosition = 5;
                break;
            default:
                Log.w(STAMP, "Idioma desconhecido: " + savedLanguage + ", usando idioma sistema");
                break;
        }

        // Set selection (listener will skip due to initialization flag)
        mSpnLanguage.setSelection(spinnerPosition);

        Log.d(STAMP, "Idioma carregado: " + savedLanguage + " (posição spinner: " + spinnerPosition + ")");
    }

    /**
     * Aplica o idioma selecionado e reinicia a activity
     *
     * @param languageCode Código do idioma a aplicar
     */
    private void applyLanguage(String languageCode) {
        // Verificar se o idioma realmente mudou
        String currentLanguage = LocaleHelper.getSavedLanguage(this);
        if (currentLanguage.equals(languageCode)) {
            Log.d(STAMP, "Idioma já está aplicado: " + languageCode);
            return;
        }

        // Guardar preferência
        LocaleHelper.setNewLocale(this, languageCode);

        Log.d(STAMP, "Idioma aplicado e guardado: " + languageCode + ", reiniciando activity");

        // Reiniciar activity para aplicar o idioma
        recreate();
    }


    /**
     * Check storage permission and export if granted
     */
    private void checkStoragePermissionAndExport() {
        mUtils.exportTransactions(mDatabase, mExecutorService);
    }

    /**
     * Check storage permission and import if granted
     */
    private void checkStoragePermissionAndImport() {
        mFilePickerLauncher.launch("application/json");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExecutorService != null && !mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
    }
}