package com.example.chatdemo.activity

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
import com.example.chatdemo.adapter.ChatAdapter
import com.example.chatdemo.databinding.ActivityChatBinding
import com.example.chatdemo.utils.ChatModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChatActivity : AppCompatActivity() {

    lateinit var chatAdapter: ChatAdapter
    var chatList = ArrayList<ChatModal>()
    val map = hashMapOf<String, String>()
    lateinit var manager: LinearLayoutManager
    var chatId: String? = null
    var reverseChatId: String? = null
    var receiverUid: String? = null
    var receiverId: String? = null
    val senderUid = FirebaseAuth.getInstance().currentUser?.uid
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_VIDEO_REQUEST = 100
    var link: String = ""

    lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        receiverUid = intent.getStringExtra("userId")
        receiverId = intent.getStringExtra("email")
        chatId = senderUid + receiverUid
        reverseChatId = receiverUid + senderUid

        initView()
    }

    private fun initView() {

        if (intent != null) {
            val name = intent.getStringExtra("name")
            val img = intent.getStringExtra("img")
            binding.userName.text = name
            Glide.with(this@ChatActivity).load(img).into(binding.userImage)
        }



        verfyChatId()
        binding.imgSend.setOnClickListener {

            link = binding.edtMsg.text.toString()

            if (binding.edtMsg.text.isEmpty()) {
                Toast.makeText(this, "Please Enter Your Message", Toast.LENGTH_SHORT).show()
            } else {
                storeData(binding.edtMsg.text.toString(), null, null)
            }
        }

        binding.images.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
            )
        }

        binding.imgVideo.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("video/*")
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    val imageUri: Uri = data.data!!
                    val imageUrl = imageUri.toString()
                    storeData("", imageUrl, "")
                }
            }

            PICK_VIDEO_REQUEST -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    val videoUri: Uri = data.data!!
                    val videoUrl = videoUri.toString()
                    storeData("", "", videoUrl)

                }
            }
        }
    }

    private fun verfyChatId() {
        val reference = FirebaseDatabase.getInstance().getReference("chats")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(chatId!!)) {
                    getData(chatId!!)
                } else if (snapshot.hasChild(reverseChatId!!)) {

                    chatId = reverseChatId
                    getData(chatId!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getData(chatId: String) {
        FirebaseDatabase.getInstance().getReference("chats")
            .child(chatId!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatList.clear()
                    for (show in snapshot.children) {
                        chatList.add(show.getValue(ChatModal::class.java)!!)
                    }
                    chatAdapter = ChatAdapter(
                        this@ChatActivity,
                        chatList,
                        onVideoPlay = { videoUrl, thumbnail ->

                            val i = Intent(this@ChatActivity, VideoPlayActivity::class.java)
                            i.putExtra("videoUrl", videoUrl)
                            i.putExtra("thumbnail", thumbnail)
                            startActivity(i)
                        },
                        onItemclick = { message ->

                            val i = Intent(this@ChatActivity, WebViewActivity::class.java)
                            i.putExtra("message", message)
                            i.putExtra("link",link)
                            startActivity(i)

                        })
                    manager =
                        LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
                    binding.rcv.layoutManager = manager
                    binding.rcv.adapter = chatAdapter
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, error.message, Toast.LENGTH_SHORT).show()
                }

            })

    }


    private fun storeData(msg: String, imageUrl: String?, videoUrl: String?) {
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date())

        map["currentTime"] = currentTime
        map["currentDate"] = currentDate
        map["message"] = msg
        map["senderId"] = senderUid!!
        map["receiverId"] = receiverId!!

        if (imageUrl != null) {
            map["imageUrl"] = imageUrl
        }

        if (videoUrl != null) {
            map["videoUrl"] = videoUrl
            map["videoThumbnailUrl"] = videoUrl
        }



        val reference = FirebaseDatabase.getInstance().getReference("chats").child(chatId!!)
        reference.push().setValue(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    binding.edtMsg.setText("")
                    getData(chatId!!)

                } else {
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }


}




