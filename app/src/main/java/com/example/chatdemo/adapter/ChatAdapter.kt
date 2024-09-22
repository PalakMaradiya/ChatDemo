package com.example.chatdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatdemo.R
import com.example.chatdemo.utils.ChatModal
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(
    val context: Context, val chatList: ArrayList<ChatModal>, var onVideoPlay: ((videoUrl: String, thumbnail: String) -> Unit)
    , var onItemclick: ((message: String) -> Unit)) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var txtRightChat: TextView = itemView.findViewById(R.id.txtRightChat)
        var txtLeftChat: TextView = itemView.findViewById(R.id.txtLeftChat)
        var txtRightTime: TextView = itemView.findViewById(R.id.txtRightTime)
        var txtLeftTime: TextView = itemView.findViewById(R.id.txtLeftTime)
        var txtimgRightTime: TextView = itemView.findViewById(R.id.txtimgRightTime)
        var txtImageLeftTime: TextView = itemView.findViewById(R.id.txtImageLeftTime)
        var imgRight: ImageView = itemView.findViewById(R.id.imgRight)
        var imgLeft: ImageView = itemView.findViewById(R.id.imgLeft)
        var thumbnailRight: ImageView = itemView.findViewById(R.id.thumbnailRight)
        var thumbnailLeft: ImageView = itemView.findViewById(R.id.thumbnailLeft)
        var videoViewRight: VideoView = itemView.findViewById(R.id.videoViewRight)
        var videoViewLeft: VideoView = itemView.findViewById(R.id.videoViewLeft)
        var RightChatLayout: LinearLayout = itemView.findViewById(R.id.RightChatLayout)
        var LeftChatLayout: LinearLayout = itemView.findViewById(R.id.LeftChatLayout)
        var LeftRelativeLayout: RelativeLayout = itemView.findViewById(R.id.LeftRelativeLayout)
        var RightRelativeLayout: RelativeLayout = itemView.findViewById(R.id.RightRelativeLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.send_chat_item_file, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatItem = chatList[position]

        // Reset visibilities to default
        holder.LeftChatLayout.visibility = View.GONE
        holder.RightChatLayout.visibility = View.GONE
        holder.imgLeft.visibility = View.GONE
        holder.imgRight.visibility = View.GONE
        holder.thumbnailLeft.visibility = View.GONE
        holder.thumbnailRight.visibility = View.GONE
        holder.videoViewLeft.visibility = View.GONE
        holder.videoViewRight.visibility = View.GONE
        holder.LeftRelativeLayout.visibility = View.GONE
        holder.RightRelativeLayout.visibility = View.GONE

        if (chatItem.senderId == FirebaseAuth.getInstance().currentUser!!.uid) {
            // Handle right side (sent) messages
            holder.RightChatLayout.visibility = View.VISIBLE
            holder.txtRightChat.text = chatItem.message
            holder.txtRightTime.text = chatItem.currentTime

            holder.txtRightChat.setOnClickListener {
                onItemclick.invoke(chatItem.message)
            }

            if (!chatItem.imageUrl.isNullOrEmpty()) {
                holder.RightRelativeLayout.visibility = View.VISIBLE
                Glide.with(context).load(chatItem.imageUrl).into(holder.imgRight)
                holder.txtimgRightTime.text = chatItem.currentTime
                holder.imgRight.visibility = View.VISIBLE
                holder.txtRightChat.visibility = View.GONE
                holder.txtRightTime.visibility = View.GONE
            }
            if (!chatItem.videoUrl.isNullOrEmpty()) {
                holder.txtRightChat.visibility = View.GONE
                holder.txtRightTime.visibility = View.GONE
                holder.txtimgRightTime.text = chatItem.currentTime
                holder.RightRelativeLayout.visibility = View.VISIBLE
                Glide.with(context).load(chatItem.videoThumbnailUrl).into(holder.thumbnailRight)
                holder.thumbnailRight.visibility = View.VISIBLE
                holder.thumbnailRight.setOnClickListener {
                    onVideoPlay.invoke(chatItem.videoUrl, chatItem.videoThumbnailUrl)
                }
            }
        }

        else {
            // Handle left side (received) messages
            holder.LeftChatLayout.visibility = View.VISIBLE
            holder.txtLeftChat.text = chatItem.message
            holder.txtLeftTime.text = chatItem.currentTime

            if (!chatItem.imageUrl.isNullOrEmpty()) {
                holder.txtLeftChat.visibility = View.GONE
                holder.txtLeftTime.visibility = View.GONE
                holder.LeftRelativeLayout.visibility = View.VISIBLE
                Glide.with(context).load(chatItem.imageUrl).into(holder.imgLeft)
                holder.txtImageLeftTime.text = chatItem.currentTime
                holder.imgLeft.visibility = View.VISIBLE
            }

            if (!chatItem.videoUrl.isNullOrEmpty()) {
                holder.txtLeftChat.visibility = View.GONE
                holder.txtLeftTime.visibility = View.GONE
                holder.LeftRelativeLayout.visibility = View.VISIBLE
                Glide.with(context).load(chatItem.videoThumbnailUrl).into(holder.thumbnailLeft)
                holder.thumbnailLeft.visibility = View.VISIBLE
                holder.txtImageLeftTime.text = chatItem.currentTime
                holder.thumbnailLeft.setOnClickListener {
                    onVideoPlay.invoke(chatItem.videoUrl, chatItem.videoThumbnailUrl)
                }
            }
        }

        // Make sure text is always visible if no image or video is present
        if (chatItem.imageUrl.isNullOrEmpty() && chatItem.videoUrl.isNullOrEmpty()) {
            if (chatItem.senderId == FirebaseAuth.getInstance().currentUser!!.uid) {
                holder.RightChatLayout.visibility = View.VISIBLE
            } else {
                holder.LeftChatLayout.visibility = View.VISIBLE
            }
        }
    }



}
