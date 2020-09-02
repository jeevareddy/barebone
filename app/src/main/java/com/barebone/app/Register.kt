package com.barebone.app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.edit


class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Register", "Register Activity started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val dropdown = findViewById<AppCompatSpinner>(R.id.gender)
        ArrayAdapter.createFromResource(
            this,
            R.array.genderList,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dropdown.adapter = adapter
            dropdown.setSelection(0)
        }

        findViewById<Button>(R.id.register).setOnClickListener {
            val user = User(
                findViewById(R.id.name),

                findViewById(R.id.gender),

                findViewById(R.id.email),

                findViewById(R.id.mobile),

                findViewById(R.id.password),

                findViewById(R.id.confirmPassowrd)
            )

            validate(user)
        }


        findViewById<TextView>(R.id.login).setOnClickListener { finish() }

    }

    private fun validate(vararg user: User) {
        when {
            user[0].name.text.isNullOrEmpty() -> Toast.makeText(
                applicationContext,
                "Name cannot be empty",
                Toast.LENGTH_LONG
            ).show()
            user[0].gender.selectedItemPosition == 0 -> Toast.makeText(
                applicationContext,
                "Select your gender",
                Toast.LENGTH_LONG
            ).show()
            user[0].email.text.isNullOrEmpty() -> Toast.makeText(
                applicationContext,
                "Email cannot be empty",
                Toast.LENGTH_LONG
            ).show()
            user[0].mobile.text.isNullOrEmpty() -> Toast.makeText(
                applicationContext,
                "Mobile Number cannot be empty",
                Toast.LENGTH_LONG
            )
                .show()
            user[0].password.text.isNullOrEmpty() -> Toast.makeText(
                applicationContext,
                "Password cannot be empty",
                Toast.LENGTH_LONG
            ).show()
            user[0].password.text.toString() != user[0].confirmPassword.text.toString() -> Toast.makeText(
                applicationContext,
                "Passwords doesn't match",
                Toast.LENGTH_LONG
            ).show()
            else -> {
                register(user[0])
            }
        }
    }

    private fun register(vararg user: User) {
        val shredPref = getSharedPreferences(
            "${packageName}.${user[0].email.text.toString()}",
            Context.MODE_PRIVATE
        )
        if (shredPref.getString(
                "email",
                resources.getString(R.string.defaultValue)
            ) == user[0].email.text.toString()
        ) {
            Toast.makeText(applicationContext, "Email already exist", Toast.LENGTH_LONG).show()
        } else {
            shredPref.edit {
                putString("name", user[0].name.text.toString())
                putString("gender", user[0].gender.selectedItem.toString())
                putString("email", user[0].email.text.toString())
                putString("mobile", user[0].mobile.text.toString())
                putString("password", user[0].password.text.toString())
                commit()
            }
            Toast.makeText(applicationContext, "Registered Successfully", Toast.LENGTH_LONG).show()
            finish()
        }
    }


}


