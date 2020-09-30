package com.barebone.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.iid.FirebaseInstanceId


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "userId"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Int? = null
    private var param2: String? = null


    private lateinit var mFunctions: FirebaseFunctions
    private lateinit var token: String

    var db: Barebonedb? = null
    var dao: UserDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_add_edit, container, false)
        db = Barebonedb.getAppDatabase(context = view.context)
        dao = db?.userDao()

        val userId = param1//intent.getIntExtra("userId", -9999999)
        val title = view.findViewById<TextView>(R.id.aeTittle)
        val icon = view.findViewById<ImageView>(R.id.aeImage)
        val name = view.findViewById<TextInputEditText>(R.id.aeName)
        val mobile = view.findViewById<TextInputEditText>(R.id.aeMobileNumber)

        val button = view.findViewById<Button>(R.id.aeButton)

//        Log.d("Edit","${intent.getStringExtra("userId")}")
        if (userId == null || userId == -9999999) {
            button.setOnClickListener {
                if (!name.text.isNullOrBlank() && !mobile.text.isNullOrBlank()) {
                    var user =
                        ModelUser(name = name.text.toString(), mobile = mobile.text.toString())

                    dao?.write(user)

                    Toast.makeText(view.context, "Added", Toast.LENGTH_LONG).show()
                    view.findNavController().navigate(R.id.navigation_dashboard)
//                    startActivity(Intent(this, Dashboard::class.java))
                } else {
                    Toast.makeText(view.context, "Invalid fields", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            title.text = "Edit Contact"
            button.text = "SAVE"
//            icon.setImageLevel(R.drawable.ic_person)
            icon.setImageResource(R.drawable.ic_person)
//            view.findViewById<Toolbar>(R.id.toolbar).title="Edit Contact"

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

                    Toast.makeText(view.context, "Saved", Toast.LENGTH_LONG).show()
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
                                    view.context,
                                    "An error occurred.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@OnCompleteListener
                            }
                        })

                    view.findNavController().navigate(R.id.navigation_dashboard)
//                    startActivity(Intent(this, Dashboard::class.java))
                } else {
                    Toast.makeText(view.context, "Invalid fields", Toast.LENGTH_LONG).show()
                }
            }
        }
        return view
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
            ?.call(data)
            .continueWith { task ->
                val result = task.result?.data
                result
            }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment addEditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}