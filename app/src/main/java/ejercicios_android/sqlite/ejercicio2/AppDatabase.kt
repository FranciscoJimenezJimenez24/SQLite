package ejercicios_android.sqlite.ejercicio2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ejercicios_android.sqlite.ejercicio2.dao.ContactoDao
import ejercicios_android.sqlite.ejercicio2.entity.Contacto

@Database(entities = [Contacto::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactoDao(): ContactoDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val DBNAME = "contactos_ej2.bd"
        private val LOCK = Any()

        operator fun invoke(contexto: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(contexto).also { instance = it}
        }
        private fun buildDatabase(contexto: Context) = Room.databaseBuilder(
            contexto, AppDatabase::class.java, DBNAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE contacto ADD COLUMN email TEXT")
            }
        }
    }
}