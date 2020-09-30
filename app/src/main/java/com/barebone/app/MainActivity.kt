package com.barebone.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var persistantlogin = this.getPreferences(Context.MODE_PRIVATE)
        Log.d("sp", getPreferences(Context.MODE_PRIVATE).getString("email", "null"))
        if (persistantlogin != null) {
            val logedin = persistantlogin.getString("logedin", "null")
            if (logedin != "null" && logedin == "true") {
                val logedEmail = persistantlogin.getString("email", "null")
                if (logedEmail != "null") {
                    sharedPref =
                        getSharedPreferences("${packageName}.$logedEmail", Context.MODE_PRIVATE)
                    login()
                }
            }
        }

        val login = findViewById<TextView>(R.id.login)
        login.setOnClickListener {
            val email = findViewById<EditText>(R.id.email).text
            val password = findViewById<EditText>(R.id.password).text
            sharedPref = getSharedPreferences(
                "${packageName}.$email",
                MODE_PRIVATE
            )
            Log.d("msg", sharedPref.toString())
            if (sharedPref.getString(
                    "email",
                    resources.getString(R.string.defaultValue)
                ) == email.toString()
            ) {
                //Email Exists
                if (sharedPref.getString(
                        "password",
                        resources.getString(R.string.defaultValue)
                    ) == password.toString()
                ) {
                    //password matched
                    persistantlogin = this.getPreferences(Context.MODE_PRIVATE)
                    persistantlogin.edit {
                        putString("logedin", "true")
                        putString("email", email.toString())
                    }
                    getSharedPreferences(packageName, Context.MODE_PRIVATE).edit {
                        putString("email", email.toString())
                    }
                    login()
                } else {
                    //Password doesn't match
                    Toast.makeText(applicationContext, "Invalid password", Toast.LENGTH_LONG)
                        .show()
                }

            } else {
                //Email not exist
                Toast.makeText(applicationContext, "Email not exist", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<TextView>(R.id.forgotPassword).setOnClickListener {
            val email = findViewById<EditText>(R.id.email)
            if (email.text.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Enter your email", Toast.LENGTH_LONG).show()
            } else {
                sharedPref = getSharedPreferences(
                    "${packageName}.${email.text}",
                    MODE_PRIVATE
                )
                if (sharedPref.getString(
                        "email",
                        "@string/defaultValue"
                    ) != email.text.toString()
                ) {
                    Toast.makeText(applicationContext, "Invalid email", Toast.LENGTH_LONG).show()
                } else {
                    val intent = Intent(this, ForgotPassword::class.java)
                    intent.putExtra("email", email.text.toString())
                    startActivity(intent)
                }
            }
        }

        val register = findViewById<TextView>(R.id.register)
        register.setOnClickListener {

            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

    }

    private fun login() {
//        val intent = Intent(this, Dashboard::class.java)
        val intent = Intent(this, BottomNav::class.java)
        intent.putExtra(
            "name", sharedPref.getString(
                "name",
                resources.getString(R.string.defaultValue)
            )
        )
        intent.putExtra(
            "email", sharedPref.getString(
                "email",
                resources.getString(R.string.defaultValue)
            )
        )
        intent.putExtra(
            "mobile", sharedPref.getString(
                "mobile",
                resources.getString(R.string.defaultValue)
            )
        )
        intent.putExtra(
            "gender", sharedPref.getString(
                "gender",
                resources.getString(R.string.defaultValue)
            )
        )
        finish()
        startActivity(intent)
        Toast.makeText(applicationContext, "Login success", Toast.LENGTH_LONG).show()
    }
}

