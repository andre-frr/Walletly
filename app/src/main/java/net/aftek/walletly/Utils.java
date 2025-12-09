package net.aftek.walletly;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.aftek.walletly.database.AppDatabase;
import net.aftek.walletly.database.Movimento;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Classe utilitária com métodos reutilizáveis para todas as activities
 * Segue os princípios DRY (Don't Repeat Yourself) e KISS (Keep It Simple, Stupid)
 */
public class Utils {

    public final static String STAMP = "@Utils";
    private final Activity mA;

    /**
     * Construtor
     *
     * @param pA Activity que instancia esta classe
     */
    public Utils(Activity pA) {
        this.mA = pA;
    }

    /**
     * Toast genérico
     *
     * @param message mensagem a ser mostrada
     */
    public void showToast(String message) {
        Toast.makeText(mA, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Popular um spinner
     *
     * @param spinner spinner a ser populado
     * @param items   lista de itens a serem mostrados
     */
    public void populateSpinner(@NonNull Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                mA,
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Formatar o timestamp para uma string legível
     *
     * @param timestamp corresponde à coluna data da tabela Movimento
     * @return a data em string legível
     */
    @androidx.annotation.NonNull
    public static String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }

    /**
     * Método genérico para guardar um movimento (receita ou despesa)
     *
     * @param etValor         EditText do valor
     * @param etDescricao     EditText da descrição
     * @param spnCategoria    Spinner da categoria
     * @param tipo            "receita" ou "despesa"
     * @param database        instância do banco de dados
     * @param executorService executor para background thread
     */
    public void saveMovimento(
            EditText etValor,
            EditText etDescricao,
            Spinner spnCategoria,
            String tipo,
            AppDatabase database,
            ExecutorService executorService
    ) {
        // Validação do valor
        String valorStr = etValor.getText().toString().trim();
        if (valorStr.isEmpty()) {
            showToast("Por favor, insira o valor " + (tipo.equals("receita") ? "da receita" : "da despesa"));
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                showToast("O valor deve ser maior que zero");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Valor inválido");
            return;
        }

        // Validação da descrição
        String descricao = etDescricao.getText().toString().trim();
        if (descricao.isEmpty()) {
            showToast("Por favor, insira uma descrição");
            return;
        }

        // Validação da categoria
        if (spnCategoria.getSelectedItemPosition() == 0) {
            showToast("Por favor, selecione uma categoria");
            return;
        }

        String categoria = spnCategoria.getSelectedItem().toString();
        long data = System.currentTimeMillis();

        // Criar o movimento
        Movimento movimento = new Movimento(tipo, valor, descricao, data, categoria);
        Log.d(STAMP, "A criar movimento: tipo=" + tipo + ", valor=" + valor + ", desc=" + descricao + ", categoria=" + categoria);

        // Salvar na base de dados em background thread
        executorService.execute(() -> {
            try {
                database.movimentoDao().insert(movimento);
                Log.d(STAMP, "Movimento inserido com sucesso na BD");

                // Voltar para a UI thread para mostrar o toast e navegar
                mA.runOnUiThread(() -> {
                    String mensagem = tipo.equals("receita") ? "Receita guardada com sucesso!" : "Despesa guardada com sucesso!";
                    showToast(mensagem);
                    Intent intent = new Intent(mA, MainActivity.class);
                    mA.startActivity(intent);
                    mA.finish();
                });
            } catch (Exception e) {
                Log.e(STAMP, "Erro ao inserir movimento na BD: " + e.getMessage());
                mA.runOnUiThread(() -> showToast("Erro ao guardar"));
            }
        });
    }

    /**
     * Carrega o saldo total (receitas - despesas)
     *
     * @param textView        TextView onde será exibido o saldo
     * @param database        Instância do banco de dados
     * @param executorService Executor para background thread
     */
    public void loadBalance(TextView textView, AppDatabase database, ExecutorService executorService) {
        executorService.execute(() -> {
            List<Movimento> allMovimentos = database.movimentoDao().getAll();
            double balance = 0.0;

            // Calcular saldo: somar receitas, subtrair despesas
            for (Movimento m : allMovimentos) {
                if (m.getTipo().equalsIgnoreCase("receita")) {
                    balance += m.getValor();
                } else {
                    balance -= m.getValor();
                }
            }

            double finalBalance = balance;
            mA.runOnUiThread(() -> {
                String balanceText = String.format(java.util.Locale.getDefault(), "%.2f €", finalBalance);
                textView.setText(balanceText);
                Log.d(STAMP, "Saldo atualizado: " + balanceText);
            });
        });
    }

    /**
     * Carrega transações recentes
     *
     * @param adapter         Adapter do RecyclerView
     * @param database        Instância do banco de dados
     * @param executorService Executor para background thread
     * @param limit           Número de transações a carregar
     */
    public void loadRecentTransactions(MovimentoAdapter adapter, AppDatabase database,
                                       ExecutorService executorService, int limit) {
        executorService.execute(() -> {
            List<Movimento> recentMovimentos = database.movimentoDao().getRecent(limit);

            mA.runOnUiThread(() -> {
                adapter.setMovimentos(recentMovimentos);
                Log.d(STAMP, "Carregados " + recentMovimentos.size() + " movimentos recentes");
            });
        });
    }

    /**
     * Carrega todas as transações
     *
     * @param adapter         Adapter do RecyclerView
     * @param database        Instância da base de dados
     * @param executorService Executor para background thread
     */
    public void loadAllTransactions(MovimentoAdapter adapter, AppDatabase database,
                                    ExecutorService executorService) {
        executorService.execute(() -> {
            List<Movimento> allMovimentos = database.movimentoDao().getAll();

            mA.runOnUiThread(() -> {
                adapter.setMovimentos(allMovimentos);
                Log.d(STAMP, "Carregados " + allMovimentos.size() + " movimentos no histórico");
            });
        });
    }

    /**
     * Navega de volta para a MainActivity
     */
    public void navigateToMain() {
        Intent intent = new Intent(mA, MainActivity.class);
        mA.startActivity(intent);
    }

    /**
     * Configura um RecyclerView com LinearLayoutManager
     *
     * @param recyclerView RecyclerView a ser configurado
     * @param adapter      Adapter a ser associado
     */
    public void setupRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(mA));
        recyclerView.setAdapter(adapter);
        Log.d(STAMP, "RecyclerView configurado");
    }

    /**
     * Calcula o total de receitas e despesas do mês atual
     *
     * @param database        Instância do banco de dados
     * @param executorService Executor para background thread
     * @param tvReceitas      TextView para exibir receitas
     * @param tvDespesas      TextView para exibir despesas
     * @param adapter         Adapter para as transações
     * @param onDataLoaded    Callback executado após carregar dados
     */
    public void loadMonthlySummary(AppDatabase database, ExecutorService executorService,
                                   TextView tvReceitas, TextView tvDespesas,
                                   MovimentoAdapter adapter, Runnable onDataLoaded) {
        executorService.execute(() -> {
            // Obter limites do mês atual
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);
            long monthStart = calendar.getTimeInMillis();

            calendar.set(java.util.Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
            calendar.set(java.util.Calendar.MINUTE, 59);
            calendar.set(java.util.Calendar.SECOND, 59);
            long monthEnd = calendar.getTimeInMillis();

            // Obter todos os movimentos
            List<Movimento> allMovimentos = database.movimentoDao().getAll();

            double totalReceitas = 0.0;
            double totalDespesas = 0.0;

            // Filtrar e calcular mês atual
            List<Movimento> monthMovimentos = new java.util.ArrayList<>();
            for (Movimento m : allMovimentos) {
                if (m.getData() >= monthStart && m.getData() <= monthEnd) {
                    monthMovimentos.add(m);
                    if (m.getTipo().equalsIgnoreCase("receita")) {
                        totalReceitas += m.getValor();
                    } else {
                        totalDespesas += m.getValor();
                    }
                }
            }

            double finalReceitas = totalReceitas;
            double finalDespesas = totalDespesas;

            mA.runOnUiThread(() -> {
                String receitasText = String.format(java.util.Locale.getDefault(),
                        "%.2f €", finalReceitas);
                String despesasText = String.format(java.util.Locale.getDefault(),
                        "%.2f €", finalDespesas);

                tvReceitas.setText(receitasText);
                tvDespesas.setText(despesasText);
                adapter.setMovimentos(monthMovimentos);

                Log.d(STAMP, "Receitas mensais: " + receitasText +
                        ", Despesas mensais: " + despesasText);
                Log.d(STAMP, "Total de movimentos: " + allMovimentos.size() +
                        ", Movimentos este mês: " + monthMovimentos.size());

                // Executar callback se fornecido
                if (onDataLoaded != null) {
                    onDataLoaded.run();
                }
            });
        });
    }

}