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
 * Activity para adicionar receitas
 */
public class AddIncomeActivity extends AppCompatActivity {

    public final static String STAMP = "@AddIncomeActivity";

    // Membros de dados
    ImageButton mIbVoltar;
    EditText mEtValorReceita, mEtDescReceita;
    Spinner mSpnCategorias;
    Button mBtnGuardar;
    Utils mUtils;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_income);
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
        mIbVoltar = findViewById(R.id.idIbBack1);
        mEtValorReceita = findViewById(R.id.idEtValorReceita);
        mEtDescReceita = findViewById(R.id.idEtDescReceita);
        mSpnCategorias = findViewById(R.id.idSpnCategorias1);
        mBtnGuardar = findViewById(R.id.idBtnGuardar1);
        mUtils = new Utils(this);

        // Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        // Popular spinner com categorias
        incomeCategories();

        // Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão voltar clicado");
            mUtils.navigateToMain();
        });

        mBtnGuardar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão Guardar clicado");
            saveIncome();
        });
    }

    /**
     * Popular spinner com categorias de receita
     */
    private void incomeCategories() {
        List<String> categorias = new ArrayList<>();
        categorias.add(getString(R.string.str_cat_select));
        categorias.add(getString(R.string.str_cat_salario));
        categorias.add(getString(R.string.str_cat_freelance));
        categorias.add(getString(R.string.str_cat_investimentos));
        categorias.add(getString(R.string.str_cat_rendas));
        categorias.add(getString(R.string.str_cat_vendas));
        categorias.add(getString(R.string.str_cat_reembolsos));
        categorias.add(getString(R.string.str_cat_presentes));
        categorias.add(getString(R.string.str_cat_outras));

        mUtils.populateSpinner(mSpnCategorias, categorias);
    }

    /**
     * Guarda a receita na base de dados
     */
    private void saveIncome() {
        mUtils.saveMovimento(
                mEtValorReceita,
                mEtDescReceita,
                mSpnCategorias,
                "receita",
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