package net.aftek.walletly;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import net.aftek.walletly.database.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity para adicionar despesas
 */
public class AddExpenseActivity extends AppCompatActivity {

    public final static String STAMP = "@AddExpenseActivity";

    // Membros de Dados
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
     * Utiliza CategoryManager centralizado (DRY)
     */
    private void expenseCategories() {
        List<String> categorias = CategoryManager.getExpenseCategories(this);
        mUtils.populateSpinner(mSpnCategorias, categorias);
        Log.d(STAMP, "Categorias de despesa carregadas: " + categorias.size() + " itens");
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