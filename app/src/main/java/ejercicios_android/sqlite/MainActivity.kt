package ejercicios_android.sqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ejercicios_android.sqlite.ejercicio1.actividades.Ejercicio1Activity
import ejercicios_android.sqlite.ejercicio2.actividades.Ejercicio2Activity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnEj1).setOnClickListener {
            startActivity(Intent(this@MainActivity, Ejercicio1Activity::class.java))
        }

        findViewById<Button>(R.id.btnEj2).setOnClickListener {
            startActivity(Intent(this@MainActivity, Ejercicio2Activity::class.java))
        }
    }
}