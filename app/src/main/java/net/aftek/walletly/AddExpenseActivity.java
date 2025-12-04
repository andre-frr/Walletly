package net.aftek.walletly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    public final static String STAMP = "@AddExpenseActivity";

    //Membros de dados
    ImageButton mIbVoltar;
    EditText mEtValorDespesa, mEtDescDespesa;
    Spinner mSpnCategorias;
    Button mBtnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mSpnCategorias = findViewById(R.id.idSpnCategorias);
        mBtnGuardar = findViewById(R.id.idBtnGuardar2);

        //Setup Spinner com categorias
        List<String> categorias = new ArrayList<>();
        categorias.add("Selecione a categoria"); // Placeholder hint
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnCategorias.setAdapter(adapter);

        //Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
            startActivity(intent);
        });

        mBtnGuardar.setOnClickListener(v -> {
            if (mSpnCategorias.getSelectedItemPosition() == 0) {
                Utils.showToast(this, "Por favor, selecione uma categoria");
                return;
            }

            String categoria = mSpnCategorias.getSelectedItem().toString();
            // TODO { Save Logic }
        });

    }
}