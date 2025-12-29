package net.aftek.walletly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import net.aftek.walletly.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity de histórico - exibe todas as transações
 */
public class HistoryActivity extends AppCompatActivity {

    public static final String STAMP = "@HistoryActivity";

    // Membros de Dados
    ImageButton mIbVoltar;
    RecyclerView mRvMovimentos;
    Utils mUtils;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;
    MovimentoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        setContentView(R.layout.activity_history);

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
        mIbVoltar = findViewById(R.id.idIbBack);
        mRvMovimentos = findViewById(R.id.idRvHistory);
        mUtils = new Utils(this);

        // Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        // Setup RecyclerView usando Utils
        mAdapter = new MovimentoAdapter();
        mUtils.setupRecyclerView(mRvMovimentos, mAdapter);

        // Configurar listener para long click (editar transação)
        mAdapter.setOnItemLongClickListener(movimento -> {
            Log.d(STAMP, "Long click na transação ID: " + movimento.getId());
            Intent intent = new Intent(HistoryActivity.this, EditTransactionActivity.class);
            intent.putExtra(EditTransactionActivity.EXTRA_TRANSACTION_ID, movimento.getId());
            startActivity(intent);
        });

        // Carregar todas as transações usando Utils
        mUtils.loadAllTransactions(mAdapter, mDatabase, mExecutorService);

        // Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão voltar clicado");
            mUtils.navigateToMain();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarregar transações quando voltar da tela de edição
        mUtils.loadAllTransactions(mAdapter, mDatabase, mExecutorService);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExecutorService != null && !mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
    }
}