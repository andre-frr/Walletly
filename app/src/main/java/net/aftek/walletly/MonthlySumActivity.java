package net.aftek.walletly;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;

import net.aftek.walletly.database.AppDatabase;
import net.aftek.walletly.database.Movimento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity de resumo mensal - exibe receitas, despesas e gráfico do mês atual
 */
public class MonthlySumActivity extends AppCompatActivity {

    public final static String STAMP = "@MonthlySumActivity";

    // Membros de Dados
    TextView mTvReceitas, mTvDespesas;
    LineChart mChartMensal;
    RecyclerView mRvMovimentos;
    ImageButton mIbVoltar;
    Utils mUtils;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;
    MovimentoAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        setContentView(R.layout.activity_monthly_sum);

        init();
    }

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    /**
     * Inicializa os componentes da activity
     */
    void init() {

        // Associações de views
        mTvReceitas = findViewById(R.id.idTvReceitas);
        mTvDespesas = findViewById(R.id.idTvDespesas);
        mChartMensal = findViewById(R.id.idChartMensal);
        mRvMovimentos = findViewById(R.id.idRvMovimentos);
        mIbVoltar = findViewById(R.id.idIbBack);
        mUtils = new Utils(this);

        // Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        // Setup RecyclerView
        mAdapter = new MovimentoAdapter();
        mUtils.setupRecyclerView(mRvMovimentos, mAdapter);

        // Carregar dados do mês
        loadMonthlySummary();

        // Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão voltar clicado");
            mUtils.navigateToMain();
        });
    }

    /**
     * Carrega o resumo mensal (receitas, despesas, transações e gráfico)
     */
    private void loadMonthlySummary() {
        mExecutorService.execute(() -> {
            // Obter limites do mês atual
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long monthStart = calendar.getTimeInMillis();

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            long monthEnd = calendar.getTimeInMillis();

            // Obter todos os movimentos
            List<Movimento> allMovimentos = mDatabase.movimentoDao().getAll();

            double totalReceitas = 0.0;
            double totalDespesas = 0.0;

            // Filtrar e calcular mês atual
            for (Movimento m : allMovimentos) {
                if (m.getData() >= monthStart && m.getData() <= monthEnd) {
                    if (m.getTipo().equalsIgnoreCase("receita")) {
                        totalReceitas += m.getValor();
                    } else {
                        totalDespesas += m.getValor();
                    }
                }
            }

            double finalReceitas = totalReceitas;
            double finalDespesas = totalDespesas;

            // Filtrar transações do mês atual
            List<Movimento> monthMovimentos = new ArrayList<>();
            for (Movimento m : allMovimentos) {
                if (m.getData() >= monthStart && m.getData() <= monthEnd) {
                    monthMovimentos.add(m);
                }
            }

            runOnUiThread(() -> {
                String receitasText = String.format(Locale.getDefault(), "%.2f €", finalReceitas);
                String despesasText = String.format(Locale.getDefault(), "%.2f €", finalDespesas);

                mTvReceitas.setText(receitasText);
                mTvDespesas.setText(despesasText);

                // Atualizar gráfico com saldos diários
                updateChartWithTransactions(monthMovimentos);

                // Ordenar transações por data (mais recentes primeiro) para o RecyclerView
                monthMovimentos.sort((m1, m2) -> {
                    return Long.compare(m2.getData(), m1.getData()); // DESC
                });

                // Carregar transações do MÊS ATUAL no RecyclerView
                mAdapter.setMovimentos(monthMovimentos);

                Log.d(STAMP, "Total de movimentos: " + allMovimentos.size() + ", Movimentos este mês: " + monthMovimentos.size());

                Log.d(STAMP, "Receitas mensais: " + receitasText + ", Despesas mensais: " + despesasText);
            });
        });
    }

    /**
     * Atualiza o gráfico com os saldos diários do mês
     * Delega a lógica complexa ao ChartHelper (KISS)
     *
     * @param movimentos Lista de movimentos do mês atual
     */
    private void updateChartWithTransactions(List<Movimento> movimentos) {
        ChartHelper.updateMonthlyChart(mChartMensal, movimentos, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(STAMP, "onResume - recarregando dados");
        // Recarregar dados ao voltar para esta activity
        loadMonthlySummary();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExecutorService != null && !mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
    }
}