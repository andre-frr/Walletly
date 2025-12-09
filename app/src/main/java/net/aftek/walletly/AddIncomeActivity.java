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

public class AddIncomeActivity extends AppCompatActivity {

    public final static String STAMP = "@AddIncomeActivity";

    //Membros de dados
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

    void init() {

        //Associações
        mIbVoltar = findViewById(R.id.idIbBack1);
        mEtValorReceita = findViewById(R.id.idEtValorReceita);
        mEtDescReceita = findViewById(R.id.idEtDescReceita);
        mSpnCategorias = findViewById(R.id.idSpnCategorias1);
        mBtnGuardar = findViewById(R.id.idBtnGuardar1);
        mUtils = new Utils(this);

        //Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        //Helpers
        incomeCategories();

        //Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(AddIncomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        mBtnGuardar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão Guardar clicado");
            saveIncome();
        });
    }

    private void incomeCategories() {
        List<String> categorias = new ArrayList<>();
        categorias.add("Selecione a categoria"); // Placeholder hint
        categorias.add("Salário");
        categorias.add("Freelance");
        categorias.add("Investimentos");
        categorias.add("Rendas");
        categorias.add("Vendas");
        categorias.add("Reembolsos");
        categorias.add("Presentes ou Doações");
        categorias.add("Outras");

        Utils.populateSpinner(this, mSpnCategorias, categorias);
    }

    private void saveIncome() {
        Utils.saveMovimento(
                this,
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