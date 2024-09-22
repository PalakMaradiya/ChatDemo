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
import com.example.chatdemo.databinding.ActivityMainBinding
import com.example.chatdemo.utils.Constants
import com.example.chatdemo.utils.UserModal
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var firebaseAuth: FirebaseAuth

    var userList = ArrayList<UserModal>()
    val reference = FirebaseDatabase.getInstance().reference


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {


        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("706617078841-tujdsm7m03g936b8kbcacim0rd25fhlc.apps.googleusercontent.com")
            .requestEmail().build()


        googleSignInClient = GoogleSignIn.getClient(this@MainActivity, googleSignInOptions)
        binding.btnClick.setOnClickListener { // Initialize sign in intent
            val intent: Intent = googleSignInClient.signInIntent

            startActivityForResult(intent, 100)
        }


        firebaseAuth = FirebaseAuth.getInstance()

        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

        if (firebaseUser != null) {


            startActivity(
                Intent(
                    this@MainActivity, GoogleLoginActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 100) {
            val signInAccountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            if (signInAccountTask.isSuccessful) {
//                val s = "Google sign in successful"
//                Constants.displayToast(s, this)

                try {
                    val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                    if (googleSignInAccount != null) {
                        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(
                            googleSignInAccount.idToken, null
                        )
                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {

                                    Toast.makeText(this, "Login SuccessFully", Toast.LENGTH_SHORT)
                                        .show()


                                    // add data into user table in firestore


                                    val email = googleSignInAccount.email
                                    val firstName = googleSignInAccount.displayName
                                    val img = googleSignInAccount.photoUrl
                                    val userId = firebaseAuth.currentUser!!.uid;


//                                    Log.e("TAG", "name:"+googleSignInAccount.email )


                                    val key = reference.root.child("users").push().key ?: ""


//
                                    // Check if user with the same first name already exists
                                    reference.child("users").orderByChild("email").equalTo(email)
                                        .addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    // User with the same name already exists
                                                    startActivity(
                                                        Intent(
                                                            this@MainActivity,
                                                            GoogleLoginActivity::class.java
                                                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    )
                                                    //Toast.makeText(this@MainActivity, "User already exists", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    // User with this name doesn't exist, proceed to add the user
                                                    val key =
                                                        reference.child("users").push().key ?: ""
                                                    val usermodal = UserModal(firstName, email, userId, img)
                                                    reference.child("users").child(key)
                                                        .setValue(usermodal).addOnCompleteListener {
                                                            if (it.isSuccessful) {
                                                                Toast.makeText(
                                                                    this@MainActivity,
                                                                    "Data Added Successfully",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                startActivity(
                                                                    Intent(
                                                                        this@MainActivity,
                                                                        GoogleLoginActivity::class.java
                                                                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                )
                                                            }
                                                        }.addOnFailureListener {
                                                            Log.e("TAG", "Error: $it")
                                                        }
                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                Log.e(
                                                    "TAG",
                                                    "onCancelled",
                                                    databaseError.toException()
                                                )
                                            }
                                        })

                                } else {
                                    Constants.displayToast(
                                        "Authentication Failed :" + task.exception!!.message, this
                                    )

                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }


}


