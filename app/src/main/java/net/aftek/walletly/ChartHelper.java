package net.aftek.walletly;

import android.content.Context;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import net.aftek.walletly.database.Movimento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

/**
 * Helper para configuração e atualização de gráficos
 * Extrai lógica complexa seguindo o princípio KISS - cada classe tem uma responsabilidade
 * Segue o princípio DRY - centraliza a lógica de gráficos
 */
public class ChartHelper {

    private static final String STAMP = "@ChartHelper";

    /**
     * Construtor privado para prevenir instanciação desta classe utilitária
     */
    private ChartHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Atualiza o gráfico com os saldos diários do mês
     * Calcula o saldo acumulado dia a dia e exibe no gráfico de linha
     *
     * @param chart      Gráfico a atualizar
     * @param movimentos Lista de movimentos do mês
     * @param context    Contexto para aceder aos recursos
     */
    public static void updateMonthlyChart(LineChart chart, List<Movimento> movimentos, Context context) {
        Log.d(STAMP, "Atualizando gráfico com " + movimentos.size() + " movimentos do mês");

        // Obter entradas do gráfico calculando saldos diários
        List<Entry> entries = calculateDailyBalances(movimentos);

        // Criar e estilizar dataset
        LineDataSet dataSet = createStyledDataSet(entries, context);

        // Configurar e atualizar gráfico
        configureChart(chart, new LineData(dataSet), context);

        Log.d(STAMP, "Gráfico atualizado com " + entries.size() + " pontos de dados");
    }

    /**
     * Calcula os saldos diários a partir dos movimentos
     * Processa todas as transações e calcula o saldo acumulado para cada dia
     *
     * @param movimentos Lista de movimentos a processar
     * @return Lista de entradas para o gráfico
     */
    private static List<Entry> calculateDailyBalances(List<Movimento> movimentos) {
        List<Entry> entries = new ArrayList<>();

        // Ordenar transações por data (mais antigas primeiro)
        movimentos.sort(Comparator.comparingLong(Movimento::getData));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Array para guardar saldo de cada dia (índice 0 não usado, 1-31 para dias)
        double[] dailyBalances = new double[maxDay + 1];
        double runningBalance = 0.0;

        // Processar todas as transações e calcular saldos diários
        for (Movimento m : movimentos) {
            calendar.setTimeInMillis(m.getData());
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Atualizar saldo corrente
            if (m.getTipo().equalsIgnoreCase(Utils.TYPE_RECEITA)) {
                runningBalance += m.getValor();
            } else if (m.getTipo().equalsIgnoreCase(Utils.TYPE_DESPESA)) {
                runningBalance -= m.getValor();
            }

            // Guardar saldo para este dia
            dailyBalances[day] = runningBalance;
            Log.d(STAMP, "Dia " + day + ": " + m.getTipo() + " " + m.getValor() + "€ → Saldo: " + runningBalance + "€");
        }

        // Propagar saldos: se um dia não tem transações, manter o saldo do dia anterior
        double lastBalance = 0.0;
        for (int day = 1; day <= maxDay; day++) {
            if (dailyBalances[day] == 0.0 && runningBalance != 0.0) {
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

        return entries;
    }

    /**
     * Cria um dataset estilizado para o gráfico
     * Define cores, espessuras de linha e preenchimento
     *
     * @param entries Entradas de dados
     * @param context Contexto para aceder aos recursos de cores
     * @return Dataset estilizado
     */
    private static LineDataSet createStyledDataSet(List<Entry> entries, Context context) {
        LineDataSet dataSet = new LineDataSet(entries, context.getString(R.string.str_dataset_label));

        // Estilizar a linha
        dataSet.setColor(context.getColor(R.color.text_primary));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(context.getColor(R.color.text_primary));
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawValues(false); // Esconder valores nos pontos para visual mais limpo
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(context.getColor(R.color.text_secondary));
        dataSet.setFillAlpha(50);

        return dataSet;
    }

    /**
     * Configura o gráfico com os dados e estilos
     * Define interatividade, eixos e animações
     *
     * @param chart    Gráfico a configurar
     * @param lineData Dados a exibir
     * @param context  Contexto para aceder aos recursos
     */
    private static void configureChart(LineChart chart, LineData lineData, Context context) {
        // Definir dados
        chart.setData(lineData);

        // Configurações gerais
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        // Configurar eixo X (dias do mês)
        XAxis xAxis = chart.getXAxis();
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
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setGridColor(context.getColor(R.color.text_secondary));
        chart.getAxisLeft().setGridLineWidth(0.5f);
        chart.getAxisRight().setEnabled(false);

        // Animar e atualizar
        chart.animateX(1000);
        chart.invalidate();
    }
}