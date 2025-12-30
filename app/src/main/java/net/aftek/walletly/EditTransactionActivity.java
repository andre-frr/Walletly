package net.aftek.walletly;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.aftek.walletly.database.AppDatabase;
import net.aftek.walletly.database.Movimento;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity para editar transações existentes (receitas ou despesas)
 */
public class EditTransactionActivity extends AppCompatActivity {

    public static final String STAMP = "@EditTransactionActivity";
    public static final String EXTRA_TRANSACTION_ID = "transaction_id";

    // Membros de Dados
    ImageButton mIbVoltar;
    TextView mTvTitle;
    EditText mEtValor;
    EditText mEtDesc;
    Spinner mSpnCategorias;
    Button mBtnGuardar;
    Button mBtnEliminar;
    Utils mUtils;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;

    private Movimento mMovimento;
    private int mTransactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        setContentView(R.layout.activity_edit_transaction);

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
        mIbVoltar = findViewById(R.id.idIbBack);
        mTvTitle = findViewById(R.id.idTvEditTitle);
        mEtValor = findViewById(R.id.idEtValor);
        mEtDesc = findViewById(R.id.idEtDesc);
        mSpnCategorias = findViewById(R.id.idSpnCategorias);
        mBtnGuardar = findViewById(R.id.idBtnGuardar);
        mBtnEliminar = findViewById(R.id.idBtnEliminar);
        mUtils = new Utils(this);

        // Validar ID da transação
        mTransactionId = getIntent().getIntExtra(EXTRA_TRANSACTION_ID, -1);
        if (mTransactionId == -1) {
            Log.e(STAMP, "ID de transação inválido");
            mUtils.showToast(getString(R.string.str_error_loading_transaction));
            finish();
            return;
        }

        // Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        // Carregar dados da transação
        loadTransaction();

        // Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão voltar clicado");
            finish();
        });

        mBtnGuardar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão Guardar clicado");
            updateTransaction();
        });

        mBtnEliminar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão Eliminar clicado");
            deleteTransaction();
        });
    }

    /**
     * Carrega a transação da base de dados
     */
    private void loadTransaction() {
        mExecutorService.execute(() -> {
            List<Movimento> movimentos = mDatabase.movimentoDao().getAll();

            // Encontrar a transação pelo ID
            for (Movimento m : movimentos) {
                if (m.getId() == mTransactionId) {
                    mMovimento = m;
                    break;
                }
            }

            runOnUiThread(() -> {
                if (mMovimento != null) {
                    populateFields();
                } else {
                    Log.e(STAMP, "Transação não encontrada");
                    mUtils.showToast(getString(R.string.str_error_loading_transaction));
                    finish();
                }
            });
        });
    }

    /**
     * Preenche os campos com os dados da transação
     */
    private void populateFields() {
        // Definir título baseado no tipo
        if (Utils.TYPE_RECEITA.equalsIgnoreCase(mMovimento.getTipo())) {
            mTvTitle.setText(R.string.str_edit_income);
            List<String> categorias = CategoryManager.getIncomeCategories(this);
            mUtils.populateSpinner(mSpnCategorias, categorias);
            // Selecionar categoria atual
            selectCategory(categorias, mMovimento.getCategoria());
        } else {
            mTvTitle.setText(R.string.str_edit_expense);
            List<String> categorias = CategoryManager.getExpenseCategories(this);
            mUtils.populateSpinner(mSpnCategorias, categorias);
            // Selecionar categoria atual
            selectCategory(categorias, mMovimento.getCategoria());
        }

        // Preencher valor (sem símbolo de moeda)
        mEtValor.setText(String.valueOf(mMovimento.getValor()));

        // Preencher descrição
        mEtDesc.setText(mMovimento.getDescricao());

        Log.d(STAMP, "Campos preenchidos com dados da transação ID: " + mTransactionId);
    }

    /**
     * Seleciona a categoria no spinner
     */
    private void selectCategory(List<String> categorias, String categoria) {
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).equals(categoria)) {
                mSpnCategorias.setSelection(i);
                break;
            }
        }
    }

    /**
     * Atualiza a transação na base de dados
     */
    private void updateTransaction() {
        // Validar entrada
        String valorStr = mEtValor.getText().toString().trim();
        String descricao = mEtDesc.getText().toString().trim();

        if (valorStr.isEmpty()) {
            mUtils.showToast(getString(R.string.str_enter_amount));
            return;
        }

        if (descricao.isEmpty()) {
            mUtils.showToast(getString(R.string.str_enter_description));
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                mUtils.showToast(getString(R.string.str_amount_must_be_positive));
                return;
            }
        } catch (NumberFormatException e) {
            mUtils.showToast(getString(R.string.str_invalid_amount));
            return;
        }

        String categoria = mSpnCategorias.getSelectedItem().toString();

        // Atualizar objeto
        mMovimento.setValor(valor);
        mMovimento.setDescricao(descricao);
        mMovimento.setCategoria(categoria);

        // Guardar na base de dados
        mExecutorService.execute(() -> {
            mDatabase.movimentoDao().update(mMovimento);
            runOnUiThread(() -> {
                mUtils.showToast(getString(R.string.str_transaction_updated));
                finish();
            });
        });

        Log.d(STAMP, "Transação atualizada: " + mTransactionId);
    }

    /**
     * Elimina a transação da base de dados
     */
    private void deleteTransaction() {
        // Confirmar eliminação
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.str_delete_transaction)
                .setMessage(R.string.str_delete_transaction_confirm)
                .setPositiveButton(R.string.str_btn_delete, (dialog, which) -> {
                    mExecutorService.execute(() -> {
                        mDatabase.movimentoDao().delete(mMovimento);
                        runOnUiThread(() -> {
                            mUtils.showToast(getString(R.string.str_transaction_deleted));
                            finish();
                        });
                    });
                    Log.d(STAMP, "Transação eliminada: " + mTransactionId);
                })
                .setNegativeButton(R.string.str_cancel, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExecutorService != null && !mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
    }
}
