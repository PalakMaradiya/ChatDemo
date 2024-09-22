package com.example.chatdemo.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatdemo.R
import com.example.chatdemo.utils.GroupModal
import com.google.firebase.database.FirebaseDatabase

class GroupListAdapter(var context: Context , var groupUserlist : ArrayList<GroupModal> ,
                       var onItemClick: ((name: String, img: String ,grpId : String) -> Unit))
    : RecyclerView.Adapter<GroupListAdapter.MyViewHolder>() {
    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var grpImage : ImageView = itemView.findViewById(R.id.grpImage)
        var txtgrpName : TextView = itemView.findViewById(R.id.txtgrpName)
        var linearLayout : LinearLayout = itemView.findViewById(R.id.linearLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.group_item_file, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  groupUserlist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.txtgrpName.text = groupUserlist[position].groupName
        Glide.with(context).load(groupUserlist[position].grpimg).into(holder.grpImage)
//        Log.e("TAG", "Image: "+groupUserlist[position].imgUrl )

        holder.linearLayout.setOnClickListener {

            onItemClick.invoke(
                groupUserlist[position].groupName,
                groupUserlist[position].grpimg,
                groupUserlist[position].grpID

            )


            Log.e("==>ID", "ID: "+ groupUserlist[position].grpID)
            Log.e("==>name", "name: "+ groupUserlist[position].groupName)
            Log.e("==>img", "img: "+ groupUserlist[position].grpimg)
        }


    }
}