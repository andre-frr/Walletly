package net.aftek.walletly;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import net.aftek.walletly.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity para adicionar despesas
 */
public class AddExpenseActivity extends AppCompatActivity {

    public final static String STAMP = "@AddExpenseActivity";

    // Membros de dados
    ImageButton mIbVoltar;
    EditText mEtValorDespesa, mEtDescDespesa;
    Spinner mSpnCategorias;
    Button mBtnGuardar;
    Utils mUtils;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        setContentView(R.layout.activity_add_expense);

        init();
    }

    /**
     * Inicializa os componentes da activity
     */
    void init() {

        // Associações de views
        mIbVoltar = findViewById(R.id.idIbBack);
        mEtValorDespesa = findViewById(R.id.idEtValorDespesa);
        mEtDescDespesa = findViewById(R.id.idEtDescDespesa);
        mSpnCategorias = findViewById(R.id.idSpnCategorias);
        mBtnGuardar = findViewById(R.id.idBtnGuardar);
        mUtils = new Utils(this);

        // Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        // Popular spinner com categorias
        expenseCategories();

        // Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão voltar clicado - navegando para TransactionHub");
            mUtils.navigateToTransactionHub();
        });

        mBtnGuardar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão Guardar clicado");
            saveExpense();
        });
    }

    /**
     * Popular spinner com categorias de despesa
     */
    private void expenseCategories() {
        List<String> categorias = new ArrayList<>();
        categorias.add(getString(R.string.str_cat_select));
        categorias.add(getString(R.string.str_cat_despesas_gerais));
        categorias.add(getString(R.string.str_cat_saude));
        categorias.add(getString(R.string.str_cat_educacao));
        categorias.add(getString(R.string.str_cat_habitacao));
        categorias.add(getString(R.string.str_cat_lares));
        categorias.add(getString(R.string.str_cat_reparacao_veiculos));
        categorias.add(getString(R.string.str_cat_restauracao));
        categorias.add(getString(R.string.str_cat_cabeleireiros));
        categorias.add(getString(R.string.str_cat_veterinarias));
        categorias.add(getString(R.string.str_cat_passes));
        categorias.add(getString(R.string.str_cat_ginasios));
        categorias.add(getString(R.string.str_cat_jornais));
        categorias.add(getString(R.string.str_cat_outras));

        mUtils.populateSpinner(mSpnCategorias, categorias);
    }

    /**
     * Guarda a despesa na base de dados
     */
    private void saveExpense() {
        mUtils.saveMovimento(
                mEtValorDespesa,
                mEtDescDespesa,
                mSpnCategorias,
                "despesa",
                mDatabase,
                mExecutorService
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExecutorService != null && !mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
    }
}