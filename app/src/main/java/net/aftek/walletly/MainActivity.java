package net.aftek.walletly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import net.aftek.walletly.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity principal - exibe saldo total e movimentos recentes
 */
public class MainActivity extends AppCompatActivity {

    public static final String STAMP = "@MainActivity";

    // Membros de Dados
    MaterialCardView mCardSaldo;
    MaterialCardView mCardMovimentos;
    TextView mTvEuros;
    RecyclerView mRvMovRecentes;
    Utils mUtils;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;
    MovimentoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        setContentView(R.layout.activity_main);

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

        // Associações de views
        mCardSaldo = findViewById(R.id.idCardSaldo);
        mCardMovimentos = findViewById(R.id.idCardMovimentos);
        mTvEuros = findViewById(R.id.idTvEuros);
        mRvMovRecentes = findViewById(R.id.idRvMovRecentes);
        mUtils = new Utils(this);

        // Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        // Setup RecyclerView
        mAdapter = new MovimentoAdapter();
        mUtils.setupRecyclerView(mRvMovRecentes, mAdapter);

        // Carregar dados
        mUtils.loadBalance(mTvEuros, mDatabase, mExecutorService);
        mUtils.loadRecentTransactions(mAdapter, mDatabase, mExecutorService, 10);

        // Comportamentos
        mCardSaldo.setOnClickListener(v -> {
            Log.d(STAMP, "Navegando para resumo mensal");
            Intent intent = new Intent(MainActivity.this, MonthlySumActivity.class);
            startActivity(intent);
        });

        mCardMovimentos.setOnClickListener(v -> {
            Log.d(STAMP, "Navegando para histórico completo");
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Configurar barra de navegação
        mUtils.setupBottomNavigation(R.id.nav_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(STAMP, "onResume - recarregando dados");
        // Recarregar dados ao voltar para esta activity
        mUtils.loadBalance(mTvEuros, mDatabase, mExecutorService);
        mUtils.loadRecentTransactions(mAdapter, mDatabase, mExecutorService, 10);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExecutorService != null && !mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
    }
}