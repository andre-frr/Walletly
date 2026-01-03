package net.aftek.walletly.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import net.aftek.walletly.R;
import net.aftek.walletly.database.Movimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para RecyclerView de movimentos (transações)
 * Mostra cada transação com layout history_item.xml
 */
public class MovimentoAdapter extends RecyclerView.Adapter<MovimentoAdapter.MovimentoViewHolder> {

    private List<Movimento> mMovimentos;
    private OnItemLongClickListener mLongClickListener;

    public MovimentoAdapter() {
        this.mMovimentos = new ArrayList<>();
    }

    /**
     * Define o listener para long clicks em itens
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mLongClickListener = listener;
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
        holder.bind(movimento, mLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mMovimentos.size();
    }

    /**
     * Atualiza a lista de movimentos exibidos
     * Usa DiffUtil para calcular diferenças e atualizar apenas itens alterados
     * Isto melhora a performance e adiciona animações automáticas
     *
     * @param movimentos Nova lista de movimentos
     */
    public void setMovimentos(List<Movimento> movimentos) {
        List<Movimento> newList = movimentos != null ? movimentos : new ArrayList<>();

        // Calcula as diferenças entre a lista antiga e a nova
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MovimentoDiffCallback(this.mMovimentos, newList));

        // Atualiza a lista
        this.mMovimentos = newList;

        // Notifica apenas as mudanças específicas com animações
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * Interface para callbacks de clique longo em itens
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(Movimento movimento);
    }

    /**
     * Callback para o DiffUtil calcular diferenças entre listas de movimentos
     * Compara IDs e conteúdo para determinar mudanças
     */
    private static class MovimentoDiffCallback extends DiffUtil.Callback {
        private final List<Movimento> oldList;
        private final List<Movimento> newList;

        public MovimentoDiffCallback(List<Movimento> oldList, List<Movimento> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // Compara se são o mesmo item (mesmo ID)
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            // Compara se o conteúdo é o mesmo
            Movimento oldMovimento = oldList.get(oldItemPosition);
            Movimento newMovimento = newList.get(newItemPosition);

            return oldMovimento.getValor() == newMovimento.getValor() &&
                    oldMovimento.getDescricao().equals(newMovimento.getDescricao()) &&
                    oldMovimento.getTipo().equals(newMovimento.getTipo()) &&
                    oldMovimento.getData() == newMovimento.getData() &&
                    oldMovimento.getCategoria().equals(newMovimento.getCategoria());
        }
    }

    /**
     * ViewHolder para cada item de movimento na lista.
     */
    public static class MovimentoViewHolder extends RecyclerView.ViewHolder {

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
         * Preenche os dados do movimento no layout.
         *
         * @param movimento         movimento a ser exibido
         * @param longClickListener listener para clique longo
         */
        public void bind(Movimento movimento, OnItemLongClickListener longClickListener) {
            // Definir descrição
            mTvDesc.setText(movimento.getDescricao());

            // Definir valor com cor baseada no tipo
            String valorFormatado = String.format(Locale.getDefault(), "%.2f €", movimento.getValor());
            if (Utils.TYPE_RECEITA.equalsIgnoreCase(movimento.getTipo())) {
                String textoReceita = itemView.getContext().getString(R.string.str_format_income, valorFormatado);
                mTvValor.setText(textoReceita);
                mTvValor.setTextColor(itemView.getContext().getColor(R.color.income_green));
            } else {
                String textoDespesa = itemView.getContext().getString(R.string.str_format_expense, valorFormatado);
                mTvValor.setText(textoDespesa);
                mTvValor.setTextColor(itemView.getContext().getColor(R.color.expense_red));
            }

            // Definir data formatada
            String dataFormatada = Utils.formatTimestamp(movimento.getData());
            mTvDate.setText(dataFormatada);

            // Configurar long click listener
            if (longClickListener != null) {
                itemView.setOnLongClickListener(v -> {
                    longClickListener.onItemLongClick(movimento);
                    return true;
                });
            }
        }
    }
}
