package com.example.apptecnicatura


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        databaseHelper = DatabaseHelper(this)

        // Recuperar el ID del usuario de SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)

        if (userId != -1L) {
            // Obtener los detalles del usuario y mostrarlos
            val userDetails = databaseHelper.getUserDetails(userId)
            if (userDetails != null) {

                val inscriptionDate =  userDetails["inscriptiondate"] as String

                findViewById<TextView>(R.id.nameText).text = "Nombre: ${userDetails["name"] as String}"
                findViewById<TextView>(R.id.ageText).text = "Edad: ${(userDetails["age"] as Number)}"

                findViewById<TextView>(R.id.inscriptionDate).text = inscriptionDate

                if(userDetails["paymentdate"] != null){

                    val paymentDate = userDetails["paymentdate"] as String
                    val dueDate = userDetails["duedate"] as String
                    findViewById<TextView>(R.id.makePayDate).text = paymentDate
                    findViewById<TextView>(R.id.paymentDate).text = dueDate
                }else{
                    findViewById<TextView>(R.id.paymentDate).text = "---/--/--"
                    findViewById<TextView>(R.id.makePayDate).text = "No hay pagos realizados"
                }

                if(userDetails["paid"] as Boolean) {
                    findViewById<TextView>(R.id.userStatus).text = "Membresia Activa"
                }else{
                    findViewById<TextView>(R.id.userStatus).text = "Membresia Vencida"
                }

            } else {
                Toast.makeText(this, "No se encontraron detalles del usuario", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "ID de usuario no válido", Toast.LENGTH_SHORT).show()
        }


        val button: Button = findViewById(R.id.PayButton)

        button.setOnClickListener{

            val d = databaseHelper.getNoSocioId(userId)
            println(d)


            if (d == null) {
                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            } else {
                val paidStatus: Boolean = true
                val currentDate = LocalDate.now().toString()
                var dueDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE).plusDays(1).toString()
                databaseHelper.updatePaidStatus(d,paidStatus,currentDate,dueDate, "NoSocios")
                findViewById<TextView>(R.id.paymentDate).text = dueDate
                findViewById<TextView>(R.id.makePayDate).text = currentDate
                findViewById<TextView>(R.id.userStatus).text = "Membresia Activa"
                Toast.makeText(this, "Pago realizado.", Toast.LENGTH_SHORT).show()

            }


        }
    }





}