package net.aftek.walletly.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Base de dados Room para a aplicação Walletly
 * Utiliza o padrão Singleton para garantir uma única instância em toda a aplicação
 * Segue o princípio KISS - implementação simples e direta
 */
@Database(entities = {Movimento.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    /**
     * Obtém a instância única da base de dados (Singleton)
     * Thread-safe para evitar múltiplas instâncias
     *
     * @param context Contexto da aplicação
     * @return Instância única do AppDatabase
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "walletly_database"
            ).build();
        }
        return instance;
    }

    /**
     * Obtém o DAO para operações com movimentos
     *
     * @return MovimentoDao para acesso à tabela de movimentos
     */
    public abstract MovimentoDao movimentoDao();
}