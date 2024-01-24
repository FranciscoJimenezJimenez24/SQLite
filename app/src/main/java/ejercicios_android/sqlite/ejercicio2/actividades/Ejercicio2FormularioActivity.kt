package ejercicios_android.sqlite.ejercicio2.actividades

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ejercicios_android.sqlite.Extra
import ejercicios_android.sqlite.R
import ejercicios_android.sqlite.ejercicio2.AppDatabase
import ejercicios_android.sqlite.ejercicio2.entity.Contacto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Ejercicio2FormularioActivity : AppCompatActivity() {

    private lateinit var mDb: AppDatabase
    private var mId:Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val etEmail = findViewById<EditText>(R.id.etEmail)

        mDb = AppDatabase(this)

        // Comprobamos si se usa el formulario para crear un nuevo contacto o para modificarlo.
        if (intent != null) {
            mId = intent.getLongExtra(Extra.CONTACTO_ID, -1)
            if (mId != -1L) {

                GlobalScope.launch(Dispatchers.IO) {
                    mDb.contactoDao().getById(mId)?.let {
                        launch(Dispatchers.Main) {
                            etNombre.setText( it.nombre )
                            etTelefono.setText( it.telefono )
                            etEmail.setText( it.email )
                        }
                    }
                }
            }
        }

        // Listener Guardar
        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val nombre = etNombre.text.toString()
            val telefono = etTelefono.text.toString()
            val email = etEmail.text.toString()

            if (nombre.isEmpty())
                prv_showToast("El nombre no puede estar vacío")
            else if (telefono.isEmpty())
                prv_showToast("El teléfono no puede estar vacío")
            else if (email.isEmpty())
                prv_showToast("El email no puede estar vacío")
            else {
                if (mId == -1L) {
                    GlobalScope.launch(Dispatchers.IO) {
                        mDb.contactoDao().insert(Contacto(0, nombre, telefono, email))
                        launch(Dispatchers.Main) {
                            prv_showToast("El contacto se ha añadido")
                        }
                    }
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        mDb.contactoDao().update(Contacto(mId, nombre, telefono, email))
                        launch(Dispatchers.Main) {
                            prv_showToast("El contacto se ha actualizado")
                        }
                    }
                }
                setResult(RESULT_OK)
                finish()
            }
        }

        // Listener Cancelar
        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun prv_showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }
}