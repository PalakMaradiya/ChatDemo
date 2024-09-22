package com.example.chatdemo.activity

import android.media.MediaPlayer
import android.net.Uri
import android.net.Uri.parse
import android.os.Bundle
import android.widget.MediaController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatdemo.R
import com.example.chatdemo.databinding.ActivityVideoPlayBinding


class VideoPlayActivity : AppCompatActivity() {

    lateinit var binding : ActivityVideoPlayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {


        if(intent!=null){

            val videoUrl = intent.getStringExtra("videoUrl")
//            val thumbnaill = intent.getStringExtra("thumbnail")


//            Glide.with(this@VideoPlayActivity).load(thumbnaill).into(binding.imgThumbnail)

            val mediaController = MediaController(this)

            binding.videoView.setOnClickListener {
                mediaController.setAnchorView(binding.videoView)
                val uri:Uri = parse(videoUrl)
                binding.videoView.setMediaController(mediaController)
                binding.videoView.setVideoURI(uri)
                binding.videoView.requestFocus()
                binding.videoView.start()


            }
        }
    }


}