package net.aftek.walletly;

import android.content.Context;
import android.widget.Toast;

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

}
