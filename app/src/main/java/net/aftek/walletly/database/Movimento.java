package net.aftek.walletly.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Movimentos")

public class Movimento {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String tipo; // receita ou despesa
    private double valor;
    private String descricao;
    private long data;
    private String categoria;

    // Construtor
    public Movimento(String tipo, double valor, String descricao, long data, String categoria) {
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.data = data;
        this.categoria = categoria;
    }

    // Gets e Sets
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}