package ejercicios_android.sqlite.ejercicio1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import android.widget.BaseAdapter

class DataHelper(contexto: Context) {
    private val DBNAME = "contactos_ej1.bd"
    private val DBVERSION = 2

    private var mAdapter: BaseAdapter? = null

    private lateinit var mDB: SQLiteDatabase

    private lateinit var mInsertStatement: SQLiteStatement

    init {
        val oHelper = MiOpenHelper(contexto, DBNAME, null, DBVERSION)
        mDB = oHelper.writableDatabase

        val INSERT = "INSERT INTO contactos (nombre, telefono, email) VALUES (?,?,?)"
        mInsertStatement = mDB.compileStatement(INSERT)
    }

    fun setAdapter(adapter: BaseAdapter?) {
        mAdapter = adapter
    }

    fun getCursor(): Cursor? {
        val orderBy = "nombre ASC"
        return mDB.query("contactos", null, null, null, null, null, orderBy)
    }

    fun selectById(id: Long): Contacto? {
        val columns = arrayOf("nombre", "telefono", "email")
        val selection = "_id = ?"
        val args = arrayOf(id.toString())
        val limit = "1"
        val cursor = mDB.query("contactos", columns, selection, args, null, null, null, limit)
        var contacto: Contacto? = null
        if (cursor != null && cursor.moveToFirst()) {
            contacto = Contacto(id, cursor.getString(0), cursor.getString(1), cursor.getString(2))
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close() // Cerramos el cursor
        }
        return contacto
    }

    fun insert(contacto: Contacto): Long {
        val values = ContentValues()
        values.put("nombre", contacto.nombre)
        values.put("telefono", contacto.telefono)
        values.put("email", contacto.email)
        val rvalue = mDB.insert("contactos", null, values)
        mAdapter?.notifyDataSetChanged()
        return rvalue
    }

    // Insertar datos mediante sentencia compilada
    fun insertWithCompileStatement(contacto: Contacto): Long {
        mInsertStatement.bindString(1, contacto.nombre)
        mInsertStatement.bindString(2, contacto.telefono)
        mInsertStatement.bindString(3, contacto.email)
        val rvalue = mInsertStatement.executeInsert()
        mAdapter?.notifyDataSetChanged()
        return rvalue
    }

    fun update(contacto: Contacto): Int {
        val values = ContentValues()
        values.put("nombre", contacto.nombre)
        values.put("telefono", contacto.telefono)
        values.put("email", contacto.email)
        val selection = "_id = ?"
        val args = arrayOf(contacto._id.toString())
        val rvalue = mDB.update("contactos", values, selection, args)
        mAdapter?.notifyDataSetChanged()
        return rvalue
    }

    fun deleteAll(): Int {
        val rvalue = mDB.delete("contactos", "1", null)
        mAdapter?.notifyDataSetChanged()
        return rvalue
    }

    fun deleteById(id: Long): Int {
        val rvalue = mDB.delete("contactos", "_id = ?", arrayOf(id.toString()))
        mAdapter?.notifyDataSetChanged()
        return rvalue
    }

    class MiOpenHelper(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
        SQLiteOpenHelper(context, name, factory, version) {
        override fun onCreate(db: SQLiteDatabase) {
            val CREATE_DB = ("CREATE TABLE contactos ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT,"
                    + "telefono TEXT,"
                    + "email TEXT)")
            db.execSQL(CREATE_DB)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if(oldVersion == 1)
                db.execSQL("ALTER TABLE contactos ADD COLUMN email TEXT")
            if(oldVersion > 1)
                throw IllegalStateException("Versión desconocida $oldVersion")
        }
    }
}