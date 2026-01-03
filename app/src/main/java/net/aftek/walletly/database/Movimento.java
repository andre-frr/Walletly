package net.aftek.walletly.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidade Room que representa um movimento financeiro (receita ou despesa).
 * Armazenada na tabela "Movimentos" da base de dados.
 */
@Entity(tableName = "Movimentos")
public class Movimento {

    /**
     * Data do movimento em timestamp Unix (milissegundos)
     */
    private final long data;
    /**
     * ID único gerado automaticamente
     */
    @PrimaryKey(autoGenerate = true)
    private int id;
    /**
     * Tipo do movimento: "receita" ou "despesa"
     */
    private String tipo;
    /**
     * Valor monetário do movimento
     */
    private double valor;
    /**
     * Descrição textual do movimento
     */
    private String descricao;
    /**
     * Categoria do movimento (ex: Salário, Alimentação, etc.)
     */
    private String categoria;

    /**
     * Construtor para criar um novo movimento.
     *
     * @param tipo      tipo do movimento ("receita" ou "despesa")
     * @param valor     valor monetário
     * @param descricao descrição do movimento
     * @param data      timestamp da transação
     * @param categoria categoria associada
     */
    public Movimento(String tipo, double valor, String descricao, long data, String categoria) {
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.data = data;
        this.categoria = categoria;
    }

    /**
     * @return ID único do movimento
     */
    public int getId() {
        return id;
    }

    /**
     * @param id ID único do movimento
     */
    @SuppressWarnings("unused") // Usado pelo Room para popular a entidade
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return tipo do movimento ("receita" ou "despesa")
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo tipo do movimento
     */
    @SuppressWarnings("unused") // Usado pelo Room para popular a entidade
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return valor monetário do movimento
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor valor monetário
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return descrição do movimento
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao descrição do movimento
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return timestamp da transação
     */
    public long getData() {
        return data;
    }

    /**
     * @return categoria do movimento
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * @param categoria categoria do movimento
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
