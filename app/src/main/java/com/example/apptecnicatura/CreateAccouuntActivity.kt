package com.example.apptecnicatura

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreateAccouuntActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_accouunt)

        databaseHelper = DatabaseHelper(this)
        val usernameText = findViewById<EditText>(R.id.userNameText)
        val passwordText = findViewById<EditText>(R.id.passwordText)
        val nameText = findViewById<EditText>(R.id.nameText)
        val ageText = findViewById<EditText>(R.id.ageText)
        val radioGroupTipo = findViewById<RadioGroup>(R.id.radioGroupTipo)
        val buttonCreateAccount = findViewById<Button>(R.id.createAccountButton)


        buttonCreateAccount.setOnClickListener {
            val user = usernameText.text.toString()
            val password = passwordText.text.toString()
            val name = nameText.text.toString()
            val ageString = ageText.text.toString()
            val currentDate = LocalDate.now().toString()

            if (user.isEmpty() || password.isEmpty() || name.isEmpty() || ageString.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age: Int = try {
                ageString.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Por favor, ingrese una edad válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadioButton = radioGroupTipo.checkedRadioButtonId

            if (selectedRadioButton != -1) {
                val isSocio = findViewById<RadioButton>(selectedRadioButton).text == "Socio"

                val success = if (isSocio) {

                    databaseHelper.insertSocio(user, password, name, age, currentDate)
                } else {

                    databaseHelper.insertNoSocio(user, password, name, age, currentDate)
                }

                if (success) {
                    Toast.makeText(this, if (isSocio) "Socio Registrado" else "No Socio Registrado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Nombre de usuario ya en uso", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Selecciona un tipo", Toast.LENGTH_SHORT).show()
            }

        }


    }
}