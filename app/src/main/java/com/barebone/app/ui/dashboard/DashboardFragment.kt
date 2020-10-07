package com.barebone.app.ui.dashboard

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barebone.app.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "userId"
private const val ARG_PARAM2 = "param2"

class DashboardFragment : Fragment() {

    var db: Barebonedb? = null
    var dao: UserDao? = null
    var userList: ArrayList<ModelUser> = ArrayList()
    lateinit var root: View
    lateinit var adapter: UserListAdapter

    private lateinit var dashboardViewModel: DashboardViewModel

    // TODO: Rename and change types of parameters
    private var param1: Int? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        var persistantlogin =
            root.context.getSharedPreferences(root.context.packageName, Context.MODE_PRIVATE)
        Log.d("sp", persistantlogin.getString("email", "null"))
//        setContentView(R.layout.activity_dashboard)
        var sp = root.context.getSharedPreferences(
            "${root.context.packageName}.${persistantlogin.getString("email", "null")}",
            Context.MODE_PRIVATE
        )
        Log.d("sp", sp.getString("name", "null"))
        var name = sp.getString("name", "null")

        root.findViewById<MaterialTextView>(R.id.userName).text = name


        db = Barebonedb.getAppDatabase(context = root.context)
        dao = db?.userDao()

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


        var recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)
        if (userList.isEmpty()) {
            recyclerView.visibility = View.GONE
            var newTextView: TextView = root.findViewById(R.id.newtextview)
            newTextView.visibility = View.VISIBLE

        } else {

            recyclerView.visibility = View.VISIBLE
            var newTextView: TextView = root.findViewById(R.id.newtextview)
            newTextView.visibility = View.GONE
            adapter = UserListAdapter(this)

            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(root.context)
        }


        root.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
//            root.findNavController().navigate(R.id.addEditFragment)
            var newEntryIntent = Intent(root.context, AddEdit::class.java)
            startActivityForResult(newEntryIntent, 1)

        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("Res_Act", requestCode.toString())
        userList.clear()
        dao?.getUsers()?.forEach { e ->
            run {
                Log.d("obs", e.name)
                userList.add(e)
            }
        }
        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
        super.onActivityResult(requestCode, resultCode, data)
    }

}

class UserListAdapter(private val appContext: DashboardFragment) :
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


//            val addEdit = AddEditFragment()
//            appContext.userList[position].id?.let { it1 ->
//                addEdit.arguments?.putInt(
//                    "userId",
//                    it1
//                )
//            }
//            var bundle = bundleOf("userId" to appContext.userList[position].id)
//            Navigation.findNavController(appContext.root).navigate(R.id.addEditFragment, bundle)

            var editIntent = Intent(appContext.root.context, AddEdit::class.java)
            editIntent.putExtra("userId", appContext.userList[position].id)
            appContext.startActivityForResult(editIntent, 1)
        }
        val delete = viewHolder.deleteButton
        delete.setOnClickListener {


            val builder = AlertDialog.Builder(appContext.root.context)
            //set title for alert dialog
            builder.setTitle(R.string.dialogTitle)
            //set message for alert dialog
            builder.setMessage(appContext.root.context.getString(R.string.confirmToDelete) + name.text)
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                run {
                    Toast.makeText(
                        appContext.root.context,
                        "contact deleted",
                        Toast.LENGTH_LONG
                    )
                        .show()

                    appContext.dao?.delete(appContext.userList[position])
                    appContext.userList.removeAt(position)
                    this.notifyDataSetChanged()
                    if (appContext.userList.isEmpty()) {
                        appContext.recyclerView.visibility = View.GONE
                        var newTextView: TextView = appContext.root.findViewById(R.id.newtextview)
                        newTextView.visibility = View.VISIBLE

                    } else {
                        appContext.recyclerView.visibility = View.VISIBLE
                        var newTextView: TextView = appContext.root.findViewById(R.id.newtextview)
                        newTextView.visibility = View.GONE

                    }
                }
            }
            //performing negative action
            builder.setNegativeButton("No") { dialogInterface, which ->
                Toast.makeText(appContext.root.context, "cancel", Toast.LENGTH_LONG).show()
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