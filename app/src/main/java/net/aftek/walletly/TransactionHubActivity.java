package net.aftek.walletly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import net.aftek.walletly.helpers.LocaleHelper;
import net.aftek.walletly.helpers.Utils;

/**
 * Activity hub para adicionar transações - centro de navegação para adicionar receitas ou despesas
 */
public class TransactionHubActivity extends AppCompatActivity {

    public static final String STAMP = "@TransactionHubActivity";

    // Membros de Dados
    Button mBtnAddIncome;
    Button mBtnAddExpense;
    Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_transaction_hub);

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
        mBtnAddIncome = findViewById(R.id.idBtnAddIncome);
        mBtnAddExpense = findViewById(R.id.idBtnAddExpense);
        mUtils = new Utils(this);

        // Comportamentos dos botões
        mBtnAddIncome.setOnClickListener(v -> {
            Log.d(STAMP, "Navegando para adicionar receita");
            Intent intent = new Intent(TransactionHubActivity.this, AddIncomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        mBtnAddExpense.setOnClickListener(v -> {
            Log.d(STAMP, "Navegando para adicionar despesa");
            Intent intent = new Intent(TransactionHubActivity.this, AddExpenseActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Configurar barra de navegação
        mUtils.setupBottomNavigation(R.id.nav_transactions);
    }
}
