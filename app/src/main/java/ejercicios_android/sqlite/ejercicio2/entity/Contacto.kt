package ejercicios_android.sqlite.ejercicio2.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contacto(
    @PrimaryKey(autoGenerate = true) val _id: Long = 0,
    val nombre: String,
    val telefono: String,
    val email: String)
