package com.example.chatdemo.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatdemo.R
import com.example.chatdemo.adapter.UserAdapter
import com.example.chatdemo.databinding.ActivitySelectGroupBinding
import com.example.chatdemo.utils.UserModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SelectGroupActivity : AppCompatActivity() {

    val reference = FirebaseDatabase.getInstance().reference
    var userList = ArrayList<UserModal>()
    lateinit var adapter: UserAdapter
    lateinit var manager: LinearLayoutManager
    val uniqueEmails = HashSet<String>()
    lateinit var binding: ActivitySelectGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {

        binding.imgBack.setOnClickListener {

            onBackPressed()
        }

        binding.btnNext.setOnClickListener {

            val i = Intent(this@SelectGroupActivity,GroupNameActivity::class.java)
            startActivity(i)
            finish()

        }


        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Application is Loading")
        progressDialog.show()

        reference.root.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

                for (child in snapshot.children) {
                    val userData: UserModal? = child.getValue(UserModal::class.java)
                    if (userData != null && userData.email != currentUserEmail && uniqueEmails.add(
                            userData.email
                        )
                    ) {
                        userList.add(userData)
                    }
                }


                progressDialog.dismiss()
                adapter = UserAdapter(
                    this@SelectGroupActivity,
                    userList,
                    onItemClick = { name, userId, img, email ->




                    })
                manager = LinearLayoutManager(
                    this@SelectGroupActivity, LinearLayoutManager.VERTICAL, false
                )
                binding.rcv.layoutManager = manager
                binding.rcv.adapter = adapter
                adapter.notifyDataSetChanged()


            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")


            }

        })


    }


}