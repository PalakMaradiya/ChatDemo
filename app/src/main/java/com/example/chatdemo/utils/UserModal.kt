package com.example.chatdemo.utils

import android.net.Uri

class UserModal {



    var email: String = ""
    var firstName: String = ""
    var userId: String = ""
    var img: String = ""
    var isSelected: Boolean = false


    constructor()
    {

    }



    constructor(firstName: String?, email: String?, userId: String?, img: Uri?)
    {
        this.firstName = firstName.toString()
        this.email = email.toString()
        this.userId = userId.toString()
        this.img = img.toString()
    }

    fun firstName(): String {
        return firstName
    }

    fun firstName(firstName: String) {
        this.firstName = firstName
    }

    fun img(): String {
        return img
    }

    fun img(img: String) {
        this.img = img
    }

    fun email(): String {
        return email
    }

    fun email(email: String) {
        this.email = email
    }

    fun userId(): String {
        return userId
    }

    fun userId(userId: String) {
        this.userId = userId
    }
}
