package net.aftek.walletly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.aftek.walletly.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity principal - exibe saldo total e movimentos recentes
 */
public class MainActivity extends AppCompatActivity {

    public final static String STAMP = "@MainActivity";

    // Membros de Dados
    MaterialCardView mCardSaldo, mCardMovimentos;
    TextView mTvEuros;
    RecyclerView mRvMovRecentes;
    FloatingActionButton mFABAdicionar;
    Utils mUtils;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;
    MovimentoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */

        init();
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
        mFABAdicionar = findViewById(R.id.idFABAdicionar);
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

        //Comportamentos
        mCardSaldo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MonthlySumActivity.class);
            startActivity(intent);
        });

        mCardMovimentos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        mFABAdicionar.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(
                    MainActivity.this, mFABAdicionar, Gravity.NO_GRAVITY, 0, R.style.NoShadowPopupMenu);
            String receita = getString(R.string.str_menu_receita);
            String despesa = getString(R.string.str_menu_despesa);
            popupMenu.getMenu().add(receita);
            popupMenu.getMenu().add(despesa);

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals(receita)) {
                    Log.d(STAMP, "Selecionado: Adicionar Receita");
                    Intent intent = new Intent(MainActivity.this, AddIncomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals(despesa)) {
                    Log.d(STAMP, "Selecionado: Adicionar Despesa");
                    Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
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