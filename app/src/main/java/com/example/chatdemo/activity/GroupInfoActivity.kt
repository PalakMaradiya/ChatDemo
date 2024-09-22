package com.example.chatdemo.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatdemo.R
import com.example.chatdemo.adapter.UserAdapter
import com.example.chatdemo.databinding.ActivityGroupInfoBinding
import com.example.chatdemo.utils.UserModal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupInfoActivity : AppCompatActivity() {

    val reference = FirebaseDatabase.getInstance().reference

    private var groupName: String? = null
    private var groupImage: String = ""
    var userList = ArrayList<UserModal>()
    lateinit var userAdapter: UserAdapter
    lateinit var manager: LinearLayoutManager
    val uniqueEmails = HashSet<String>()
    lateinit var binding : ActivityGroupInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {

        if (intent != null) {
            groupName = intent.getStringExtra("groupName").toString()
            groupImage = intent.getStringExtra("groupImageUrl").toString()
            binding.txtGroupName.text = groupName
            Glide.with(this@GroupInfoActivity).load(groupImage).into(binding.imgGroup)

            Log.e("TAG", "GRPIMG: "+groupImage)
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Application is Loading")
        progressDialog.show()

        reference.root.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val userData: UserModal? = child.getValue(UserModal::class.java)
                    if (userData != null) {
                        userList.add(userData)
                    }
                }

                binding.txtMembers.text = userList.size.toString()
                progressDialog.dismiss()
                userAdapter = UserAdapter(
                    this@GroupInfoActivity,
                    userList,
                    onItemClick = { name, userId, img, email ->
                    })

                manager = LinearLayoutManager(
                    this@GroupInfoActivity, LinearLayoutManager.VERTICAL, false
                )
                binding.rcv.layoutManager = manager
                binding.rcv.adapter = userAdapter
                userAdapter.notifyDataSetChanged()
                userAdapter.reorderList()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}