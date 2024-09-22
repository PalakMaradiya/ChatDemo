package com.example.chatdemo.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatdemo.R
import com.example.chatdemo.adapter.ChatAdapter
import com.example.chatdemo.adapter.GroupListAdapter
import com.example.chatdemo.adapter.UserAdapter
import com.example.chatdemo.databinding.ActivityGoogleLoginBinding
import com.example.chatdemo.utils.ChatModal
import com.example.chatdemo.utils.GroupModal
import com.example.chatdemo.utils.UserModal
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GoogleLoginActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    val reference = FirebaseDatabase.getInstance().reference
    lateinit var grpAdapter: GroupListAdapter
    var userList = ArrayList<UserModal>()
    var groupUserlist = ArrayList<GroupModal>()
    lateinit var adapter: UserAdapter
    lateinit var manager: LinearLayoutManager
    val uniqueEmails = HashSet<String>()
    var link: String = ""

    lateinit var binding: ActivityGoogleLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGoogleLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {
        binding.btnCreteGroup.setOnClickListener {
            val i = Intent(this@GoogleLoginActivity, SelectGroupActivity::class.java)
            startActivity(i)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            Glide.with(this@GoogleLoginActivity).load(firebaseUser.photoUrl).into(binding.userImage)
            binding.userName.text = firebaseUser.displayName
        }

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Application is Loading")
        progressDialog.show()

        // Load user data
        reference.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
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

                adapter =
                    UserAdapter(this@GoogleLoginActivity, userList) { name, userId, img, email ->
                        val i = Intent(this@GoogleLoginActivity, ChatActivity::class.java)
                        i.putExtra("name", name)
                        i.putExtra("userId", userId)
                        i.putExtra("email", email)
                        i.putExtra("img", img)
                        startActivity(i)
                    }
                manager = LinearLayoutManager(
                    this@GoogleLoginActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                binding.rcvUser.layoutManager = manager
                binding.rcvUser.adapter = adapter
                adapter.notifyDataSetChanged()

                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(this@GoogleLoginActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

//        // Load group data
        reference.child("groups").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupUserlist.clear()
                for (child in snapshot.children) {
                    val groupData: GroupModal? = child.getValue(GroupModal::class.java)
                    if (groupData != null) {
                        groupUserlist.add(groupData)
                    }
                }

                // Initialize and set the group adapter
                grpAdapter = GroupListAdapter(
                    this@GoogleLoginActivity,
                    groupUserlist,
                    onItemClick = { grpname, grpimg ,grpID->

                        val i = Intent(this@GoogleLoginActivity, GroupChatActivity::class.java)
                        i.putExtra("groupName", grpname)
                        i.putExtra("groupImageUrl", grpimg)
                        i.putExtra("grpID", grpID)
                        startActivity(i)

                    })
                binding.rcvGroup.layoutManager = LinearLayoutManager(
                    this@GoogleLoginActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                binding.rcvGroup.adapter = grpAdapter
                grpAdapter.notifyDataSetChanged()

                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(this@GoogleLoginActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })


        googleSignInClient =
            GoogleSignIn.getClient(this@GoogleLoginActivity, GoogleSignInOptions.DEFAULT_SIGN_IN)
        binding.btnLogout.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.signOut()
                    Toast.makeText(applicationContext, "Logout successful", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }
    }


}
