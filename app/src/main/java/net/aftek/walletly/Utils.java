package net.aftek.walletly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.aftek.walletly.database.AppDatabase;
import net.aftek.walletly.database.Movimento;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class Utils {

    public final static String STAMP = "@Utils";
    Activity mA;

    public Utils(Activity pA) {
        this.mA = pA;
    }

    /**
     * Toast genérico
     *
     * @param context activity em questão
     * @param message mensagem a ser mostrada
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Popular um spinner
     *
     * @param context activity em questão
     * @param spinner spinner a ser populado
     * @param items   lista de itens a serem mostrados
     */
    public static void populateSpinner(Context context, @NonNull Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
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
     * Método genérico para salvar um movimento (receita ou despesa)
     *
     * @param activity        activity atual
     * @param etValor         EditText do valor
     * @param etDescricao     EditText da descrição
     * @param spnCategoria    Spinner da categoria
     * @param tipo            "receita" ou "despesa"
     * @param database        instância do banco de dados
     * @param executorService executor para background thread
     */
    public static void saveMovimento(
            Activity activity,
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
            showToast(activity, "Por favor, insira o valor " + (tipo.equals("receita") ? "da receita" : "da despesa"));
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                showToast(activity, "O valor deve ser maior que zero");
                return;
            }
        } catch (NumberFormatException e) {
            showToast(activity, "Valor inválido");
            return;
        }

        // Validação da descrição
        String descricao = etDescricao.getText().toString().trim();
        if (descricao.isEmpty()) {
            showToast(activity, "Por favor, insira uma descrição");
            return;
        }

        // Validação da categoria
        if (spnCategoria.getSelectedItemPosition() == 0) {
            showToast(activity, "Por favor, selecione uma categoria");
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
                activity.runOnUiThread(() -> {
                    String mensagem = tipo.equals("receita") ? "Receita guardada com sucesso!" : "Despesa guardada com sucesso!";
                    showToast(activity, mensagem);
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                });
            } catch (Exception e) {
                Log.e(STAMP, "Erro ao inserir movimento na BD: " + e.getMessage());
                activity.runOnUiThread(() -> showToast(activity, "Erro ao guardar"));
            }
        });
    }

}
