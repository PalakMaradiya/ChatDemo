package com.example.chatdemo.activity

import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatdemo.R
import com.example.chatdemo.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWebViewBinding.inflate(layoutInflater)
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

            val msg = intent.getStringExtra("message").toString()
            val link = intent.getStringExtra("link").toString()


            Log.e("==>", "Received link:"+link)


            if (link.isNotEmpty()) {
                binding.webView.webViewClient = WebViewClient()
                binding.webView.loadUrl(link)
                binding.webView.settings.javaScriptEnabled = true
                binding.webView.settings.setSupportZoom(true)

            } else {
                Toast.makeText(this@WebViewActivity, "Invild Link", Toast.LENGTH_SHORT).show()
                Log.e("==>", "Invalid link:"+link)
            }


        }


    }



}