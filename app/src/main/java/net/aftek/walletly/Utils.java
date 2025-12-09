package net.aftek.walletly;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class Utils {

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
}
