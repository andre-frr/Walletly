package net.aftek.walletly;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import net.aftek.walletly.database.AppDatabase;
import net.aftek.walletly.helpers.CategoryManager;
import net.aftek.walletly.helpers.LocaleHelper;
import net.aftek.walletly.helpers.Utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity para adicionar receitas
 */
public class AddIncomeActivity extends AppCompatActivity {

    public static final String STAMP = "@AddIncomeActivity";

    // Membros de Dados
    ImageButton mIbVoltar;
    EditText mEtValorReceita;
    EditText mEtDescReceita;
    Spinner mSpnCategorias;
    Button mBtnGuardar;
    Utils mUtils;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        setContentView(R.layout.activity_add_income);

        init();
    }

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    /**
     * Inicializa os componentes da activity.
     */
    void init() {

        // Associações de views
        mIbVoltar = findViewById(R.id.idIbBack);
        mEtValorReceita = findViewById(R.id.idEtValorReceita);
        mEtDescReceita = findViewById(R.id.idEtDescReceita);
        mSpnCategorias = findViewById(R.id.idSpnCategorias);
        mBtnGuardar = findViewById(R.id.idBtnGuardar);
        mUtils = new Utils(this);

        // Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        incomeCategories();

        // Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão voltar clicado - navegando para TransactionHub");
            mUtils.navigateToTransactionHub();
        });

        mBtnGuardar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão Guardar clicado");
            saveIncome();
        });
    }

    /**
     * Popular spinner com categorias de receita
     * Utiliza a classe CategoryManager
     */
    private void incomeCategories() {
        List<String> categorias = CategoryManager.getIncomeCategories(this);
        mUtils.populateSpinner(mSpnCategorias, categorias);
        Log.d(STAMP, "Categorias de receita carregadas: " + categorias.size() + " itens");
    }

    /**
     * Guarda a receita na base de dados
     */
    private void saveIncome() {
        mUtils.saveMovimento(
                mEtValorReceita,
                mEtDescReceita,
                mSpnCategorias,
                Utils.TYPE_RECEITA,
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
