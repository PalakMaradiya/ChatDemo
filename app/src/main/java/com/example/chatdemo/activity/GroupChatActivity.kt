package com.example.chatdemo.activity

import GroupAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatdemo.R
import com.example.chatdemo.databinding.ActivityGroupChatBinding
import com.example.chatdemo.utils.GroupMessageModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GroupChatActivity : AppCompatActivity() {

    lateinit var grpAdapter: GroupAdapter
    lateinit var name: String
    lateinit var Groupimges: String
    lateinit var  groupId :String
   // = FirebaseDatabase.getInstance().reference.child("groups").push().key!!
    var groupList = ArrayList<GroupMessageModal>()
    val map = hashMapOf<String, String>()
    lateinit var manager: LinearLayoutManager
    val senderUid = FirebaseAuth.getInstance().currentUser?.uid
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_VIDEO_REQUEST = 100
    var link: String = ""

    lateinit var binding: ActivityGroupChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        groupId = intent.getStringExtra("grpID").toString()
        initview()
        fetchMessages()



        Log.e("TAG", "GRPID: " + groupId)

    }

    private fun initview() {


        if (intent != null) {
            name = intent.getStringExtra("groupName").toString()
            binding.groupName.text = name
            Groupimges = intent.getStringExtra("groupImageUrl").toString()
            Log.e("TAG", "Groupimges: "+Groupimges )
            Glide.with(this@GroupChatActivity).load(Groupimges).into(binding.groupImage)
        }





        binding.imgSend.setOnClickListener {
            val message = binding.edtMsg.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message, "", "")
                binding.edtMsg.text.clear()
            } else {
                Toast.makeText(this, "Please Enter Your Message", Toast.LENGTH_SHORT).show()
            }
        }

        binding.toolbar.setOnClickListener {


            val i = Intent(this@GroupChatActivity, GroupInfoActivity::class.java)
            i.putExtra("groupName", name)
            i.putExtra("groupImageUrl", Groupimges)

            Log.e("==>", "initview: "+Groupimges )
            startActivity(i)
        }

        binding.images.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST
            )
        }



        binding.imgVideo.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("video/*")
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST)
        }
    }


    private fun fetchMessages() {

        val databaseReference = FirebaseDatabase.getInstance().reference.child("groups").child(groupId!!)
                .child("messages")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot?.getValue(GroupMessageModal::class.java)
                    if (message != null) {
                        groupList.add(message)
                        //Log.d("==>", "Received message:"+message)

                    }



                    grpAdapter = GroupAdapter(this@GroupChatActivity, groupList,

                        onVideoPlay = { videoUrl, thumbnail ->

                            val i = Intent(this@GroupChatActivity, VideoPlayActivity::class.java)
                            i.putExtra("videoUrl", videoUrl)
                            i.putExtra("thumbnail", thumbnail)
                            startActivity(i)

                        }, onItemclick = { message ->

                            val i = Intent(this@GroupChatActivity, WebViewActivity::class.java)
                            i.putExtra("message", message)
                            i.putExtra("link", link)
                            startActivity(i)

                        })
                    manager = LinearLayoutManager(
                        this@GroupChatActivity, LinearLayoutManager.VERTICAL, false
                    )
                    binding.rcv.layoutManager = manager
                    binding.rcv.adapter = grpAdapter
                    grpAdapter.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GroupChatActivity", "Failed to fetch messages: ${error.message}")
            }
        })
    }

    private fun sendMessage(message: String, imageUrl: String, videoUrl: String) {

        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date())

        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("groups").child(groupId)
                .child("messages")
        val messageId = databaseReference.push().key ?: return

        val message = GroupMessageModal(
            senderId = senderUid ?: "",
            senderName = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown",
            message = message,
            imageUrl = imageUrl,
            videoUrl = videoUrl,
            videoThumbnailUrl = videoUrl,
            currentTime = currentTime,
            currentDate = currentDate)

        databaseReference.child(messageId).setValue(message).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                 /*  Log.d("==> Data", "Message sent successfully")*/
            } else {
                Toast.makeText(this@GroupChatActivity, "Failed to send message", Toast.LENGTH_SHORT).show()

            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    val imageUri: Uri = data.data!!
                    val imageUrl = imageUri.toString()
                    sendMessage("", imageUrl, "")
                }
            }

            PICK_VIDEO_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    val videoUri: Uri = data.data!!
                    val videoUrl = videoUri.toString()
                    sendMessage("", "", videoUrl)

                }
            }
        }
    }


}
