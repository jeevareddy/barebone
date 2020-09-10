package com.barebone.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class AddEdit : AppCompatActivity() {
    var db: Barebonedb? = null
    var dao: UserDao? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add__edit)

        db = Barebonedb.getAppDatabase(context = this)
        dao = db?.userDao()

        val userId = intent.getIntExtra("userId", -9999999)
        val title = findViewById<TextView>(R.id.aeTittle)
        val icon = findViewById<ImageView>(R.id.aeImage)
        val name = findViewById<TextInputEditText>(R.id.aeName)
        val mobile = findViewById<TextInputEditText>(R.id.aeMobileNumber)

        val button = findViewById<Button>(R.id.aeButton)

//        Log.d("Edit","${intent.getStringExtra("userId")}")
        if (userId == null || userId == -9999999) {
            button.setOnClickListener {
                if (!name.text.isNullOrBlank() && !mobile.text.isNullOrBlank()) {
                    var user =
                        ModelUser(name = name.text.toString(), mobile = mobile.text.toString())

                    dao?.write(user)
                    Toast.makeText(this, "Added", Toast.LENGTH_LONG).show()
                    finish()
                    startActivity(Intent(this, Dashboard::class.java))
                } else {
                    Toast.makeText(this, "Invalid fields", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            title.text = "Edit Contact"
            button.text = "SAVE"
            icon.setImageLevel(R.drawable.ic_person)

            var user = dao?.findUser(userId)
            if (user != null) {
                name.setText(user.name)
                mobile.setText(user.mobile)
            }
            button.setOnClickListener {
                if (!name.text.isNullOrBlank() && !mobile.text.isNullOrBlank() && user != null) {
                    user.name = name.text.toString()
                    user.mobile = mobile.text.toString()
                    dao?.write(user)

                    Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()

                    finish()
                    startActivity(Intent(this, Dashboard::class.java))
                } else {
                    Toast.makeText(this, "Invalid fields", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}