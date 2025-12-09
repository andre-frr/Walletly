package net.aftek.walletly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public final static String STAMP = "@MainActivity";

    //Membros de Dados
    MaterialCardView mCardSaldo, mCardMovimentos;
    TextView mTvEuros;
    ListView mLvMovRecentes;
    FloatingActionButton mFABAdicionar;
    Utils mUtils;

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

    void init() {

        //Associações
        mCardSaldo = findViewById(R.id.idCardSaldo);
        mCardMovimentos = findViewById(R.id.idCardMovimentos);
        mTvEuros = findViewById(R.id.idTvEuros);
        mLvMovRecentes = findViewById(R.id.idLvMovRecentes);
        mFABAdicionar = findViewById(R.id.idFABAdicionar);
        mUtils = new Utils(this);

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
            popupMenu.getMenu().add("Receita");
            popupMenu.getMenu().add("Despesa");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Receita")) {
                    Log.d(STAMP, "Selecionado: Adicionar Receita");
                    Intent intent = new Intent(MainActivity.this, AddIncomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals("Despesa")) {
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
}