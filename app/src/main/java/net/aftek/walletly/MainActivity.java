package net.aftek.walletly;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public final static String STAMP = "@MainActivity";

    //Membros de Dados
    MaterialCardView mCardSaldo, mCardMovRecentes;
    TextView mTvEuros;
    ListView mLvMovRecentes;
    FloatingActionButton mFABAdicionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mCardMovRecentes = findViewById(R.id.idCardMovRecentes);
        mTvEuros = findViewById(R.id.idTvEuros);
        mLvMovRecentes = findViewById(R.id.idLvMovRecentes);
        mFABAdicionar = findViewById(R.id.idFABAdicionar);

        //Comportamentos

    }
}