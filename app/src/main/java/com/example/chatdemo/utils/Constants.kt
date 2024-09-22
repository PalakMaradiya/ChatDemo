package com.example.chatdemo.utils

import android.content.Context
import android.widget.Toast

class Constants {

    companion object {
        fun displayToast(s: String , context : Context) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
        }
    }
}