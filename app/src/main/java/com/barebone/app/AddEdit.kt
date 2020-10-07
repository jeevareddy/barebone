package com.barebone.app

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.iid.FirebaseInstanceId

class AddEdit : AppCompatActivity() {
    var db: Barebonedb? = null
    var dao: UserDao? = null

    private lateinit var mFunctions: FirebaseFunctions
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_add__edit)


        mFunctions = FirebaseFunctions.getInstance()
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener OnCompleteListener@{ task ->
            if (!task.isSuccessful) {
                Log.w("Firebase FC", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }
            // 3
            token = task.result?.token.toString()

            // 4
            Log.d("FCM Token", token)

        }


        db = Barebonedb.getAppDatabase(context = this)
        dao = db?.userDao()

        val userId = intent.getIntExtra("userId", -9999999)
        val title = findViewById<TextView>(R.id.aeTittle)
        val icon = findViewById<ImageView>(R.id.aeImage)
        val name = findViewById<TextInputEditText>(R.id.aeName)
        val mobile = findViewById<TextInputEditText>(R.id.aeMobileNumber)
        val backButton = findViewById<ImageButton>(R.id.back)
        val button = findViewById<Button>(R.id.aeButton)

        backButton.setOnClickListener {
            setResult(1)
            finish()
        }

//        Log.d("Edit","${intent.getStringExtra("userId")}")
        if (userId == null || userId == -9999999) {
            button.setOnClickListener {
                if (!name.text.isNullOrBlank() && !mobile.text.isNullOrBlank()) {
                    var user =
                        ModelUser(name = name.text.toString(), mobile = mobile.text.toString())

                    dao?.write(user)
                    Toast.makeText(this, "Added", Toast.LENGTH_LONG).show()
                    setResult(1)
                    finish()
//                    startActivity(Intent(this, Dashboard::class.java))
                } else {
                    Toast.makeText(this, "Invalid fields", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            title.text = getString(R.string.editContact)
            button.text = getString(R.string.save)
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_person))
//            icon.setBackgroundResource(R.drawable.ic_person)

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

                    addMessage("${user.name} has been updated", token).addOnCompleteListener(
                        OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                val e = task.exception
                                if (e is FirebaseFunctionsException) {
                                    val code = e.code
                                    val details = e.details
                                }

                                // [START_EXCLUDE]
                                Log.w("Firebase FC", "addMessage:onFailure", e)
                                Toast.makeText(
                                    this,
                                    "An error occurred.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@OnCompleteListener
                            }
                        })

                    setResult(1)
                    finish()
//                    startActivity(Intent(this, Dashboard::class.java))
                } else {
                    Toast.makeText(this, "Invalid fields", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun addMessage(text: String, token: String): Task<Any> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "title" to "Contact Updated",
            "body" to text,
            "token" to token,
            "push" to true
        )

        return mFunctions
            ?.getHttpsCallable("helloWorld")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data
                result
            }
    }
}