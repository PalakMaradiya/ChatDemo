package com.example.chatdemo.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatdemo.R
import com.example.chatdemo.databinding.ActivityAddDataBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddDataActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        initView()
    }

    private fun initView() {
        binding.saveButton.setOnClickListener {
            val firstName = binding.inputFirstName.text.toString()
            val lastName = binding.inputLastName.text.toString()


            saveFireStore(firstName, lastName)

            Log.e("==>", "initView: "+firstName)

        }
        readFireStoreData()

        binding.btnNext.setOnClickListener {

            var i =Intent(this@AddDataActivity , ShowDataActivity :: class.java)
            startActivity(i)
        }
    }

    private fun saveFireStore(firstName: String, lastName: String) {
        val db = FirebaseFirestore.getInstance()
        val user: MutableMap<String, Any> = HashMap()
        user["firstName"] = firstName
        user["lastName"] = lastName

        Log.e("==>", "name: "+firstName)
        Log.e("==>", "lastName: " +lastName)

        db.collection("users")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(this@AddDataActivity, "record added successfully ", Toast.LENGTH_SHORT ).show()
            }
            .addOnFailureListener{ e ->

               // Log.e("TAG", "saveFireStore: "+e.toString() )
                Toast.makeText(this@AddDataActivity, "record Failed to add "+e, Toast.LENGTH_SHORT ).show()
            }
        readFireStoreData()
    }

    private fun readFireStoreData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnCompleteListener {

                val result: StringBuffer = StringBuffer()

                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        result.append(document.data.getValue("firstName")).append(" ")
                            .append(document.data.getValue("lastName")).append("\n\n")
                    }
                    binding.textViewResult.setText(result)
                }
            }
    }

   
}
