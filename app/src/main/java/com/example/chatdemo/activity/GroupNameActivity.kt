package com.example.chatdemo.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatdemo.R
import com.example.chatdemo.databinding.ActivityGroupNameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class GroupNameActivity : AppCompatActivity() {

    lateinit var name: String
    lateinit var Groupimges: String
    private val PICK_IMAGE_REQUEST = 1
    lateinit var binding: ActivityGroupNameBinding
    private var selectedImageUri: Uri? = null
    val map = hashMapOf<String, String>()
    val senderUid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGroupNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView() {


        binding.img.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST
            )
        }



        binding.btnDone.setOnClickListener {
            name = binding.edtName.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            val groupId = FirebaseDatabase.getInstance().reference.child("groups").push().key
            if (groupId != null) {
                val groupData = HashMap<String, Any>()
                groupData["groupName"] = name
                groupData["imageUrl"] = selectedImageUri?.toString() ?: ""
                groupData["groupID"] = groupId

                FirebaseDatabase.getInstance().reference.child("groups").child(groupId)
                    .setValue(groupData).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Group created successfully", Toast.LENGTH_SHORT)
                                .show()
                            val i = Intent(this@GroupNameActivity, GroupChatActivity::class.java)
                            i.putExtra("groupId", groupId)
                            Log.e("TAG", "initView: " + groupId)
                            i.putExtra("groupName", name)
                            i.putExtra("groupImageUrl", selectedImageUri?.toString())
                            startActivity(i)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to create group: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                val uri = data?.data
                if (uri != null) {
                    selectedImageUri = uri
                    setImage(selectedImageUri)
                }
            }
        }
    }

    private fun setImage(uri: Uri?) {
        binding.img.setImageURI(uri)
    }


}