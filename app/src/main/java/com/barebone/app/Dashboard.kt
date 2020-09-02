package com.barebone.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView

class Dashboard : AppCompatActivity() {
//    var name : String
//        get() {
//           return name
//        }
//        set(value) {
//            this.name = value
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        var name = intent.getStringExtra("name").toString()
        Log.d("Dashboard", name)
        findViewById<MaterialTextView>(R.id.userName).text = name
    }
}