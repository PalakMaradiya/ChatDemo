package com.example.chatdemo.utils

import java.io.Serializable

class GroupMessageModal : Serializable{

    var senderName: String = ""
    var message: String = ""
    var senderId: String = ""
    var currentTime: String = ""
    var currentDate: String = ""
    var imageUrl: String = ""
    var videoUrl: String = ""
    var videoThumbnailUrl: String = ""






    constructor(
        senderId: String,
        senderName: String,
        message: String,
        imageUrl: String,
        videoUrl: String,
        currentTime: String,
        currentDate: String,
        videoThumbnailUrl: String) {


        this.senderId = senderId
        this.senderName = senderName
        this.message = message
        this.imageUrl = imageUrl
        this.currentTime = currentTime
        this.currentDate = currentDate
        this.videoUrl = videoUrl
        this.videoThumbnailUrl = videoThumbnailUrl

    }

    constructor(){}

}