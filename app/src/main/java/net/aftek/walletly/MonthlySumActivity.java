package net.aftek.walletly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;

public class MonthlySumActivity extends AppCompatActivity {

    public final static String STAMP = "@MonthlySumActivity";

    //Membros de Dados
    TextView mTvReceitas, mTvDespesas;
    LineChart mChartMensal;
    ListView mLvMovimentos;
    ImageButton mIbVoltar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_monthly_sum);
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
        mTvReceitas = findViewById(R.id.idTvReceitas);
        mTvDespesas = findViewById(R.id.idTvDespesas);
        mChartMensal = findViewById(R.id.idChartMensal);
        mLvMovimentos = findViewById(R.id.idLvMovimentos);
        mIbVoltar = findViewById(R.id.idIbBack3);

        //Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(MonthlySumActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}