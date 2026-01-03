package net.aftek.walletly;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import net.aftek.walletly.database.AppDatabase;
import net.aftek.walletly.helpers.LocaleHelper;
import net.aftek.walletly.helpers.MovimentoAdapter;
import net.aftek.walletly.helpers.Utils;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity de histórico - mostra todas as transações
 */
public class HistoryActivity extends AppCompatActivity {

    public static final String STAMP = "@HistoryActivity";

    // Membros de Dados
    ImageButton mIbVoltar;
    ImageButton mIbFilter;
    RecyclerView mRvMovimentos;
    Utils mUtils;
    AppDatabase mDatabase;
    ExecutorService mExecutorService;
    MovimentoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(STAMP, "onCreate iniciado");
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_history);

        init();
    }

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    /**
     * Inicializa os componentes da activity.
     */
    void init() {

        // Associações de views
        mIbVoltar = findViewById(R.id.idIbBack);
        mIbFilter = findViewById(R.id.idIbFilter);
        mRvMovimentos = findViewById(R.id.idRvHistory);
        mUtils = new Utils(this);

        // Database e Executor
        mDatabase = AppDatabase.getInstance(this);
        mExecutorService = Executors.newSingleThreadExecutor();

        // Setup RecyclerView usando Utils
        mAdapter = new MovimentoAdapter();
        mUtils.setupRecyclerView(mRvMovimentos, mAdapter);

        // Configurar listener para long click (editar transação)
        mAdapter.setOnItemLongClickListener(movimento -> {
            Log.d(STAMP, "Long click na transação ID: " + movimento.getId());
            Intent intent = new Intent(HistoryActivity.this, EditTransactionActivity.class);
            intent.putExtra(EditTransactionActivity.EXTRA_TRANSACTION_ID, movimento.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Carregar todas as transações usando Utils
        mUtils.loadAllTransactions(mAdapter, mDatabase, mExecutorService);

        // Comportamentos
        mIbVoltar.setOnClickListener(v -> {
            Log.d(STAMP, "Botão voltar clicado");
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        mIbFilter.setOnClickListener(v -> {
            Log.d(STAMP, "Botão filtro clicado");
            showFilterMenu();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarregar transações quando voltar da tela de edição
        mUtils.loadAllTransactions(mAdapter, mDatabase, mExecutorService);
    }

    /**
     * Mostra o menu de filtros
     */
    private void showFilterMenu() {
        PopupMenu popupMenu = new PopupMenu(this, mIbFilter);
        popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_sort_highest) {
                mAdapter.sortByHighestValue();
                Toast.makeText(this, R.string.str_filter_applied, Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.menu_sort_lowest) {
                mAdapter.sortByLowestValue();
                Toast.makeText(this, R.string.str_filter_applied, Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.menu_sort_newest) {
                mAdapter.sortByNewest();
                Toast.makeText(this, R.string.str_filter_applied, Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.menu_sort_oldest) {
                mAdapter.sortByOldest();
                Toast.makeText(this, R.string.str_filter_applied, Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.menu_filter_value_range) {
                showValueRangeDialog();
                return true;
            } else if (itemId == R.id.menu_filter_date_range) {
                showDateRangeDialog();
                return true;
            } else if (itemId == R.id.menu_filter_clear) {
                mAdapter.clearFilters();
                Toast.makeText(this, R.string.str_filter_cleared, Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });

        popupMenu.show();
    }

    /**
     * Mostra o diálogo de filtro por intervalo de valores
     */
    private void showValueRangeDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_value_range, null);
        EditText etMinValue = dialogView.findViewById(R.id.idEtMinValue);
        EditText etMaxValue = dialogView.findViewById(R.id.idEtMaxValue);

        // Create custom title view
        TextView titleView = (TextView) LayoutInflater.from(this).inflate(R.layout.dialog_title_centered, null);
        titleView.setText(R.string.str_filter_value_range_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(titleView)
                .setView(dialogView)
                .setPositiveButton(R.string.str_btn_apply, (dialog, which) -> {
                    String minStr = etMinValue.getText().toString().trim();
                    String maxStr = etMaxValue.getText().toString().trim();

                    if (minStr.isEmpty() || maxStr.isEmpty()) {
                        Toast.makeText(this, R.string.str_toast_invalid_value, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double minValue = Double.parseDouble(minStr);
                        double maxValue = Double.parseDouble(maxStr);

                        if (minValue < 0 || maxValue < 0) {
                            Toast.makeText(this, R.string.str_toast_value_greater_zero, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (minValue > maxValue) {
                            Toast.makeText(this, R.string.str_toast_invalid_value, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAdapter.filterByValueRange(minValue, maxValue);
                        Toast.makeText(this, R.string.str_filter_applied, Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, R.string.str_toast_invalid_value, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.str_cancel, null);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setDimAmount(0.7f);
        }

        dialog.show();
    }

    /**
     * Mostra o diálogo de filtro por intervalo de datas
     */
    private void showDateRangeDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_date_range, null);
        TextView btnStartDate = dialogView.findViewById(R.id.idBtnStartDate);
        TextView btnEndDate = dialogView.findViewById(R.id.idBtnEndDate);

        final Calendar startCalendar = Calendar.getInstance();
        final Calendar endCalendar = Calendar.getInstance();

        final long[] startDate = {0};
        final long[] endDate = {System.currentTimeMillis()};

        btnStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        startCalendar.set(year, month, dayOfMonth, 0, 0, 0);
                        startCalendar.set(Calendar.MILLISECOND, 0);
                        startDate[0] = startCalendar.getTimeInMillis();
                        btnStartDate.setText(Utils.formatTimestamp(startDate[0]));
                    },
                    startCalendar.get(Calendar.YEAR),
                    startCalendar.get(Calendar.MONTH),
                    startCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        btnEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        endCalendar.set(year, month, dayOfMonth, 23, 59, 59);
                        endCalendar.set(Calendar.MILLISECOND, 999);
                        endDate[0] = endCalendar.getTimeInMillis();
                        btnEndDate.setText(Utils.formatTimestamp(endDate[0]));
                    },
                    endCalendar.get(Calendar.YEAR),
                    endCalendar.get(Calendar.MONTH),
                    endCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Create custom title view
        TextView titleView = (TextView) LayoutInflater.from(this).inflate(R.layout.dialog_title_centered, null);
        titleView.setText(R.string.str_filter_date_range_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(titleView)
                .setView(dialogView)
                .setPositiveButton(R.string.str_btn_apply, (dialog, which) -> {

                    if (startDate[0] > endDate[0]) {
                        Toast.makeText(this, R.string.str_toast_invalid_value, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAdapter.filterByDateRange(startDate[0], endDate[0]);
                    Toast.makeText(this, R.string.str_filter_applied, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.str_cancel, null);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setDimAmount(0.7f);
        }

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mExecutorService != null && !mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
    }
}
