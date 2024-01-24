package ejercicios_android.sqlite.ejercicio2.dao

import android.database.Cursor
import androidx.room.*
import ejercicios_android.sqlite.ejercicio2.entity.Contacto

@Dao
interface ContactoDao {
    @Query("SELECT * FROM contacto ORDER BY nombre")
    fun getAll(): List<Contacto>

    @Query("SELECT * FROM contacto ORDER BY nombre")
    fun getCursor(): Cursor

    @Query("SELECT * FROM contacto WHERE _id = :id")
    fun getById(id: Long): Contacto?

    @Insert
    fun insert(contacto: Contacto): Long

    @Update
    fun update(contacto: Contacto)

    @Delete
    fun delete(contacto: Contacto): Int

    @Query("DELETE FROM contacto")
    fun deleteAll()
}