package net.aftek.walletly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryActivity extends AppCompatActivity {

    public final static String STAMP = "@HistoryActivity";

    // Membros de dados
    ImageButton mIbVoltar;
    RecyclerView mRvMovimentos;
    Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
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

        // Associações
        mIbVoltar = findViewById(R.id.idIbBack4);
        mRvMovimentos = findViewById(R.id.idRvHistory);
        mUtils = new Utils(this);

        //Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(intent);
        });
        mRvMovimentos.setLayoutManager(new LinearLayoutManager(this));
    }
}