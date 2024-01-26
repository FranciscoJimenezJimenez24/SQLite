package ejercicios_android.sqlite.ejercicio1.actividades

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ejercicios_android.sqlite.R
import ejercicios_android.sqlite.ejercicio1.DataHelper
import ejercicios_android.sqlite.Extra
import ejercicios_android.sqlite.ejercicio1.Contacto
import kotlin.random.Random

class Ejercicio1Activity : AppCompatActivity() {
    private val INSERTAR_DATOS_PRUEBA = true

    private val CODIGO_RESPUESTA_ACTIVIDAD = 1

    private lateinit var mDb: DataHelper
    private lateinit var mAdapter: SimpleCursorAdapter

    private lateinit var mListView: ListView
    private lateinit var mTvListaVacia: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)

        mListView = findViewById(R.id.list)
        mTvListaVacia = findViewById(R.id.emptyView)
        mTvListaVacia.visibility = View.GONE

        // Instanciamos el DataHelper para el acceso a la base de datos
        mDb = DataHelper(this)

        // Insertamos datos de prueba
        if(INSERTAR_DATOS_PRUEBA) {
            mDb.deleteAll()
            for(i in 0..100) {
                mDb.insert(Contacto(
                        -1,
                        "Contacto SQLite " + String.format("%03d", i),
                        String.format("%03d %03d %03d", i, i, i),
                        "sqlite" + String.format("%03d", i) + "@email.com"
                    ),

                )
            }
        }

        // Creamos el adapter usando un SimpleCursorAdapter y el layout  simple_list_item_2
        mAdapter = SimpleCursorAdapter(
            this,
            android.R.layout.simple_list_item_2,  // layout propio de Android
            mDb.getCursor(),
            arrayOf("nombre", "telefono"),
            intArrayOf(android.R.id.text1, android.R.id.text2),  // ids de las vistas
            0   // Evitar el auto-requery (deprecated)
        )


        mListView.adapter = mAdapter
        mDb.setAdapter(mAdapter)

        // Listener pulsación
        mListView.setOnItemClickListener { adapterView, view, position, id ->
            val cursor = mAdapter.getItem(position) as Cursor
            val contactoId = cursor.getLong(cursor.getColumnIndex("_id")) // Obtenemos el identificador del contacto
            Log.d(javaClass.name, "Id: $contactoId")
            val intent = Intent(this@Ejercicio1Activity, Ejercicio1DetalleActivity::class.java)
            intent.putExtra(Extra.CONTACTO_ID, contactoId)
            startActivityForResult(intent, CODIGO_RESPUESTA_ACTIVIDAD)
        }

        prv_actualizarVisibilidadListado()
    }

    // Actualizar la visibilidad del listado. Si el listado está vacío mostramos un aviso.
    private fun prv_actualizarVisibilidadListado() {
        if (mAdapter.count == 0) {
            mListView.visibility = View.GONE
            mTvListaVacia.visibility = View.VISIBLE
        } else {
            mListView.visibility = View.VISIBLE
            mTvListaVacia.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        // Asociamos el menú mediante el fichero de recurso
        menuInflater.inflate(R.menu.listado_contactos_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(elemento: MenuItem): Boolean {
        super.onOptionsItemSelected(elemento)
        if (elemento.itemId == R.id.anadir_contacto) {
            val intent = Intent(this@Ejercicio1Activity, Ejercicio1FormularioActivity::class.java)
            startActivityForResult(intent, CODIGO_RESPUESTA_ACTIVIDAD)
            return true
        }
        // Devolvemos false si no hemos hecho nada con el elemento seleccionado.
        // (permitirá que se ejecuten otros manejadores)
        // Si devolvemos true finalizará el procesamiento.
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_RESPUESTA_ACTIVIDAD && resultCode == RESULT_OK) {
            mAdapter.changeCursor( mDb.getCursor() ) // Actualizamos el cursor
            prv_actualizarVisibilidadListado()
        }
    }
}