package net.aftek.walletly;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity de definições - configurações da aplicação
 */
public class SettingsActivity extends AppCompatActivity {

    public final static String STAMP = "@SettingsActivity";

    // Membros de Dados
    Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        init();
    }

    /**
     * Inicializa os componentes da activity
     */
    void init() {

        mUtils = new Utils(this);

        // Configurar barra de navegação
        mUtils.setupBottomNavigation(R.id.nav_settings);

        // TODO: Adicionar funcionalidades de definições
        Log.d(STAMP, "Activity de definições inicializada - funcionalidades a serem implementadas");
    }
}