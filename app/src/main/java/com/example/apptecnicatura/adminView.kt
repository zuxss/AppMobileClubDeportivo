package com.example.apptecnicatura

import android.os.Build
import android.os.Bundle
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate

class adminView : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_view)
        databaseHelper = DatabaseHelper(this)

        val date = LocalDate.now()

        val gridLayoutResult = findViewById<GridLayout>(R.id.gridLayout)


        val resultadosConsulta: List <List<String>> = databaseHelper.getList()

        for(fila in resultadosConsulta){
            for(dato in fila){
                val textView = TextView(this)
                textView.text = dato
                val params = GridLayout.LayoutParams()
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1F)
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1F)
                textView.layoutParams = params
                gridLayoutResult.addView(textView)
            }
        }


    }
}