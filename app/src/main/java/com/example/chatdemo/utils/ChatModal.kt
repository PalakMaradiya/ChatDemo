package com.example.chatdemo.utils

class ChatModal {


    var senderName: String = ""
    var message: String = ""
    var senderId: String? = null
    var currentTime: String? = null
    var imageUrl: String =""
    var videoUrl: String = ""
    var videoThumbnailUrl : String =""

    constructor(
        senderId: String,
        senderName: String,
        message: String,
        imageUrl: String,
    ) {

        this.senderId = senderId
        this.senderName = senderName
        this.message = message
        this.imageUrl = imageUrl

    }

    constructor(){

    }




}