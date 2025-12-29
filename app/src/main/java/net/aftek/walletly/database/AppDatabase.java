package net.aftek.walletly.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Base de dados Room para a aplicação Walletly.
 * Utiliza o padrão Singleton para garantir uma única instância em toda a aplicação.
 * Segue o princípio KISS - implementação simples e direta.
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
     * Thread-safe usando AtomicReference para evitar múltiplas instâncias
     * O padrão Singleton é necessário para:
     * - Evitar criações repetidas da base de dados (operação cara)
     * - Prevenir conflitos de acesso concorrente
     * - Garantir consistência dos dados em toda a aplicação
     *
     * @param context Contexto da aplicação
     * @return Instância única do AppDatabase
     */
    public static AppDatabase getInstance(Context context) {
        if (instance.get() == null) {
            synchronized (AppDatabase.class) {
                if (instance.get() == null) {
                    instance.set(Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "walletly_database"
                    ).build());
                }
            }
        }
        return instance.get();
    }

    /**
     * Obtém o DAO para operações com movimentos
     *
     * @return MovimentoDao para acesso à tabela de movimentos
     */
    public abstract MovimentoDao movimentoDao();
}