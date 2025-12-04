package net.aftek.walletly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AddIncomeActivity extends AppCompatActivity {

    public final static String STAMP = "@AddIncomeActivity";

    //Membros de dados
    ImageButton mIbVoltar;
    EditText mEtValorReceita, mEtDescReceita;
    Button mBtnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mBtnGuardar = findViewById(R.id.idBtnGuardar1);

        //Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(AddIncomeActivity.this, MainActivity.class);
            startActivity(intent);

        });
    }
}