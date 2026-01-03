package net.aftek.walletly.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.util.Log;

import java.util.Locale;

/**
 * Helper class para gerir mudanças de idioma na aplicação
 */
public class LocaleHelper {

    public static final String LANGUAGE_SYSTEM = "system";
    public static final String LANGUAGE_PORTUGUESE = "pt";
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_SPANISH = "es";
    public static final String LANGUAGE_GERMAN = "de";
    public static final String LANGUAGE_FRENCH = "fr";
    private static final String STAMP = "@LocaleHelper";
    private static final String PREFS_NAME = "WalletlyPrefs";
    private static final String PREF_LANGUAGE = "language";

    /**
     * Construtor privado para prevenir instanciação desta classe utilitária
     */
    private LocaleHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Aplica o idioma salvo nas preferências
     *
     * @param context Contexto da aplicação
     * @return Context com o idioma aplicado
     */
    public static Context setLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(PREF_LANGUAGE, LANGUAGE_SYSTEM);
        Log.d(STAMP, "A aplicar idioma salvo: " + language);
        return updateResources(context, language);
    }

    /**
     * Guarda o idioma selecionado e atualiza o contexto
     *
     * @param context  Contexto da aplicação
     * @param language Código do idioma a aplicar
     */
    public static void setNewLocale(Context context, String language) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_LANGUAGE, language).apply();
        Log.d(STAMP, "Novo idioma guardado: " + language);
    }

    /**
     * Obtém o idioma guardado nas preferências
     *
     * @param context Contexto da aplicação
     * @return Código do idioma salvo
     */
    public static String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(PREF_LANGUAGE, LANGUAGE_SYSTEM);
        Log.d(STAMP, "Idioma salvo obtido: " + language);
        return language;
    }

    /**
     * Determina o locale a ser usado baseado no código de idioma
     *
     * @param language Código do idioma ("system", "pt", "en", etc.)
     * @return Locale correspondente ao código fornecido
     */
    private static Locale getLocaleFromLanguageCode(String language) {
        if (LANGUAGE_SYSTEM.equals(language)) {
            // Usar idioma do sistema
            LocaleList systemLocales = Resources.getSystem().getConfiguration().getLocales();
            Locale systemLocale = systemLocales.isEmpty() ? Locale.getDefault() : systemLocales.get(0);
            Log.d(STAMP, "A usar idioma do sistema: " + systemLocale.getLanguage());
            return systemLocale;
        }

        // Usar idioma selecionado
        Log.d(STAMP, "A usar idioma selecionado: " + language);
        return Locale.forLanguageTag(language);
    }

    /**
     * Atualiza os recursos da aplicação com o idioma especificado.
     *
     * @param context  contexto da aplicação
     * @param language código do idioma a aplicar
     * @return Context atualizado
     */
    private static Context updateResources(Context context, String language) {
        Locale locale = getLocaleFromLanguageCode(language);


        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());

        config.setLocale(locale);
        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        config.setLocales(localeList);

        // Criar novo contexto com configuração atualizada
        context = context.createConfigurationContext(config);

        Log.d(STAMP, "Recursos atualizados para o idioma: " + locale.getLanguage());
        return context;
    }
}
