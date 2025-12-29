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

    public static final String STAMP = "@MonthlySumActivity";

    // Membros de Dados
    TextView mTvReceitas;
    TextView mTvDespesas;
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
            long[] monthBounds = getMonthBounds();
            long monthStart = monthBounds[0];
            long monthEnd = monthBounds[1];

            List<Movimento> allMovimentos = mDatabase.movimentoDao().getAll();
            List<Movimento> monthMovimentos = filterMonthMovimentos(allMovimentos, monthStart, monthEnd);
            double[] totals = calculateMonthlyTotals(monthMovimentos);

            runOnUiThread(() -> updateUI(totals[0], totals[1], monthMovimentos, allMovimentos.size()));
        });
    }

    /**
     * Obtém os limites do mês atual (início e fim)
     *
     * @return Array com [monthStart, monthEnd]
     */
    private long[] getMonthBounds() {
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

        return new long[]{monthStart, monthEnd};
    }

    /**
     * Filtra movimentos do mês atual
     *
     * @param allMovimentos Todos os movimentos
     * @param monthStart    Início do mês
     * @param monthEnd      Fim do mês
     * @return Lista de movimentos do mês
     */
    private List<Movimento> filterMonthMovimentos(List<Movimento> allMovimentos, long monthStart, long monthEnd) {
        List<Movimento> monthMovimentos = new ArrayList<>();
        for (Movimento m : allMovimentos) {
            if (m.getData() >= monthStart && m.getData() <= monthEnd) {
                monthMovimentos.add(m);
            }
        }
        return monthMovimentos;
    }

    /**
     * Calcula totais de receitas e despesas do mês
     *
     * @param monthMovimentos Movimentos do mês
     * @return Array com [totalReceitas, totalDespesas]
     */
    private double[] calculateMonthlyTotals(List<Movimento> monthMovimentos) {
        double totalReceitas = 0.0;
        double totalDespesas = 0.0;

        for (Movimento m : monthMovimentos) {
            if (m.getTipo().equalsIgnoreCase("receita")) {
                totalReceitas += m.getValor();
            } else {
                totalDespesas += m.getValor();
            }
        }

        return new double[]{totalReceitas, totalDespesas};
    }

    /**
     * Atualiza a UI com os dados mensais
     *
     * @param receitas        Total de receitas
     * @param despesas        Total de despesas
     * @param monthMovimentos Movimentos do mês
     * @param totalMovimentos Total de todos os movimentos
     */
    private void updateUI(double receitas, double despesas, List<Movimento> monthMovimentos, int totalMovimentos) {
        String receitasText = String.format(Locale.getDefault(), "%.2f €", receitas);
        String despesasText = String.format(Locale.getDefault(), "%.2f €", despesas);

        mTvReceitas.setText(receitasText);
        mTvDespesas.setText(despesasText);

        updateChartWithTransactions(monthMovimentos);

        monthMovimentos.sort((m1, m2) -> Long.compare(m2.getData(), m1.getData())); // DESC
        mAdapter.setMovimentos(monthMovimentos);

        Log.d(STAMP, "Total de movimentos: " + totalMovimentos + ", Movimentos este mês: " + monthMovimentos.size());
        Log.d(STAMP, "Receitas mensais: " + receitasText + ", Despesas mensais: " + despesasText);
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