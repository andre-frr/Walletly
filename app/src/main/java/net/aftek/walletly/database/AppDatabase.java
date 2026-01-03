package net.aftek.walletly.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Base de dados Room para a aplicação Walletly.
 * Utiliza o padrão Singleton para garantir uma única instância em toda a aplicação.
 * Singleton Pattern Justification:
 * - Room databases são thread-safe por design
 * - Criação de múltiplas instâncias causaria inconsistência de dados
 * - AtomicReference garante thread-safety completa
 */
@Database(entities = {Movimento.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final AtomicReference<AppDatabase> instance = new AtomicReference<>();

    /**
     * Obtém a instância única da base de dados (Singleton)
     * Thread-safe ao usar AtomicReference e sincronização
     *
     * @param context Contexto da aplicação
     * @return Instância única do AppDatabase
     */
    public static AppDatabase getInstance(Context context) {
        AppDatabase db = instance.get();
        if (db == null) {
            synchronized (AppDatabase.class) {
                db = instance.get();
                if (db == null) {
                    db = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "walletly_database"
                    ).build();
                    instance.set(db);
                }
            }
        }
        return db;
    }

    /**
     * Obtém o DAO para operações com movimentos.
     *
     * @return MovimentoDao para acesso à tabela de movimentos
     */
    public abstract MovimentoDao movimentoDao();
}
