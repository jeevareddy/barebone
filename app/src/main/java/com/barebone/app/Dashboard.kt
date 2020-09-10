package com.barebone.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*
import kotlin.collections.ArrayList

class Dashboard : AppCompatActivity() {
    var db: Barebonedb? = null
    var dao: UserDao? = null
    var userList: ArrayList<ModelUser> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var persistantlogin = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        Log.d("sp", persistantlogin.getString("email", "null"))
        setContentView(R.layout.activity_dashboard)
        var sp = getSharedPreferences(
            "${packageName}.${persistantlogin.getString("email", "null")}",
            Context.MODE_PRIVATE
        )
        Log.d("sp", sp.getString("name", "null"))
        var name = sp.getString("name", "null")

        findViewById<MaterialTextView>(R.id.userName).text = name


        db = Barebonedb.getAppDatabase(context = this)
        dao = db?.userDao()

        val user = ModelUser(name = "User 1", mobile = "1234567")
        val observable = Observable()

        observable.run {
            userList.clear()
            dao?.getUsers()?.forEach { e ->
                run {
                    Log.d("obs", e.name)
                    userList.add(e)
                }
            }

        }


        var recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        if (userList.isEmpty()) {
            recyclerView.visibility = View.GONE
            var newTextView: TextView = findViewById(R.id.newtextview)
            newTextView.visibility = View.VISIBLE

        } else {

            recyclerView.visibility = View.VISIBLE
            var newTextView: TextView = findViewById(R.id.newtextview)
            newTextView.visibility = View.GONE
            var adapter = UserListAdapter(this)

            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }



        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {

            var newEntryIntent = Intent(this, AddEdit::class.java)
            startActivity(newEntryIntent)
        }


    }
}


class UserListAdapter(private val appContext: Dashboard) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    class ViewHolder(listView: View) : RecyclerView.ViewHolder(listView) {
        val nameTextView = itemView.findViewById<MaterialTextView>(R.id.view_name)
        val mobileTextView = itemView.findViewById<MaterialTextView>(R.id.view_mobile)
        val editButton = itemView.findViewById<ImageButton>(R.id.view_button_edit)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.view_button_delete)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.view_card, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val user: ModelUser = appContext.userList[position]
        // Set item views based on your views and data model
        val name = viewHolder.nameTextView
        name.text = user.name
        val mobile = viewHolder.mobileTextView
        mobile.text = user.mobile
        val edit = viewHolder.editButton
        edit.setOnClickListener {
            var editIntent = Intent(appContext.applicationContext, AddEdit::class.java)
            editIntent.putExtra("userId", appContext.userList[position].id)
            appContext.startActivity(editIntent)
        }
        val delete = viewHolder.deleteButton
        delete.setOnClickListener {


            val builder = AlertDialog.Builder(appContext)
            //set title for alert dialog
            builder.setTitle(R.string.dialogTitle)
            //set message for alert dialog
            builder.setMessage("Confirm to delete ${name.text}")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                run {
                    Toast.makeText(
                        appContext.applicationContext,
                        "contact deleted",
                        Toast.LENGTH_LONG
                    )
                        .show()

                    appContext.dao?.delete(appContext.userList[position])
                    appContext.userList.removeAt(position)
                    this.notifyDataSetChanged()
                    if (appContext.userList.isEmpty()) {
                        appContext.recyclerView.visibility = View.GONE
                        var newTextView: TextView = appContext.findViewById(R.id.newtextview)
                        newTextView.visibility = View.VISIBLE

                    } else {
                        appContext.recyclerView.visibility = View.VISIBLE
                        var newTextView: TextView = appContext.findViewById(R.id.newtextview)
                        newTextView.visibility = View.GONE

                    }
                }
            }
            //performing negative action
            builder.setNegativeButton("No") { dialogInterface, which ->
                Toast.makeText(appContext.applicationContext, "cancel", Toast.LENGTH_LONG).show()
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()

        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return appContext.userList.size
    }
}