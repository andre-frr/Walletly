package net.aftek.walletly.helpers;

import android.content.Context;

import net.aftek.walletly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestor centralizado de categorias
 */
public class CategoryManager {

    /**
     * Construtor privado para prevenir instanciação desta classe utilitária
     */
    private CategoryManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Obtém a lista de categorias de receitas
     *
     * @param context Contexto para aceder aos recursos de strings
     * @return Lista de categorias de receita
     */
    public static List<String> getIncomeCategories(Context context) {
        List<String> categorias = new ArrayList<>();
        categorias.add(context.getString(R.string.str_cat_select));
        categorias.add(context.getString(R.string.str_cat_salario));
        categorias.add(context.getString(R.string.str_cat_freelance));
        categorias.add(context.getString(R.string.str_cat_investimentos));
        categorias.add(context.getString(R.string.str_cat_rendas));
        categorias.add(context.getString(R.string.str_cat_vendas));
        categorias.add(context.getString(R.string.str_cat_reembolsos));
        categorias.add(context.getString(R.string.str_cat_presentes));
        categorias.add(context.getString(R.string.str_cat_outras));
        return categorias;
    }

    /**
     * Obtém a lista de categorias de despesas.
     *
     * @param context contexto para aceder aos recursos de strings
     * @return lista de categorias de despesa
     */
    public static List<String> getExpenseCategories(Context context) {
        List<String> categorias = new ArrayList<>();
        categorias.add(context.getString(R.string.str_cat_select));
        categorias.add(context.getString(R.string.str_cat_despesas_gerais));
        categorias.add(context.getString(R.string.str_cat_saude));
        categorias.add(context.getString(R.string.str_cat_educacao));
        categorias.add(context.getString(R.string.str_cat_habitacao));
        categorias.add(context.getString(R.string.str_cat_lares));
        categorias.add(context.getString(R.string.str_cat_reparacao_veiculos));
        categorias.add(context.getString(R.string.str_cat_restauracao));
        categorias.add(context.getString(R.string.str_cat_cabeleireiros));
        categorias.add(context.getString(R.string.str_cat_veterinarias));
        categorias.add(context.getString(R.string.str_cat_passes));
        categorias.add(context.getString(R.string.str_cat_ginasios));
        categorias.add(context.getString(R.string.str_cat_jornais));
        categorias.add(context.getString(R.string.str_cat_outras));
        return categorias;
    }
}
