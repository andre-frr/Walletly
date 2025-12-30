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
     */
    @Insert
    void insert(Movimento movimento);

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
    List<Movimento> getAll();

    /**
     * Obtém os N movimentos mais recentes
     *
     * @param limit Número máximo de movimentos a retornar
     * @return Lista dos movimentos mais recentes
     */
    @Query("SELECT * FROM Movimentos ORDER BY data DESC LIMIT :limit")
    List<Movimento> getRecent(int limit);

}
