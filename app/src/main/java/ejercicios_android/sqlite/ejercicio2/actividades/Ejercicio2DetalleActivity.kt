package ejercicios_android.sqlite.ejercicio2.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import ejercicios_android.sqlite.Extra
import ejercicios_android.sqlite.R
import ejercicios_android.sqlite.ejercicio2.AppDatabase
import ejercicios_android.sqlite.ejercicio2.entity.Contacto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Ejercicio2DetalleActivity : AppCompatActivity() {
    private val CODIGO_RESPUESTA_ACTIVIDAD = 1

    private lateinit var mTvNombre: TextView
    private lateinit var mTvTelefono: TextView
    private lateinit var mTvEmail: TextView

    private lateinit var mDb: AppDatabase
    private var mId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        mTvNombre = findViewById(R.id.tvNombre)
        mTvTelefono = findViewById(R.id.tvTelefono)
        mTvEmail = findViewById(R.id.tvEmail)

        if (intent == null) {
            prv_returnResult("Error: no se ha especificado ningún contacto", RESULT_CANCELED)
            return
        }

        mDb = AppDatabase(this)

        mId = intent.getLongExtra(Extra.CONTACTO_ID, -1)
        Log.d(javaClass.name, "Id: $mId")

        if (mId == -1L) {
            prv_returnResult("Error: contacto incorrecto", RESULT_CANCELED)
            return
        }

        prv_actualizarInterfaz()

        // Listener Editar
        findViewById<Button>(R.id.btnEditar).setOnClickListener {
            val intent = Intent(this@Ejercicio2DetalleActivity, Ejercicio2FormularioActivity::class.java)
            intent.putExtra(Extra.CONTACTO_ID, mId)
            startActivityForResult(intent, CODIGO_RESPUESTA_ACTIVIDAD)
        }

        // Listener Eliminar
        findViewById<Button>(R.id.btnEliminar).setOnClickListener {
            prv_emilinarContacto(mId)
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener{
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_RESPUESTA_ACTIVIDAD && resultCode == RESULT_OK) {
            prv_actualizarInterfaz()
            setResult(RESULT_OK) // Para que al finalizar se actualice el listado
        }
    }

    private fun prv_actualizarInterfaz() {
        GlobalScope.launch(Dispatchers.IO) {
            val contacto = mDb.contactoDao().getById(mId)

            launch(Dispatchers.Main) {
                if (contacto == null)
                    prv_returnResult("Error: contacto incorrecto", RESULT_CANCELED)
                else {
                    mTvNombre.text = contacto.nombre
                    mTvTelefono.text = contacto.telefono
                    mTvEmail.text = contacto.email
                }
            }
        }
    }

    private fun prv_emilinarContacto(id: Long) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("¿Desea eliminar este contacto?")
            .setPositiveButton("Aceptar") { _, _ ->
                GlobalScope.launch(Dispatchers.IO) {
                    val result = mDb.contactoDao().delete(
                        Contacto(mId, "", "", ""))

                    launch(Dispatchers.Main) {
                        if (result > 0)
                            prv_returnResult("El contacto se ha eliminado", RESULT_OK)
                        else
                            Toast.makeText(this@Ejercicio2DetalleActivity, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun prv_returnResult(str: String, resultCode: Int) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        setResult(resultCode)
        finish()
    }
}