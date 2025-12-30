package net.aftek.walletly;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Classe utilitária com métodos reutilizáveis para todas as activities
 * Segue os princípios DRY (Don't Repeat Yourself) e KISS (Keep It Simple, Stupid)
 */
public class Utils {

    public static final String STAMP = "@Utils";
    public static final String TYPE_RECEITA = "receita";
    public static final String TYPE_DESPESA = "despesa";

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
                R.layout.spinner_item,
                items
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
            String message = tipo.equals(TYPE_RECEITA)
                    ? mA.getString(R.string.str_toast_insert_income_value)
                    : mA.getString(R.string.str_toast_insert_expense_value);
            showToast(message);
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                showToast(mA.getString(R.string.str_toast_value_greater_zero));
                return;
            }
        } catch (NumberFormatException e) {
            showToast(mA.getString(R.string.str_toast_invalid_value));
            return;
        }

        // Validação da descrição
        String descricao = etDescricao.getText().toString().trim();
        if (descricao.isEmpty()) {
            showToast(mA.getString(R.string.str_toast_insert_description));
            return;
        }

        // Validação da categoria
        if (spnCategoria.getSelectedItemPosition() == 0) {
            showToast(mA.getString(R.string.str_toast_select_category));
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
                    String mensagem = tipo.equals(TYPE_RECEITA)
                            ? mA.getString(R.string.str_toast_income_saved)
                            : mA.getString(R.string.str_toast_expense_saved);
                    showToast(mensagem);
                    Log.d(STAMP, "Navegando de volta para TransactionHub");
                    Intent intent = new Intent(mA, TransactionHubActivity.class);
                    mA.startActivity(intent);
                    mA.finish();
                });
            } catch (Exception e) {
                Log.e(STAMP, "Erro ao inserir movimento na BD: " + e.getMessage());
                mA.runOnUiThread(() -> showToast(mA.getString(R.string.str_toast_save_error)));
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
                if (TYPE_RECEITA.equalsIgnoreCase(m.getTipo())) {
                    balance += m.getValor();
                } else if (TYPE_DESPESA.equalsIgnoreCase(m.getTipo())) {
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
     * Navega de volta para o TransactionHub
     */
    public void navigateToTransactionHub() {
        Log.d(STAMP, "Navegando para TransactionHub");
        Intent intent = new Intent(mA, TransactionHubActivity.class);
        mA.startActivity(intent);
        mA.finish();
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
     * Configura a barra de navegação inferior
     * Gerencia a navegação entre as 3 activities principais: Home, In & Out, Definições
     *
     * @param selectedItemId ID do item atualmente selecionado na navegação
     */
    public void setupBottomNavigation(int selectedItemId) {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                mA.findViewById(R.id.idBottomNavBar);

        if (bottomNav == null) {
            Log.w(STAMP, "BottomNavigationView não encontrada");
            return;
        }

        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home && !(mA instanceof MainActivity)) {
                Log.d(STAMP, "Navegação: Home");
                Intent intent = new Intent(mA, MainActivity.class);
                mA.startActivity(intent);
                mA.finish();
                return true;
            } else if (itemId == R.id.nav_transactions && !(mA instanceof TransactionHubActivity)) {
                Log.d(STAMP, "Navegação: In & Out");
                Intent intent = new Intent(mA, TransactionHubActivity.class);
                mA.startActivity(intent);
                mA.finish();
                return true;
            } else if (itemId == R.id.nav_settings && !(mA instanceof SettingsActivity)) {
                Log.d(STAMP, "Navegação: Definições");
                Intent intent = new Intent(mA, SettingsActivity.class);
                mA.startActivity(intent);
                mA.finish();
                return true;
            }

            return false;
        });
    }

    /**
     * Exporta todas as transações para um arquivo JSON
     *
     * @param database        Instância da base de dados
     * @param executorService Executor para background thread
     */
    public void exportTransactions(AppDatabase database, ExecutorService executorService) {
        executorService.execute(() -> {
            try {
                List<Movimento> allMovimentos = database.movimentoDao().getAll();

                // Converter para JSON
                org.json.JSONArray jsonArray = new org.json.JSONArray();
                for (Movimento m : allMovimentos) {
                    org.json.JSONObject obj = new org.json.JSONObject();
                    obj.put("tipo", m.getTipo());
                    obj.put("valor", m.getValor());
                    obj.put("descricao", m.getDescricao());
                    obj.put("data", m.getData());
                    obj.put("categoria", m.getCategoria());
                    jsonArray.put(obj);
                }

                String filename = "walletly_backup_" + System.currentTimeMillis() + ".json";
                String jsonContent = jsonArray.toString(2);

                // Save to Downloads folder using MediaStore API
                // Android 13+ (API 33+) - No permissions needed
                // Android 10-12 (API 29-32) - Requires WRITE_EXTERNAL_STORAGE permission
                // The permission check is done in SettingsActivity before calling this method
                saveToDownloadsModern(filename, jsonContent, allMovimentos.size());

            } catch (Exception e) {
                Log.e(STAMP, "Erro ao exportar dados: " + e.getMessage());
                mA.runOnUiThread(() -> showToast(mA.getString(R.string.str_toast_export_error)));
            }
        });
    }

    /**
     * Save file to Downloads folder using MediaStore (Android 10+)
     */
    private void saveToDownloadsModern(String filename, String content, int count) {
        try {
            ContentResolver resolver = mA.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/json");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                    if (outputStream != null) {
                        outputStream.write(content.getBytes());

                        mA.runOnUiThread(() -> {
                            showToast(mA.getString(R.string.str_toast_export_success) + ": " + count + " " + mA.getString(R.string.str_toast_transactions));
                            Log.d(STAMP, "Dados exportados para Downloads: " + filename);
                        });
                    }
                }
            }
        } catch (Exception e) {
            Log.e(STAMP, "Erro ao exportar dados (modern): " + e.getMessage());
            mA.runOnUiThread(() -> showToast(mA.getString(R.string.str_toast_export_error)));
        }
    }

    /**
     * Importa transações de um arquivo JSON
     *
     * @param fileUri         URI do arquivo a importar
     * @param database        Instância da base de dados
     * @param executorService Executor para background thread
     */
    public void importTransactions(android.net.Uri fileUri, AppDatabase database, ExecutorService executorService) {
        executorService.execute(() -> {
            try {
                // Ler arquivo
                java.io.InputStream inputStream = mA.getContentResolver().openInputStream(fileUri);
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                reader.close();

                // Parse JSON
                org.json.JSONArray jsonArray = new org.json.JSONArray(jsonString.toString());
                int importedCount = 0;

                for (int i = 0; i < jsonArray.length(); i++) {
                    org.json.JSONObject obj = jsonArray.getJSONObject(i);
                    Movimento movimento = new Movimento(
                            obj.getString("tipo"),
                            obj.getDouble("valor"),
                            obj.getString("descricao"),
                            obj.getLong("data"),
                            obj.getString("categoria")
                    );
                    database.movimentoDao().insert(movimento);
                    importedCount++;
                }

                int finalCount = importedCount;
                mA.runOnUiThread(() -> {
                    showToast(mA.getString(R.string.str_toast_import_success) + ": " + finalCount + " " + mA.getString(R.string.str_toast_transactions));
                    Log.d(STAMP, "Importados " + finalCount + " movimentos");
                });

            } catch (Exception e) {
                Log.e(STAMP, "Erro ao importar dados: " + e.getMessage());
                mA.runOnUiThread(() -> showToast(mA.getString(R.string.str_toast_import_error)));
            }
        });
    }
}