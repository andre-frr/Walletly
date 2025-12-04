package net.aftek.walletly.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovimentoDao {

    @Insert
    long insert(Movimento movimento);

    @Update
    void update(Movimento movimento);

    @Delete
    void delete(Movimento movimento);

    @Query("SELECT * FROM Movimentos ORDER BY data DESC")
    List<Movimento> getMovimentos();

    @Query("SELECT * FROM Movimentos WHERE tipo = :tipo ORDER BY data DESC")
    List<Movimento> getMovimentosPorTipo(String tipo);

    @Query("SELECT SUM(valor) FROM Movimentos WHERE tipo = 'receita'")
    double getTotalReceitas();

    @Query("SELECT SUM(valor) FROM Movimentos WHERE tipo = 'despesa'")
    double getTotalDespesas();

    @Query("SELECT * FROM Movimentos WHERE categoria = :categoria ORDER BY data DESC")
    List<Movimento> getMovimentosPorCategoria(String categoria);

}
