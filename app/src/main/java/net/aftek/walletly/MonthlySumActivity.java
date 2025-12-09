package net.aftek.walletly;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import net.aftek.walletly.database.AppDatabase;
import net.aftek.walletly.database.Movimento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

    /**
     * Inicializa os componentes da activity
     */
    void init() {

        // Associações de views
        mTvReceitas = findViewById(R.id.idTvReceitas);
        mTvDespesas = findViewById(R.id.idTvDespesas);
        mChartMensal = findViewById(R.id.idChartMensal);
        mRvMovimentos = findViewById(R.id.idRvMovimentos);
        mIbVoltar = findViewById(R.id.idIbBack3);
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

                // Carregar transações do MÊS ATUAL no RecyclerView
                mAdapter.setMovimentos(monthMovimentos);

                // Atualizar gráfico com saldos diários
                updateChartWithTransactions(monthMovimentos);

                Log.d(STAMP, "Total de movimentos: " + allMovimentos.size() + ", Movimentos este mês: " + monthMovimentos.size());

                Log.d(STAMP, "Receitas mensais: " + receitasText + ", Despesas mensais: " + despesasText);
            });
        });
    }

    /**
     * Atualiza o gráfico com os saldos diários do mês
     * Usa o padrão da indústria financeira: um ponto por dia com saldo do final do dia
     *
     * @param movimentos Lista de movimentos do mês atual
     */
    private void updateChartWithTransactions(List<Movimento> movimentos) {
        List<Entry> entries = new ArrayList<>();

        Log.d(STAMP, "Atualizando gráfico com " + movimentos.size() + " movimentos do mês");

        // Ordenar transações por data (mais antigas primeiro)
        Collections.sort(movimentos, new Comparator<Movimento>() {
            @Override
            public int compare(Movimento m1, Movimento m2) {
                return Long.compare(m1.getData(), m2.getData());
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Calcular saldo final de cada dia do mês
        double[] dailyBalances = new double[maxDay + 1]; // Índice 0 não usado, 1-31 para dias
        double runningBalance = 0.0;

        // Processar todas as transações e calcular saldos diários
        for (Movimento m : movimentos) {
            calendar.setTimeInMillis(m.getData());
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Atualizar saldo corrente
            if (m.getTipo().equalsIgnoreCase("receita")) {
                runningBalance += m.getValor();
            } else if (m.getTipo().equalsIgnoreCase("despesa")) {
                runningBalance -= m.getValor();
            }

            // Guardar saldo para este dia (será sobrescrito se houver múltiplas transações no mesmo dia)
            dailyBalances[day] = runningBalance;
            Log.d(STAMP, "Dia " + day + ": " + m.getTipo() + " " + m.getValor() + "€ → Saldo: " + runningBalance + "€");
        }

        // Propagar saldos: se um dia não tem transações, manter o saldo do dia anterior
        double lastBalance = 0.0;
        for (int day = 1; day <= maxDay; day++) {
            if (dailyBalances[day] == 0.0 && runningBalance != 0.0) {
                // Sem transações neste dia, usar saldo do dia anterior
                dailyBalances[day] = lastBalance;
            } else {
                lastBalance = dailyBalances[day];
            }
        }

        // Criar entradas do gráfico para cada dia
        for (int day = 1; day <= maxDay; day++) {
            entries.add(new Entry(day, (float) dailyBalances[day]));
        }

        // Se não houver transações, mostrar linha plana em 0
        if (movimentos.isEmpty()) {
            Log.d(STAMP, "Nenhuma transação este mês - mostrando linha plana");
            for (int day = 1; day <= maxDay; day++) {
                entries.add(new Entry(day, 0f));
            }
        }

        // Entradas já estão ordenadas por data de transação, não é necessário ordenar novamente
        Log.d(STAMP, "Total de pontos no gráfico: " + entries.size());

        // Criar dataset
        LineDataSet dataSet = new LineDataSet(entries, "Saldo Diário");

        // Estilizar a linha
        dataSet.setColor(getColor(R.color.text_primary));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(getColor(R.color.text_primary));
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawValues(false); // Esconder valores nos pontos para visual mais limpo
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getColor(R.color.text_secondary));
        dataSet.setFillAlpha(50);

        // Criar LineData
        LineData lineData = new LineData(dataSet);

        // Configurar gráfico
        mChartMensal.setData(lineData);
        mChartMensal.getDescription().setEnabled(false);
        mChartMensal.setTouchEnabled(true);
        mChartMensal.setDragEnabled(true);
        mChartMensal.setScaleEnabled(false);
        mChartMensal.setPinchZoom(false);
        mChartMensal.setDrawGridBackground(false);

        // Configurar eixo X (dias do mês - formato padrão de apps financeiros)
        XAxis xAxis = mChartMensal.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        // Configurar eixo Y (valores de saldo)
        mChartMensal.getAxisLeft().setDrawGridLines(true);
        mChartMensal.getAxisLeft().setGridColor(getColor(R.color.text_secondary));
        mChartMensal.getAxisLeft().setGridLineWidth(0.5f);
        mChartMensal.getAxisRight().setEnabled(false);

        // Animar
        mChartMensal.animateX(1000);
        mChartMensal.invalidate();

        Log.d(STAMP, "Gráfico atualizado com " + entries.size() + " pontos de dados");
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