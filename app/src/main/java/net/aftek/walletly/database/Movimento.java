package net.aftek.walletly.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidade Room que representa um movimento financeiro (receita ou despesa)
 * Armazenada na tabela "Movimentos" da base de dados
 */
@Entity(tableName = "Movimentos")
public class Movimento {

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
     * Data do movimento em timestamp Unix (milissegundos)
     */
    private long data;

    /**
     * Categoria do movimento (ex: Salário, Alimentação, etc.)
     */
    private String categoria;

    /**
     * Construtor para criar um novo movimento
     *
     * @param tipo      Tipo do movimento ("receita" ou "despesa")
     * @param valor     Valor monetário
     * @param descricao Descrição do movimento
     * @param data      Timestamp da transação
     * @param categoria Categoria associada
     */
    public Movimento(String tipo, double valor, String descricao, long data, String categoria) {
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.data = data;
        this.categoria = categoria;
    }

    // Getters e Setters

    /**
     * @return ID único do movimento
     */
    public int getId() {
        return id;
    }

    /**
     * @param id ID único do movimento
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Tipo do movimento ("receita" ou "despesa")
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo Tipo do movimento
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return Valor monetário do movimento
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor Valor monetário
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return Descrição do movimento
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao Descrição do movimento
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return Timestamp da transação
     */
    public long getData() {
        return data;
    }

    /**
     * @param data Timestamp da transação
     */
    public void setData(long data) {
        this.data = data;
    }

    /**
     * @return Categoria do movimento
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * @param categoria Categoria do movimento
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
