package com.barebone.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import java.util.*

class SettingsFragment : Fragment() {

//    lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view = inflater.inflate(R.layout.fragment_settings, container, false)

        var sp = view.context.getSharedPreferences(view.context.packageName, Context.MODE_PRIVATE)

        val dropdown = view.findViewById<AppCompatSpinner>(R.id.languageSpinner)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.languageList,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dropdown.adapter = adapter
            var loc = sp.getString("locale", "en")
            if (loc == "en")
                dropdown.setSelection(0)
            else dropdown.setSelection(1)
        }
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                when (position) {
                    0 -> setLocale("en", view)
                    1 -> setLocale("ar", view)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }




        return view
    }

    lateinit var locale: Locale

    private fun setLocale(localeName: String, view: View) {

        var sp = view.context.getSharedPreferences(view.context.packageName, Context.MODE_PRIVATE)
        if (sp.getString("locale", "en") != localeName) {
            sp.edit {
                putString("locale", localeName)
            }


            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = locale
            res.updateConfiguration(conf, dm)
//        getFragmentManager()?.beginTransaction()?.detach(this)?.attach(this)?.commit()

            val refresh = Intent(
                view.context,
                BottomNav::class.java
            )

            startActivity(refresh)
        }
    }
}