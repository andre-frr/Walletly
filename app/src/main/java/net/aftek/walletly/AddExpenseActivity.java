package net.aftek.walletly;

import android.content.Intent;
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

public class AddExpenseActivity extends AppCompatActivity {

    public final static String STAMP = "@AddExpenseActivity";

    //Membros de dados
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
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_expense);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */

        init();
    }

    void init() {

        //Associações
        mIbVoltar = findViewById(R.id.idIbBack2);
        mEtValorDespesa = findViewById(R.id.idEtValorDespesa);
        mEtDescDespesa = findViewById(R.id.idEtDescDespesa);
        mSpnCategorias = findViewById(R.id.idSpnCategorias2);
        mBtnGuardar = findViewById(R.id.idBtnGuardar2);
        mUtils = new Utils(this);

        //Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        //Helpers
        expenseCategories();

        //Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
            startActivity(intent);
        });

        mBtnGuardar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão Guardar clicado");
            saveExpense();
        });
    }

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