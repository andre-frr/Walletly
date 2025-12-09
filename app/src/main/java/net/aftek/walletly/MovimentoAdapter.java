package net.aftek.walletly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.aftek.walletly.database.Movimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para RecyclerView de movimentos (transações)
 * Exibe cada transação usando o layout history_item.xml
 */
public class MovimentoAdapter extends RecyclerView.Adapter<MovimentoAdapter.MovimentoViewHolder> {

    private List<Movimento> mMovimentos;

    public MovimentoAdapter() {
        this.mMovimentos = new ArrayList<>();
    }

    @NonNull
    @Override
    public MovimentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new MovimentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimentoViewHolder holder, int position) {
        Movimento movimento = mMovimentos.get(position);
        holder.bind(movimento);
    }

    @Override
    public int getItemCount() {
        return mMovimentos.size();
    }

    /**
     * Atualiza a lista de movimentos e notifica o adapter
     *
     * @param movimentos Nova lista de movimentos
     */
    public void setMovimentos(List<Movimento> movimentos) {
        this.mMovimentos = movimentos != null ? movimentos : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder para cada item de movimento na lista
     */
    public class MovimentoViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTvDesc;
        private final TextView mTvValor;
        private final TextView mTvDate;

        public MovimentoViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvDesc = itemView.findViewById(R.id.idTvHistoryDesc);
            mTvValor = itemView.findViewById(R.id.idTvValorMov);
            mTvDate = itemView.findViewById(R.id.idTvHistoryDate);
        }

        /**
         * Preenche os dados do movimento no layout
         *
         * @param movimento Movimento a ser exibido
         */
        public void bind(Movimento movimento) {
            // Definir descrição
            mTvDesc.setText(movimento.getDescricao());

            // Definir valor com cor baseada no tipo
            String valorFormatado = String.format(Locale.getDefault(), "%.2f €", movimento.getValor());
            if (movimento.getTipo().equalsIgnoreCase("receita")) {
                mTvValor.setText("+ " + valorFormatado);
                mTvValor.setTextColor(itemView.getContext().getColor(R.color.income_green));
            } else {
                mTvValor.setText("- " + valorFormatado);
                mTvValor.setTextColor(itemView.getContext().getColor(R.color.expense_red));
            }

            // Definir data formatada
            String dataFormatada = Utils.formatTimestamp(movimento.getData());
            mTvDate.setText(dataFormatada);
        }
    }
}

