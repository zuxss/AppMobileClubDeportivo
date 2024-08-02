package com.example.apptecnicatura

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PaymentActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
        databaseHelper = DatabaseHelper(this)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)

        val userDetails = databaseHelper.getUserDetails(userId)
        val id =  userDetails?.get("id") as Integer
        val button: Button = findViewById(R.id.PaymentConfirmButton)
        val button2: Button = findViewById(R.id.CancelButton)

        button.setOnClickListener{
            val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
            val paidStatus: Boolean = true
            val currentDate = LocalDate.now().toString()
            var dueDate: String? = null
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButton1Month -> {
                    dueDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE).plusMonths(1).toString()
                    databaseHelper.updatePaidStatus(id.toString(),paidStatus,currentDate,dueDate, "Socios")
                }
                R.id.radioButton3Month -> {
                    dueDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE).plusMonths(3).toString()
                    databaseHelper.updatePaidStatus(id.toString(),paidStatus,currentDate,dueDate, "Socios")
                }
                R.id.radioButton6Month -> {
                    dueDate = LocalDate.parse(currentDate, DateTimeFormatter.ISO_DATE).plusMonths(6).toString()
                    databaseHelper.updatePaidStatus(id.toString(),paidStatus,currentDate,dueDate, "Socios")
                }
            }


            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener{
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }
        }
    }
