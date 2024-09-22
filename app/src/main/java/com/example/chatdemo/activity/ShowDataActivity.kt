package com.example.chatdemo.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatdemo.R
import com.example.chatdemo.adapter.UserAdapter
import com.example.chatdemo.databinding.ActivityShowDataBinding
import com.example.chatdemo.utils.UserModal
import com.google.firebase.firestore.FirebaseFirestore

class ShowDataActivity : AppCompatActivity() {


    lateinit var binding: ActivityShowDataBinding
    val db = FirebaseFirestore.getInstance()
    lateinit var userModal: UserModal
    lateinit var adapter: UserAdapter
    lateinit var manager: LinearLayoutManager

    var userList = ArrayList<UserModal>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityShowDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {



    }


}





