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
        categorias.add("Selecione a categoria"); // Placeholder
        categorias.add("Despesas Gerais e Familiares");
        categorias.add("Saúde e Bem-estar");
        categorias.add("Educação");
        categorias.add("Habitação");
        categorias.add("Lares");
        categorias.add("Reparação de veículos");
        categorias.add("Restauração e Alojamento");
        categorias.add("Cabeleireiros");
        categorias.add("Atividades Veterinárias");
        categorias.add("Passes Mensais");
        categorias.add("Ginásios");
        categorias.add("Jornais e Revistas");
        categorias.add("Outras");

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