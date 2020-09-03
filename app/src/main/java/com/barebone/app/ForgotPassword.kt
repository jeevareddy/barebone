package com.barebone.app

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class ForgotPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val sharedPref = getSharedPreferences(
            "${packageName}.${intent.getStringExtra("email")}",
            MODE_PRIVATE
        )
        val actualMobileNumber = sharedPref.getString(
            "mobile",
            resources.getString(R.string.defaultValue)
        )
        val forgotPasPhrase: TextView = findViewById(R.id.enterMobileNumber)
        val sourceString = "Enter your mobile number ending with " + "<b>" + "******${
            actualMobileNumber.toString().subSequence(6, 10)
        }" + "</b>"
        forgotPasPhrase.text = Html.fromHtml(sourceString)

        val button = findViewById<Button>(R.id.validate)
        button.setOnClickListener {
            val mobileNumber: TextView = findViewById(R.id.mobile)

            when {
                mobileNumber.text.isNullOrEmpty() -> Toast.makeText(
                    applicationContext,
                    "Mobile number can't be empty",
                    Toast.LENGTH_LONG
                ).show()
                mobileNumber.text.toString() == actualMobileNumber -> {
                    forgotPasPhrase.text = "Enter new password"
                    findViewById<TextInputLayout>(R.id.mobileInputLayout).visibility = View.GONE
                    findViewById<TextInputLayout>(R.id.passwordInputLayout).visibility =
                        View.VISIBLE
                    findViewById<TextInputLayout>(R.id.confirmPasswordInputLayout).visibility =
                        View.VISIBLE
                    Toast.makeText(applicationContext, "Validation success", Toast.LENGTH_LONG)
                        .show()
                    button.text = "CHANGE"
                    button.setOnClickListener {
                        val password = findViewById<TextInputEditText>(R.id.password)
                        val confirmPassword = findViewById<TextInputEditText>(R.id.confirmPassword)
                        when {
                            password.text.isNullOrEmpty() -> Toast.makeText(
                                applicationContext,
                                "Password can't be empty",
                                Toast.LENGTH_LONG
                            ).show()
                            password.text.toString() != confirmPassword.text.toString() -> Toast.makeText(
                                applicationContext,
                                "Passwords doesn't match",
                                Toast.LENGTH_LONG
                            ).show()
                            else -> {
                                sharedPref.edit { putString("password", password.text.toString()) }
                                Toast.makeText(
                                    applicationContext,
                                    "Password changed successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }

                        }
                    }
                }
                else -> {
                    Toast.makeText(applicationContext, "Incorrect Mobile Number", Toast.LENGTH_LONG)
                        .show()
                }
            }

        }

    }
}