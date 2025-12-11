package net.aftek.walletly.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object para operações com a tabela Movimentos
 * Interface Room que gera automaticamente as implementações SQL
 * Queries simples seguindo o princípio KISS
 */
@Dao
public interface MovimentoDao {

    /**
     * Insere um novo movimento na base de dados
     *
     * @param movimento Movimento a inserir
     * @return ID do movimento inserido
     */
    @Insert
    long insert(Movimento movimento);

    /**
     * Atualiza um movimento existente
     *
     * @param movimento Movimento a atualizar
     */
    @Update
    void update(Movimento movimento);

    /**
     * Remove um movimento da base de dados
     *
     * @param movimento Movimento a remover
     */
    @Delete
    void delete(Movimento movimento);

    /**
     * Obtém todos os movimentos ordenados por data (mais recente primeiro)
     *
     * @return Lista de todos os movimentos
     */
    @Query("SELECT * FROM Movimentos ORDER BY data DESC")
    List<Movimento> getMovimentos();

    /**
     * Obtém todos os movimentos ordenados por data (mais recente primeiro)
     * Método alternativo ao getMovimentos() - evitar duplicação (DRY)
     *
     * @return Lista de todos os movimentos
     */
    @Query("SELECT * FROM Movimentos ORDER BY data DESC")
    List<Movimento> getAll();

    /**
     * Obtém os N movimentos mais recentes
     *
     * @param limit Número máximo de movimentos a retornar
     * @return Lista dos movimentos mais recentes
     */
    @Query("SELECT * FROM Movimentos ORDER BY data DESC LIMIT :limit")
    List<Movimento> getRecent(int limit);

    /**
     * Obtém movimentos filtrados por tipo (receita ou despesa)
     *
     * @param tipo Tipo de movimento ("receita" ou "despesa")
     * @return Lista de movimentos do tipo especificado
     */
    @Query("SELECT * FROM Movimentos WHERE tipo = :tipo ORDER BY data DESC")
    List<Movimento> getMovimentosPorTipo(String tipo);

    /**
     * Calcula o total de todas as receitas
     *
     * @return Soma de todos os valores de receitas
     */
    @Query("SELECT SUM(valor) FROM Movimentos WHERE tipo = 'receita'")
    double getTotalReceitas();

    /**
     * Calcula o total de todas as despesas
     *
     * @return Soma de todos os valores de despesas
     */
    @Query("SELECT SUM(valor) FROM Movimentos WHERE tipo = 'despesa'")
    double getTotalDespesas();

    /**
     * Obtém movimentos filtrados por categoria
     *
     * @param categoria Categoria a filtrar
     * @return Lista de movimentos da categoria especificada
     */
    @Query("SELECT * FROM Movimentos WHERE categoria = :categoria ORDER BY data DESC")
    List<Movimento> getMovimentosPorCategoria(String categoria);

}