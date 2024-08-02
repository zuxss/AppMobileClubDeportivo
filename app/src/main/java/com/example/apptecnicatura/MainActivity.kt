package com.example.apptecnicatura

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val button: Button = findViewById(R.id.StartButton)
        val userNameEditText = findViewById<EditText>(R.id.InputUser)
        val userPasswordEditText = findViewById<EditText>(R.id.InputPasword)
        val textView = findViewById<TextView>(R.id.CreateAccountText)
        databaseHelper = DatabaseHelper(this)

        button.setOnClickListener{
            val username = userNameEditText.text.toString()
            val password = userPasswordEditText.text.toString()
            val userId = databaseHelper.validateUser(username, password)


            if (userId != null){
                //guardar id del usuario que inicia sesión
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putLong("USER_ID", userId)
                editor.apply()

                if(username == "admin"){
                    val intent = Intent(this,adminView:: class.java)
                    startActivity(intent)
                }else{
                    //ir a home
                    val intent = Intent(this, HomeScreenActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }else{
                Toast.makeText(this,"Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }

        }

        textView.setOnClickListener {

            val intent = Intent(this, CreateAccouuntActivity::class.java)
            startActivity(intent)
        }

        }
    }

private fun Any.putLong(s: String, userId: Boolean) {

}



